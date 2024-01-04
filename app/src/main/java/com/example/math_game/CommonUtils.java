package com.example.math_game;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private CommonUtils() {
        // Private constructor to prevent instantiation
    }

    public static Animation loadSlideUpAnimation(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public static Animation loadSlideDownAnimation(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_down);
    }

    public static SharedPreferences sharedPrefer;
    public static String username;


    // ERROR MESSAGE DIALOG
    public static void errorMsg(Dialog errorDialog,
                         android.content.Context context ,int view, String text){
        ImageView exitIcon;
        Button exit_btn;

        errorDialog.setContentView(view);
        exitIcon = errorDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(context, R.color.error), PorterDuff.Mode.SRC_IN);
            errorDialog.dismiss();
        });

        TextView changeText =  errorDialog.findViewById(R.id.success_msg);
        changeText.setText(text);

        exit_btn =  errorDialog.findViewById(R.id.success_ok);
        exit_btn.setOnClickListener(v -> errorDialog.dismiss());

        errorDialog.show();
    }

    // CONFIRM/WELCOME MESSAGE DIALOG
    public static void welcome(Dialog confirmDialog,
                        android.content.Context context, int view, String text, String title, DialogDismissListener dismissListener){
        ImageView exitIcon;
        Button exit_btn;


        confirmDialog.setContentView(view);
        exitIcon = confirmDialog.findViewById(R.id.success_exit);
        exitIcon.setOnClickListener(v -> {
            exitIcon.setColorFilter(ContextCompat.getColor(context, R.color.error), PorterDuff.Mode.SRC_IN);
            confirmDialog.dismiss();
            if (dismissListener != null) dismissListener.onDialogDismissed(1);

        });

        TextView changeTitle =  confirmDialog.findViewById(R.id.success_title);
        changeTitle.setText(title);

        TextView changeText =  confirmDialog.findViewById(R.id.success_msg);
        changeText.setText(text);

        exit_btn = confirmDialog.findViewById(R.id.success_ok);
        exit_btn.setOnClickListener(v -> {
            confirmDialog.dismiss();
            if (dismissListener != null) dismissListener.onDialogDismissed(1);
        } );

        confirmDialog.show();
    }

    public interface DialogDismissListener {
        void onDialogDismissed(int result);
    }

    // PASSWORD CHECKER
    public static boolean passChecker(String password) {
        String regex = "^(?=.*[0-9]).{8,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    // DATE-PICKER
    public static DatePickerDialog initDatePicker(Context context, DatePickerDialog dpDialog, Calendar calendar, String[] textDateWrapper, String[] dateMonth, int[] ageWrapper, int cYear, int cMonth, int cDay, Button UPBday) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {

            textDateWrapper[0] = dateMonth[month];
            textDateWrapper[0] = textDateWrapper[0] + " / " + day + " / " + year;

            ageWrapper[0] = calendar.get(Calendar.YEAR) - year - 1;
            if (month < calendar.get(Calendar.MONTH) || ((month == calendar.get(Calendar.MONTH)) && (day <= calendar.get(Calendar.DAY_OF_MONTH)))) {
                ageWrapper[0]++;
            }

            UPBday.setText(textDateWrapper[0]);
        };

        dpDialog = new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, dateSetListener, cYear, cMonth, cDay);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return dpDialog;
    }

    // FOR GAME UTILITY

    public static int getRandomNumber(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    @SuppressLint("SetTextI18n")
    public static int buttonChoicesGen(int correctAns, TextView[] option){
        int correctAnsIndex = getRandomNumber(1, option.length);
        UniqueRandomGenerator optionGen;
        if(correctAns <= 0 ) optionGen = new UniqueRandomGenerator(correctAns - 5, correctAns + 5);
        else optionGen = new UniqueRandomGenerator(0, correctAns + 5);

        for(int x = 0; x < option.length; x++){
            if(correctAnsIndex - 1 == x ) option[x].setText(Integer.toString(correctAns));
            else {
                try {
                    option[x].setText(Integer.toString(optionGen.call()));
                    if(Integer.parseInt((String) option[x].getText()) == correctAns) option[x].setText(Integer.toString(optionGen.call()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return correctAnsIndex;
    }

    @SuppressLint("SetTextI18n")
    public static void showNoTimer(View popup, TextView popTitle, TextView popPoints, TextView popStatus, Button continueBtn, Button[] option, int[] buttonIds, int correctIndex, Animation slideUp){

        popup.startAnimation(slideUp);
        popup.setVisibility(View.VISIBLE);

        popup.setBackgroundColor(Color.parseColor("#A6D82618"));
        popTitle.setText("Time's up!");
        popTitle.setTextColor(Color.parseColor("#AF1C11"));
        popPoints.setText("");
        popStatus.setText("");
        continueBtn.setBackgroundResource(R.drawable.custom_button3);
        option[correctIndex].setBackgroundResource(R.drawable.custom_button4);
        option[correctIndex].setTextColor(Color.parseColor("#00B32D"));
        for (int i = 0; i < option.length && i < buttonIds.length; i++) option[i].setClickable(false);
    }

}