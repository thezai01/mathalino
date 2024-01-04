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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class GameNum4 extends AppCompatActivity {

    // XML ELEMENTS VARIABLE
    Handler handle;
    Button[] option = new Button[9];
    Button continueBtn, confirmGame;
    TextView g4Score, timerNum, popTitle, popStatus, popPoints, txtQuestion;
    View popup;
    ProgressBar gameProgress;

    // COUNTDOWN TIMER
    CountDownTimer countdown;

    // INTEGER ARRAY FOR COMPOSITE NUMBERS
    int[] compositeNum = { 4, 6, 8, 9, 10, 12, 14, 15, 16, 18,
            20, 21, 22, 24, 25, 26, 27, 28, 30, 32, 33, 34,
            35, 36, 38, 39, 40, 42, 44, 45, 46, 48, 49, 50,
            51, 52, 54, 55, 56, 57, 58, 60, 62, 63, 64, 65,
            66, 68, 69, 70, 72, 74, 75, 76, 77, 78, 80, 81,
            82, 84, 85, 86, 87, 88, 90, 91, 92, 93, 94, 95,
            96, 98, 99
    };
    // INTEGER ARRAY FOR PRIME NUMBERS
    int[] primeNum = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
            79, 83, 89, 97
    };
    // BUTTON ID
    int[] buttonIds = {R.id.btnGame4A, R.id.btnGame4B, R.id.btnGame4C, R.id.btnGame4D,
            R.id.btnGame4E, R.id.btnGame4F, R.id.btnGame4G, R.id.btnGame4H, R.id.btnGame4I
    };

    // VARIABLES FOR THE GAME MECHANICS
    // ITEM NUM IS WHAT ITEM HAVE ALREADY SHOW
    // OTHER VARIABLES ARE SELF EXPLANATORY
    int[] correctAnsIndex, correctAns, selectedAnsIndex;
    boolean[] isButtonClicked;
    int itemNum = -1;
    int points = 0;
    static int rightAns = 0;
    // TO CHECK IF THE GAME NUM 5 IS ALL ABOUT LCD OR GCF
    int gameModeChosen;

    // RANDOM NUMBER GENERATOR FOR THE GAME
    UniqueRandomGenerator correctAnswersGen, correctAnswersButtonsGen, gameModeGen, randomCompositeGen, randomPrimeGen;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_num4);

        dbHelper = new DatabaseHelper(this);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());

        handle = new Handler();

        ImageView exit = findViewById(R.id.game4_exit);
        exit.setOnClickListener(v ->{
            intent.set(new Intent(this, Navigation.class));
            startActivity(intent.get());
            handle.postDelayed(this::finish, 0);
        });

        txtQuestion = findViewById(R.id.txtG4Question);
        gameProgress = findViewById(R.id.game4ProgressBar);

        timerNum = findViewById(R.id.txtGame4_Timer);

        for (int i = 0; i < option.length && i < buttonIds.length; i++) {
            option[i] = findViewById(buttonIds[i]);
            option[i].setOnClickListener(buttonAns);
        }

        confirmGame = findViewById(R.id.btnGame4Confirm);
        confirmGame.setOnClickListener(buttonAns);

        g4Score = findViewById(R.id.txtG4Score);

        continueBtn = findViewById(R.id.continueButton);
        continueBtn.setOnClickListener(buttonAns);
        popup = findViewById(R.id.popup);
        popTitle = findViewById(R.id.popTitle);
        popStatus = findViewById(R.id.popStatus);
        popPoints = findViewById(R.id.popPoints);

        // INITIALIZING RANDOM NUMBER GENERATOR
        gameModeGen = new UniqueRandomGenerator(1, 10);
        correctAnswersGen = new UniqueRandomGenerator(3, 4);
        correctAnswersButtonsGen = new UniqueRandomGenerator(0, 8);
        randomCompositeGen = new UniqueRandomGenerator(0, compositeNum.length - 1);
        randomPrimeGen = new UniqueRandomGenerator(0, primeNum.length - 1);
        gameNum4();
    }

    @SuppressLint("SetTextI18n")
    View.OnClickListener buttonAns = view -> {

        // ONCLICK LISTENER FOR THE 9 BUTTONS OF SELECTING WHICH IS THE ANSWER
        for (int i = 0; i < option.length && i < buttonIds.length; i++) {
            if(view.getId() ==  buttonIds[i]) {
                isButtonClicked[i] = !isButtonClicked[i];
                updateButtonAppearance(option[i], isButtonClicked[i]);
                break;
            }
        }

        // ONCLICK LISTENER FOR THE CONFIRM BUTTON
        if(view.getId() == R.id.btnGame4Confirm){

            popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
            popup.setVisibility(View.VISIBLE);
            popPoints.setText("");
            popStatus.setText("");

            int addPoints;

            // CANCEL COUNTDOWN
            countdown.cancel();

            // DISABLE CLICKABLE OF SELECTING WHICH IS PRIME OR COMPOSITE NUMBERS
            for (int i = 0; i < option.length && i < buttonIds.length; i++) option[i].setClickable(false);

            int selectedAnsQuantity = countTrueValues(isButtonClicked);
            selectedAnsIndex = new int[selectedAnsQuantity];
            int index = 0;
            for(int i = 0; i < option.length; i++){
                if(isButtonClicked[i]){
                    selectedAnsIndex[index] = i;
                    index++;
                }
            }

            addPoints = countMatchingElements(correctAnsIndex, selectedAnsIndex);

            if(addPoints >= 1){
                popup.setBackgroundColor(Color.parseColor("#A84CAF50"));
                popTitle.setText("Good Job!");
                popTitle.setTextColor(Color.parseColor("#2B812E"));
                popStatus.setText("You got "+ rightAns +" out of "+ correctAnsIndex.length +" right.");
                popPoints.setText("+" + addPoints + " pts");
                continueBtn.setBackgroundResource(R.drawable.custom_button2);
                points+= addPoints;
                g4Score.setText(String.valueOf(points));
            } else {
                popup.setBackgroundColor(Color.parseColor("#A6D82618"));
                popTitle.setText("Nice Try!");
                popTitle.setTextColor(Color.parseColor("#AF1C11"));
                continueBtn.setBackgroundResource(R.drawable.custom_button3);
                if(rightAns == 0) {
                    for (int i : correctAnsIndex) {
                        option[i].setBackgroundResource(R.drawable.custom_button4);
                        option[i].setTextColor(correct);
                    }
                }
            }
            // SHOW THE CORRECT AND THE INCORRECT IN THE SELECTED ANSWER
            Set<Integer> correctAnsSet = new HashSet<>();
            for (int i : correctAnsIndex) {
                correctAnsSet.add(i);
            }

            for (int ansIndex : selectedAnsIndex) {
                if (!correctAnsSet.contains(ansIndex)) {
                    option[ansIndex].setBackgroundResource(R.drawable.custom_button5);
                    option[ansIndex].setTextColor(wrong);
                } else {
                    option[ansIndex].setBackgroundResource(R.drawable.custom_button4);
                    option[ansIndex].setTextColor(correct);
                }
            }

        }else if(view.getId()== R.id.continueButton){
            popup.startAnimation(loadSlideDownAnimation(getApplicationContext()));
            popup.setVisibility(View.INVISIBLE);
            handle.postDelayed(this::gameNum4, 800);
            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                option[i].setBackgroundResource(R.drawable.custom_button1);
                option[i].setTextColor(normal);
                option[i].setClickable(true);
            }
        }

    };

    // ALL CLASSES AND FUNCTION FOR GAME 4
    @SuppressLint("SetTextI18n")
    private void gameNum4(){
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(4, points, points/2);
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
            countdown= new CountDownTimer(46000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    showNoTimer();
                }
            }.start();

            for (int i = 0; i < option.length && i < buttonIds.length; i++) option[i].setText("");

            gameModeChosen = gameModeGen.call() % 2;
            // if gameModeChosen equals 0, the game would be all about PRIME NUMBERS
            // then if equals to 1, the game would be all about COMPOSITE NUMBERS

            int correctAnsQuantity = correctAnswersGen.call();
            correctAns = new int[correctAnsQuantity];
            correctAnsIndex = new int[correctAnsQuantity];
            isButtonClicked = new boolean[9];
            Arrays.fill(isButtonClicked, false);

            for(int i = 0; i < correctAnsQuantity; i++) {
                if(gameModeChosen == 0){
                    txtQuestion.setText("Select all prime numbers.");
                    correctAns[i] = primeNum[randomPrimeGen.call()];
                }else{
                    txtQuestion.setText("Select all composite numbers.");
                    correctAns[i] = compositeNum[randomCompositeGen.call()];
                }

                correctAnsIndex[i] = correctAnswersButtonsGen.call();
                option[correctAnsIndex[i]].setText(String.valueOf(correctAns[i]));
            }

            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                if(option[i].getText().equals("")){
                    if(gameModeChosen == 0)option[i].setText(String.valueOf(compositeNum[randomCompositeGen.call()]));
                    else option[i].setText(String.valueOf(primeNum[randomPrimeGen.call()]));
                }
            }

        }
    }

    // CLASS THAT NECESSARY FOR THE GAME
    private static int countMatchingElements(int[] correctAnsIndex, int[] selectedAnsIndex) {
        int count = 0, minus = 0;
        rightAns = 0;

        for (int ansIndex : selectedAnsIndex) {
            boolean match = false;
            for (int index : correctAnsIndex) {
                if (ansIndex == index) {
                    count++;
                    rightAns++;
                    match = true;
                    break;
                }
            }
            if (!match) minus++;
        }

        if(count >= minus) count -= minus;
        else count = 0;

        return count;
    }

    private static int countTrueValues(boolean[] array) {
        int count = 0;
        for (boolean value : array) if (value) count++;
        return count;
    }

    // UPDATE BUTTON APPEARANCE TO RECOGNIZE WHICH ONE IS SELECTED OR NOT
    private void updateButtonAppearance(Button button, boolean isButtonClicked) {
        int backgroundColor, txtColor;
        if(isButtonClicked){
            backgroundColor = R.drawable.custom_button_c;
            txtColor = darkNormal;
        } else{
            backgroundColor = R.drawable.custom_button_nc;
            txtColor = normal;
        }
        button.setBackgroundResource(backgroundColor);
        button.setTextColor(txtColor);
    }

    // SHOW TIME'S UP POP UP
    @SuppressLint("SetTextI18n")
    private void showNoTimer(){
        popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
        popup.setVisibility(View.VISIBLE);
        popup.setBackgroundColor(Color.parseColor("#A6D82618"));
        popPoints.setText("");
        popStatus.setText("");
        popTitle.setText("Time's up!");
        popTitle.setTextColor(Color.parseColor("#AF1C11"));
        continueBtn.setBackgroundResource(R.drawable.custom_button3);
        for (int i : correctAnsIndex) {
            option[i].setBackgroundResource(R.drawable.custom_button4);
            option[i].setTextColor(correct);
        }
        for (int i = 0; i < option.length && i < buttonIds.length; i++) option[i].setClickable(false);
    }

}
