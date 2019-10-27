package com.example.workoutforgood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "message";

    EditText txtZipCode;

    TextView lblCount;
    TextView lblOpportunity;
    TextView lblOrganization;
    TextView lblRating;
    TextView lblOppCount;

    Button btnUpload;
    Button btnPrevious;
    Button btnNext;
    Button btnURL;

    DatabaseReference zipCodeRef;
    DatabaseReference update;
    DatabaseReference oppOpportunityRef;
    DatabaseReference oppCountRef;
    DatabaseReference oppInfoRef;
    DatabaseReference oppOrganizationRef;
    DatabaseReference oppRatingRef;
    DatabaseReference oppURLRef;

    int index;
    String strIndex;
    int count;
    String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpload = findViewById(R.id.btnUpload);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnURL = findViewById(R.id.btnURL);

        txtZipCode = findViewById(R.id.txtZipCode);

        lblCount = findViewById(R.id.lblCount);
        lblOpportunity = findViewById(R.id.lblOpportunity);
        lblOrganization = findViewById(R.id.lblOrganization);
        lblRating = findViewById(R.id.lblRating);
        lblOppCount = findViewById(R.id.lblOppCount);

        index = 0;
        strIndex = String.valueOf(index);

        oppCountRef = FirebaseDatabase.getInstance().getReference("Vol_Opp");
        oppCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = parseInt(dataSnapshot.child("Opp_Cnt").getValue().toString());
                String s = "We have found " + String.valueOf(count) + " volunteering opportunities in your area!";
                SpannableString ss = new SpannableString(s);

                ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.RED);

                ss.setSpan(fcsOrange, 14, 14+ String.valueOf(count).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                lblCount.setText(ss);
                lblOppCount.setText(index + 1 + "/" + count);

                // lblCount.setText("We have found " + String.valueOf(count) + " volunteering opportunities in your area!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        oppOpportunityRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Names");
        oppOpportunityRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String oppName = dataSnapshot.child(strIndex).getValue().toString();

                lblOpportunity.setText(oppName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        oppOrganizationRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Orgs");
        oppOrganizationRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String organization = dataSnapshot.child(strIndex).getValue().toString();

                lblOrganization.setText("Organization: " + organization);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        oppRatingRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Ratings");
        oppRatingRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String rating = dataSnapshot.child(strIndex).getValue().toString();

                if (rating.equals("-1"))
                {
                    lblRating.setText("No rating");
                }
                else
                {
                    lblRating.setText("Rating: " + rating);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        oppURLRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Urls");
        oppURLRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                URL = dataSnapshot.child(strIndex).getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        btnURL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                oppOpportunityRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Names");
                oppInfoRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Info");
                oppOrganizationRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Orgs");
                oppRatingRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Ratings");
                oppURLRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Urls");

                if(index < (count - 1))
                {
                    index +=1;
                    lblOppCount.setText(index + 1 + "/" + count);
                }
                strIndex = String.valueOf(index);

                oppOpportunityRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String oppName = dataSnapshot.child(strIndex).getValue().toString();

                        lblOpportunity.setText(oppName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

                oppOrganizationRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String organization = dataSnapshot.child(strIndex).getValue().toString();

                        lblOrganization.setText("Organization: " + organization);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


                oppRatingRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String rating = dataSnapshot.child(strIndex).getValue().toString();

                        if (rating.equals("-1"))
                        {
                            lblRating.setText("No rating");
                        }
                        else
                        {
                            lblRating.setText("Rating: " + rating);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


                oppURLRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        URL = dataSnapshot.child(strIndex).getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                oppOpportunityRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Names");
                oppInfoRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Info");
                oppOrganizationRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Orgs");
                oppRatingRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Ratings");
                oppURLRef = FirebaseDatabase.getInstance().getReference("Vol_Opp").child("Opp_Urls");

                if(index > 0)
                {
                    index -=1;
                    lblOppCount.setText(index + 1 + "/" + count);
                }

                strIndex = String.valueOf(index);

                oppOpportunityRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String oppName = dataSnapshot.child(strIndex).getValue().toString();

                        lblOpportunity.setText(oppName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

                oppOrganizationRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String organization = dataSnapshot.child(strIndex).getValue().toString();

                        lblOrganization.setText("Organization: " + organization);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


                oppRatingRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String rating = dataSnapshot.child(strIndex).getValue().toString();

                        if (rating.equals("-1"))
                        {
                            lblRating.setText("No rating");
                        }
                        else
                        {
                            lblRating.setText("Rating: " + rating);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


                oppURLRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        URL = dataSnapshot.child(strIndex).getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }
        });

    }
}
