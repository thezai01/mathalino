package com.example.math_game;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import android.window.SplashScreen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText loginUsername, loginPass;

    boolean passMatch, userMatch;

    Handler handle;
    Dialog errorDialog, confirmDialog;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tableAcc (username TEXT PRIMARY KEY,"+
                "uPass TEXT, uName TEXT, uBday TEXT, uAge INTEGER, uGender TEXT);");

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
                welcome(R.layout.activity_success,"Login Successfully. Welcome "+loginUsername.getText().toString()+"!","LOGIN");

            }
        }

        if(loginUsername.getText().toString().isEmpty() || loginPass.getText().toString().isEmpty()) errorMsg(R.layout.activity_error_msg,"Please ensure that you provide the required information.");
        else if(!userMatch) errorMsg(R.layout.activity_error_msg,"Entered username doesn't exist.");
        else if(userMatch && passMatch==false) errorMsg(R.layout.activity_error_msg,"Entered password is incorrect. Please try again.");

    }

    // error message
    public void errorMsg(int view, String text){
        ImageView exitIcon;
        Button exit_btn;

        errorDialog.setContentView(view);
        exitIcon = errorDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.error), PorterDuff.Mode.SRC_IN);
            errorDialog.dismiss();
        });

        TextView changeText =  errorDialog.findViewById(R.id.success_msg);
        changeText.setText(text);

        exit_btn =  errorDialog.findViewById(R.id.success_ok);
        exit_btn.setOnClickListener(v -> errorDialog.dismiss());

        errorDialog.show();
    }

    // welcome message
    @SuppressLint("StringFormatInvalid")
    public void welcome(int view, String text, String title){
        ImageView exitIcon;
        Button exit_btn;

        confirmDialog.setContentView(view);
        exitIcon = confirmDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.error), PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            intent.putExtra("username",loginUsername.getText().toString());
            confirmDialog.dismiss();
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });

        TextView changeTitle =  confirmDialog.findViewById(R.id.success_title);
        changeTitle.setText(title);

        TextView changeText =  confirmDialog.findViewById(R.id.success_msg);
        changeText.setText(text);

        exit_btn = confirmDialog.findViewById(R.id.success_ok);
        exit_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            intent.putExtra("username",loginUsername.getText().toString());
            startActivity(intent);
            confirmDialog.dismiss();
            handle.postDelayed(this::finish, 0);
        });

        confirmDialog.show();

    }


}