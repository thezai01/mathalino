package com.example.math_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    String username="";

    int intentCheck, lvl, score, scorelvl1, scorelvl2, scorelvl3;

    CardView lvl1, lvl2, lvl3;

    Intent intent;
    Handler handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        handle = new Handler();

        intent = getIntent();
        username = intent.getStringExtra("username");
        intentCheck = intent.getIntExtra("intent",0);

        SQLiteDatabase db = openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM userScore WHERE username ='" + username + "'", null);

        if(intentCheck == 1) {
            lvl = intent.getIntExtra("lvl", 0);
            score = intent.getIntExtra("score", 0);

            switch (lvl) {
                case 1 -> scorelvl1 = score;
                case 2 -> scorelvl2 = score;
                case 3 -> scorelvl3 = score;
            }

            if (cursor.moveToFirst()) {

                if (!(scorelvl1 > cursor.getInt(1))) scorelvl1 = cursor.getInt(1);
                if (!(scorelvl2 > cursor.getInt(2))) scorelvl2 = cursor.getInt(2);
                if (!(scorelvl3 > cursor.getInt(3))) scorelvl3 = cursor.getInt(3);

                db.execSQL("UPDATE userScore SET scorelvl1='" + scorelvl1 + "', scorelvl2='" + scorelvl2 + "', scorelvl3='" + scorelvl3 + "' WHERE username = '" + username + "'");
            }
        }else{
            cursor.moveToFirst();
            scorelvl1 = cursor.getInt(1) ;
            scorelvl2 = cursor.getInt(2);
            scorelvl3 =  cursor.getInt(3);
        }

        TextView hpUser = findViewById(R.id.homepageUser);
        hpUser.setText(username);

        lvl1 = findViewById(R.id.cardViewLvl1);
        lvl1.setOnClickListener(v ->{
            intent = new Intent(HomePage.this, GameActivity.class);
            intent.putExtra("lvl",1);
            intent.putExtra("username",username);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });

        lvl2 = findViewById(R.id.cardViewLvl2);
        lvl2.setOnClickListener(v ->{
            intent = new Intent(HomePage.this, GameActivity.class);
            intent.putExtra("lvl",2);
            intent.putExtra("username",username);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });

        lvl3 = findViewById(R.id.cardViewLvl3);
        lvl3.setOnClickListener(v ->{
            intent = new Intent(HomePage.this, GameActivity.class);
            intent.putExtra("lvl",3);
            intent.putExtra("username",username);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });

    }
}