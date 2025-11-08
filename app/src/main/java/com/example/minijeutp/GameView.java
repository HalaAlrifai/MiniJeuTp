package com.example.minijeutp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
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
    private Player player;
    private boolean showStart = true;

    public static int screenWidth, screenHeight;
    private List<Platform> platforms = new ArrayList<>();

    private boolean isGameOver = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float ax = 0;

    private float lightLevel = 1000f;
    private Sensor lightSensor;

    private Platform.Type randomType() {
        double r = Math.random();
        if (r < 0.70) return Platform.Type.NORMAL;
        if (r < 0.95) return Platform.Type.BREAKABLE;
        return Platform.Type.BOOST;
    }



    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        // Capteur d’acc.
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int
            width, int height) {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        // Création des plateformes
        platforms.clear();
        int numPlatforms = 10;
        float platformWidth = screenWidth / 5f;
        float platformHeight = 20f;
        float verticalSpacing = screenHeight / numPlatforms;

        for (int i = 0; i < numPlatforms; i++) {
            float px = (float) (Math.random() * (screenWidth - platformWidth));
            float py = screenHeight - i * verticalSpacing;
            Platform.Type t = randomType();
            platforms.add(new Platform(px, py, platformWidth, platformHeight, t));

        }

        // Création du joueur
        float playerWidth = 80f;
        float playerHeight = 80f;
        float startX = screenWidth / 2f - playerWidth / 2f;
        float startY = screenHeight - playerHeight;
        player = new Player(startX, startY, playerWidth, playerHeight);
        player.vy = -25; // saut initial

        // Démarrage du thread
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        drawBackground(canvas);

        Paint paint = new Paint();

        // Plateformes
        for (Platform p : platforms) {
            p.draw(canvas, paint);
        }

        // Joueur
        if (player != null) {
            player.draw(canvas, paint);
        }
        if (isGameOver) {
            drawGameOver(canvas);
        }


        // Score
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        canvas.drawText("Score: " + player.Score, 50, 45, paint);
        if (showStart) {
            Paint mask = new Paint();
            mask.setColor(Color.argb(150, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), mask);

            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.WHITE);
            p.setTextSize(64f);
            p.setFakeBoldText(true);
            String title = "Doodle Jump";
            float tw = p.measureText(title);
            canvas.drawText(title, (getWidth()-tw)/2f, getHeight()*0.42f, p);

            p.setTextSize(42f);
            String tip = "Touch to Start";
            float ww = p.measureText(tip);
            canvas.drawText(tip, (getWidth()-ww)/2f, getHeight()*0.58f, p);
        }

    }

    public void update() {
        if (player == null) return;
        if (showStart) return;

//        if (isGameOver) {
//            player.y += fallSpeed;
//            fallSpeed += 1;
//            return;
//        }


        player.update(platforms);

        if (player.y > screenHeight) {
            isGameOver = true;
            return;
        }

        player.x -= ax * 5;

        if (player.x < -player.width) {
            player.x = screenWidth;
        } else if (player.x > screenWidth) {
            player.x = -player.width;
        }


        if (player.vy < 0 && player.y < screenHeight / 1.4f) {
            float offset = -player.vy;
            player.y += offset;


            for (Platform p : platforms) {
                p.y += offset;

                if (p.y > screenHeight) {
                    p.y = 0;
                    p.x = (float) (Math.random() * (screenWidth - p.width));
                    p.setType(randomType());
                }
            }
        }
    }



    private void drawBackground(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (lightLevel < 50f) {
            canvas.drawColor(Color.BLACK);
        } else {
            canvas.drawColor(Color.rgb(240, 240, 255));
        }

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.rgb(220, 220, 220));
        gridPaint.setStrokeWidth(1);

        int gridSize = 40;
        for (int x = 0; x < width; x += gridSize) {
            canvas.drawLine(x, 0, x, height, gridPaint);
        }
        for (int y = 0; y < height; y += gridSize) {
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        Paint marginPaint = new Paint();
        marginPaint.setColor(Color.rgb(210, 220, 255));
        canvas.drawRect(0, 0, width, 60, marginPaint);
    }

    // --- Accéléromètre ---
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
        }else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightLevel = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    private void drawGameOver(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(200, 255, 255, 255)); // fond semi-transparent
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setTextSize(70);
        text.setFakeBoldText(true);
        text.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("GAME OVER", screenWidth / 2f, screenHeight / 2f, text);
        text.setTextSize(40);
        canvas.drawText("Score: " + player.Score, screenWidth / 2f, screenHeight / 2f + 60, text);
        canvas.drawText("Tap to restart", screenWidth / 2f, screenHeight / 2f + 150, text);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (showStart) {
                showStart = false;
            } else if (isGameOver) {
                resetGame();
            }
        }
        return true;
    }


    private void resetGame() {
        isGameOver = false;
        player.Score = 0;

        // recréer les plateformes
        platforms.clear();
        int numPlatforms = 10;
        float platformWidth = screenWidth / 5f;
        float platformHeight = 20f;
        float verticalSpacing = screenHeight / numPlatforms;

        for (int i = 0; i < numPlatforms; i++) {
            float px = (float) (Math.random() * (screenWidth - platformWidth));
            float py = screenHeight - i * verticalSpacing;
            Platform.Type t = randomType();
            platforms.add(new Platform(px, py, platformWidth, platformHeight, t));

        }

        // replacer le joueur
        float playerWidth = 80f;
        float playerHeight = 80f;
        float startX = screenWidth / 2f - playerWidth / 2f;
        float startY = screenHeight - playerHeight;
        player = new Player(startX, startY, playerWidth, playerHeight);
        player.vy = -25;
    }

}