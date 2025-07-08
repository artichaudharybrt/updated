package com.gamegards.gaming27.Comman;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.gaming27.Activity.Homepage;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Utils.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialogReport {

    private static final String TAG = "DialogReport";
    Context context;

    public DialogReport(Context context) {
        this.context = context;
    }

    public interface DealerInterface{

        void onClick(int pos);

    }

    public void showReportDialog() {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_send_report);
        dialog.setTitle("Title...");

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);

        tv_heading.setText("Report isssue!");

        final EditText edtReportDecriction = (EditText) dialog.findViewById(R.id.edtReportDecriction);

        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Functions.checkisStringValid(Functions.getStringFromEdit(edtReportDecriction)))
                {
                    Functions.showToast(context,"Please enter report description.");
                    return;
                }

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    // Public method to test admin settings API
    public void testAdminSettings() {
        Log.d(TAG, "testAdminSettings: Starting admin settings test");
        UserTermsAndCondition(null, null, "test");
    }

    private void UserTermsAndCondition(final WebView webview, final Dialog dialog, final String tag) {

        Log.d(TAG, "UserTermsAndCondition: Starting admin settings API call");

        final RelativeLayout rlt_progress;
        if (dialog != null) {
            rlt_progress = dialog.findViewById(R.id.rlt_progress);
            rlt_progress.setVisibility(View.VISIBLE);
        } else {
            rlt_progress = null;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.TERMS_CONDITION,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "UserTermsAndCondition: Raw API Response: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            Log.d(TAG, "UserTermsAndCondition: Response Code: " + code);
                            Log.d(TAG, "UserTermsAndCondition: Response Message: " + message);

                            if (code.equalsIgnoreCase("200")) {
                                Log.d(TAG, "UserTermsAndCondition: Success response received");

                                if (jsonObject.has("setting")) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("setting");
                                    Log.d(TAG, "UserTermsAndCondition: Admin Settings Object: " + jsonObject1.toString());

                                    // Log all keys in the settings object
                                    Log.d(TAG, "UserTermsAndCondition: Settings Keys: " + jsonObject1.keys().toString());

                                    String data = "";
                                    // You can process the settings data here
                                } else {
                                    Log.w(TAG, "UserTermsAndCondition: No 'setting' object found in response");
                                }
                            } else {
                                Log.e(TAG, "UserTermsAndCondition: API returned error code: " + code + ", message: " + message);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "UserTermsAndCondition: JSON parsing error", e);
                            e.printStackTrace();
                        }

                        if (rlt_progress != null) {
                            rlt_progress.setVisibility(View.GONE);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "UserTermsAndCondition: API Error", error);
                if (error.networkResponse != null) {
                    Log.e(TAG, "UserTermsAndCondition: Error Status Code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "UserTermsAndCondition: Error Response Data: " + new String(error.networkResponse.data));
                }
                Functions.showToast(context, "Something went wrong");
                if (rlt_progress != null) {
                    rlt_progress.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE);
                String userId = prefs.getString("user_id", "");
                String token = prefs.getString("token", "");

                Log.d(TAG, "UserTermsAndCondition: Request Parameters - user_id: " + userId + ", token: " + token);

                params.put("user_id", userId);
                params.put("token", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                Log.d(TAG, "UserTermsAndCondition: Request Headers - token: " + Const.TOKEN);
                return headers;
            }
        };

        Log.d(TAG, "UserTermsAndCondition: Making API request to: " + Const.TERMS_CONDITION);
        Volley.newRequestQueue(context).add(stringRequest);

    }



}
