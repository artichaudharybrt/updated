package com.example.sms_readpayment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.example.sms_readpayment.UsdtConfig;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InitiatePayment extends AppCompatActivity implements PaymentCallback {

    // Logging tags for different components
    private static final String TAG_MAIN = "USDT_PAYMENT_MAIN";
    private static final String TAG_DEPOSIT = "USDT_DEPOSIT";
    private static final String TAG_WITHDRAWAL = "USDT_WITHDRAWAL";
    private static final String TAG_VALIDATION = "USDT_VALIDATION";
    private static final String TAG_API = "USDT_API";
    private static final String TAG_UI = "USDT_UI";
    private static final String TAG_ERROR = "USDT_ERROR";

    EditText et_amt, et_user_id, et_name, et_note, et_withdraw_address;
    TextView btn_deposit, btn_withdraw;
    Button btn_quick_pay;
    final int BACK_PRESS = 8;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10 * 1000; //Delay for 15 seconds.  One second = 1000 milliseconds.
    TextView txt_title;
    TranslateAnimation animate;

    // USDT Payment Gateway
    private UsdtPaymentGateway usdtPaymentGateway;
    private SimpleUsdtPayment simpleUsdtPayment;
    private String selectedNetwork = "BEP20"; // Default network (BEP20 or TRC20)

    String str_user_id = "", str_amt = "", str_user_name = "", param1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_MAIN, "=== USDT Payment Activity Started ===");
        Log.d(TAG_UI, "Setting content view: activity_initiate_payment");
        setContentView(R.layout.activity_initiate_payment);

        Log.d(TAG_UI, "Initializing UI components...");
        et_amt = findViewById(R.id.et_amt);
        et_user_id = findViewById(R.id.et_user_id);
        et_name = findViewById(R.id.et_name);
        et_note = findViewById(R.id.et_note);
        et_withdraw_address = findViewById(R.id.et_withdraw_address);
        txt_title = findViewById(R.id.txt_title);
        btn_deposit = findViewById(R.id.btn_deposit);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        btn_quick_pay = findViewById(R.id.btn_quick_pay);
        Log.d(TAG_UI, "UI components initialized successfully");

        // Initialize USDT payment gateway
        Log.d(TAG_MAIN, "Initializing USDT Payment Gateway...");
        usdtPaymentGateway = new UsdtPaymentGateway(this, this);
        simpleUsdtPayment = new SimpleUsdtPayment(this);
        Log.d(TAG_MAIN, "USDT Payment Gateway initialized successfully");

        animate = new TranslateAnimation(11, -11, 0, 0);
        animate.setDuration(1000);
        animate.setFillAfter(true);
        animate.setRepeatMode(2);
        animate.setRepeatCount(Animation.INFINITE);
        txt_title.startAnimation(animate);

        // Check for required parameters (simplified for API-based system)
        Log.d(TAG_MAIN, "Checking intent parameters...");
        if (getIntent().hasExtra("amt") && getIntent().hasExtra("user_id") && getIntent().hasExtra("name")) {
            str_amt = getIntent().getStringExtra("amt");
            str_user_id = getIntent().getStringExtra("user_id");
            str_user_name = getIntent().getStringExtra("name");

            Log.d(TAG_MAIN, "Intent parameters found:");
            Log.d(TAG_MAIN, "  Amount: " + str_amt);
            Log.d(TAG_MAIN, "  User ID: " + str_user_id);
            Log.d(TAG_MAIN, "  User Name: " + str_user_name);

            btn_deposit.setEnabled(true);
            btn_withdraw.setEnabled(true);
            Log.d(TAG_UI, "Deposit and Withdraw buttons enabled");

            // Set values in the fields
            et_amt.setText(str_amt);
            et_user_id.setText(str_user_id);
            et_name.setText(str_user_name);
            Log.d(TAG_UI, "Form fields populated with intent data");

            // Check if auto_start_deposit is enabled
            boolean autoStartDeposit = getIntent().getBooleanExtra("auto_start_deposit", false);
            Log.d(TAG_MAIN, "Auto start deposit: " + autoStartDeposit);
            if (autoStartDeposit) {
                Log.d(TAG_DEPOSIT, "Auto-starting USDT deposit in 1 second...");
                // Automatically start USDT deposit after a short delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG_DEPOSIT, "Executing auto-start deposit");
                        initiate_usdt_deposit();
                    }
                }, 1000); // 1 second delay to allow UI to load
            }
        } else {
            Log.d(TAG_MAIN, "No intent parameters found, using default values");
            // Enable buttons even without parameters for manual entry
            btn_deposit.setEnabled(true);
            btn_withdraw.setEnabled(true);

            // Set default values
            et_amt.setText("10.00");
            et_user_id.setText("user123");
            et_name.setText("Test User");
            Log.d(TAG_UI, "Default values set in form fields");
        }

        // Quick Pay button click listener (Static QR)
        Log.d(TAG_UI, "Setting up quick pay button click listener");
        btn_quick_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("USDT_PAYMENT", "=== QUICK PAY BUTTON CLICKED ===");
                Log.d("USDT_PAYMENT", "Showing static QR code directly");

                // Get amount from input field
                str_amt = et_amt.getText().toString().trim();
                str_user_id = et_user_id.getText().toString().trim();

                if (str_amt.isEmpty()) {
                    str_amt = "10.00"; // Default amount
                }
                if (str_user_id.isEmpty()) {
                    str_user_id = "user123"; // Default user ID
                }

                Log.d("USDT_PAYMENT", "Amount: " + str_amt + ", User: " + str_user_id);

                // Call the static payment method directly
                initiate_payment();
            }
        });

        // USDT Deposit button click listener
        Log.d(TAG_UI, "Setting up deposit button click listener");
        btn_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DEPOSIT, "=== DEPOSIT BUTTON CLICKED ===");
                Log.d(TAG_DEPOSIT, "Starting deposit validation...");
                if (validateInputs()) {
                    Log.d(TAG_DEPOSIT, "Validation passed, initiating USDT deposit");
                    initiate_usdt_deposit();
                } else {
                    Log.w(TAG_VALIDATION, "Deposit validation failed");
                }
            }
        });

        // USDT Withdrawal button click listener
        Log.d(TAG_UI, "Setting up withdrawal button click listener");
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_WITHDRAWAL, "=== WITHDRAWAL BUTTON CLICKED ===");
                Log.d(TAG_WITHDRAWAL, "Starting withdrawal validation...");
                if (validateWithdrawalInputs()) {
                    Log.d(TAG_WITHDRAWAL, "Validation passed, initiating USDT withdrawal");
                    initiate_usdt_withdrawal();
                } else {
                    Log.w(TAG_VALIDATION, "Withdrawal validation failed");
                }
            }
        });

    }

    String payment_id = "", payment_upi = "", payment_amt = "", payment_url = "";

    public void initiate_payment() {
        Log.d("USDT_PAYMENT", "Showing static USDT wallet directly - no API calls");

        // Directly use static wallet data - no API calls, no merchant authentication
        String staticWalletAddress = "0xd13e2620c159394477881d9828049d5e5932b88d";

        // Store wallet information for payment processing
        payment_id = staticWalletAddress; // Use wallet address as payment ID
        payment_upi = staticWalletAddress; // Store wallet address
        payment_amt = str_amt; // Use the amount entered
        payment_url = ""; // Not needed for USDT payments

        Log.d("USDT_PAYMENT", "Static wallet address: " + staticWalletAddress);
        Log.d("USDT_PAYMENT", "Payment amount: " + payment_amt);

        Toast.makeText(InitiatePayment.this, "USDT Wallet Ready - No API needed", Toast.LENGTH_SHORT).show();

        // Go directly to payment screen
        call_success();
    }



    void payUsingUpi(String amount, String upiId, String name, String note) {
        Log.e("payUsingUpi", "payUsingUpi: " + upiId);
        Log.e("payUsingUpi", "payUsingUpi: " + amount);
        Log.e("payUsingUpi", "payUsingUpi: " + name);
        Log.e("payUsingUpi", "payUsingUpi: " + note);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
//                .appendQueryParameter("mc", "5816")
//                .appendQueryParameter("tid", "AXIFRCO120720221js3b5g8ft")
//                .appendQueryParameter("tr", "AXIFRCO120720221js3b5g8ft")
                .appendQueryParameter("cu", "INR")
                .build();

        Log.e("uri_upi", "payUsingUpi: " + uri);
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(InitiatePayment.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }

    public void call_success() {
        Intent i = new Intent(InitiatePayment.this, CheckPaymentStatus.class);
        i.putExtra("payment_id", payment_id);
        i.putExtra("amount", payment_amt);
        i.putExtra("wallet_address", payment_upi); // Pass wallet address
        i.putExtra("upi_id_entered", ""); // No UPI ID for USDT payments
        i.putExtra("payment_url", payment_url);
        i.putExtra("user_id", str_user_id);
        i.putExtra("payment_type", "USDT"); // Indicate this is a USDT payment
        i.putExtra("network", "BEP20"); // Default network

        Log.d("USDT_PAYMENT", "Navigating to CheckPaymentStatus with wallet: " + payment_upi);
        Log.d("USDT_PAYMENT", "Payment amount: " + payment_amt);

        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivity", "" + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 123) {
                Toast.makeText(this, "back again", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == UPI_PAYMENT) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.d("UPI", "onActivityResult: " + trxt);
                    Log.d("_data_", String.valueOf(data));
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        } else if (requestCode == UPI_PAYMENT_via) {
            if ((RESULT_OK == resultCode)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.d("UPI", "onActivityResult: " + trxt);
                    Log.d("_data_", String.valueOf(data));
                }
            }
        }


    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(InitiatePayment.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            Log.d("status_", status);
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(InitiatePayment.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI_payment", "responseStr: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(InitiatePayment.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.d("payment_cancelled", "cancel");
            } else {
                Toast.makeText(InitiatePayment.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InitiatePayment.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
        if (!payment_id.equals("")) {
//            Intent i = new Intent(InitiatePayment.this, CheckPaymentStatus.class);
//            i.putExtra("payment_id", payment_id);
//            i.putExtra("amount", et_amt.getText().toString());
//            i.putExtra("upi_id", et_upi_id.getText().toString());
//            startActivity(i);
            call_success();
        } else {
            Toast.makeText(this, "Payment id is empty!", Toast.LENGTH_SHORT).show();
        }

//        check();

    }

    private void check_status() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(InitiatePayment.this);
        progressDialog.setMessage("Checking...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        if (ApiLinks.isNetworkAvailable(InitiatePayment.this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
//                                AppHelper.LogCat(response);
                                Log.d("payment_status", response);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
//                                payment_id = jsonObject.getString("payment_id");

                                if (code.equals("200") && message.equals("Success")) {
                                    progressDialog.dismiss();
                                    handler.removeCallbacks(runnable);
                                    Toast.makeText(InitiatePayment.this, "Thank you for your Payment!", Toast.LENGTH_SHORT).show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(InitiatePayment.this, "" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("token", ApiLinks.Token);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_id", payment_id);
//                    params.put("sms",sms_text);
                    Log.d("data", "getParams1: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(InitiatePayment.this).add(stringRequest);

        } else {
            errMsg("check your internet connection...");
        }
    }

    public void check() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                //do something
                check_status();
                handler.postDelayed(runnable, delay);
            }
        }, delay);
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private boolean errMsg(String msg) {
        Toast.makeText(InitiatePayment.this, "" + msg, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Validate inputs for deposit
     */
    private boolean validateInputs() {
        Log.d(TAG_VALIDATION, "=== STARTING DEPOSIT VALIDATION ===");
        String amount = et_amt.getText().toString().trim();
        String userId = et_user_id.getText().toString().trim();

        Log.d(TAG_VALIDATION, "Validating inputs:");
        Log.d(TAG_VALIDATION, "  Amount: '" + amount + "'");
        Log.d(TAG_VALIDATION, "  User ID: '" + userId + "'");

        if (amount.isEmpty()) {
            Log.e(TAG_VALIDATION, "Validation failed: Amount is empty");
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userId.isEmpty()) {
            Log.e(TAG_VALIDATION, "Validation failed: User ID is empty");
            Toast.makeText(this, "Please enter user ID", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double amountValue = Double.parseDouble(amount);
            Log.d(TAG_VALIDATION, "Parsed amount value: " + amountValue);
            if (amountValue <= 0) {
                Log.e(TAG_VALIDATION, "Validation failed: Amount must be greater than 0");
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG_VALIDATION, "Validation failed: Invalid amount format - " + e.getMessage());
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG_VALIDATION, "=== DEPOSIT VALIDATION PASSED ===");
        return true;
    }

    /**
     * Validate inputs for withdrawal
     */
    private boolean validateWithdrawalInputs() {
        Log.d(TAG_VALIDATION, "=== STARTING WITHDRAWAL VALIDATION ===");

        // First validate basic inputs (amount and user ID)
        if (!validateInputs()) {
            Log.e(TAG_VALIDATION, "Basic validation failed for withdrawal");
            return false;
        }

        String withdrawAddress = et_withdraw_address.getText().toString().trim();
        Log.d(TAG_VALIDATION, "Withdrawal address: '" + withdrawAddress + "'");

        if (withdrawAddress.isEmpty()) {
            Log.e(TAG_VALIDATION, "Validation failed: Withdrawal address is empty");
            Toast.makeText(this, "Please enter withdrawal address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Basic wallet address validation
        Log.d(TAG_VALIDATION, "Validating wallet address format...");
        if (!withdrawAddress.startsWith("0x") || withdrawAddress.length() != 42) {
            Log.e(TAG_VALIDATION, "Validation failed: Invalid wallet address format");
            Log.e(TAG_VALIDATION, "  Expected: 0x + 40 characters (total 42)");
            Log.e(TAG_VALIDATION, "  Received: " + withdrawAddress.length() + " characters");
            Toast.makeText(this, "Please enter valid wallet address", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(TAG_VALIDATION, "=== WITHDRAWAL VALIDATION PASSED ===");
        return true;
    }

    /**
     * Initiate USDT deposit (using own_pay API)
     */
    private void initiate_usdt_deposit() {
        Log.d(TAG_DEPOSIT, "=== INITIATING USDT DEPOSIT ===");
        String userId = et_user_id.getText().toString().trim();
        String amount = et_amt.getText().toString().trim();

        Log.d(TAG_DEPOSIT, "Deposit parameters:");
        Log.d(TAG_DEPOSIT, "  User ID: " + userId);
        Log.d(TAG_DEPOSIT, "  Amount: " + amount);
        Log.d(TAG_DEPOSIT, "  Network: " + selectedNetwork);

        if (userId.isEmpty() || amount.isEmpty()) {
            Log.e(TAG_DEPOSIT, "Deposit failed: Missing required parameters");
            Toast.makeText(this, "Please enter user ID and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start USDT deposit process using simplified API (NO MERCHANT AUTH)
        Log.d(TAG_DEPOSIT, "Calling Simple USDT Payment...");
        Log.d(TAG_API, "Starting deposit with simple payment: userId=" + userId + ", amount=" + amount);
        simpleUsdtPayment.startDeposit(userId, amount);
        Log.d(TAG_DEPOSIT, "Simple payment call completed");
    }

    /**
     * Initiate USDT withdrawal (using own_pay API)
     */
    private void initiate_usdt_withdrawal() {
        Log.d(TAG_WITHDRAWAL, "=== INITIATING USDT WITHDRAWAL ===");
        String userId = et_user_id.getText().toString().trim();
        String amount = et_amt.getText().toString().trim();
        String withdrawAddress = et_withdraw_address.getText().toString().trim();

        Log.d(TAG_WITHDRAWAL, "Withdrawal parameters:");
        Log.d(TAG_WITHDRAWAL, "  User ID: " + userId);
        Log.d(TAG_WITHDRAWAL, "  Amount: " + amount);
        Log.d(TAG_WITHDRAWAL, "  Withdraw Address: " + withdrawAddress);
        Log.d(TAG_WITHDRAWAL, "  Network: " + selectedNetwork);

        if (userId.isEmpty() || amount.isEmpty() || withdrawAddress.isEmpty()) {
            Log.e(TAG_WITHDRAWAL, "Withdrawal failed: Missing required parameters");
            Toast.makeText(this, "Please fill all fields for withdrawal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start USDT withdrawal process using simplified API (NO MERCHANT AUTH)
        Log.d(TAG_WITHDRAWAL, "Calling Simple USDT Withdrawal...");
        simpleUsdtPayment.startWithdrawal(userId, amount, withdrawAddress);
    }

    /**
     in notification its showing all the notificcation add that if isDeleted is 1 rhen notification toh wo notification nai dikhegi ye har notification me hai 
     */
    
    /**
     * Request USDT withdrawal (using own_pay API)
     */
    private void requestWithdrawal() {
        Log.d(TAG_WITHDRAWAL, "=== STARTING WITHDRAWAL REQUEST ===");

        String apiUrl = "http://localhost:3000/api/withdraw";
        Log.d(TAG_API, "Withdrawal API URL: " + apiUrl);

        ProgressDialog progressDialog = new ProgressDialog(InitiatePayment.this);
        progressDialog.setMessage("Processing withdrawal request...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Log.d(TAG_UI, "Progress dialog shown for withdrawal");

        if (ApiLinks.isNetworkAvailable(InitiatePayment.this)) {
            Log.d(TAG_API, "Network available, making withdrawal request...");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG_API, "=== WITHDRAWAL API RESPONSE RECEIVED ===");
                            Log.d(TAG_API, "Raw response: " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d(TAG_API, "Parsed JSON response successfully");

                                boolean status = jsonObject.getBoolean("status");
                                String message = jsonObject.getString("message");

                                Log.d(TAG_API, "Response status: " + status);
                                Log.d(TAG_API, "Response message: " + message);

                                progressDialog.dismiss();
                                Log.d(TAG_UI, "Progress dialog dismissed");

                                if (status) {
                                    Log.d(TAG_WITHDRAWAL, "Withdrawal request successful");
                                    Toast.makeText(InitiatePayment.this, "Withdrawal request submitted: " + message, Toast.LENGTH_LONG).show();
                                    Log.d(TAG_MAIN, "Closing activity after successful withdrawal");
                                    finish(); // Close the activity
                                } else {
                                    Log.e(TAG_WITHDRAWAL, "Withdrawal request failed: " + message);
                                    Toast.makeText(InitiatePayment.this, "Withdrawal failed: " + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Log.e(TAG_ERROR, "Error parsing withdrawal response", e);
                                progressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(InitiatePayment.this, "Error processing withdrawal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG_ERROR, "=== WITHDRAWAL API ERROR ===");
                    Log.e(TAG_ERROR, "Error details: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG_ERROR, "Status code: " + error.networkResponse.statusCode);
                        Log.e(TAG_ERROR, "Response data: " + new String(error.networkResponse.data));
                    }
                    progressDialog.dismiss();
                    error.printStackTrace();
                    Toast.makeText(InitiatePayment.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", et_user_id.getText().toString());
                    params.put("amount", et_amt.getText().toString());
                    params.put("wallet_address", et_withdraw_address.getText().toString());
                    params.put("currency", "USDT");
                    params.put("network", selectedNetwork);

                    Log.d(TAG_API, "=== WITHDRAWAL REQUEST PARAMETERS ===");
                    Log.d(TAG_API, "Parameters being sent:");
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        Log.d(TAG_API, "  " + entry.getKey() + ": " + entry.getValue());
                    }

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(InitiatePayment.this).add(stringRequest);

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * PaymentCallback implementation
     */
    @Override
    public void onPaymentSuccess(String transactionId, String message) {
        Toast.makeText(this, "Payment successful: " + message, Toast.LENGTH_SHORT).show();
        // Handle successful payment
    }

    @Override
    public void onPaymentFailed(String message) {
        Toast.makeText(this, "Payment failed: " + message, Toast.LENGTH_SHORT).show();
        // Handle failed payment
    }

    @Override
    public void onPaymentPending(String message) {
        Toast.makeText(this, "Payment pending: " + message, Toast.LENGTH_SHORT).show();
        // Handle pending payment
    }

    /**
     * Static method to start InitiatePayment activity for USDT deposit (API-based)
     * @param context Context to start the activity from
     * @param amount Deposit amount
     * @param userId User ID
     * @param userName User Name
     * @param autoStart Whether to automatically start USDT deposit
     */
    public static void startUsdtDeposit(Context context, String amount, String userId, String userName, boolean autoStart) {
        Log.d(TAG_MAIN, "=== STARTING USDT DEPOSIT (STATIC METHOD) ===");
        Log.d(TAG_MAIN, "Parameters:");
        Log.d(TAG_MAIN, "  Amount: " + amount);
        Log.d(TAG_MAIN, "  User ID: " + userId);
        Log.d(TAG_MAIN, "  User Name: " + userName);
        Log.d(TAG_MAIN, "  Auto Start: " + autoStart);

        Intent intent = new Intent(context, InitiatePayment.class);
        intent.putExtra("amt", amount);
        intent.putExtra("user_id", userId);
        intent.putExtra("name", userName);
        intent.putExtra("auto_start_deposit", autoStart);

        Log.d(TAG_MAIN, "Starting InitiatePayment activity...");
        context.startActivity(intent);
        Log.d(TAG_MAIN, "InitiatePayment activity started");
    }

    /**
     * Static method to start InitiatePayment activity for USDT deposit (auto-start enabled)
     */
    public static void startUsdtDepositAuto(Context context, String amount, String userId, String userName) {
        Log.d(TAG_MAIN, "=== STARTING USDT DEPOSIT AUTO (STATIC METHOD) ===");
        Log.d(TAG_MAIN, "Auto-start deposit with amount: " + amount + ", userId: " + userId);
        startUsdtDeposit(context, amount, userId, userName, true);
    }

    /**
     * Static method to start InitiatePayment activity for USDT withdrawal (API-based)
     */
    public static void startUsdtWithdrawal(Context context, String amount, String userId, String userName) {
        Log.d(TAG_MAIN, "=== STARTING USDT WITHDRAWAL (STATIC METHOD) ===");
        Log.d(TAG_MAIN, "Parameters:");
        Log.d(TAG_MAIN, "  Amount: " + amount);
        Log.d(TAG_MAIN, "  User ID: " + userId);
        Log.d(TAG_MAIN, "  User Name: " + userName);

        Intent intent = new Intent(context, InitiatePayment.class);
        intent.putExtra("amt", amount);
        intent.putExtra("user_id", userId);
        intent.putExtra("name", userName);

        Log.d(TAG_MAIN, "Starting InitiatePayment activity for withdrawal...");
        context.startActivity(intent);
        Log.d(TAG_MAIN, "InitiatePayment activity started for withdrawal");
    }

    /**
     * Quick helper method for USDT deposit (API-based)
     */
    public static void quickUsdtDeposit(Context context, String amount, String userId) {
        Log.d(TAG_MAIN, "=== QUICK USDT DEPOSIT ===");
        Log.d(TAG_MAIN, "Quick deposit: amount=" + amount + ", userId=" + userId);
        startUsdtDepositAuto(context, amount, userId, "User");
    }

    /**
     * Quick helper method for USDT withdrawal (API-based)
     */
    public static void quickUsdtWithdrawal(Context context, String amount, String userId) {
        Log.d(TAG_MAIN, "=== QUICK USDT WITHDRAWAL ===");
        Log.d(TAG_MAIN, "Quick withdrawal: amount=" + amount + ", userId=" + userId);
        startUsdtWithdrawal(context, amount, userId, "User");
    }

    /**
     * Open USDT payment system directly (API-based)
     */
    public static void openUsdtPaymentSystem(Context context) {
        Log.d(TAG_MAIN, "=== OPENING USDT PAYMENT SYSTEM DIRECTLY ===");
        Intent intent = new Intent(context, InitiatePayment.class);
        Log.d(TAG_MAIN, "Starting InitiatePayment activity directly...");
        context.startActivity(intent);
        Log.d(TAG_MAIN, "InitiatePayment activity started directly");
    }
}