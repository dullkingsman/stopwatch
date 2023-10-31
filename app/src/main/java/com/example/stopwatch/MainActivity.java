package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.example.stopwatch.service.StopWatchNotificationService;
import com.example.stopwatch.ui.StopWatchView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    StopWatchView stopWatchView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopWatchView = new StopWatchView(this, savedInstanceState);
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override protected void onStart() {
        super.onStart();

        StopwatchApp app = (StopwatchApp) this.getApplication();

        app.stopService(
            new Intent(this, StopWatchNotificationService.class)
        );
    }


    @Override protected void onStop() {
        super.onStop();

        StopwatchApp app = (StopwatchApp) this.getApplication();

        if (Objects.requireNonNull(app.getStopwatch().status.getValue()) != 0)
            ContextCompat.startForegroundService(
                this,
                new Intent(this, StopWatchNotificationService.class)
            );
    }
}