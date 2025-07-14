package com.gamegards.bigjackpot.Details.Menu;

import static android.content.Context.MODE_PRIVATE;
import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

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

import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.MyAccountDetails.MySalaryAdapte;
import com.gamegards.bigjackpot.MyAccountDetails.MyWinnigmodel;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.RedeemCoins.RedeemModel;
import com.gamegards.bigjackpot.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogSalaryHistory {

    Dialog alert;
    Context context;
    TextView nofound;
    ProgressBar progressBar;
    RecyclerView rec_winning;
    MySalaryAdapte mySalaryAdapte;

    public DialogSalaryHistory(Context context) {
        this.context = context;
        alert = Functions.DialogInstance(context);
        alert.setContentView(R.layout.dialog_salaryincome);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvTitle = alert.findViewById(R.id.txtheader);
        tvTitle.setText("Salary Income");
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

        ApiRequest.Call_Api(context, Const.USER_SALARYINCOME, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");

                    if (code.equalsIgnoreCase("200")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("salary_bonus_data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            MyWinnigmodel model = new MyWinnigmodel();
                            model.setId(jsonObject1.optString("id"));
                            model.setCoin(jsonObject1.optString("bonus"));
                            model.setLevel(jsonObject1.optString("active_users"));
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

                mySalaryAdapte = new MySalaryAdapte(context,redeemModelArrayList);
                rec_winning.setAdapter(mySalaryAdapte);

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
