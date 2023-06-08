package com.example.math_game;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    TextView num1, num2, num3, sign1, popTitle;
    ImageView lineNum1, lineNum2, lineNum3, lineSign;
    Button[] option = new Button[4];
    Button continuebtn;
    ProgressBar gameProgress;

    SecureRandom rng = new SecureRandom();

    Animation slideUp, slideDown;
    Handler handle;

    int lvl, itemNum = -1;
    int missingNum, correctAns, ranNum1, ranNum2, ranNum3, ranCorrectAns,ranSign;
    int[] ranPossibleAns = new int[3];
    String username;
    String[] ranSign1={"+","-","*","/"};

    View popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        handle = new Handler();

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        popup = findViewById(R.id.popup);

        Intent intent = getIntent();
        lvl = intent.getIntExtra("lvl", 0);
        username = intent.getStringExtra("username");

        num1 = findViewById(R.id.numRandom1);
        num2 = findViewById(R.id.numRandom2);
        num3 = findViewById(R.id.numRandom3);
        sign1 = findViewById(R.id.sign1);
        lineNum1 = findViewById(R.id.underline1);
        lineNum2 = findViewById(R.id.underline2);
        lineNum3 = findViewById(R.id.underline3);
        lineSign = findViewById(R.id.underlineSign);
        option[0] = findViewById(R.id.option1);
        option[1] = findViewById(R.id.option2);
        option[2] = findViewById(R.id.option3);
        option[3] = findViewById(R.id.option4);
        continuebtn = findViewById(R.id.continueButton);
        option[0].setOnClickListener(buttonAnswers);
        option[1].setOnClickListener(buttonAnswers);
        option[2].setOnClickListener(buttonAnswers);
        option[3].setOnClickListener(buttonAnswers);
        continuebtn.setOnClickListener(buttonAnswers);
        popTitle = findViewById(R.id.popTitle);

        gameProgress = findViewById(R.id.gameProgressBar);

        game();
    }

    private void game(){
        itemNum++;
        if(itemNum>=5){

            handle.postDelayed(() -> {
                Intent intent = new Intent(GameActivity.this, HomePage.class);
                intent.putExtra("lvl",lvl);
                intent.putExtra("score",0);
                intent.putExtra("username",username);
                startActivity(intent);
                handle.postDelayed(this::finish, 0);
            }, 800);

        }

        ObjectAnimator.ofInt(gameProgress, "Progress", 20*(itemNum))
                .setDuration(500)
                .start();

        if(itemNum<=4) {
            missingNum = randomizer(3);
            lineSign.setVisibility(View.INVISIBLE);
            lineNum1.setVisibility(View.INVISIBLE);
            lineNum2.setVisibility(View.INVISIBLE);
            lineNum3.setVisibility(View.INVISIBLE);
            switch (missingNum) {
                case 0:
                    ranNum2 = 2 + randomizer(14);
                    ranNum3 = 2 + randomizer(14);
                    lineNum1.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ranNum1 = 2 + randomizer(14);
                    ranNum3 = 2 + randomizer(14);
                    lineNum2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ranNum1 = 2 + randomizer(14);
                    ranNum2 = 2 + randomizer(14);
                    lineNum3.setVisibility(View.VISIBLE);
                    break;
            }

            int[] fixVal1;
            if (lvl == 1) {
                ranSign = randomizer(2);
                if (ranSign == 0 && missingNum == 0) {
                    fixVal1 = fixVal(ranNum3, ranNum2);
                    ranNum3 = fixVal1[0];
                    ranNum2 = fixVal1[1];
                    correctAns = ranNum3 - ranNum2;
                }
                else if (ranSign == 0 && missingNum == 1) {
                    fixVal1 = fixVal(ranNum3, ranNum1);
                    ranNum3 = fixVal1[0];
                    ranNum1 = fixVal1[1];
                    correctAns = ranNum3 - ranNum1;
                }
                else if (ranSign == 0 && missingNum == 2) correctAns = ranNum1 + ranNum2;
                else if (ranSign == 1 && missingNum == 0) correctAns = ranNum3 + ranNum2;
                else if (ranSign == 1 && missingNum == 1) {
                    fixVal1 = fixVal(ranNum1, ranNum3);
                    ranNum1 = fixVal1[0];
                    ranNum3 = fixVal1[1];
                    correctAns = ranNum1 - ranNum3;
                }
                else if (ranSign == 1 && missingNum == 2) {
                    fixVal1 = fixVal(ranNum1, ranNum2);
                    ranNum1 = fixVal1[0];
                    ranNum2 = fixVal1[1];
                    correctAns = ranNum1 - ranNum2;
                }

            } else if (lvl == 2) {
                ranSign = 2 + randomizer(2);
                if (ranSign == 3) {

                    if (missingNum == 1) {

                        do {
                            ranNum1 = (randomizer(30) + 1);
                            ranNum3 = (randomizer(30) + 1);
                        } while ((ranNum3 > ranNum1) || (!isDivisible(ranNum1, ranNum3)));
                    } else if (missingNum == 2) {

                        do {
                            ranNum1 = (randomizer(30) + 1);
                            ranNum2 = (randomizer(30) + 1);
                        } while ((ranNum2 > ranNum1) || (!isDivisible(ranNum1, ranNum2)));
                    }

                    if (missingNum == 0) correctAns = ranNum2 * ranNum3;
                    else if (missingNum == 1) correctAns = ranNum1 / ranNum3;
                    else if (missingNum == 2) correctAns = ranNum1 / ranNum2;

                } else if (ranSign == 2) {
                    if (missingNum == 0) {
                        do {
                            ranNum2 =  (randomizer(30) + 1);
                            ranNum3 = (randomizer(30) + 1);
                        } while ((ranNum2 > ranNum3) || (!isDivisible(ranNum3, ranNum2)));
                    } else if (missingNum == 1) {
                        do {
                            ranNum1 =  (randomizer(30) + 1);
                            ranNum3 = (randomizer(30) + 1);
                        } while ((ranNum1 > ranNum3) || (!isDivisible(ranNum3, ranNum1)));
                    }

                    if (missingNum == 0) correctAns = ranNum3 / ranNum2;
                    else if (missingNum == 1) correctAns = ranNum3 / ranNum1;
                    else if (missingNum == 2) correctAns = ranNum1 * ranNum2;
                }

            }

            num1.setText(String.valueOf(ranNum1));
            num2.setText(String.valueOf(ranNum2));
            num3.setText(String.valueOf(ranNum3));

            if (missingNum == 0)  num1.setText("");
            else if (missingNum == 1)  num2.setText("");
            else if (missingNum == 2)  num3.setText("");

            sign1.setText(ranSign1[ranSign]);

            // FOR BUTTONS
            ranCorrectAns = randomizer(4);
            ranPossibleAns = randomPossibleNum(correctAns + 10, correctAns);

            int counter = 0;
            for (int x = 0; x < 4; x++) {
                if (x == ranCorrectAns) option[x].setText(String.valueOf(correctAns));
                else {
                    option[x].setText(String.valueOf(ranPossibleAns[counter]));
                    counter++;
                }
            }

        }

    }

    public int randomizer(int max){
        return rng.nextInt(max);
    }

    public boolean isDivisible(int number, int divisor) {
        return number % divisor == 0;
    }

    public int[] fixVal(int num1, int num2){
        int[] fixValue = new int[2];
        do {
            num2 = 1 + randomizer(20) ;
            num1 = 1 + randomizer(20) ;
        } while (num2 > num1);

        fixValue[0] = num1;
        fixValue[1] = num2;

        return fixValue;
    }

    public int[] randomPossibleNum(int max, int except){

        Set<Integer> randomNumbers = new LinkedHashSet<>();

        while (randomNumbers.size() < 3) {
            int next = rng.nextInt(max);
            if(next != except) randomNumbers.add(next);

        }

        int[] generated = new int[3];
        int counter = 0;
        for(int ele:randomNumbers) generated[counter++] = ele;

        return generated;
    }

    View.OnClickListener buttonAnswers = view -> {
        String answer="";

        if(view.getId()==R.id.option1 || view.getId()==R.id.option2 || view.getId()==R.id.option3 || view.getId()==R.id.option4){
            for(int x=0; x<4; x++){
                option[x].setClickable(false);
            }
            popup.startAnimation(slideUp);
            popup.setVisibility(View.VISIBLE);
            if (missingNum == 0)  num1.setText(String.valueOf(correctAns));
            else if (missingNum == 1)  num2.setText(String.valueOf(correctAns));
            else if (missingNum == 2)  num3.setText(String.valueOf(correctAns));

            if(view.getId()==R.id.option1) answer=option[0].getText().toString();
            else if(view.getId()==R.id.option2) answer=option[1].getText().toString();
            else if(view.getId()==R.id.option3) answer=option[2].getText().toString();
            else if(view.getId()==R.id.option4) answer=option[3].getText().toString();

            if(correctAns==Integer.parseInt(answer)){
                popup.setBackgroundColor(Color.parseColor("#A84CAF50"));
                popTitle.setText("Good Job!");
                popTitle.setTextColor(Color.parseColor("#2B812E"));
                continuebtn.setBackgroundResource(R.drawable.custom_button2);
            }else{
                popup.setBackgroundColor(Color.parseColor("#A6D82618"));
                popTitle.setText("Wrong Answer!");
                popTitle.setTextColor(Color.parseColor("#AF1C11"));
                continuebtn.setBackgroundResource(R.drawable.custom_button3);
            }

        }

        if(view.getId()==R.id.continueButton){
            popup.startAnimation(slideDown);
            popup.setVisibility(View.INVISIBLE);
            handle.postDelayed(this::game, 800);
            for(int x=0; x<4; x++){
                option[x].setClickable(true);
            }
        }


    };

}