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
import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.model.InstructionData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class FoldedInstructionRecyclerAdapter extends RecyclerView.Adapter<FoldedInstructionRecyclerAdapter.InstructionViewHolder> {

    public interface OnInstructionClickListener {
        void onClick(InstructionData instructionData);
    }

    private List<InstructionData> instructionDataList;
    private OnInstructionClickListener onItemClickListener;

    public FoldedInstructionRecyclerAdapter(List<InstructionData> instructionDataList, OnInstructionClickListener onItemClickListener) {
        this.instructionDataList = instructionDataList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public InstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View scheduleInstructionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_folded_instruction, parent, false);
        InstructionViewHolder instructionViewHolder = new InstructionViewHolder(scheduleInstructionView);
        return instructionViewHolder;
    }

    @Override
    public void onBindViewHolder(InstructionViewHolder holder, int position) {
        InstructionData instructionData = instructionDataList.get(position);
        holder.setData(instructionData, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return instructionDataList.size();
    }

    class InstructionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        protected TextView titleTextView;

        @BindView(R.id.labour_row)
        protected LinearLayout labourRow;

        @BindView(R.id.input_row)
        protected LinearLayout inputRow;

        @BindView(R.id.output_row)
        protected LinearLayout outputRow;

        @BindView(R.id.select_button)
        protected View selectButton;

        public InstructionViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(final InstructionData instructionData, final OnInstructionClickListener onClickListener) {
            titleTextView.setText(instructionData.getText());

            //relay button click to parent listener
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(instructionData);
                }
            });

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            labourRow.removeAllViews();
            inputRow.removeAllViews();
            outputRow.removeAllViews();

            for(int i = 0; i < instructionData.getLabour(); i++) {
                addInfoImage(inflater, labourRow, Constants.getLabourIcon());
            }

            for(int i = 0; i < instructionData.getInput(); i++) {
                addInfoImage(inflater, inputRow, Constants.getProductIcon(instructionData.getInputType()));
            }

            for(int i = 0; i < instructionData.getOutput(); i++) {
                addInfoImage(inflater, outputRow, Constants.getProductIcon(instructionData.getOutputType()));
            }
        }

        private void addInfoImage(LayoutInflater inflater, ViewGroup row, @DrawableRes int drawableId) {
            ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview_info_folded_instruction, row, false);
            imageView.setImageResource(drawableId);
            row.addView(imageView);
        }
    }
}
