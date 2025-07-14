package com.gamegards.bigjackpot._LuckJackpot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.bigjackpot.R;

import java.util.List;

public class BetHistoryAdapter extends RecyclerView.Adapter<BetHistoryAdapter.BetHistoryViewHolder> {

    private Context context;
    private List<BetHistoryModel> betHistoryList;

    public BetHistoryAdapter(Context context, List<BetHistoryModel> betHistoryList) {
        this.context = context;
        this.betHistoryList = betHistoryList;
    }

    @NonNull
    @Override
    public BetHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bet_history, parent, false);
        return new BetHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BetHistoryViewHolder holder, int position) {
        BetHistoryModel betHistory = betHistoryList.get(position);
        
        holder.tvBetType.setText(betHistory.getBetType());
        holder.tvAmount.setText("₹" + betHistory.getAmount());
        holder.tvGameId.setText("Game #" + betHistory.getGameId());
        holder.tvStatus.setText(betHistory.getStatus());
    }

    @Override
    public int getItemCount() {
        return betHistoryList.size();
    }

    public void updateBetHistory(List<BetHistoryModel> newBetHistory) {
        this.betHistoryList = newBetHistory;
        notifyDataSetChanged();
    }

    public static class BetHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvBetType, tvAmount, tvGameId, tvStatus;

        public BetHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBetType = itemView.findViewById(R.id.tvBetType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvGameId = itemView.findViewById(R.id.tvGameId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
