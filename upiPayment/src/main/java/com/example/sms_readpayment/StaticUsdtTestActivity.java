package com.example.sms_readpayment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Test Activity for Static USDT Payment System
 * This demonstrates how to use the USDT payment with static wallet data
 */
public class StaticUsdtTestActivity extends AppCompatActivity {

    private static final String TAG = "STATIC_USDT_TEST";
    
    private EditText etAmount, etUserId;
    private Button btnStartPayment;
    private TextView tvInstructions, tvWalletInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_usdt_test);
        
        Log.d(TAG, "Static USDT Test Activity started");
        
        initializeViews();
        setupClickListeners();
        displayStaticWalletInfo();
    }

    private void initializeViews() {
        etAmount = findViewById(R.id.et_amount);
        etUserId = findViewById(R.id.et_user_id);
        btnStartPayment = findViewById(R.id.btn_start_payment);
        tvInstructions = findViewById(R.id.tv_instructions);
        tvWalletInfo = findViewById(R.id.tv_wallet_info);
        
        // Set default values for testing
        etAmount.setText("10.00");
        etUserId.setText("test_user_123");
        
        // Set instructions
        tvInstructions.setText(
            "This is a test of the USDT payment system using static wallet data.\n\n" +
            "Steps:\n" +
            "1. Enter amount and user ID\n" +
            "2. Click 'Start USDT Payment'\n" +
            "3. You'll see the QR code and wallet address\n" +
            "4. The system will simulate payment monitoring\n\n" +
            "Note: This uses static data for testing purposes."
        );
    }

    private void setupClickListeners() {
        btnStartPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUsdtPayment();
            }
        });
    }

    private void displayStaticWalletInfo() {
        String walletInfo = "Static Wallet Data:\n" +
                "Address: 0xd13e2620c159394477881d9828049d5e5932b88d\n" +
                "Network: BEP20 (Binance Smart Chain)\n" +
                "Currency: USDT";
        
        tvWalletInfo.setText(walletInfo);
        Log.d(TAG, "Displaying static wallet info: " + walletInfo);
    }

    private void startUsdtPayment() {
        String amount = etAmount.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();
        
        // Validate inputs
        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter user ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Starting USDT payment with amount: " + amount + ", user: " + userId);
        
        // Start the InitiatePayment activity with USDT parameters
        Intent intent = new Intent(this, InitiatePayment.class);
        intent.putExtra("amount", amount);
        intent.putExtra("user_id", userId);
        intent.putExtra("payment_type", "USDT");
        intent.putExtra("test_mode", true);
        
        Toast.makeText(this, "Starting USDT payment for $" + amount, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed - ready for testing");
    }
}
