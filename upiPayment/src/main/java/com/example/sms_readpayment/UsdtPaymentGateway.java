package com.example.sms_readpayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.sms_readpayment.Interface.PaymentCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * USDT Payment Gateway class for handling USDT payments
 * Supports both BEP20 (Binance Smart Chain) and TRC20 (TRON) networks
 * Integrates with own_pay.js backend for wallet generation and payment processing
 */
public class UsdtPaymentGateway {

    private Activity context;
    private PaymentCallback callback;
    private ProgressDialog progressDialog;
    private String TAG = "UsdtPaymentGateway";

    // Payment details
    private String orderId = "";
    private String amount = "";
    private String walletAddress = "";
    private String walletPrivateKey = ""; // Store private key securely
    private String network = "BEP20"; // Default network (BEP20 or TRC20)
    private String merchantId = "";
    private String merchantSecret = "";
    private String userId = "";

    // Custom API endpoints for own_pay.js integration
    private static final String GENERATE_WALLET_ENDPOINT = "http://your-server-address/api/generate-wallet";
    private static final String START_MONITORING_ENDPOINT = "http://your-server-address/api/start-monitoring";
    private static final String CHECK_PAYMENT_STATUS_ENDPOINT = "http://your-server-address/api/check-payment-status";

    public UsdtPaymentGateway(Activity context, PaymentCallback callback) {
        this.context = context;
        this.callback = callback;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Processing payment...");
        this.progressDialog.setCancelable(false);
    }

    /**
     * Start USDT payment process
     * @param userId User ID
     * @param amount Amount to pay
     * @param merchantId Merchant ID
     * @param merchantSecret Merchant Secret
     * @param network Network type (BEP20 or TRC20)
     */
    public void startPayment(String userId, String amount, String merchantId, String merchantSecret, String network) {
        this.userId = userId;
        this.amount = amount;
        this.merchantId = merchantId;
        this.merchantSecret = merchantSecret;
        this.network = network;

        // First generate a new wallet for this transaction
        generateNewWallet();
    }

    /**
     * Generate a new wallet for the transaction
     */
    private void generateNewWallet() {
        progressDialog.show();
        progressDialog.setMessage("Generating secure wallet...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERATE_WALLET_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "generate_wallet response: " + response);

                                boolean status = jsonObject.getBoolean("status");

                                if (status) {
                                    // Extract wallet details
                                    JSONObject walletObj = jsonObject.getJSONObject("wallet");
                                    walletAddress = walletObj.getString("address");
                                    walletPrivateKey = walletObj.getString("privateKey");

                                    Log.d(TAG, "Wallet generated: " + walletAddress);

                                    // Save wallet to user account
                                    saveWalletToUserAccount();
                                } else {
                                    String message = jsonObject.getString("message");
                                    progressDialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    callback.onPaymentFailed(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error generating wallet: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    return header;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }

    /**
     * Save the generated wallet to user account
     */
    private void saveWalletToUserAccount() {
        progressDialog.setMessage("Setting up payment...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiLinks.BASE_URL + "save-wallet",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "save_wallet response: " + response);

                                boolean status = jsonObject.getBoolean("status");
                                String message = jsonObject.getString("message");

                                if (status) {
                                    // Now initiate the payment with the new wallet
                                    initiateUsdtPayment();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    callback.onPaymentFailed(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error saving wallet: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("token", ApiLinks.Token);
                    header.put("merchantid", merchantId);
                    header.put("merchantsecret", merchantSecret);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", userId);
                    params.put("walletAddress", walletAddress);
                    params.put("walletPrivateKey", walletPrivateKey);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }

    /**
     * Initiate USDT payment by calling the API
     */
    private void initiateUsdtPayment() {
        progressDialog.setMessage("Preparing payment details...");

        // Generate a unique order ID
        orderId = "USDT_" + System.currentTimeMillis() + "_" + userId;

        if (ApiLinks.isNetworkAvailable(context)) {
            // Now we'll open the payment screen directly with our generated wallet
            // The actual monitoring will be started when the user confirms
            openPaymentStatusScreen(orderId, amount, walletAddress);
            progressDialog.dismiss();
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }

    /**
     * Open payment status screen to track payment
     */
    private void openPaymentStatusScreen(String orderId, String amount, String walletAddress) {
        Intent intent = new Intent(context, CheckPaymentStatus.class);
        intent.putExtra("payment_id", orderId);
        intent.putExtra("amount", amount);
        intent.putExtra("pay_address", walletAddress);
        intent.putExtra("payment_type", "USDT");
        intent.putExtra("network", network);
        intent.putExtra("merchant_id", merchantId);
        intent.putExtra("merchant_secret", merchantSecret);
        intent.putExtra("user_id", userId);
        context.startActivity(intent);
    }

    /**
     * Start monitoring the wallet for incoming USDT
     * This should be called when the user confirms they've sent the payment
     */
    public void startMonitoring() {
        progressDialog.show();
        progressDialog.setMessage("Checking payment status...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, START_MONITORING_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "start_monitoring response: " + response);

                                boolean status = jsonObject.getBoolean("status");

                                if (status) {
                                    JSONObject result = jsonObject.getJSONObject("result");
                                    boolean found = result.getBoolean("found");

                                    if (found) {
                                        // Payment found and processed
                                        String amount = result.getString("amount");
                                        progressDialog.dismiss();
                                        callback.onPaymentSuccess(orderId, "Payment of " + amount + " USDT received successfully");
                                    } else {
                                        // Payment not found yet
                                        String message = result.getString("message");
                                        progressDialog.dismiss();
                                        callback.onPaymentPending(message);
                                    }
                                } else {
                                    String message = jsonObject.getString("message");
                                    progressDialog.dismiss();
                                    callback.onPaymentFailed(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error monitoring payment: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("walletAddress", walletAddress);
                    params.put("walletPrivateKey", walletPrivateKey);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000, // 60 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }

    /**
     * Verify USDT payment status
     * @param orderId Order ID to check
     */
    public void verifyPayment(String orderId) {
        progressDialog.show();
        progressDialog.setMessage("Verifying payment...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_PAYMENT_STATUS_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "verify_payment response: " + response);

                                boolean status = jsonObject.getBoolean("status");
                                String message = jsonObject.getString("message");

                                if (status) {
                                    progressDialog.dismiss();
                                    callback.onPaymentSuccess(orderId, message);
                                } else {
                                    progressDialog.dismiss();
                                    callback.onPaymentPending(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error verifying payment: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_id", orderId);
                    params.put("wallet_address", walletAddress);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }
    }

    /**
     * Start monitoring the wallet for incoming USDT
     * This should be called when the user confirms they've sent the payment
     */
    public void startMonitoring() {
        progressDialog.show();
        progressDialog.setMessage("Checking payment status...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, START_MONITORING_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "start_monitoring response: " + response);

                                boolean status = jsonObject.getBoolean("status");

                                if (status) {
                                    JSONObject result = jsonObject.getJSONObject("result");
                                    boolean found = result.getBoolean("found");

                                    if (found) {
                                        // Payment found and processed
                                        String amount = result.getString("amount");
                                        progressDialog.dismiss();
                                        callback.onPaymentSuccess(orderId, "Payment of " + amount + " USDT received successfully");
                                    } else {
                                        // Payment not found yet
                                        String message = result.getString("message");
                                        progressDialog.dismiss();
                                        callback.onPaymentPending(message);
                                    }
                                } else {
                                    String message = jsonObject.getString("message");
                                    progressDialog.dismiss();
                                    callback.onPaymentFailed(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error monitoring payment: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("walletAddress", walletAddress);
                    params.put("walletPrivateKey", walletPrivateKey);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000, // 60 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }

    /**
     * Verify USDT payment status
     * @param orderId Order ID to check
     */
    public void verifyPayment(String orderId) {
        progressDialog.show();
        progressDialog.setMessage("Verifying payment...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_PAYMENT_STATUS_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG, "verify_payment response: " + response);

                                boolean status = jsonObject.getBoolean("status");
                                String message = jsonObject.getString("message");

                                if (status) {
                                    progressDialog.dismiss();
                                    callback.onPaymentSuccess(orderId, message);
                                } else {
                                    progressDialog.dismiss();
                                    callback.onPaymentPending(message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error verifying payment: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    callback.onPaymentFailed("Network error: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_id", orderId);
                    params.put("wallet_address", walletAddress);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            callback.onPaymentFailed("No internet connection");
        }
    }