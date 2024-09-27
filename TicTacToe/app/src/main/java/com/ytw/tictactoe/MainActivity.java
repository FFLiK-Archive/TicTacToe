package com.ytw.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Item{
    static public int revival, freeze;

    public static void Init(){
        revival = 0;
        freeze = 0;
    }
    public static void SetText(View revival_t, View freeze_t){
        ((TextView)revival_t).setText(Integer.toString(revival));
        ((TextView)freeze_t).setText(Integer.toString(freeze));
    }
}

class GameData extends AppCompatActivity{
    static public int heart;
    static public int coin;
    static public Item item;
    static public int high_score;
    static public int time;

    static public SharedPreferences preferences;
    static public SharedPreferences.Editor editor;

    static public boolean sound;

    public static void Init(){
        heart = 0;
        coin = 0;
        high_score = 0;
        item.Init();
    }
    public static void LoadData(){
        heart = preferences.getInt("heart", 10);
        coin = preferences.getInt("coin", 0);
        high_score = preferences.getInt("high_score", 0);
        Item.revival = preferences.getInt("i_revival", 3);
        Item.freeze = preferences.getInt("i_freeze", 0);
        sound = preferences.getBoolean("sound", true);
        time = preferences.getInt("time", -1);
        Time.Start(time);
    }
    public static void SaveData(){
        editor.putInt("heart", heart);
        editor.putInt("coin", coin);
        editor.putInt("i_revival", Item.revival);
        editor.putInt("i_freeze", Item.freeze);
        editor.putInt("high_score", high_score);
        editor.putBoolean("sound", sound);
        editor.putInt("time", Time.GetTime());
        editor.commit();
    }
    public static void SetText(View heart_t, View coin_t, View high_score_t){
        ((TextView)heart_t).setText(Integer.toString(heart));
        ((TextView)coin_t).setText(Integer.toString(coin));
        ((TextView)high_score_t).setText(Integer.toString(high_score));
    }
    public static void SetText(View heart_t, View coin_t){
        ((TextView)heart_t).setText(Integer.toString(heart));
        ((TextView)coin_t).setText(Integer.toString(coin));
    }
}

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    Button start_button, store_button, setting_button;

    private AlertDialog dialog;

    static public Context context;

    private SoundPool sound;
    private int button_click;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameData.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        GameData.editor = GameData.preferences.edit();

        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        button_click = sound.load(this, R.raw.button_click, 1);

        GameData.Init();

        GameData.LoadData();

        GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin), findViewById(R.id.high_score));

        if(GameData.time == -1){
            Intent intent= new Intent(getApplicationContext(), RuleActivity.class);
            startActivity(intent);
        }

        start_button = (Button)findViewById(R.id.start_button);
        start_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                if(GameData.heart > 0){
                    GameData.heart--;
                    GameData.SaveData();
                    Intent intent= new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                }
                else {
                    dialog = new AlertDialog.Builder(MainActivity.this) // Pass a reference to your main activity here
                            .setTitle("하트가 부족합니다.")
                            .setMessage("상점에서 구매하실 수 있습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            }
        });

        store_button = (Button)findViewById(R.id.store_button);
        store_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                Intent intent= new Intent(getApplicationContext(), StoreActivity.class);
                startActivity(intent);
            }
        });

        setting_button = (Button)findViewById(R.id.setting_btn);
        setting_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.sound)
                    sound.play(button_click, 1.0F, 1.0F,  0,  0,  1);
                Intent intent= new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        timer = new CountDownTimer(60000, 5000) {
            @Override
            public void onTick(long l) {
                GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin), findViewById(R.id.high_score));
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        }.start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        GameData.SetText(findViewById(R.id.hp), findViewById(R.id.coin), findViewById(R.id.high_score));
    }

    @Override
    protected void onStop(){
        GameData.SaveData();
        super.onStop();
    }
}