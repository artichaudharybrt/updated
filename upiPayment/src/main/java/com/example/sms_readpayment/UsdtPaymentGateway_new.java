package com.example.sms_readpayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

    // Comprehensive logging tags
    private String TAG = "UsdtPaymentGateway";
    private static final String TAG_GATEWAY = "USDT_GATEWAY";
    private static final String TAG_WALLET = "USDT_WALLET";
    private static final String TAG_API_CALL = "USDT_API_CALL";
    private static final String TAG_MONITORING = "USDT_MONITORING";
    private static final String TAG_GATEWAY_ERROR = "USDT_GATEWAY_ERROR";

    // Payment details
    private String orderId = "";
    private String amount = "";
    private String walletAddress = "";
    private String walletPrivateKey = ""; // Store private key securely
    private String network = "BEP20"; // Default network (BEP20 or TRC20)
    private String userId = "";

    // Direct own_pay API endpoints (no merchant authentication)
    private static final String OWN_PAY_BASE_URL = "http://localhost:3000/api/";
    private static final String GENERATE_WALLET_ENDPOINT = OWN_PAY_BASE_URL + "generate-wallet";
    private static final String START_MONITORING_ENDPOINT = OWN_PAY_BASE_URL + "start-monitoring";
    private static final String CHECK_PAYMENT_STATUS_ENDPOINT = OWN_PAY_BASE_URL + "check-payment-status";

    public UsdtPaymentGateway(Activity context, PaymentCallback callback) {
        Log.d(TAG_GATEWAY, "=== INITIALIZING USDT PAYMENT GATEWAY ===");
        this.context = context;
        this.callback = callback;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Processing payment...");
        this.progressDialog.setCancelable(false);
        Log.d(TAG_GATEWAY, "USDT Payment Gateway initialized successfully");
        Log.d(TAG_GATEWAY, "API Endpoints configured:");
        Log.d(TAG_GATEWAY, "  Generate Wallet: " + GENERATE_WALLET_ENDPOINT);
        Log.d(TAG_GATEWAY, "  Start Monitoring: " + START_MONITORING_ENDPOINT);
        Log.d(TAG_GATEWAY, "  Check Status: " + CHECK_PAYMENT_STATUS_ENDPOINT);
    }

    /**
     * Start USDT payment process (simplified - no merchant credentials needed)
     * @param userId User ID
     * @param amount Amount to pay
     * @param network Network type (BEP20 or TRC20)
     */
    public void startPayment(String userId, String amount, String network) {
        Log.d(TAG_GATEWAY, "=== STARTING USDT PAYMENT PROCESS ===");
        Log.d(TAG_GATEWAY, "Payment parameters:");
        Log.d(TAG_GATEWAY, "  User ID: " + userId);
        Log.d(TAG_GATEWAY, "  Amount: " + amount);
        Log.d(TAG_GATEWAY, "  Network: " + network);

        this.userId = userId;
        this.amount = amount;
        this.network = network;

        // First generate a new wallet for this transaction
        Log.d(TAG_WALLET, "Initiating wallet generation...");
        generateNewWallet();
    }

    /**
     * Generate a new wallet for the transaction
     */
    private void generateNewWallet() {
        Log.d(TAG_WALLET, "=== GENERATING NEW WALLET ===");
        progressDialog.show();
        progressDialog.setMessage("Generating secure wallet...");
        Log.d(TAG_WALLET, "Progress dialog shown");

        if (ApiLinks.isNetworkAvailable(context)) {
            Log.d(TAG_API_CALL, "Network available, making generate wallet API call");
            Log.d(TAG_API_CALL, "API URL: " + GENERATE_WALLET_ENDPOINT);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERATE_WALLET_ENDPOINT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG_API_CALL, "=== GENERATE WALLET API RESPONSE ===");
                            Log.d(TAG_API_CALL, "Raw response: " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG_API_CALL, "JSON parsed successfully");

                                boolean status = jsonObject.getBoolean("status");
                                Log.d(TAG_API_CALL, "Response status: " + status);

                                if (status) {
                                    Log.d(TAG_WALLET, "Wallet generation successful");
                                    // Extract wallet details
                                    JSONObject walletObj = jsonObject.getJSONObject("wallet");
                                    walletAddress = walletObj.getString("address");
                                    walletPrivateKey = walletObj.getString("privateKey");

                                    Log.d(TAG_WALLET, "Wallet details extracted:");
                                    Log.d(TAG_WALLET, "  Address: " + walletAddress);
                                    Log.d(TAG_WALLET, "  Private Key: [HIDDEN FOR SECURITY]");

                                    // Save wallet to user account
                                    Log.d(TAG_WALLET, "Proceeding to save wallet to user account");
                                    saveWalletToUserAccount();
                                } else {
                                    String message = jsonObject.getString("message");
                                    Log.e(TAG_GATEWAY_ERROR, "Wallet generation failed: " + message);
                                    progressDialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    callback.onPaymentFailed(message);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG_GATEWAY_ERROR, "Error parsing wallet generation response", e);
                                progressDialog.dismiss();
                                e.printStackTrace();
                                callback.onPaymentFailed("Error generating wallet: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG_GATEWAY_ERROR, "=== GENERATE WALLET API ERROR ===");
                    Log.e(TAG_GATEWAY_ERROR, "Error details: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG_GATEWAY_ERROR, "Status code: " + error.networkResponse.statusCode);
                        Log.e(TAG_GATEWAY_ERROR, "Response data: " + new String(error.networkResponse.data));
                    }
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
                    params.put("user_id", userId);
                    params.put("amount", amount);
                    params.put("network", network);

                    Log.d(TAG_API_CALL, "=== GENERATE WALLET REQUEST PARAMETERS ===");
                    Log.d(TAG_API_CALL, "Parameters being sent:");
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        Log.d(TAG_API_CALL, "  " + entry.getKey() + ": " + entry.getValue());
                    }

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
     * Save the generated wallet to user account
     */
    private void saveWalletToUserAccount() {
        progressDialog.setMessage("Setting up payment...");

        if (ApiLinks.isNetworkAvailable(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, OWN_PAY_BASE_URL + "save-wallet",
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
                    header.put("Content-Type", "application/json");
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
     * Generate QR code for wallet address
     * @param walletAddress Wallet address to encode in QR
     * @return Bitmap containing QR code or null if generation fails
     */
    public Bitmap generateQRCode(String walletAddress) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(walletAddress, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
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

    /**
     * Alias method for generateNewWallet (for compatibility)
     */
    private void generateWallet() {
        generateNewWallet();
    }
}