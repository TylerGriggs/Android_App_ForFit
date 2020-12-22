package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SearchView;

import com.cmsc355.forfit.custObjects.AthleteUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class FriendList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> friendsList;
    ArrayList<String> friendsListUID;
    ArrayAdapter<String> adapter;

    Integer friendIndex;


    private ListView friendsListView;

    private TextView bio;
    private TextView friendUsername;
    private ImageView profilePic;
    private Button removeFriendButton;
    private Button viewProfileButton;

    private ArrayList<AthleteUser> FriendsArrayList; //Maybe for holding the bios and info for friend profiles
    private FirebaseAuth mAuth;

    DatabaseReference athletesReference;
    DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        //ArrayList To store data from database
        friendsList = new ArrayList<>();
        friendsListUID = new ArrayList<>();

        //Automatic adapter for ArrayList data into ListView, with preset options "android.R.layout..."
        adapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, friendsList);

        friendUsername = findViewById(R.id.FL_textViewUsername);
        bio = findViewById(R.id.FL_textViewFriendBio);
        profilePic = findViewById(R.id.FL_imageViewFriendProfilePic);
        removeFriendButton = findViewById(R.id.FL_buttonRemoveFriend);
        viewProfileButton = findViewById(R.id.FL_buttonViewProfile);

        //A ListView to Populate from Database
        friendsListView = findViewById(R.id.FL_listViewMyFriends);
        friendsListView.setAdapter(adapter);
        friendsListView.setOnItemClickListener(this);
        adapter.setNotifyOnChange(true);

        //Set the Athletes Database Reference
        athletesReference = FirebaseDatabase.getInstance().getReference("Athlete Users");

        removeFriendButton.setVisibility(View.INVISIBLE);
        viewProfileButton.setVisibility(View.INVISIBLE);

        //Friends List Loader
        loadFriendsList();


    }

    /** ///////////////////////////////////////////////////////////////////////////////////////////
     * The onItemClick() method identifies which Username is selected from User input,
     * and sets the Integer friendIndex based on the position of the user click.
     *      UI buttons based on the addFriendIndex are revealed after first item click.
     * @param l
     * @param v
     * @param position
     * @param id
    ////////////////////////////////////////////////////////////////////////////////////////////*/

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        friendIndex = position;
        friendUsername.setText(friendsList.get(position));
        loadUserBio(friendsListUID.get(position));

        removeFriendButton.setVisibility(View.VISIBLE);
        viewProfileButton.setVisibility(View.VISIBLE);
    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadFriendsList() method is called to refresh the stored list of current Friend UIDs
     * ///////////////////////////////////////////////////////////////////////////////////*/

    private void loadFriendsList() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        athletesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsListUID.clear();
                friendsList.clear();

                String username;

                DataSnapshot friendSnapshot = dataSnapshot.child(uid).child("friendsList");
                ArrayList<String> tempFriendsUIDList = (ArrayList<String>) friendSnapshot.getValue();

                if(tempFriendsUIDList != null) {

                    //For each UID stored in the friends list
                    for (String friendUID : tempFriendsUIDList) {

                        //Look up the username from the list of athletes
                        username = (String) dataSnapshot.child(friendUID).child("name").getValue();

                        //Store the Username and UID locally
                        friendsList.add(username);
                        friendsListUID.add(friendUID);
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

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadUserBIo() method is called in the onItemClick() method
     *
     *  The Method retrieves basic info about a user, from their UID, to populate a short
     *      description while browsing for friends.
     * @param uid
     * /////////////////////////////////////////////////////////////////////////////////////*/

    public void loadUserBio(String uid) {
        profileRef = athletesReference.child(uid).child("profile");

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Load the User Info from the database
                String tempBio = (
                                dataSnapshot.child("bio").getValue().toString() +
                                "\nAge: " + dataSnapshot.child("age").getValue().toString() +
                                "\nGender: " + dataSnapshot.child("gender").getValue().toString() +
                                "\nHeight: " + dataSnapshot.child("height").getValue().toString() + " ft." +
                                "\nWeight: " + dataSnapshot.child("weight").getValue().toString() + "lbs."
                );

                bio.setText(tempBio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * This removeFriend() Method is activated by the "Remove Friend" UI button
     *
     *  It retrieves the current selected index of the AdapterView from onItemClick() method
     *  then removes the UID from the ArrayList friendsListUID
     *  Then updates the database, and refreshes the Friends List
     * /////////////////////////////////////////////////////////////////////////////////////*/

    public void removeFriend(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        //Remove the friend's UID (must be done with new Integer object)
        int index = new Integer(friendIndex);
        friendsListUID.remove(index);

        //Update the database
        athletesReference.child(uid).child("friendsList").setValue(friendsListUID);

        //Refresh the friends list
        loadFriendsList();


    }

    /** UI Button to view the profile of the current user*/
    public void buttonMyProfile (View view){
        //Authentication
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = Auth.getCurrentUser();
        String user = currentUser.getUid();

        //Start the Profile activity and send it the UID String
        Intent intent = new Intent (this, Profile.class);
        intent.putExtra("user",user);
        startActivity(intent);

    }

    /** UI Button method to view the profile of a selected user, based on the Integer friendIndex */
    public void buttonViewProfile (View view){
        //Get the currently selected user's UID
        String user = friendsListUID.get(friendIndex);

        //Start the Profile activity and send it the UID String
        Intent intent = new Intent (this, Profile.class);
        intent.putExtra("user", user);
        startActivity(intent);

    }

    /** UI Button method to open the FriendSearch activity*/
    public void buttonFriendSearch (View view){
        Intent intent = new Intent (this, FriendSearch.class);
        startActivity(intent);

    }

    /** UI Button method to open the AthleteMain activity*/
    public void buttonHome (View view){
        Intent intent = new Intent (this, AthleteMain.class);
        startActivity(intent);

    }
}
