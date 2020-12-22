package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PasswordRecovery extends AppCompatActivity {


    private Button send;

    private EditText email;

    private FirebaseAuth firebaseAuth;


    private TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);


        send = findViewById(R.id.pass_sendbtn);
        email = findViewById(R.id.pass_email);
        firebaseAuth = FirebaseAuth.getInstance();

        //testing
        ////////////////////////////////////////////////
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(); // used to get the info
        // DatabaseReference r2 = database.getReference().child("posts");  // these were used to create the new part on the database.
        //r2.push().setValue(new Post("test1","test2")); // these were used to create the new part on the database.

        test = findViewById(R.id.p_test);

        ref.child("posts").addValueEventListener(new ValueEventListener() { //gets from the post part of the database..
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> posts = dataSnapshot.getChildren(); // creates an iterator for all the objects under post
             for(DataSnapshot child:posts){ // goes through each post.
               Post chall = child.getValue(Post.class); // creates new post object and saves it as one from the iterator of objects
               test.setText(chall.s2); // sets text box  from each object
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Getting password reset e-mail to be sent.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String useremail = email.getText().toString().trim();

                if (useremail.equals("")) {
                    Toast.makeText(PasswordRecovery.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordRecovery.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordRecovery.this, Login.class));
                            }
                        }
                    });
                }


            }
        });


    }

  // test class that was made to be sent to the database
    public static class Post{
        public String s1;
        public String s2;

        public Post(){}
        public Post( String a, String B){
            this.s1 = a;
            this.s2 =B;
        }

    }



}
