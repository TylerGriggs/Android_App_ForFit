package com.cmsc355.forfit;

import android.content.Intent;
import android.service.autofill.UserData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.cmsc355.forfit.custObjects.AthleteUser;
import com.cmsc355.forfit.custObjects.Challenge;
import com.cmsc355.forfit.custObjects.PersonalInfo;
import com.cmsc355.forfit.custObjects.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChallengeLeaderboard extends AppCompatActivity {

    private ListView leaderboard;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference ChallengeData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_leaderboard);

        String challengeid = getIntent().getStringExtra("ChallengeID");
        leaderboard = findViewById(R.id.L_leaderboard);
        ChallengeData = FirebaseDatabase.getInstance().getReference("Challenges").child(challengeid);

        final Challenge[] curchallenge = {new Challenge()};

        //////////////////////////////////////////////////////////////////////////////////
        // Database listener to set the current object.
        /////////////////////////////////////////////////////////////////////////////////
        ChallengeData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Challenge CurrentChallenge = dataSnapshot.getValue(Challenge.class);
                curchallenge[0] = CurrentChallenge;

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //////////////////////////////////////////////////////////////////////////////////

        ArrayList<Team> leaderboard = curchallenge[0].getLeaderboard();
        ArrayList<Team> regTeams = curchallenge[0].getRegisterdTeams();


        Collections.sort(regTeams, new Sortbyroll());













    }

    public void buttonMyProfile (View view){
        Intent intent = new Intent (this, Profile.class);
        startActivity(intent);

    }

    public void buttonHome (View view){
        Intent intent = new Intent (this, AthleteMain.class);
        startActivity(intent);

    }

    public void buttonChallengeSearch (View view){
        Intent intent = new Intent (this, ChallengeSearch.class);
        startActivity(intent);

    }




}

class Sortbyroll implements Comparator<Team>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Team a, Team b)
    {
        return a.getCurrentpoints() - b.getCurrentpoints();
    }
}