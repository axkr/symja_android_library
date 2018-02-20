package com.example.sample_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.matheclipse.core.eval.ExprEvaluator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(new ExprEvaluator().eval("Int(Sin(x)+Cos(x), x)"));
    }
}
