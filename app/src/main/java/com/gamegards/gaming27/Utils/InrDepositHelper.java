package com.gamegards.gaming27.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Comman.CommonAPI;
import com.gamegards.gaming27.Interface.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for INR Deposit functionality
 * Handles UTR generation and API calls for INR deposits
 */
public class InrDepositHelper {

    private static final String TAG = "InrDepositHelper";
    private static final String MY_PREFS_NAME = "Login_data";

    private Activity context;
    private ProgressDialog progressDialog;

    public interface InrDepositCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public InrDepositHelper(Activity context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Processing INR Deposit...");
        this.progressDialog.setCancelable(false);
    }



    /**
     * Submit INR deposit to server
     * @param amount Deposit amount
     * @param utr UTR ID (required)
     * @param callback Callback for success/error handling
     */

    public void submitInrDeposit(String amount, String utr, InrDepositCallback callback) {

        // First fetch user data to get userId from API response
        CommonAPI.CALL_API_UserDetails((Activity) context, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.getString("code");

                        if (code.equalsIgnoreCase("200")) {
                            // Extract userId from API response (not SharedPreferences)
                            JSONObject userDataObject = jsonObject.getJSONArray("user_data").getJSONObject(0);
                            String userId = userDataObject.getString("id");

                            // Continue with deposit process using userId from API
                            continueInrDepositProcess(userId, amount, utr, callback);

                        } else {
                                  callback.onError("Failed to fetch user data. Please try again.");
                        }

                    } catch (JSONException e) {
                               callback.onError("Error processing user data");
                    }
                } else {
                         callback.onError("Failed to fetch user data. Please check your connection.");
                }
            }
        });
    }

    /**
     * Continue INR deposit process after getting userId from API
     */
    private void continueInrDepositProcess(String userId, String amount, String utr, InrDepositCallback callback) {
        if (userId == null || userId.trim().isEmpty()) {
               callback.onError("User ID not found. Please login again.");
            return;
        }

        if (amount == null || amount.trim().isEmpty()) {
             callback.onError("Amount is required");
            return;
        }

        if (utr == null || utr.trim().isEmpty()) {
             callback.onError("UTR is required");
            return;
        }

        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                 callback.onError("Amount must be greater than 0");
                return;
            }
          } catch (NumberFormatException e) {
              callback.onError("Invalid amount format");
            return;
        }
        progressDialog.show();
        makeInrDepositRequest(userId, amount, utr, callback);
    }



    /**
     * Make the actual API request for INR deposit
     */
    private void makeInrDepositRequest(String userId, String amount, String utr, InrDepositCallback callback) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.INR_DEPOSIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                          progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Check for different response formats
                            boolean isSuccess = false;
                            String message = jsonObject.optString("message", "Unknown response");

                            // Check for "success" field (new API format)
                            if (jsonObject.has("success")) {
                                isSuccess = jsonObject.getBoolean("success");
                             }
                            // Check for "code" field (old API format)
                            else if (jsonObject.has("code")) {
                                String code = jsonObject.getString("code");
                                isSuccess = code.equals("200") || code.equals("1");
                              }
                            // Check for "status" field (alternative format)
                            else if (jsonObject.has("status")) {
                                String status = jsonObject.getString("status");
                                isSuccess = status.equalsIgnoreCase("success") || status.equalsIgnoreCase("true");
                            }

                            if (isSuccess) {
                                 callback.onSuccess(message);
                            } else {
                                   callback.onError(message);
                            }

                        } catch (JSONException e) {
                            callback.onError("Invalid response format");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         progressDialog.dismiss();


                        String errorMessage = "Network error occurred";
                        if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }

                             callback.onError(errorMessage);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("userId", userId);
                params.put("amount", amount);
                params.put("utr", utr);

                 return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                return headers;
            }
        };

        // Set retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                1, // 1 retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to queue
        Volley.newRequestQueue(context).add(stringRequest);
    }

    /**
     * Quick helper method to submit INR deposit with current user
     */
    public void submitInrDepositForCurrentUser(String amount, String utr, InrDepositCallback callback) {
        submitInrDeposit(amount, utr, callback);
    }

    /**
     * Static helper method for quick INR deposit
     */
    public static void quickInrDeposit(Activity context, String amount, String utr) {
        InrDepositHelper helper = new InrDepositHelper(context);
        helper.submitInrDepositForCurrentUser(amount, utr, new InrDepositCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(context, "Deposit Successful: " + message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Deposit Failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }


}
