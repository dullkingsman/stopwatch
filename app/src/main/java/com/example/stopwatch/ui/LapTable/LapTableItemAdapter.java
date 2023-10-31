package com.example.stopwatch.ui.LapTable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.stopwatch.R;
import com.example.stopwatch.core.Stopwatch;
import com.example.stopwatch.utils.Utils;

public class LapTableItemAdapter extends ListAdapter<Stopwatch.Lap, LapTableItemViewHolder> {
    public LapTableItemAdapter(@NonNull DiffUtil.ItemCallback<Stopwatch.Lap> diffCallback) {
        super(diffCallback);
    }

    @NonNull @Override public LapTableItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lap_table_item, parent, false);
        return new LapTableItemViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull LapTableItemViewHolder holder, int position) {
        Stopwatch.Lap lap = this.getItem(position);

        String _id = lap.id + "";

        holder.id.setText(
            lap.id >= 10
                ? _id
                : Utils.padWith0(_id, Utils.PadPlacement.START)
        );
        holder.lapTime.setText(lap.lapTime);
        holder.overallTime.setText(lap.overallTime);
    }
}
