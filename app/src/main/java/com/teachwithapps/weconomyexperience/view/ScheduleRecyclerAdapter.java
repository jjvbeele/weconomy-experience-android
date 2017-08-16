package com.teachwithapps.weconomyexperience.view;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.Constants;
import com.teachwithapps.weconomyexperience.GameActivity;
import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {

    private List<ScheduledInstructionData> scheduledInstructionDataList;

    public ScheduleRecyclerAdapter(List<ScheduledInstructionData> scheduledInstructionDataList) {
        this.scheduledInstructionDataList = scheduledInstructionDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View scheduleInstructionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_scheduled_instruction, parent, false);
        return new ViewHolder(scheduleInstructionView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduledInstructionData scheduledInstructionData = scheduledInstructionDataList.get(position);
        holder.setData(scheduledInstructionData);
    }

    @Override
    public int getItemCount() {
        return scheduledInstructionDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private int PROPERTY_INPUT = 0;
        private int PROPERTY_LABOUR = 1;
        private int PROPERTY_CLAIM = 2;

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

        private ScheduledInstructionData scheduledInstructionData;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(final ScheduledInstructionData scheduledInstructionData) {
            this.scheduledInstructionData = scheduledInstructionData;
            InstructionData instructionData = scheduledInstructionData.getBindedInstructionData();
            titleTextView.setText(instructionData.getText());

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((GameActivity) itemView.getContext()).removeData(scheduledInstructionData);
                    return true;
                }
            });

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            labourCol1.removeAllViews();
            labourCol2.removeAllViews();
            inputCol.removeAllViews();
            outputCol.removeAllViews();

            for (int i = 0; i < instructionData.getLabour(); i++) {
                if (i % 2 == 0) {
                    addInfoImage(inflater, labourCol1, Constants.getLabourIcon(), PROPERTY_LABOUR);
                } else {
                    addInfoImage(inflater, labourCol2, Constants.getLabourIcon(), PROPERTY_LABOUR);
                }
            }

            for (int i = 0; i < instructionData.getInput(); i++) {
                addInfoImage(inflater, inputCol, Constants.getProductIcon(instructionData.getInputType()), PROPERTY_INPUT);
            }

            for (int i = 0; i < instructionData.getOutput(); i++) {
                addInfoImage(inflater, outputCol, Constants.getProductIcon(instructionData.getOutputType()), PROPERTY_CLAIM);
            }
        }

        private void setLabour() {
            ((GameActivity) itemView.getContext()).setLabour(scheduledInstructionData);
        }

        private void setClaim() {
            ((GameActivity) itemView.getContext()).setClaim(scheduledInstructionData);
        }

        private void addInfoImage(LayoutInflater inflater, ViewGroup row, @DrawableRes int drawableId, final int propertyType) {
            final ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview_info_scheduled_instruction, row, false);
            imageView.setImageResource(drawableId);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (propertyType == PROPERTY_LABOUR) {
                        setLabour();

                    } else if (propertyType == PROPERTY_CLAIM) {
                        setClaim();
                    }
                }
            });
            row.addView(imageView);
        }
    }
}
