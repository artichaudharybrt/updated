package com.gamegards.gaming27.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.gaming27.MyAccountDetails.MyWinnigmodel;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.Utils.Functions;

import java.util.ArrayList;

public class AviatorMyHistoryAdapter extends RecyclerView.Adapter<AviatorMyHistoryAdapter.Myholder> {

    Context context;
    ArrayList<MyWinnigmodel> myWinnigmodelArrayList;
    private int[] mColors;

    public AviatorMyHistoryAdapter(Context context, ArrayList<MyWinnigmodel> myWinnigmodelArrayList) {

        this.context = context;
        this.myWinnigmodelArrayList = myWinnigmodelArrayList;
        mColors = new int[]{0xFF45B3F2, 0xFFB0278F, 0xFF9F5CFF};
    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.vertical_winnig_itemview,parent,false);

        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {

        MyWinnigmodel myWinnigmodel = myWinnigmodelArrayList.get(position);

        holder.tvName.setText(""+ Functions.maskString(myWinnigmodel.getName()));
        holder.tvAmount.setText(""+myWinnigmodel.amount);
        holder.tvCash.setText(""+myWinnigmodel.winning_amount);

        int colorIndex = position % mColors.length;
        holder.tvBet.setTextColor(mColors[colorIndex]);
        holder.tvBet.setText(""+myWinnigmodel.bet+"x");

        if(myWinnigmodel.winning_amount.equals("0.00")){
            holder.tvCash.setTextColor(context.getResources().getColor(R.color.red));
        }else {
            holder.tvCash.setTextColor(context.getResources().getColor(R.color.green));
        }

    }

    @Override
    public int getItemCount() {
        return myWinnigmodelArrayList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{

        TextView tvName,tvAmount,tvCash,tvBet;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCash = itemView.findViewById(R.id.tvCash);
            tvBet = itemView.findViewById(R.id.tvBet);

        }
    }

}
