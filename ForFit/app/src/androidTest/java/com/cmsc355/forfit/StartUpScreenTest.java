package com.cmsc355.forfit;



import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class StartUpScreenTest {

    @Rule
    public ActivityTestRule<StartUpScreen> mActivityTestRule = new ActivityTestRule<>(StartUpScreen.class);

    @Test
    public void startUpScreenTest() {
    }



    
}
