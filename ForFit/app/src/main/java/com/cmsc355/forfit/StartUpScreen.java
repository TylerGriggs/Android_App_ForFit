package com.cmsc355.forfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.VideoView;

public class StartUpScreen extends AppCompatActivity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade());
        } else {
        }


        video  = findViewById(R.id.logoVideo);
        Uri uri = Uri.parse("android.resource://" + getPackageName()+"/"+R.raw.forfitsphere5);
        video.setVideoURI(uri);
        video.start();


        new CountDownTimer(3000, 3000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){

                Intent intent = new Intent (StartUpScreen.this, Login.class);
                startActivity(intent);
                //finish();
            }
        }.start();
    }
}
