package com.example.math_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_DELAY = 3000; // Delay in milliseconds (e.g., 3000 = 3 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity or any other activity
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: close the splash activity
            }
        }, SPLASH_DELAY);
    }
}
