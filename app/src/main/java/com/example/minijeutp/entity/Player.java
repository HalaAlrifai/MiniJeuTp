package com.example.minijeutp.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.minijeutp.GameView;

import java.util.List;

public class Player {
    public float x, y, width, height;
    public float vy = 0;        // vitesse verticale
    public float gravity = 0.8f;
    public boolean jumping = false;
    public int Score = 0;

    private Platform lastPlatformTouched = null; // nouvelle variable

    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(List<Platform> platforms) {
        // gravité
        vy += gravity;
        y += vy;

        // vérifie les collisions avec les plateformes
        for (Platform p : platforms) {
            boolean withinX = x + width > p.x && x < p.x + p.width;
            boolean hittingTop = y + height >= p.y && y + height <= p.y + vy + 10;

            if (withinX && hittingTop && vy > 0) {
                // rebond sur la plateforme
                y = p.y - height;
                vy = -25; // rebond vers le haut
                jumping = true;

                // Ajout du score quand le joueur touche une nouvelle plateforme
                if (p != lastPlatformTouched) {
                    Score += 10;
                    lastPlatformTouched = p;
                }
            }
        }

        // si le joueur tombe tout en bas
        if (y > GameView.screenHeight) {
            y = GameView.screenHeight - height;
            vy = -25; // saute à nouveau
            lastPlatformTouched = null; // réinitialise
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
