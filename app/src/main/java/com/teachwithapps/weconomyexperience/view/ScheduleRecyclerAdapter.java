package com.teachwithapps.weconomyexperience.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.model.InstructionData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.InstructionViewHolder> {

    private List<InstructionData> instructionDataList;

    public ScheduleRecyclerAdapter(List<InstructionData> instructionDataList) {
        this.instructionDataList = instructionDataList;
    }

    @Override
    public InstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View scheduleInstructionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_scheduled_instruction, parent, false);
        InstructionViewHolder instructionViewHolder = new InstructionViewHolder(scheduleInstructionView);
        return instructionViewHolder;
    }

    @Override
    public void onBindViewHolder(InstructionViewHolder holder, int position) {
        InstructionData instructionData = instructionDataList.get(position);
        holder.setData(instructionData);
    }

    @Override
    public int getItemCount() {
        return instructionDataList.size();
    }

    class InstructionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.instruction_view)
        protected View instructionView;

        @BindView(R.id.add_instruction_button)
        protected View instructionButton;

        @BindView(R.id.title)
        protected TextView titleTextView;

        @BindView(R.id.filler)
        protected View fillerView;

        @BindView(R.id.image_view)
        protected View imageView;

        public InstructionViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(InstructionData instructionData) {
            if(instructionData.getType() == InstructionData.ListItemType.TYPE_ADD_BUTTON) {
                instructionView.setVisibility(View.INVISIBLE);
                instructionButton.setVisibility(View.VISIBLE);
                fillerView.setMinimumHeight(0);
                itemView.requestLayout();

            } else {
                instructionView.setVisibility(View.VISIBLE);
                instructionButton.setVisibility(View.INVISIBLE);

                titleTextView.setText(instructionData.getText());
                fillerView.setMinimumHeight(instructionData.getSize() * 100);
                itemView.requestLayout();
            }
        }
    }
}
