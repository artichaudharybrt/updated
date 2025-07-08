package com.example.sms_readpayment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Example activity showing how to use USDT deposit and withdrawal system
 */
public class UsdtPaymentExample extends AppCompatActivity {

    private EditText etAmount;
    private EditText etUserId;
    private EditText etWithdrawAddress;
    private Button btnDeposit;
    private Button btnDepositAuto;
    private Button btnWithdraw;

    // Simple API-based system - just hit your own_pay API
    // Backend handles all contract interactions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usdt_payment_example);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etAmount = findViewById(R.id.et_amount);
        etUserId = findViewById(R.id.et_user_id);
        etWithdrawAddress = findViewById(R.id.et_withdraw_address);
        btnDeposit = findViewById(R.id.btn_deposit);
        btnDepositAuto = findViewById(R.id.btn_deposit_auto);
        btnWithdraw = findViewById(R.id.btn_withdraw);
    }

    private void setupClickListeners() {
        // Manual USDT deposit (user needs to click deposit button)
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    InitiatePayment.startUsdtDeposit(
                            UsdtPaymentExample.this,
                            etAmount.getText().toString(),
                            etUserId.getText().toString(),
                            "User Name",
                            false // Don't auto-start
                    );
                }
            }
        });

        // Auto USDT deposit (automatically opens deposit interface)
        btnDepositAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    InitiatePayment.startUsdtDepositAuto(
                            UsdtPaymentExample.this,
                            etAmount.getText().toString(),
                            etUserId.getText().toString(),
                            "User Name"
                    );
                }
            }
        });

        // USDT withdrawal
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateWithdrawalInputs()) {
                    InitiatePayment.startUsdtWithdrawal(
                            UsdtPaymentExample.this,
                            etAmount.getText().toString(),
                            etUserId.getText().toString(),
                            "User Name"
                    );
                }
            }
        });
    }

    private boolean validateInputs() {
        String amount = etAmount.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();

        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter user ID", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateWithdrawalInputs() {
        if (!validateInputs()) {
            return false;
        }

        String withdrawAddress = etWithdrawAddress.getText().toString().trim();
        if (withdrawAddress.isEmpty()) {
            Toast.makeText(this, "Please enter withdrawal address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Basic wallet address validation
        if (!withdrawAddress.startsWith("0x") || withdrawAddress.length() != 42) {
            Toast.makeText(this, "Please enter valid wallet address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Helper method to start USDT deposit from anywhere in your app (API-based)
     */
    public static void startQuickUsdtDeposit(Context context, String amount, String userId) {
        InitiatePayment.startUsdtDepositAuto(
                context,
                amount,
                userId,
                "User"
        );
    }

    /**
     * Helper method to start USDT withdrawal from anywhere in your app (API-based)
     */
    public static void startQuickUsdtWithdrawal(Context context, String amount, String userId) {
        InitiatePayment.startUsdtWithdrawal(
                context,
                amount,
                userId,
                "User"
        );
    }

    /**
     * Open USDT payment system directly (API-based)
     */
    public static void openUsdtPaymentSystem(Context context) {
        InitiatePayment.openUsdtPaymentSystem(context);
    }
}
