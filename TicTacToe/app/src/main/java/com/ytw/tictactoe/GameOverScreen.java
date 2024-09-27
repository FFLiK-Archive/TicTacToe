package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverScreen extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    TextView point, coin;
    Button go_back;

    private boolean button_click;

    private CountDownTimer timer;

    private SoundPool sound;
    private int rate, id, button;
    private boolean r = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        rate = sound.load(this, R.raw.point_rate, 1);
        button = sound.load(this, R.raw.button_click, 1);

        overridePendingTransition(R.anim.slide_up, R.anim.slide_not_move);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);

        point = findViewById(R.id.point);
        coin = findViewById(R.id.coin);
        go_back = findViewById(R.id.return_button);
        r = false;

        point.setText("0");

        button_click = false;

        go_back.setEnabled(button_click);

        go_back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button, 1.0F, 1.0F,  0,  0,  1);
                if(GameActivity.point > GameData.high_score){
                    GameData.high_score = GameActivity.point;
                    GameData.SaveData();
                }
                GameActivity.this_activity.finish();
                finish();
            }
        });
        coin.setText(" ");

        timer = new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long l) {
                if(!r){
                    if(GameData.sound)
                        id = sound.play(rate, 1.0F, 1.0F,  0,  0,  1);
                    r = true;
                }
                point.setText(Integer.toString((int)((float)(GameActivity.point * (1000-l)) / 1000.0)));
                if((int)((float)(GameActivity.point * (1000-l)) / 1000.0) == GameActivity.point){
                    button_click = true;
                    go_back.setEnabled(button_click);
                }
            }

            @Override
            public void onFinish() {
                if(GameData.sound)
                    sound.stop(id);
                point.setText(Integer.toString(GameActivity.point));
                int T = GameActivity.point / 5;
                if(GameActivity.point % 5 >= 3){
                    T++;
                }
                coin.setText("+ "+Integer.toString(T)+" T");
                GameData.coin += T;
                button_click = true;
                go_back.setEnabled(button_click);
            }
        };

        timer.start();
    }
}