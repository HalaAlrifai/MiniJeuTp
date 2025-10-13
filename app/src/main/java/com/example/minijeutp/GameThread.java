package com.example.minijeutp;

import android.view.SurfaceHolder;
import android.graphics.Canvas;
public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private volatile boolean running = false;
    private GameView gameView;
    public GameThread(SurfaceHolder surfaceHolder, GameView
            gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }
    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
    @Override
    public void run() {
        while (running) {
            Canvas canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder) {
                    this.gameView.update();
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {}
            finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
