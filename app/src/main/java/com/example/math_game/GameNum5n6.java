package com.example.math_game;

import static com.example.math_game.CommonUtils.*;
import static com.example.math_game.HomeFragment.*;
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

import java.util.concurrent.atomic.AtomicReference;

public class GameNum5n6 extends AppCompatActivity {

    Handler handle;

    // XML ELEMENTS VARIABLE
    Button[] option = new Button[4];
    Button continueBtn;
    TextView[] txtRatio = new TextView[4];
    TextView  txtMiddleLeft, txtMiddle, txtMiddleRight, txtQuestion;
    TextView g5n6Score, timerNum, popTitle, popPoints, popStatus;
    View popup;
    ProgressBar gameProgress;

    // COUNTDOWN TIMER
    CountDownTimer countdown;

    // ID OF THE BUTTONS
    int[] buttonIds = {R.id.btnGame5C1, R.id.btnGame5C2, R.id.btnGame5C3, R.id.btnGame5C4};
    int[] txtRatioId = {R.id.txtG5n6FirstQLeft, R.id.txtG5n6SecondQLeft, R.id.txtG5n6FirstQRight, R.id.txtG5n6SecondQRight};
    // VARIABLES FOR THE GAME MECHANICS
    // ITEM NUM IS WHAT ITEM HAVE ALREADY SHOW
    // OTHER VARIABLES ARE SELF EXPLANATORY
    int correctAns, correctAnsIndex, missingVal = 0;
    int itemNum = -1, winstreak = 0, points = 0;
    // TO CHECK IF THE GAME NUM 5 IS ALL ABOUT LCD OR GCF
    int gameModeChosen;
    // FOR CHECKING WHICH GAMEMODE IS CHOSEN
    String gameModeStr;
    // RANDOM NUMBER GENERATOR FOR RATIO AND PROPORTION
    UniqueRandomGenerator multipleGen, numRatioGen, missingNumGen;
    // RANDOM NUMBER GENERATOR FOR LCD AND GCF GAME
    UniqueRandomGenerator gameModeGen, gcfGen, multiplyByGen;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_num5n6);


        dbHelper = new DatabaseHelper(this);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        gameModeStr = intent.get().getStringExtra("game");

        handle = new Handler();

        txtQuestion = findViewById(R.id.txtG5n6Question);
        gameProgress = findViewById(R.id.game5n6ProgressBar);

        timerNum = findViewById(R.id.txtGame5n6_Timer);

        txtMiddleLeft = findViewById(R.id.txtG5n6QMissingLeft);
        txtMiddleRight = findViewById(R.id.txtG5n6QMissingRight);
        txtMiddle = findViewById(R.id.txtG5n6Mid);

        for (int i = 0; i < option.length && i < buttonIds.length; i++) {
            option[i] = findViewById(buttonIds[i]);
            option[i].setOnClickListener(buttonAns);
            txtRatio[i] = findViewById(txtRatioId[i]);
        }

        g5n6Score = findViewById(R.id.txtG1Score);

        continueBtn = findViewById(R.id.continueButton);
        continueBtn.setOnClickListener(buttonAns);
        popup = findViewById(R.id.popup);
        popTitle = findViewById(R.id.popTitle);
        popPoints = findViewById(R.id.popPoints);
        popStatus = findViewById(R.id.popStatus);

        ImageView exit = findViewById(R.id.game5n6_exit);
        exit.setOnClickListener(v ->{
            intent.set(new Intent(this, Navigation.class));
            startActivity(intent.get());
            handle.postDelayed(this::finish, 0);
        });

        if(gameModeStr.equals("game5")) {
            gameModeGen = new UniqueRandomGenerator(1, 10);
            gcfGen = new UniqueRandomGenerator(2, 9);
            multiplyByGen = new UniqueRandomGenerator(2, 5);
            txtMiddle.setText("");
            gameNum5();
        }else if(gameModeStr.equals("game6")){
            numRatioGen = new UniqueRandomGenerator(2, 9);
            multipleGen = new UniqueRandomGenerator(2, 9);
            missingNumGen = new UniqueRandomGenerator(0, 3);
            txtMiddle.setText("::");
            gameNum6();
        }

    }

    // BUTTONS ONCLICKLISTENER ////////////////////////////////////////////////////////////////////
    @SuppressLint("SetTextI18n")
    View.OnClickListener buttonAns = view -> {

        countdown.cancel();
        int timeRemaining = Integer.parseInt((String) timerNum.getText()), addPoints;

        if(view.getId()== R.id.btnGame5C1 || view.getId()== R.id.btnGame5C2 || view.getId()== R.id.btnGame5C3 || view.getId()== R.id.btnGame5C4) {

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
                SoundManager.playSound(GameNum5n6.this, R.raw.gjgj);
                popup.setBackgroundColor(Color.parseColor("#A84CAF50"));
                popTitle.setText("Good Job!");
                popTitle.setTextColor(Color.parseColor("#2B812E"));
                continueBtn.setBackgroundResource(R.drawable.custom_button2);
                winstreak++;
                addPoints = (timeRemaining / 10) + 1;
                if(winstreak >= 3) {
                    if(winstreak == 3) SoundManager.playSound(GameNum5n6.this, R.raw.glike);
                    else if(winstreak == 4) SoundManager.playSound(GameNum5n6.this, R.raw.dom);
                    else if(winstreak >= 5) SoundManager.playSound(GameNum5n6.this, R.raw.legend);
                    addPoints += winstreak - 2;
                    popStatus.setText("x"+ winstreak +" combo!");
                }else{
                    SoundManager.playSound(GameNum5n6.this, R.raw.gjgj);
                }
                popPoints.setText("+" + addPoints + " pts");
                points+= addPoints;
                g5n6Score.setText(String.valueOf(points));
            } else {
                SoundManager.playSound(GameNum5n6.this, R.raw.ntnt);
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
            if(gameModeStr.equals("game5")) handle.postDelayed(this::gameNum5, 800);
            else handle.postDelayed(this::gameNum6, 800);
            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                option[i].setBackgroundResource(R.drawable.custom_button1);
                option[i].setTextColor(normal);
                option[i].setClickable(true);
            }

        }

    };

    // ALL CLASSES AND FUNCTION FOR GAME 5 /////////////////////////////////////////////////////////
    @SuppressLint("SetTextI18n")
    private void gameNum5(){
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(5, points, points/2);
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

            countdown= new CountDownTimer(21000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    winstreak = 0;
                    SoundManager.playSound(GameNum5n6.this, R.raw.tayms);
                    CommonUtils.showNoTimer(popup, popTitle, popPoints, popStatus, continueBtn, option, buttonIds, correctAnsIndex-1, loadSlideUpAnimation(getApplicationContext()));
                }
            }.start();

            gameModeChosen = gameModeGen.call() % 2;
            // if gameModeChosen equals 0, the game would be all about GCF else LCD

            correctAns = gcfGen.call();
            int firstNum = correctAns * multiplyByGen.call();
            int secondNum = correctAns * multiplyByGen.call();
            txtMiddleLeft.setText(String.valueOf(firstNum));
            txtMiddleRight.setText(String.valueOf(secondNum));
            // GCF
            if(gameModeChosen == 0){
                txtQuestion.setText("Find the GCF of the two numbers shown below.");
                correctAns = calculateGCF(firstNum,secondNum);
            // LCD
            } else{
                txtQuestion.setText("Find the LCD of the two numbers shown below.");
                correctAns = calculateLCD(firstNum,secondNum);
               }
            correctAnsIndex = CommonUtils.buttonChoicesGen(correctAns, option);

        }

    }

    // CALCULATING LCD
    private static int calculateLCD(int firstNum, int secondNum) {
        int gcd = calculateGCF(firstNum, secondNum);
        return Math.abs(firstNum * secondNum) / gcd;
    }

    // CALCULATING GCF
    private static int calculateGCF(int firstNum, int secondNum) {
        while (secondNum != 0) {
            int temp = secondNum;
            secondNum = firstNum % secondNum;
            firstNum = temp;
        }
        return Math.abs(firstNum);
    }

    // ALL CLASSES AND FUNCTION FOR GAME 6 ////////////////////////////////////////////////////////
    private void gameNum6(){
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(6, points, points/2);
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

            countdown= new CountDownTimer(21000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    winstreak = 0;
                    SoundManager.playSound(GameNum5n6.this, R.raw.tayms);
                    CommonUtils.showNoTimer(popup, popTitle, popPoints, popStatus, continueBtn, option, buttonIds, correctAnsIndex-1, loadSlideUpAnimation(getApplicationContext()));
                }
            }.start();

            txtRatio[missingVal].setTextColor(grayNormal);

            int[] ratio = new int[4];
            int multiple = multipleGen.call();
            // SETTING UP THE RATIO
            for(int x = 0; x < 2; x++){
                ratio[x] = numRatioGen.call();
                ratio[x+2] = ratio[x] * multiple;
            }

            // GENERATING WHICH PART OF PROPORTION IS MISSING
            missingVal = missingNumGen.call();
            correctAns = ratio[missingVal];
            for(int i = 0; i < 4; i++)  {
                if(i == missingVal){
                    txtRatio[i].setTextColor(errorColor);
                    txtRatio[i].setText("?");
                }else txtRatio[i].setText(String.valueOf(ratio[i]));
            }
            correctAnsIndex = CommonUtils.buttonChoicesGen(correctAns, option);

        }

    }


}