package com.cmsc355.forfit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc355.forfit.util.exerciseAdapterClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChallengeView extends AppCompatActivity {

    public static Context context;

    //Variables
    String team;
    HashMap<String, String> currChallenges;
    ArrayList<String> subAthletes;
    ArrayList<String> exerciseList;
    ArrayList<String> teamMem;

    //System
    static public Intent intent;
    public static String cName;

    //XML
    private TextView name;
    private TextView startDateTV;
    private TextView endDateTV;
    private TextView description;
    private Button bSubscribe;
    private Button bDropout;
    private TextView exerciseTitle;

    private ProgressBar difficulty;

    //Exercise List
    private RecyclerView exerciseRecyclerView;
    private RecyclerView.Adapter exerciseAdapter;
    private RecyclerView.LayoutManager exerciselayoutManager;

    //Trophy Reward
    ImageView goldTrophy;
    Animation slideUpAnimation;
    Button revealTrophyButton;

    //Database
    DatabaseReference databaseReferenceChallenge;
    DatabaseReference databaseReferenceAthlete;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initial setup of activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_view);

        ChallengeView.context = getApplicationContext();

        //variable list
        currChallenges = new HashMap<>();
        subAthletes = new ArrayList<>();
        exerciseList = new ArrayList<>();
        teamMem = new ArrayList<>();

        //system references
        intent = getIntent();
        cName = intent.getStringExtra("name");

        //Authentication references
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final String uid = user.getUid();

        //XML references
        name = findViewById(R.id.ChallengeName);
        startDateTV = findViewById(R.id.DurationTextStart);
        endDateTV = findViewById(R.id.DurationTextEnd);
        description = findViewById(R.id.DescriptionText);
        difficulty = findViewById(R.id.difficultyBar);

        bSubscribe = findViewById(R.id.BSignUp);
        bDropout = findViewById(R.id.CV_DropOut);
        exerciseTitle = findViewById(R.id.ExercisesHeader);

        //Exercise List
        exerciseRecyclerView = findViewById(R.id.ExerciseView);
        exerciseRecyclerView.setHasFixedSize(true);

        exerciselayoutManager = new LinearLayoutManager(this);
        exerciseRecyclerView.setLayoutManager(exerciselayoutManager);
        exerciseAdapter = new exerciseAdapterClass(exerciseList);

        //Trophy
        goldTrophy = findViewById(R.id.CV_GoldTrophyView);
        goldTrophy.setVisibility(View.INVISIBLE);
        revealTrophyButton = findViewById(R.id.CV_RevealTrophyButton);
        revealTrophyButton.setVisibility(View.GONE);

        //database references
        databaseReferenceChallenge = FirebaseDatabase.getInstance().getReference("Challenges");
        databaseReferenceAthlete = FirebaseDatabase.getInstance().getReference("Athlete Users");
        databaseReference = FirebaseDatabase.getInstance().getReference();


        //Initial population of views and variables
        databaseReferenceChallenge.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot da = dataSnapshot.child(cName);

                String dateStart = da.child("startDate").child("day").getValue() + "/" + da.child("startDate").child("month").getValue()+ "/" + da.child("startDate").child("year").getValue();
                String dateEnd = da.child("endDate").child("day").getValue() + "/" + da.child("endDate").child("month").getValue()+ "/" + da.child("endDate").child("year").getValue();


                long diff = (long)da.child("difficulty").getValue();
                int tDifficulty = (int)diff;

                name.setText((String)da.child("name").getValue());
                startDateTV.setText(dateStart);
                endDateTV.setText(dateEnd);
                description.setText((String)da.child("description").getValue());
                difficulty.setMin(1);
                difficulty.setMax(5);
                difficulty.setProgress(tDifficulty);


                for(DataSnapshot ex : da.child("exercises").getChildren()){
                    exerciseList.add((String)ex.getValue());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ((exerciseAdapterClass) exerciseAdapter).update(exerciseList);
        exerciseRecyclerView.setAdapter(exerciseAdapter);
        exerciseAdapter.notifyDataSetChanged();

        //***************************************************************************************************************************************\\
        //This should be grabbing all the challenges that the athlete is currently subscribed to.
        databaseReferenceAthlete.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(uid).child("currChallenges");
                HashMap<String, String> currChallengesT = (HashMap<String, String>) dataSnapshot1.getValue();
                currChallenges.clear();
                if(currChallengesT != null) {
                    currChallenges.putAll(currChallengesT);
                    if(currChallenges.containsKey(cName)) {
                        bSubscribe.setVisibility(View.GONE);
                        bDropout.setVisibility(View.VISIBLE);
                        exerciseRecyclerView.setVisibility(View.VISIBLE);
                        exerciseTitle.setVisibility(View.VISIBLE);
                    }
                    else{
                        bSubscribe.setVisibility(View.VISIBLE);
                        bDropout.setVisibility(View.GONE);
                        exerciseRecyclerView.setVisibility(View.GONE);
                        exerciseTitle.setVisibility(View.GONE);
                    }
                }
                else{
                    bSubscribe.setVisibility(View.VISIBLE);
                    bDropout.setVisibility(View.GONE);
                    exerciseRecyclerView.setVisibility(View.VISIBLE);
                    exerciseTitle.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        //***************************************************************************************************************************************\\

        //***************************************************************************************************************************************\\
        //This should be grabbing all the athletes subscribed to the challenge.
        databaseReferenceChallenge.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

        databaseReferenceAthlete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(uid).child("currChallenges").child(cName).child("team");
                team = (String) dataSnapshot1.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                final String uid = user.getUid();

                HashMap<String, Integer> athData = new HashMap<>();
                HashMap<String, Integer> chaData = new HashMap<>();

                for(DataSnapshot data : dataSnapshot.child("Athlete Users").child(uid).child("currChallenges").child(cName).getChildren()){
                    if(!data.getKey().equals("team")){
                        athData.put(data.getKey(), Integer.parseInt(data.getValue().toString()));
                    }
                }

                for(DataSnapshot data : dataSnapshot.child("Challenges").child(cName).child("exercises").getChildren()){
                    chaData.putAll(getAmount(data.getValue().toString()));
                }

                System.out.println(athData.toString());
                System.out.println(chaData.toString());

                int curPoints = 0;

                String[] athKey = athData.keySet().toArray(new String[athData.size()]);
                String[] chaKey = chaData.keySet().toArray(new String[chaData.size()]);

                for(int i = 0; i < chaData.size(); i ++){
                    for(int j = 0; j < athData.size(); j ++){
                        if(chaKey[i].equals(athKey[j])){
                            if(athData.get(athKey[j]) >= chaData.get(chaKey[i])){
                                curPoints += 10;
                            }
                        }
                    }
                }

                if(Integer.parseInt(dataSnapshot.child("Challenges").child(cName).child("totalpoints").getValue().toString()) == curPoints){
                    revealGoldTrophy();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static Context getAppContext() {
        return ChallengeView.context;
    }

    /*
    Calls for the transfer to the challenge signup activity
     */
    public void Subscribe(View view){
        Intent intent = new Intent (this, ChallengeSignUp.class);
        intent.putExtra("name", cName);
        startActivity(intent);

    }

    /*
    Removes the challenge from the athlete's list.
    Removes the athlete from the challenge's list.
    Removes the athlete from the team they were on.
     */
    public void DropOut(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        final String uid = user.getUid();

        teamMem.remove(uid);
        if(teamMem.isEmpty()){
            teamMem.add("null");
        }

        databaseReferenceChallenge.child(cName).child("teams").child(team).setValue(teamMem);

        databaseReferenceChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child(cName).child("teams").child(team);
                ArrayList<String> teamMemT = (ArrayList<String>) dataSnapshot1.getValue();
                if(teamMemT != null) {
                    teamMem.addAll(teamMemT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        currChallenges.remove(cName);
        databaseReferenceAthlete.child(uid).child("currChallenges").setValue(currChallenges);

        subAthletes.remove(uid);
        databaseReferenceChallenge.child(cName).child("subscribedAthletes").setValue(subAthletes);

        bSubscribe.setVisibility(View.VISIBLE);
        bDropout.setVisibility(View.GONE);
        Toast.makeText(this, "Unsubscribed from challenge.", Toast.LENGTH_SHORT).show();
    }

    /**  ///////////////////////////////////////////////////////////////////////////////////////////
     *   This function reveals an image asset preloaded into an ImageView object, and slides the
     *       image up from the bottom of the phone screen. The image is removed after a handled delay
     *       set in milliseconds.
     * param view
     * *///////////////////////////////////////////////////////////////////////////////////////////
    public void revealGoldTrophy(/*View view*/) {
        //Load Animation .xml from java/res/anim
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);

        //Make the ImageView visible and start the animation on the ImageView
        goldTrophy.setVisibility(View.VISIBLE);
        goldTrophy.startAnimation(slideUpAnimation);

        //Create a delayed operation handler
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Set the ImageView back to invisible
                goldTrophy.setVisibility(View.INVISIBLE);
            }
        }, 8000);


    }

    /*
    Back button functionality
     */
    public void Back(View view){
        Intent intent = new Intent (this, AthleteMain.class);
        startActivity(intent);
        finish();
    }

    private HashMap<String, Integer> getAmount(String in){
        String s1 = in;

        //Possible error if the name contains "Amount"
        final String name = s1.substring(0, s1.indexOf("Amount")-1); //first section of the string to give the name.

        String s2 = s1.substring(s1.indexOf(": ")); //cuts of the front of the string and puts to a new substring.

        String a = s2.substring(s2.indexOf(' '), s2.indexOf("\n")); //cuts the part after the : but before the \n
        a = a.trim(); //trims the inevitable white space
        final String units = a.substring(a.indexOf(' ')); //gets the units off the string
        a = a.substring(0, a.indexOf(' ')); //cuts off the units
        final int amount = Integer.parseInt(a); //parse to use as a number

        String s3 = s2.substring(s2.indexOf("\n")); //cuts to the date and new string
        s3 = s3.substring(8); //removes the date tag

        String y = s3.substring(0, s3.indexOf('/')); //cuts year into new string
        final int year = Integer.parseInt(y); //parse year to use as a number

        s3 = s3.substring(s3.indexOf('/') + 1); //cuts year off

        String d = s3.substring(0, s3.indexOf('/')); //cuts day into new string
        final int day = Integer.parseInt(d); //parse day

        s3 = s3.substring(s3.indexOf('/') + 1); //cuts day off
        final int month = Integer.parseInt(s3); //parses month

        HashMap<String, Integer> ret = new HashMap<>();
        ret.put(name, amount);

        return ret;
    }
}
