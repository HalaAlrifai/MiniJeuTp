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
        float prevY = y;
        y += vy;

        float prevBottom = prevY + height;
        float currBottom = y + height;

        for (Platform p : platforms) {
            if (!p.active) continue;
            boolean falling = vy > 0;
            boolean withinX = (x + width > p.x) && (x < p.x + p.width);
            boolean crossedTop = (prevBottom <= p.y) && (currBottom >= p.y);
            if (falling && withinX && crossedTop) {
                y = p.y - height;
                p.onLand(this);
                if (vy < 0f) jumping = true;
                if (p != lastPlatformTouched) {
                    Score += 10;
                    lastPlatformTouched = p;
                }
                break;
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(200, 20, 0));
        canvas.drawRect(x, y, x + width, y + height, paint);


    }
}
