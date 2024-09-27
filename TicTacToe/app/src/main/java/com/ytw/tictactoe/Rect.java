package com.ytw.tictactoe;

import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Rect extends AppCompatActivity {
    public View r;

    public void setSize(int w, int h) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(w,h);
        this.r.setLayoutParams(layout);
        layout = null;
    }

    public int getWidth(){
        return r.getWidth();
    }

    public int getHeight(){
        return r.getHeight();
    }
}
