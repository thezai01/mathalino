package com.example.math_game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    EditText loginUsername, loginPass;

    boolean passMatch, userMatch;
    String gender;

    Handler handle;
    Dialog errorDialog, confirmDialog;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //FOR DEBUGGING ONLY
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS tableAcc (username TEXT PRIMARY KEY,"+
                "uPass TEXT, uName TEXT, uBday TEXT, uAge INTEGER, uGender TEXT, uExp INTEGER);");

        handle = new Handler();
        errorDialog = new Dialog(this);
        confirmDialog = new Dialog(this);

        loginUsername = findViewById(R.id.loginUsername);
        loginPass = findViewById(R.id.loginPassword);

        Button signUp = findViewById(R.id.loginSignUp);
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        } );

       CheckBox loginShowpass= findViewById(R.id.loginShowPass);
        loginShowpass.setOnClickListener(v -> {
            if(loginShowpass.isChecked()) loginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else loginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        });

        Button login = findViewById(R.id.loginLogin);
        login.setOnClickListener(v -> database());

    }

    private void database() {

        // DATABASES
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + loginUsername.getText().toString() + "'", null);

        passMatch = false;
        userMatch = false;

        if(!(cursor.getCount()==0)){
            cursor.moveToFirst();
            userMatch = true;
            if (loginPass.getText().toString().equals(cursor.getString(1))) {
               passMatch = true;
               gender = cursor.getString(5);
                CommonUtils.welcome(confirmDialog, this, R.layout.activity_success,"Login Successfully.\nWelcome "+loginUsername.getText().toString()+"!","LOGIN", result -> {
                    if(result == 1) startIntent();
                });
            }
        }

        if(loginUsername.getText().toString().isEmpty() || loginPass.getText().toString().isEmpty()) CommonUtils.errorMsg(errorDialog,this, R.layout.activity_error_msg,"Please ensure that you provide the required information.");
        else if(!userMatch) CommonUtils.errorMsg(errorDialog,this,R.layout.activity_error_msg,"Entered username doesn't exist.");
        else if(!passMatch) CommonUtils.errorMsg(errorDialog,this,R.layout.activity_error_msg,"Entered password is incorrect. Please try again.");

        cursor.close();
    }

    public void startIntent(){
        Intent intent = new Intent(MainActivity.this, Navigation.class);
        SharedPreferences sharedPref = getSharedPreferences("mathalino", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", loginUsername.getText().toString());
        editor.putString("gender", gender);
        editor.apply();
        startActivity(intent);
        confirmDialog.dismiss();
        handle.postDelayed(this::finish, 0);
    }



}