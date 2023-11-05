package com.example.stopwatch.notification;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.stopwatch.MainActivity;
import com.example.stopwatch.R;
import com.example.stopwatch.core.Stopwatch;
import com.example.stopwatch.StopwatchApp;

import java.util.Objects;

public class StopwatchNotification {
    private final int id = 69_420;

    private final String channelId = "control_channel";

    public final String supportActionId = "com.example.STOPWATCH_SUPPORT";

    public final String mainActionId = "com.example.STOPWATCH_MAIN";

    private final Context context;
    private final NotificationManagerCompat notificationManagerCompat;

    private NotificationCompat.Builder notificationBuilder = null;
    private final PendingIntent contentPendingIntent;

    private final NotificationCompat.Action supportAction;

    private final NotificationCompat.Action mainAction;

    private boolean notifyForeground = false;

    public StopwatchNotification(Context context) {
        this.notificationManagerCompat = NotificationManagerCompat.from(context);
        this.context = context;
        this.contentPendingIntent = this.getContentPendingIntent();
        this.supportAction =
            new NotificationCompat.Action(
                0,
                "",
                this.getSupportPendingIntent()
            );
        this.mainAction =
            new NotificationCompat.Action(
                0,
                "",
                this.getMainPendingIntent()
            );
    }

    private PendingIntent getSupportPendingIntent() {
        Intent intent = new Intent(this.context, StopwatchNotificationBroadcastReceiver.class);
        intent.setAction(this.supportActionId);

        return PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getMainPendingIntent() {
        Intent intent = new Intent(this.context, StopwatchNotificationBroadcastReceiver.class);
        intent.setAction(this.mainActionId);

        return PendingIntent.getBroadcast(this.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getContentPendingIntent() {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this.context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public Notification getNotification(String elapsedTimeText) {
        this.createNotificationChannel();

        StopwatchApp app = (StopwatchApp) this.context.getApplicationContext();
        int stopwatchStatus = Objects.requireNonNull(app.getStopwatch().status.getValue());

        String defaultContentText =
            context.getString(R.string.default_minutes) + context.getString(R.string.minute_second_separator) + context.getString(R.string.default_seconds);

        String supportActionText = stopwatchStatus == 2
            ? context.getString(R.string.support_button_paused_text)
            : context.getString(R.string.support_button_default_text);
        String mainActionText = stopwatchStatus == 0
            ? context.getString(R.string.main_button_default_text)
            : stopwatchStatus == 1
            ? context.getString(R.string.main_button_ticking_text)
            : context.getString(R.string.main_button_paused_text);

        this.supportAction.title = supportActionText;
        this.mainAction.title = mainActionText;

        if (notificationBuilder == null)
            this.notificationBuilder = new NotificationCompat
                .Builder(this.context, this.channelId)
                .setSmallIcon(R.drawable.ic_stopwatch)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColorized(true)
                .setColor(this.context.getColor(R.color.tones_down_bright_purple))
                .setContentTitle("Stopwatch")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setContentIntent(this.contentPendingIntent);

        this.notificationBuilder.setContentText(elapsedTimeText == null? defaultContentText: elapsedTimeText);

        this.notificationBuilder.clearActions();

        this.notificationBuilder.addAction(this.supportAction);
        this.notificationBuilder.addAction(this.mainAction);

        return notificationBuilder.build();
    }

    public void createNotificationChannel() {
        if (this.notificationManagerCompat.getNotificationChannel(this.channelId) == null) {
            NotificationChannelCompat channel =
                new NotificationChannelCompat.Builder(
                    this.channelId,
                    NotificationManagerCompat.IMPORTANCE_MIN
                ).setName("Stopwatch Control").build();

            this.notificationManagerCompat.createNotificationChannel(channel);
        }
    }

    public void clearNotification() {
        this.notificationManagerCompat.cancel(this.id);
    }

    public void updateNotification(String elapsedTimeText, boolean force) {
        if (
            ActivityCompat.checkSelfPermission(
                this.context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        )
            return;

        if (this.notifyForeground) {
            StopwatchApp app = (StopwatchApp) this.context.getApplicationContext();
            Stopwatch stopwatch = app.getStopwatch();

            if (
                !Objects.equals(
                    stopwatch.getLastMainTickerText().toShortString(),
                    elapsedTimeText
                ) ||
                force
            ) {
                stopwatch.setLastMainTickerText(stopwatch.mainTickerText.getValue());

                boolean shouldNotify = true;

                if (
                    !NotificationManagerCompat.from(this.context).areNotificationsEnabled() &&
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ) {
                    int result = ContextCompat.checkSelfPermission(
                        this.context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    );

                    if (result != PackageManager.PERMISSION_GRANTED)
                        shouldNotify = false;
                }

                if (shouldNotify)
                    this.notificationManagerCompat.notify(
                        this.id,
                        this.getNotification(elapsedTimeText)
                    );
            }
        }
    }

    public boolean getNotifyForeground() {
        return this.notifyForeground;
    }

    public void setNotifyForeground(boolean value) {
        this.notifyForeground = value;
    }

    public int getId() {
        return id;
    }
}

