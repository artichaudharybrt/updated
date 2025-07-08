package com.example.sms_readpayment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sms_readpayment.ApiClasses.ApiLinks;
import com.example.sms_readpayment.UsdtConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CheckPaymentStatus extends AppCompatActivity {
    CountDownTimer cTimer = null;
    ProgressDialog progressDialog;
    String pay_id = "", payment_url = "";
    final Handler ha = new Handler();
    Runnable runnable;
    int delay = 10000;
    String check_timer = "";
    final int UPI_PAYMENT_via = 2;
    final int BACK_PRESS = 8;
    LinearLayout lnr_utr, lnr_crypto;
    Button btn_submitUTR, btn_copyAddress, btn_paymentComplete;
    EditText edt_utr, edt_amt;
    String str_merchant_id = "", str_merchant_secret = "", user_id = "";
    TextView txt_pp_pay, txt_gp_pay, txt_pt_pay, txt_wallet_address, txt_payment_type;
    ImageView img_qr_code;
    String payment_type = "UPI"; // Default payment type
    String wallet_address = ""; // USDT wallet address
    String network = "BEP20"; // Default network

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_payment_status);
// Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        check_timer = "";
        progressDialog = new ProgressDialog(CheckPaymentStatus.this);
        progressDialog.setMessage("Checking Payment Status..");
        progressDialog.setCancelable(true);

        pay_id = getIntent().getStringExtra("payment_id");
        payment_url = getIntent().getStringExtra("payment_url");
        Log.d("USDT_PAYMENT", "Payment ID received: " + pay_id);

        user_id = getIntent().getStringExtra("user_id");

        // Check if payment type is provided
        if (getIntent().hasExtra("payment_type")) {
            payment_type = getIntent().getStringExtra("payment_type");
            Log.d("USDT_PAYMENT", "Payment type: " + payment_type);
        }

        // Get USDT wallet address and network if available
        if (getIntent().hasExtra("wallet_address")) {
            wallet_address = getIntent().getStringExtra("wallet_address");
            Log.d("USDT_PAYMENT", "Wallet address received: " + wallet_address);
        }

        if (getIntent().hasExtra("network")) {
            network = getIntent().getStringExtra("network");
            Log.d("USDT_PAYMENT", "Network: " + network);
        }

        ((TextView) findViewById(R.id.txt_amount)).setText("Amount: Rs." + getIntent().getStringExtra("amount"));

        // Initialize views based on payment type
        if (payment_type.equals("USDT")) {
            // Setup for USDT payment
            findViewById(R.id.lnr_upi_options).setVisibility(View.GONE);
            lnr_crypto = findViewById(R.id.lnr_crypto);
            lnr_crypto.setVisibility(View.VISIBLE);

            txt_wallet_address = findViewById(R.id.txt_wallet_address);
            txt_payment_type = findViewById(R.id.txt_payment_type);
            btn_copyAddress = findViewById(R.id.btn_copy_address);
            btn_paymentComplete = findViewById(R.id.btn_payment_complete);

            txt_wallet_address.setText(wallet_address);
            txt_payment_type.setText("Payment Type: USDT " + network);

            // Set up copy address button
            btn_copyAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Wallet Address", wallet_address);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(CheckPaymentStatus.this, "Wallet address copied to clipboard", Toast.LENGTH_SHORT).show();
                    Log.d("USDT_PAYMENT", "Wallet address copied to clipboard");
                }
            });

            // Set up payment complete button (for testing)
            btn_paymentComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("USDT_PAYMENT", "Manual payment completion triggered");
                    Toast.makeText(CheckPaymentStatus.this, "Payment marked as complete!", Toast.LENGTH_SHORT).show();
                    SuccesBox();
                }
            });

            // Generate and display QR code for the wallet address
            generateQRCode(wallet_address);

            ((TextView) findViewById(R.id.txn_id)).setText("USDT Wallet Address");
            Log.d("USDT_PAYMENT", "USDT payment UI setup completed");
        } else {
            // Setup for UPI payment
            findViewById(R.id.lnr_upi_options).setVisibility(View.VISIBLE);
            findViewById(R.id.lnr_crypto).setVisibility(View.GONE);

            ((TextView) findViewById(R.id.txn_id)).setText("Entered UPI ID:" + getIntent().getStringExtra("upi_id_entered"));
            ((TextView) findViewById(R.id.txt_phonepe)).setText("PHONEPE: " + getIntent().getStringExtra("upi_id"));
            ((TextView) findViewById(R.id.txt_gpay)).setText("GOOGLE PAY: " + getIntent().getStringExtra("upi_id"));
            ((TextView) findViewById(R.id.txt_paytm)).setText("PAYTM: " + getIntent().getStringExtra("upi_id"));

            ((TextView) findViewById(R.id.txt_pp_amt)).setText("Amount Rs: " + getIntent().getStringExtra("amount"));
            ((TextView) findViewById(R.id.txt_gp_amt)).setText("Amount Rs: " + getIntent().getStringExtra("amount"));
            ((TextView) findViewById(R.id.txt_pt_amt)).setText("Amount Rs: " + getIntent().getStringExtra("amount"));

            txt_pp_pay = findViewById(R.id.txt_pp_pay);
            txt_gp_pay = findViewById(R.id.txt_gp_pay);
            txt_pt_pay = findViewById(R.id.txt_pt_pay);

            // Set up UPI app buttons
            setupUpiButtons();
        }

        edt_utr = findViewById(R.id.edt_utr);
        edt_amt = findViewById(R.id.edt_amt);

        lnr_utr = findViewById(R.id.lnr_utr);
        btn_submitUTR = findViewById(R.id.btn_submitUTR);


        btn_submitUTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call back api
                if (edt_utr.getText().toString().equals("")) {
                    Toast.makeText(CheckPaymentStatus.this, "Please enter valid UTR No.", Toast.LENGTH_SHORT).show();
                } else if (edt_amt.getText().toString().equals("")) {
                    Toast.makeText(CheckPaymentStatus.this, "Please enter valid Amount!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    update_utr();
                }
            }
        });

        // For USDT payments, just show the interface - no status checking needed
        if (payment_type.equals("USDT")) {
            Log.d("USDT_PAYMENT", "USDT payment interface ready - no status checking");
            Toast.makeText(this, "Scan QR code or copy address to send USDT", Toast.LENGTH_LONG).show();
        } else {
            // Start payment timer and status check for UPI payments
            if (check_timer.equals("")) {
                progressDialog.show();
                StartPaymentTimer();
                check();
            }
        }
    }

    /**
     * Set up UPI app buttons
     */
    private void setupUpiButtons() {
        txt_pp_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.phonepe.app");
                    startActivity(i);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(CheckPaymentStatus.this, "No PhonePe app installed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_gp_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user");
                    startActivity(i);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(CheckPaymentStatus.this, "No GPay app installed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_pt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
                    startActivity(i);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(CheckPaymentStatus.this, "No PayTM app installed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void check() {
        runnable = new Runnable() {
            @Override
            public void run() {
                check_status();
                ha.postDelayed(this, delay);
            }
        };
        ha.postDelayed(runnable, delay);

    }

    private void check_status() {
        if (payment_type.equals("USDT")) {
            // No status checking for USDT - just display QR and wait for manual confirmation
            Log.d("USDT_PAYMENT", "USDT payment - no automatic status checking");
            return;
        }

        if (ApiLinks.isNetworkAvailable(CheckPaymentStatus.this)) {
            // Use real API for UPI payments
            String apiEndpoint = ApiLinks.check_status;
            Log.d("UPI_PAYMENT", "Checking UPI payment status using endpoint: " + apiEndpoint);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndpoint,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("payment_status", response);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");

                                if (code.equals("200")) {
                                    progressDialog.dismiss();
                                    ha.removeCallbacks(runnable);
                                    cTimer.cancel();
                                    SuccesBox();

                                } else if (code.equals("404") && message.equals("Failed")) {
                                    progressDialog.dismiss();
                                    ha.removeCallbacks(runnable);
                                    Toast.makeText(CheckPaymentStatus.this, "Payment failed!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(CheckPaymentStatus.this, InitiatePayment.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(CheckPaymentStatus.this, "" + message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                ha.removeCallbacks(runnable);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    ha.removeCallbacks(runnable);
                    error.printStackTrace();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("token", ApiLinks.Token);

                    // Use contract-based authentication for USDT payments
                    if (payment_type.equals("USDT")) {
                        header.put("Content-Type", "application/json");
                        Log.d("USDT_PAYMENT", "Using contract-based auth for USDT payment status check");
                    } else {
                        // Keep merchant credentials for UPI payments if needed
                        header.put("merchantid", str_merchant_id);
                        header.put("merchantsecret", str_merchant_secret);
                    }

                    Log.d("USDT_PAYMENT", "Headers for payment status check: " + header);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();

                    if (payment_type.equals("USDT")) {
                        // Parameters for USDT payment status check
                        params.put("payment_id", pay_id);
                        params.put("wallet_address", wallet_address);
                        params.put("network", network);
                        Log.d("USDT_PAYMENT", "USDT status check params: " + params);
                    } else {
                        // Parameters for UPI payment status check
                        params.put("payment_id", pay_id);
                        Log.d("UPI_PAYMENT", "UPI status check params: " + params);
                    }

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(CheckPaymentStatus.this).add(stringRequest);

        } else {
            errMsg("check your internet connection...");
        }
    }

    public void onResume() {
        super.onResume();
        if (check_timer.equals("1")) {
            progressDialog.show();
            StartPaymentTimer();
            check();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ha.removeCallbacks(runnable); //stop handler when activity not visible
        check_timer = "1";
    }

    public void onBackPressed() {
//        check_timer="";
//        Log.d("check_timer_", check_timer);
//        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckPaymentStatus.this);
//                builder.setTitle("Location")
        builder.setMessage("Are you Sure you want to close this page?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        check_timer = "";
                        Log.d("check_timer_", check_timer);
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("ALERT!");
        alert.show();
    }

    private boolean errMsg(String msg) {
        Toast.makeText(CheckPaymentStatus.this, "" + msg, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void SuccesBox() {
        new AlertDialog.Builder(this)
                .setTitle("Thank You")
                .setMessage("Your Payment has been done Successfully!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ha.removeCallbacks(runnable);
                        Intent intent = new Intent("com.gamegards.letsplaycard.Activity.Homepage");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).create().show();

    }

    private void StartPaymentTimer() {
        cTimer = new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.pay_time)).setText("" + millisUntilFinished / 1000);
                ((TextView) findViewById(R.id.pay_time)).setText("" + String.format("%d min : %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                ((TextView) findViewById(R.id.pay_time)).setText("Session Expired");
                Intent i = new Intent(CheckPaymentStatus.this, InitiatePayment.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ha.removeCallbacks(runnable);
                cTimer.cancel();
                progressDialog.dismiss();
                lnr_utr.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
    }

    public void update_utr() {

        if (ApiLinks.isNetworkAvailable(CheckPaymentStatus.this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiLinks.update_utr,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("utr_update_msg", response);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");

                                if (code.equals("200")) {
                                    lnr_utr.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    Toast.makeText(CheckPaymentStatus.this, "" + message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent("com.gamegards.letsplaycard.Activity.Homepage");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                   /* Intent intent = new Intent("com.gamegards.letsplaycard.Activity.Homepage");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();*/
                                    Toast.makeText(CheckPaymentStatus.this, "" + message, Toast.LENGTH_SHORT).show();
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
                    header.put("merchantid", str_merchant_id);
                    header.put("merchantsecret", str_merchant_secret);
                    header.put("token", ApiLinks.Token);
                    android.util.Log.e("header_check", "getHeaders: " + header);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("utr_no", edt_utr.getText().toString());
                    params.put("amount", edt_amt.getText().toString());
                    params.put("user_id", user_id);
                    Log.d("data", "getParams1_init: " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(CheckPaymentStatus.this).add(stringRequest);

        } else {
            errMsg("check your internet connection...");
        }
    }

    /**
     * Generate QR code for wallet address and display it
     * @param walletAddress The wallet address to encode in QR code
     */
    private void generateQRCode(String walletAddress) {
        try {
            Log.d("USDT_PAYMENT", "Generating QR code for wallet: " + walletAddress);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(walletAddress, BarcodeFormat.QR_CODE, 250, 250);
            Bitmap bitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.RGB_565);

            for (int x = 0; x < 250; x++) {
                for (int y = 0; y < 250; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Find QR code ImageView in the crypto layout and set the bitmap
            // Note: We need to add this ImageView to the layout if it doesn't exist
            ImageView qrImageView = findViewById(R.id.img_qr_code);
            if (qrImageView != null) {
                qrImageView.setImageBitmap(bitmap);
                Log.d("USDT_PAYMENT", "QR code generated and displayed successfully");
            } else {
                Log.e("USDT_PAYMENT", "QR code ImageView not found in layout");
            }

        } catch (Exception e) {
            Log.e("USDT_PAYMENT", "Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }



}