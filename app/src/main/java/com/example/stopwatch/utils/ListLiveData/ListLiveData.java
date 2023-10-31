package com.example.stopwatch.utils.ListLiveData;

import androidx.lifecycle.LiveData;

import java.util.Collections;
import java.util.List;

public class ListLiveData<T> extends LiveData<ListLiveDataChange<T>> {

    private final List<T> list;

    public ListLiveData(List<T> list) {
        this.list = list;
        setValue(null);
    }

    public void add(T item) {
        list.add(item);

        setValue(new ListLiveDataChange<T>(
                ListLiveDataChange.ListLiveDataChangeType.ADD,
            Collections.singletonList(item),
            this.list
        ));
    }

    public void clear() {
        this.list.clear();

        setValue(new ListLiveDataChange<T>(
            ListLiveDataChange.ListLiveDataChangeType.CLEAR,
            null,
            this.list
        ));
    }

    public List<T> getList() {
        return list;
    }
}
