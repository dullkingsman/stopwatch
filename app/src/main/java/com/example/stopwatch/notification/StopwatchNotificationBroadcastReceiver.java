package com.example.stopwatch.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.stopwatch.core.Stopwatch;
import com.example.stopwatch.StopwatchApp;

import java.util.Objects;

public class StopwatchNotificationBroadcastReceiver extends BroadcastReceiver {
    public StopwatchNotificationBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        StopwatchApp app = (StopwatchApp) context.getApplicationContext();
        Stopwatch stopwatch = app.getStopwatch();

        String action = Objects.requireNonNull(intent.getAction());

        if (action.equals(app.getStopwatchNotification().mainActionId))
            stopwatch.handleMainAction();
        else if (action.equals(app.getStopwatchNotification().supportActionId))
            stopwatch.handleSupportAction();
    }
}
