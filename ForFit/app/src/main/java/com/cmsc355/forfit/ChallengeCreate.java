package com.cmsc355.forfit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cmsc355.forfit.custObjects.Challenge;
import com.cmsc355.forfit.custObjects.Date;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class ChallengeCreate extends AppCompatActivity {


    private ArrayList<String> exerciseList;
    private ArrayAdapter<String> adapter;

    private EditText exercise;
    private EditText name;
    private EditText description;
    private EditText TeamMin;
    private EditText TeamMax;
    private EditText ExerciseUnits;
    private EditText exerciseamount;

    private TextView duration;
    private TextView tv_teammin;
    private TextView tv_teammax;
    private TextView viewexercisedate;
    private TextView calenderstart;
    private TextView calenderend;


    private Button addButton;
    private Button createButton;
    private Button CalStart;
    private Button Calend;
    private Button exerciseDate;


    private CheckBox individual;
    private CheckBox team;


    private RatingBar difficultyrating;
    private ListView lv;

    private Calendar c;
    private Calendar c2;
    private Calendar c3;
    private DatePickerDialog datepick;
    private DatePickerDialog datepick2;
    private DatePickerDialog datepick3;

    private Date startDate;
    private Date end;

    private int totalPoints;
    private Date exerdate;

    private FirebaseAuth Auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_create);
        FirebaseApp.initializeApp(this);

        // list view
        lv = findViewById(R.id.C_exerciselist);

        //Edit Text
        exercise = findViewById(R.id.C_exercise);
        exerciseamount = findViewById(R.id.C_exerciseamount);
        name = findViewById(R.id.C_name);
        description = findViewById(R.id.C_description);
        TeamMax = findViewById(R.id.C_maxteam);
        TeamMin = findViewById(R.id.c_Minteam);
        ExerciseUnits = findViewById(R.id.C_exerciseUnit);

        //Text View
        tv_teammin = findViewById(R.id.ct_teammin);
        tv_teammax = findViewById(R.id.ct_teammax);
        calenderstart = findViewById(R.id.Ct_StartDate);
        calenderend = findViewById(R.id.Ct_EndDate);
        viewexercisedate = findViewById(R.id.C_exercisedate);


        // Buttons
        addButton = findViewById(R.id.Cb_add);
        createButton = findViewById(R.id.Cb_create);
        CalStart = findViewById(R.id.C_StartDateButt);
        Calend = findViewById(R.id.C_EndDateButt);
        exerciseDate = findViewById(R.id.C_exerciseDate);

        //rating bar
        difficultyrating = (RatingBar) findViewById(R.id.C_diffbar);

        //Global Date
        startDate = new Date();
        end = new Date();

        //Checkboxes
        individual = findViewById(R.id.C_individual);
        team = findViewById(R.id.C_team);

        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Challenges");
        duration = findViewById(R.id.Ct_duration);
        Auth = FirebaseAuth.getInstance();


        String exDate;


        ////////////////////////////////////////////////////////////////////////////////////////////
        // Determining if team or individual. Check Boxes
        ////////////////////////////////////////////////////////////////////////////////////////////


        tv_teammax.setVisibility(View.GONE);
        tv_teammin.setVisibility(View.GONE);
        TeamMin.setVisibility(View.GONE);
        TeamMax.setVisibility(View.GONE);

        individual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    team.setChecked(false);
                    tv_teammax.setVisibility(View.GONE);
                    tv_teammin.setVisibility(View.GONE);
                    TeamMin.setVisibility(View.GONE);
                    TeamMax.setVisibility(View.GONE);
                    TeamMax.setText("1");
                    TeamMin.setText("1");

                }
            }
        });
        team.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    individual.setChecked(false);
                    TeamMax.setText("");
                    TeamMin.setText("");
                    tv_teammax.setVisibility(View.VISIBLE);
                    tv_teammin.setVisibility(View.VISIBLE);
                    TeamMin.setVisibility(View.VISIBLE);
                    TeamMax.setVisibility(View.VISIBLE);

                }
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Date Start button
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        CalStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                datepick = new DatePickerDialog(ChallengeCreate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        Date selecteddate = new Date(Year, Month, Day);
                        c.set(Year, Month, Day);
                        if (c.after(c2)) {
                            Toast.makeText(ChallengeCreate.this, "The Date Selected can not be after the end date.", Toast.LENGTH_SHORT).show();
                            CalStart.setError("The Date Selected can not be after the end date.");
                            CalStart.requestFocus();
                            calenderstart.setText("");
                            return;
                        } else {
                            CalStart.setError(null);
                            CalStart.clearFocus();
                            calenderstart.setText(Year + "/" + (Month + 1) + "/" + Day);
                            startDate = selecteddate;
                        }

                    }

                }, year, month, day);
                datepick.show();
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Date End button
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        Calend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c2 = Calendar.getInstance();
                int day = c2.get(Calendar.DAY_OF_MONTH);
                int month = c2.get(Calendar.MONTH);
                int year = c2.get(Calendar.YEAR);

                datepick2 = new DatePickerDialog(ChallengeCreate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        Date selecteddate = new Date(Year, Month, Day);
                        c2.set(Year, Month, Day);
                        if (c2.before(c)) {
                            Toast.makeText(ChallengeCreate.this, "The Date Selected can not be before the start date.", Toast.LENGTH_SHORT).show();
                            Calend.setError("The Date Selected can not be before the start date.");
                            Calend.requestFocus();
                            calenderend.setText("");
                            return;
                        } else {
                            Calend.setError(null);
                            Calend.clearFocus();
                            calenderend.setText(Year + "/" + (Month + 1) + "/" + Day);
                            end = selecteddate;
                        }


                    }

                }, year, month, day);
                datepick2.show();
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Exercise Date button
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        exerciseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StartDate = calenderstart.getText().toString();
                String EndDate = calenderend.getText().toString();

                if (StartDate.isEmpty()) {
                    Toast.makeText(ChallengeCreate.this, "Please Enter a Start Date.", Toast.LENGTH_SHORT).show();
                    CalStart.setError("Start Date Needed.");
                    CalStart.requestFocus();
                    return;
                }
                if (EndDate.isEmpty()) {
                    Toast.makeText(ChallengeCreate.this, "Please Enter a End Date.", Toast.LENGTH_SHORT).show();
                    Calend.setError("End Date Needed.");
                    Calend.requestFocus();
                    return;
                }

                c3 = Calendar.getInstance();
                int day = c3.get(Calendar.DAY_OF_MONTH);
                int month = c3.get(Calendar.MONTH);
                int year = c3.get(Calendar.YEAR);

                datepick3 = new DatePickerDialog(ChallengeCreate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int Year, int Month, int Day) {
                        Date selecteddate = new Date(Year, Month, Day);
                        c3.set(Year, Month, Day);
                        if (c3.before(c) || c3.after(c2)) {
                            Toast.makeText(ChallengeCreate.this, "The Date Selected needs to be within the challenge dates", Toast.LENGTH_SHORT).show();
                            exerciseDate.setError("The Date out of bounds.");
                            exerciseDate.requestFocus();
                            viewexercisedate.setText("");
                            return;
                        } else {
                            exerciseDate.setError(null);
                            exerciseDate.clearFocus();
                            viewexercisedate.setText(Year + "/" + (Month + 1) + "/" + Day);
                            exerdate = selecteddate;
                        }

                    }

                }, year, month, day);
                datepick3.show();
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Exercise list
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        exerciseList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(ChallengeCreate.this, android.R.layout.simple_list_item_multiple_choice, exerciseList);

        View.OnClickListener addlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Exe = exercise.getText().toString();
                String Amount = exerciseamount.getText().toString();
                String ExeUnits = ExerciseUnits.getText().toString();
                String ExeDate = viewexercisedate.getText().toString();


                if (Exe.isEmpty()) {
                    exercise.setError("Exercise Needed.");
                    exercise.requestFocus();
                    return;
                }
                if (Amount.isEmpty()) {
                    exerciseamount.setError("Exercise Amount Needed.");
                    exerciseamount.requestFocus();
                    return;
                }
                if (ExeUnits.isEmpty()) {
                    ExerciseUnits.setError("Exercise Units Needed.");
                    ExerciseUnits.requestFocus();
                    return;
                }
                if (ExeDate.isEmpty()) {
                    Toast.makeText(ChallengeCreate.this, "The Date for this Exercise Needs to be selected.", Toast.LENGTH_SHORT).show();
                    exerciseDate.setError("No exercise Date.");
                    exerciseDate.requestFocus();
                    return;
                }


                exerciseList.add(Exe + " Amount: " + Amount + " " + ExeUnits + "\nDate: " + ExeDate);
                totalPoints =totalPoints + 10;
                viewexercisedate.setText("");
                ExerciseUnits.setText("");
                exercise.setText("");
                exerciseamount.setText("");
                adapter.notifyDataSetChanged();
            }
        };


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();
                int count = lv.getCount();
                for (int item = count - 1; item >= 0; item--) {
                    if (positionchecker.get(item)) {
                        adapter.remove(exerciseList.get(item));
                        totalPoints =totalPoints -10 ;
                        Toast.makeText(ChallengeCreate.this, "Exercise Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                positionchecker.clear();
                adapter.notifyDataSetChanged();

                return false;
            }
        });


        addButton.setOnClickListener(addlistener);
        lv.setAdapter(adapter);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Create challenge button when clicked.
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String N = name.getText().toString().trim();
                String des = description.getText().toString().trim();
                String startD = calenderstart.getText().toString().trim();
                String endD = calenderend.getText().toString().trim();

                // checks for empty inputs
                if (startD.isEmpty()) {
                    calenderstart.setError("Start Date Required");
                    calenderstart.requestFocus();
                    return;
                }
                if (endD.isEmpty()) {
                    calenderend.setError("End Date Required");
                    calenderend.requestFocus();
                    return;
                }
                if (N.isEmpty()) {
                    name.setError("Challenge Name Required");
                    name.requestFocus();
                    return;
                }
                if (des.isEmpty()) {
                    description.setError("Challenge Description");
                    description.requestFocus();
                    return;
                }




                // Alert box that makes sure you want to create a challenge.
               AlertDialog.Builder builder = new AlertDialog.Builder(ChallengeCreate.this);
                builder.setMessage("Do you Wish to Create this Challenge?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            addChallenges();
                            startActivity(new Intent(ChallengeCreate.this, CoachMain.class));
                            finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                builder.setTitle("Challenge Create");
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Creating challenge objects and sending them to the database
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void addChallenges() {

        String title = name.getText().toString(); // name of challenge
        String des = description.getText().toString(); // description of challenge.
        float diff = difficultyrating.getRating(); // difficulty rating
        String Minteam = TeamMin.getText().toString();
        String MaxTeam = TeamMax.getText().toString();
        FirebaseUser user = Auth.getCurrentUser();
        String currentuserID = user.getUid();



        Challenge challenge = new Challenge(title, des, startDate, end, diff, exerciseList, currentuserID, Minteam, MaxTeam,totalPoints); // challenge constructor.

        databaseReference.child(title).setValue(challenge);


       /* name.setText("");
        description.setText("");
        duration.setText("");
        exerciseamount.setText("");
        difficultyrating.setRating(0);
        adapter.clear();
        calenderstart.setText("");
        calenderend.setText("");*/

        Toast.makeText(ChallengeCreate.this, "Challenge Created Successfully", Toast.LENGTH_SHORT).show();

    }

    public void buttonHome(View view) {
        Intent intent = new Intent(this, CoachMain.class);
        startActivity(intent);

    }

    public void buttonAthleteSearch(View view){
        Intent intent = new Intent (this, AthleteSearch.class);
        startActivity(intent);
    }

}
