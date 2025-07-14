package com.gamegards.bigjackpot.RedeemCoins;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.model.ChipsBuyModel;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RedeemActivity extends BaseActivity {
    private static final String MY_PREFS_NAME = "Login_data";
    //ImageView img_back;
    ArrayList<ChipsBuyModel> historyModelArrayList;
    RedeemAdapter historyAdapter;
    RecyclerView rec_history;
    ProgressDialog progressDialog;
    LinearLayout linear_no_history;
    ImageView imgback,imaprofile;
    Context context;
    TextView tvRedeemWallet,txtwallet,txtproname,txtbonus,txtwin,txtunuti;

    // New withdrawal UI elements
    EditText etWithdrawAmount;
    Spinner spinnerWithdrawMethod;
    Button btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_redeem);
            // img_back=findViewById(R.id.img_back);
            context = this;
            rec_history = findViewById(R.id.rec_history);
            linear_no_history = findViewById(R.id.linear_no_history);
            imgback = findViewById(R.id.imgback);

            txtproname = findViewById(R.id.txtproname);
            txtwallet = findViewById(R.id.txtwallet);
            txtbonus = findViewById(R.id.txtbonus);
            txtwin = findViewById(R.id.txtwin);
            txtunuti = findViewById(R.id.txtunuti);
            imaprofile = findViewById(R.id.imaprofile);
    //        tvRedeemWallet= findViewById(R.id.tvRedeemWallet);

            // Initialize new withdrawal UI elements
            etWithdrawAmount = findViewById(R.id.et_withdraw_amount);
            spinnerWithdrawMethod = findViewById(R.id.spinner_withdraw_method);
            btnWithdraw = findViewById(R.id.btn_withdraw);

            // Setup withdrawal method spinner
            setupWithdrawMethodSpinner();

            // Setup withdraw button click listener
            setupWithdrawButton();

            if (imgback != null) {
                imgback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else {
                Log.e("RedeemActivity", "imgback view not found");
            }

            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

            if (imaprofile != null) {
                Picasso.get().load(Const.IMGAE_PATH + prefs.getString("profile_pic", "")).into(imaprofile);
            }

            if (txtwallet != null) {
                txtwallet.setText("" + prefs.getString("wallet", ""));
                Log.e("walletnew", "wallet" + txtwallet);
            }

            if (txtbonus != null) {
                txtbonus.setText("" + prefs.getString("bonus_wallet", ""));
                Log.e("bonusnew", "bonus" + txtbonus);
            }

            if (txtwin != null) {
                txtwin.setText("" + prefs.getString("winning_wallet", ""));
            }

            if (txtunuti != null) {
                txtunuti.setText("" + prefs.getString("unutilized_wallet", ""));
            }
    //        tvRedeemWallet.setText("Amount can be Redeem "+ Variables.CURRENCY_SYMBOL + prefs.getString("winning_wallet",""));

            if (rec_history != null) {
                rec_history.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            } else {
                Log.e("RedeemActivity", "rec_history view not found");
            }

            getChipsList();
        } catch (Exception e) {
            Log.e("RedeemActivity", "Error in onCreate", e);
            Toast.makeText(this, "Error initializing page", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if it can't be properly initialized
        }
    }

    public void getChipsList() {
        try {
            if (progressDialog != null) {
                progressDialog.show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.GET_Redeem_List,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("RedeemActivity", "" + Const.GET_Redeem_List + " \n" + response);
                            try {
                                if (isFinishing()) return;

                                JSONObject jsonObject = new JSONObject(response);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                if (code.equals("200")) {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    JSONArray jsonArray = jsonObject.getJSONArray("List");
                                    historyModelArrayList = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        ChipsBuyModel model = new ChipsBuyModel();
                                        model.setId(jsonObject1.getString("id"));
                                        model.title = jsonObject1.getString("title");
                                        model.Image = jsonObject1.getString("img");
                                        model.setProname(jsonObject1.getString("coin"));
                                        model.setAmount(jsonObject1.getString("amount"));
                                        // model.setTicket_id(jsonObject1.getString("desc"));

                                        historyModelArrayList.add(model);
                                    }

                                    if (rec_history != null) {
                                        historyAdapter = new RedeemAdapter(RedeemActivity.this, historyModelArrayList);
                                        rec_history.setAdapter(historyAdapter);
                                    } else {
                                        Log.e("RedeemActivity", "rec_history is null when trying to set adapter");
                                    }
                                    //   Toast.makeText(context,historyModelArrayList.size(),Toast.LENGTH_LONG).show();
                                } else {
                                    if (linear_no_history != null) {
                                        linear_no_history.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.e("RedeemActivity", "linear_no_history view is null");
                                        Toast.makeText(context, "No data available: " + message, Toast.LENGTH_SHORT).show();
                                    }

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    //  Toast.makeText(context,"No Data",Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.e("RedeemActivity", "JSON parsing error", e);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("RedeemActivity", "Error in response handling", e);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(context, "Error processing data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RedeemActivity", "Network error", error);
                    if (!isFinishing()) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(context, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("token", Const.TOKEN);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("token", prefs.getString("token", ""));
                    params.put("user_id", prefs.getString("user_id", ""));
                    Log.d("RedeemActivity_params", "getParams: " + params);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(RedeemActivity.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("RedeemActivity", "Error in getChipsList method", e);
            if (!isFinishing()) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(context, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupWithdrawMethodSpinner() {
        try {
            // Create array of withdrawal methods
            String[] withdrawMethods = {"USDT", "INR"};

            // Create adapter for spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, withdrawMethods) {
                @Override
                public View getView(int position, View convertView, android.view.ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setTextSize(16);
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    textView.setTextColor(getResources().getColor(R.color.black));
                    textView.setTextSize(16);
                    textView.setPadding(20, 15, 20, 15);
                    return view;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWithdrawMethod.setAdapter(adapter);

            Log.d("RedeemActivity", "Withdrawal method spinner setup completed");
        } catch (Exception e) {
            Log.e("RedeemActivity", "Error setting up withdrawal method spinner", e);
            Toast.makeText(this, "Error setting up withdrawal methods", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupWithdrawButton() {
        try {
            btnWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RedeemActivity", "Withdraw button clicked");
                    handleWithdrawRequest();
                }
            });
            Log.d("RedeemActivity", "Withdraw button setup completed");
        } catch (Exception e) {
            Log.e("RedeemActivity", "Error setting up withdraw button", e);
            Toast.makeText(this, "Error setting up withdraw button", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleWithdrawRequest() {
        try {
            // Get input values
            String amountStr = etWithdrawAmount.getText().toString().trim();
            String selectedMethod = spinnerWithdrawMethod.getSelectedItem().toString();

            Log.d("RedeemActivity", "Withdrawal request - Amount: " + amountStr + ", Method: " + selectedMethod);

            // Validate input
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter withdrawal amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check minimum withdrawal amount
            if (amount < 10) {
                Toast.makeText(this, "Minimum withdrawal amount is ₹10", Toast.LENGTH_SHORT).show();
                return;
            }

            // Process withdrawal based on selected method
            processWithdrawal(amount, selectedMethod);

        } catch (Exception e) {
            Log.e("RedeemActivity", "Error handling withdrawal request", e);
            Toast.makeText(this, "Error processing withdrawal request", Toast.LENGTH_SHORT).show();
        }
    }

    private void processWithdrawal(double amount, String method) {
        try {
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            callWithdrawalAPI(amount, method, prefs);
        } catch (Exception e) {
            Toast.makeText(this, "Error processing withdrawal. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callWithdrawalAPI(double amount, String withdrawalMode, SharedPreferences prefs) {
        try {
            String TAG = "WithdrawalAPI";

            // Get user data from SharedPreferences
            String userId = prefs.getString("user_id", "");
            String mobile = prefs.getString("mobile", "");
            String walletAddress = prefs.getString("wallet_address", "");

            Log.d(TAG, "User ID: " + userId);
            Log.d(TAG, "Mobile: " + mobile);
            Log.d(TAG, "Wallet Address: " + walletAddress);
            Log.d(TAG, "Amount: " + amount);
            Log.d(TAG, "Withdrawal Mode: " + withdrawalMode);

            if (userId.isEmpty()) {
                Log.e(TAG, "User ID is missing.");
                Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_LONG).show();
                return;
            }

            if (mobile.isEmpty()) {
                Log.e(TAG, "Mobile number is missing.");
                Toast.makeText(this, "Mobile number not found. Please update your profile.", Toast.LENGTH_LONG).show();
                return;
            }

            if (walletAddress.isEmpty()) {
                Log.e(TAG, "Wallet address is missing.");
                Toast.makeText(this, "Wallet address not found. Please update your profile.", Toast.LENGTH_LONG).show();
                return;
            }

            if (progressDialog != null) {
                progressDialog.setMessage("Processing withdrawal...");
                progressDialog.show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_WITHDRAW,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Response received: " + response);
                            try {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                JSONObject jsonObject = new JSONObject(response);
                                String code = jsonObject.optString("code", "");
                                String message = jsonObject.optString("message", "Unknown error");

                                Log.d(TAG, "Parsed code: " + code);
                                Log.d(TAG, "Parsed message: " + message);

                                if (code.equals("200")) {
                                    String redeemId = jsonObject.optString("redeem_id", "");
                                    String successMessage = "Withdrawal successful!";
                                    if (!redeemId.isEmpty()) {
                                        successMessage += " Redeem ID: " + redeemId;
                                    }

                                    Toast.makeText(RedeemActivity.this, successMessage, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, successMessage);

                                    etWithdrawAmount.setText("");
                                    finish();
                                } else {
                                    Log.e(TAG, "Withdrawal failed with message: " + message);
                                    Toast.makeText(RedeemActivity.this, "Withdrawal failed: " + message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON parsing error: " + e.getMessage());
                                Toast.makeText(RedeemActivity.this, "Error processing withdrawal response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Log.e(TAG, "Network error: " + error.toString());
                    Toast.makeText(RedeemActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    Log.d(TAG, "Headers: " + headers.toString());
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("user_id", Integer.parseInt(userId));
                        jsonBody.put("coin", amount);
                        jsonBody.put("mobile", mobile);
                        jsonBody.put("wallet_address", walletAddress);
                        jsonBody.put("withdrawal_mode", withdrawalMode);
                        jsonBody.put("admin_notes", "");

                        String bodyString = jsonBody.toString();
                        Log.d(TAG, "Request body: " + bodyString);

                        return bodyString.getBytes("utf-8");

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to create JSON body: " + e.getMessage());
                        throw new AuthFailureError("Failed to create JSON body: " + e.getMessage());
                    }
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Log.d(TAG, "Sending request to: " + Const.USER_WITHDRAW);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Log.e("WithdrawalAPI", "Exception during API call: " + e.getMessage());
            Toast.makeText(this, "Error initiating withdrawal. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}