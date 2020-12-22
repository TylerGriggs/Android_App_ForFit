package com.cmsc355.forfit;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendSearch extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> friendsListUID;
    ArrayList<String> searchList;
    ArrayList<String> searchListUID;
    ArrayList<String> filteredList;
    ArrayAdapter<String> adapter;

    Integer addFriendIndex;

    private ListView friendsListView;

    private TextView title;
    private SearchView search;
    private TextView friendUsername;
    private TextView bio;
    private ImageView profilePic;
    private Button addFriendButton;
    private Button viewProfileButton;

    DatabaseReference athletesReference;
    DatabaseReference profileRef;

    ArrayList<String> tempAnnouncementList;
    String myUsername;

    private FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        title = findViewById(R.id.FS_titleFriendSearch);

        friendUsername = findViewById(R.id.FS_textViewUserName);
        bio = findViewById(R.id.FS_textViewUserBio);
        profilePic = findViewById(R.id.FS_imageViewUserProfilePic);
        addFriendButton = findViewById(R.id.FS_buttonAddFriend);
        viewProfileButton = findViewById(R.id.FS_buttonViewProfile);

        //ArrayList To store data from database, and a filtered list to change based on searches
        friendsListUID = new ArrayList<>();
        searchList = new ArrayList<>();
        searchListUID = new ArrayList<>();
        filteredList = new ArrayList<>();

        tempAnnouncementList = new ArrayList<>();
        myUsername = new String();

        //Automatic adapter for ArrayList data into ListView, with preset options "android.R.layout..."
        adapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, searchList);

        //Define the ListView to change
        friendsListView = findViewById(R.id.FS_friendListView);

        //Set the Athletes Database Reference
        athletesReference = FirebaseDatabase.getInstance().getReference("Athlete Users");

        //Set list-action buttons invisible
        addFriendButton.setVisibility(View.INVISIBLE);
        viewProfileButton.setVisibility(View.INVISIBLE);

        //Set the adapter on that ListView
        friendsListView.setAdapter(adapter);
        friendsListView.setOnItemClickListener(this);
        adapter.setNotifyOnChange(true);

        //Retrieve and load filtered athletes, and activate SearchView
        loadFriendsList();
        loadSearchList();

    }

    /** ///////////////////////////////////////////////////////////////////////////////////////////
     * The onItemClick() method identifies which Username is selected from User input,
     * and sets the addFriendIndex variable based on the position of the user click.
     *      UI buttons based on the addFriendIndex are revealed after first item click.
     * @param l
     * @param v
     * @param position
     * @param id
     ////////////////////////////////////////////////////////////////////////////////////////////*/

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        addFriendIndex = position;
        friendUsername.setText(searchList.get(position));
        loadUserBio(searchListUID.get(position));

        //Make the buttons for list-actions viewable
        addFriendButton.setVisibility(View.VISIBLE);
        viewProfileButton.setVisibility(View.VISIBLE);
    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadFriendsList() method is called to refresh the stored list of current Friend UIDs
     * ///////////////////////////////////////////////////////////////////////////////////*/

    private void loadFriendsList() {
        //Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        //Get a Reference for the current user and set their Username and FriendsList
        DatabaseReference thisUserRef = athletesReference.child(uid);
        thisUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsListUID.clear();
                //Update the Current User's Usersname
                myUsername = (String) dataSnapshot.child("name").getValue();

                DataSnapshot friendListSnapshot = dataSnapshot.child("friendsList");
                ArrayList<String> tempFriendsList = (ArrayList<String>) friendListSnapshot.getValue();

                if(tempFriendsList != null) {
                    friendsListUID.addAll(tempFriendsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * The loadSearchList() Method is called after refreshing the friend's list by calling loadFriendsList()
     * This method loads all athlete users, except those on the friends list, into the search list
     * ///////////////////////////////////////////////////////////////////////////////////*/

    private void loadSearchList() {
        //Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        // Of all athlete users
        athletesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchList.clear();
                searchListUID.clear();

                String uniqueID;
                String friendUsername;

                //For Each athlete user
                for (DataSnapshot athleteSnapshot : dataSnapshot.getChildren()) {
                    uniqueID = athleteSnapshot.child("id").getValue(String.class);

                    // If the UID is not the current user UID, and it's not already in the Friends List
                    if(!friendsListUID.contains(uniqueID) && !uniqueID.equals(uid)) {
                        //Look in the Athlete-Data-Snapshot for a child called "name" and set its value(String)
                        friendUsername = athleteSnapshot.child("name").getValue(String.class);

                        //Put String username into Username and UID ArrayList \\
                        searchList.add(friendUsername);
                        searchListUID.add(uniqueID);
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
    * This Method is activated by the "Add Friend" UI button
    *
    *  It retrieves the current selected index of the AdapterView from OnItemClick
    *  Retreives the UID from the searchListUID, adds to the friendListUID
    *  Then updates the database, and the searchable list.
    * /////////////////////////////////////////////////////////////////////////////////////*/

    public void addFriend(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        ////////////////////////////////////////////////////
        // ADD FRIEND TO FRIENDS LIST

        //Pull current index from AdapterView, find associated UID, add it to the friendsListUID
        final String friendUID = searchListUID.get(addFriendIndex);
        final String friendName = searchList.get(addFriendIndex);
        friendsListUID.add(friendUID);

        //Update the User's Friend's List with a the new version, and notify them on screen
        athletesReference.child(uid).child("friendsList").setValue(friendsListUID);
        Toast.makeText(FriendSearch.this, "Added "+ friendName +" to Friends", Toast.LENGTH_SHORT).show();

        ////////////////////////////////////////////////////
        // SEND FRIEND NOTIFICATION

        //Load the friend's announcements, as to not erase any when updating
        collectAnnouncements(friendUID);

        //Create the Personalized Announcement for the new friend
        String friendNotification = "Athlete "+ myUsername +" just added you to their Friends List!";
        tempAnnouncementList.add(friendNotification);

        //Update the friend's announcements in the database
        athletesReference.child(friendUID).child("announcements").setValue(tempAnnouncementList);

        //Reload the Searchable List and Friends List
        loadFriendsList();
        loadSearchList();

    }

    /** ///////////////////////////////////////////////////////////////////////////////////
     * This Method is called in the addFriend() method.
     *  The method retrieves the current announcements of a friend with the parameter String UID
     * @param friendUID
     * /////////////////////////////////////////////////////////////////////////////////////*/

    public void collectAnnouncements (String friendUID) {
        tempAnnouncementList.clear();

        //Get Database Reference of the new Friend
        DatabaseReference friendAnnouncementRef = athletesReference.child(friendUID).child("announcements");
        //Send Friend a notification to their announcements
        friendAnnouncementRef.addValueEventListener(new ValueEventListener() {
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
                String tempBio = dataSnapshot.child("bio").getValue().toString();
                tempBio = tempBio.concat("\nAge: " + dataSnapshot.child("age").getValue().toString());
                tempBio = tempBio.concat("\nGender: " + dataSnapshot.child("gender").getValue().toString());
                tempBio = tempBio.concat("\nHeight: " + dataSnapshot.child("height").getValue().toString() + " ft.");
                tempBio = tempBio.concat("\nWeight: " + dataSnapshot.child("weight").getValue().toString() + " lbs.");

                bio.setText(tempBio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    /** UI Button to view the profile of a selected user
     *  The Integer addFriendIndex is set in the onItemClick() method */
    public void buttonViewProfile (View view){
        //Get the currently selected user's UID
        String user = searchListUID.get(addFriendIndex);

        //Start the Profile activity and send it the UID String
        Intent intent = new Intent (this, Profile.class);
        intent.putExtra("user", user);
        startActivity(intent);

    }

    /** UI Button to open the FriendsList activity*/
    public void buttonFriendList (View view){
        Intent intent = new Intent (this, FriendList.class);
        startActivity(intent);

    }

    /** UI Button to open the AthleteMain activity*/
    public void buttonHome (View view){
        Intent intent = new Intent (this, AthleteMain.class);
        startActivity(intent);

    }



}



