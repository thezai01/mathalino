package com.example.math_game;

import static com.example.math_game.CommonUtils.*;
import static com.example.math_game.HomeFragment.*;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.concurrent.atomic.AtomicReference;

public class GameNum1 extends AppCompatActivity {

    // XML ELEMENTS VARIABLE
    Handler handle;

    TextView g1Score, timerNum, popTitle, popStatus, popPoints;
    TextView[] txtShown = new TextView[3];
    TextView[] operatorShown = new TextView[3];
    ImageView[] imgShown = new ImageView[7];
    Button[] option = new Button[3];
    Button continueBtn;
    View popup;
    ProgressBar gameProgress;


    // COUNTDOWN TIMER
    CountDownTimer countdown;

    // ID
    int[] buttonIds = {R.id.btnGame1A, R.id.btnGame1B, R.id.btnGame1C};
    int[] imgShownID = {R.id.imgGameHintFirst, R.id.imgGameHintSecond, R.id.imgGameHintSecond1,
            R.id.imgGameHintThird1, R.id.imgGameHintThird2, R.id.imgGameQuestion, R.id.imgGameQuestion1
    };
    int[] txtShownID = {R.id.txtGameHintFirst1, R.id.txtGameHintSecond2, R.id.txtGameHintThird
    };
    int[] txtOperatorID = {R.id.txtGame1SecondOperation, R.id.txtGame1ThirdOperation, R.id.txtGame1QuestionOperation
    };

    int[] imgID = {R.drawable.icon_apple, R.drawable.icon_apple2, R.drawable.icon_apple3,
            R.drawable.icon_banana, R.drawable.icon_banana2, R.drawable.icon_banana3,
            R.drawable.icon_blueberries, R.drawable.icon_blueberries2, R.drawable.icon_blueberries3,
            R.drawable.icon_cherry, R.drawable.icon_cherry2, R.drawable.icon_cherry3,
            R.drawable.icon_orange, R.drawable.icon_orange2, R.drawable.icon_orange3
    };

    // VARIABLE FOR THE GAME
    int correctAnsIndex, correctAns;
    int itemNum = -1, winstreak = 0, points = 0;

    int[][] fruit = new int[3][2];
    int[] operator = new int[3];
    char[] strOperator = {'+', '+', '-'};
    UniqueRandomGenerator fruitGen, fruitQuantityGen, operatorGen, valueGen;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_num1);

        dbHelper = new DatabaseHelper(this);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());

        handle = new Handler();

        ImageView exit = findViewById(R.id.game1_exit);
        exit.setOnClickListener(v -> {
            intent.set(new Intent(this, Navigation.class));
            startActivity(intent.get());
            handle.postDelayed(this::finish, 0);
        });

        gameProgress = findViewById(R.id.game1ProgressBar);
        timerNum = findViewById(R.id.txtGame1_Timer);

        g1Score = findViewById(R.id.txtG1Score);

        for (int i = 0; i < imgShown.length; i++) imgShown[i] = findViewById(imgShownID[i]);
        for (int i = 0; i < txtShown.length; i++) {
            txtShown[i] = findViewById(txtShownID[i]);
            operatorShown[i] = findViewById(txtOperatorID[i]);
        }

        for (int i = 0; i < option.length; i++){
            option[i] = findViewById(buttonIds[i]);
            option[i].setOnClickListener(buttonAns);
        }

        continueBtn = findViewById(R.id.continueButton);
        continueBtn.setOnClickListener(buttonAns);
        popup = findViewById(R.id.popup);
        popTitle = findViewById(R.id.popTitle);
        popStatus = findViewById(R.id.popStatus);
        popPoints = findViewById(R.id.popPoints);

        fruitQuantityGen = new UniqueRandomGenerator(1, 3);
        operatorGen = new UniqueRandomGenerator(0, 2);
        valueGen = new UniqueRandomGenerator(2, 8);
        gameNum1();
    }

    @SuppressLint("SetTextI18n")
    View.OnClickListener buttonAns = view -> {
        countdown.cancel();
        int timeRemaining = Integer.parseInt((String) timerNum.getText()), addPoints;

        if(view.getId()== R.id.btnGame1A || view.getId()== R.id.btnGame1B || view.getId()== R.id.btnGame1C) {

            int answer = 0;
            for (int i = 0; i < option.length && i < buttonIds.length; i++){
                if (view.getId() == buttonIds[i]) answer = Integer.parseInt(option[i].getText().toString());
                option[i].setClickable(false);
            }

            popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
            popup.setVisibility(View.VISIBLE);
            popPoints.setText("");
            popStatus.setText("");
            if (correctAns == answer) {
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
                g1Score.setText(String.valueOf(points));
            } else {
                popup.setBackgroundColor(Color.parseColor("#A6D82618"));
                popTitle.setText("Wrong Answer!");
                popTitle.setTextColor(Color.parseColor("#AF1C11"));
                continueBtn.setBackgroundResource(R.drawable.custom_button3);
                for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                    if(buttonIds[i] == view.getId()) {
                        option[i].setBackgroundResource(R.drawable.custom_button5);
                        option[i].setTextColor(wrong);
                    }
                }
                winstreak = 0;
            }
            option[correctAnsIndex-1].setBackgroundResource(R.drawable.custom_button4);
            option[correctAnsIndex-1].setTextColor(correct);
        }else if(view.getId()== R.id.continueButton){
            popup.startAnimation(loadSlideDownAnimation(getApplicationContext()));
            popup.setVisibility(View.INVISIBLE);
            handle.postDelayed(this::gameNum1, 800);
            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                option[i].setBackgroundResource(R.drawable.custom_button1);
                option[i].setTextColor(normal);
                option[i].setClickable(true);
            }
        }

    };

    @SuppressLint("SetTextI18n")
    private void gameNum1() {
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(1, points, points/2);
                dbHelper.dbScoreSummary();
                Intent intent = new Intent(this, Navigation.class);
                startActivity(intent);
                handle.postDelayed(this::finish, 0);
            }, 800);
        }

        ObjectAnimator.ofInt(gameProgress, "Progress", 10 * (itemNum))
                .setDuration(500)
                .start();

        if (itemNum <= 9) {

            countdown = new CountDownTimer(91000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    winstreak = 0;
                    CommonUtils.showNoTimer(popup, popTitle, popPoints, popStatus, continueBtn, option, buttonIds, correctAnsIndex-1, loadSlideUpAnimation(getApplicationContext()));
                }
            }.start();

            fruitGen = new UniqueRandomGenerator(0, 4);
            for (int i = 0; i < fruit.length; i++) {
                fruit[i][0] = fruitGen.call();
                fruit[i][1] = valueGen.call();
                operator[i] = operatorGen.call();
                operatorShown[i].setText(String.valueOf(strOperator[operator[i]]));
            }

            int[] quantity = new int[7];
            int[] fruitIndex = {0, 1, 0, 1, 2, 0, 2};
            int answer = 0;
            for (int i = 0; i < imgShown.length; i++) {
                quantity[i] = fruitQuantityGen.call();
                imgShown[i].setImageResource(imgID[(fruit[fruitIndex[i]][0]* 3) + (quantity[i]-1)]);
                if (i == 0) txtShown[0].setText(String.valueOf(fruit[fruitIndex[i]][1] * quantity[i]));
                else if (i == 2) {
                    answer = solveGen((fruit[fruitIndex[i - 1]][1] * quantity[i - 1]), (fruit[fruitIndex[i]][1] * quantity[i]), operator[0]);
                    txtShown[1].setText(String.valueOf(answer));
                } else if (i == 4) {
                    switch (operator[1]) {
                        case 0, 1 ->
                                answer = (fruit[fruitIndex[i]][1] * quantity[i]) - (fruit[fruitIndex[i - 1]][1] * quantity[i - 1]);
                        case 2 ->
                                answer = (fruit[fruitIndex[i]][1] * quantity[i]) + (fruit[fruitIndex[i - 1]][1] * quantity[i - 1]);
                    }
                    txtShown[2].setText(String.valueOf(answer));
                }else if (i == 6) answer = solveGen((fruit[fruitIndex[i - 1]][1] * quantity[i - 1]), (fruit[fruitIndex[i]][1] * quantity[i]), operator[2]);

            }
            correctAns = answer;
            correctAnsIndex = CommonUtils.buttonChoicesGen(correctAns, option);
        }
    }
    private int solveGen(int fruit1, int fruit2, int operator) {
        switch (operator) {
            case 0, 1 -> {
                return fruit1 + fruit2;
            }
            case 2 -> {
                return fruit1 - fruit2;
            }
            default -> {
                return 0;
            }
        }

    }
}