package com.cmsc355.forfit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChallengeSearch extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayList<String> challengeList;
    ArrayAdapter<String> adapter;
    DatabaseReference databaseReference;
    ListView challengeLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_search);

        challengeList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_dark_format, challengeList);
        challengeLV = findViewById(R.id.ChallengeList);
        challengeLV.setAdapter(adapter);
        challengeLV.setOnItemClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Challenges");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> da = dataSnapshot.getChildren();
                for(DataSnapshot data : da){
                    challengeList.add(data.child("name").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }

    public void ButtonBack(View view){
        Intent intent = new Intent (this, AthleteMain.class);
        startActivity(intent);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(this, ChallengeView.class);
        intent.putExtra("name", challengeList.get(position));
        startActivity(intent);
    }
}
