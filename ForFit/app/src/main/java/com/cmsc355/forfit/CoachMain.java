package com.cmsc355.forfit;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CoachMain extends AppCompatActivity {

    ArrayList<String> challengesList;
    ListView myChallengesView;
    ArrayAdapter<String> adapter;

    DatabaseReference ChallengesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_main);

        challengesList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, challengesList);

        myChallengesView = findViewById(R.id.CD_MyChallengesListView);

        myChallengesView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ChallengesRef = database.getReference("Challenges");

        loadMyChallengesList();
    }

    private void loadMyChallengesList() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        ChallengesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                challengesList.clear();
                String challengeName;

                    //For each Challenge in the database
                    for (DataSnapshot challenge : dataSnapshot.getChildren()) {

                        //If the user's UID matches the Challenge's Coach ID
                        if(challenge.child("coachid").getValue().equals(uid)){

                            //Set and Store the Challenge locally
                            challengeName = challenge.getKey();
                            challengesList.add(challengeName);
                        }
                    }

                //Let the ListView adapter update the GUI
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void ViewProfile(View view){
        Intent intent = new Intent (this, Profile.class);
        startActivity(intent);

    }

    public void EditProfile(View view){
        Intent intent = new Intent (this, ProfileEdit.class);
        startActivity(intent);

    }

    public void CreateChallenge(View view){
        Intent intent = new Intent (this, ChallengeCreate.class);
        startActivity(intent);

    }

    public void buttonAthleteSearch(View view){
        Intent intent = new Intent (this, AthleteSearch.class);
        startActivity(intent);
    }

    public void buttonPushNotification(View view){
        Intent intent = new Intent (this, CoachNotify.class);
        startActivity(intent);
    }


}
