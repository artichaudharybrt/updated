package com.example.sms_readpayment.Interface;

/**
 * Callback interface for payment operations
 */
public interface PaymentCallback {
    /**
     * Called when payment is successful
     * @param transactionId Transaction ID or reference number
     * @param message Success message
     */
    void onPaymentSuccess(String transactionId, String message);

    /**
     * Called when payment fails
     * @param message Error message
     */
    void onPaymentFailed(String message);

    /**
     * Called when payment is pending
     * @param message Status message
     */
    void onPaymentPending(String message);
}
