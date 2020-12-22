package com.cmsc355.forfit.util;

import java.util.ArrayList;

public class UtilMethods {

    public static String cleanString(String input){
        input.trim();
        ArrayList<String> temp = new ArrayList<>();
        for (char c : input.toCharArray()){
            temp.add("" + c);
        }
        temp.remove("\n");
        String output = "";
        for(String s : temp){
            output += s;
        }
        return output;
    }


}
