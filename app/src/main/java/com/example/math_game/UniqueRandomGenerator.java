package com.example.math_game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class UniqueRandomGenerator implements Callable<Integer> {

    private final Random random;
    private final int min;
    private final int max;
    private List<Integer> remainingNumbers;

    public UniqueRandomGenerator(int min, int max) {
        this.random = new Random();
        this.min = min;
        this.max = max;
        refillRemainingNumbers();
    }

    private void refillRemainingNumbers() {
        remainingNumbers = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            remainingNumbers.add(i);
        }
        Collections.shuffle(remainingNumbers);
    }

    @Override
    public Integer call(){
        if (remainingNumbers.isEmpty()) {
            refillRemainingNumbers();
        }
        int index = random.nextInt(remainingNumbers.size());
        int value = remainingNumbers.remove(index);
        return value;
    }
}