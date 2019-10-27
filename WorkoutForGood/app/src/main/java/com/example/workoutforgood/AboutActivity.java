package com.example.workoutforgood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView lblAbout;
    TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        lblAbout = findViewById(R.id.lblAbout);
        textView5 = findViewById(R.id.textView5);

        textView5.setMovementMethod(LinkMovementMethod.getInstance());



    }
}
