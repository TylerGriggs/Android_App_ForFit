package com.cmsc355.forfit.custObjects;

import java.util.ArrayList;

public class AthleteUser {

    public String name, email, phone, bio, id;
    PersonalInfo profile;
    ArrayList<String> announcements;
    ArrayList<String> friendsList;
    ArrayList<Challenge> currChallenges;
    ArrayList<Challenge> failedChallenges;
    ArrayList<Challenge> completedChallenges;

    public AthleteUser(){}

    public AthleteUser(String name, String email, String phone, String id, PersonalInfo profile, ArrayList<String> welcome){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.profile = profile;
        this.announcements = welcome;

    }

    public void addChallenge(Challenge challenge){
        currChallenges.add(challenge);
    }

    /**
     * Moves an expired challenge to either the failed or completed challenge arraylist. This is dependent on the failed boolean.
     * <p>
     * Returns true if the challenge moved. Returns false if the challenge was not contained in the current challenge list.
     * @param challenge
     * @param failed
     * @return
     */
    public boolean expireChallenge(Challenge challenge, boolean failed){
        if(currChallenges.contains(challenge)){
            currChallenges.remove(challenge);
            if(failed){
                failedChallenges.add(challenge);
            }
            else{
                completedChallenges.add(challenge);
            }
            return true;
        }
        else{
            return false;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
