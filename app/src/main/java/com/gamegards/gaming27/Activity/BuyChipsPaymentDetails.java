package com.gamegards.gaming27.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.BaseActivity;
import com.gamegards.gaming27.Comman.CommonAPI;
import com.gamegards.gaming27.Comman.PaymentGetway_CashFree;
import com.gamegards.gaming27.Comman.PaymentGetway_Paymt;
import com.gamegards.gaming27.Comman.PaymentGetway_PayuMoney;
import com.gamegards.gaming27.Details.Menu.DialogDepositHistory;
import com.gamegards.gaming27.Interface.Callback;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.Utils.Functions;
import com.gamegards.gaming27.Utils.SharePref;
import com.gamegards.gaming27.Utils.Variables;
import com.gamegards.gaming27.data.ChipsRepository;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import butterknife.ButterKnife;
import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;

public class BuyChipsPaymentDetails {
//
//
//    Activity context = this;
//    private static final String MY_PREFS_NAME = "Login_data";
//    EditText amountpay;
//    // EditText utr; // Hidden - not needed
//    Button btnPaynow;
//    TextView txtChipsdetails, address, txtQrInstruction;
//    String whats_no = "", str_image_1 = "";
//    String plan_id = "",qr_image;
//    String chips_details = "";
//    String pay_amount = "";
//    // Button tv_qr_img; // Hidden - not needed
//    String str_order_id, str_total_amount, str_txnid, str_clientRefId, str_intentData;
//    String amount = "";
//    String RazorPay_ID = "", orderIdString = "", payment_id, pay_address;
//    private String order_id;
//    ImageView imgback,qrcode;
//    RelativeLayout imgsub;
//    String county_code = "+91 ";
//    ProgressDialog progressDialog;
//    Button btnContactSupport, btnCheckStatus, btnViewHistory;
//    TextView txtStatus;
//    Calendar calendar = Calendar.getInstance();
//    public static String btn_clicked = "";
//    RelativeLayout rl_extra,rlthistory;
//    private static final String[] sectors = {"1",
//            "2", "3", "4", "5", "6", "7", "8",
//            "9", "10", "11", "12", "13", "14", "15",
//            "16", "17", "18", "19", "20"};
//   // @BindView(R.id.spinBtn)
//    Button spinBtn;
//   // @BindView(R.id.resultTv)
//    TextView resultTv;
//  //  @BindView(R.id.wheel)
//    ImageView wheel;
//
//   // @BindView(R.id.btnDummyPay)
//    TextView btnDummyPay;
//
//    // We create a Random instance to make our wheel spin randomly
//    private static final Random RANDOM = new Random();
//    private int degree = 0, degreeOld = 0;
//    // We have 37 sectors on the wheel, we divide 360 by this value to have angle for each sector
//    // we divide by 2 to have a half sector
//    private static final float HALF_SECTOR = 360f / 20f / 2f;
//
//    private EasyUpiPayment easyUpiPayment;
//    private RadioGroup radioAppChoice;
//    RadioButton paymentAppChoice;
//    ImageView imgqrcode;
//    TextView copywalletaddress;
//    RelativeLayout qr_container;
//    ImageView usdt_logo;
//    // ImageView img_qr; // Hidden - not needed
//    Timer t;
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
//
//    String selected_payment = "", str_extraVal = "";
//    public static String str_diff = "";
//    static Date date1 = null, date2 = null;
//    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//    String currentDateandTimeOld = sdf.format(new Date());
//
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Set fullscreen
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        setContentView(R.layout.activity_chips_payment_details);
//        ButterKnife.bind(this);
//
//
//        //  initialize();
//        radioAppChoice = findViewById(R.id.radioAppChoice);
//        imgqrcode = findViewById(R.id.qrcode);
//        address = findViewById(R.id.address);
//        qr_container = findViewById(R.id.qr_container);
//        usdt_logo = findViewById(R.id.usdt);
//
//        qrcode = findViewById(R.id.qrcode);
//
//
//        spinBtn = findViewById(R.id.spinBtn);
//        resultTv = findViewById(R.id.resultTv);
//        wheel = findViewById(R.id.wheel);
//        btnDummyPay = findViewById(R.id.btnDummyPay);
//
//        // tv_qr_img = findViewById(R.id.tv_qr_img); // Hidden - not needed
//        copywalletaddress = findViewById(R.id.copyaddress);
//        // img_qr = findViewById(R.id.img_qr); // Hidden - not needed
//
//        progressDialog = new ProgressDialog(this);
//
//        // Initialize buttons and status text
////        btnContactSupport = findViewById(R.id.btn_contact_support);
//        btnCheckStatus = findViewById(R.id.btn_check_status);
//        btnViewHistory = findViewById(R.id.btn_view_history);
//        txtStatus = findViewById(R.id.txt_status);
//
//        rl_extra = findViewById(R.id.rl_extra);
//        String str_extra = String.valueOf(SharePref.getInstance().getInt(SharePref.isExtra));     //0= visible
//        Log.d("visible_extra", str_extra);
//        if (str_extra.equals("0")) {
//            // rl_extra.setVisibility(View.VISIBLE);
//        }
//        if (str_extra.equals("1")) {
//            rl_extra.setVisibility(View.GONE);
//        }
//
//        Intent intent = getIntent();
//        if (intent != null) {
//            // We still get these values for backward compatibility
//            plan_id = intent.getStringExtra("plan_id");
//            chips_details = intent.getStringExtra("chips_details");
//            pay_amount = intent.getStringExtra("amount");
//
//            // Check if we're coming from homepage (Add Cash button)
//            String fromHomepage = intent.getStringExtra("homepage");
//            if (fromHomepage != null && fromHomepage.equals("homepage")) {
//                // Coming from Add Cash button, use default amount
//                pay_amount = "100"; // Default amount for Add Cash
//                Log.d("BuyChipsPaymentDetails", "Coming from homepage, using default amount: " + pay_amount);
//            }
//
//            // Get wallet address from generateWallet API response or use existing one
//            String walletAddress = intent.getStringExtra("wallet_address");
//            String privateKey = intent.getStringExtra("private_key");
//
//            // If no wallet address in intent, check SharedPreferences for existing one
//            if (walletAddress == null || walletAddress.isEmpty()) {
//
//
//                Log.d("BuyChipsPaymentDetails", "Using existing wallet address from SharedPreferences: " + walletAddress);
//            } else {
//                Log.d("BuyChipsPaymentDetails", "Received wallet address from intent: " + walletAddress);
//            }
//
//            // Don't set any dummy address - let checkPayAddressAndUpdateUI handle it
//            Log.d("BuyChipsPaymentDetails", "Wallet address from intent: " + walletAddress);
//
//            // Log the received values
//            Log.d("BuyChipsPaymentDetails", "Received amount: " + pay_amount);
//            Log.d("BuyChipsPaymentDetails", "Received wallet address: " + walletAddress);
//        }
//
//        if (pay_amount == null || pay_amount.isEmpty()) {
//            pay_amount = "100"; // Default amount
//            Log.d("BuyChipsPaymentDetails", "Using default amount: " + pay_amount);
//        }
//
//
//        txtChipsdetails = findViewById(R.id.txtChipsdetails);
//        txtQrInstruction = findViewById(R.id.txt_qr_instruction);
//        // Don't set any default address - will be set by checkPayAddressAndUpdateUI
//
//        // Update the UI to show the amount input field - will be updated dynamically
//        txtChipsdetails.setText("Add Funds to Your Wallet"); // Default text, will be updated by updatePaymentTexts()
//
//        // Set default amount in the input field
//
//        if (pay_amount != null && !pay_amount.isEmpty()) {
////            amountInput.setText(pay_amount);
//        } else {
////            amountInput.setText("100"); // Default amount
//        }
//
//        // Initialize payment gateway
//        PaymentGateWayInit();
//
//        selected_payment = SharePref.getInstance().getString(SharePref.PaymentType);
//
//        if (imgsub != null) {
//            imgsub.setVisibility(View.GONE);
//        }
//
////        imgPaynow.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                // Debug: Show that button was clicked
////                Toast.makeText(BuyChipsPaymentDetails.this, "PAY NOW BUTTON CLICKED - SHOWING QR", Toast.LENGTH_LONG).show();
////                Log.d("DEBUG_PAYMENT", "=== PAY NOW BUTTON CLICKED ===");
////
////                // Show static USDT QR code instead of web payme
////            }
////        });
//        imgback = findViewById(R.id.imgback);
//        imgback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Check if we're coming from homepage (Add Cash button)
//                String fromHomepage = getIntent().getStringExtra("homepage");
//                if (fromHomepage != null && fromHomepage.equals("homepage")) {
//                    // Coming from Add Cash button, go to homepage
//                    startActivity(new Intent(context, Homepage.class));
//                } else {
//                    // Coming from game, go back to previous activity
//                    finish();
//                }
//            }
//        });
//
//
//        if (btn_clicked.equals("")) {
//            spinBtn.setEnabled(true);
//        }
//
//        try {
//            date2 = sdf.parse(currentDateandTimeOld);
//            Log.d("date_one", String.valueOf(date2));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//        if (date1 != null) {
//            long difference = date2.getTime() - date1.getTime();
//            int days = (int) (difference / (1000 * 60 * 60 * 24));
//            Log.d("days_diff", String.valueOf(days));
//            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
//            Log.d("hours_diff", String.valueOf(hours));
//            int minute = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
//            Log.d("mins_diff", String.valueOf(minute));
////            SharePref.getInstance().putInt(SharePref.isEnable, Integer.parseInt(String.valueOf(minute)));
//            Log.d("difference_", String.valueOf(minute));
//            if (date1 != null && minute >= 10) {
//                spinBtn.setEnabled(true);
//            } else {
//                spinBtn.setEnabled(false);
//            }
//        }
//
//
//        // Check if copywalletaddress is not null before setting OnClickListener
//        if (copywalletaddress != null) {
//            copywalletaddress.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ClipboardManager clipboardManager = (ClipboardManager)
//                            getSystemService(Context.CLIPBOARD_SERVICE);
//                    ClipData clipData = ClipData.newPlainText("nonsense_data",
//                            address.getText().toString());
//                    clipboardManager.setPrimaryClip(clipData);
//                    Toast.makeText(BuyChipsPaymentDetails.this, "Address Copied", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        // Set up copy functionality for the new address field
//        ImageView imgCopyAddress = findViewById(R.id.img_copy_address);
//        if (imgCopyAddress != null) {
//            imgCopyAddress.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TextView txtAddress = findViewById(R.id.txt_address);
//                    if (txtAddress != null) {
//                        ClipboardManager clipboardManager = (ClipboardManager)
//                                getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clipData = ClipData.newPlainText("wallet_address",
//                                txtAddress.getText().toString());
//                        clipboardManager.setPrimaryClip(clipData);
//                        Toast.makeText(BuyChipsPaymentDetails.this, "Wallet Address Copied!", Toast.LENGTH_SHORT).show();
//                        Log.d("COPY_ADDRESS", "Copied address: " + txtAddress.getText().toString());
//                    }
//                }
//            });
//        }
//
//        // Set up contact support button
//        if (btnContactSupport != null) {
//            btnContactSupport.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openWhatsAppSupport();
//                }
//            });
//        }
//
//        // Set up check status button
//        if (btnCheckStatus != null) {
//            btnCheckStatus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    checkWalletStatus();
//                }
//            });
//        }
//
//        // Set up view history button
//        if (btnViewHistory != null) {
//            btnViewHistory.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openDepositHistory();
//                }
//            });
//        }
//
//        // No need for timer to check payment status
//        // Payment status will be handled by deep link callback
//
//        // Check pay_address on activity creation
//        CommonAPI.CALL_API_UserDetails(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//                checkPayAddressAndUpdateUI(resp);
//            }
//        });
//
//        // Initialize QR instruction text with default value
//        updateQrInstructionText();
//
//    }
//
//    private void initialize() {
//        initPaymentStage();
//    }
//
//    private void initPaymentStage() {
//        ChipsRepository chipsRepository = new ChipsRepository(getApplicationContext());
////        btnDummyPay.setVisibility(SharePref.getInstance().isApplicationStage() ? View.VISIBLE : View.GONE);
//
//        btnDummyPay.setOnClickListener(view -> {
//            progressDialog.show();
//            chipsRepository.call_api_dummyOrder(plan_id, amount).observe(this, new Observer<String>() {
//                @Override
//                public void onChanged(String s) {
//                    progressDialog.dismiss();
//                    if (s.equalsIgnoreCase("success"))
//                        SuccessBox();
//                    else
//                        Functions.showToast(context, s);
//                }
//            });
//        });
//    }
//
//   // @OnClick(R.id.spinBtn)
//    public void spin(View v) {
//        try {
//            date1 = sdf.parse(currentDateandTimeOld);
//            Log.d("date_two", String.valueOf(date2));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        degreeOld = degree % 1920;
//        // we calculate random angle for rotation of our wheel
//        degree = RANDOM.nextInt(360) + 1920;
////        degree = 270+360;                             //custom win
//        Log.d("degree_new", String.valueOf(degree));
//        Log.d("degree_old", String.valueOf(degreeOld));
//        // rotation effect on the center of the wheel
//        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnim.setDuration(3600);
//        rotateAnim.setFillAfter(true);
//        rotateAnim.setInterpolator(new DecelerateInterpolator());
//        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // we empty the result text view when the animation start
//                resultTv.setText("");
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // we display the correct sector pointed by the triangle at the end of the rotate animation
//                spinBtn.setEnabled(false);
//                resultTv.setVisibility(View.VISIBLE);
//                resultTv.setText("You will get " + getSector(360 - (degree % 360)) + " % extra coins!");
//                str_extraVal = getSector(360 - (degree % 360));
//                Log.d("getSector_val", str_extraVal);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        wheel.startAnimation(rotateAnim);
//    }
//
//
//
//    private String getSector(int degrees) {
//        int i = 0;
//        String text = null;
//
//        do {
//            float start = HALF_SECTOR * (i * 2 + 1);
//            float end = HALF_SECTOR * (i * 2 + 3);
//            Log.d("_start", String.valueOf(start));
//            Log.d("_end", String.valueOf(end));
//
//            if (degrees >= start && degrees < end) {
//                text = sectors[i];
//            }
//
//            i++;
//        } while (text == null && i < sectors.length);
//
//        return text;
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        try {
//            // Refresh user details when returning to this screen
//            CommonAPI.CALL_API_UserDetails(context, new Callback() {
//                @Override
//                public void Responce(String resp, String type, Bundle bundle) {
//                    // Update UI if needed after refreshing user details
//                    checkPayAddressAndUpdateUI(resp);
//                }
//            });
//
//            // Check if we're returning from a payment
//            if (getIntent() != null && getIntent().getData() != null) {
//                Uri data = getIntent().getData();
//                Log.d("DeepLink", "Received deep link: " + data.toString());
//                String scheme = data.getScheme();
//                String host = data.getHost();
//                String path = data.getPath();
//
//                Log.d("DeepLink", "Scheme: " + scheme + ", Host: " + host + ", Path: " + path);
//
//                // Handle both old and new deep link formats for backward compatibility
//                if (data.toString().contains("payment_success") ||
//                    (scheme != null && scheme.equals("play24") &&
//                     ((host != null && host.equals("payment") && path != null && path.equals("/success")) ||
//                      (host != null && host.equals("payment_success"))))) {
//
//                    // Handle successful payment return from web
//                    String amount = data.getQueryParameter("amount");
//                    String status = data.getQueryParameter("status");
//                    String transactionId = data.getQueryParameter("transaction_id");
//
//                    Log.d("DeepLink", "Payment success with amount: " + amount + ", status: " + status + ", transaction_id: " + transactionId);
//
//                    // Show success dialog
//                    showDeepLinkSuccessDialog();
//
//                } else if (data.toString().contains("payment_failure") ||
//                          (scheme != null && scheme.equals("play24") &&
//                           ((host != null && host.equals("payment") && path != null && path.equals("/failure")) ||
//                            (host != null && host.equals("payment_failure"))))) {
//
//                    // Handle failed payment return from web
//                    String status = data.getQueryParameter("status");
//                    Log.d("DeepLink", "Payment failure with status: " + status);
//                    Toast.makeText(BuyChipsPaymentDetails.this, "Payment failed or was cancelled", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//            Log.e("DeepLink", "Error handling deep link", e);
//        }
//    }
//
//    // Store user token for saving wallet
//    private String userToken = "";
//
//    // Store current recharge option from user data
//    private String currentRechargeOption = "";
//
//    // Flag to prevent showing wallet selection popup multiple times
//    private boolean walletSelectionShown = false;
//
//    private void checkPayAddressAndUpdateUI(String response) {
//        try {
//            Log.d("PayAddressCheck", "Step 1: Fetch user data and check pay_address");
//
//            if (response == null || response.isEmpty()) {
//                Log.e("PayAddressCheck", "User data is null, calling GENERATE_WALLET API");
//                callGenerateWalletAPI();
//                return;
//            }
//
//            JSONObject jsonObject = new JSONObject(response);
//            String code = jsonObject.optString("code", "");
//
//            if (code.equalsIgnoreCase("200")) {
//                JSONArray userDataArray = jsonObject.optJSONArray("user_data");
//                if (userDataArray != null && userDataArray.length() > 0) {
//                    JSONObject jsonObject0 = userDataArray.getJSONObject(0);
//                    String pay_address = jsonObject0.optString("pay_address", null);
//
//                    // Extract token from user data
//                    userToken = jsonObject0.optString("token", "");
//                    Log.d("PayAddressCheck", "User token from user data: " + (userToken.isEmpty() ? "empty" : "***"));
//
//                    // Check rechargeOption for wallet selection popup
//                    String rechargeOption = jsonObject0.optString("rechargeOption", null);
//                    Log.d("PayAddressCheck", "rechargeOption from user data: " + rechargeOption);
//
//                    // Store rechargeOption for later use in monitor wallet API
//                    currentRechargeOption = rechargeOption;
//                    Log.d("PayAddressCheck", "Stored currentRechargeOption: " + currentRechargeOption);
//
//                    // Update QR instruction text based on payment type
//                    updateQrInstructionText();
//
//                    // Show wallet selection popup if rechargeOption is null and not already shown
//                    if (rechargeOption == null || rechargeOption.isEmpty() || rechargeOption.equals("null")) {
//                        if (!walletSelectionShown) {
//                            Log.d("PayAddressCheck", "rechargeOption is null, showing wallet selection popup");
//                            walletSelectionShown = true; // Set flag to prevent multiple popups
//                            showWalletSelectionPopup();
//                        } else {
//                            Log.d("PayAddressCheck", "Wallet selection popup already shown, skipping");
//                        }
//                        return; // Don't proceed with address checking until user selects wallet option
//                    }
//
//                    Log.d("PayAddressCheck", "pay_address from user data: " + pay_address);
//
//                    // Step 1: Check if pay_address is null in user data
//                    if (pay_address == null || pay_address.isEmpty() || pay_address.equals("null")) {
//                        Log.d("PayAddressCheck", "pay_address is null in user data, calling GENERATE_WALLET API");
//                        callGenerateWalletAPI();
//                    } else {
//                        Log.d("PayAddressCheck", "pay_address is not null, showing existing address from user data");
//                        // Step 3: Show the pay_address from user data in address field
//                        updateAddressField(pay_address);
//                    }
//                } else {
//                    Log.e("PayAddressCheck", "User data array is null/empty, calling GENERATE_WALLET API");
//                    callGenerateWalletAPI();
//                }
//            } else {
//                Log.e("PayAddressCheck", "API response code not 200, calling GENERATE_WALLET API");
//                callGenerateWalletAPI();
//            }
//        } catch (Exception e) {
//            Log.e("PayAddressCheck", "Error processing user data: " + e.getMessage() + ", calling GENERATE_WALLET API");
//            e.printStackTrace();
//            callGenerateWalletAPI();
//        }
//    }
//
//    private void updateAddressField(String text) {
//        try {
//            TextView txtAddress = findViewById(R.id.txt_address);
//            if (txtAddress != null) {
//                txtAddress.setText(text);
//                Log.d("PayAddressCheck", "Updated address field with: " + text);
//
//                // Generate and display QR code when address is set
//                if (text != null && !text.isEmpty() && !text.contains("Error") && !text.contains("Generating")) {
//                    generateAndDisplayQRCode(text);
//                }
//            } else {
//                Log.e("PayAddressCheck", "txt_address TextView not found");
//            }
//        } catch (Exception e) {
//            Log.e("PayAddressCheck", "Error updating address field: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void generateAndDisplayQRCode(String walletAddress) {
//        try {
//            Log.d("QRCode", "Generating QR code for address: " + walletAddress);
//
//            // Find the QR code ImageView
//            ImageView qrCodeImageView = findViewById(R.id.qr_code_image);
//            if (qrCodeImageView != null) {
//                // Create QR code URL using the API you specified
//                String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + walletAddress;
//                Log.d("QRCode", "QR code URL: " + qrCodeUrl);
//
//                // First load a dummy QR code as placeholder
//                String dummyQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=Loading...";
//
//                // Load dummy QR first, then load actual QR
//                Picasso.get()
//                    .load(dummyQrUrl)
//                    .into(qrCodeImageView, new com.squareup.picasso.Callback() {
//                        @Override
//                        public void onSuccess() {
//                            Log.d("QRCode", "Dummy QR loaded, now loading actual QR");
//                            // Now load the actual QR code
//                            Picasso.get()
//                                .load(qrCodeUrl)
//                                .into(qrCodeImageView, new com.squareup.picasso.Callback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Log.d("QRCode", "Actual QR code loaded successfully");
//                                    }
//
//                                    @Override
//                                    public void onError(Exception e) {
//                                        Log.e("QRCode", "Error loading actual QR code: " + e.getMessage());
//                                        // Keep the dummy QR code if actual loading fails
//                                        Toast.makeText(BuyChipsPaymentDetails.this, "QR code loaded with placeholder", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Log.e("QRCode", "Error loading dummy QR code: " + e.getMessage());
//                            // If even dummy fails, show error message
//                            Toast.makeText(BuyChipsPaymentDetails.this, "Failed to load QR code", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                Log.d("QRCode", "QR code generation initiated for address: " + walletAddress);
//            } else {
//                Log.e("QRCode", "QR code ImageView not found");
//            }
//        } catch (Exception e) {
//            Log.e("QRCode", "Error generating QR code: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void callGenerateWalletAPI() {
//        Log.d("GenerateWallet", "Calling GENERATE_WALLET API with user token");
//
//        // Show loading state
//        updateAddressField("Generating wallet...");
//
//        // Validate user token
//        if (userToken.isEmpty()) {
//            Log.e("GenerateWallet", "User token is empty, cannot generate wallet");
//            updateAddressField("Error: User token missing");
//            return;
//        }
//
//        String url = Const.GENERATE_WALLET;
//
//        // Create JSON body with auth token
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("auth", userToken);
//            Log.d("GenerateWallet", "Sending auth token: ***");
//        } catch (JSONException e) {
//            Log.e("GenerateWallet", "Error creating JSON body: " + e.getMessage());
//            updateAddressField("Error: Failed to create request");
//            return;
//        }
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.d("GenerateWallet", "GENERATE_WALLET API Response: " + response.toString());
//                    parseGenerateWalletResponse(response.toString());
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("GenerateWallet", "GENERATE_WALLET API Error: " + error.getMessage());
//                    updateAddressField("Failed to generate wallet");
//
//                    // Show error message to user
//                    if (error.networkResponse != null) {
//                        Log.e("GenerateWallet", "Error code: " + error.networkResponse.statusCode);
//                        try {
//                            String responseBody = new String(error.networkResponse.data, "utf-8");
//                            Log.e("GenerateWallet", "Error response body: " + responseBody);
//                        } catch (Exception e) {
//                            Log.e("GenerateWallet", "Error parsing error response: " + e.getMessage());
//                        }
//                    }
//                }
//            }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        // Set timeout
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//            30000, // 30 seconds timeout
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        // Add request to queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonRequest);
//    }
//
//    private void parseGenerateWalletResponse(String response) {
//        try {
//            Log.d("GenerateWallet", "Parsing GENERATE_WALLET response");
//            JSONObject jsonObject = new JSONObject(response);
//            boolean status = jsonObject.optBoolean("status", false);
//            String message = jsonObject.optString("message", "");
//
//            Log.d("GenerateWallet", "Status: " + status + ", Message: " + message);
//
//            if (status) {
//                JSONObject walletObject = jsonObject.optJSONObject("wallet");
//                if (walletObject != null) {
//                    String address = walletObject.optString("address", "");
//                    String privateKey = walletObject.optString("privateKey", "");
//
//                    Log.d("GenerateWallet", "Generated address: " + address);
//                    Log.d("GenerateWallet", "Generated privateKey: " + (privateKey.isEmpty() ? "empty" : "***"));
//
//                    if (!address.isEmpty()) {
//                        // Show the generated address in address field
//                        updateAddressField(address);
//                        Log.d("GenerateWallet", "Successfully showing generated address in address field: " + address);
//                    } else {
//                        Log.e("GenerateWallet", "Generated address is empty");
//                        updateAddressField("Error: Empty address generated");
//                    }
//                } else {
//                    Log.e("GenerateWallet", "Wallet object not found in response");
//                    updateAddressField("Error: Invalid wallet response");
//                }
//            } else {
//                Log.e("GenerateWallet", "Wallet generation failed: " + message);
//                updateAddressField("Error: " + message);
//            }
//        } catch (JSONException e) {
//            Log.e("GenerateWallet", "JSON parsing error: " + e.getMessage());
//            e.printStackTrace();
//            updateAddressField("Error: Failed to parse response");
//        } catch (Exception e) {
//            Log.e("GenerateWallet", "Unexpected error: " + e.getMessage());
//            e.printStackTrace();
//            updateAddressField("Error: Unexpected error occurred");
//        }
//    }
//
//    private void showWalletSelectionPopup() {
//        Log.d("WalletSelection", "Showing wallet selection popup for new user");
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dialogView = null;
//
//        // Try to create custom layout for the popup
//        try {
//            LayoutInflater inflater = getLayoutInflater();
//            dialogView = inflater.inflate(R.layout.dialog_wallet_selection, null);
//            Log.d("WalletSelection", "Custom layout loaded successfully");
//        } catch (Exception e) {
//            Log.e("WalletSelection", "Failed to load custom layout: " + e.getMessage());
//            dialogView = null;
//        }
//
//        AlertDialog dialog;
//
//        // If custom layout loaded successfully, use it
//        if (dialogView != null) {
//            Log.d("WalletSelection", "Using custom layout dialog");
//            builder.setView(dialogView);
//            dialog = builder.create();
//
//            // Set up click listeners for custom layout buttons
//            Button btnUSDT = dialogView.findViewById(R.id.btn_usdt);
////            Button btnRBM = dialogView.findViewById(R.id.btn_rbm);
//
//            if (btnUSDT != null) {
//                btnUSDT.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d("WalletSelection", "User selected USDT wallet (custom layout)");
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        handleWalletSelection("USDT");
//                    }
//                });
//            } else {
//                Log.e("WalletSelection", "USDT button not found in custom layout");
//            }
//
////            if (btnRBM != null) {
////                btnRBM.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        Log.d("WalletSelection", "User selected RBM wallet (custom layout)");
////                        if (dialog.isShowing()) {
////                            dialog.dismiss();
////                        }
////                        handleWalletSelection("RBM");
////                    }
////                });
////            } else {
////                Log.e("WalletSelection", "RBM button not found in custom layout");
////            }
//        } else {
//            // Fallback to simple dialog
//            Log.d("WalletSelection", "Using simple dialog as fallback");
//            builder.setTitle("Select Wallet Option")
//                   .setMessage("Please select your preferred wallet option for payments:")
//                   .setPositiveButton("USDT", new DialogInterface.OnClickListener() {
//                       @Override
//                       public void onClick(DialogInterface dialog, int which) {
//                           Log.d("WalletSelection", "User selected USDT wallet (simple dialog)");
//                           handleWalletSelection("USDT");
//                       }
//                   })
//                   .setNegativeButton("RBM", new DialogInterface.OnClickListener() {
//                       @Override
//                       public void onClick(DialogInterface dialog, int which) {
//                           Log.d("WalletSelection", "User selected RBM wallet (simple dialog)");
//                           handleWalletSelection("RBM");
//                       }
//                   })
//                   .setCancelable(false);
//            dialog = builder.create();
//        }
//
//        dialog.setCancelable(false); // Prevent dismissing without selection
//        dialog.show();
//        Log.d("WalletSelection", "Dialog shown successfully");
//    }
//
//    private void handleWalletSelection(String selectedWallet) {
//        Log.d("WalletSelection", "Handling wallet selection: " + selectedWallet);
//
//        // Reset the flag since user has made a selection
//        walletSelectionShown = false;
//        Log.d("WalletSelection", "Reset walletSelectionShown flag");
//
//        // Send selected wallet option to RECHARGE_OPTION API
//        sendRechargeOptionToAPI(selectedWallet);
//    }
//
//    private void sendRechargeOptionToAPI(String selectedWallet) {
//        Log.d("RechargeOption", "Sending rechargeOption to API: " + selectedWallet);
//
//        // Validate user token
//        if (userToken.isEmpty()) {
//            Log.e("RechargeOption", "User token is empty, cannot send recharge option");
//            Toast.makeText(this, "Error: User authentication failed", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String url = Const.RECHARGE_OPTION;
//
//        // Create JSON body with rechargeOption and auth token
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("rechargeOption", selectedWallet);
//            jsonBody.put("auth", userToken);
//            Log.d("RechargeOption", "Sending rechargeOption: " + selectedWallet + ", auth: ***");
//        } catch (JSONException e) {
//            Log.e("RechargeOption", "Error creating JSON body: " + e.getMessage());
//            Toast.makeText(this, "Error: Failed to create request", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.d("RechargeOption", "RECHARGE_OPTION API Response: " + response.toString());
//                    parseRechargeOptionResponse(response.toString(), selectedWallet);
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("RechargeOption", "RECHARGE_OPTION API Error: " + error.getMessage());
//                    Toast.makeText(BuyChipsPaymentDetails.this, "Failed to save wallet option", Toast.LENGTH_SHORT).show();
//
//                    // Show error message to user
//                    if (error.networkResponse != null) {
//                        Log.e("RechargeOption", "Error code: " + error.networkResponse.statusCode);
//                        try {
//                            String responseBody = new String(error.networkResponse.data, "utf-8");
//                            Log.e("RechargeOption", "Error response body: " + responseBody);
//                        } catch (Exception e) {
//                            Log.e("RechargeOption", "Error parsing error response: " + e.getMessage());
//                        }
//                    }
//
//                    // Continue with normal flow even if API fails
//                    continueWithNormalFlow(selectedWallet);
//                }
//            }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        // Set timeout
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//            30000, // 30 seconds timeout
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        // Add request to queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonRequest);
//    }
//
//    private void parseRechargeOptionResponse(String response, String selectedWallet) {
//        try {
//            Log.d("RechargeOption", "Parsing RECHARGE_OPTION response");
//            JSONObject jsonObject = new JSONObject(response);
//            boolean status = jsonObject.optBoolean("status", false);
//            String message = jsonObject.optString("message", "");
//
//            Log.d("RechargeOption", "Status: " + status + ", Message: " + message);
//
//            if (status) {
//                Log.d("RechargeOption", "Recharge option saved successfully: " + message);
//                Toast.makeText(this, "Selected " + selectedWallet + " wallet option", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.e("RechargeOption", "Failed to save recharge option: " + message);
//                Toast.makeText(this, "Warning: " + message, Toast.LENGTH_SHORT).show();
//            }
//
//            // Continue with normal flow after API response
//            continueWithNormalFlow(selectedWallet);
//
//        } catch (JSONException e) {
//            Log.e("RechargeOption", "JSON parsing error: " + e.getMessage());
//            e.printStackTrace();
//            Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
//
//            // Continue with normal flow even if parsing fails
//            continueWithNormalFlow(selectedWallet);
//        } catch (Exception e) {
//            Log.e("RechargeOption", "Unexpected error: " + e.getMessage());
//            e.printStackTrace();
//            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
//
//            // Continue with normal flow even if error occurs
//            continueWithNormalFlow(selectedWallet);
//        }
//    }
//
//    private void continueWithNormalFlow(String selectedWallet) {
//        Log.d("WalletSelection", "Continuing with normal flow after wallet selection: " + selectedWallet);
//
//        // Update currentRechargeOption with the selected wallet
//        currentRechargeOption = selectedWallet;
//
//        // Update QR instruction text based on selected payment type
//        updateQrInstructionText();
//
//        // Continue with the normal address checking flow
//        CommonAPI.CALL_API_UserDetails(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//                // Skip rechargeOption check this time and proceed with address logic
//                checkPayAddressAndUpdateUIAfterWalletSelection(resp);
//            }
//        });
//    }
//
//    private void checkPayAddressAndUpdateUIAfterWalletSelection(String response) {
//        try {
//            Log.d("PayAddressCheck", "Checking pay_address after wallet selection");
//
//            if (response == null || response.isEmpty()) {
//                Log.e("PayAddressCheck", "User data is null, calling GENERATE_WALLET API");
//                callGenerateWalletAPI();
//                return;
//            }
//
//            JSONObject jsonObject = new JSONObject(response);
//            String code = jsonObject.optString("code", "");
//
//            if (code.equalsIgnoreCase("200")) {
//                JSONArray userDataArray = jsonObject.optJSONArray("user_data");
//                if (userDataArray != null && userDataArray.length() > 0) {
//                    JSONObject jsonObject0 = userDataArray.getJSONObject(0);
//                    String pay_address = jsonObject0.optString("pay_address", null);
//
//                    // Extract token from user data
//                    userToken = jsonObject0.optString("token", "");
//                    Log.d("PayAddressCheck", "User token from user data: " + (userToken.isEmpty() ? "empty" : "***"));
//
//                    Log.d("PayAddressCheck", "pay_address from user data: " + pay_address);
//
//                    // Check if pay_address is null in user data (skip rechargeOption check)
//                    if (pay_address == null || pay_address.isEmpty() || pay_address.equals("null")) {
//                        Log.d("PayAddressCheck", "pay_address is null in user data, calling GENERATE_WALLET API");
//                        callGenerateWalletAPIAfterWalletSelection();
//                    } else {
//                        Log.d("PayAddressCheck", "pay_address is not null, showing existing address from user data");
//                        // Step 3: Show the pay_address from user data in address field
//                        updateAddressField(pay_address);
//
//                        // 🎯 Auto-focus amount field after wallet selection to continue payment flow
//                        autoFocusAmountFieldAfterWalletSelection();
//                    }
//                } else {
//                    Log.e("PayAddressCheck", "User data array is null/empty, calling GENERATE_WALLET API");
//                    callGenerateWalletAPI();
//                }
//            } else {
//                Log.e("PayAddressCheck", "API response code not 200, calling GENERATE_WALLET API");
//                callGenerateWalletAPI();
//            }
//        } catch (Exception e) {
//            Log.e("PayAddressCheck", "Error processing user data: " + e.getMessage() + ", calling GENERATE_WALLET API");
//            e.printStackTrace();
//            callGenerateWalletAPI();
//        }
//    }
//
//    private void makeDirectPaymentRequest(String userId, String token, String amount) {
//        try {
//            // Log detailed information about the request
//            Log.d("Payment", "Making DIRECT payment request with userId: " + userId + ", amount: " + amount);
//            Log.d("Payment", "API URL: " + Const.MAIN + "api/Plan/Place_Order_EkQR");
//
//
//            // Make a POST request to the EkQR payment gateway API
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.MAIN + "api/Plan/Place_Order_EkQR",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progressDialog.dismiss();
//                            Log.d("Payment", "API Response: " + response);
//
//                            // Check if response contains HTML content
//                            if (response.contains("<html") || response.contains("<div")) {
//                                Log.e("Payment", "Received HTML response instead of JSON: " + response);
//
//                                // Extract error message if possible
//                                String errorMessage = "Server returned an invalid response. Please try again later.";
//                                if (response.contains("Message:")) {
//                                    try {
//                                        int messageStart = response.indexOf("Message:") + 9;
//                                        int messageEnd = response.indexOf("</p>", messageStart);
//                                        if (messageEnd > messageStart) {
//                                            String extractedMessage = response.substring(messageStart, messageEnd).trim();
//                                            errorMessage = "Server error: " + extractedMessage;
//                                            Log.e("Payment", "Extracted error message: " + extractedMessage);
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e("Payment", "Error extracting message from HTML: " + e.getMessage());
//                                    }
//                                }
//
//                                Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                                return;
//                            }
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String code = jsonObject.getString("code");
//
//                                if (code.equals("200")) {
//                                    // Check if payment_url exists in the response
//                                    if (jsonObject.has("payment_url")) {
//                                        String paymentUrl = jsonObject.getString("payment_url");
//
//                                        // Log the payment URL for debugging
//                                        Log.d("Payment", "Payment URL: " + paymentUrl);
//
//                                        // Open the payment URL in browser
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
//                                        startActivity(intent);
//
//                                        // Show a toast to inform the user about automatic processing
//                                        Toast.makeText(BuyChipsPaymentDetails.this, "After successful payment, cash will be automatically added to your account", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Log.e("Payment", "Missing payment_url in response: " + response);
//                                        Toast.makeText(BuyChipsPaymentDetails.this, "Payment URL not found in response", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    String message = jsonObject.has("message") ? jsonObject.getString("message") : "Unknown error";
//                                    Toast.makeText(BuyChipsPaymentDetails.this, message, Toast.LENGTH_SHORT).show();
//                                    Log.e("Payment", "Error code: " + code + ", Message: " + message);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e("Payment", "JSON parsing error: " + e.getMessage() + "\nResponse: " + response);
//                                Toast.makeText(BuyChipsPaymentDetails.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            progressDialog.dismiss();
//                            Log.e("Payment", "API Error: " + error.toString());
//
//                            // Get more detailed error information
//                            String errorMessage = "Network error. Please try again.";
//                            if (error.networkResponse != null) {
//                                errorMessage += " Status code: " + error.networkResponse.statusCode;
//                                try {
//                                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                                    Log.e("Payment", "Error response body: " + responseBody);
//                                    errorMessage += "\nResponse: " + responseBody;
//                                } catch (Exception e) {
//                                    Log.e("Payment", "Error parsing error response: " + e.getMessage());
//                                }
//                            } else if (error.getCause() != null) {
//                                Log.e("Payment", "Error cause: " + error.getCause().getMessage());
//                                errorMessage += "\nCause: " + error.getCause().getMessage();
//                            } else {
//                                Log.e("Payment", "Unknown error: " + error.toString());
//                            }
//
//                            Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user_id", userId);
//                    params.put("amount", amount);
//                    params.put("token", token);
//                    // Backend requires a plan_id, so we'll use a default value
//                    params.put("plan_id", "1"); // Using default plan_id=1
//
//                    // Log the parameters for debugging
//                    Log.d("Payment", "API Parameters: " + params.toString());
//
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("Token", Const.TOKEN);
//                    return headers;
//                }
//            };
//
//            // Add the request to the RequestQueue
//            RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsPaymentDetails.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//
//        } catch (Exception e) {
//            Log.e("Payment", "Error in makeDirectPaymentRequest", e);
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            Toast.makeText(BuyChipsPaymentDetails.this, "Error processing payment. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void makePaymentRequest(String userId, String token, String amount, String planId) {
//        try {
//            // Log detailed information about the request
//            Log.d("Payment", "Making payment request with userId: " + userId + ", amount: " + amount + ", planId: " + planId);
//            Log.d("Payment", "API URL: " + Const.MAIN + "api/Plan/Place_Order_EkQR");
//
//            // Make a POST request to the EkQR payment gateway API
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.MAIN + "api/Plan/Place_Order_EkQR",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            progressDialog.dismiss();
//                            Log.d("Payment", "API Response: " + response);
//
//                            // Check if response contains HTML content
//                            if (response.contains("<html") || response.contains("<div")) {
//                                Log.e("Payment", "Received HTML response instead of JSON: " + response);
//
//                                // Extract error message if possible
//                                String errorMessage = "Server returned an invalid response. Please try again later.";
//                                if (response.contains("Message:")) {
//                                    try {
//                                        int messageStart = response.indexOf("Message:") + 9;
//                                        int messageEnd = response.indexOf("</p>", messageStart);
//                                        if (messageEnd > messageStart) {
//                                            String extractedMessage = response.substring(messageStart, messageEnd).trim();
//                                            errorMessage = "Server error: " + extractedMessage;
//                                            Log.e("Payment", "Extracted error message: " + extractedMessage);
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e("Payment", "Error extracting message from HTML: " + e.getMessage());
//                                    }
//                                }
//
//                                Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                                return;
//                            }
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String code = jsonObject.getString("code");
//
//                                if (code.equals("200")) {
//                                    // Check if payment_url exists in the response
//                                    if (jsonObject.has("payment_url")) {
//                                        String paymentUrl = jsonObject.getString("payment_url");
//
//                                        // Log the payment URL for debugging
//                                        Log.d("Payment", "Payment URL: " + paymentUrl);
//
//                                        // Open the payment URL in browser
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
//                                        startActivity(intent);
//
//                                        // Show a toast to inform the user about automatic processing
//                                        Toast.makeText(BuyChipsPaymentDetails.this, "After successful payment, cash will be automatically added to your account", Toast.LENGTH_LONG).show();
//
//                                        // Add a handler to check payment status after a delay
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                dialog_payment_success();
//                                            }
//                                        }, 5000); // 5 seconds delay
//                                    } else {
//                                        Log.e("Payment", "Missing payment_url in response: " + response);
//                                        Toast.makeText(BuyChipsPaymentDetails.this, "Payment URL not found in response", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    String message = jsonObject.has("message") ? jsonObject.getString("message") : "Unknown error";
//                                    Toast.makeText(BuyChipsPaymentDetails.this, message, Toast.LENGTH_SHORT).show();
//                                    Log.e("Payment", "Error code: " + code + ", Message: " + message);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e("Payment", "JSON parsing error: " + e.getMessage() + "\nResponse: " + response);
//                                Toast.makeText(BuyChipsPaymentDetails.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            progressDialog.dismiss();
//                            Log.e("Payment", "API Error: " + error.toString());
//
//                            // Get more detailed error information
//                            String errorMessage = "Network error. Please try again.";
//                            if (error.networkResponse != null) {
//                                errorMessage += " Status code: " + error.networkResponse.statusCode;
//                                try {
//                                    String responseBody = new String(error.networkResponse.data, "utf-8");
//                                    Log.e("Payment", "Error response body: " + responseBody);
//                                    errorMessage += "\nResponse: " + responseBody;
//                                } catch (Exception e) {
//                                    Log.e("Payment", "Error parsing error response: " + e.getMessage());
//                                }
//                            }
//
//                            Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user_id", userId);
//                    params.put("amount", amount);
//                    params.put("token", token);
//                    params.put("plan_id", planId);
//
//                    // Log the parameters for debugging
//                    Log.d("Payment", "API Parameters: " + params.toString());
//                    Log.d("Payment", "API URL: " + Const.MAIN + "api/Plan/Place_Order_EkQR");
//                    Log.d("Payment", "Token: " + Const.TOKEN);
//
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("Token", Const.TOKEN);
//                    return headers;
//                }
//            };
//
//            // Add the request to the RequestQueue
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//
//        } catch (Exception e) {
//            Log.e("Payment", "Error in openWebPayment", e);
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            Toast.makeText(BuyChipsPaymentDetails.this, "Error processing payment. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Gets plan details if amount is not available
//     * This method is no longer needed since we're using a direct approach
//     * but kept for backward compatibility
//     */
//    private void getPlanDetails(String userId, final String providedPlanId) {
//        // Show progress dialog
//        progressDialog.show();
//
//        // Get token from shared preferences
//        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        String token = prefs.getString("token", "");
//
//        // Default amount
//        String amount = "100";
//
//        // First, get the list of available plans to find a valid plan ID
//        StringRequest planRequest = new StringRequest(Request.Method.POST, Const.MAIN + "api/Plan",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String code = jsonObject.getString("code");
//
//                            if (code.equals("200")) {
//                                // Get the list of plans
//                                JSONArray planDetails = jsonObject.getJSONArray("PlanDetails");
//                                String validPlanId = providedPlanId; // Use provided plan ID if available
//
//                                // If no plan ID provided, find a valid one
//                                if (validPlanId == null || validPlanId.isEmpty()) {
//                                    // Try to find a plan with a matching amount
//                                    for (int i = 0; i < planDetails.length(); i++) {
//                                        JSONObject plan = planDetails.getJSONObject(i);
//                                        String planPrice = plan.getString("price");
//
//                                        if (planPrice.equals(amount)) {
//                                            validPlanId = plan.getString("id");
//                                            Log.d("Payment", "Found matching plan: " + validPlanId + " with price: " + planPrice);
//                                            break;
//                                        }
//                                    }
//
//                                    // If no matching plan, get the first active plan
//                                    if (validPlanId == null || validPlanId.isEmpty()) {
//                                        for (int i = 0; i < planDetails.length(); i++) {
//                                            JSONObject plan = planDetails.getJSONObject(i);
//                                            String status = plan.getString("status");
//
//                                            if (status.equals("1")) { // Assuming 1 means active
//                                                validPlanId = plan.getString("id");
//                                                Log.d("Payment", "Using first active plan: " + validPlanId);
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    // If still no valid plan, use the first plan
//                                    if ((validPlanId == null || validPlanId.isEmpty()) && planDetails.length() > 0) {
//                                        JSONObject plan = planDetails.getJSONObject(0);
//                                        validPlanId = plan.getString("id");
//                                        Log.d("Payment", "Using first available plan: " + validPlanId);
//                                    }
//                                }
//
//                                // Now make the payment request with the valid plan ID
//                                if (validPlanId != null && !validPlanId.isEmpty()) {
//                                    makeLegacyPaymentRequest(userId, token, amount, validPlanId);
//                                } else {
//                                    // If no valid plan found, show error
//                                    progressDialog.dismiss();
//                                    Toast.makeText(BuyChipsPaymentDetails.this, "No valid plan found. Please try again later.", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                // If we can't get plans, show error
//                                progressDialog.dismiss();
//                                String message = jsonObject.has("message") ? jsonObject.getString("message") : "Error getting plans";
//                                Toast.makeText(BuyChipsPaymentDetails.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (Exception e) {
//                            Log.e("Payment", "Error parsing plans: " + e.getMessage());
//                            progressDialog.dismiss();
//                            Toast.makeText(BuyChipsPaymentDetails.this, "Error processing plans. Please try again.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Payment", "Error getting plans: " + error.toString());
//                        progressDialog.dismiss();
//                        Toast.makeText(BuyChipsPaymentDetails.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", userId);
//                params.put("token", token);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Token", Const.TOKEN);
//                return headers;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        planRequest.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(planRequest);
//    }
//
//    private void makeLegacyPaymentRequest(String userId, String token, String amount, String planId) {
//        try {
//            // Make a POST request to the EkQR payment gateway API
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.MAIN + "api/Plan/Place_Order_EkQR",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progressDialog.dismiss();
//                        Log.d("Payment", "API Response: " + response);
//
//                        // Check if response contains HTML content
//                        if (response.contains("<html") || response.contains("<div")) {
//                            Log.e("Payment", "Received HTML response instead of JSON: " + response);
//
//                            // Extract error message if possible
//                            String errorMessage = "Server returned an invalid response. Please try again later.";
//                            if (response.contains("Message:")) {
//                                try {
//                                    int messageStart = response.indexOf("Message:") + 9;
//                                    int messageEnd = response.indexOf("</p>", messageStart);
//                                    if (messageEnd > messageStart) {
//                                        String extractedMessage = response.substring(messageStart, messageEnd).trim();
//                                        errorMessage = "Server error: " + extractedMessage;
//                                        Log.e("Payment", "Extracted error message: " + extractedMessage);
//                                    }
//                                } catch (Exception e) {
//                                    Log.e("Payment", "Error extracting message from HTML: " + e.getMessage());
//                                }
//                            }
//
//                            Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String code = jsonObject.getString("code");
//
//                            if (code.equals("200")) {
//                                // Check if payment_url exists in the response
//                                if (jsonObject.has("payment_url")) {
//                                    String paymentUrl = jsonObject.getString("payment_url");
//
//                                    // Log the payment URL for debugging
//                                    Log.d("Payment", "Payment URL: " + paymentUrl);
//
//                                    // Open the payment URL in browser
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
//                                    startActivity(intent);
//
//                                    // Show a toast to inform the user about automatic processing
//                                    Toast.makeText(BuyChipsPaymentDetails.this, "After successful payment, cash will be automatically added to your account", Toast.LENGTH_LONG).show();
//                                } else {
//                                    Log.e("Payment", "Missing payment_url in response: " + response);
//                                    Toast.makeText(BuyChipsPaymentDetails.this, "Payment URL not found in response", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                String message = jsonObject.has("message") ? jsonObject.getString("message") : "Unknown error";
//                                Toast.makeText(BuyChipsPaymentDetails.this, message, Toast.LENGTH_SHORT).show();
//                                Log.e("Payment", "Error code: " + code + ", Message: " + message);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("Payment", "JSON parsing error: " + e.getMessage() + "\nResponse: " + response);
//                            Toast.makeText(BuyChipsPaymentDetails.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
//                        Log.e("Payment", "API Error: " + error.toString());
//
//                        // Get more detailed error information
//                        String errorMessage = "Network error. Please try again.";
//                        if (error.networkResponse != null) {
//                            errorMessage += " Status code: " + error.networkResponse.statusCode;
//                            try {
//                                String responseBody = new String(error.networkResponse.data, "utf-8");
//                                Log.e("Payment", "Error response body: " + responseBody);
//                                errorMessage += "\nResponse: " + responseBody;
//                            } catch (Exception e) {
//                                Log.e("Payment", "Error parsing error response: " + e.getMessage());
//                            }
//                        } else if (error.getCause() != null) {
//                            Log.e("Payment", "Error cause: " + error.getCause().getMessage());
//                            errorMessage += "\nCause: " + error.getCause().getMessage();
//                        } else {
//                            Log.e("Payment", "Unknown error: " + error.toString());
//                        }
//
//                        Toast.makeText(BuyChipsPaymentDetails.this, errorMessage, Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", userId);
//                params.put("amount", amount);
//                params.put("token", token);
//                params.put("plan_id", planId);
//
//                // Log the parameters for debugging
//                Log.d("Payment", "API Parameters: " + params.toString());
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Token", Const.TOKEN);
//                return headers;
//            }
//        };
//
//            // Add the request to the RequestQueue
//            RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsPaymentDetails.this);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(stringRequest);
//        } catch (Exception e) {
//            Log.e("Payment", "Error in makeLegacyPaymentRequest", e);
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            Toast.makeText(BuyChipsPaymentDetails.this, "Error processing payment. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void PaymentGateWayInit() {
//        _paymentGetway_paymt = new PaymentGetway_Paymt(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//
//                if (resp.equalsIgnoreCase(Variables.SUCCESS)) {
//                    dialog_payment_success();
//                } else {
//
//                }
//
//            }
//        });
//
//        _paymentGetwayCashFree = new PaymentGetway_CashFree(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//
//                if (resp.equalsIgnoreCase(Variables.SUCCESS)) {
//                    dialog_payment_success();
//                } else {
//
//                }
//
//            }
//        });
//
////        paymentGetway_payuMoney = new PaymentGetway_PayuMoney(context, new Callback() {
//////            @Override
//////            public void Responce(String resp, String type, Bundle bundle) {
//////
//////                if (resp.equalsIgnoreCase(Variables.SUCCESS)) {
//////                    dialog_payment_success();
//////                } else {
//////
//////                }
//////
//////            }
//////        });
////    }
//
//    PaymentGetway_CashFree _paymentGetwayCashFree;
//    PaymentGetway_PayuMoney paymentGetway_payuMoney;
//    PaymentGetway_Paymt _paymentGetway_paymt;
//
//
//    @Override
//    public void onPaymentSuccess(String razorpayPaymentID) {
//        try {
//            payNow(razorpayPaymentID);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        try {
//            //Funtions.showToast(this, "Payment failed: " + code + " " + response, Toast
//            // .LENGTH_SHORT).show();
//        } catch (Exception e) {
//            //Log.e(TAG, "Exception in onPaymentError", e);
//        }
//    }
//
//
//    public void payNow(final String payment_id) {
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.PY_NOW,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//                            ParseSuccessPayment(jsonObject);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                // NoInternet(listTicket.this);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("token", Const.TOKEN);
//
//                return header;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                params.put("user_id", prefs.getString("user_id", ""));
//                params.put("token", prefs.getString("token", ""));
//                params.put("order_id", order_id);
//                params.put("payment_id", payment_id);
//
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsPaymentDetails.this);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(stringRequest);
//
//
//    }
//
//    private void image() {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.qr_code,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("RES_IMAGE","cHECK" + response);
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String code = jsonObject.getString("code");
//
//                            if (code.equalsIgnoreCase("201")) {
//
//                            } else if (code.equalsIgnoreCase("200")) {
//                                qr_image = jsonObject.optString("qr_image");
//                                String imagePath =  qr_image;
//
//try {
//    Picasso.get().load(imagePath).into(qrcode);
//}catch (Exception e){
//
//}
//
//                                Log.v("RES_CHECKnew", "PARAMSnew : " + response);
//
//                            } else {
//                                if (jsonObject.has("message")) {
//                                    String message = jsonObject.getString("message");
//                                    //Toast.makeText(userpayment.this, "" + message, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Functions.showToast(BuyChipsPaymentDetails.this, "Something went wrong");
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("user_id", prefs.getString("user_id", ""));
//                params.put("qr_image", prefs.getString("qr_image", ""));
//                params.put("token", prefs.getString("token", ""));
//
//                Log.v("RES_CHECK", "PARAMS : " + params);
//
////                Functions.LOGE("LoginScreen", "params : " + params);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("token", Const.TOKEN);
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(stringRequest);
//
//    }
//
//    private void ParseSuccessPayment(JSONObject jsonObject) throws JSONException {
//
//
//        String code = jsonObject.getString("code");
//        String message = jsonObject.getString("message");
//
//        if (code.equals("200")) {
//            Functions.showToast(BuyChipsPaymentDetails.this, "" + message);
//            dialog_payment_success();
//        } else if (code.equals("404")) {
//            Functions.showToast(BuyChipsPaymentDetails.this, "" + message);
//        }
//
//    }
//
//    private void dialog_payment_success() {
//
//        new SmartDialogBuilder(BuyChipsPaymentDetails.this)
//                .setTitle("Your Payment has been done Successfully!")
//                .setSubTitle("Your Payment has been done Successfully!")
//                .setCancalable(false)
//
//                //.setTitleFont("Do you want to Logout?") //set title font
//                // .setSubTitleFont(subTitleFont) //set sub title font
//                .setNegativeButtonHide(true) //hide cancel button
//                .setPositiveButton("Ok", new SmartDialogClickListener() {
//                    @Override
//                    public void onClick(SmartDialog smartDialog) {
//                        smartDialog.dismiss();
//                        finish();
//                    }
//                }).setNegativeButton("Cancel", new SmartDialogClickListener() {
//                    @Override
//                    public void onClick(SmartDialog smartDialog) {
//                        // Funtions.showToast(context,"Cancel button Click");
//                        smartDialog.dismiss();
//
//                    }
//                }).build().show();
//
//    }
//
//    /**
//     * Shows a custom success dialog with payment details
//     * This is called when a payment is successful
//     */
//    private void showSuccessDialog() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Check if activity is still active
//                    if (!isFinishing()) {
//                        dialog_payment_success();
//                    }
//                } catch (Exception e) {
//                    Log.e("Payment", "Error showing success dialog", e);
//                }
//            }
//        });
//    }
//
//    private void SuccessBox() {
//        // Refresh user details to get updated wallet balance
//        CommonAPI.CALL_API_UserDetails(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//                // Show success dialog after refreshing user details
//                dialog_payment_success();
//
//                // Log the successful payment
//                Log.d("Payment", "Payment successful, wallet updated");
//            }
//        });
//    }
//
//    private void finalCallback() {
//        //   progressDialog.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.CHECK_UPI_STATUS_Crypto,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("CHECK_UPI_STATUS_Crypto", "onResponse: " + Const.CHECK_UPI_STATUS);
//                        Log.e("callback_place", "onResponse: " + response);
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//                            String code = jsonObject.getString("code");
//                            String message = jsonObject.getString("message");
//
//                            if (code.equals("200")) {
//                                //    progressDialog.dismiss();
//                                dialog_payment_success();
////                                finalCallbackNeo();
//                            } else {
//                                //    progressDialog.dismiss();
//                                //     Functions.showToast(BuyChipsDetails.this, "" + message);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            //   progressDialog.dismiss();
//
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                //  progressDialog.dismiss();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> header = new HashMap<>();
//                header.put("token", Const.TOKEN);
//                return header;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                params.put("param1", order_id);
//                params.put("status", "1");
//                params.put("token", prefs.getString("token", ""));
//                params.put("user_id", prefs.getString("user_id", ""));
//                params.put("amount", amount);
//                Log.d("data", "getParams1_check " + params);
//                return params;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(BuyChipsPaymentDetails.this);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.add(stringRequest);
//    }
//
//
//    /**
//     * Shows a custom success dialog with payment details from deep link
//     * This is called when a payment is successful via web payment
//     */
//    private void showDeepLinkSuccessDialog() {
//        // Get payment details from deep link or use stored values
//        Uri data = getIntent().getData();
//        String amount = "";
//        String transactionId = "";
//
//        if (data != null) {
//            amount = data.getQueryParameter("amount");
//            transactionId = data.getQueryParameter("transaction_id");
//        }
//
//        // If amount is not available from deep link, use the pay_amount
//        if (amount == null || amount.isEmpty()) {
//            amount = pay_amount != null ? pay_amount : "100";
//        }
//
//        // If transaction ID is not available from deep link, use a default value
//        if (transactionId == null || transactionId.isEmpty()) {
//            transactionId = "TXN" + System.currentTimeMillis();
//        }
//
//        // Create and show the custom success dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_payment_success, null);
//        builder.setView(dialogView);
//
//        TextView tvAmount = dialogView.findViewById(R.id.tvSuccessAmount);
//        TextView tvTransactionId = dialogView.findViewById(R.id.tvTransactionId);
//        Button btnDone = dialogView.findViewById(R.id.btnDone);
//
//        tvAmount.setText("₹" + amount);
//        tvTransactionId.setText("Transaction ID: " + transactionId);
//
//        final AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
//
//        btnDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//                // Refresh user details to update wallet balance
//                CommonAPI.CALL_API_UserDetails(context, new Callback() {
//                    @Override
//                    public void Responce(String resp, String type, Bundle bundle) {
//                        // Go back to homepage after refreshing user details
//                        Intent i = new Intent(BuyChipsPaymentDetails.this, Homepage.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(i);
//                    }
//                });
//            }
//        });
//
//        dialog.show();
//    }
//
//
//    public long generateID() {
//        Random rnd = new Random();
//        char[] digits = new char[11];
//        digits[0] = (char) (rnd.nextInt(9) + '1');
//        for (int i = 1; i < digits.length; i++) {
//            digits[i] = (char) (rnd.nextInt(10) + '0');
//        }
//        return Long.parseLong(new String(digits));
//    }
//
//    @Override
//    public void onTransactionCompleted(TransactionDetails transactionDetails) {
//        // Transaction Completed
//        Log.d("TransactionDetails", transactionDetails.toString());
//        //  statusView.setText(transactionDetails.toString());
//
//        switch (transactionDetails.getTransactionStatus()) {
//            case SUCCESS:
//                onTransactionSuccess();
//                break;
//            case FAILURE:
//                onTransactionFailed();
//                break;
//            case SUBMITTED:
//                onTransactionSubmitted();
//                break;
//        }
//    }
//
//    @Override
//    public void onTransactionCancelled() {
//        // Payment Cancelled by User
//        toast("Cancelled by user");
//
//    }
//
//    private void onTransactionSuccess() {
//        // Payment Success
//        toast("Success");
//        finalCallback();
//    }
//
//    private void onTransactionSubmitted() {
//        // Payment Pending
//        toast("Pending | Submitted");
//
//    }
//
//    private void onTransactionFailed() {
//        // Payment Failed
//        toast("Payment Failed");
//
//    }
//
//    private void toast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//
//    /**
//     * Generate QR code for the given wallet address
//     */
//    private void generateqrcode(String walletAddress) {
//        try {
//            Toast.makeText(this, "🔄 Generating QR code...", Toast.LENGTH_SHORT).show();
//
//            // Create larger, higher quality QR code
//            int qrSize = 600; // Increased size for better quality
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(walletAddress, BarcodeFormat.QR_CODE, qrSize, qrSize);
//            Bitmap bitmap = Bitmap.createBitmap(qrSize, qrSize, Bitmap.Config.ARGB_8888); // Better quality
//
//            // Generate QR code with better contrast
//            for (int x = 0; x < qrSize; x++) {
//                for (int y = 0; y < qrSize; y++) {
//                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                }
//            }
//
//            // Display QR code in the main ImageView
//            boolean qrDisplayed = false;
//
//            if (qrcode != null) {
//                qrcode.setImageBitmap(bitmap);
//                qrcode.setVisibility(View.VISIBLE);
//                qrDisplayed = true;
//              }
//
//            // Fallback to imgqrcode if qrcode is not available
//            if (!qrDisplayed && imgqrcode != null) {
//                imgqrcode.setImageBitmap(bitmap);
//                imgqrcode.setVisibility(View.VISIBLE);
//                qrDisplayed = true;
//             }
//
//            if (qrDisplayed) {
//                 Toast.makeText(this, "✅ High-Quality QR Code Generated!", Toast.LENGTH_SHORT).show();
//
//                // Show the QR container with USDT logo
//                if (qr_container != null) {
//                    qr_container.setVisibility(View.VISIBLE);
//                }
//                if (usdt_logo != null) {
//                    usdt_logo.setVisibility(View.VISIBLE);
//                }
//            } else {
//                  Toast.makeText(this, "❌ QR ImageView not found", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception e) {
//             e.printStackTrace();
//            Toast.makeText(this, "❌ Error generating QR code: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void openWhatsAppSupport() {
//        try {
//            // WhatsApp support number (replace with your actual support number)
//            String supportNumber = "+1234567890"; // Replace with actual support WhatsApp number
//            String message = "Hi, I need help with my payment. Please assist me.";
//
//            // Create WhatsApp intent
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String url = "https://api.whatsapp.com/send?phone=" + supportNumber + "&text=" + Uri.encode(message);
//            intent.setData(Uri.parse(url));
//
//            // Check if WhatsApp is installed
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//              } else {
//                // WhatsApp not installed, open in browser
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);
//               }
//        } catch (Exception e) {
//            Toast.makeText(this, "Unable to open WhatsApp support", Toast.LENGTH_SHORT).show();
//
//            // Fallback: show support contact info
//            showSupportContactInfo();
//        }
//    }
//
//    private void showSupportContactInfo() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Contact Support")
//               .setMessage("For payment support, please contact us:\n\n" +
//                          "WhatsApp: +1234567890\n" +
//                          "Email: gaming27.com@gmail.com\n" +
//                          "Phone: +1234567890")
//               .setPositiveButton("OK", null)
//               .show();
//    }
//
//    private void checkWalletStatus() {
//
//        // Check if already processing to prevent double calls
//        if (progressDialog != null && progressDialog.isShowing()) {
//             return;
//        }
//
//        // Show full screen progress dialog to freeze the screen
//        progressDialog.setMessage("Checking payment status...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // Show progress bar where "Pending" text is
//        if (txtStatus != null) {
//            txtStatus.setText("Checking...");
//            txtStatus.setVisibility(View.VISIBLE);
//           }
//
//        // Get wallet address and private key from user data
//         CommonAPI.CALL_API_UserDetails(context, new Callback() {
//            @Override
//            public void Responce(String resp, String type, Bundle bundle) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(resp);
//                    String code = jsonObject.optString("code", "");
//                     if (code.equalsIgnoreCase("200")) {
//                        JSONArray userDataArray = jsonObject.optJSONArray("user_data");
//
//                        if (userDataArray != null && userDataArray.length() > 0) {
//                            JSONObject userData = userDataArray.getJSONObject(0);
//                            String walletAddress = userData.optString("pay_address", "");
//                            String walletPrivateKey = userData.optString("pay_privatekey", "");
//
//
//                            if (!walletAddress.isEmpty() && !walletPrivateKey.isEmpty()) {
//                                 callMonitorWalletAPI(walletAddress, walletPrivateKey);
//                            } else {
//                                 updateStatusText("Error: Wallet not found");
//                            }
//                        } else {
//                             updateStatusText("Error: User data not found");
//                        }
//                    } else {
//                          updateStatusText("Error: Failed to get user data");
//                    }
//                } catch (Exception e) {
//                       e.printStackTrace();
//                    updateStatusText("Error: Failed to check status");
//                }
//            }
//        });
//    }
//
//    private String getCurrentRechargeOption() {
//
//        try {
//            if (currentRechargeOption != null && !currentRechargeOption.isEmpty() && !currentRechargeOption.equals("null")) {
//                  return currentRechargeOption;
//            }
//              return "USDT"; // Default to USDT
//        } catch (Exception e) {
//               return "USDT"; // Default to USDT on error
//        }
//    }
//
//    private void callMonitorWalletAPI(String walletAddress, String walletPrivateKey) {
//         // Get rechargeOption from user data to determine which API to use
//        String rechargeOption = getCurrentRechargeOption();
//        String url;
//        if ("RBM".equalsIgnoreCase(rechargeOption)) {
//            url = "https://socket.gaming27.com/api/rbmMonitorWallet";
//        } else if ("USDT".equalsIgnoreCase(rechargeOption)) {
//            url = "https://socket.gaming27.com/api/usdtMonitorWallet";
//
//        } else {
//              updateStatusText("Error: Invalid wallet option");
//            return;
//        }
//
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("walletAddress", walletAddress);
//            jsonBody.put("walletPrivateKey", walletPrivateKey);
//             } catch (JSONException e) {
//            e.printStackTrace();
//            updateStatusText("Error: Failed to create request");
//            return;
//        }
//
//
//        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                     parseMonitorWalletResponse(response.toString());
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    if (error.networkResponse != null) {
//                          }
//
//                    // Handle different types of errors with user-friendly messages
//                    String errorMessage;
//                    if (error instanceof TimeoutError) {
//                        errorMessage = "⏳ Request timed out. Payment processing may take longer. Please try again in a few minutes.";
//
//                    } else if (error instanceof NoConnectionError) {
//                        errorMessage = "🌐 No internet connection. Please check your network and try again.";
//
//                    } else if (error instanceof NetworkError) {
//                        errorMessage = "🌐 Network error. Please check your connection and try again.";
//
//                    } else if (error instanceof ServerError) {
//                        errorMessage = "🔧 Server temporarily unavailable. Please try again in a few minutes.";
//
//                    } else {
//                        errorMessage = "❌ Unable to check payment status. Please try again.";
//
//                    }
//
//                    updateStatusText(errorMessage);
//                }
//            }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        // Set longer timeout for monitor wallet API (3 minutes)
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
//            180000, // 3 minutes timeout
//            0, // No retries to avoid duplicate calls
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonRequest);
//    }
//
//    private void parseMonitorWalletResponse(String response) {
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            boolean status = jsonObject.optBoolean("status", false);
//            String message = jsonObject.optString("message", "");
//
//            if (status) {
//                JSONObject result = jsonObject.optJSONObject("result");
//                   if (result != null) {
//                    boolean found = result.optBoolean("found", false);
//                    String amount = result.optString("amount", "");
//                    String currency = result.optString("currency", "");
//                    String resultMessage = result.optString("message", "");
//
//                    if (found) {
//                        String successText = "✅ Payment Found: " + amount + " " + currency;
//                        updateStatusText(successText);
//                    } else {
//                        // Check if it's a gas/nonce error
//                        if (resultMessage.contains("gas") || resultMessage.contains("nonce")) {
//                            String gasErrorText = "⏳ Payment detected but processing delayed. Please try again in a few minutes.";
//                            updateStatusText(gasErrorText);
//                        } else {
//                            String noPaymentText = "❌ No payment found";
//                            updateStatusText(noPaymentText);
//                        }
//                    }
//                } else {
//                    String errorText = "❌ " + message;
//                    updateStatusText(errorText);
//                }
//            } else {
//                String statusErrorText = "❌ " + message;
//                updateStatusText(statusErrorText);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatusText("Error: Failed to parse response");
//        }
//    }
//
//    private void updateStatusText(String text) {
//        // Dismiss progress dialog to unfreeze the screen
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//
//        if (txtStatus != null) {
//            txtStatus.setText(text);
//            txtStatus.setVisibility(View.VISIBLE);
//        } else {
//        }
//    }
//
//    private void openDepositHistory() {
//        try {
//            DialogDepositHistory dialogDepositHistory = new DialogDepositHistory(this);
//            dialogDepositHistory.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Unable to open history. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 🎯 Auto-focus amount field after wallet selection to continue payment flow seamlessly
//     */
//    private void autoFocusAmountFieldAfterWalletSelection() {
//        try {
//            Log.d("WalletSelection", "Auto-focusing amount field after wallet selection");
//
//            // Post a delayed runnable to ensure UI is updated first
//            new android.os.Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        // Find amount input field and focus it
//                        EditText amountField = findViewById(R.id.edt_amount);
//                        if (amountField != null) {
//                            amountField.requestFocus();
//
//                            // Show soft keyboard
//                            android.view.inputmethod.InputMethodManager imm =
//                                (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            if (imm != null) {
//                                imm.showSoftInput(amountField, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
//                            }
//
//                            Log.d("WalletSelection", "Amount field focused successfully");
//
//                            // Show a helpful toast
//                            Toast.makeText(BuyChipsPaymentDetails.this,
//                                "✅ Wallet ready! Enter amount to continue payment",
//                                Toast.LENGTH_LONG).show();
//                        } else {
//                            Log.e("WalletSelection", "Amount field not found");
//                        }
//                    } catch (Exception e) {
//                        Log.e("WalletSelection", "Error focusing amount field: " + e.getMessage());
//                    }
//                }
//            }, 500); // 500ms delay to ensure UI is ready
//
//        } catch (Exception e) {
//            Log.e("WalletSelection", "Error in autoFocusAmountFieldAfterWalletSelection: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 🎯 Generate wallet API call specifically after wallet selection
//     */
//    private void callGenerateWalletAPIAfterWalletSelection() {
//        Log.d("WalletSelection", "Calling GENERATE_WALLET API after wallet selection");
//
//        // Call the existing generate wallet method
//        callGenerateWalletAPI();
//
//        // Add a callback to auto-focus after wallet generation
//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                autoFocusAmountFieldAfterWalletSelection();
//            }
//        }, 2000); // 2 second delay to allow wallet generation to complete
//    }
//
//    /**
//     * Update both header text and QR instruction text based on the current payment type (USDT/RBM)
//     */
//    private void updateQrInstructionText() {
//        updatePaymentTexts();
//    }

    /**
     * Update all payment-related texts based on the current payment type (USDT/RBM)
     */
//    private void updatePaymentTexts() {
//        try {
//            String paymentType = getCurrentRechargeOption();
//            Log.d("PAYMENT_TEXTS", "Updating payment texts for type: " + paymentType);
//
//            // Update header text: "Add [USDT/RBM] to Your Wallet" for crypto, keep "Add Funds to Your Wallet" for INR
//            if (txtChipsdetails != null) {
//                String headerText;
//                if ("USDT".equalsIgnoreCase(paymentType) || "RBM".equalsIgnoreCase(paymentType)) {
//                    headerText = "Add " + paymentType + " to Your Wallet";
//                } else {
//                    headerText = "Add Funds to Your Wallet"; // Default for INR or unknown types
//                }
//                txtChipsdetails.setText(headerText);
//                Log.d("PAYMENT_TEXTS", "Updated header text to: " + headerText);
//            } else {
//                Log.e("PAYMENT_TEXTS", "txtChipsdetails TextView is null");
//            }
//
//            // Update QR instruction text: "Scan this QR code with your [USDT/RBM] wallet"
//            if (txtQrInstruction != null) {
//                String instructionText = "Scan this QR code with your " + paymentType + " wallet";
//                txtQrInstruction.setText(instructionText);
//                Log.d("PAYMENT_TEXTS", "Updated QR instruction text to: " + instructionText);
//            } else {
//                Log.e("PAYMENT_TEXTS", "txtQrInstruction TextView is null");
//            }
//        } catch (Exception e) {
//            Log.e("PAYMENT_TEXTS", "Error updating payment texts: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        // Check if we're coming from homepage (Add Cash button)
//        String fromHomepage = getIntent().getStringExtra("homepage");
//        if (fromHomepage != null && fromHomepage.equals("homepage")) {
//            // Coming from Add Cash button, go to homepage
//            startActivity(new Intent(this, Homepage.class));
//            finish();
//        } else {
//            // Coming from game, go back to previous activity
//            super.onBackPressed();
//        }
//    }

}
