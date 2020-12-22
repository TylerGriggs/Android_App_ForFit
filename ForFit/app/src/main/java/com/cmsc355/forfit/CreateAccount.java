package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cmsc355.forfit.custObjects.AthleteUser;
import com.cmsc355.forfit.custObjects.CoachUser;
import com.cmsc355.forfit.custObjects.PersonalInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreateAccount extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;
    EditText phonenumber;

    Button signup;

    ProgressBar progress;

    CheckBox athlete;
    CheckBox coach;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        username = findViewById(R.id.CA_UserName);
        email = findViewById(R.id.CA_EmailAddress);
        password = findViewById(R.id.CA_Password);
        signup = findViewById(R.id.CA_CreateAccount);
        progress = findViewById(R.id.CA_progress);
        phonenumber = findViewById(R.id.CA_Phone);
        athlete = findViewById(R.id.CA_boxAthlete);
        coach = findViewById(R.id.CA_boxCoach);
        mAuth = FirebaseAuth.getInstance();




        ////////////////////////////////////////////////////////////////////////////////////////////
        // Determining if Athlete or Coach user. Check Boxes
        ////////////////////////////////////////////////////////////////////////////////////////////

        athlete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    coach.setChecked(false);
                }
            }
        });
        coach.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    athlete.setChecked(false);
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Hide progress bar
        ////////////////////////////////////////////////////////////////////////////////////////////
        progress.setVisibility(View.GONE);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Signup button
        ////////////////////////////////////////////////////////////////////////////////////////////

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = username.getText().toString().trim();
                String mail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String phone = phonenumber.getText().toString().trim();

                ////////////////////////////////////////////////////////////////////////////////////////////
                // Checking inputs
                ////////////////////////////////////////////////////////////////////////////////////////////

                if (name.isEmpty()) {
                    username.setError("username required");
                    username.requestFocus();
                    return;
                }
                if (mail.isEmpty()) {
                    email.setError("email required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setError("enter a valid email");
                    email.requestFocus();
                    return;
                }
                if (pass.length() < 6) {
                    password.setError("Password should be at least 6 characters long");
                    password.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    phonenumber.setError("enter a phone number");
                    phonenumber.requestFocus();
                    return;
                }

                if (phone.length() != 10) {
                    phonenumber.setError("enter valid phone number");
                    phonenumber.requestFocus();
                    return;
                }

                if (athlete.isChecked() == false && coach.isChecked() == false) {
                    athlete.setError("Pick athlete user or ");
                    athlete.requestFocus();
                    coach.setError("Pick athlete user or ");
                    coach.requestFocus();
                    return;
                }


                progress.setVisibility(View.VISIBLE);

                ////////////////////////////////////////////////////////////////////////////////////////////
                // Authentication to create new user depending on coach or athlete.
                ////////////////////////////////////////////////////////////////////////////////////////////


                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    if (coach.isChecked() == true) {
                                        CoachUser user = new CoachUser(username.getText().toString(), email.getText().toString(), phonenumber.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        FirebaseDatabase.getInstance().getReference("Coach Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CreateAccount.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    user.sendEmailVerification();
                                                    startActivity(new Intent(CreateAccount.this, Login.class));
                                                    finish();

                                                } else {
                                                    // display message failure
                                                }
                                            }
                                        });

                                } else {

                                        PersonalInfo profile = new PersonalInfo("","","","","");

                                        String welcomeAnnouncement = "Thanks for signing up ForFit! To get started, search for challenges, or find friends!";
                                        ArrayList<String> tempAnnouncements = new ArrayList<>();
                                        tempAnnouncements.add(welcomeAnnouncement);

                                        AthleteUser user = new AthleteUser(username.getText().toString(), email.getText().toString(), phonenumber.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), profile, tempAnnouncements);

                                        FirebaseDatabase.getInstance().getReference("Athlete Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(CreateAccount.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                    // user id
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    // sending user a e-mail verification.
                                                    user.sendEmailVerification();
                                                    // sending back to login page.
                                                    finish();
                                                    startActivity(new Intent(CreateAccount.this, Login.class));


                                                } else {
                                                    // display message failure
                                                }
                                            }
                                        });
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(CreateAccount.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }


                                // ...
                            }
                        });


            }
        });


    }
}
