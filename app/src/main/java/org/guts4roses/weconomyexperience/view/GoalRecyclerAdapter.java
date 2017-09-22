package org.guts4roses.weconomyexperience.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.guts4roses.weconomyexperience.ViewGoalsActivity;
import org.guts4roses.weconomyexperience.model.SelectedGoalData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 30-8-17.
 */

public class GoalRecyclerAdapter extends RecyclerView.Adapter<GoalRecyclerAdapter.GoalViewHolder> {

    private List<SelectedGoalData> goalDataList;

    public GoalRecyclerAdapter(List<SelectedGoalData> goalDataList) {
        this.goalDataList = goalDataList;
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(org.guts4roses.weconomyexperience.R.layout.view_goal_item, parent, false);
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

        @BindView(org.guts4roses.weconomyexperience.R.id.goal_check)
        protected CheckBox goalCheck;

        @BindView(org.guts4roses.weconomyexperience.R.id.goal_text)
        protected TextView goalText;

        @BindView(org.guts4roses.weconomyexperience.R.id.player_name)
        protected TextView playerNameText;

        @BindView(org.guts4roses.weconomyexperience.R.id.avatar)
        protected ImageView avatar;

        public GoalViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(final SelectedGoalData selectedGoalData) {
            Bitmap playerBitmap = selectedGoalData.getPlayerData().getBitmap();
            if(playerBitmap == null) {
                Picasso.with(itemView.getContext())
                        .load(selectedGoalData.getPlayerData().getPhotoUrl())
                        .into(avatar);
            } else {
                avatar.setImageBitmap(playerBitmap);
            }

            if (selectedGoalData.getGoalData() != null) {
                goalText.setText(selectedGoalData
                        .getGoalData()
                        .getText());
            }

            goalCheck.setChecked(selectedGoalData.getRealised());
            goalCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selectedGoalData.setRealised(isChecked);
                    ((ViewGoalsActivity) itemView.getContext()).onCheckSelectedGoal(selectedGoalData);
                }
            });
            playerNameText.setText(selectedGoalData.getPlayerData().getName());

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((ViewGoalsActivity) itemView.getContext()).onLongClickSelectedGoal(selectedGoalData);
                    return false;
                }
            });
        }
    }
}
