package com.example.stopwatch.core;

import android.os.Handler;
import android.os.SystemClock;

public class Ticker {
    @FunctionalInterface
    public interface Callbacks {
        void onTick (long elapsedTime);
    }

    private int status = 0; // 0 - not started, 1 - running, 2 - paused
    private long startTime = 0L;
    private long elapsedTime = 0L;

    private final Callbacks callbacks;

    private final Handler tickHandler = new Handler();

    private final Runnable tickerThread = new Runnable() {
        @Override public void run() {
            if (status == 1) {
                long newStartTime = SystemClock.elapsedRealtime();

                elapsedTime += newStartTime - startTime;
                startTime = newStartTime;

                callbacks.onTick(elapsedTime);

                tickHandler.postDelayed(this, 10);
            }
        }
    };

    public Ticker(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void tick () {
        this.startTime = SystemClock.elapsedRealtime();

        this.tickHandler.post(tickerThread);

        this.status = 1;
    }

    public void start () {
        this.tick();
    }

    public void pause () {
        this.status = 2;
        this.callbacks.onTick(this.elapsedTime);
    }

    public void resume () {
        this.tick();
    }

    public void reset(boolean keepTicking) {
        this.status = keepTicking? 1: 0;

        this.tickHandler.removeCallbacks(tickerThread);

        this.startTime = 0L;
        this.elapsedTime = 0L;

        if (!keepTicking) this.callbacks.onTick(this.elapsedTime);
        else this.tick();
    }

    public int getStatus() {
        return this.status;
    }
}

