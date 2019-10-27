package com.example.workoutforgood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

public class StartSearching extends AppCompatActivity {

    ProgressBar progressBar;
    Button btnUpload;
    EditText txtZipCode;
    public Button btnResults;

    DatabaseReference oppCountRef;
    DatabaseReference currentCountRef;
    DatabaseReference zipCodeRef;
    DatabaseReference update;
    DatabaseReference checkBool;

    DataSnapshot dataSnapshot;

    String curState;
    boolean isUpdated;

    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_searching);

        btnUpload = findViewById(R.id.btnUpload);
        txtZipCode = findViewById(R.id.txtZipCode);
        progressBar = findViewById(R.id.progressBar);

        btnResults = findViewById(R.id.btnResults);

        progressBar.setVisibility(View.GONE);
        btnResults.setVisibility(View.VISIBLE);





        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtZipCode.length() != 5)
                {
                    Toast.makeText(StartSearching.this, "Please enter a zip code", Toast.LENGTH_LONG).show();
                }
                else
                {
                    zipCodeRef = FirebaseDatabase.getInstance().getReference("User").child("ZipCode");
                    update = FirebaseDatabase.getInstance().getReference("Update").child("bool");



                    zipCodeRef.setValue(txtZipCode.getText().toString());
                    update.setValue("True");






                    /*
                    Toast.makeText(StartSearching.this, "Value set", Toast.LENGTH_LONG).show();

                    checkBool = FirebaseDatabase.getInstance().getReference("Update");
                    isUpdated = true;
                    while(parseBoolean(dataSnapshot.child("bool").getValue().toString()))
                    {
                        SystemClock.sleep(2000);
                        Toast.makeText(StartSearching.this, "IN WHILE LOOP", Toast.LENGTH_LONG).show();
                    }


                    progressBar.setVisibility(View.INVISIBLE);
                    btnResults.setVisibility(View.VISIBLE);
                    */

                }


            }
        });

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        //prog();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //btnResults.setVisibility(View.INVISIBLE);

    }

    /*
    public void prog()
    {

        currentCountRef = FirebaseDatabase.getInstance().getReference("CurCnt");
        totalCount = 0;
        //counter = 0;

        oppCountRef = FirebaseDatabase.getInstance().getReference("Vol_Opp");
        oppCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                totalCount = parseInt(dataSnapshot.child("Opp_Cnt").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        while(counter < totalCount)
        {
            currentCountRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    counter = parseInt(dataSnapshot.child("oppCur").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        btnResults.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }*/

    public void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
