package com.example.math_game;

import static com.example.math_game.CommonUtils.*;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.atomic.AtomicReference;

public class GameNum7 extends AppCompatActivity {

    // XML ELEMENTS VARIABLE
    Handler handle;
    ImageView[] buttonImg = new ImageView[4];
    CardView[] option = new CardView[4];
    ConstraintLayout[] clButton = new ConstraintLayout[4];
    Button continueBtn;
    TextView g7Score, timerNum, popTitle, popStatus, popPoints, txtQuestion;
    View popup;
    ProgressBar gameProgress;

    // COUNTDOWN TIMER
    CountDownTimer countdown;

    // IDS
    int[] buttonImgId = {R.id.imgGame7A, R.id.imgGame7B, R.id.imgGame7C, R.id.imgGame7D};
    int[] buttonIds = {R.id.btnGame7A, R.id.btnGame7B, R.id.btnGame7C, R.id.btnGame7D};
    int[] clButtonID = {R.id.clG7A, R.id.clG7B, R.id.clG7C, R.id.clG7D};
    static int[] shapeImgID = {R.drawable.icon_circle, R.drawable.icon_triangle, R.drawable.icon_square,
            R.drawable.icon_pentagon, R.drawable.icon_hexagon, R.drawable.icon_heptagon,
            R.drawable.icon_octagon, R.drawable.icon_nonagon, R.drawable.icon_decagon
    };
    String[] shapeAns = { "circle", "triangle", "square", "pentagon", "hexagon", "heptagon",
            "octagon", "nonagon", "decagon" };


    // VARIABLE FOR THE GAME
    int correctAnsIndex, correctAns;
    int itemNum = -1, winstreak = 0, points = 0;

    // RANDOM NUMBER GENERATOR
    static UniqueRandomGenerator shapeGen;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_num7);

        dbHelper = new DatabaseHelper(this);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());

        handle = new Handler();

        ImageView exit = findViewById(R.id.game7_exit);
        exit.setOnClickListener(v ->{
            intent.set(new Intent(this, Navigation.class));
            startActivity(intent.get());
            handle.postDelayed(this::finish, 0);
        });

        txtQuestion = findViewById(R.id.txtG7Question);
        gameProgress = findViewById(R.id.game7ProgressBar);

        timerNum = findViewById(R.id.txtGame7_Timer);

        for (int i = 0; i < option.length && i < buttonIds.length; i++) {
            option[i] = findViewById(buttonIds[i]);
            buttonImg[i] = findViewById(buttonImgId[i]);
            clButton[i] = findViewById(clButtonID[i]);
            clButton[i].setOnClickListener(buttonAns);
        }

        g7Score = findViewById(R.id.txtG1Score);

        continueBtn = findViewById(R.id.continueButton);
        continueBtn.setOnClickListener(buttonAns);
        popup = findViewById(R.id.popup);
        popTitle = findViewById(R.id.popTitle);
        popStatus = findViewById(R.id.popStatus);
        popPoints = findViewById(R.id.popPoints);

        shapeGen = new UniqueRandomGenerator(0, 8);
        gameNum7();
    }

    @SuppressLint("SetTextI18n")
    View.OnClickListener buttonAns = view -> {

        countdown.cancel();
        int timeRemaining = Integer.parseInt((String) timerNum.getText()), addPoints, answer = 0;

        if(view.getId()== clButtonID[0] || view.getId()== clButtonID[1] || view.getId()== clButtonID[2] || view.getId()== clButtonID[3]) {

            for (int i = 0; i < option.length && i < buttonIds.length; i++){
                if(view.getId() == clButtonID[i]) answer = i;
                option[i].setClickable(false);
            }

            popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
            popup.setVisibility(View.VISIBLE);
            popPoints.setText("");
            popStatus.setText("");
            if (correctAnsIndex == answer) {
                popup.setBackgroundColor(Color.parseColor("#A84CAF50"));
                popTitle.setText("Good Job!");
                popTitle.setTextColor(Color.parseColor("#2B812E"));
                continueBtn.setBackgroundResource(R.drawable.custom_button2);
                winstreak++;
                addPoints = (timeRemaining / 10) + 1;
                if(winstreak >= 3) {
                    addPoints += winstreak - 2;
                    popStatus.setText("x"+ winstreak +" combo!");
                }
                popPoints.setText("+" + addPoints + " pts");
                points+= addPoints;
                g7Score.setText(String.valueOf(points));
            } else {
                popup.setBackgroundColor(Color.parseColor("#A6D82618"));
                popTitle.setText("Wrong Answer!");
                popTitle.setTextColor(Color.parseColor("#AF1C11"));
                continueBtn.setBackgroundResource(R.drawable.custom_button3);
                clButton[answer].setBackgroundResource(R.drawable.custom_button5);
                winstreak = 0;
            }
            clButton[correctAnsIndex].setBackgroundResource(R.drawable.custom_button4);
        }else if(view.getId()== R.id.continueButton){
            popup.startAnimation(loadSlideDownAnimation(getApplicationContext()));
            popup.setVisibility(View.INVISIBLE);
            handle.postDelayed(this::gameNum7, 800);
            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                clButton[i].setBackgroundResource(R.drawable.custom_button1);
                option[i].setClickable(true);
            }
        }

    };

    @SuppressLint("SetTextI18n")
    private void gameNum7() {
        itemNum++;
        if(itemNum>=5){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(7, points, points/2);
                dbHelper.dbScoreSummary();
                Intent intent = new Intent(this, Navigation.class);
                startActivity(intent);
                handle.postDelayed(this::finish, 0);
            }, 800);
        }

        ObjectAnimator.ofInt(gameProgress, "Progress", 20 * (itemNum))
                .setDuration(500)
                .start();

        if (itemNum <= 4) {
            countdown= new CountDownTimer(21000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    winstreak = 0;
                    showNoTimer();
                }
            }.start();

            correctAns = shapeGen.call();
            correctAnsIndex = buttonChoicesGen(correctAns, buttonImg);
            txtQuestion.setText("Which of these is a " + shapeAns[correctAns] + "?");
        }

    }

    public static int buttonChoicesGen(int correctAns, ImageView[] buttonImg){
        int correctAnsIndex = getRandomNumber(0, 3);
        for(int x = 0; x < 4; x++){
            if(correctAnsIndex == x ) buttonImg[x].setImageResource(shapeImgID[correctAns]);
            else {
                int imgResource = shapeGen.call();
                buttonImg[x].setImageResource(shapeImgID[imgResource]);
                if(imgResource == correctAns) buttonImg[x].setImageResource(shapeImgID[shapeGen.call()]);
            }
        }
        return correctAnsIndex;
    }

    @SuppressLint("SetTextI18n")
    public void showNoTimer(){

        popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
        popup.setVisibility(View.VISIBLE);

        popup.setBackgroundColor(Color.parseColor("#A6D82618"));
        popTitle.setText("Time's up!");
        popTitle.setTextColor(Color.parseColor("#AF1C11"));
        popPoints.setText("");
        popStatus.setText("");
        continueBtn.setBackgroundResource(R.drawable.custom_button3);
        clButton[correctAnsIndex].setBackgroundResource(R.drawable.custom_button4);
        for (int i = 0; i < option.length && i < buttonIds.length; i++) option[i].setClickable(false);
    }

}