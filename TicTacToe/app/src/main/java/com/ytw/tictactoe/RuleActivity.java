package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;

public class RuleActivity extends AppCompatActivity {

    int page;

    private SoundPool sound;
    private int button_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_1pg);
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        button_click = sound.load(this, R.raw.button_click, 1);
        page = 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            page++;
            sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
            if(page > 8){
                finish();
            }
            else{
                setContentView(getResources().getIdentifier("activity_rule_"+Integer.toString(page)+"pg","layout",getPackageName()));
            }
        }
        return true;
    }
}