package com.teachwithapps.weconomyexperience.view;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teachwithapps.weconomyexperience.Constants;
import com.teachwithapps.weconomyexperience.GameActivity;
import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        @BindView(R.id.input_image)
        protected ImageView inputImageView;

        @BindView(R.id.output_image)
        protected ImageView outputImageView;

        private ScheduledInstructionData scheduledInstructionData;

        private List<ImageView> claimViewList;
        private List<ImageView> labourViewList;

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
                    ((GameActivity) itemView.getContext()).longClickScheduledInstruction(scheduledInstructionData);
                    return true;
                }
            });

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            labourCol1.removeAllViews();
            labourCol2.removeAllViews();
            inputCol.removeAllViews();
            outputCol.removeAllViews();

            labourViewList = new ArrayList<>();
            claimViewList = new ArrayList<>();

            inputImageView.setImageResource(Constants.getProductIcon(instructionData.getInputType()));
            outputImageView.setImageResource(Constants.getProductIcon(instructionData.getOutputType()));

            //set labour icons
            for (int i = 0; i < instructionData.getLabour(); i++) {
                if (i % 2 == 0) {
                    addInfoImage(inflater, labourCol1, Constants.getLabourIcon(), true, PROPERTY_LABOUR, i);
                } else {
                    addInfoImage(inflater, labourCol2, Constants.getLabourIcon(), true, PROPERTY_LABOUR, i);
                }
            }

            //set input icons
            boolean inputReady = true;
            for (int i = 0; i < instructionData.getInput(); i++) {
                addInfoImage(
                        inflater,
                        inputCol,
                        Constants.getProductIcon(instructionData.getInputType()),
                        inputReady,
                        PROPERTY_INPUT,
                        i);
            }

            //set output icons
            boolean outputReady = instructionData.getLabour() == scheduledInstructionData.getLabourList().size();
            for (int i = 0; i < instructionData.getOutput(); i++) {
                addInfoImage(
                        inflater,
                        outputCol,
                        Constants.getProductIcon(instructionData.getOutputType()),
                        outputReady,
                        PROPERTY_CLAIM,
                        i);
            }

            handlePlayerDataFromIds(scheduledInstructionData.getClaimMap(), claimViewList);
            handlePlayerDataFromIds(scheduledInstructionData.getLabourMap(), labourViewList);
        }

        /**
         * Handle iconlist, assign player avatars on the icons in the respective player map
         * Needs to be a map (instead of a list) to retain order
         * @param playerIdMap
         * @param iconList
         */
        private void handlePlayerDataFromIds(final Map<String, String> playerIdMap, final List<ImageView> iconList) {
            for(int i = 0; i < playerIdMap.keySet().size(); i++) {
                final int index = i;
                final String keyString = (String)playerIdMap.keySet().toArray()[index];
                String playerId = playerIdMap.get(keyString);
                ((GameActivity) itemView.getContext()).getPlayerById(
                        playerId,
                        new Returnable<PlayerData>() {
                            @Override
                            public void onResult(PlayerData data) {
                                Picasso.with(itemView.getContext())
                                        .load(data.getPhotoUrl())
                                        .into(iconList.get(Integer.parseInt(keyString)));
                            }
                        }
                );
            }
        }

        private void setLabour(int index) {
            ((GameActivity) itemView.getContext()).setLabour(scheduledInstructionData, index);
        }

        private void setClaim(int index) {
            ((GameActivity) itemView.getContext()).setClaim(scheduledInstructionData, index);
        }

        private void addInfoImage(
                LayoutInflater inflater,
                ViewGroup row,
                @DrawableRes int drawableId,
                final boolean ready,
                final int propertyType,
                final int index) {
            final ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview_info_scheduled_instruction, row, false);
            imageView.setImageResource(drawableId);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (propertyType == PROPERTY_LABOUR) {
                        setLabour(index);

                    } else if (propertyType == PROPERTY_CLAIM) {
                        setClaim(index);
                    }
                }
            });
            row.addView(imageView);

            if (propertyType == PROPERTY_LABOUR) {
                labourViewList.add(imageView);
            }

            if (propertyType == PROPERTY_CLAIM) {
                claimViewList.add(imageView);
            }

            if(ready) {
                imageView.setAlpha(1f);
            } else {
                imageView.setAlpha(0.4f);
            }
        }
    }
}
