package com.example.math_game;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {

    private static MediaPlayer mediaPlayer;

    public static void playSound(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        mediaPlayer.start();
    }
}