package com.example.stopwatch.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.stopwatch.ui.StopWatchText;
import com.example.stopwatch.utils.ListLiveData.ListLiveData;

import java.util.ArrayList;
import java.util.Objects;

public class Stopwatch {
    public enum StopwatchStateChangeType {
        START,
        STOP,
        RESET,
        IDLE
    }

    public static class Lap {
        public int id;
        public String lapTime;
        public String overallTime;

        public Lap(
            int id,
            String lapTime,
            String overallTime
        ) {
            this.id = id;
            this.lapTime = lapTime;
            this.overallTime = overallTime;
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;

            if (obj == null || this.getClass() != obj.getClass()) return false;

            Lap person = (Lap) obj;

            return this.id == person.id &&
                Objects.equals(this.lapTime, person.lapTime) &&
                Objects.equals(this.overallTime, person.overallTime);
        }

        @NonNull @Override public String toString() {
            return this.id + "  " + this.lapTime + "  "  + this.overallTime;
        }
    }

    public int previousStatus = -1;

    public MutableLiveData<Integer> status = new MutableLiveData<>(0); // 0 - not started, 1 - running, 2 - paused

    public ListLiveData<Stopwatch.Lap> laps = new ListLiveData<>(new ArrayList<>());

    public MutableLiveData<StopWatchText> mainTickerText = new MutableLiveData<>(StopWatchText.from(0)); // 0 - not started, 1 - running, 2 - paused

    public MutableLiveData<StopWatchText> lapTickerText = new MutableLiveData<>(StopWatchText.from(0)); // 0 - not started, 1 - running, 2 - paused

    public Ticker mainTicker = null;

    public Ticker lapTicker = null;

    private StopWatchText lastMainTickerText = StopWatchText.from(0);

    public StopwatchStateChangeType getStopwatchStateChangeType(Integer status) {
        if (status == 0 && this.previousStatus == 2)
            return Stopwatch.StopwatchStateChangeType.RESET;
        else if (status == 1 && (this.previousStatus == 0 || this.previousStatus == 2))
            return Stopwatch.StopwatchStateChangeType.START;
        else if (status == 2 && this.previousStatus == 1)
            return Stopwatch.StopwatchStateChangeType.STOP;
        else return StopwatchStateChangeType.IDLE;
    }

    public void handleSupportAction() {
        this.previousStatus = Objects.requireNonNull(this.status.getValue());

        if (this.previousStatus == 2) {
            this.laps.clear();

            this.lapTicker.reset(false);
            this.status.setValue(0);
        } else if (this.previousStatus != 0) {
            StopWatchText mainTickerText = Objects.requireNonNull(this.mainTickerText.getValue());
            StopWatchText lapTickerText = Objects.requireNonNull(this.lapTickerText.getValue());

            String overallTimeText = mainTickerText.toShortStringWithMilliseconds();

            if (this.lapTicker.getStatus() > 0) {
                this.laps.add(new Lap(
                    laps.getList().size() + 1,
                    lapTickerText.toShortStringWithMilliseconds(),
                    overallTimeText
                ));

                this.lapTicker.reset(true);
            } else {
                this.laps.add(new Lap(
                    laps.getList().size() + 1,
                    overallTimeText,
                    overallTimeText
                ));

                this.lapTicker.start();
            }
        }
    }

    public void handleMainAction() {
        this.previousStatus = Objects.requireNonNull(this.status.getValue());

        if (this.previousStatus == 0) this.status.setValue(1);
        else if (this.previousStatus == 1) {
            this.status.setValue(2);
            if (this.lapTicker.getStatus() > 0) this.lapTicker.pause();
        }
        else if (this.previousStatus == 2) {
            this.status.setValue(1);
            if (this.lapTicker.getStatus() > 0) this.lapTicker.resume();
        }
    }

    public StopWatchText getLastMainTickerText() {
        return this.lastMainTickerText;
    }

    public void setLastMainTickerText(StopWatchText lastMainTickerText) {
        this.lastMainTickerText = lastMainTickerText;
    }
}
