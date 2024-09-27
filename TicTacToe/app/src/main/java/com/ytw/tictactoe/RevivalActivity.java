package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RevivalActivity extends AppCompatActivity {

    public static boolean rev;
    public static boolean run;

    private SoundPool sound;
    private int revival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        revival = sound.load(this, R.raw.revival, 1);

        run = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revival);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_not_move);
        final TextView revival_time = findViewById(R.id.revival_timer);
        rev = false;
        Button use = findViewById(R.id.revival_use_btn);
        Button close = findViewById(R.id.revival_close_btn);

        use.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(revival, 1.0F, 1.0F,  0,  0,  1);
                GameData.item.revival--;
                GameData.SaveData();
                rev = true;
                finish();
            }
        });

        close.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                revival_time.setText(Integer.toString((int)(l / 1000 + 1)));
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
}