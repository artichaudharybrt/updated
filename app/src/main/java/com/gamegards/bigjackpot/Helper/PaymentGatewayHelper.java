package com.gamegards.bigjackpot.Helper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamegards.bigjackpot.Activity.AddCashDetailsActivity;
import com.gamegards.bigjackpot.Activity.Homepage;
import com.gamegards.bigjackpot.Activity.BuyChipsPaymentDetails;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;

/**
 * Helper class for managing payment gateway options based on admin settings
 * Handles INR and USDT payment gateway configurations
 */
public class PaymentGatewayHelper {

    /**
     * Get INR payment gateway status from admin settings
     * @param context Application context
     * @return "1" if enabled, "0" if disabled
     */
    public static String getInrPaymentGateway(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("inr_payment_gateway", "0");
    }

    /**
     * Get Crypto payment gateway status from admin settings
     * @param context Application context
     * @return "1" if enabled, "0" if disabled
     */
    public static String getCryptoPaymentGateway(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("crypto_payment_gateway", "0");
    }

    /**
     * Check if INR payment is enabled
     * @param context Application context
     * @return true if INR payment is enabled
     */
    public static boolean isInrPaymentEnabled(Context context) {
        return getInrPaymentGateway(context).equals("1");
    }

    /**
     * Check if Crypto payment is enabled
     * @param context Application context
     * @return true if Crypto payment is enabled
     */
    public static boolean isCryptoPaymentEnabled(Context context) {
        return getCryptoPaymentGateway(context).equals("1");
    }

    /**
     * Main method to show payment options based on admin settings
     * @param context Application context
     */
    public static void showPaymentOptions(Context context) {
        boolean inrEnabled = isInrPaymentEnabled(context);
        boolean cryptoEnabled = isCryptoPaymentEnabled(context);

        if (inrEnabled && cryptoEnabled) {
            // Both enabled - Show selection popup
            showPaymentSelectionDialog(context);
        } else if (inrEnabled) {
            // Only INR enabled - Show INR page directly
            openInrPaymentPage(context);
        } else if (cryptoEnabled) {
            // Only Crypto enabled - Show USDT page directly
            openUsdtPaymentPage(context);
        } else {
            // Neither enabled - Show "No wallet found" dialog
            showNoWalletFoundDialog(context);
        }
    }

    /**
     * Show payment selection dialog when both INR and Crypto are enabled
     * @param context Application context
     */
    private static void showPaymentSelectionDialog(Context context) {
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_payment_selection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        // 🎯 Center dialog and set compact size
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(800, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(android.view.Gravity.CENTER);
        }

        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        Button btnInr = dialog.findViewById(R.id.btn_inr_payment);
        Button btnUsdt = dialog.findViewById(R.id.btn_usdt_payment);
        View imgClose = dialog.findViewById(R.id.img_close);

        txtTitle.setText("Choose Payment Method");

        // INR Payment Button
        btnInr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openInrPaymentPage(context);
            }
        });

        // USDT Payment Button
        btnUsdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openUsdtPaymentPage(context);
            }
        });

        // Close Button
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    /**
     * Open INR payment page
     * @param context Application context
     */
    private static void openInrPaymentPage(Context context) {
        Intent intent = new Intent(context, AddCashDetailsActivity.class);
        context.startActivity(intent);
    }

    /**
     * Open USDT payment page
     * @param context Application context
     */
    private static void openUsdtPaymentPage(Context context) {
        Intent intent = new Intent(context, BuyChipsPaymentDetails.class);

        // Pass wallet address from SharedPreferences to the activity
        SharedPreferences prefs = context.getSharedPreferences("Login_data", Context.MODE_PRIVATE);
        String payAddress = prefs.getString("pay_address", "");
        intent.putExtra("pay_address", payAddress);

        context.startActivity(intent);
    }

    /**
     * Show attractive "No wallet found" dialog when both payment gateways are disabled
     * @param context Application context
     */
    private static void showNoWalletFoundDialog(Context context) {
        try {
            final Dialog dialog = Functions.DialogInstance(context);
            dialog.setContentView(R.layout.dialog_no_wallet_found);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(android.view.Gravity.CENTER);
            }

            // Find views
            View imgClose = dialog.findViewById(R.id.img_close);


            // Close Button
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            Functions.setDialogParams(dialog);

        } catch (Exception e) {
            // Fallback to simple toast if dialog fails
            Toast.makeText(context, "❌ No wallet found. Payment services unavailable.", Toast.LENGTH_LONG).show();
        }
    }
    public static String getPaymentGatewayStatus(Context context) {
        return "INR: " + getInrPaymentGateway(context) +
               ", Crypto: " + getCryptoPaymentGateway(context);
    }
}
