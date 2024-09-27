package com.ytw.tictactoe;

import android.os.CountDownTimer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    private static int time;
    private static CountDownTimer timer;
    private static int CurrentTime(){
        long mNow = System.currentTimeMillis();
        Date date = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("HH");
        return Integer.parseInt(mFormat.format(date));
    }
    public static void Start(int t){
        if(t != -1){
            int hour = CurrentTime() - t;
            if(hour < 0)
                hour += 24;
            if(GameData.heart < 10){
                GameData.heart += hour;
                if(GameData.heart > 10){
                    GameData.heart = 10;
                }
            }
        }
        time = CurrentTime();
        GameData.SaveData();

        timer = new CountDownTimer(60000, 5000) {
            @Override
            public void onTick(long l) {
                if(CurrentTime() != time){
                    if(GameData.heart < 10){
                        GameData.heart++;
                        time = CurrentTime();
                        GameData.SaveData();
                    }
                }
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        };
        timer.start();
    }

    public static int GetTime(){
        return time;
    }
}
