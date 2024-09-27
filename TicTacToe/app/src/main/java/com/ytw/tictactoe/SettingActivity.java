package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {
    private Button howtoplay, close;
    private Switch sound_switch;

    private SoundPool sound;
    private int button_click;

    @Override
    public void onBackPressed(){
        if(GameData.sound)
            sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        button_click = sound.load(this, R.raw.button_click, 1);

        setContentView(R.layout.activity_setting);
        howtoplay = findViewById(R.id.howtoplay_button);
        close = findViewById(R.id.setting_close_btn);
        sound_switch = findViewById(R.id.sound_switch);

        howtoplay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                Intent intent= new Intent(getApplicationContext(), RuleActivity.class);
                startActivity(intent);
            }
        });

        close.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                finish();
            }
        });

        sound_switch.setChecked(GameData.sound);

        sound_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sound_switch.isChecked()){
                    GameData.sound = true;
                }
                else{
                    GameData.sound = false;
                }
                GameData.SaveData();
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
            }
        });
    }
}