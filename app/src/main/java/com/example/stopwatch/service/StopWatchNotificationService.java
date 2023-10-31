package com.example.stopwatch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.stopwatch.StopwatchApp;

import java.util.Objects;

public class StopWatchNotificationService extends Service {
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        StopwatchApp app = (StopwatchApp) this.getApplication();

        this.startForeground(
            app.getStopwatchNotification().getId(),
            app.getStopwatchNotification().getNotification(
                Objects.requireNonNull(app.getStopwatch().mainTickerText.getValue()).toShortString()
            )
        );

        app.getStopwatchNotification().setNotifyForeground(true);

        return START_STICKY;
    }

    @Override public void onDestroy() {
        super.onDestroy();

        StopwatchApp app = (StopwatchApp) this.getApplication();

        app.getStopwatchNotification().setNotifyForeground(false);
        app.getStopwatchNotification().clearNotification();
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }
}