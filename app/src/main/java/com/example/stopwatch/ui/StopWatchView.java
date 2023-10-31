package com.example.stopwatch.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopwatch.MainActivity;
import com.example.stopwatch.R;
import com.example.stopwatch.StopwatchApp;
import com.example.stopwatch.core.Stopwatch;
import com.example.stopwatch.ui.LapTable.LapTableItemAdapter;
import com.example.stopwatch.ui.LapTable.LapTableListDiffCallback;
import com.example.stopwatch.utils.ListLiveData.ListLiveDataChange;
import com.example.stopwatch.utils.Utils;

import java.util.ArrayList;

public class StopWatchView {
    private final AppCompatActivity context;

    private LinearLayout tickerTextContainer;

    private TextView mainHoursView;
    private TextView mainHoursMinutesSeparatorView;
    private TextView mainMinutesView;
    private TextView mainSecondsView;
    private TextView mainCentiSecondsView;

    private TextView lapHoursView;
    private TextView lapHoursMinutesSeparatorView;
    private TextView lapMinutesView;
    private TextView lapSecondsView;
    private TextView lapCentiSecondsView;

    private LinearLayout lapTickerText;

    private ConstraintLayout lapTableContainer;

    private RecyclerView lapTable;

    private Button mainButton;
    private Button supportButton;

    private ImageButton copyLapsDataButton;

    private boolean hasUpdatedLapUi = false;

    private LapTableItemAdapter lapTableItemAdapter;

    private ObjectAnimator tickerTextContainerAnimator;

    private ObjectAnimator lapTableContainerPositionAnimator;

    private ValueAnimator lapTableContainerHeightAnimator;

    public StopWatchView(MainActivity context, Bundle savedInstanceState) {
        this.context = context;

        Stopwatch stopwatch = ((StopwatchApp) this.context.getApplication()).getStopwatch();

        this.initViews();

        this.initRecyclers();

        this.initAnimators();

        updateControlUi(stopwatch.getStopwatchStateChangeType(
            stopwatch.status.getValue()
        ));

        this.initObserversAndListeners(stopwatch);
    }

    private void initViews () {
        this.tickerTextContainer = context.findViewById(R.id.ticker_text_container);

        this.mainHoursView = context.findViewById(R.id.hours);
        this.mainHoursMinutesSeparatorView = context.findViewById(R.id.hours_minutes_separator);
        this.mainMinutesView = context.findViewById(R.id.minutes);
        this.mainSecondsView = context.findViewById(R.id.seconds);
        this.mainCentiSecondsView = context.findViewById(R.id.centi_seconds);

        this.lapTickerText = context.findViewById(R.id.lap_ticker_text);

        this.lapHoursView = context.findViewById(R.id.lap_hours);
        this.lapHoursMinutesSeparatorView = context.findViewById(R.id.lap_hours_minutes_separator);
        this.lapMinutesView = context.findViewById(R.id.lap_minutes);
        this.lapSecondsView = context.findViewById(R.id.lap_seconds);
        this.lapCentiSecondsView = context.findViewById(R.id.lap_centi_seconds);

        this.lapTableContainer = context.findViewById(R.id.lap_table_container);

        this.lapTable = context.findViewById(R.id.lap_table);

        this.supportButton = context.findViewById(R.id.support_button);
        this.mainButton = context.findViewById(R.id.main_button);
        this.copyLapsDataButton = context.findViewById(R.id.copy_laps_data_button);
    }

    private void initRecyclers() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.context);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        lapTable.setLayoutManager(layoutManager);

        this.lapTableItemAdapter = new LapTableItemAdapter(new LapTableListDiffCallback());

        lapTableItemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (lapTableItemAdapter.getItemCount() - 1) && lastVisiblePosition == (positionStart - 1))) {
                    lapTable.scrollToPosition(positionStart);
                }
            }
        });

        lapTable.setAdapter(lapTableItemAdapter);
    }

    private void initAnimators() {
        this.tickerTextContainerAnimator = ObjectAnimator.ofFloat(this.tickerTextContainer, "translationY", 0f, -Utils.dpToPx(this.context, 107));
        this.tickerTextContainerAnimator.setDuration(250);

        int lapTableMoveUnit = Utils.dpToPx(context, 155); // 48 added on the original value

        this.lapTableContainerPositionAnimator = ObjectAnimator.ofFloat(this.lapTableContainer, "translationY", 0f, -lapTableMoveUnit);
        this.lapTableContainerPositionAnimator.setDuration(250);

        this.lapTableContainerHeightAnimator = ValueAnimator.ofInt(0, lapTableMoveUnit);

        this.lapTableContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int lapTableContainerHeight = lapTableContainer.getHeight();

                lapTableContainerHeightAnimator = ValueAnimator.ofInt(lapTableContainerHeight, lapTableContainerHeight + lapTableMoveUnit);
                lapTableContainerHeightAnimator.setDuration(250);

                lapTableContainerHeightAnimator.addUpdateListener(animation -> {
                    ViewGroup.LayoutParams layoutParams = lapTableContainer.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();

                    lapTableContainer.setLayoutParams(layoutParams);
                    lapTableContainer.requestLayout();
                });

                lapTableContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initObserversAndListeners(Stopwatch stopwatch) {
        stopwatch.mainTickerText.observe(context, _mainTickerText -> {
            if (this.mainHoursView.getVisibility() == View.GONE && _mainTickerText.hoursValue > 0) {
                this.mainHoursView.setVisibility(View.VISIBLE);
                this.mainHoursMinutesSeparatorView.setVisibility(View.VISIBLE);

                this.mainHoursView.setText(_mainTickerText.hours);
            } else if (this.mainHoursView.getVisibility() == View.VISIBLE && _mainTickerText.hoursValue == 0) {
                this.mainHoursView.setVisibility(View.GONE);
                this.mainHoursMinutesSeparatorView.setVisibility(View.GONE);

                this.mainHoursView.setText(_mainTickerText.hours);
            }

            this.mainMinutesView.setText(_mainTickerText.minutes);
            this.mainSecondsView.setText(_mainTickerText.seconds);
            this.mainCentiSecondsView.setText(_mainTickerText.centiSeconds);
        });

        stopwatch.lapTickerText.observe(context, _lapTickerText -> {
            if (this.lapHoursView.getVisibility() == View.GONE && _lapTickerText.hoursValue > 0) {
                this.lapHoursView.setVisibility(View.VISIBLE);
                this.lapHoursMinutesSeparatorView.setVisibility(View.VISIBLE);

                this.lapHoursView.setText(_lapTickerText.hours);
            } else if (this.lapHoursView.getVisibility() == View.VISIBLE && _lapTickerText.hoursValue == 0) {
                this.lapHoursView.setVisibility(View.GONE);
                this.lapHoursMinutesSeparatorView.setVisibility(View.GONE);

                this.lapHoursView.setText(_lapTickerText.hours);
            }

            this.lapMinutesView.setText(_lapTickerText.minutes);
            this.lapSecondsView.setText(_lapTickerText.seconds);
            this.lapCentiSecondsView.setText(_lapTickerText.centiSeconds);
        });

        stopwatch.status.observe(context, status ->
            this.updateControlUi(stopwatch.getStopwatchStateChangeType(status))
        );

        stopwatch.laps.observe(context, this::updateLapUi);

        this.supportButton.setOnClickListener(view -> stopwatch.handleSupportAction());

        this.mainButton.setOnClickListener(view -> stopwatch.handleMainAction());

        this.copyLapsDataButton.setOnClickListener(view -> {
            String laps = stopwatch.laps.getList()
                .stream()
                .map(Object::toString)
                .reduce("", (prev, curr) -> prev + "\n" + curr);

            Utils.copyToClipBoard(this.context, "Laps", laps);
        });
    }

    public void animateUi(boolean reverse) {
        if (this.tickerTextContainerAnimator.isRunning()) this.tickerTextContainerAnimator.cancel();
        if (this.lapTableContainerPositionAnimator.isRunning()) this.lapTableContainerPositionAnimator.cancel();
        if (this.lapTableContainerHeightAnimator.isRunning()) this.lapTableContainerPositionAnimator.cancel();

        if (reverse) {
            this.tickerTextContainerAnimator.reverse();
            this.lapTableContainerPositionAnimator.reverse();
            this.lapTableContainerHeightAnimator.reverse();
        } else {
            this.tickerTextContainerAnimator.start();
            this.lapTableContainerPositionAnimator.start();
            this.lapTableContainerHeightAnimator.start();
        }
    }

    public void updateLapUi (ListLiveDataChange<Stopwatch.Lap> listLiveDataChange) {
        if (listLiveDataChange != null) {
            if (!this.hasUpdatedLapUi) this.hasUpdatedLapUi = true;

            this.lapTableItemAdapter.submitList(
                listLiveDataChange.list == null
                    ? new ArrayList<>()
                    : new ArrayList<>(listLiveDataChange.list)
            );

            if (listLiveDataChange.type == ListLiveDataChange.ListLiveDataChangeType.CLEAR) {
                this.animateUi(true);

                if (this.lapTickerText.getVisibility() == View.VISIBLE)
                    this.lapTickerText.setVisibility(View.INVISIBLE);

                if (this.lapTableContainer.getVisibility() == View.VISIBLE)
                    this.lapTableContainer.setVisibility(View.INVISIBLE);

                if (this.copyLapsDataButton.getVisibility() == View.VISIBLE) {
                    this.copyLapsDataButton.setVisibility(View.INVISIBLE);
                    this.copyLapsDataButton.setClickable(false);
                    this.copyLapsDataButton.setFocusable(false);
                }
            } else {
                if (listLiveDataChange.list.size() <= 1) this.animateUi(false);

                if (this.lapTickerText.getVisibility() == View.INVISIBLE)
                    this.lapTickerText.setVisibility(View.VISIBLE);

                if (this.lapTableContainer.getVisibility() == View.INVISIBLE)
                    this.lapTableContainer.setVisibility(View.VISIBLE);

                if (this.copyLapsDataButton.getVisibility() == View.INVISIBLE) {
                    this.copyLapsDataButton.setVisibility(View.VISIBLE);
                    this.copyLapsDataButton.setClickable(true);
                    this.copyLapsDataButton.setFocusable(true);
                }
            }
        }
    }

    public void updateControlUi(Stopwatch.StopwatchStateChangeType changeType) {
        if (
            changeType == Stopwatch.StopwatchStateChangeType.RESET ||
            changeType == Stopwatch.StopwatchStateChangeType.IDLE
        ) {
            this.supportButton.setEnabled(false);
            this.supportButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorSurfaceMuted)));
            this.supportButton.setTextColor(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorOnBackgroundMutedMin));
            this.supportButton.setText(R.string.support_button_default_text);
            this.mainButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorPrimary)));
            this.mainButton.setText(R.string.main_button_default_text);
        } else if (changeType == Stopwatch.StopwatchStateChangeType.START) {
            this.supportButton.setEnabled(true);
            this.supportButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorSurface)));
            this.supportButton.setTextColor(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorOnSurface));
            this.supportButton.setText(R.string.support_button_default_text);
            this.mainButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorError)));
            this.mainButton.setText(R.string.main_button_ticking_text);
        } else if (changeType == Stopwatch.StopwatchStateChangeType.STOP) {
            this.supportButton.setEnabled(true);
            this.supportButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorSurface)));
            this.supportButton.setTextColor(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorOnBackground));
            this.supportButton.setText(R.string.support_button_paused_text);
            this.mainButton.setBackgroundTintList(ColorStateList.valueOf(Utils.getColorFromThemeAttribute(this.context, R.attr.SWColorPrimary)));
            this.mainButton.setText(R.string.main_button_paused_text);
        }
    }
}

