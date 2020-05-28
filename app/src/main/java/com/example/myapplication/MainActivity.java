package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.genetic.Chromosome;
import com.example.myapplication.genetic.DemoGA;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onButtonClick(View v){
        EditText equals = findViewById(R.id.equals);
        EditText A = findViewById(R.id.editA);
        EditText B = findViewById(R.id.editB);
        EditText C = findViewById(R.id.editC);
        EditText D = findViewById(R.id.editD);


        TextView resultText = findViewById(R.id.resultText);
        TextView timeText = findViewById(R.id.timeText);

        if (A.getText().toString().trim().equals("")
                || B.getText().toString().trim().equals("")
                || C.getText().toString().trim().equals("")
                || D.getText().toString().trim().equals("")
                || equals.getText().toString().trim().equals("")){
            resultText.setText("Введіть вірні дані!");
            return;
        }


        int Y = Integer.parseInt(equals.getText().toString());
        int a = Integer.parseInt(A.getText().toString());
        int b = Integer.parseInt(B.getText().toString());
        int c = Integer.parseInt(C.getText().toString());
        int d = Integer.parseInt(D.getText().toString());

        long time = System.currentTimeMillis();

        int[] arr = new int[]{a,b,c,d};
        DemoGA demoGA = new DemoGA(arr,Y);
        demoGA.printEquation();
        Chromosome chr = demoGA.findSolution();
        System.out.println(chr);

        StringBuilder sb_text = new StringBuilder();
        sb_text.append("Результат: ").append(chr)
                .append("\nкількість генерацій: ").append(demoGA.getGenerations());
        resultText.setText(sb_text);

        timeText.setText("Час виконання: " + (System.currentTimeMillis() - time) + "мс");
    }
}
