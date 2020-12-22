package com.cmsc355.forfit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.cmsc355.forfit.custObjects.PersonalInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;

public class ProfileEdit extends AppCompatActivity implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

    private Button removeButton;
    private Button submit;
    private Button defaultprofile;

    private EditText bio;
    private EditText age;
    private EditText weight;
    private EditText height;
    private EditText gender;


    private TextView username;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference profiledatabase,databaseReferenceAthlete;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // buttons
        removeButton = findViewById(R.id.EP_buttonRemoveProfile);
        submit = findViewById(R.id.EP_buttonSubmitChanges);
        defaultprofile = findViewById(R.id.EP_buttonDefualtProfile);

        //text view
        username =findViewById(R.id.EP_textViewUsername);

        //edit text
        bio =findViewById(R.id.EP_UserBio);
        age = findViewById(R.id.EP_Age_Display);
        weight = findViewById(R.id.EP_WeightDisplay);
        height = findViewById(R.id.EP_HeightDisplay);
        gender = findViewById(R.id.EP_Gender_Display);



        //auth
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();

        // database
        mData = FirebaseDatabase.getInstance();
        databaseReferenceAthlete = mData.getReference("Athlete Users").child(current.getUid());
        profiledatabase = mData.getReference("Athlete Users").child(current.getUid()).child("profile");




        final PersonalInfo[] personalInfo = {new PersonalInfo()};


        dialog= new ProgressDialog(this);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Reads from the database and sets the edit text boxes to the values stored.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        databaseReferenceAthlete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user = dataSnapshot.child("name").getValue().toString();


                username.setText(user);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Reads from the database and sets the edit text boxes to the values stored.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        profiledatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PersonalInfo personal = dataSnapshot.getValue(PersonalInfo.class);
                personalInfo[0] = personal;


                age.setText(personal.getAge());
                bio.setText(personal.getBio());
                weight.setText(personal.getWeight());
                height.setText(personal.getHeight());
                gender.setText(personal.getGender());




                // Previous, testing using objects
                /*
                String uage = dataSnapshot.child("age").getValue().toString();
                String ubio = dataSnapshot.child("bio").getValue().toString();
                String uweight = dataSnapshot.child("weight").getValue().toString();
                String uheight = dataSnapshot.child("height").getValue().toString();
                String ugender = dataSnapshot.child("gender").getValue().toString();


                age.setText(uage);
                bio.setText(ubio);
                weight.setText(uweight);
                height.setText(uheight);
                gender.setText(ugender);

                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Updates the profile based on inputs.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
                builder.setMessage("Are you happy with the profile updates?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If accept


                                personalInfo[0].setAge(age.getText().toString());
                                personalInfo[0].setBio(bio.getText().toString());
                                personalInfo[0].setWeight(weight.getText().toString());
                                personalInfo[0].setHeight(height.getText().toString());
                                personalInfo[0].setGender(gender.getText().toString());

                                profiledatabase.setValue(personalInfo[0]);

                                /*
                                profiledatabase.child("age").setValue(age.getText().toString());
                                profiledatabase.child("bio").setValue(bio.getText().toString());
                                profiledatabase.child("weight").setValue(weight.getText().toString());
                                profiledatabase.child("height").setValue(height.getText().toString());
                                profiledatabase.child("gender").setValue(gender.getText().toString());
                                */

                                Toast.makeText(ProfileEdit.this, "Profile updated Successfully", Toast.LENGTH_SHORT).show();

                                String user = current.getUid().toString();
                                Intent intent = new Intent(ProfileEdit.this,Profile.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If cancel
                                return;
                            }
                        });
                builder.setTitle("Edit Profile");
                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Sets the profile to default.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        defaultprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
                builder.setMessage("Set Profile to default?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If accept


                                personalInfo[0].setAge("PRIVATE");
                                personalInfo[0].setBio("PRIVATE");
                                personalInfo[0].setWeight("PRIVATE");
                                personalInfo[0].setHeight("PRIVATE");
                                personalInfo[0].setGender("PRIVATE");

                                profiledatabase.setValue(personalInfo[0]);



                                //Previous before it started working with objects
                               /*
                                profiledatabase.child("age").setValue("");
                                profiledatabase.child("bio").setValue("");
                                profiledatabase.child("weight").setValue("");
                                profiledatabase.child("height").setValue("");
                                profiledatabase.child("gender").setValue("");
                                */

                                Toast.makeText(ProfileEdit.this, "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                                String user = current.getUid().toString();
                                Intent intent = new Intent(ProfileEdit.this,Profile.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If cancel
                                return;
                            }
                        });
                builder.setTitle("Edit Profile");
                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });




    }



    public void buttonMyProfile (View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);

    }

    public void FriendList(View view){
        Intent intent = new Intent (this, FriendList.class);
        startActivity(intent);

    }

    public void buttonHome (View view){
        Intent intent = new Intent(this, AthleteMain.class);
        startActivity(intent);

    }


//CKG 27Mar2019 popup message
    protected void showPopup(View view){
        PopupMenu popup= new PopupMenu(this, view);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
       switch (menuItem.getItemId()){
           case R.id.item1:
               Toast.makeText(this,"Item 1 clicked", Toast.LENGTH_SHORT).show();
               return true;
           case R.id.item2:
               Toast.makeText(this,"Item 2 clicked", Toast.LENGTH_SHORT).show();
               return true;
           default:
               return false;
       }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Remove account
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //CKG 24Mar2019 Added functionality to allow the user to remove account.
    public void buttonRemove (View view){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

/*

        if (user != null) {
            dialog.setMessage("Removing Account... Please Wait"); //progress of account being removed
            dialog.show(); //show the progress message
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileEdit.this, "Account Removed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent delete= new Intent(ProfileEdit.this, Login.class); //leave profile edit screen and go to login.
                        startActivity(delete);

                    } else {
                        Toast.makeText(ProfileEdit.this, "Account could not be removed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }*/

        // CKG 03Apr2019 Added improved functionality to allow the user to remove account.
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileEdit.this);
                dialog.setTitle("Are You Sure?");
                dialog.setMessage("Deleting this account will remove it completely.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ProfileEdit.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                            Intent delete= new Intent(ProfileEdit.this, Login.class); //leave profile edit screen and go to login.
                                            startActivity(delete);
                                        }else{
                                            Toast.makeText(ProfileEdit.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

}