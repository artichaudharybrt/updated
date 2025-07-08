package com.gamegards.gaming27.Details.Menu;

import static android.content.Context.MODE_PRIVATE;
import static com.gamegards.gaming27.Activity.Homepage.MY_PREFS_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Interface.ApiRequest;
import com.gamegards.gaming27.Interface.Callback;
import com.gamegards.gaming27.MyAccountDetails.MyRebateAdapte;
import com.gamegards.gaming27.MyAccountDetails.MyWinnigmodel;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.RedeemCoins.RedeemModel;
import com.gamegards.gaming27.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogRebateHistory {

    Dialog alert;
    Context context;
    TextView nofound;
    ProgressBar progressBar;
    RecyclerView rec_winning;
    MyRebateAdapte myRebateAdapte;

    public DialogRebateHistory(Context context) {
        this.context = context;
        alert = Functions.DialogInstance(context);
        alert.setContentView(R.layout.dialog_rebate);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvTitle = alert.findViewById(R.id.txtheader);
        tvTitle.setText("Rebate History");
        nofound = alert.findViewById(R.id.txtnotfound);
        progressBar = alert.findViewById(R.id.progressBar);
        rec_winning = alert.findViewById(R.id.rec_winning);
        rec_winning.setLayoutManager(new LinearLayoutManager(context));

        alert.findViewById(R.id.imgclosetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        SwipeRefreshLayout swiperefresh = alert.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CALL_API_GET_LIST();
                swiperefresh.setRefreshing(false);
            }
        });


        CALL_API_GET_LIST();
    }

    public void show(){
        alert.show();
        Functions.setDialogParams(alert);
    }
    public void dismiss(){alert.dismiss();}

    private void CALL_API_GET_LIST(){

        NoDataVisible(false);
        final ArrayList<MyWinnigmodel> redeemModelArrayList = new ArrayList();

        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id",prefs.getString("user_id", ""));
        params.put("token",prefs.getString("token", ""));

        ApiRequest.Call_Api(context, Const.USER_REBATE, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
//                    String message = jsonObject.getString("message");

                    if (code.equalsIgnoreCase("200")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            MyWinnigmodel model = new MyWinnigmodel();
                            model.setId(jsonObject1.optString("id"));
                            model.setCoin(jsonObject1.optString("coin"));
                            model.setLevel(jsonObject1.optString("bet_amount"));
//                            model.setPurchase_user_id(jsonObject1.optString("purchase_user_id"));
                            model.added_date = jsonObject1.optString("added_date");
                            model.ViewType = RedeemModel.TRANSACTION_LIST;


                            redeemModelArrayList.add(model);
                        }

                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(redeemModelArrayList.size() <= 0)
                    NoDataVisible(true);

                myRebateAdapte = new MyRebateAdapte(context,redeemModelArrayList);
                rec_winning.setAdapter(myRebateAdapte);

                HideProgressBar(true);
            }
        });
    }

    private void NoDataVisible(boolean visible){

        nofound.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void HideProgressBar(boolean gone){
        progressBar.setVisibility(!gone ? View.VISIBLE : View.GONE);
    }

}
