package com.cmsc355.forfit.custObjects;

import java.util.ArrayList;

public class Team {

    ArrayList<AthleteUser> athletes;
    int totalpoints;
    int currentpoints;
    int teamsize;


    public Team(){}
    public Team(ArrayList<AthleteUser> atheltes, int totalpoints ){
        this.athletes = atheltes;
        this.totalpoints = totalpoints;
        this.currentpoints = 0;
        this.teamsize = atheltes.size();

    }

    public ArrayList<AthleteUser> getAthletes() {
        return athletes;
    }

    public void setAthletes(ArrayList<AthleteUser> athletes) {
        this.athletes = athletes;
    }

    public int getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(int totalpoints) {
        this.totalpoints = totalpoints;
    }

    public int getCurrentpoints() {
        return currentpoints;
    }

    public void setCurrentpoints(int currentpoints) {
        this.currentpoints = currentpoints;
    }

    public int getTeamsize() {
        return teamsize;
    }

    public void setTeamsize(int teamsize) {
        this.teamsize = teamsize;
    }
}
