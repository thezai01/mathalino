package com.example.math_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    String username="";

    int intentCheck, lvl, score, scorelvl1, scorelvl2, scorelvl3;

    CardView lvl1, lvl2, lvl3;
    TextView lvl1tx, lvl2tx, lvl3tx;

    Intent intent;
    Handler handle;
    Dialog errorDialog, confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        errorDialog = new Dialog(this);
        confirmDialog = new Dialog(this);
        handle = new Handler();

        intent = getIntent();
        username = intent.getStringExtra("username");
        intentCheck = intent.getIntExtra("intent",0);

        lvl1 = findViewById(R.id.cardViewLvl1);
        lvl2 = findViewById(R.id.cardViewLvl2);
        lvl3 = findViewById(R.id.cardViewLvl3);

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

        lvl1tx = findViewById(R.id.lvl1HighScore);
        lvl2tx = findViewById(R.id.lvl2HighScore);
        lvl3tx = findViewById(R.id.lvl3HighScore);
        lvl1tx.setText(String.valueOf(scorelvl1));
        lvl2tx.setText(String.valueOf(scorelvl2));
        lvl3tx.setText(String.valueOf(scorelvl3));

        ImageView locked2 = findViewById(R.id.lvl2Locked);
        ImageView locked3 = findViewById(R.id.lvl3Locked);

        if(scorelvl1 >= 60){
            lvl2.setCardBackgroundColor(Color.WHITE);
            locked2.setVisibility(View.INVISIBLE);
        }
        if(scorelvl2 >= 60){
            lvl3.setCardBackgroundColor(Color.WHITE);
            locked3.setVisibility(View.INVISIBLE);
        }

        TextView hpUser = findViewById(R.id.homepageUser);
        hpUser.setText(username);

        lvl1.setOnClickListener(v ->{
            intent = new Intent(HomePage.this, GameActivity.class);
            intent.putExtra("lvl",1);
            intent.putExtra("username",username);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });

        lvl2.setOnClickListener(v ->{
            if(scorelvl1>=60) {
                intent = new Intent(HomePage.this, GameActivity.class);
                intent.putExtra("lvl", 2);
                intent.putExtra("username", username);
                startActivity(intent);
                handle.postDelayed(this::finish, 0);
            }else{
                errorMsg(R.layout.activity_error_msg,"You need to have at least 60 points from the previous level to unlock this.");
            }
        });

        lvl3.setOnClickListener(v ->{
            if(scorelvl2>=60) {
                intent = new Intent(HomePage.this, GameActivity.class);
                intent.putExtra("lvl", 3);
                intent.putExtra("username", username);
                startActivity(intent);
                handle.postDelayed(this::finish, 0);
            }else{
                errorMsg(R.layout.activity_error_msg,"You need to have at least 60 points from the previous level to unlock this.");
            }
        });

        ImageView logout = findViewById(R.id.homepageLogout);
        logout.setOnClickListener(v ->{
            showConfirm(R.layout.activity_confirm_msg, "Logout","Are you sure about that?");
        });


    }

    // error message
    public void errorMsg(int view, String text){
        ImageView exitIcon;
        Button exit_btn;

        errorDialog.setContentView(view);
        exitIcon = errorDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(HomePage.this , R.color.error), PorterDuff.Mode.SRC_IN);
            errorDialog.dismiss();
        });

        TextView changeText1 =  errorDialog.findViewById(R.id.success_title);
        changeText1.setText("Unlock Level");

        TextView changeText =  errorDialog.findViewById(R.id.success_msg);
        changeText.setText(text);

        exit_btn =  errorDialog.findViewById(R.id.success_ok);
        exit_btn.setOnClickListener(v -> errorDialog.dismiss());

        errorDialog.show();
    }

    // confirm message
    public void showConfirm(int view, String title, String msg){
        ImageView exitIcon;
        Button cancel_btn, confirm_btn;
        TextView changeTitle, changeMsg;

        confirmDialog.setContentView(view);
        exitIcon = confirmDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(HomePage.this, R.color.error), PorterDuff.Mode.SRC_IN);
            confirmDialog.dismiss();
        });

        changeTitle = confirmDialog.findViewById(R.id.success_title);
        changeTitle.setText(title);

        changeMsg = confirmDialog.findViewById(R.id.success_msg);
        changeMsg.setText(msg);

        confirm_btn = confirmDialog.findViewById(R.id.dialog_cancel);
        confirm_btn.setOnClickListener(v -> confirmDialog.dismiss());

        confirm_btn = confirmDialog.findViewById(R.id.success_ok);
        confirm_btn.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            startActivity(intent);
            confirmDialog.dismiss();
            handle.postDelayed(this::finish, 0);
        });

        confirmDialog.show();
    }

}