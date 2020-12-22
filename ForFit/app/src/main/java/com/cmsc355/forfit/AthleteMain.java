package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AthleteMain extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> announcementList;
    ArrayList<String> challengeList;
    ArrayAdapter<String> challengeAdapter;
    ArrayAdapter<String> announcementAdapter;
    DatabaseReference databaseReference;
    ListView subscribedChallenges;
    ListView announcementsListView;
    TextView title;

    private FirebaseAuth Auth;

    public static DataSnapshot cCESnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        title = findViewById(R.id.AM_TitleUsername);

        // Challenges Variables
        challengeList = new ArrayList<>();
        challengeAdapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, challengeList);
        subscribedChallenges = findViewById(R.id.SubscribedChallenges);
        subscribedChallenges.setAdapter(challengeAdapter);

        //Announcements Variables
        announcementList = new ArrayList<>();
        announcementAdapter = new ArrayAdapter<String>(this, R.layout.list_dark_format_font14, announcementList);
        announcementsListView = findViewById(R.id.AM_Announcements);
        announcementsListView.setAdapter(announcementAdapter);

        //Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Athlete Users");

        //ListView Listener for choosing friends
        subscribedChallenges.setOnItemClickListener(this);



        /**
         * This listener and function set the UI elements for Announcements, Challenges and Username
         *      for the Athlete User's home page during creation of the activity.
         */
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Clear the ArrayLists for displaying challenges and announcements
                challengeList.clear();
                announcementList.clear();

                //Set the Title on the actiivity to the current user's username
                title.setText(dataSnapshot.child(uid).child("name").getValue().toString());

                //Grab a snapshot from the database for the challenges and announcements
                DataSnapshot challengeSnapshot = dataSnapshot.child(uid).child("currChallenges");
                DataSnapshot announcementSnapshot = dataSnapshot.child(uid).child("announcements");

                //Load the snapshots into temporary variables to avoid sending NULL to the database later
                HashMap<String, String> currChallengesT = (HashMap<String, String>) challengeSnapshot.getValue();
                ArrayList<String> announcementsT = (ArrayList<String>) announcementSnapshot.getValue();

                //Check both lists IF NULL
                if(currChallengesT != null) {
                    challengeList.addAll(currChallengesT.keySet());
                }

                if(announcementsT != null) {
                    for (String announcement : announcementsT)
                    announcementList.add(announcement);
                }

                //Update the UI list adapters
                announcementAdapter.notifyDataSetChanged();
                challengeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Handler for the listview action.
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> l, View v, final int position, long id) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cCESnapshot = dataSnapshot.child(uid).child("currChallenges").child(challengeList.get(position));
                Intent intent = new Intent();
                intent.setClass(AthleteMain.this, ChallengeView.class);
                intent.putExtra("snapshot", cCESnapshot.toString());
                intent.putExtra("name", challengeList.get(position));
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * Button activity for transfer to the ViewProfile activity.
     * @param view
     */
    public void ViewProfile(View view){
        Auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = Auth.getCurrentUser();
        String user = currentUser.getUid().toString();
        Intent intent = new Intent (this, Profile.class);
        intent.putExtra("user",user);
        startActivity(intent);

    }

    /**
     * Button activity for transfer to the EditProfile activity.
     * @param view
     */
    public void EditProfile(View view){
        Intent intent = new Intent (this, ProfileEdit.class);
        startActivity(intent);

    }

    /**
     * Button activity for transfer to the FriendList activity.
     * @param view
     */
    public void FriendList(View view){

        Intent intent = new Intent (this, FriendList.class);
        startActivity(intent);

    }

    /**
     * Button activity for transfer to the ChallengeSearch activity.
     * @param view
     */
    public void ChallengeSearch(View view){
        Intent intent = new Intent (this, ChallengeSearch.class);
        startActivity(intent);

    }
}
