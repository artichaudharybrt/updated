package com.gamegards.bigjackpot._RedBlack.menu;

import static android.content.Context.MODE_PRIVATE;
import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.Utils.Variables;
import com.gamegards.bigjackpot._RedBlack.Model.RedBlackWinHistory;
import com.google.gson.Gson;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DialogRedBlackHistory {

    Context context;
    Callback callback;
    private static DialogRedBlackHistory mInstance;


    public static DialogRedBlackHistory getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DialogRedBlackHistory.class) {
                if (null == mInstance) {
                    mInstance = new DialogRedBlackHistory(context);
                }
            }
        }

        if(mInstance != null)
            mInstance.init(context);

        return mInstance;
    }

    /**
     * initialization of context, use only first time later it will use this again and again
     *
     * @param context app context: first time
     */
    public DialogRedBlackHistory init(Context context) {
        try {

            if (context != null) {
                this.context = context;
                initDialog();
            }

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return mInstance;
    }

    RecyclerView recJackpotHistory ;
    TextView tvHeader;
    ArrayList<RedBlackWinHistory.Winner> rulesModelArrayList = new ArrayList<RedBlackWinHistory.Winner>();
    private DialogRedBlackHistory initDialog() {
        dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_redblack_history);

        tvHeader = dialog.findViewById(R.id.txtheader);
        tvHeader.setText("Jackpot : ");
        recJackpotHistory = dialog.findViewById(R.id.recJackpotHistory);
        recJackpotHistory.setLayoutManager(new LinearLayoutManager(context));
        getJackpotHistoryList();

        return mInstance;
    }

    public DialogRedBlackHistory(Context context) {
        this.context = context;
    }

    public DialogRedBlackHistory() {
    }
    Dialog dialog;

    public DialogRedBlackHistory show() {

        dialog.findViewById(R.id.imgclosetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return mInstance;
    }

    public void dismiss(){
        if(dialog != null)
            dialog.dismiss();
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    void getJackpotHistoryList(){
        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id",prefs.getString("user_id", ""));
        params.put("token",""+ SharePref.getAuthorization());


        ApiRequest.Call_Api(context, Const.JackpotWinnerHistory, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if(resp != null)
                {
                    Gson gson = new Gson();
                    Reader reader = new StringReader(resp);
                    final RedBlackWinHistory jackpotWinHistory = gson.fromJson(reader, RedBlackWinHistory.class);

                    if(jackpotWinHistory.getCode() == 200)
                    {
                        rulesModelArrayList.clear();
                        rulesModelArrayList.addAll(jackpotWinHistory.getWinners());
                    }

                    recJackpotHistory.setAdapter(new jackpotHistoryAdapter(rulesModelArrayList));
                }
            }
        });
    }

    class jackpotHistoryAdapter extends RecyclerView.Adapter<jackpotHistoryAdapter.myHolder>{
        ArrayList<RedBlackWinHistory.Winner> rulesModelArrayList;
        public jackpotHistoryAdapter(ArrayList<RedBlackWinHistory.Winner> rulesModelArrayList) {
            this.rulesModelArrayList = rulesModelArrayList;
        }

        public void setDataList(ArrayList<RedBlackWinHistory.Winner> rulesModelArrayList){
            this.rulesModelArrayList.addAll(rulesModelArrayList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new myHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redblackhistory,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull myHolder holder, int position) {
            RedBlackWinHistory.Winner winnermodel = rulesModelArrayList.get(position);
            if(winnermodel != null)
                holder.bind(winnermodel);
        }

        @Override
        public int getItemCount() {
            return rulesModelArrayList.size();
        }

        public  class myHolder extends RecyclerView.ViewHolder{
            public myHolder(@NonNull View itemView) {
                super(itemView);
            }

            float total_amount = 0;
            public void bind(RedBlackWinHistory.Winner winnermodel) {

                RedBlackWinHistory.UserDatum userDataModel = winnermodel.getUserData().get(0);

                float winnig_amount = Float.parseFloat(userDataModel.getWinningAmount());

                total_amount += winnig_amount;

                tvHeader.setText("Jackpot : "+Variables.CURRENCY_SYMBOL+total_amount);

                getTextView(R.id.tvTime).setText(""+winnermodel.getTime());
                getTextView(R.id.tvWinnerId).setText(""+winnermodel.getWinners());
                getTextView(R.id.tvrewards).setText(""+winnermodel.getRewards());
                getTextView(R.id.txtName).setText(""+userDataModel.getName());
                getTextView(R.id.tvbetvalue).setText(
                        "BET "+ Variables.CURRENCY_SYMBOL +userDataModel.getAmount()+" "+
                                "GET "+ Variables.CURRENCY_SYMBOL +userDataModel.getWinningAmount());
                Glide.with(context).load(Const.IMGAE_PATH + userDataModel.getProfilePic()).into(((ImageView)itemView.findViewById(R.id.ivUserImage)));
                String[] typeCards = winnermodel.getType().split(",");
                int card1 = Functions.GetResourcePath(typeCards[0],context);
                int card2 = Functions.GetResourcePath(typeCards[1],context);
                int card3 = Functions.GetResourcePath(typeCards[2],context);
                Glide.with(context).load(Functions.getDrawable(context,card1)).into(((ImageView)itemView.findViewById(R.id.ivJackpotCard1)));
                Glide.with(context).load(Functions.getDrawable(context,card2)).into(((ImageView)itemView.findViewById(R.id.ivJackpotCard2)));
                Glide.with(context).load(Functions.getDrawable(context,card3)).into(((ImageView)itemView.findViewById(R.id.ivJackpotCard3)));
            }

            TextView getTextView(int id){
                return  ((TextView) itemView.findViewById(id));
            }
        }


    }
}
