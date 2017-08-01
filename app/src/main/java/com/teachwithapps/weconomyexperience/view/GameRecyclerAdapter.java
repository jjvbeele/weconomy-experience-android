package com.teachwithapps.weconomyexperience.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.model.GameData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 26-7-17.
 */

public class GameRecyclerAdapter extends RecyclerView.Adapter<GameRecyclerAdapter.GameViewHolder> {

    private List<GameData> gameDataList;

    public GameRecyclerAdapter(List<GameData> gameDataList) {
        this.gameDataList = gameDataList;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gameListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_game, parent, false);
        GameViewHolder gameViewHolder = new GameViewHolder(gameListItem);
        return gameViewHolder;
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        final GameData gameData = gameDataList.get(position);
        holder.setData(gameData.getGameName());
        holder.setOnGameDelete(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = gameDataList.indexOf(gameData);
                gameDataList.remove(gameData);
                notifyItemRemoved(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameDataList.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_game_name)
        protected TextView gameNameTextView;

        @BindView(R.id.button_game_delete)
        protected View deleteGameButton;

        public GameViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(String gameName) {
            gameNameTextView.setText(gameName);
        }

        public void setOnGameDelete(View.OnClickListener deleteGameListener) {
            deleteGameButton.setOnClickListener(deleteGameListener);
        }
    }
}