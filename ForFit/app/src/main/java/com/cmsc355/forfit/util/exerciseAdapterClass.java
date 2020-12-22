package com.cmsc355.forfit.util;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cmsc355.forfit.ChallengeView;
import com.cmsc355.forfit.ExerciseView;
import com.cmsc355.forfit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class exerciseAdapterClass extends RecyclerView.Adapter<exerciseAdapterClass.exerciseViewHolder>{

    ArrayList<String> exerciseList;

    public static class exerciseViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView exerciseName;
        public TextView amount;
        //public EditText inputAmount;
        public Button bEnter;

        int currDone;

        public exerciseViewHolder(View itemView) {
            super(itemView);

            //Variables
            currDone = 0;

            //Authentication
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            final String uid = user.getUid();



            //XML
            cardView = itemView.findViewById(R.id.cv_cardView);
            exerciseName = itemView.findViewById(R.id.cv_exerciseName);
            amount = itemView.findViewById(R.id.cv_amount);
            //inputAmount = itemView.findViewById(R.id.cv_input);
            bEnter = itemView.findViewById(R.id.cv_button);

            bEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ChallengeView.getAppContext(), ExerciseView.class);
                    intent.putExtra("cName", ChallengeView.intent.getStringExtra("name"));
                    intent.putExtra("exercise", exerciseName.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ChallengeView.getAppContext().startActivity(intent);

                }
            });
        }
    }

    public exerciseAdapterClass(ArrayList<String> inputList){
        exerciseList = inputList;
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    @Override
    public exerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_card, viewGroup, false);
        exerciseViewHolder pvh = new exerciseViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final exerciseViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        String s1 = exerciseList.get(i);

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


        holder.amount.setText(year + "/" + month + "/" + day);
        holder.exerciseName.setText(name);
    }

    public void update(ArrayList<String> exerciseList){
        this.exerciseList = exerciseList;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
