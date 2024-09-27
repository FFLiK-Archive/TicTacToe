package com.ytw.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import hari.floatingtoast.FloatingToast;

public class GameActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
    }

    private Button[][] buttons = new Button[3][3];
    private Button freeze_btn;

    public static GameActivity this_activity;

    private boolean game_end;
    static public int stage, point;
    private TextView text_view_stage;
    private TextView text_view_point;
    //private TextView debug;
    private LinearLayout time_bar_space;
    private CountDownTimer timer, freeze_timer;
    private Rect time_bar;
    private double time;
    private int change_term;
    private int term_cnt;
    private pair button_pos;
    private FloatingToast toast;
    private TextView msg;
    private int w;
    private boolean checking = false;
    private boolean freeze = false;
    private boolean playing = false;
    private SoundPool sound;
    private int o3_eff, ox_eff, freeze_eff;

    void GameEnd() {
        game_end = true;
        SetNewTimer(2);
        timer.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        o3_eff = sound.load(this, R.raw.o3, 1);
        ox_eff = sound.load(this, R.raw.pop, 1);
        freeze_eff = sound.load(this, R.raw.freeze, 1);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        w = size.x;

        this_activity = this;

        text_view_stage = findViewById(R.id.text_view_stage);
        text_view_point = findViewById(R.id.text_view_point);
        //debug = findViewById(R.id.debug);
        time_bar_space = findViewById(R.id.time_bar_space);
        time_bar = new Rect();
        freeze_btn = findViewById(R.id.freeze_use_btn);

        stage = 1;
        point = 0;
        text_view_stage.setText("stage " + stage);
        text_view_point.setText(point + "");
        time_bar.r = findViewById(R.id.time_bar);
        time = 9500;
        change_term = 1;
        term_cnt = 0;
        button_pos = new pair(0, 0);

        freeze_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(GameData.item.freeze > 0){
                    if(!freeze){
                        if(GameData.sound)
                            sound.play(freeze_eff, 1.0F, 1.0F,  0,  0,  1);
                        GameData.item.freeze--;
                        GameData.SaveData();
                        freeze = true;
                        freeze_timer.start();
                    }
                }
                else{
                    Toast.makeText(GameActivity.this, "아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        freeze_timer = new CountDownTimer(10000, 10) {
            @Override
            public void onTick(long l) {
                time_bar.setSize((int) (w * l / (double)time), time_bar.getHeight());
                time_bar.r.setBackgroundColor(Color.parseColor("#99fdff"));
            }

            @Override
            public void onFinish() {
                freeze = false;
                time_bar.setSize(0, time_bar.getHeight());
                if(playing == true)
                    timer.start();
            }
        };

        SetNewTimer(1);

        timer.start();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String id = "button_" + i + j;
                buttons[i][j] = (Button) findViewById(getResources().getIdentifier(id, "id", getPackageName()));
                buttons[i][j].setOnClickListener(new Button.OnClickListener() {
                        @Override
                    public void onClick(View view) {
                        if (!checking && !game_end && ((Button) view).getText().toString().equals("")) {
                            checking = true;
                            ((Button) view).setText("O");
                            timer.cancel();
                            int ret = Check();
                            pair[] pos;
                            int s = view.getId();
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 3; j++) {
                                    String id = "button_" + i + j;
                                    if (getResources().getIdentifier(id, "id", getPackageName()) == s) {
                                        button_pos.x = i;
                                        button_pos.y = j;
                                    }
                                }
                            }
                            int point_plus;
                            switch (ret) {
                                case 3:
                                    if(GameData.sound)
                                        sound.play(o3_eff, 1.0F, 1.0F,  0,  0,  1);
                                    point_plus = 3 + 3 * time_bar.getWidth() / w + 1;
                                    if(freeze)
                                        point_plus = 6;
                                    FloatingToast.makeToast(GameActivity.this, "+" + point_plus, FloatingToast.LENGTH_SHORT)
                                            .setGravity(FloatingToast.GRAVITY_BOTTOM)
                                            .setFadeOutDuration(FloatingToast.FADE_DURATION_LONG)
                                            .setTextSizeInDp(36)
                                            .setBackgroundBlur(true)
                                            .setFloatDistance(80)
                                            .setTextColor(Color.parseColor("#abff88"))
                                            .setShadowLayer(100, 0, 0, Color.parseColor("#000000"))
                                            .show();
                                    pos = GetPositions(button_pos.x, button_pos.y, ret);
                                    point += point_plus;
                                    if (pos[0].x != -1) {
                                        buttons[pos[0].x][pos[0].y].setTextColor(Color.parseColor("#a7ff8c"));
                                        buttons[pos[1].x][pos[1].y].setTextColor(Color.parseColor("#a7ff8c"));
                                        buttons[pos[2].x][pos[2].y].setTextColor(Color.parseColor("#a7ff8c"));
                                    }
                                    break;
                                case 1:
                                    if(GameData.sound)
                                        sound.play(ox_eff, 1.0F, 1.0F,  0,  0,  1);
                                    pos = GetPositions(button_pos.x, button_pos.y, ret);
                                    if (pos[0].x != -1) {
                                        buttons[pos[0].x][pos[0].y].setText("X");
                                        buttons[pos[1].x][pos[1].y].setText("X");
                                        buttons[pos[2].x][pos[2].y].setText("X");
                                        buttons[pos[0].x][pos[0].y].setTextColor(Color.parseColor("#ff8c8c"));
                                        buttons[pos[1].x][pos[1].y].setTextColor(Color.parseColor("#ff8c8c"));
                                        buttons[pos[2].x][pos[2].y].setTextColor(Color.parseColor("#ff8c8c"));
                                    }
                                    GameEnd();
                                    break;
                                case 0:
                                    if(GameData.sound)
                                        sound.play(ox_eff, 1.0F, 1.0F,  0,  0,  1);
                                    point_plus = 3 * time_bar.getWidth() / w + 1;
                                    if(freeze)
                                        point_plus = 3;
                                    ///*
                                    FloatingToast.makeToast(GameActivity.this, "+" + point_plus, FloatingToast.LENGTH_SHORT)
                                            .setGravity(FloatingToast.GRAVITY_BOTTOM)
                                            .setFadeOutDuration(FloatingToast.FADE_DURATION_LONG)
                                            .setTextSizeInDp(36)
                                            .setBackgroundBlur(true)
                                            .setFloatDistance(80)
                                            .setTextColor(Color.parseColor("#cfffb9"))
                                            .setShadowLayer(100, 0, 0, Color.parseColor("#000000"))
                                            .show();
                                    point += point_plus;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    }, 500);
                                    //*/
                                    pos = GetPositions(button_pos.x, button_pos.y, ret);
                                    if (pos[0].x != -1) {
                                        buttons[pos[0].x][pos[0].y].setTextColor(Color.parseColor("#ff8c8c"));
                                        buttons[pos[1].x][pos[1].y].setTextColor(Color.parseColor("#ff8c8c"));
                                        buttons[pos[2].x][pos[2].y].setTextColor(Color.parseColor("#ff8c8c"));
                                        buttons[button_pos.x][button_pos.y].setTextColor(Color.parseColor("#a7ff8c"));
                                    }
                                    break;
                            }

                            if (!game_end) {
                                time *= 0.994;
                                //f: y = 9500 * 0.994^x + 500
                                //debug.setText(Integer.toString((int)time + 500));
                                stage++;
                                text_view_stage.setText("stage " + stage);
                                text_view_point.setText(point + "");
                                SetNewTimer(0);
                                timer.start();
                            }
                        }
                    }
                });
            }
        }

        SetRandomMap();
    }

    private int Check() {
        int board[][] = new int[3][3];

        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                if(buttons[i][j].getText().toString().equals("X"))
                    board[i][j] = -1;
                else if(buttons[i][j].getText().toString().equals("O"))
                    board[i][j] = 1;
                else
                    board[i][j] = 0;
            }
        }
        int x_cnt = 0;
        int o_cnt = 0;

        for(int i = 0; i < 3; i++){
            switch (board[i][0] + board[i][1] + board[i][2]){
                case -2 :
                    x_cnt++;
                    break;
                case -3 :
                    x_cnt+=2;
                    break;
                case 3:
                    o_cnt++;
                    break;
            }

            switch (board[0][i] + board[1][i] + board[2][i]){
                case -2 :
                    x_cnt++;
                    break;
                case -3 :
                    x_cnt+=2;
                    break;
                case 3:
                    o_cnt++;
                    break;
            }
        }

        switch (board[0][0] + board[1][1] + board[2][2]){
            case -2 :
                x_cnt++;
                break;
            case -3 :
                x_cnt+=2;
                break;
            case 3:
                o_cnt++;
                break;
        }

        switch (board[2][0] + board[1][1] + board[0][2]){
            case -2 :
                x_cnt++;
                break;
            case -3 :
                x_cnt+=2;
                break;
            case 3:
                o_cnt++;
                break;
        }

        if(o_cnt != 0)
            return 3;
        if(x_cnt >= 2)
            return 2;
        if(x_cnt == 1)
            return 1;
        return 0;
    }



    private pair[] GetPositions(int x, int y, int ret) {
        int board[][] = new int[3][3];
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                if(buttons[i][j].getText().toString().equals("X"))
                    board[i][j] = -1;
                else if(buttons[i][j].getText().toString().equals("O"))
                    board[i][j] = 3;
                else
                    board[i][j] = 0;
            }
        }

        pair[] pos = new pair[3];
        if(ret == 1){
            for(int i = 0; i < 3; i++){
                if (board[i][0] + board[i][1] + board[i][2] == -2){
                    pos[0] = new pair(i,0);
                    pos[1] = new pair(i,1);
                    pos[2] = new pair(i,2);
                    return pos;
                }

                if (board[0][i] + board[1][i] + board[2][i] == -2){
                    pos[0] = new pair(0,i);
                    pos[1] = new pair(1,i);
                    pos[2] = new pair(2,i);
                    return pos;
                }
            }

            if (board[0][0] + board[1][1] + board[2][2] == -2){
                pos[0] = new pair(0,0);
                pos[1] = new pair(1,1);
                pos[2] = new pair(2,2);
                return pos;
            }

            if (board[2][0] + board[1][1] + board[0][2] == -2){
                pos[0] = new pair(2,0);
                pos[1] = new pair(1,1);
                pos[2] = new pair(0,2);
                return pos;
            }
        }
        else{
            int t = 100;
            switch(ret){
                case 3:
                    t = 9;
                    break;
                case 0:
                    t = 1;
                    break;
            }

            if(board[0][y] + board[1][y] + board[2][y] == t) {
                pos[0] = new pair(0,y);
                pos[1] = new pair(1,y);
                pos[2] = new pair(2,y);
                return pos;
            }
            else if(board[x][0] + board[x][1] + board[x][2] == t){
                pos[0] = new pair(x,0);
                pos[1] = new pair(x,1);
                pos[2] = new pair(x,2);
                return pos;
            }
            else if(x == y || x + y == 2){
                if(board[0][0] + board[1][1] + board[2][2] == t && x == y){
                    pos[0] = new pair(0,0);
                    pos[1] = new pair(1,1);
                    pos[2] = new pair(2,2);
                    return pos;
                }
                else if(board[2][0] + board[1][1] + board[0][2] == t && x + y == 2){
                    pos[0] = new pair(2,0);
                    pos[1] = new pair(1,1);
                    pos[2] = new pair(0,2);
                    return pos;
                }
            }
        }
        pos[0] = new pair(-1,-1);
        pos[1] = new pair(-1,-1);
        pos[2] = new pair(-1,-1);
        return pos;
    }

    private void SetRandomMap() {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                buttons[i][j].setText("");
                buttons[i][j].setTextColor(Color.parseColor("#BBBBBB"));
            }
        }

        Random rand = new Random();

        int x_cnt = rand.nextInt(3) + 2;
        int o_cnt = x_cnt;
        if(rand.nextInt(2) == 1 || x_cnt == 4) {
            o_cnt--;
        }
        int total_size = 9;

        //fill X
        for(int i = 0; i < x_cnt; i++){
            int cnt = rand.nextInt(total_size);
            total_size--;
            ENDLOOP:for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(buttons[j][k].getText().toString().equals("")){
                        if(cnt == 0){
                            buttons[j][k].setText("X");
                            break ENDLOOP;
                        }
                        cnt--;
                    }
                }
            }
        }

        //fill O
        for(int i = 0; i < o_cnt; i++){
            int cnt = rand.nextInt(total_size);
            total_size--;
            ENDLOOP:for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(buttons[j][k].getText().toString().equals("")){
                        if(cnt == 0 && buttons[j][k].getText().toString().equals("")){
                            buttons[j][k].setText("O");
                            break ENDLOOP;
                        }
                        cnt--;
                    }
                }
            }
        }

        rand = null;

        int ret = Check();
        if(ret == 2 || ret == 3){
            SetRandomMap();
        }
    }

    private void SetNewTimer(int mode) {
        timer = null;
        playing = false;
        if(mode == 1){
            playing = true;
            timer = new CountDownTimer((long)time, 10) {
                @Override
                public void onTick(long l) {
                    if(freeze){
                        this.cancel();
                    }
                    else{
                        time_bar.setSize((int) (w * l / (double)time), time_bar.getHeight());
                        switch((int)(time_bar.getWidth() * 3 / w)) {
                            case 0:
                                time_bar.r.setBackgroundColor(Color.parseColor("#a76d6d"));
                                break;
                            case 1:
                                time_bar.r.setBackgroundColor(Color.parseColor("#a99d72"));
                                break;
                            default:
                                time_bar.r.setBackgroundColor(Color.parseColor("#69995a"));
                                break;
                        }
                    }
                }

                @Override
                public void onFinish() {
                    GameEnd();
                    time_bar.setSize(0, time_bar.getHeight());
                }
            };
        }
        else if(mode == 2){
            timer = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    if(GameData.item.revival > 0){
                        Intent intent= new Intent(getApplicationContext(), RevivalActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent= new Intent(getApplicationContext(), GameOverScreen.class);
                        startActivity(intent);
                    }
                }
            };
        }
        else{
            timer = new CountDownTimer(500, 100) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    if(!game_end){
                        SetRandomMap();
                        SetNewTimer(1);
                        timer.start();
                        checking = false;
                    }
                }
            };
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(RevivalActivity.run){
            overridePendingTransition(R.anim.slide_not_move, R.anim.slide_down_close);
            RevivalActivity.run = false;
            if(RevivalActivity.rev){
                game_end = false;
                SetNewTimer(0);
                timer.start();
            }
            else{
                Intent intent= new Intent(getApplicationContext(), GameOverScreen.class);
                startActivity(intent);
            }
        }
    }
}


class pair{
    pair(){
        this.x = 0;
        this.y = 0;
    }
    pair(int x , int y){
        this.x = x;
        this.y = y;
    }
    public void SetValue(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int x;
    public int y;
}