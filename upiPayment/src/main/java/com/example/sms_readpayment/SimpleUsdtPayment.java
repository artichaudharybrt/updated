package com.example.sms_readpayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sms_readpayment.ApiClasses.ApiLinks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplified USDT Payment class - No merchant authentication required
 * Works directly with own_pay API
 */
public class SimpleUsdtPayment {

    private static final String TAG = "SIMPLE_USDT_PAYMENT";

    // Using configuration from UsdtConfig
    private static final String OWN_PAY_API = UsdtConfig.OWN_PAY_API_BASE_URL;

    private Activity context;
    private ProgressDialog progressDialog;

    public SimpleUsdtPayment(Activity context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);

        Log.d(TAG, "=== SIMPLE USDT PAYMENT INITIALIZED ===");
        Log.d(TAG, "API Base URL: " + OWN_PAY_API);
    }

    /**
     * Start USDT deposit process
     */
    public void startDeposit(String userId, String amount) {
        Log.d(TAG, "=== STARTING USDT DEPOSIT ===");
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Amount: " + amount);

        generateWalletForDeposit(userId, amount);
    }

    /**
     * Start USDT withdrawal process
     */
    public void startWithdrawal(String userId, String amount, String withdrawAddress) {
        Log.d(TAG, "=== STARTING USDT WITHDRAWAL ===");
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Amount: " + amount);
        Log.d(TAG, "Withdraw Address: " + withdrawAddress);

        processWithdrawal(userId, amount, withdrawAddress);
    }

    /**
     * Generate wallet for deposit (no merchant auth)
     */
    private void generateWalletForDeposit(String userId, String amount) {
        Log.d(TAG, "Generating wallet for deposit...");

        progressDialog.setMessage("Generating wallet...");
        progressDialog.show();

        String url = OWN_PAY_API + "generate-wallet";
        Log.d(TAG, "API URL: " + url);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "=== WALLET GENERATION RESPONSE ===");
                        Log.d(TAG, "Response: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");

                            if (status) {
                                JSONObject wallet = jsonObject.getJSONObject("wallet");
                                String walletAddress = wallet.getString("address");

                                Log.d(TAG, "Wallet generated successfully: " + walletAddress);
                                progressDialog.dismiss();

                                // Open payment screen with generated wallet
                                openPaymentScreen(userId, amount, walletAddress);

                            } else {
                                String message = jsonObject.getString("message");
                                Log.e(TAG, "Wallet generation failed: " + message);
                                progressDialog.dismiss();
                                Toast.makeText(context, "Failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "=== WALLET GENERATION ERROR ===");
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Response: " + new String(error.networkResponse.data));
                        }
                        progressDialog.dismiss();
                        Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // NO MERCHANT AUTHENTICATION
                Log.d(TAG, "Headers: " + headers.toString());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("amount", amount);
                params.put("network", "BEP20");

                Log.d(TAG, "=== REQUEST PARAMETERS ===");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Log.d(TAG, entry.getKey() + ": " + entry.getValue());
                }

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1.0f));
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Process withdrawal (no merchant auth)
     */
    private void processWithdrawal(String userId, String amount, String withdrawAddress) {
        Log.d(TAG, "Processing withdrawal...");

        progressDialog.setMessage("Processing withdrawal...");
        progressDialog.show();

        String url = OWN_PAY_API + "withdraw";
        Log.d(TAG, "API URL: " + url);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "=== WITHDRAWAL RESPONSE ===");
                        Log.d(TAG, "Response: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");

                            progressDialog.dismiss();

                            if (status) {
                                Log.d(TAG, "Withdrawal successful: " + message);
                                Toast.makeText(context, "Success: " + message, Toast.LENGTH_LONG).show();
                                context.finish();
                            } else {
                                Log.e(TAG, "Withdrawal failed: " + message);
                                Toast.makeText(context, "Failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "=== WITHDRAWAL ERROR ===");
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Response: " + new String(error.networkResponse.data));
                        }
                        progressDialog.dismiss();
                        Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // NO MERCHANT AUTHENTICATION
                Log.d(TAG, "Headers: " + headers.toString());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("amount", amount);
                params.put("wallet_address", withdrawAddress);
                params.put("currency", "USDT");
                params.put("network", "BEP20");

                Log.d(TAG, "=== REQUEST PARAMETERS ===");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    Log.d(TAG, entry.getKey() + ": " + entry.getValue());
                }

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1.0f));
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Open payment screen with generated wallet
     */
    private void openPaymentScreen(String userId, String amount, String walletAddress) {
        Log.d(TAG, "Opening payment screen...");
        Log.d(TAG, "Wallet Address: " + walletAddress);

        Intent intent = new Intent(context, CheckPaymentStatus.class);
        intent.putExtra("payment_id", "USDT_" + System.currentTimeMillis() + "_" + userId);
        intent.putExtra("amount", amount);
        intent.putExtra("pay_address", walletAddress);
        intent.putExtra("payment_type", "USDT");
        intent.putExtra("network", "BEP20");
        intent.putExtra("user_id", userId);

        Log.d(TAG, "Starting CheckPaymentStatus activity");
        context.startActivity(intent);
    }
}
