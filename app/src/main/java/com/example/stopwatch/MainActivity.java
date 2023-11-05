package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.stopwatch.service.StopWatchNotificationService;
import com.example.stopwatch.ui.StopWatchView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    StopWatchView stopWatchView;
    boolean isComingFromAConfigurationChange = false;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.stopWatchView = new StopWatchView(this);

        this.isComingFromAConfigurationChange = savedInstanceState != null && savedInstanceState.getBoolean("config-changed");

        if (
            !NotificationManagerCompat.from(this).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        ) {
            int result = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            );

            if (result != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    0
                );
            }
        }
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("config-changed", true);
    }

    @Override protected void onStart() {
        super.onStart();

        StopwatchApp app = (StopwatchApp) this.getApplication();

        if (!this.isComingFromAConfigurationChange) {
            app.stopService(
                new Intent(this, StopWatchNotificationService.class)
            );
        }
    }


    @Override protected void onStop() {
        super.onStop();

        StopwatchApp app = (StopwatchApp) this.getApplication();

        if (
            Objects.requireNonNull(app.getStopwatch().status.getValue()) != 0 &&
            !this.isChangingConfigurations()
        )
            ContextCompat.startForegroundService(
                this,
                new Intent(this, StopWatchNotificationService.class)
            );
    }
}