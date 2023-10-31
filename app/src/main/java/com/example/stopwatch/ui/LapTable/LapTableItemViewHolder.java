package com.example.stopwatch.ui.LapTable;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopwatch.R;

public class LapTableItemViewHolder extends RecyclerView.ViewHolder {
    TextView id;
    TextView lapTime;
    TextView overallTime;

    public LapTableItemViewHolder(@NonNull View itemView) {
        super(itemView);

        this.id = itemView.findViewById(R.id.lap_table_item_id);
        this.lapTime = itemView.findViewById(R.id.lap_table_item_lap_times);
        this.overallTime = itemView.findViewById(R.id.lap_table_item_overall_time);
    }
}
