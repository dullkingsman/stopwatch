package com.example.stopwatch;

import android.app.Application;
import android.content.Intent;

import com.example.stopwatch.core.Stopwatch;
import com.example.stopwatch.core.Ticker;
import com.example.stopwatch.notification.StopwatchNotification;
import com.example.stopwatch.service.StopWatchNotificationService;
import com.example.stopwatch.ui.StopWatchText;

import java.util.Objects;

public class StopwatchApp extends Application {
    private Stopwatch stopwatch;

    private StopwatchNotification stopwatchNotification;

    @Override public void onCreate() {
        super.onCreate();

        stopwatch = new Stopwatch();
        stopwatchNotification = new StopwatchNotification(this);

        this.stopwatch.mainTicker = new Ticker(elapsedTime ->
            this.stopwatch.mainTickerText.setValue(StopWatchText.from(elapsedTime))
        );

        this.stopwatch.lapTicker = new Ticker(elapsedTime ->
            this.stopwatch.lapTickerText.setValue(StopWatchText.from(elapsedTime))
        );

        this.initStopwatchStatusHandler();

        this.initForegroundNotify();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.startActivity(intent);
    }

    public Stopwatch getStopwatch() {
        return this.stopwatch;
    }

    public StopwatchNotification getStopwatchNotification() {
        return this.stopwatchNotification;
    }

    private void updateNotification(StopWatchText mainTickerText) {
        StopWatchText _mainTickerText =
            mainTickerText != null
                ? mainTickerText
                : Objects.requireNonNull(this.stopwatch.mainTickerText.getValue());

        int lapsCount = this.stopwatch.laps.getList().size();
        int status = Objects.requireNonNull(this.stopwatch.status.getValue());

        String lapText = lapsCount > 0? "Lap " + lapsCount : "";
        String extendedText = status == 1? lapText : status == 2? "Paused": "";

        this.stopwatchNotification.updateNotification(
            _mainTickerText.toShortString() + "    " /*+ extendedText*/,
            mainTickerText == null
        );
    }

    private boolean processShouldEnd() {
        return this.stopwatchNotification.getNotifyForeground();
    }

    public void initStopwatchStatusHandler() {
        this.stopwatch.status.observeForever(status -> {
            Stopwatch.StopwatchStateChangeType changeType = this.stopwatch.getStopwatchStateChangeType(status);

            switch (changeType) {
                case RESET:
                    this.stopwatch.mainTicker.reset(false);

                    if(processShouldEnd()) {
                        this.stopService(
                            new Intent(this, StopWatchNotificationService.class)
                        );
                        System.exit(0); return;
                    }

                    break;
                case STOP:
                    this.stopwatch.mainTicker.pause();

                    break;
                case START:
                    if (this.stopwatch.previousStatus == 0) this.stopwatch.mainTicker.start();
                    else this.stopwatch.mainTicker.resume();

                    break;
            }

            this.updateNotification(null);
        });
    }

    public void initForegroundNotify() {
        this.stopwatch.mainTickerText.observeForever(this::updateNotification);
    }
}
