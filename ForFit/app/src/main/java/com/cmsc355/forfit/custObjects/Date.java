package com.cmsc355.forfit.custObjects;

public class Date {

    int year, month, day;

    public Date(){}
    public Date(int year, int month, int date){
        this.year = year;
        this.month = month;
        this.day = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
