package com.example.myapplication.genetic;

import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    private int[] genes;

    Chromosome(int length, int max){
        genes = new int[length];

        Random rand = new Random();

        for (int i = 0; i < genes.length; i++) {
            genes[i] = rand.nextInt(max);
        }
    }

    Chromosome(int ...genes){
        this.genes = genes;
    }

    public int getGene(int pos){
        if(pos < 0 || pos >= genes.length) throw new ArrayIndexOutOfBoundsException();
        return genes[pos];
    }
    public int[] getGenes(int from, int to){
        return Arrays.copyOfRange(genes, from,to);
    }

    public void setGene(int pos, int gene){
        genes[pos] = gene;
    }

    @Override
    public String toString(){
        return "[genes=" + Arrays.toString(genes) + "]";
    }
}
