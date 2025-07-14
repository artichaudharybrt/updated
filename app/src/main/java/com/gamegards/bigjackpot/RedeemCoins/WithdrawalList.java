package com.gamegards.bigjackpot.RedeemCoins;

import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WithdrawalList extends AppCompatActivity {

    TextView txtheader;
    ImageView imgclosetop;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_withdrawal_list);

            progressDialog = new ProgressDialog(WithdrawalList.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");

            txtheader = findViewById(R.id.txtheader);
            if (txtheader != null) {
                txtheader.setText("Withdrawal Logs");
            } else {
                Log.e("WithdrawalList", "txtheader view not found");
            }

            imgclosetop = findViewById(R.id.imgclosetop);
            if (imgclosetop != null) {
                imgclosetop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            } else {
                Log.e("WithdrawalList", "imgclosetop view not found");
            }

            View applyButton = findViewById(R.id.btn_apply);
            if (applyButton != null) {
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(WithdrawalList.this, RedeemActivity.class);
                            // Add flags to create a new task and clear top to avoid issues with existing instances
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("WithdrawalList", "Error starting RedeemActivity", e);
                            Toast.makeText(WithdrawalList.this, "Error opening withdrawal page", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Log.e("WithdrawalList", "btn_apply view not found");
            }

//        getList();
        } catch (Exception e) {
            Log.e("WithdrawalList", "Error in onCreate", e);
            Toast.makeText(this, "Error initializing page", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if it can't be properly initialized
        }
    }

    public void onResume() {
        try {
            super.onResume();
            getList();
        } catch (Exception e) {
            Log.e("WithdrawalList", "Error in onResume", e);
            if (!isFinishing()) {
                Toast.makeText(this, "Error loading data", Toast.LENGTH_LONG).show();
            }
        }
    }

    LinearLayout lnrcancelist;

    public void showList(JSONArray last_bet) {
        try {
            Log.d("WITHDRAWAL_LOGS_DISPLAY", "=== DISPLAYING WITHDRAWAL LOGS ===");
            Log.d("WITHDRAWAL_LOGS_DISPLAY", "Total logs to display: " + last_bet.length());
            Log.d("json_list", String.valueOf(last_bet));

            lnrcancelist = findViewById(R.id.lnrlastView);
            if (lnrcancelist == null) {
                Log.e("WITHDRAWAL_LOGS_DISPLAY", "lnrlastView not found in layout");
                Log.e("WithdrawalList", "lnrlastView not found in layout");
                Toast.makeText(WithdrawalList.this, "Layout error. Please try again later.", Toast.LENGTH_LONG).show();
                return;
            }

            lnrcancelist.removeAllViews();
            Log.d("WITHDRAWAL_LOGS_DISPLAY", "Cleared existing views, starting to add withdrawal logs");

            for (int i = 0; i < last_bet.length(); i++) {
                try {
                    JSONObject betObject = last_bet.getJSONObject(i);
                    String id = betObject.optString("id", "");
                    String coin = betObject.optString("coin", "0");
                    String withdrawal_mode = betObject.optString("withdrawal_mode", "USDT");
                    String status = betObject.optString("status", "0");
                    String tx_hash = betObject.optString("tx_hash", "");
                    String created_date = betObject.optString("created_date", "");

                    Log.d("WITHDRAWAL_LOGS_DISPLAY", "Processing withdrawal log " + (i + 1) + "/" + last_bet.length());
                    Log.d("WITHDRAWAL_LOGS_DISPLAY", "ID: " + id + ", Amount: " + coin + ", Mode: " + withdrawal_mode + ", Status: " + status + ", TxHash: " + tx_hash + ", Date: " + created_date);

                    addLastWinBet(coin, withdrawal_mode, status, tx_hash, created_date);
                } catch (JSONException e) {
                    Log.e("WITHDRAWAL_LOGS_DISPLAY", "Error parsing JSON object at index " + i, e);
                    Log.e("WithdrawalList", "Error parsing JSON object at index " + i, e);
                    // Continue with the next item instead of crashing
                }
            }
            Log.d("WITHDRAWAL_LOGS_DISPLAY", "Finished displaying all withdrawal logs");
        } catch (Exception e) {
            Log.e("WithdrawalList", "Error showing list", e);
            Toast.makeText(WithdrawalList.this, "Error loading data. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    int pos = 1;

    private void addLastWinBet(String coin, String withdrawal_mode, String status, String tx_hash, String date) {
        try {
            View view = LayoutInflater.from(WithdrawalList.this).inflate(R.layout.view_color_lastwin_list, null);
            if (view == null) {
                 return;
            }

            TextView tvSerial = view.findViewById(R.id.tvSerial); // Serial Number
            TextView tvFiled1 = view.findViewById(R.id.tvFiled1); // Amount
            TextView tvFiled2 = view.findViewById(R.id.tvFiled2); // Withdrawal Mode
            TextView tvFiled3 = view.findViewById(R.id.tvFiled3); // Status
            Button btnViewTxHash = view.findViewById(R.id.btnViewTxHash); // View Button
            TextView tvFiled4 = view.findViewById(R.id.tvFiled4); // Date

            if (tvSerial == null || tvFiled1 == null || tvFiled2 == null || tvFiled3 == null || btnViewTxHash == null || tvFiled4 == null) {
                return;
            }

            // Set Serial Number
            tvSerial.setText(String.valueOf(pos++));

            // Set Amount
            tvFiled1.setText(coin);

            // Set Withdrawal Mode
            tvFiled2.setText(withdrawal_mode);

            // Set Status with proper text
            String statusText = "";
            if (status.equals("0")) {
                statusText = "Pending";
                tvFiled3.setText("Pending");
            } else if (status.equals("1")) {
                statusText = "Approved";
                tvFiled3.setText("Approved");
            } else if (status.equals("2")) {
                statusText = "Rejected";
                tvFiled3.setText("Rejected");
            } else {
                statusText = "Unknown (" + status + ")";
                tvFiled3.setText(statusText);
            }

            // Set Date
            tvFiled4.setText(date);

            // Set up View button click listener for tx_hash
            btnViewTxHash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("WITHDRAWAL_LOGS_ITEM", "View button clicked for Tx Hash: " + tx_hash);

                    if (tx_hash != null && !tx_hash.isEmpty() && !tx_hash.equals("null")) {
                        // Navigate to BSC scan transaction page
                        String bscScanUrl = "https://bscscan.com/tx/" + tx_hash;
                          try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bscScanUrl));
                            WithdrawalList.this.startActivity(intent);
                            Toast.makeText(WithdrawalList.this, "Opening transaction details...", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                             Toast.makeText(WithdrawalList.this, "Error opening transaction details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                         Toast.makeText(WithdrawalList.this, "Transaction hash not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
              if (lnrcancelist != null) {
                lnrcancelist.addView(view);
            } else {
            }
        } catch (Exception e) {
         }
    }

    private void getList() {      //list
        try {
               if (!isFinishing()) {
                progressDialog.show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_WITHDRAWAL_LOGS,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                if (isFinishing()) return;
                                      try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String code = jsonObject.optString("code", "");
                                    String message = jsonObject.optString("message", "");

                                         if (code.equalsIgnoreCase("200")) {
                                        progressDialog.dismiss();
                                        JSONArray data = jsonObject.optJSONArray("data");
                                        if (data == null) {
                                              Toast.makeText(WithdrawalList.this, "No data received from server", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        int data_ = data.length();
                                         if (data_ == 0) {
                                              Toast.makeText(WithdrawalList.this, "No Logs found!", Toast.LENGTH_LONG).show();
                                        } else {
                                             for (int i = 0; i < data.length(); i++) {
                                                try {
                                                    JSONObject logItem = data.getJSONObject(i);
                                                        } catch (JSONException e) {
                                                 }
                                            }
                                        }
                                        showList(data);

                                    } else {
                                        if (jsonObject.has("message")) {
                                            String errorMessage = jsonObject.getString("message");
                                            Functions.showToast(WithdrawalList.this, errorMessage);
                                        } else {
                                             Functions.showToast(WithdrawalList.this, "Unknown error occurred");
                                        }
                                        progressDialog.dismiss();
                                    }
                                 } catch (JSONException e) {
                                    Log.e("WithdrawalList", "JSON parsing error", e);
                                    Toast.makeText(WithdrawalList.this, "Error parsing server response", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            } catch (Exception e) {
                                Log.e("WithdrawalList", "Error in response handling", e);
                                if (!isFinishing()) {
                                    Toast.makeText(WithdrawalList.this, "Error processing data", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                     if (error.networkResponse != null) {
                        }
                     if (!isFinishing()) {
                        Functions.showToast(WithdrawalList.this, "Network error. Please check your connection.");
                        progressDialog.dismiss();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences prefs = WithdrawalList.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String userId = prefs.getString("user_id", "");
                    String token = prefs.getString("token", "");

                    params.put("user_id", userId);
                    params.put("token", token);
  return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("token", Const.TOKEN);
                    return headers;
                }
            };

            try {
                Volley.newRequestQueue(WithdrawalList.this).add(stringRequest);
            } catch (Exception e) {
                Log.e("WithdrawalList", "Error adding request to queue", e);
                if (!isFinishing()) {
                    Toast.makeText(WithdrawalList.this, "Network error. Please try again later.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            Log.e("WithdrawalList", "Error in getList method", e);
            if (!isFinishing()) {
                Toast.makeText(WithdrawalList.this, "An error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }
}