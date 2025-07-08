package com.gamegards.gaming27.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.gaming27.Adapter.ChipsBuyPaymentAdapter;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.BaseActivity;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.Utils.Functions;
import com.gamegards.gaming27.Utils.SharePref;
import com.gamegards.gaming27.Utils.Variables;
import com.gamegards.gaming27.model.ChipsBuyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BuyChipsListCrypto extends BaseActivity {
    private static final String MY_PREFS_NAME = "Login_data";
    //ImageView img_back;
    ArrayList<ChipsBuyModel> historyModelArrayList;
    ChipsBuyPaymentAdapter historyPaymentAdapter;
    RecyclerView rec_history;
    ProgressDialog progressDialog;
    LinearLayout linear_no_history;
    ImageView imgback;
    EditText txtamount;
    TextView btn_add,cryptocoins;
    String crypto_inr = String.valueOf(90), dollar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chips_list_crypto);

//        RelativeLayout relativeLayout = findViewById(R.id.rlt_parent);
//        SetBackgroundImageAsDisplaySize(this,relativeLayout,R.drawable.bghome);


        ((TextView) findViewById(R.id.txtheader))
                .setText(Variables.DOLLAR.equalsIgnoreCase(Variables.DOLLAR) ? "Add Coins"
                        : Variables.DOLLAR.equalsIgnoreCase(Variables.DOLLAR) ? "Add Coins" : "Add Chips");


        // img_back=findViewById(R.id.img_back);
        rec_history=findViewById(R.id.rec_history);
        linear_no_history=findViewById(R.id.linear_no_history);
        imgback = findViewById(R.id.imgclosetop);
        txtamount = findViewById(R.id.txtamount);
        dollar = SharePref.getInstance().getString("dollar");
        Log.v("REs_CheckBuy","Dollar"+ 90);
        btn_add = findViewById(R.id.btn_add);
        cryptocoins = findViewById(R.id.cryptocoinss);
        txtamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is being changed.
                updateCryptoCoins(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has changed.
            }
        });

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtamount.getText().toString().equals("")){
                    Functions.showToast(BuyChipsListCrypto.this,"Please Enter Some Amount");

                }else if((Integer.parseInt(txtamount.getText().toString() ))< 1000){
                    Toast.makeText(BuyChipsListCrypto.this, "Minimum 1000 amount should be there", Toast.LENGTH_SHORT).show();
                }else {
//                    Intent intent = new Intent(BuyChipsList.this , BuyChipsDetails.class);
//                    intent.putExtra("amount",txtamount.getText().toString());
//                    startActivity(intent);

//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    context.startActivity(i);
                    Intent intent = new Intent(BuyChipsListCrypto.this, BuyCryptoPaymentDetails.class);
                    intent.putExtra("amount", cryptocoins.getText().toString());
                    intent.putExtra("coin", txtamount.getText().toString());
                    startActivity(intent);

                }

            }
        });
        progressDialog = new ProgressDialog(this);

        getChipsList();

      //  rec_history.setLayoutManager(new GridLayoutManager(this, 3));
        rec_history.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
       // rec_history.setLayoutManager(new LinearLayoutManager(this,, LinearLayoutManager.VERTICAL, false));

    }
    private void updateCryptoCoins(String amountText) {
        if (amountText.isEmpty()) {
            cryptocoins.setText(""); // Clear the text if amount is empty
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            double percent = Double.parseDouble("90"); // 90% as a decimal value
            double res = amount / percent;
            Log.v("REs_CheckBuy","Dollar"+ res);
            cryptocoins.setText(String.format("%.2f", res)); // Display result formatted to 2 decimal places
        } catch (NumberFormatException e) {
            e.printStackTrace();
            cryptocoins.setText(""); // Handle invalid input gracefully
        }
    }


    public void getChipsList(){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Const.GET_CHIP_PLAN,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG_responseC", "onResponse: "+response );
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code=jsonObject.getString("code");
                    String message=jsonObject.getString("message");
                    if (code.equals("200")){
                        progressDialog.dismiss();
                        JSONArray jsonArray=jsonObject.getJSONArray("PlanDetails");
                        historyModelArrayList=new ArrayList<>();
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            ChipsBuyModel model=new ChipsBuyModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setProname(jsonObject1.getString("coin"));
                            model.title = jsonObject1.getString("title");
                            model.setAmount(jsonObject1.getString("price"));
                           // model.setTicket_id(jsonObject1.getString("desc"));

                            historyModelArrayList.add(model);
                        }

                        historyPaymentAdapter=new ChipsBuyPaymentAdapter(BuyChipsListCrypto.this,historyModelArrayList);
                        rec_history.setAdapter(historyPaymentAdapter);
                    }
                    else {
                        linear_no_history.setVisibility(View.VISIBLE);
                        // Funtions.showToast(HistoryActivity.this, ""+message);
                        progressDialog.dismiss();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header= new HashMap<>();
                header.put("token",Const.TOKEN);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                HashMap<String,String> params= new HashMap<>();
                params.put("token", prefs.getString("token", ""));
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("coin_type","2");
                //params.put("user_id", SharedPref.getVal(HistoryActivity.this,SharedPref.id));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsListCrypto.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }
}
