package com.example.sms_readpayment;

/**
 * USDT Payment Configuration
 * Update these values to match your own_pay API setup
 */
public class UsdtConfig {

    // ========================================
    // IMPORTANT: UPDATE THESE VALUES
    // ========================================

    /**
     * Your own_pay API base URL
     * Examples:
     * - Local development: "http://localhost:3000/api/"
     * - Production: "https://your-domain.com/api/"
     * - Staging: "https://staging.your-domain.com/api/"
     */
    public static final String OWN_PAY_API_BASE_URL = "https://your-backend-domain.com/api/";

    /**
     * Default network for USDT transactions
     * Options: "BEP20" (Binance Smart Chain) or "TRC20" (TRON)
     */
    public static final String DEFAULT_NETWORK = "BEP20";

    /**
     * API timeout in milliseconds
     */
    public static final int API_TIMEOUT = 30000; // 30 seconds

    // ========================================
    // API ENDPOINTS (Auto-generated)
    // ========================================

    public static final String GENERATE_WALLET_ENDPOINT = OWN_PAY_API_BASE_URL + "generate-wallet";
    public static final String SAVE_WALLET_ENDPOINT = OWN_PAY_API_BASE_URL + "save-wallet";
    public static final String START_MONITORING_ENDPOINT = OWN_PAY_API_BASE_URL + "start-monitoring";
    public static final String CHECK_PAYMENT_STATUS_ENDPOINT = OWN_PAY_API_BASE_URL + "check-payment-status";
    public static final String WITHDRAW_ENDPOINT = OWN_PAY_API_BASE_URL + "withdraw";

    // ========================================
    // LOGGING CONFIGURATION
    // ========================================

    /**
     * Enable/disable detailed logging
     */
    public static final boolean ENABLE_DETAILED_LOGGING = true;

    /**
     * Log tag prefix for all USDT-related logs
     */
    public static final String LOG_TAG_PREFIX = "USDT_";

    // ========================================
    // VALIDATION SETTINGS
    // ========================================

    /**
     * Minimum USDT amount for transactions
     */
    public static final double MIN_TRANSACTION_AMOUNT = 0.01;

    /**
     * Maximum USDT amount for transactions
     */
    public static final double MAX_TRANSACTION_AMOUNT = 10000.0;

    /**
     * Wallet address validation pattern (Ethereum-style addresses)
     */
    public static final String WALLET_ADDRESS_PATTERN = "^0x[a-fA-F0-9]{40}$";

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Check if the configuration is valid
     */
    public static boolean isConfigurationValid() {
        return !OWN_PAY_API_BASE_URL.equals("http://localhost:3000/api/") ||
               OWN_PAY_API_BASE_URL.contains("your-domain.com");
    }

    /**
     * Get configuration status message
     */
    public static String getConfigurationStatus() {
        if (OWN_PAY_API_BASE_URL.contains("localhost")) {
            return "⚠️ Using localhost - Update for production";
        } else if (OWN_PAY_API_BASE_URL.contains("your-domain.com")) {
            return "❌ Please update API URL";
        } else {
            return "✅ Configuration looks good";
        }
    }

    /**
     * Validate wallet address format
     */
    public static boolean isValidWalletAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return address.matches(WALLET_ADDRESS_PATTERN);
    }

    /**
     * Validate transaction amount
     */
    public static boolean isValidAmount(double amount) {
        return amount >= MIN_TRANSACTION_AMOUNT && amount <= MAX_TRANSACTION_AMOUNT;
    }

    /**
     * Validate transaction amount from string
     */
    public static boolean isValidAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            return isValidAmount(amount);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ========================================
    // SETUP INSTRUCTIONS
    // ========================================

    /**
     * Get setup instructions for developers
     */
    public static String getSetupInstructions() {
        return "USDT Payment Setup Instructions:\n\n" +
               "1. Update OWN_PAY_API_BASE_URL to your actual API URL\n" +
               "2. Ensure your backend API endpoints are running:\n" +
               "   - " + GENERATE_WALLET_ENDPOINT + "\n" +
               "   - " + WITHDRAW_ENDPOINT + "\n" +
               "   - " + START_MONITORING_ENDPOINT + "\n" +
               "3. Test with small amounts first\n" +
               "4. Monitor logs with: adb logcat | grep 'USDT_'\n\n" +
               "Current Status: " + getConfigurationStatus();
    }
}
