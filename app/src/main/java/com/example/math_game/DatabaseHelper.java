package com.example.math_game;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "accDetails";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "userScore";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database if needed
        // This method is called when the database needs to be upgraded, e.g., when you increment DATABASE_VERSION
    }

    public void dbPutScore(int gameNum, int gameScore, int exp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valuesAdd = new ContentValues();
        valuesAdd.put("username", CommonUtils.username);
        valuesAdd.put("gameNo", gameNum);
        valuesAdd.put("scoreGame", gameScore);
        valuesAdd.put("exp", exp);
        db.insert(TABLE_NAME, null, valuesAdd);
        db.close();
    }

    public float[] dbScoreSummary(){
        float[] scoreSummary = new float[15];
        int[] gameNoGames = new int[7];
        for (int i = 0; i < scoreSummary.length; i++) scoreSummary[i] = 0.0f;
        for (int i = 0; i < gameNoGames.length; i++) gameNoGames[i] = 0;

        int exp = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM userScore WHERE username ='" + CommonUtils.username + "'", null);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                int gameNoIndex = (cursor.getInt(2) - 1) * 2;
                int scoreGame = cursor.getInt(3);
                scoreSummary[gameNoIndex + 1] += scoreGame;
                gameNoGames[gameNoIndex / 2]++;
                if (scoreSummary[gameNoIndex] <= scoreGame) scoreSummary[gameNoIndex] = scoreGame;
                exp += cursor.getInt(4);
            } while (cursor.moveToNext());
        }

        for (int i = 0; i < gameNoGames.length; i++) scoreSummary[(i * 2) + 1] /= gameNoGames[i];
        scoreSummary[14] = exp;

        ContentValues valuesAdd = new ContentValues();
        valuesAdd.put("uExp", exp);
        db.update("tableAcc",  valuesAdd, "username = ?", new String[]{CommonUtils.username});
        db.close();
        return scoreSummary;
    }
}