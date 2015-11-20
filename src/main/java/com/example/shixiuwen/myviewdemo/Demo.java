package com.example.shixiuwen.myviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Demo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        MyView myview = (MyView) findViewById(R.id.myview);
        myview.rotate(true,myview,4000);
    }
}
