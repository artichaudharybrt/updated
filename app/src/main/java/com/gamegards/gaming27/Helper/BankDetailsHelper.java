package com.gamegards.gaming27.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.gamegards.gaming27.Activity.Homepage;

/**
 * Helper class for managing bank details from admin settings
 * Bank details are fetched from TERMS_CONDITION API and stored in SharedPreferences
 */
public class BankDetailsHelper {

    /**
     * Get bank account number from admin settings
     * @param context Application context
     * @return Bank account number or empty string if not available
     */
    public static String getBankAccountNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("bank_account_number", "");
    }

    /**
     * Get bank IFSC code from admin settings
     * @param context Application context
     * @return Bank IFSC code or empty string if not available
     */
    public static String getBankIFSCCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("bank_ifsc_code", "");
    }

    /**
     * Get bank account holder name from admin settings
     * @param context Application context
     * @return Bank account holder name or empty string if not available
     */
    public static String getBankAccountName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("bank_account_name", "");
    }

    /**
     * Get bank name from admin settings
     * @param context Application context
     * @return Bank name or empty string if not available
     */
    public static String getBankName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("bank_name", "");
    }

    /**
     * Get bank branch from admin settings
     * @param context Application context
     * @return Bank branch or empty string if not available
     */
    public static String getBankBranch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("bank_branch", "");
    }



    /**
     * Check if all bank details are available
     * @param context Application context
     * @return true if all bank details are available, false otherwise
     */
    public static boolean areBankDetailsAvailable(Context context) {
        return !getBankAccountNumber(context).isEmpty() &&
               !getBankIFSCCode(context).isEmpty() &&
               !getBankAccountName(context).isEmpty() &&
               !getBankName(context).isEmpty();
    }

    /**
     * Get formatted bank details string for display
     * @param context Application context
     * @return Formatted bank details string
     */
    public static String getFormattedBankDetails(Context context) {
        StringBuilder details = new StringBuilder();
        details.append("Bank Name: ").append(getBankName(context)).append("\n");
        details.append("Account Name: ").append(getBankAccountName(context)).append("\n");
        details.append("Account Number: ").append(getBankAccountNumber(context)).append("\n");
        details.append("IFSC Code: ").append(getBankIFSCCode(context)).append("\n");
        details.append("Branch: ").append(getBankBranch(context));
        return details.toString();
    }
}
