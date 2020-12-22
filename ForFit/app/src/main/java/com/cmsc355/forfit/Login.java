package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {


    Button login;
    Button signup;
    Button coachtemp;
    Button athletetemp;

    TextView forgotpass;

    EditText email;
    EditText password;

    ArrayList<String> athletelist;
    ArrayList<String> coachlist;

    private FirebaseAuth Auth;
    DatabaseReference Data;
    DatabaseReference Data2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_login);

        //////////////////////////////////////////////////////////////////////////////////
        // Background video player.
        /////////////////////////////////////////////////////////////////////////////////
        //VideoView videoview = (VideoView) findViewById(R.id.videoView);
        //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.live2);
        //videoview.setVideoURI(uri);
        //videoview.start();

        //////////////////////////////////////////////////////////////////////////////////
        // setting
        /////////////////////////////////////////////////////////////////////////////////

        athletelist = new ArrayList<>();
        coachlist = new ArrayList<>();


        coachtemp = findViewById(R.id.BCoachTemp);
        athletetemp = findViewById(R.id.BAthleteTemp);

        login = findViewById(R.id.LoginButton);
        signup = findViewById(R.id.CAAbutton);
        email = findViewById(R.id.EmailEntry);
        password = findViewById(R.id.PasswordEntry);
        forgotpass=findViewById(R.id.log_forgotpasstxt);

        Auth = FirebaseAuth.getInstance();
        Data = FirebaseDatabase.getInstance().getReference("Athlete Users");
        Data2= FirebaseDatabase.getInstance().getReference("Coach Users");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Removing the temp buttons.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        coachtemp.setVisibility(View.GONE);
        athletetemp.setVisibility(View.GONE);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////
        // Database listener for athletes;
        /////////////////////////////////////////////////////////////////////////////////

        // Read from the database
        Data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    //Look in the Athlete-Data-Snapshot for a child called "name" and set its value(String)
                    String username = postSnapshot.child("id").getValue(String.class);

                    // Putting Data into ArrayList \\
                    athletelist.add(username);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



        //////////////////////////////////////////////////////////////////////////////////
        // Database listener for coaches;
        /////////////////////////////////////////////////////////////////////////////////

        // Read from the database
        Data2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    //Look in the Athlete-Data-Snapshot for a child called "name" and set its value(String)
                    String username2 = postSnapshot.child("id").getValue(String.class);

                    // Putting Data into ArrayList \\
                    coachlist.add(username2);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //////////////////////////////////////////////////////////////////////////////////
        // Forgot Password text
        /////////////////////////////////////////////////////////////////////////////////
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,PasswordRecovery.class));
            }
        });



        //////////////////////////////////////////////////////////////////////////////////
        // signup button
        /////////////////////////////////////////////////////////////////////////////////
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, CreateAccount.class));
                //finish();
            }
        });

        //////////////////////////////////////////////////////////////////////////////////
        // login button
        /////////////////////////////////////////////////////////////////////////////////
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ////////////////////////////////////////////////////////////////////////////////////////////
                // Checking inputs
                ////////////////////////////////////////////////////////////////////////////////////////////
                String em = email.getText().toString();
                String pass = password.getText().toString();

                if (em.isEmpty()) {
                    email.setError("username required");
                    email.requestFocus();
                    return;
                }
                if (pass.isEmpty()) {
                    password.setError("username required");
                    password.requestFocus();
                    return;

                }


                ////////////////////////////////////////////////////////////////////////////////////////////
                // Checking Authentication
                ////////////////////////////////////////////////////////////////////////////////////////////
                Auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = Auth.getCurrentUser();
                                    String currentuserID = user.getUid();
                                   boolean verifyoff = true;
                                    if ( verifyoff/*user.isEmailVerified()*/){
                                        if (athletelist.contains(currentuserID)) {
                                            Intent intent = new Intent(Login.this, AthleteMain.class);
                                            startActivity(intent);
                                            //finish();
                                        }
                                        else if (coachlist.contains(currentuserID))
                                        {
                                            Intent intent = new Intent(Login.this, CoachMain.class);
                                            startActivity(intent);
                                            //finish();
                                        }
                                    }
                                    else
                                    {
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Login.this, "Verification E-mail Sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }





                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }

                            }
                        });

            }
        });


        //////////////////////////////////////////////////////////////////////////////////
        // coach temp
        /////////////////////////////////////////////////////////////////////////////////
        coachtemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, CoachMain.class));
                //finish();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////
        // athlete temp
        /////////////////////////////////////////////////////////////////////////////////
        athletetemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, AthleteMain.class));
                //finish();
            }
        });


    }



}
