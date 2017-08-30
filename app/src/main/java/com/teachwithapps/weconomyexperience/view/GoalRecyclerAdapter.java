package com.teachwithapps.weconomyexperience.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.model.GoalData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 30-8-17.
 */

public class GoalRecyclerAdapter extends RecyclerView.Adapter<GoalRecyclerAdapter.GoalViewHolder> {

    private List<GoalData> goalDataList;

    public GoalRecyclerAdapter(List<GoalData> goalDataList) {
        this.goalDataList = goalDataList;
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_goal_item, parent, false);
        return new GoalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GoalViewHolder holder, int position) {
        holder.setData(goalDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return goalDataList.size();
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goal_check)
        protected CheckBox goalCheck;

        @BindView(R.id.goal_text)
        protected TextView goalText;

        public GoalViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(final GoalData goalData) {
            goalText.setText(goalData.getText());
            goalCheck.setChecked(goalData.isCompleted());
            goalCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    goalData.setCompleted(isChecked);
                }
            });
        }
    }
}
