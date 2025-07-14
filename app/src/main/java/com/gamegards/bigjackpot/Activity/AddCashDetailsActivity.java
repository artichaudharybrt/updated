package com.gamegards.bigjackpot.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.InrDepositHelper;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.Helper.BankDetailsHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class AddCashDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AddCashDetails";
    
    // UI Components
    private ImageView imgBack;
    private TextView txtAccountNumber, txtIfscCode, txtAccountName, txtUpiId;
    private ImageView imgCopyAccount, imgCopyIfsc, imgCopyName, imgCopyUpi;
    private ImageView qrCodeImageView;
    private EditText edtAmount, edtUtrNumber;
    private Button btnSubmit, btnGenerateUtr;
    
    // Account Details (As provided by user)
    private String accountNumber = "937821783781287";
    private String ifscCode = "ICIB7878650";
    private String accountName = "Lucky Singh";
    private String upiId = "8708737699@sbi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash_details);
        
        Log.d(TAG, "AddCashDetailsActivity created");
        
        initViews();
        setupClickListeners();
        populateAccountDetails();
        generateQRCode();
    }

    private void initViews() {
        // Initialize all views
        imgBack = findViewById(R.id.imgback);

        txtAccountNumber = findViewById(R.id.txt_account_number);
        txtIfscCode = findViewById(R.id.txt_ifsc_code);
        txtAccountName = findViewById(R.id.txt_account_name);
        txtUpiId = findViewById(R.id.txt_upi_id);

        imgCopyAccount = findViewById(R.id.img_copy_account);
        imgCopyIfsc = findViewById(R.id.img_copy_ifsc);
        imgCopyName = findViewById(R.id.img_copy_name);
        imgCopyUpi = findViewById(R.id.img_copy_upi);

        qrCodeImageView = findViewById(R.id.qrcode);

        edtAmount = findViewById(R.id.edt_amount);
        edtUtrNumber = findViewById(R.id.edt_utr_number);
        btnSubmit = findViewById(R.id.btn_submit);

        Log.d(TAG, "Views initialized successfully");
    }

    private void setupClickListeners() {
        imgBack.setOnClickListener(this);
        imgCopyAccount.setOnClickListener(this);
        imgCopyIfsc.setOnClickListener(this);
        imgCopyName.setOnClickListener(this);
        imgCopyUpi.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        
        Log.d(TAG, "Click listeners setup completed");
    }

    private void populateAccountDetails() {
        // Get bank details from SharedPreferences (already stored from admin settings)
        getBankDetailsFromPreferences();

        Log.d(TAG, "Account details populated from stored preferences");
    }

    private void getBankDetailsFromPreferences() {
        try {
            // 🎯 Use BankDetailsHelper to get admin bank details
            accountNumber = BankDetailsHelper.getBankAccountNumber(this);
            ifscCode = BankDetailsHelper.getBankIFSCCode(this);
            accountName = BankDetailsHelper.getBankAccountName(this);
            String bankName = BankDetailsHelper.getBankName(this);
            String bankBranch = BankDetailsHelper.getBankBranch(this);

            // Get UPI ID from SharePref (same way it's accessed in other activities)
            upiId = SharePref.getInstance().getString(SharePref.UPI_ID, "");

            // Check if bank details are empty and use fallback values for testing
            if (accountNumber.isEmpty()) {
                accountNumber = "-";
             }
            if (ifscCode.isEmpty()) {
                ifscCode = "-";
             }
            if (accountName.isEmpty()) {
                accountName = "-";
               }
            if (upiId.isEmpty()) {
                upiId = "-";
              }
            // Update UI
            updateAccountDetailsUI();

        } catch (Exception e) {
               e.printStackTrace();
        }
    }

    private void updateAccountDetailsUI() {
        txtAccountNumber.setText(accountNumber);
        txtIfscCode.setText(ifscCode);
        txtAccountName.setText(accountName);
        txtUpiId.setText(upiId);
    }

    private void generateQRCode() {
        try {
            // Create UPI payment string for QR code
            String upiPaymentString = "upi://pay?pa=" + upiId + "&pn=" + accountName + "&cu=INR";
            Log.d(TAG, "Generating QR code for: " + upiPaymentString);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(upiPaymentString, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            // Generate QR code bitmap
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            if (qrCodeImageView != null) {
                qrCodeImageView.setImageBitmap(bitmap);
            } else {
            }
        } catch (WriterException e) {
            e.printStackTrace();

            // Set a placeholder if QR generation fails
            if (qrCodeImageView != null) {
                qrCodeImageView.setImageResource(R.drawable.rbm);
            }
        } catch (Exception e) {
            e.printStackTrace();

            // Set a placeholder if QR generation fails
            if (qrCodeImageView != null) {
                qrCodeImageView.setImageResource(R.drawable.rbm);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.imgback) {
            onBackPressed();
            
        } else if (id == R.id.img_copy_account) {
            copyToClipboard("Account Number", accountNumber);
            
        } else if (id == R.id.img_copy_ifsc) {
            copyToClipboard("IFSC Code", ifscCode);
            
        } else if (id == R.id.img_copy_name) {
            copyToClipboard("Account Name", accountName);
            
        } else if (id == R.id.img_copy_upi) {
            copyToClipboard("UPI ID", upiId);

        } else if (id == R.id.btn_submit) {
            handleSubmitTransaction();

        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        showToast(label + " copied to clipboard");
    }

    private void generateAndSetUTR() {
         showToast("Please enter your actual UTR number from your bank transaction");
    }

    private void handleSubmitTransaction() {
        String amount = edtAmount.getText().toString().trim();
        String utrNumber = edtUtrNumber.getText().toString().trim();
        // Validate inputs
        if (TextUtils.isEmpty(amount)) {
            showToast("Please enter the amount");
            edtAmount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(utrNumber)) {
            showToast("Please enter the UTR number");
            edtUtrNumber.requestFocus();
            return;
        }

        // Validate amount
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                showToast("Please enter a valid amount");
                edtAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Please enter a valid amount");
            edtAmount.requestFocus();
            return;
        }

        // Validate UTR number (basic validation)
        if (utrNumber.length() < 8) {
            showToast("Please enter a valid UTR number");
            edtUtrNumber.requestFocus();
            return;
        }

        // Submit transaction
        submitTransaction(amount, utrNumber);
    }

    private void submitTransaction(String amount, String utrNumber) {
        // Create INR deposit helper
        InrDepositHelper inrDepositHelper = new InrDepositHelper(this);

        // Submit INR deposit with provided UTR
        inrDepositHelper.submitInrDeposit(amount, utrNumber, new InrDepositHelper.InrDepositCallback() {
            @Override
            public void onSuccess(String message) {
                showToast("Deposit submitted successfully! " + message);

                // Clear the form on success
                edtAmount.setText("");
                edtUtrNumber.setText("");

                // Optionally, finish the activity after successful submission
                // finish();
            }

            @Override
            public void onError(String error) {
                  showToast("Deposit failed: " + error);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();
    }
}
