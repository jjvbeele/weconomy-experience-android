package com.teachwithapps.weconomyexperience.view.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.teachwithapps.weconomyexperience.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

public class MultiLinearRecyclerView<DT> extends LinearLayout {

    public interface AdapterFactory<VT extends RecyclerView.ViewHolder, DT> {
        RecyclerView.Adapter<VT> createAdapter(List<DT> ts);
    }

    private static final String TAG = MultiLinearRecyclerView.class.getName();

    private List<List<DT>> dataMap;

    private AdapterFactory<? extends RecyclerView.ViewHolder, DT> adapterFactory;

    private List<RecyclerView> childRecyclerList = new ArrayList<>();

    public MultiLinearRecyclerView(Context context) {
        super(context);
    }

    public MultiLinearRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLinearRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDataMap(List<List<DT>> dataMap, AdapterFactory<? extends RecyclerView.ViewHolder, DT> adapterFactory) {
        this.dataMap = dataMap;
        this.adapterFactory = adapterFactory;
        dataMapChanged();
    }

    /**
     * Method to be called when the map's child content changed
     *
     * @param column   column to change
     * @param index    index where a new item was inserted or removed
     * @param inserted boolean to indicate if an item was inserted or removed
     */
    public void dataMapContentChanged(int column, int index, boolean inserted) {
        RecyclerView childRecycler = childRecyclerList.get(column);
        RecyclerView.Adapter adapter = childRecycler.getAdapter();

        if (inserted) {
            adapter.notifyItemInserted(index);

        } else {
            Log.d(TAG, "Removing data on " + column + ", " + index);
//            adapter.notifyItemRemoved(index);
            dataMapChanged();
        }

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
