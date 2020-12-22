package com.cmsc355.forfit;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class AthleteSearch extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> searchList;
    ArrayList<String> searchListUID;
    ArrayList<String> filteredList;
    ArrayAdapter<String> adapter;

    Integer getAthleteIndex;

    private ListView athleteListView;

    private TextView title;
    private TextView username;
    private TextView bio;
    private ImageView profilePic;
    private Button buttonViewProfile;

    DatabaseReference athletesReference;
    DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_search);

        title = findViewById(R.id.AS_titleTextView);

        username = findViewById(R.id.AS_UsernameTextView);
        bio = findViewById(R.id.AS_textViewUserBio);
        profilePic = findViewById(R.id.AS_ProfilePicImageView);
        buttonViewProfile = findViewById(R.id.AS_ViewProfileButton);
        athleteListView = findViewById(R.id.AS_athletesListView);

        //ArrayList To store data from database, and a filtered list to change based on searches
        searchList = new ArrayList<>();
        searchListUID = new ArrayList<>();
        filteredList = new ArrayList<>();

        //Automatic adapter for ArrayList data into ListView, with preset options "android.R.layout..."
        adapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, searchList);

        //Set the Athletes Database Reference
        athletesReference = FirebaseDatabase.getInstance().getReference("Athlete Users");

        buttonViewProfile.setVisibility(View.INVISIBLE);

        //Set the adapter on that ListView
        athleteListView.setAdapter(adapter);
        athleteListView.setOnItemClickListener(this);
        adapter.setNotifyOnChange(true);

        //Retrieve and load filtered athletes, and activate SearchView
        loadSearchList();

    }

    /** ///////////////////////////////////////////////////////////////////////////////////////////
     * The onItemClick() method identifies which Username is selected from User input,
     * and sets the addFriendIndex variable based on the position of the user click.
     *      UI buttons based on the getAthleteIndex are revealed after first item click.
     * @param l
     * @param v
     * @param position
     * @param id
    ////////////////////////////////////////////////////////////////////////////////////////////*/

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        getAthleteIndex = position;
        username.setText(searchList.get(position));
        loadUserBio(searchListUID.get(position));

        //Set list-action button visible
        buttonViewProfile.setVisibility(View.VISIBLE);
    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadSearchList() Method is called to populate the ArrayLists searchList and searchListUID
     *      this method loads all athlete users so that a coach may browse
     * ///////////////////////////////////////////////////////////////////////////////////*/

    private void loadSearchList() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        athletesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchList.clear();
                searchListUID.clear();

                String uniqueID;
                String friendUsername;

                for (DataSnapshot athleteSnapshot : dataSnapshot.getChildren()) {
                    //Set the UID and Username for the Athlete
                    uniqueID = athleteSnapshot.child("id").getValue(String.class);
                    friendUsername = athleteSnapshot.child("name").getValue(String.class);

                    //Put Strings into Username and UID ArrayList \\
                    searchList.add(friendUsername);
                    searchListUID.add(uniqueID);

                }
                //Let the ListView adapter update the GUI
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadUserBIo() method is called in the onItemClick() method
     *
     *  The Method retrieves basic info about a user, from their UID, to populate a short
     *      description while browsing athletes.
     * @param uid
     * /////////////////////////////////////////////////////////////////////////////////////*/

    public void loadUserBio(String uid) {
        profileRef = athletesReference.child(uid).child("profile");

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Load the User Info from the database
                String tempBio = dataSnapshot.child("bio").getValue().toString();
                tempBio = tempBio.concat("\nAge: " + dataSnapshot.child("age").getValue().toString());
                tempBio = tempBio.concat("\nGender: " + dataSnapshot.child("gender").getValue().toString());
                tempBio = tempBio.concat("\nHeight: " + dataSnapshot.child("height").getValue().toString() + " ft.");
                tempBio = tempBio.concat("\nWeight: " + dataSnapshot.child("weight").getValue().toString() + "lbs.");

                bio.setText(tempBio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** UI Button method to open the ChallengeCreate activity*/
    public void buttonCoachCC (View view){
        Intent intent = new Intent (this, ChallengeCreate.class);
        startActivity(intent);

    }

    /** UI Button method to open the CoachMain activity*/
    public void buttonCoachHome (View view){
        Intent intent = new Intent (this, CoachMain.class);
        startActivity(intent);

    }

    /** UI Button method to view the profile of a selected user, based on the Integer getAthleteIndex */
    public void buttonViewProfile (View view){
        //Get the currently selected user's UID
        String user = searchListUID.get(getAthleteIndex);

        //Start the Profile activity and send it the UID String
        Intent intent = new Intent (this, Profile.class);
        intent.putExtra("user", user);
        startActivity(intent);

    }
}

