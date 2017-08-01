package com.teachwithapps.weconomyexperience.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.teachwithapps.weconomyexperience.model.InstructionData;

import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

public class MultiLinearRecyclerView extends LinearLayout {

    private List<List<InstructionData>> instructionDataMap;

    public MultiLinearRecyclerView(Context context) {
        super(context);
    }

    public MultiLinearRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLinearRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDataMap(List<List<InstructionData>> instructionDataMap) {
        this.instructionDataMap = instructionDataMap;
        dataMapChanged();
    }

    public void dataMapChanged() {
        removeAllViews();
        for (int i = 0; i < instructionDataMap.size(); i++) {
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new ScheduleRecyclerAdapter(instructionDataMap.get(i)));
            addView(recyclerView);
        }

        requestLayout();
    }

}
