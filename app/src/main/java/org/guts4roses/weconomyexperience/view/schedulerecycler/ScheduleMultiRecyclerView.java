package org.guts4roses.weconomyexperience.view.schedulerecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

import org.guts4roses.weconomyexperience.GameActivity;
import org.guts4roses.weconomyexperience.R;
import org.guts4roses.weconomyexperience.model.ScheduledInstructionData;
import org.guts4roses.weconomyexperience.view.util.MultiRecyclerView;

import java.util.List;

/**
 * Created by mint on 14-9-17.
 */

public class ScheduleMultiRecyclerView extends MultiRecyclerView<ScheduledInstructionData> {

    private static final String TAG = ScheduleMultiRecyclerView.class.getName();

    private static ScheduledInstructionData draggedScheduledInstructionData;

    static void setDraggedScheduledInstructionData(ScheduledInstructionData draggedScheduledInstructionData) {
        ScheduleMultiRecyclerView.draggedScheduledInstructionData = draggedScheduledInstructionData;
    }

    public static ScheduledInstructionData obtainAndClearDraggedScheduledInstructionData() {
        ScheduledInstructionData data = draggedScheduledInstructionData;
        draggedScheduledInstructionData = null;
        return data;
    }

    private AdapterFactory<? extends RecyclerView.ViewHolder, ScheduledInstructionData> adapterFactory;

    public ScheduleMultiRecyclerView(Context context) {
        super(context);
        init();
    }

    public ScheduleMultiRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScheduleMultiRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //adapterfactory that will create scheduleadapters for the scheduledrecyclerview
        adapterFactory =
                new MultiRecyclerView.AdapterFactory<ScheduleRecyclerAdapter.ViewHolder, ScheduledInstructionData>() {
                    @Override
                    public RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> createAdapter(List<ScheduledInstructionData> scheduledInstructionDataList) {
                        return new ScheduleRecyclerAdapter(scheduledInstructionDataList);
                    }
                };
    }

    public void setDataMap(List<List<ScheduledInstructionData>> dataMap) {
        super.setDataMap(dataMap, adapterFactory);
    }

    @Override
    public void dataMapChanged() {
        super.dataMapChanged();

        for (int i = 0; i < childRecyclerList.size(); i++) {
            final RecyclerView recyclerView = childRecyclerList.get(i);
            final int index = i;
            recyclerView.setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch(event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            return true;

                        case DragEvent.ACTION_DRAG_ENDED:
                            return true;

                        case DragEvent.ACTION_DRAG_ENTERED:
                            recyclerView.setBackgroundColor(getResources().getColor(R.color.light_gray));
                            return true;

                        case DragEvent.ACTION_DRAG_EXITED:
                            recyclerView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            return true;

                        case DragEvent.ACTION_DROP:
                            recyclerView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            int day = index + 1;
                            ((GameActivity)getContext()).rescheduleScheduledInstruction(draggedScheduledInstructionData, day);
                            return true;
                    }
                    return true;
                }
            });
        }
    }
}
