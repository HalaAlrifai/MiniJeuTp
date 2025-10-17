package com.example.minijeutp.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Platform {
    public float x, y, width, height;

    public Platform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(0xFF4CAF50); // bright green
        canvas.drawRoundRect(x, y, x + width, y + height, 15, 15, paint);
    }
}

