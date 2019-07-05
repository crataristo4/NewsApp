package com.hard.code.tech.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hard.code.tech.newsapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//Removes anything relating to a title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Splash Screen activity displays in a full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //creates an instance of the ShowSplash class
        ShowSplash showSplash = new ShowSplash();
        //Calls the start method in the Thread class and waits for two seconds
        showSplash.start();
    }


    //An inner class that blocks the UI and causes threading
    private class ShowSplash extends Thread {
        @Override
        public void run() {
            try {
                sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Opens the Welcome Screen Activity once the time elapses
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        }
    }
}

