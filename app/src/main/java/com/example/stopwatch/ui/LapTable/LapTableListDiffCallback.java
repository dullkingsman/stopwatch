package com.example.stopwatch.ui.LapTable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.stopwatch.core.Stopwatch;

public class LapTableListDiffCallback extends DiffUtil.ItemCallback<Stopwatch.Lap> {
    @Override public boolean areItemsTheSame(@NonNull Stopwatch.Lap oldItem, @NonNull Stopwatch.Lap newItem) {
        return oldItem.equals(newItem);
    }

    @Override public boolean areContentsTheSame(@NonNull Stopwatch.Lap oldItem, @NonNull Stopwatch.Lap newItem) {
        return oldItem.equals(newItem);
    }
}