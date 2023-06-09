package com.example.math_game;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    int birthDateYear, birthDateMonth, birthDateDay, age = 0;
    String textDate = "", gender="";
    String[] dateMonth;

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

        initDatePicker();
        dateMonth = getResources().getStringArray(R.array.dateMonth);

        sName = findViewById(R.id.loginUsername);
        sUsername = findViewById(R.id.registerUsername);
        sPass = findViewById(R.id.registerPass);
        sConfirmPass = findViewById(R.id.registerConfirmPass);

        textDate = dateFormat.format(calendar.getTime());
        sbirthDate = findViewById(R.id.registerBirthdate);
        sbirthDate.setText("Birthday: "+dateFormat.format(calendar.getTime()));
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

    // error message
    public void errorMsg(int view, String text){
        ImageView exitIcon;
        Button exit_btn;

        errorDialog.setContentView(view);
        exitIcon = errorDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(SignUp.this, R.color.error), PorterDuff.Mode.SRC_IN);
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
            exitIcon.setColorFilter(ContextCompat.getColor(SignUp.this, R.color.error), PorterDuff.Mode.SRC_IN);
            Intent intent = new Intent(SignUp.this, HomePage.class);
            intent.putExtra("username",sUsername.getText().toString());
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
            Intent intent = new Intent(SignUp.this, HomePage.class);
            intent.putExtra("username",sUsername.getText().toString());
            startActivity(intent);
            confirmDialog.dismiss();
            handle.postDelayed(this::finish, 0);
        });

        confirmDialog.show();

    }

    // date picker
    private void initDatePicker(){

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {

            textDate = dateMonth[month];
            textDate="Birthday: "+textDate+" / "+day+" / "+year;

            birthDateYear = year;
            birthDateMonth = month;
            birthDateDay = day;

            if(birthDateMonth < (calendar.get(Calendar.MONTH) + 1) || (birthDateMonth == (calendar.get(Calendar.MONTH)+1)
                    &&  birthDateDay <= calendar.get(Calendar.DAY_OF_MONTH))) age++;
            age += calendar.get(Calendar.YEAR) - birthDateYear - 1;

            sbirthDate.setText(textDate);
        };

        dpDialog = new DatePickerDialog(this,  AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, dateSetListener, cYear, cMonth, cDay);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void database(){

        String[] parts = textDate.split(":");
        if (parts.length > 1) textDate = parts[1].trim();

        if(registerMale.isChecked()) gender = "Male";
        else if(registerFemale.isChecked()) gender = "Female";
        else gender = "";

        // INITIAL DATABASE
         db.execSQL("CREATE TABLE IF NOT EXISTS tableAcc (username TEXT PRIMARY KEY,"+
                "uPass TEXT, uName TEXT, uBday TEXT, uAge INTEGER, uGender TEXT);");

        if(sName.getText().toString().isEmpty() || sUsername.getText().toString().isEmpty() ||
                sPass.getText().toString().isEmpty() || sConfirmPass.getText().toString().isEmpty() || gender.isEmpty() ){
            errorMsg(R.layout.activity_error_msg,"Please ensure that you provide the required information.");
            return;
        }
        else if(!sPass.getText().toString().equals(sConfirmPass.getText().toString())){
            errorMsg(R.layout.activity_error_msg, "The passwords you entered do not match. Please try again.");
            return;
        }

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM tableAcc WHERE username ='" + sUsername.getText().toString() + "'", null);

        if(!(cursor.getCount()==0)){
            errorMsg(R.layout.activity_error_msg, "The username you entered already exists.");
            return;
        }

        db.execSQL("INSERT INTO tableAcc (username, uPass, uName, uBday, uAge, uGender) " +
                "VALUES('"+sUsername.getText().toString()+"'"+","+"'"+sPass.getText().toString()+"'"+","+"'"+sName.getText().toString()+
                "'"+","+"'"+ textDate+"'"+","+"'"+ age+"'"+","+"'"+ gender+"')");


        db.execSQL("CREATE TABLE IF NOT EXISTS userScore (username TEXT PRIMARY KEY,"+
                "scorelvl1 INTEGER, scorelvl2 INTEGER, scorelvl3 INTEGER);");

        db.execSQL("INSERT INTO userScore (username, scorelvl1, scorelvl2, scorelvl3) " +
                "VALUES('"+sUsername.getText().toString()+"'"+","+"'"+ 0 +"'"+","+"'"+ 0 +
                "'"+","+"'"+ 0 +"')");

        db.execSQL("CREATE TABLE IF NOT EXISTS tableAccStats (username TEXT PRIMARY KEY,"+
                "level INTEGER, experience INTEGER);");

        db.execSQL("INSERT INTO tableAccStats (username, level, experience) " +
                "VALUES('"+sUsername.getText().toString()+"'"+","+"'"+ 0 +"'"+","+"'"+ 0 +"')");

        welcome(R.layout.activity_success,"Successfully created an account. Welcome "+sUsername.getText().toString()+"!","SIGN UP");
    }


}