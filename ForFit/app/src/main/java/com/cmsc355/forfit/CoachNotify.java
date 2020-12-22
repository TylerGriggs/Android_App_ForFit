package com.cmsc355.forfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoachNotify extends AppCompatActivity {

    private Button submitNotficatonButton;
    private Button findAtheleteButton;
    private Button createChallengeButton;

    private EditText notificationTextBox;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference challengesRef;
    private DatabaseReference athletesRef;

    ArrayList<String> athleteListUID;
    ArrayList<String> tempAnnouncementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_notify);

        //Variables
        athleteListUID = new ArrayList<>();
        tempAnnouncementList = new ArrayList<>();

        //UI objects
        submitNotficatonButton = findViewById(R.id.CN_SubmitButton);
        findAtheleteButton = findViewById(R.id.CN_FindAthleteButton);
        createChallengeButton = findViewById(R.id.CN_CreateChallengeButton);
        notificationTextBox = findViewById(R.id.CN_NotifyEditTextBox);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentFBUser = mAuth.getCurrentUser();
        mData = FirebaseDatabase.getInstance();

        //Database
        challengesRef = mData.getReference("Challenges");
        athletesRef = mData.getReference("Athlete Users");

    }

    /** The CNbuttonSubmitNotification() method authenticates the current user, retrieves what
     *      the user has typed into the text box, and updates all the Athlete's announcements that
     *      are subscribed to that Coach's challenges.
     *  The method returns the Coach user to CoachMain activity to automatically discourage
     *      multiple notifications being sent too quickly.
     * */

    public void CNbuttonSubmitNotification(View view) {
        //Get Authentication to determine the current Coach User
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        //Get the information typed by the user in the TextBox
        final String notifyText = notificationTextBox.getText().toString();

        /**
         * This Listener will load an ArrayList of Athletes that are subscribed to the current
         * Coach User, and send them the notification the current Coach User typed.
         * */
        challengesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                athleteListUID.clear();
                ArrayList<String> tempAthletes;

                //For Each challenge
                for (DataSnapshot challengeSnapshot : dataSnapshot.getChildren()) {

                    //If the current Coach User is the person who made the challenge

                    // If the challenge was created by the current Coach User
                    if(challengeSnapshot.child("coachid").getValue().toString().equals(uid)){

                        //Save the athletes that are subscribed
                        tempAthletes = (ArrayList<String>) challengeSnapshot.child("subscribedAthletes").getValue();

                        //As long as there is at least one athlete, store the athletes globally
                        if(tempAthletes != null) {

                            for(int count = 0; count < tempAthletes.size(); count++){
                                if(!athleteListUID.contains(tempAthletes.get(count))){
                                    athleteListUID.add(tempAthletes.get(count));
                                }
                            }

                            //For each confirmed subscribed athlete
                            for (int i = 0; i < athleteListUID.size(); i++){

                                //Load the athlete's announcements, as to not erase any when updating
                                collectAnnouncements(athleteListUID.get(i));

                                //Create the Personalized Announcement for the athlete
                                tempAnnouncementList.add(notifyText);

                                //Update the athlete's announcements in the database
                                athletesRef.child(athleteListUID.get(i)).child("announcements").setValue(tempAnnouncementList);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Change the Activity back to HOME, to discourage too many notifications at once
        Intent intent = new Intent(this, CoachMain.class);
        startActivity(intent);

    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * This Method is called in the CNbuttonSubmitNotification method. (When a notification is submitted)
     *  The method retrieves the current announcements of a friend with the parameter String UID
     * @param userUID
     * /////////////////////////////////////////////////////////////////////////////////////*/

    public void collectAnnouncements (String userUID) {
        tempAnnouncementList.clear();

        //Get Database Reference of the Athlete User
        DatabaseReference userAnnouncementRef = athletesRef.child(userUID).child("announcements");
        //Send User a notification to their announcements
        userAnnouncementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String announce;

                //For Each announcement in the snapshot
                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {

                    //Save the announcement
                    announce = announcementSnapshot.getValue(String.class);
                    tempAnnouncementList.add(announce);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    } // END collectAnnouncements()

    /** UI Button method to open the CoachMain activity*/
    public void CNbuttonHome(View view) {
        Intent intent = new Intent(this, CoachMain.class);
        startActivity(intent);

    }

    /** UI Button method to open the AthleteSearch activity*/
    public void CNbuttonAthleteSearch(View view){
        Intent intent = new Intent (this, AthleteSearch.class);
        startActivity(intent);
    }

    /** UI Button method to open the ChallengeCreate activity*/
    public void CNbuttonCreateChallenge(View view) {
        Intent intent = new Intent(this, ChallengeCreate.class);
        startActivity(intent);

    }
}
