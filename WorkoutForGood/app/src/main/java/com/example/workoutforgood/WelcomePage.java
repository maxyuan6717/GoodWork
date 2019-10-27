package com.example.workoutforgood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class WelcomePage extends AppCompatActivity {

    Button btnStart;
    Button btnExit;
    Button btnAbout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_welcome_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnStart = findViewById(R.id.btnStart);
        btnAbout = findViewById(R.id.btnAbout);
        btnExit = findViewById(R.id.btnExit);

        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openStartSearchingActivity();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                System.exit(0);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openAboutActivity();
            }
        });


    }

    public void openStartSearchingActivity()
    {
        Intent intent = new Intent(this, StartSearching.class);
        startActivity(intent);
    }

    public void openAboutActivity()
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }



}
