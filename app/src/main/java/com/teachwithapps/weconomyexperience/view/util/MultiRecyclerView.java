package com.teachwithapps.weconomyexperience.view.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

public class MultiRecyclerView<DT> extends LinearLayout {

    public interface AdapterFactory<VT extends RecyclerView.ViewHolder, DT> {
        RecyclerView.Adapter<VT> createAdapter(List<DT> ts);
    }

    private static final String TAG = MultiRecyclerView.class.getName();

    private List<List<DT>> dataMap;
    private Comparator<DT> dataComparator;

    private AdapterFactory<? extends RecyclerView.ViewHolder, DT> adapterFactory;

    private List<RecyclerView> childRecyclerList = new ArrayList<>();

    public MultiRecyclerView(Context context) {
        super(context);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDataMap(
            List<List<DT>> dataMap,
            AdapterFactory<? extends RecyclerView.ViewHolder, DT> adapterFactory) {
        this.dataMap = dataMap;
        this.adapterFactory = adapterFactory;
        dataMapChanged();
    }

    public void insertData(int column, DT data) {
        final List<DT> dataList = dataMap.get(column);
        dataList.add(0, data);
        dataMapContentInserted(column, 0);
    }

    /**
     * Method to be called when the map's child content changed
     *
     * @param column column to change
     * @param index  index where a new item was inserted or removed
     */
    public void dataMapContentInserted(int column, int index) {
        RecyclerView childRecycler = childRecyclerList.get(column);
        RecyclerView.Adapter adapter = childRecycler.getAdapter();

        adapter.notifyItemInserted(index);

        childRecycler.smoothScrollToPosition(index);
    }

    public void dataMapContentRemoved(int column, int index) {
        RecyclerView childRecycler = childRecyclerList.get(column);
        RecyclerView.Adapter adapter = childRecycler.getAdapter();

        adapter.notifyItemRemoved(index);

        childRecycler.smoothScrollToPosition(index);
    }

    public void dataMapContentUpdated(int column, int index) {
        RecyclerView childRecycler = childRecyclerList.get(column);
        RecyclerView.Adapter adapter = childRecycler.getAdapter();

        adapter.notifyItemChanged(index);

        childRecycler.smoothScrollToPosition(index);
    }

    /**
     * Method to be called when the map's child number changed, remove all views and rebuild
     */
    public void dataMapChanged() {
        removeAllViews();
        for (int i = 0; i < dataMap.size(); i++) {
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapterFactory.createAdapter(dataMap.get(i)));
            addView(recyclerView);

            childRecyclerList.add(recyclerView);
        }

        requestLayout();
    }
}
