package com.gamegards.bigjackpot.Details.Menu;

import static android.content.Context.MODE_PRIVATE;
import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.MyAccountDetails.MyWinningAdapte;
import com.gamegards.bigjackpot.MyAccountDetails.QrRedeemHistoryAdapter;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.RedeemCoins.RedeemModel;
import com.gamegards.bigjackpot.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DialogQRHistory {

    Dialog alert;
    Context context;
    TextView nofound;
    ProgressBar progressBar;
    RecyclerView rec_winning;
    MyWinningAdapte myWinningAdapte;

    public DialogQRHistory(Context context) {
        this.context = context;
        alert = Functions.DialogInstance(context);
        alert.setContentView(R.layout.dialog_qr_history);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nofound = alert.findViewById(R.id.txtnotfound);
        progressBar = alert.findViewById(R.id.progressBar);
        rec_winning = alert.findViewById(R.id.rec_winning);
        rec_winning.setLayoutManager(new LinearLayoutManager(context));

        TextView txtHeader = alert.findViewById(R.id.txtheader);
        txtHeader.setText("QR History");

        View lnrStatus = alert.findViewById(R.id.lnrStatus);
        lnrStatus.setVisibility(View.VISIBLE);

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
        final ArrayList<RedeemModel> redeemModelArrayList = new ArrayList();

        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id",prefs.getString("user_id", ""));

        ApiRequest.Call_Api(context, Const.QR_history, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                // progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    Log.e("RES_QR","Check_QR"+resp);

                    if (code.equalsIgnoreCase("200")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("purchase_history");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            RedeemModel model = new RedeemModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setCoin(jsonObject1.getString("price"));
                            model.setMobile(jsonObject1.getString("utr"));
//                            model.setUser_name(jsonObject1.getString("user_name"));
//                            model.setUser_mobile(jsonObject1.getString("user_mobile"));
                            model.setStatus(jsonObject1.getString("status"));
                            model.setUpdated_date(jsonObject1.getString("added_date"));
                            model.ViewType = RedeemModel.REDEEM_LIST;


                            redeemModelArrayList.add(model);
                        }

                        Collections.reverse(redeemModelArrayList);


                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();


                }

                if(redeemModelArrayList.size() <= 0)
                    NoDataVisible(true);

                QrRedeemHistoryAdapter userWinnerAdapter = new QrRedeemHistoryAdapter(context, redeemModelArrayList);
                rec_winning.setAdapter(userWinnerAdapter);


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
