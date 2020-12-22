package com.cmsc355.forfit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc355.forfit.util.UtilMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ChallengeSignUp extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private TextView teamHead;
    private CheckBox buttonRandTeam;
    private ListView teamList;
    ArrayList<String> teamNameList;
    ArrayAdapter<String> teamListAdapter;
    ArrayList<String> currTeamList;
    private CheckBox boxAgree;
    private Button subscribe;
    private Button createTeam;

    private int teamMin;
    private int teamMax;
    private boolean isTeam;

    private int teamIndex;

    private String cName;
    private String tName;

    final Context context = this;

    HashMap<String, HashMap<String, String>> currChallenges;
    ArrayList<String> subAthletes;
    HashMap<String, ArrayList<String>> teams;

    DatabaseReference databaseReferenceChallenge;
    DatabaseReference databaseReferenceAthlete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_sign_up);
        teamIndex = 0;

        Intent intent = this.getIntent();
        cName = intent.getStringExtra("name");

        TextView challName = findViewById(R.id.CSU_ChallengeName);
        challName.setText(cName);
        teamHead = findViewById(R.id.CSU_TeamHeader);
        buttonRandTeam = findViewById(R.id.CSU_ButtonRandomTeam);
        teamList = findViewById(R.id.CSU_TeamListView);
        teamNameList = new ArrayList<>();
        teamListAdapter = new ArrayAdapter<>(this, R.layout.list_dark_format, teamNameList);
        boxAgree = findViewById(R.id.CSU_SafetyAgree);
        subscribe = findViewById(R.id.CSU_ButtonSubscribe);
        createTeam = findViewById(R.id.CSU_ButtonCreateTeam);

        teamList.setAdapter(teamListAdapter);
        teamList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        teamList.setOnItemClickListener(this);
        teamListAdapter.setNotifyOnChange(true);

        teams = new HashMap<>();
        currTeamList = new ArrayList<>();

        databaseReferenceChallenge = FirebaseDatabase.getInstance().getReference("Challenges");
        databaseReferenceAthlete = FirebaseDatabase.getInstance().getReference("Athlete Users");

        currChallenges = new HashMap<>();
        subAthletes = new ArrayList<>();

        buttonRandTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    teamHead.setVisibility(View.GONE);
                    teamList.setVisibility(View.GONE);
                    createTeam.setVisibility(View.GONE);
                }
                else{
                    teamHead.setVisibility(View.VISIBLE);
                    teamList.setVisibility(View.VISIBLE);
                    createTeam.setVisibility(View.VISIBLE);
                }

            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final String uid = user.getUid();

        //***************************************************************************************************************************************\\
        //this grabs the names of the teams for display in the listview
        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("teams");
                HashMap<String, ArrayList<String>> teamnT = (HashMap<String, ArrayList<String>>) dataSnapshot1.getValue();
                if(teamnT != null) {
                    teamNameList.addAll(teamnT.keySet());
                }
                teamListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //***************************************************************************************************************************************\\

        //***************************************************************************************************************************************\\
        //This should be grabbing all the challenges that the athlete is currently subscribed to.
        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("teams");
                HashMap<String, ArrayList<String>> teamsT = (HashMap<String, ArrayList<String>>) dataSnapshot1.getValue();
                if(teamsT != null) {
                    teams.putAll(teamsT);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        //***************************************************************************************************************************************\\


        //***************************************************************************************************************************************\\
        /*
        This handles the popup message prompt for the team name for creation.
         */
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.activity_challenge_sign_up_popup, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = promptsView.findViewById(R.id.CSU_P_teamName);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        /*
                                        This is were stuff happens
                                         */
                                        ArrayList<String> valiu = new ArrayList<>();
                                        valiu.add("null");
                                        String cInput = UtilMethods.cleanString(userInput.getText().toString());
                                        teams.put(cInput, valiu);

                                        databaseReferenceChallenge.child(cName).child("teams").setValue(teams);

                                        teamNameList.add(cInput);
                                        teamListAdapter.notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
        //***************************************************************************************************************************************\\



        //***************************************************************************************************************************************\\
        //This should be grabbing all the challenges that the athlete is currently subscribed to.
        databaseReferenceAthlete.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(uid).child("currChallenges");
                HashMap<String, HashMap<String, String>> currChallengesT = (HashMap<String, HashMap<String, String>>) dataSnapshot1.getValue();
                if(currChallengesT != null) {
                    currChallenges.putAll(currChallengesT);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        //***************************************************************************************************************************************\\

        //***************************************************************************************************************************************\\
        //This should be grabbing all the athletes subscribed to the challenge.
        //Gets the team size.
        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshotT = dataSnapshot.child(cName);
                teamMax = Integer.parseInt((String) dataSnapshotT.child("maxTeamsize").getValue());
                teamMin = Integer.parseInt((String) dataSnapshotT.child("minTeamsize").getValue());
                if(teamMin == teamMax && teamMin == 1){
                    teamHead.setVisibility(View.GONE);
                    buttonRandTeam.setVisibility(View.GONE);
                    teamList.setVisibility(View.GONE);
                    createTeam.setVisibility(View.GONE);
                    isTeam = false;
                }
                else{
                    isTeam = true;
                }

                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("subscribedAthletes");
                ArrayList<String> subAthletesT = (ArrayList<String>) dataSnapshot1.getValue();
                if(subAthletesT != null) {
                    subAthletes.addAll(subAthletesT);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        //***************************************************************************************************************************************\\

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        teamIndex = position;
        System.out.println("TeamIndex: " + teamIndex);
        System.out.println("TeamListSelected: " + teamList.getSelectedItemPosition());
        //***************************************************************************************************************************************\\
        //This gets the current List for the team index selected
        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("teams").child(teamNameList.get(teamIndex));
                ArrayList<String> currAthletesT = (ArrayList<String>) dataSnapshot1.getValue();
                if(currAthletesT != null){
                    if(currAthletesT.contains("null"))
                        currAthletesT.remove("null");
                    currTeamList.addAll(currAthletesT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //***************************************************************************************************************************************\\

    }


    public void Subscribe(View view){
        if(boxAgree.isChecked() ){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            final String uid = user.getUid();

            if(buttonRandTeam.isChecked()){
                Random rand = new Random();
                teamIndex = rand.nextInt(teamNameList.size());
            }

            if(isTeam){
                if(isGoodTeam(teamNameList.get(teamIndex))){
                    HashMap<String, String> challTempMap = new HashMap<>();
                    challTempMap.put("team", teamNameList.get(teamIndex));
                    currChallenges.put(cName, challTempMap);

                    databaseReferenceAthlete.child(uid).child("currChallenges").setValue(currChallenges);
                    subAthletes.add(uid);
                    databaseReferenceChallenge.child(cName).child("subscribedAthletes").setValue(subAthletes);

                    currTeamList.add(uid);
                    databaseReferenceChallenge.child(cName).child("teams").child(teamNameList.get(teamIndex)).setValue(currTeamList);
                    Intent intent = new Intent(this, ChallengeView.class);
                    intent.putExtra("name", cName);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Team is full.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                HashMap<String, String> challTempMap = new HashMap<>();
                challTempMap.put("team", "none");

                currChallenges.put(cName, challTempMap);
                databaseReferenceAthlete.child(uid).child("currChallenges").setValue(currChallenges);
                subAthletes.add(uid);
                databaseReferenceChallenge.child(cName).child("subscribedAthletes").setValue(subAthletes);
                Intent intent = new Intent(this, ChallengeView.class);
                intent.putExtra("name", cName);
                startActivity(intent);
                finish();
            }


        }
        else{
            Toast.makeText(getApplicationContext(), "Please agree to the warning.", Toast.LENGTH_SHORT).show();
        }
    }

    int currTeammemNum = 0;

    private boolean isGoodTeam(final String name){


        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("teams").child(name);
                ArrayList<String> currMem = (ArrayList<String>) dataSnapshot1.getValue();
                currTeammemNum = currMem.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return (currTeammemNum < teamMax);
    }

}
