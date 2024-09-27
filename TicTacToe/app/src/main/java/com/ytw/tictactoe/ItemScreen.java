package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ItemScreen extends AppCompatActivity {

    private SoundPool sound;
    private int button;

    @Override
    public void onBackPressed(){
        if(GameData.sound)
            sound.play(button, 1.0F, 1.0F,  0,  0,  1);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        button = sound.load(this, R.raw.button_click, 1);

        overridePendingTransition(R.anim.slide_up, R.anim.slide_not_move);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_screen);

        Button close_btn = findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button, 1.0F, 1.0F,  0,  0,  1);
                finish();
            }
        });
    }

    @Override
    protected  void onStart(){
        super.onStart();
        Item.SetText(findViewById(R.id.item_1_show),
                findViewById(R.id.item_2_show));
    }
}