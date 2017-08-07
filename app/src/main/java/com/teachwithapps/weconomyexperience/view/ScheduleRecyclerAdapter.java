package com.teachwithapps.weconomyexperience.view;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        View scheduleInstructionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_scheduled_instruction, parent, false);
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

        @BindView(R.id.title)
        protected TextView titleTextView;

        @BindView(R.id.labour_col_1)
        protected LinearLayout labourCol1;

        @BindView(R.id.labour_col_2)
        protected LinearLayout labourCol2;

        @BindView(R.id.input_col)
        protected LinearLayout inputCol;

        @BindView(R.id.output_col)
        protected LinearLayout outputCol;

        public InstructionViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(InstructionData instructionData) {
            titleTextView.setText(instructionData.getText());

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            labourCol1.removeAllViews();
            labourCol2.removeAllViews();
            inputCol.removeAllViews();
            outputCol.removeAllViews();

            for (int i = 0; i < instructionData.getSize(); i++) {
                if (i % 2 == 0) {
                    addInfoImage(inflater, labourCol1, R.drawable.ic_shovel);
                } else {
                    addInfoImage(inflater, labourCol2, R.drawable.ic_shovel);
                }
            }

            for (int i = 0; i < instructionData.getSize(); i++) {
                addInfoImage(inflater, inputCol, R.drawable.ic_flour);
            }

            for (int i = 0; i < instructionData.getSize(); i++) {
                addInfoImage(inflater, outputCol, R.drawable.ic_bread);
            }

        }

        private void addInfoImage(LayoutInflater inflater, ViewGroup row, @DrawableRes int drawableId) {
            ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview_info_scheduled_instruction, row, false);
            imageView.setImageResource(drawableId);
            row.addView(imageView);
        }
    }
}
