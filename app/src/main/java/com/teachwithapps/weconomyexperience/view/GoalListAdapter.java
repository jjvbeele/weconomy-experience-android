package com.teachwithapps.weconomyexperience.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class GoalListAdapter extends ArrayAdapter<GoalData> {

    @BindView(R.id.goal_check)
    protected CheckBox goalCheck;

    @BindView(R.id.goal_text)
    protected TextView goalText;

    public GoalListAdapter(Context context, List<GoalData> goalDataList) {
        super(context, R.layout.view_goal_item, goalDataList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ButterKnife.bind(this, view);

        final GoalData goalData = getItem(position);
        if(goalData != null) {
            goalText.setText(goalData.getText());
            goalCheck.setChecked(goalData.isCompleted());
            goalCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    goalData.setCompleted(isChecked);
                }
            });
        }

        return view;
    }
}
