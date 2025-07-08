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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.view.WindowManager;

public class CheckPaymentStatus extends AppCompatActivity {
    CountDownTimer cTimer = null;
    ProgressDialog progressDialog;
    String pay_id = "", payment_url = "";
    final Handler ha = new Handler();
    Runnable runnable;
    int delay = 10000;
    String check_timer = "";
    final int UPI_PAYMENT_via = 2;
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

        findViewById(R.id.bt_menu).setOnClickListener(view -> finish());
        ((TextView) findViewById(R.id.txt_amount)).setText("Amount: Rs." + getIntent().getStringExtra("amount"));
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

        edt_utr = findViewById(R.id.edt_utr);
        edt_amt = findViewById(R.id.edt_amt);

        lnr_utr = findViewById(R.id.lnr_utr);
        btn_submitUTR = findViewById(R.id.btn_submitUTR);


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

        // Initialize views based on payment type
        if (payment_type.equals("USDT")) {
            // Setup for USDT payment - hide UPI options and show QR code
            Log.d("USDT_PAYMENT", "Setting up USDT payment interface");

            // Hide UPI payment options
            findViewById(R.id.txt_phonepe).setVisibility(View.GONE);
            findViewById(R.id.txt_gpay).setVisibility(View.GONE);
            findViewById(R.id.txt_paytm).setVisibility(View.GONE);
            txt_pp_pay.setVisibility(View.GONE);
            txt_gp_pay.setVisibility(View.GONE);
            txt_pt_pay.setVisibility(View.GONE);

            // Show USDT payment info
            ((TextView) findViewById(R.id.txt_amount)).setText("Amount: $" + getIntent().getStringExtra("amount") + " USDT");
            ((TextView) findViewById(R.id.txn_id)).setText("USDT Wallet Address");

            // Create USDT payment UI elements programmatically
            setupUsdtPaymentUI();

            // Generate and display QR code
            generateQRCode(wallet_address);

            // Don't start automatic status checking for USDT
            Toast.makeText(this, "Scan QR code or copy address to send USDT", Toast.LENGTH_LONG).show();
            Log.d("USDT_PAYMENT", "USDT payment UI setup completed");
        } else {
            // Start checking for UPI payments
            progressDialog.show();
            StartPaymentTimer();
            check();
        }

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
        if (ApiLinks.isNetworkAvailable(CheckPaymentStatus.this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiLinks.check_status,
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
                    header.put("merchantid", str_merchant_id);
                    header.put("merchantsecret", str_merchant_secret);
                    Log.e("TAG_header", "getHeaders: " + header);
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_id", pay_id);
                    Log.d("data", "getParams1_check " + params);
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        ha.removeCallbacks(runnable); //stop handler when activity not visible
        check_timer = "1";
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CheckPaymentStatus.this);
        builder.setTitle("Warning");
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
                        finish();
                        ha.removeCallbacks(runnable);
                    }
                }).create().show();

    }

    private void StartPaymentTimer() {
        cTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.pay_time)).setText("" + millisUntilFinished / 1000);
                ((TextView) findViewById(R.id.pay_time)).setText("" + String.format("%d min : %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                ((TextView) findViewById(R.id.pay_time)).setText("Session Expired");

                //start session expire here
                cTimer.cancel();
                ha.removeCallbacks(runnable);
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
                                    finish();
                                } else if (code.equals("201")) {
                                    Toast.makeText(CheckPaymentStatus.this, message, Toast.LENGTH_SHORT).show();

                                } else {
                                    progressDialog.dismiss();
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
     * Setup USDT payment UI elements programmatically
     */
    private void setupUsdtPaymentUI() {
        try {
            // Create a container for USDT payment elements
            LinearLayout mainLayout = findViewById(android.R.id.content);
            LinearLayout usdtContainer = new LinearLayout(this);
            usdtContainer.setOrientation(LinearLayout.VERTICAL);
            usdtContainer.setPadding(32, 32, 32, 32);

            // QR Code ImageView
            img_qr_code = new ImageView(this);
            LinearLayout.LayoutParams qrParams = new LinearLayout.LayoutParams(600, 600);
            qrParams.gravity = android.view.Gravity.CENTER;
            qrParams.setMargins(0, 32, 0, 32);
            img_qr_code.setLayoutParams(qrParams);
            img_qr_code.setBackgroundColor(Color.WHITE);
            usdtContainer.addView(img_qr_code);

            // Wallet Address TextView
            txt_wallet_address = new TextView(this);
            txt_wallet_address.setText(wallet_address);
            txt_wallet_address.setTextIsSelectable(true);
            txt_wallet_address.setPadding(16, 16, 16, 16);
            txt_wallet_address.setBackgroundColor(Color.LTGRAY);
            txt_wallet_address.setTextSize(12);
            LinearLayout.LayoutParams addressParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            addressParams.setMargins(0, 16, 0, 16);
            txt_wallet_address.setLayoutParams(addressParams);
            usdtContainer.addView(txt_wallet_address);

            // Buttons container
            LinearLayout buttonContainer = new LinearLayout(this);
            buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
            buttonContainer.setGravity(android.view.Gravity.CENTER);

            // Copy Address Button
            btn_copyAddress = new Button(this);
            btn_copyAddress.setText("Copy Address");
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

            // Payment Complete Button
            btn_paymentComplete = new Button(this);
            btn_paymentComplete.setText("Payment Complete");
            btn_paymentComplete.setBackgroundColor(Color.GREEN);
            btn_paymentComplete.setTextColor(Color.WHITE);
            btn_paymentComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("USDT_PAYMENT", "Manual payment completion triggered");
                    Toast.makeText(CheckPaymentStatus.this, "Payment marked as complete!", Toast.LENGTH_SHORT).show();
                    SuccesBox();
                }
            });

            buttonContainer.addView(btn_copyAddress);
            buttonContainer.addView(btn_paymentComplete);
            usdtContainer.addView(buttonContainer);

            // Add the container to the main layout
            mainLayout.addView(usdtContainer);

            Log.d("USDT_PAYMENT", "USDT payment UI elements created successfully");

        } catch (Exception e) {
            Log.e("USDT_PAYMENT", "Error setting up USDT UI: " + e.getMessage());
            e.printStackTrace();
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
            BitMatrix bitMatrix = qrCodeWriter.encode(walletAddress, BarcodeFormat.QR_CODE, 600, 600);
            Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565);

            for (int x = 0; x < 600; x++) {
                for (int y = 0; y < 600; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            if (img_qr_code != null) {
                img_qr_code.setImageBitmap(bitmap);
                Log.d("USDT_PAYMENT", "QR code generated and displayed successfully");
            } else {
                Log.e("USDT_PAYMENT", "QR code ImageView not found");
            }

        } catch (Exception e) {
            Log.e("USDT_PAYMENT", "Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

}