package com.example.math_game;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    public static float masterVolume = 100, musicVolume = 100, sfxVolume = 100;
    public static boolean hasSounds=true;
    private static MediaPlayer mediaPlayer;

    public static void playSound(Context context, int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // If Sound is Disabled
        if (!hasSounds) return;

        //MUSIC:
        //SOUND EFFECTS: dom, gjgj, glike, grats, legend, lvlup, nrec, ntnt, tayms, welcs
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
        // SOUND EFFECTS VOLUME CONTROL
        if (
                soundResourceId == R.raw.dom
                        || soundResourceId == R.raw.gjgj
                        || soundResourceId == R.raw.glike
                        || soundResourceId == R.raw.grats
                        || soundResourceId == R.raw.legend
                        || soundResourceId == R.raw.lvlup
                        || soundResourceId == R.raw.nrec
                        || soundResourceId == R.raw.ntnt
                        || soundResourceId == R.raw.tayms
                        || soundResourceId == R.raw.welcs
        ) {
            mediaPlayer.setVolume(masterVolume * (sfxVolume / 100), masterVolume * (sfxVolume / 100));
        }

        //
        else {
            mediaPlayer.setVolume(masterVolume * (musicVolume / 100), masterVolume * (musicVolume / 100));
        }
        mediaPlayer.start();
    }
}