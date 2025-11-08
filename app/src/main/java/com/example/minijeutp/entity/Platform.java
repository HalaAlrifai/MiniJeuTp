package com.example.minijeutp.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Platform {
    public float x, y, width, height;
    public enum Type { NORMAL, BREAKABLE, BOOST }

    public Type type = Type.NORMAL;
    public boolean broken = false;
    public boolean active = true;


    public Platform(float x, float y, float width, float height, Type type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
    public void setType(Type t) {
        this.type = t;
        this.broken = false;
        this.active = true;
    }

    public void onLand(Player p) {
        if (!active) return;

        switch (type) {
            case NORMAL: p.vy = -25f; p.jumping = true; break;
            case BOOST: p.vy = -35f; p.jumping = true; break;
            case BREAKABLE:
                p.vy = -25f;
                p.jumping = true;
                broken = true;
                active = false;
                break;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        int c;
        switch (type) {
            default:
            case NORMAL:    c = Color.rgb(80, 200, 120); break;
            case BREAKABLE: c = Color.rgb(200, 120, 120); break;
            case BOOST:     c = Color.rgb(240, 200, 80); break;
        }
        if (!active && type == Type.BREAKABLE) c = Color.argb(120, 200, 120, 120);
        paint.setColor(c);
        canvas.drawRoundRect(x, y, x + width, y + height, 10, 10, paint);
    }
}

