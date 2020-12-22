package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.cmsc355.forfit.custObjects.AthleteUser;
import com.cmsc355.forfit.custObjects.PersonalInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {


    private TextView username;
    private TextView email;
    private TextView phone;
    private TextView bio;
    private TextView age;
    private TextView gender;
    private TextView height;
    private TextView weight;

    private Button editProfileButton;
    private Button friendsListButton;


    private FirebaseAuth Auth;
    DatabaseReference UserData;
    DatabaseReference Profile;
    DatabaseReference CoachUsersRef;

    Boolean isCoach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = Auth.getCurrentUser();
        username = findViewById(R.id.p_username);
        email = findViewById(R.id.p_mail);
        phone = findViewById(R.id.p_phone);
        bio = findViewById(R.id.p_profile);
        age = findViewById(R.id.p_age);
        gender = findViewById(R.id.p_gender);
        height = findViewById(R.id.p_height);
        weight = findViewById(R.id.p_weight);
        editProfileButton = findViewById(R.id.AP_EditButton);
        friendsListButton = findViewById(R.id.AP_FriendsButton);

        final String currentuser = getIntent().getStringExtra("user");
        UserData = FirebaseDatabase.getInstance().getReference("Athlete Users").child(currentuser);
        Profile = FirebaseDatabase.getInstance().getReference("Athlete Users").child(currentuser).child("profile");
        CoachUsersRef = FirebaseDatabase.getInstance().getReference("Coach Users");
        isCoach = false;



      /*  /////test////////
        Profile2 = FirebaseDatabase.getInstance().getReference("Athlete Users").child(currentUser.getUid()).child("profile");
        test = findViewById(R.id.pro_test);
        Profile2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PersonalInfo personal = dataSnapshot.getValue(PersonalInfo.class);
                test.setText(personal.getAge());
                updateprofile(personal);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////test/////// */


        //Hide the UI buttons if the current user is a coach
        CoachUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String coach;
                String me = currentUser.getUid();
                //For each coach on the database
                for (DataSnapshot coachSnapshot : dataSnapshot.getChildren()) {
                    coach = coachSnapshot.child("id").getValue().toString();

                    //If the current user is a coach
                    if(me.equals(coach)){
                        isCoach = true;
                        //Set the Athlete specific buttons to invisible
                        editProfileButton.setVisibility(View.INVISIBLE);
                        friendsListButton.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        UserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AthleteUser athlete = dataSnapshot.getValue(AthleteUser.class);
                username.setText(athlete.getName());
                email.setText(athlete.getEmail());
                phone.setText(athlete.getPhone());

                // Previous and commented out to use objects to fill info
                /*
                username.setText(dataSnapshot.child("name").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString());
                */
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        Profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PersonalInfo personal = dataSnapshot.getValue(PersonalInfo.class);
                bio.setText(personal.getBio());
                age.setText(personal.getAge());
                height.setText(personal.getHeight());
                gender.setText(personal.getGender());
                weight.setText(personal.getWeight());


                //Commented this out so that I could use objects to get all the data.
                /*
                bio.setText(dataSnapshot.child("bio").getValue().toString());
                age.setText(dataSnapshot.child("age").getValue().toString());
                gender.setText(dataSnapshot.child("gender").getValue().toString());
                height.setText(dataSnapshot.child("height").getValue().toString());
                weight.setText(dataSnapshot.child("weight").getValue().toString());
                */


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void EditProfile(View view){
        Intent intent = new Intent (this, ProfileEdit.class);
        startActivity(intent);

    }

    public void buttonFriendList (View view){
        Intent intent = new Intent (this, FriendList.class);
        startActivity(intent);

    }

    public void buttonHome (View view){
        if(isCoach == false) {
            Intent intent = new Intent(this, AthleteMain.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CoachMain.class);
            startActivity(intent);
        }
    }


    //testing, remove before submitting.
   /* public void updateprofile(PersonalInfo info) {
        Auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = Auth.getCurrentUser();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("Athlete Users").child(currentUser.getUid()).child("profile");
        info.setBio("WELL THANK YOU FOR WORKING YOU SHIT");

        data.setValue(info);

    }*/

}
