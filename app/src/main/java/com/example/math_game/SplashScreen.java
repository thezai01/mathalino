package com.example.math_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler;
    private int progressStatus = 0;

    private String[] names = {"Caber, Jexter Jhon", "De Guzman, Chen Abriel", "Filosopo, Ervin Joel", "Laylay, Jericho Mark", "Soriano, Hannon Noriele"};
    private String[] roles = {"Assist. Programmer", "UI Designer", "Main Programmer", "UX Designer", "Assist. Programmer"};
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        TextView name = findViewById(R.id.textName);
        TextView role = findViewById(R.id.textRole);

        handler = new Handler(Looper.getMainLooper());
        SoundManager.playSound(SplashScreen.this, R.raw.welcs);
        // Create a new thread for the progress bar
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    if (progressStatus >= counter * 20 && progressStatus < (counter + 1) * 20) {
                        final String currentName = names[counter];
                        final String currentRole = roles[counter];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(currentName);
                                role.setText(currentRole);
                            }
                        });
                    }

                    progressStatus += 1;

                    // Update the progress bar on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        // Delay the thread by 100 milliseconds (adjust as needed)
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (progressStatus % 20 == 0) {
                        counter++;
                    }
                }
            }
        });



        // Start the thread
        thread.start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity or any other activity
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: close the splash activity
            }
        }, 10000);
    }
}