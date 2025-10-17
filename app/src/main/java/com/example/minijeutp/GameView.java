package com.example.minijeutp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.minijeutp.entity.Platform;
import com.example.minijeutp.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{
    private GameThread thread;
    private int x =0;
    private Player player;
    public static int screenWidth, screenHeight;
    private List<Platform> platforms = new ArrayList<>();


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float ax = 0; // valeur actuelle du capteur

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        // Capteur d’accélération
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int
            width, int height) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        screenWidth = getWidth();
        screenHeight = getHeight();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        platforms.clear();
        int numPlatforms = 10;
        float platformWidth = screenWidth / 5f;
        float platformHeight = 20f;
        float verticalSpacing = screenHeight / numPlatforms;

        for (int i = 0; i < numPlatforms; i++) {
            float px = (float) (Math.random() * (screenWidth - platformWidth));
            float py = screenHeight - i * verticalSpacing;
            platforms.add(new Platform(px, py, platformWidth, platformHeight));
        }


        float playerWidth =  80f;
        float playerHeight = 80f;
        float startX = screenWidth / 2f - playerWidth / 2f;
        float startY = screenHeight - playerHeight;
        player = new Player(startX, startY, playerWidth, playerHeight);

        player.jump();


    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
        sensorManager.unregisterListener(this);
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        // Background
        drawBackground(canvas);

        // Draw platforms
        Paint paint = new Paint();
        for (Platform p : platforms) {
            p.draw(canvas, paint);
        }
        if (player != null) {
            player.draw(canvas, paint);
        }

//        // Temporary red block to represent player (for testing)
//        paint.setColor(Color.RED);
//        canvas.drawRect(x, 100, x + 80, 180, paint);
    }
    public void update() {
        if (player != null) {
            player.update(platforms);

            // Déplacement horizontal selon le capteur
            player.x -= ax * 5; // ajuster le 5 pour la sensibilité

            // Gérer les bords de l’écran (wrap-around comme Doodle Jump)
            if (player.x < -player.width) {
                player.x = screenWidth;
            } else if (player.x > screenWidth) {
                player.x = -player.width;
            }
        }
    }
       // x = (x + 1) % 300;
//        for (Platform p : platforms) {
//            p.y += 2; // move downward slowly
//            if (p.y > getHeight()) {
//                p.y = 0;
//                p.x = (float) (Math.random() * getWidth());

    private void drawBackground(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // Cream-colored paper background
        canvas.drawColor(Color.rgb(255, 250, 245));

        // Draw the light gray grid lines
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.rgb(220, 220, 220));
        gridPaint.setStrokeWidth(1);

        int gridSize = 40; // spacing between lines
        for (int x = 0; x < width; x += gridSize) {
            canvas.drawLine(x, 0, x, height, gridPaint);
        }
        for (int y = 0; y < height; y += gridSize) {
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        // Optional: a faint blue margin at the top (like a notebook)
        Paint marginPaint = new Paint();
        marginPaint.setColor(Color.rgb(210, 220, 255));
        canvas.drawRect(0, 0, width, 60, marginPaint);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0]; // valeur de l’inclinaison
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // pas nécessaire ici
    }


}
