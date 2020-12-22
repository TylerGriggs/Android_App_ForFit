package com.cmsc355.forfit.custObjects;

import android.support.v7.app.AppCompatActivity;

public class PersonalInfo {

    private String Gender, Age, Height, Weight, Bio;


    public PersonalInfo(){}

    public PersonalInfo(String Gender, String Age, String Height, String Weight, String Bio){

        this.Gender = Gender;
        this.Age = Age;
        this.Height = Height;
        this.Weight = Weight;
        this.Bio = Bio;


    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }
}
