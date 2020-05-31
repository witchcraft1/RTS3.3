package com.example.myapplication.genetic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DemoGA {
    private static final Random RANDOM = new Random();
    //діафантове рівння типу ax1+bx2+cx3+dx4 = y
    private int[] coeffs;
    private int Y;
    private Chromosome[] population;
    private int sizeOfPopulation = 4;
    private int genesCount;
    private int mutationCount = 1;
    private int[] fitness;
    private double[] survival;
    private Chromosome winner;
    private int generations = 1;

    public DemoGA(int[] coeffs,int Y, int mutationCount){
        this(coeffs,Y);
        this.mutationCount = mutationCount;
    }

    public DemoGA(int ...coeffsAndY){
       this(Arrays.copyOfRange(coeffsAndY, 0, coeffsAndY.length-1), coeffsAndY[coeffsAndY.length-1]);
    }
    public DemoGA(int[] coeffs, int Y){
        this.coeffs = coeffs;
        this.Y = Y;
        this.genesCount = coeffs.length;
        /**
         * coeffs.length - кількість коефіцієнтів рівняння(a,b,c,d), такою ж
         * буде і к-сть невідомих змінних(x1-x4), а отже це к-сть генів кожної
         * хтомосоми
         */
        this.population = new Chromosome[sizeOfPopulation];
        for (int i = 0; i < sizeOfPopulation; i++) {
            population[i] = new Chromosome(genesCount, Y);
        }
        fitness = new int[sizeOfPopulation];
        survival = new double[sizeOfPopulation];
//        this.population = new Population(sizeOfPopulation, genesCount, Y);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void showGeneticPool(Chromosome[] population) {
        System.out.println("==Genetic Pool==");
        int increment=0;
        for (Chromosome individual:population) {
            System.out.println("> Individual  "+ increment +" | " + individual + " | " + fitness[increment]);
            increment++;
        }
        int indexBest = getBest(fitness);
        System.out.println("The best candidate: " + indexBest + ", Fintness: " + fitness[indexBest]);
        System.out.println("================");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Chromosome findSolution(){
        while(true){
            fitness = calcAllFitness(population);
            showGeneticPool(population);
            int winnerIndex = checkFitness();
            if(winnerIndex != -1){
                System.out.println("amount of generations: " + generations);
                return population[winnerIndex];
            }

            survival = calculateSurvival(fitness);
            population = generateNewPopul();
            mutation(population);

            generations++;
        }
    }

    public void mutation(Chromosome[] population){
//        int[] fits = calcAllFitness(population);
//        Chromosome worst = population[getWorst(fits)];
        int index = RANDOM.nextInt(genesCount);
//        worst.setGene(index, RANDOM.nextInt(Y));
        for (int i = 0; i < mutationCount; i++) {
            population[RANDOM.nextInt(sizeOfPopulation)].setGene(index,RANDOM.nextInt(Y));
            index = RANDOM.nextInt(genesCount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private  int getBest(int[] fits){
       /* List<Integer> fitsList = Arrays.stream(fits).boxed().collect(Collectors.toList());
        return fitsList.indexOf(Collections.min(fitsList));*/
       int curMin = fits[0];
       int index = 0;
        for (int i = 1; i < fits.length; i++) {
            if(fits[i] < curMin) {
                curMin = fits[i];
                index = i;
            }
        }
        return index;
    }

    public Chromosome[] generateNewPopul(){
        Chromosome[] newPopul = new Chromosome[sizeOfPopulation];

        for (int i = 0; i < sizeOfPopulation; i++) {
            newPopul[i] = crossover(rouletteRoot(population), rouletteRoot(population));
        }

       /* Map<Chromosome,Chromosome> chromosomes = new HashMap<>();
        for (int i = 0; i < sizeOfPopulation; i++) {
            chromosomes.put(rouletteRoot(population),rouletteRoot(population));
        }

//        Set<Map.Entry<Chromosome, Chromosome>> chromosomePairs = chromosomes.entrySet();
        int index = 0;

        for (Map.Entry<Chromosome, Chromosome> chrEntry : chromosomes.entrySet()) {
            newPopul[index] = crossover(chrEntry.getKey(), chrEntry.getValue());
            index++;
        }*/

        return newPopul;
    }

    public Chromosome crossover(Chromosome chr1, Chromosome chr2){
        int separator = RANDOM.nextInt(3) + 1; // місце розділення хромосом батьків (після 1,2 або 3 гена)

        int[] genes = new int[genesCount];

        //TODO

        for(int index = 0; index < genesCount; index++){
            if(index < separator)  genes[index] = chr1.getGene(index);
            else genes[index] = chr2.getGene(index);
        }

        return new Chromosome(genes);
    }

    public Chromosome rouletteRoot(Chromosome[] population){
        double rand_d = RANDOM.nextDouble();
        double[] surv = calcSurvForSelection(survival);
        int index = 0;
        for (int i = 0; i < surv.length; i++) {
            if(rand_d < surv[i]) index = i;
        }
        return population[index];
    }

    public double[] calcSurvForSelection(double[] survivals){
        double[] selectSurv = new double[survivals.length];
        double surv_sum = 0;
        for (int i = 0; i < selectSurv.length; i++) {
            selectSurv[i] = survivals[i] + surv_sum;
            surv_sum = selectSurv[i];
        }
        return selectSurv;
    }
    public double[] calculateSurvival(int[] fit){
        double[] survival = new double[sizeOfPopulation];
        double sum = 0;
        for (int i = 0; i < sizeOfPopulation; i++) {
            survival[i] = 1./fit[i];
            sum += survival[i];
        }
        for (int i = 0; i < sizeOfPopulation; i++) {
            survival[i] /= sum;
        }
        return survival;
    }


    public int[] calcAllFitness(Chromosome[] population){
        int[] fits = new int[sizeOfPopulation];
        for (int i = 0; i < sizeOfPopulation; i++) {
            fits[i] = calculateFitness(population[i]);
        }
        return fits;
    }

    private int checkFitness(){
        for (int i = 0; i < sizeOfPopulation; i++) {
            if(fitness[i] == 0) return i;
        }
        return -1;
    }

    private int calculateFitness(Chromosome chr){
        int value = 0;
        for (int i = 0; i < sizeOfPopulation; i++) {
            value += coeffs[i]*chr.getGene(i);
        }
        return Math.abs(value - Y);
    }

    public void printEquation(){
        StringBuilder equation = new StringBuilder();
        for (int i = 0; i < genesCount; i++) {
            equation.append(coeffs[i]).append("x").append(i + 1);
            if(i < genesCount - 1) equation.append(" + ");
        }
        System.out.println(equation + " = " + Y);
    }

    public Chromosome[] getPopulation() {
        return population;
    }

    public void setPopulation(Chromosome[] population) {
        this.population = population;
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void setSizeOfPopulation(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
    }

    public int[] getFitness() {
        return fitness;
    }

    public void setFitness(int[] fitness) {
        this.fitness = fitness;
    }

    public double[] getSurvival() {
        return survival;
    }

    public void setSurvival(double[] survival) {
        this.survival = survival;
    }

    public int getGenerations(){
        return generations;
    }
}