package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExerciseView extends AppCompatActivity {

    private Button bBack;
    private Button bEnter;
    private TextView exerciseName;
    private TextView amountLeft;
    private EditText input;

    DatabaseReference databaseReferenceAthlete;
    DatabaseReference databaseReferenceChallenge;

    private String cName;
    private int currDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);

        bBack = findViewById(R.id.ev_buttonBack);
        bEnter = findViewById(R.id.ev_enterButton);
        exerciseName = findViewById(R.id.ev_ExerciseName);
        amountLeft = findViewById(R.id.ev_exerciseAmount);
        input = findViewById(R.id.ev_input);

        Intent intent = getIntent();
        exerciseName.setText(intent.getStringExtra("exercise"));
        cName = intent.getStringExtra("cName");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        databaseReferenceAthlete = FirebaseDatabase.getInstance().getReference("Athlete Users");
        databaseReferenceChallenge = FirebaseDatabase.getInstance().getReference();


        bEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(input.getText().toString().trim().length() == 0)){
                    int in = 0;
                    try {
                        in = Integer.parseInt(input.getText().toString());
                    }
                    catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    databaseReferenceAthlete.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try{
                                DataSnapshot snapshot = dataSnapshot.child(uid).child("currChallenges").child(cName).child(exerciseName.getText().toString());
                                currDone = (int)((long)snapshot.getValue());
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    currDone += in;
                    databaseReferenceAthlete.child(uid).child("currChallenges").child(cName).child(exerciseName.getText().toString()).setValue(currDone);
                    Toast.makeText(ExerciseView.this, "Input Received.", Toast.LENGTH_SHORT).show();

                    Intent intent2 = new Intent(ExerciseView.this, ChallengeView.class);
                    intent2.putExtra("name", cName);
                    startActivity(intent2);

                }
                else{
                    Toast.makeText(ExerciseView.this, "Nothing in there.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amountDone = 0;
                try {
                    amountDone = Integer.parseInt(dataSnapshot.child("Athlete Users").child(uid).child("currChallenges").child(cName).child(exerciseName.getText().toString()).getValue().toString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                currDone = amountDone;

                ArrayList<String> data = (ArrayList<String>) dataSnapshot.child("Challenges").child(cName).child("exercises").getValue();
                for(String s : data){
                    String s1 = s.substring(0, exerciseName.getText().toString().length());
                    if(s1.equals(exerciseName.getText().toString())){
                        int a = getAmount(s);
                        amountLeft.setText(Math.max(0, a - amountDone) + "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private int getAmount(String in){
        String s1 = in;

        //Possible error if the name contains "Amount"
        final String name = s1.substring(0, s1.indexOf("Amount")-1); //first section of the string to give the name.

        String s2 = s1.substring(s1.indexOf(": ")); //cuts of the front of the string and puts to a new substring.

        String a = s2.substring(s2.indexOf(' '), s2.indexOf("\n")); //cuts the part after the : but before the \n
        a = a.trim(); //trims the inevitable white space
        final String units = a.substring(a.indexOf(' ')); //gets the units off the string
        a = a.substring(0, a.indexOf(' ')); //cuts off the units
        final int amount = Integer.parseInt(a); //parse to use as a number

        String s3 = s2.substring(s2.indexOf("\n")); //cuts to the date and new string
        s3 = s3.substring(8); //removes the date tag

        String y = s3.substring(0, s3.indexOf('/')); //cuts year into new string
        final int year = Integer.parseInt(y); //parse year to use as a number

        s3 = s3.substring(s3.indexOf('/') + 1); //cuts year off

        String d = s3.substring(0, s3.indexOf('/')); //cuts day into new string
        final int day = Integer.parseInt(d); //parse day

        s3 = s3.substring(s3.indexOf('/') + 1); //cuts day off
        final int month = Integer.parseInt(s3); //parses month

        input.setHint("Enter in terms of" + units);
        return amount;
    }

    /** UI Button method to return to the ChallengeView activity*/
    public void Back(View view){
        Intent intent = new Intent (this, ChallengeView.class);
        intent.putExtra("name", cName);
        startActivity(intent);
        finish();
    }

    /** UI Button method to open the CoachMain activity*/
    public void EVbuttonHome(View view) {
        Intent intent = new Intent(this, AthleteMain.class);
        startActivity(intent);

    }
}
