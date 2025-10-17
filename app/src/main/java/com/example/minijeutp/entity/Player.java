package com.example.minijeutp.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.minijeutp.GameView;

public class Player {
    public float x, y, width, height;
    public float vy = 0;        // vitesse verticale
    public float gravity = 1.0f;
    public boolean jumping = false;

    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update() {
        // Applique la gravité
        vy += gravity;
        y += vy;

        // Empêche de descendre sous le bas de l'écran
        if (y + height > GameView.screenHeight) {
            y = GameView.screenHeight - height;
            vy = 0;
            jumping = false;
        }
    }

    public void jump() {
        if (!jumping) {
            vy = -25;      // vitesse initiale du saut
            jumping = true;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(200, 20, 0));
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
}

