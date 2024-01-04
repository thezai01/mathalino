package com.example.math_game;

import static com.example.math_game.CommonUtils.*;
import static com.example.math_game.HomeFragment.*;
import static com.example.math_game.R.color;
import static com.example.math_game.R.drawable;
import static com.example.math_game.R.id;
import static com.example.math_game.R.layout;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class GameNum3 extends AppCompatActivity {

    Handler handle;

    // XML ELEMENTS VARIABLE
    Button[] option = new Button[4];
    Button continueBtn;
    TextView startEquation, lastEquation, questionMark, g3Score, timerNum, popTitle, popPoints, popStatus;
    View popup;
    ProgressBar gameProgress;

    // COUNTDOWN TIMER
    CountDownTimer countdown;

    // ID OF THE BUTTONS
    int[] buttonIds = {id.btnGame3C1, id.btnGame3C2, id.btnGame3C3, id.btnGame3C4};
    // FOR CREATING THE NUMBER SEQUENCE
    int[] patterNum = new int[5];
    // PATTERN FOR THE GAME NUMBER SEQUENCE
    int[] resultPattern = new int[5];
    // the gameMode variable is for selecting which type of sequence is gonna be use
    // the correctAnsIndex variable is for the index of the correct answer obviously
    int gameMode, correctAnsIndex;
    // VARIABLES FOR THE GAME MECHANICS
    // ITEM NUM IS WHAT ITEM HAVE ALREADY SHOW
    // OTHER VARIABLES ARE SELF EXPLANATORY
    int  itemNum = -1, correctAns, winstreak = 0, points = 0;
    String gameModeStr;

    // RANDOM NUMBER GENERATOR FOR NUMBER SEQUENCE
    UniqueRandomGenerator numPatterGen, arithGen, geoGen, geoNumGen, triGen, quadGen, missingPosition, gameModeGen;
    TextView txtSolution,txtSequence;
    String[] sequences = {"Fibbonacci Sequence", "Arithmetic Sequence", "Geometric Sequence", "Triangular Sequence", "Quadratic Sequence"};
    Spanned solution;
    // RANDOM NUMBER GENERATOR FOR ORDER OF OPERATION
    UniqueRandomGenerator operatorGen, operandGen, startIndexGen, dividendGen, divisorGen, parenthesisGen, missingValue;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_game_num3);

        dbHelper = new DatabaseHelper(this);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        gameModeStr = intent.get().getStringExtra("game");

        handle = new Handler();

        ImageView exit = findViewById(id.game3_exit);
        exit.setOnClickListener(v ->{
            intent.set(new Intent(this, Navigation.class));
            startActivity(intent.get());
            handle.postDelayed(this::finish, 0);
        });

        gameProgress = findViewById(id.game3ProgressBar);
        timerNum = findViewById(id.txtGame3Timer);

        startEquation = findViewById(id.txtG3FirstQ);
        lastEquation = findViewById(id.txtG3SecondQ);
        questionMark = findViewById(id.txtG3QMissing);

        for (int i = 0; i < option.length && i < buttonIds.length; i++) {
            option[i] = findViewById(buttonIds[i]);
            option[i].setOnClickListener(buttonAns);
        }

        txtSequence = findViewById(R.id.txtGame3Sequence);
        txtSolution = findViewById(R.id.txtGame3Solution);

        g3Score = findViewById(id.txtG3Score);

        continueBtn = findViewById(id.continueButton);
        continueBtn.setOnClickListener(buttonAns);
        popup = findViewById(id.popup);
        popTitle = findViewById(id.popTitle);
        popPoints = findViewById(R.id.popPoints);
        popStatus = findViewById(R.id.popStatus);

        txtSolution.setVisibility(View.INVISIBLE);
        if(gameModeStr.equals("game2")){
            operatorGen = new UniqueRandomGenerator(1, 4);
            startIndexGen = new UniqueRandomGenerator(0, 2);
            operandGen = new UniqueRandomGenerator(1, 9);
            dividendGen = new UniqueRandomGenerator(2, 4);
            divisorGen = new UniqueRandomGenerator(2, 4);
            parenthesisGen = new UniqueRandomGenerator(1, 2);
            missingValue = new UniqueRandomGenerator(0, 4);
            gameNum2();
        }else {
            numPatterGen = new UniqueRandomGenerator(1, 9);
            arithGen = new UniqueRandomGenerator(1, 10);
            geoNumGen = new UniqueRandomGenerator(1, 3);
            geoGen = new UniqueRandomGenerator(2, 3);
            triGen = new UniqueRandomGenerator(1, 5);
            quadGen = new UniqueRandomGenerator(1, 5);
            missingPosition = new UniqueRandomGenerator(1, 5);
            gameModeGen = new UniqueRandomGenerator(1, 5);
            questionMark.setText("?");
            gameNum3();
        }

    }

    @SuppressLint({"SetTextI18n"})
    View.OnClickListener buttonAns = view -> {

        countdown.cancel();
        int timeRemaining = Integer.parseInt((String) timerNum.getText()), addPoints;

        if(view.getId()== id.btnGame3C1 || view.getId()== id.btnGame3C2 || view.getId()== id.btnGame3C3 || view.getId()== id.btnGame3C4) {

            int answer = 0;
            for (int i = 0; i < option.length && i < buttonIds.length; i++){
                if (view.getId() == buttonIds[i]) answer = Integer.parseInt(option[i].getText().toString());
                option[i].setClickable(false);
            }

            if(gameModeStr.equals("game3")){
                txtSequence.setText(solution);
            }

            popup.startAnimation(loadSlideUpAnimation(getApplicationContext()));
            popup.setVisibility(View.VISIBLE);
            txtSolution.setVisibility(View.VISIBLE);
            popPoints.setText("");
            popStatus.setText("");
            if (correctAns == answer) {
                popup.setBackgroundColor(Color.parseColor("#A84CAF50"));
                popTitle.setText("Good Job!");
                popTitle.setTextColor(Color.parseColor("#2B812E"));
                continueBtn.setBackgroundResource(drawable.custom_button2);
                winstreak++;
                addPoints = (timeRemaining / 10) + 1;
                if(winstreak >= 3) {
                    addPoints += winstreak - 2;
                    popStatus.setText("x"+ winstreak +" combo!");
                }
                popPoints.setText("+" + addPoints + " pts");
                points+= addPoints;
                g3Score.setText(String.valueOf(points));
            } else {
                popup.setBackgroundColor(Color.parseColor("#A6D82618"));
                popTitle.setText("Wrong Answer!");
                popTitle.setTextColor(Color.parseColor("#AF1C11"));
                continueBtn.setBackgroundResource(drawable.custom_button3);
                for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                   if(buttonIds[i] == view.getId()) {
                       option[i].setBackgroundResource(drawable.custom_button5);
                       option[i].setTextColor(wrong);
                   }
                }
                winstreak = 0;
            }
            option[correctAnsIndex-1].setBackgroundResource(drawable.custom_button4);
            option[correctAnsIndex-1].setTextColor(correct);
        }else if(view.getId()== id.continueButton){
            popup.startAnimation(loadSlideDownAnimation(getApplicationContext()));
            popup.setVisibility(View.INVISIBLE);
            txtSolution.setVisibility(View.INVISIBLE);
            txtSequence.setText("");
            if(gameModeStr.equals("game2")) handle.postDelayed(this::gameNum2, 800);
            else handle.postDelayed(this::gameNum3, 800);
            for (int i = 0; i < option.length && i < buttonIds.length; i++) {
                option[i].setBackgroundResource(drawable.custom_button1);
                option[i].setTextColor(normal);
                option[i].setClickable(true);
            }
        }

    };

    // ALL CLASSES AND FUNCTION FOR GAME 3
    @SuppressLint("SetTextI18n")
    private void gameNum3() {
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(3, points, points/2);
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

            countdown= new CountDownTimer(31000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    timerNum.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    if(gameModeStr.equals("game3")){
                        txtSequence.setText(sequences[gameMode-1]);
                    }
                    winstreak = 0;
                    txtSolution.setVisibility(View.VISIBLE);
                    CommonUtils.showNoTimer(popup, popTitle, popPoints, popStatus, continueBtn, option, buttonIds, correctAnsIndex-1, loadSlideUpAnimation(getApplicationContext()));

                }
            }.start();

            try {
                gameMode = gameModeGen.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            switch (gameMode) {
                case 1 -> resultPattern = fibonacci();
                case 2 -> resultPattern = arithmetic();
                case 3 -> resultPattern = geometric();
                case 4 -> resultPattern = triangular();
                case 5 -> resultPattern = quadratic();
            }

            int misNum;
            try {misNum = missingPosition.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            StringBuilder equation = new StringBuilder();
        for(int x = 0; x < misNum - 1; x++){
            equation.append(resultPattern[x]).append(" , ");
        }
        startEquation.setText(equation.toString());
        equation = new StringBuilder();
        for(int x = misNum; x < 5; x++){
            equation.append(" , ").append(resultPattern[x]);
        }
        lastEquation.setText(equation.toString());

        correctAns = resultPattern[misNum - 1];
        correctAnsIndex = CommonUtils.buttonChoicesGen(correctAns, option);

        }
    }
    private int[] fibonacci() {

        try {
            patterNum[0] = numPatterGen.call();
            patterNum[1] = patterNum[0] + numPatterGen.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int x = 2; x < 5; x++) patterNum[x] = patterNum[x - 1] + patterNum[x - 2];
        solution = Html.fromHtml("F<sub>n</sub> = F<sub>n-1</sub> + F<sub>n-2</sub>");
        return patterNum;
    }

    private int[] arithmetic() {
        int pattern;
        try {
            pattern = arithGen.call();
            patterNum[0] = numPatterGen.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int x = 1; x < 5; x++) patterNum[x] = patterNum[x-1] + pattern;
        solution = Html.fromHtml("F<sub>n</sub> = F<sub>n-1</sub> + "+pattern);
        return patterNum;
    }

    private int[] geometric() {
        int pattern;
        try {
            pattern = geoGen.call();
            patterNum[0] = geoNumGen.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int x = 1; x < 5; x++) patterNum[x] = patterNum[x - 1] * pattern;
        solution = Html.fromHtml("F<sub>n</sub> = F<sub>n-1</sub> x "+pattern);
        return patterNum;
    }

    private int[] triangular() {
        int pattern;
        try {
            pattern = triGen.call();
            patterNum[0] = numPatterGen.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int x = 1; x < 5; x++) patterNum[x] = patterNum[x - 1] + (x - 1 + pattern);
        solution = Html.fromHtml("F<sub>n</sub> = F<sub>n-1</sub> + (n + "+pattern+")");
        return patterNum;
    }

    private int[] quadratic() {
        int pattern;
        try {
            pattern = quadGen.call();
            patterNum[0] = numPatterGen.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int patternPlus =  pattern;
        for (int x = 1; x < 5; x++){
            patternPlus += pattern;
            patterNum[x] = patterNum[x - 1] + patternPlus;
        }
        solution = Html.fromHtml("F<sub>n</sub> = F<sub>n-1</sub> + (n x "+pattern+")");
        return patterNum;
    }

    // ALL CLASSES AND FUNCTION FOR GAME 2
    private void gameNum2(){
        itemNum++;
        if(itemNum>=10){
            handle.postDelayed(() -> {
                dbHelper.dbPutScore(2, points, points/2);
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

            countdown = new CountDownTimer(46000, 1000) {
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

            int numberOfOperands = 4;
            StringBuilder equationGenerated = new StringBuilder();
            startEquation.setText("");
            lastEquation.setText("");
            questionMark.setText("");

            // ADD RANDOM OPERATORS
            List<Character> operators = generateRandomOperators(numberOfOperands - 1);
            // ADD RANDOM OPERANDS
            List<Integer> operands = generateRandomOperands(operators, numberOfOperands);
            // ADD RANDOM PARENTHESIS
            addRandomParentheses(operators);
            List<Character> solveOperators = new ArrayList<>(operators);
            List<Integer> solveOperands = new ArrayList<>(operands);

            // SOLVING THE EQUATION INSIDE THE PARENTHESIS
            int openParenthesisIndex = solveOperators.indexOf('(');
            int closeParenthesisIndex = solveOperators.indexOf(')');
            int operandsInsideParenthesis = closeParenthesisIndex - openParenthesisIndex - 1;

            List<Integer> subOperands = solveOperands.subList(openParenthesisIndex, (openParenthesisIndex + operandsInsideParenthesis + 1));
            List<Character> subOperators = solveOperators.subList(openParenthesisIndex + 1, closeParenthesisIndex);

            int subResult = calculateResult(subOperands, subOperators);
            if (closeParenthesisIndex > openParenthesisIndex)
                solveOperators.subList(openParenthesisIndex, closeParenthesisIndex).clear();
            if (openParenthesisIndex + operandsInsideParenthesis > openParenthesisIndex)
                solveOperands.subList(openParenthesisIndex, openParenthesisIndex + operandsInsideParenthesis).clear();
            // ADDING THE RESULT OF THE EQUATION INSIDE THE PARENTHESIS TO THE MAIN EQUATION
            solveOperands.add(openParenthesisIndex, subResult);

            // FINALIZING THE RESULT
            int result = calculateResult(solveOperands, solveOperators);
            operands.add(result);

            // ADDING ALL TO CREATE AN EQUATION
            String operand;
            int operandCount = 0, afterParenthesis = 0;
            int missingVal = missingValue.call();
            correctAns = operands.get(missingVal);
            for (int i = 0; i < operators.size(); i++) {
                operand = String.valueOf(operands.get(operandCount));
                if (missingVal == operandCount) operand = "?";

                if (operators.get(i).equals('(')) equationGenerated.append(operators.get(i));
                else if (operators.get(i).equals(')')) {
                    equationGenerated.append(operand).append(operators.get(i)).append(" ");
                    afterParenthesis = 1;
                    operandCount++;
                } else if (afterParenthesis == 1) {
                    equationGenerated.append(operators.get(i)).append(" ").append(operand).append(" ");
                    operandCount++;
                } else {
                    equationGenerated.append(operand).append(" ").append(operators.get(i)).append(" ");
                    operandCount++;
                }
            }

            operand = String.valueOf(operands.get(4));
            if (missingVal == 4) operand = "?";
            equationGenerated.append("= ").append(operand);

            SpannableString spannableString = new SpannableString(equationGenerated.toString());
            int iQMark = equationGenerated.toString().indexOf('?');

            questionMark.setTextColor(getResources().getColor(color.seekbar_bg, getTheme()));
            spannableString.setSpan(new ForegroundColorSpan(errorColor), iQMark, iQMark + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionMark.setText(spannableString);
            correctAnsIndex = CommonUtils.buttonChoicesGen(correctAns, option);
        }

    }
    // GENERATING RANDOM OPERANDS FOR THE EQUATION
    private List<Integer> generateRandomOperands(List<Character> operators, int numberOfOperands) {
        List<Integer> operands = new ArrayList<>();
        int divisor = divisorGen.call();
        int dividend = divisor * dividendGen.call();
        for (int i = 0; i < numberOfOperands; i++) {
                if( i >= 1 && operators.get(i - 1).equals('÷')){
                    operands.add(divisor);
                    operands.set(i - 1, dividend);
                }
                else operands.add(operandGen.call());
        }
        return operands;
    }
    // GENERATING RANDOM OPERATORS FOR THE EQUATION
    private List<Character> generateRandomOperators(int numberOfOperators) {
        char[] operators = {'+', '-', '*', '÷'};
        List<Character> operatorList = new ArrayList<>();
        for (int i = 0; i < numberOfOperators; i++) operatorList.add(operators[operatorGen.call()-1]);
        return operatorList;
    }
    // ADDING A RANDOM PARENTHESIS IN THE EQUATION
    private void addRandomParentheses(List<Character> operators) {
        int startIndex = startIndexGen.call();

        if(operators.contains('÷')){
            if(operators.indexOf('÷') == 1) startIndex = 1;
            else{
                int ran = parenthesisGen.call();
                if(ran == 1) startIndex = 0;
                else if(ran == 2) startIndex = 2;
            }
        }
        int endIndex = startIndex + 1;

        operators.add(startIndex, '(');
        operators.add(endIndex + 1, ')');
    }
    // SOLVING THE EQUATION TO FIND THE RESULT
    private int calculateResult(List<Integer> operands, List<Character> operators) {

        // Apply PEMDAS rule: First handle multiplication and division
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            if (operator == '*' || operator == '÷') {
                int operand1 = operands.get(i);
                int operand2 = operands.get(i + 1);

                int result = applyOperator(operand1, operand2, operator);

                // Update operands and operators lists
                operands.set(i, result);
                operands.remove(i + 1);
                operators.remove(i);

                i--;
            }
        }

        // Apply PEMDAS rule: Then handle addition and subtraction
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            int operand1 = operands.get(i);
            int operand2 = operands.get(i + 1);

            int result = applyOperator(operand1, operand2, operator);

            // Update operands and operators lists
            operands.set(i, result);
            operands.remove(i + 1);
            operators.remove(i);

            i--;
        }

        return operands.get(0);
    }

    // FOR THE OPERATOR
    private static int applyOperator(int operand1, int operand2, char operator) {
        switch (operator) {
            case '+' -> {
                return operand1 + operand2;
            }
            case '-' -> {
                return operand1 - operand2;
            }
            case '*' -> {
                return operand1 * operand2;
            }
            case '÷' -> {
                return operand1 / operand2;
            }
            default -> {
                return 0;
            }
        }
    }

}




