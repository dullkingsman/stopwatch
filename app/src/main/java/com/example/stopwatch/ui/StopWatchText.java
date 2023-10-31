package com.example.stopwatch.ui;

import androidx.annotation.NonNull;

import com.example.stopwatch.utils.Utils;

public class StopWatchText {

    public String hours;
    public String minutes;
    public String seconds;
    public String centiSeconds;

    int hoursValue;
    int minutesValue;
    int secondsValue;
    int centiSecondsValue;

    public StopWatchText(int hours, int minutes, int seconds, int centiSeconds) {
        this.hoursValue = hours;
        this.minutesValue = minutes;
        this.secondsValue = seconds;
        this.centiSecondsValue = centiSeconds;

        String stringHours = Integer.toString(this.minutesValue);
        String stringMinutes = Integer.toString(this.minutesValue);
        String stringSeconds = Integer.toString(this.secondsValue);
        String stringCentiSeconds = Integer.toString(this.centiSecondsValue);

        this.hours = hoursValue > 9 ? stringMinutes : Utils.padWith0(stringMinutes, null);
        this.minutes = minutesValue > 9 ? stringMinutes : Utils.padWith0(stringMinutes, null);
        this.seconds = secondsValue > 9 ? stringSeconds : Utils.padWith0(stringSeconds, null);
        this.centiSeconds = centiSecondsValue > 9 ? stringCentiSeconds : Utils.padWith0(stringCentiSeconds, null);
    }

    public static StopWatchText from(long elapsedTime) {
        double totalHours = (double) elapsedTime / (1000 * 60 * 60);
        double hours = Math.floor(totalHours);
        double afterHoursDecimal = totalHours - hours;

        double totalMinutes = afterHoursDecimal * 60;
        double minutes = Math.floor(totalMinutes);
        double afterMinutesDecimal = totalMinutes - minutes;

        double totalSeconds = afterMinutesDecimal * 60;
        double seconds = Math.floor(totalSeconds);
        double afterSecondsDecimal = totalSeconds - seconds;

        double totalCentiSeconds = afterSecondsDecimal * 100;
        double centiSeconds = Math.floor(totalCentiSeconds);

        return new StopWatchText((int) hours, (int) minutes, (int) seconds, (int) centiSeconds);
    }

    @NonNull @Override public String toString() {
        return this.hours + ":" + this.minutes + ":" + this.seconds;
    }

    public String toShortString() {
        return (this.hoursValue > 0? this.hours + ":" : "") + this.minutes + ":" + this.seconds;
    }

    public String toShortStringWithMilliseconds() {
        return (this.hoursValue > 0? this.hours + ":" : "") + this.minutes + ":" + this.seconds + "." + this.centiSeconds;
    }
}
