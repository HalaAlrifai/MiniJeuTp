package com.example.minijeutp.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.minijeutp.GameView;

import java.util.List;

public class Player {
    public float x, y, width, height;
    public float vy = 0;
    public float gravity = 0.8f;
    public boolean jumping = false;
    public int Score = 0;

    private Platform lastPlatformTouched = null;

    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(List<Platform> platforms) {

        vy += gravity;
        y += vy;


        for (Platform p : platforms) {
            boolean withinX = x + width > p.x && x < p.x + p.width;
            boolean hittingTop = y + height >= p.y && y + height <= p.y + vy + 10;

            if (withinX && hittingTop && vy > 0) {

                y = p.y - height;
                vy = -25;
                jumping = true;


                if (p != lastPlatformTouched) {
                    Score += 10;
                    lastPlatformTouched = p;
                }
            }
        }

    }




    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(200, 20, 0));
        canvas.drawRect(x, y, x + width, y + height, paint);


    }
}
