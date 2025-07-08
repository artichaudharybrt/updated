package com.example.sms_readpayment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to test and verify USDT configuration
 */
public class ConfigTestActivity extends AppCompatActivity {

    private static final String TAG = "CONFIG_TEST";
    
    private TextView tvConfigStatus;
    private TextView tvApiUrl;
    private TextView tvEndpoints;
    private Button btnTestConfig;
    private Button btnTestDeposit;
    private Button btnTestWithdrawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=== CONFIG TEST ACTIVITY STARTED ===");
        
        setContentView(R.layout.activity_config_test);
        
        initViews();
        displayConfiguration();
        setupClickListeners();
    }

    private void initViews() {
        tvConfigStatus = findViewById(R.id.tv_config_status);
        tvApiUrl = findViewById(R.id.tv_api_url);
        tvEndpoints = findViewById(R.id.tv_endpoints);
        btnTestConfig = findViewById(R.id.btn_test_config);
        btnTestDeposit = findViewById(R.id.btn_test_deposit);
        btnTestWithdrawal = findViewById(R.id.btn_test_withdrawal);
    }

    private void displayConfiguration() {
        Log.d(TAG, "Displaying current configuration...");
        
        // Display configuration status
        String status = UsdtConfig.getConfigurationStatus();
        tvConfigStatus.setText("Status: " + status);
        Log.d(TAG, "Configuration Status: " + status);
        
        // Display API URL
        String apiUrl = "API URL: " + UsdtConfig.OWN_PAY_API_BASE_URL;
        tvApiUrl.setText(apiUrl);
        Log.d(TAG, apiUrl);
        
        // Display endpoints
        String endpoints = "Endpoints:\n" +
                "• Generate Wallet: " + UsdtConfig.GENERATE_WALLET_ENDPOINT + "\n" +
                "• Withdraw: " + UsdtConfig.WITHDRAW_ENDPOINT + "\n" +
                "• Start Monitoring: " + UsdtConfig.START_MONITORING_ENDPOINT + "\n" +
                "• Check Status: " + UsdtConfig.CHECK_PAYMENT_STATUS_ENDPOINT;
        tvEndpoints.setText(endpoints);
        
        Log.d(TAG, "=== CONFIGURATION DETAILS ===");
        Log.d(TAG, "Base URL: " + UsdtConfig.OWN_PAY_API_BASE_URL);
        Log.d(TAG, "Default Network: " + UsdtConfig.DEFAULT_NETWORK);
        Log.d(TAG, "API Timeout: " + UsdtConfig.API_TIMEOUT + "ms");
        Log.d(TAG, "Min Amount: " + UsdtConfig.MIN_TRANSACTION_AMOUNT);
        Log.d(TAG, "Max Amount: " + UsdtConfig.MAX_TRANSACTION_AMOUNT);
    }

    private void setupClickListeners() {
        btnTestConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConfiguration();
            }
        });

        btnTestDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDeposit();
            }
        });

        btnTestWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWithdrawal();
            }
        });
    }

    private void testConfiguration() {
        Log.d(TAG, "=== TESTING CONFIGURATION ===");
        
        // Test API URL
        if (UsdtConfig.OWN_PAY_API_BASE_URL.contains("localhost")) {
            Log.w(TAG, "⚠️ Using localhost - OK for development");
            Toast.makeText(this, "⚠️ Using localhost - OK for development", Toast.LENGTH_LONG).show();
        } else if (UsdtConfig.OWN_PAY_API_BASE_URL.contains("your-domain.com")) {
            Log.e(TAG, "❌ Please update API URL in UsdtConfig.java");
            Toast.makeText(this, "❌ Please update API URL in UsdtConfig.java", Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.d(TAG, "✅ API URL looks good");
            Toast.makeText(this, "✅ API URL looks good", Toast.LENGTH_SHORT).show();
        }
        
        // Test validation methods
        testValidation();
        
        // Display setup instructions
        Log.d(TAG, UsdtConfig.getSetupInstructions());
        
        Toast.makeText(this, "Configuration test completed - Check logs", Toast.LENGTH_SHORT).show();
    }

    private void testValidation() {
        Log.d(TAG, "=== TESTING VALIDATION METHODS ===");
        
        // Test wallet address validation
        String validAddress = "0x742d35Cc6634C0532925a3b8D4C9db96590645d7";
        String invalidAddress = "invalid_address";
        
        Log.d(TAG, "Testing wallet address validation:");
        Log.d(TAG, "Valid address (" + validAddress + "): " + UsdtConfig.isValidWalletAddress(validAddress));
        Log.d(TAG, "Invalid address (" + invalidAddress + "): " + UsdtConfig.isValidWalletAddress(invalidAddress));
        
        // Test amount validation
        Log.d(TAG, "Testing amount validation:");
        Log.d(TAG, "Valid amount (10.00): " + UsdtConfig.isValidAmount("10.00"));
        Log.d(TAG, "Invalid amount (0): " + UsdtConfig.isValidAmount("0"));
        Log.d(TAG, "Invalid amount (abc): " + UsdtConfig.isValidAmount("abc"));
        Log.d(TAG, "Invalid amount (99999): " + UsdtConfig.isValidAmount("99999"));
    }

    private void testDeposit() {
        Log.d(TAG, "=== TESTING DEPOSIT FLOW ===");
        Toast.makeText(this, "Testing Deposit - Check logs for details", Toast.LENGTH_SHORT).show();
        
        // Test with valid parameters
        InitiatePayment.quickUsdtDeposit(this, "1.00", "config_test_user");
    }

    private void testWithdrawal() {
        Log.d(TAG, "=== TESTING WITHDRAWAL FLOW ===");
        Toast.makeText(this, "Testing Withdrawal - Check logs for details", Toast.LENGTH_SHORT).show();
        
        // Test with valid parameters
        InitiatePayment.quickUsdtWithdrawal(this, "0.50", "config_test_user");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Config test activity resumed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== CONFIG TEST ACTIVITY DESTROYED ===");
    }
}
