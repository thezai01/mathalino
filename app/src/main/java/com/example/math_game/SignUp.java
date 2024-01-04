package com.example.math_game;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    CheckBox showPass;
    RadioButton registerMale,registerFemale;
    Button sbirthDate, login;
    DatePickerDialog dpDialog;
    EditText sName, sUsername, sPass, sConfirmPass;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM / dd / yyyy", Locale.getDefault());
    private final int cYear = calendar.get(Calendar.YEAR);
    private final int cMonth = calendar.get(Calendar.MONTH);
    private final int cDay = calendar.get(Calendar.DAY_OF_MONTH);
    int[] age = {0};
    String gender="";
    String[] dateMonth, textDate = {""};

    ContentValues valuesAdd, valuesAdd1;
    Handler handle;
    private static Dialog errorDialog, confirmDialog;


    SQLiteDatabase db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // DECLARATION
        handle = new Handler();
        errorDialog = new Dialog(this);
        confirmDialog = new Dialog(this);

        db = openOrCreateDatabase("accDetails", Context.MODE_PRIVATE, null);

        sName = findViewById(R.id.loginUsername);
        sUsername = findViewById(R.id.registerUsername);
        sPass = findViewById(R.id.registerPass);
        sConfirmPass = findViewById(R.id.registerConfirmPass);

        dateMonth = getResources().getStringArray(R.array.dateMonth);
        textDate[0] = dateFormat.format(calendar.getTime());
        sbirthDate = findViewById(R.id.registerBirthdate);
        sbirthDate.setText("Birthday: "+dateFormat.format(calendar.getTime()));
        dpDialog = CommonUtils.initDatePicker(this, dpDialog, calendar, textDate, dateMonth, age, cYear, cMonth, cDay, sbirthDate);
        sbirthDate.setOnClickListener(v ->  dpDialog.show());

        registerMale = findViewById(R.id.registerMale);
        registerFemale = findViewById(R.id.registerFemale);

        showPass = findViewById(R.id.registerShowPass);
        showPass.setOnClickListener(v -> {
          if(showPass.isChecked()){
              sPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
              sConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
          }else{
              sPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
              sConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
          }
        });

        login = findViewById(R.id.loginLogin);
        login.setOnClickListener(v -> database());

        ImageView registerExit = findViewById(R.id.registerExit);
        registerExit.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
            handle.postDelayed(this::finish, 0);
        });
    }

    private void database(){

        String[] parts = textDate[0].split(":");
        if (parts.length > 1) textDate[0] = parts[1].trim();

        if(registerMale.isChecked()) gender = "Male";
        else if(registerFemale.isChecked()) gender = "Female";
        else gender = "";

        // INITIAL DATABASE
         db.execSQL("CREATE TABLE IF NOT EXISTS tableAcc (username TEXT PRIMARY KEY,"+
                "uPass TEXT, uName TEXT, uBday TEXT, uAge INTEGER, uGender TEXT, uExp INTEGER);");

        if(sName.getText().toString().isEmpty() || sUsername.getText().toString().isEmpty() ||
                sPass.getText().toString().isEmpty() || sConfirmPass.getText().toString().isEmpty() || gender.isEmpty() ){
            CommonUtils.errorMsg(errorDialog,this, R.layout.activity_error_msg,"Please ensure that you provide the required information.");
            return;
        }
        else if(!sPass.getText().toString().equals(sConfirmPass.getText().toString())){
            CommonUtils.errorMsg(errorDialog,this,R.layout.activity_error_msg, "The passwords you entered do not match. Please try again.");
            return;
        }
        else if(!CommonUtils.passChecker(sPass.getText().toString())){
            CommonUtils.errorMsg(errorDialog, this,R.layout.activity_error_msg, "The password should be 8-15 characters and with atleast one number." );
            return;
        }

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + sUsername.getText().toString() + "'", null);

        if(!(cursor.getCount()==0)){
            CommonUtils.errorMsg(errorDialog,this,R.layout.activity_error_msg, "The username you entered already exists.");
            return;
        }

        valuesAdd = new ContentValues();
        valuesAdd.put("username",String.valueOf(sUsername.getText()));
        valuesAdd.put("uPass",String.valueOf(sPass.getText()));
        valuesAdd.put("uName",String.valueOf(sName.getText()));
        valuesAdd.put("uBday",textDate[0]);
        valuesAdd.put("uAge",age[0]);
        valuesAdd.put("uGender",gender);
        valuesAdd.put("uExp",0);
        db.insert("tableAcc",null,valuesAdd);

        //DATABASE FOR SCORES
        db.execSQL("CREATE TABLE IF NOT EXISTS userScore (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT,"+
                "gameNo INTEGER, scoreGame INTEGER, exp INTEGER);");

        CommonUtils.welcome(confirmDialog,this,R.layout.activity_success,"Successfully created an account.\n Welcome "+sUsername.getText().toString()+"!","SIGN UP",
                result -> { if (result == 1) returnHome(); });

    }

    public void returnHome(){
        Intent intent = new Intent(this, Navigation.class);
        SharedPreferences sharedPref = getSharedPreferences("mathalino", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", String.valueOf(sUsername.getText()));
        editor.putString("gender", gender);
        editor.apply();
        startActivity(intent);
        confirmDialog.dismiss();
        handle.postDelayed(this::finish, 0);
    }

}