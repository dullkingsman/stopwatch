package com.example.stopwatch.utils.ListLiveData;

import java.util.List;

public class ListLiveDataChange<T> {
    public enum ListLiveDataChangeType {
        ADD,
        CLEAR,
    }

    public ListLiveDataChangeType type;
    public List<T> values;

    public List<T> list;

    public ListLiveDataChange(
            ListLiveDataChangeType type,
            List<T> values,
            List<T> list
    ) {
        this.type = type;
        this.values = values;
        this.list = list;
    }
}
