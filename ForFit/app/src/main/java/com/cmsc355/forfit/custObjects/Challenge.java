package com.cmsc355.forfit.custObjects;

import java.util.ArrayList;

public class Challenge {
    private String name, description, id, coachid, MinTeamsize,MaxTeamsize;
    private Date StartDate, EndDate;
    private float difficulty;
    private ArrayList exercises;
    private ArrayList<Team> leaderboard;
    private ArrayList<Team> registerdTeams;
    private int totalpoints, currentpoints;
    private Team team;


    public Challenge(){}

    public Challenge(String nam, String des, Date start, Date end, float diff, ArrayList exc,String coachid,String MinT,String MaxT,int totalpoints) {
        //this.id = id;
        this.name = nam;
        this.description = des;
        this.StartDate= start;
        this.EndDate= end;
        this.difficulty = diff;
        this.exercises = exc;
        this.coachid = coachid;
        this.MinTeamsize=MinT;
        this.MaxTeamsize=MaxT;
        this.totalpoints=totalpoints;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoachid() {
        return coachid;
    }

    public void setCoachid(String coachid) {
        this.coachid = coachid;
    }

    public String getMinTeamsize() {
        return MinTeamsize;
    }

    public void setMinTeamsize(String minTeamsize) {
        MinTeamsize = minTeamsize;
    }

    public String getMaxTeamsize() {
        return MaxTeamsize;
    }

    public void setMaxTeamsize(String maxTeamsize) {
        MaxTeamsize = maxTeamsize;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList exercises) {
        this.exercises = exercises;
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

    public ArrayList<Team> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(ArrayList<Team> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public ArrayList<Team> getRegisterdTeams() {
        return registerdTeams;
    }

    public void setRegisterdTeams(ArrayList<Team> registerdTeams) {
        this.registerdTeams = registerdTeams;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
