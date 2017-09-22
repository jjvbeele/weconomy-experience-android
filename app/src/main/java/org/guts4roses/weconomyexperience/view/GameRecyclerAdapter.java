package org.guts4roses.weconomyexperience.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.guts4roses.weconomyexperience.model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 26-7-17.
 */

public class GameRecyclerAdapter extends RecyclerView.Adapter<GameRecyclerAdapter.GameViewHolder> {

    public interface OnClickListener {
        void onClick(GameData gameData);
    }

    private Map<String, GameData> gameDataMap;
    private OnClickListener removeGameListener;
    private OnClickListener clickGameListener;

    private boolean admin;

    public GameRecyclerAdapter(Map<String, GameData> gameDataList, OnClickListener clickGameListener, OnClickListener removeGameListener) {
        this.gameDataMap = gameDataList;
        this.clickGameListener = clickGameListener;
        this.removeGameListener = removeGameListener;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gameListItem = LayoutInflater.from(parent.getContext()).inflate(org.guts4roses.weconomyexperience.R.layout.listitem_game, parent, false);
        GameViewHolder gameViewHolder = new GameViewHolder(gameListItem);
        return gameViewHolder;
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        if(holder == null) {
            return;
        }
        final List<GameData> gameDataList = new ArrayList<>(gameDataMap.values());
        final GameData gameData = gameDataList.get(position);
        holder.setData(gameData.getName());
        holder.setOnGameDelete(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = gameDataList.indexOf(gameData);
                gameDataMap.remove(gameData.getId());
                notifyItemRemoved(index);

                removeGameListener.onClick(gameData);
            }
        });
        holder.setOnGameClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGameListener.onClick(gameData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameDataMap.size();
    }

    public void updateAdmin(boolean admin) {
        this.admin = admin;
        notifyDataSetChanged();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        @BindView(org.guts4roses.weconomyexperience.R.id.text_game_name)
        protected TextView gameNameTextView;

        @BindView(org.guts4roses.weconomyexperience.R.id.button_game_delete)
        protected View deleteGameButton;

        public GameViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(String gameName) {
            gameNameTextView.setText(gameName);
            if(admin) {
                deleteGameButton.setVisibility(View.VISIBLE);
            } else {
                deleteGameButton.setVisibility(View.GONE);
            }
        }

        public void setOnGameDelete(View.OnClickListener deleteGameListener) {
            deleteGameButton.setOnClickListener(deleteGameListener);
        }

        public void setOnGameClick(View.OnClickListener clickGameListener) {
            itemView.setOnClickListener(clickGameListener);
        }
    }
}