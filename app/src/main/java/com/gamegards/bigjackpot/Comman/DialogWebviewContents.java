package com.gamegards.bigjackpot.Comman;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.Activity.Homepage;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialogWebviewContents {

    Context context;
    public DialogWebviewContents(Context context) {
        this.context = context;
    }

    public interface DealerInterface{

        void onClick(int pos);

    }

    TextView txtnotfound;
    public void showDialog(String tag) {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_webviewcontent);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((ImageView)dialog.findViewById(R.id.imgclosetop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txtnotfound = dialog.findViewById(R.id.txtnotfound);
        TextView txtheader = dialog.findViewById(R.id.txtheader);
        txtheader.setText(""+tag);

        WebView webView = dialog.findViewById(R.id.webview);

        webView.setBackgroundColor(Color.TRANSPARENT);


        UserTermsAndCondition(webView,dialog,tag);

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    private void UserTermsAndCondition(final WebView webview, final Dialog dialog, final String tag) {


        final RelativeLayout rlt_progress = dialog.findViewById(R.id.rlt_progress);
        rlt_progress.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.TERMS_CONDITION,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");


                            if (code.equalsIgnoreCase("200")) {


                                JSONObject jsonObject1 = jsonObject.getJSONObject("setting");

                                // ===== ADMIN SETTINGS API (SECONDARY SOURCE FOR CONTENT) =====
                                Log.d("AdminSettings", "=== Admin Settings API (Secondary Source for Content) ===");
                                Log.d("AdminSettings", "API: " + Const.TERMS_CONDITION);
                                Log.d("AdminSettings", "");
                                Log.d("AdminSettings", "Admin Settings Retrieved:");
                                Log.d("AdminSettings", "");
                                Log.d("AdminSettings", "terms - Terms & Conditions content: " + jsonObject1.optString("terms", "Not available"));
                                Log.d("AdminSettings", "privacy_policy - Privacy Policy content: " + jsonObject1.optString("privacy_policy", "Not available"));
                                Log.d("AdminSettings", "help_support - Help & Support content: " + jsonObject1.optString("help_support", "Not available"));
                                Log.d("AdminSettings", "refund_policy - Refund Policy content: " + jsonObject1.optString("refund_policy", "Not available"));
                                Log.d("AdminSettings", "contact_us - Contact Us content: " + jsonObject1.optString("contact_us", "Not available"));

                                // 🎯 Bank Details from Admin Settings API
                                Log.d("AdminSettings", "--- BANK DETAILS FROM ADMIN SETTINGS ---");
                                Log.d("AdminSettings", "bank_account_number: " + jsonObject1.optString("bank_account_number"));
                                Log.d("AdminSettings", "bank_ifsc_code: " + jsonObject1.optString("bank_ifsc_code"));
                                Log.d("AdminSettings", "bank_account_name: " + jsonObject1.optString("bank_account_name"));
                                Log.d("AdminSettings", "bank_name: " + jsonObject1.optString("bank_name"));
                                Log.d("AdminSettings", "bank_branch: " + jsonObject1.optString("bank_branch"));

                                // 🎯 Store Bank Details in SharedPreferences
                                SharedPreferences.Editor editor = context.getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("bank_account_number", jsonObject1.optString("bank_account_number"));
                                editor.putString("bank_ifsc_code", jsonObject1.optString("bank_ifsc_code"));
                                editor.putString("bank_account_name", jsonObject1.optString("bank_account_name"));
                                editor.putString("bank_name", jsonObject1.optString("bank_name"));
                                editor.putString("bank_branch", jsonObject1.optString("bank_branch"));
                                editor.apply();

                                Log.d("AdminSettings", "================================================================");

                                String data = "";

                                if(tag.equals("Privacy Policy"))
                                {
                                    data = jsonObject1.getString("privacy_policy");
                                }
                                else if(tag.equals(Variables.SUPPORT))
                                {
                                    data = jsonObject1.getString("help_support");
                                }
                                else if(tag.equals(Variables.REFUND_POLICY))
                                {
                                    data = jsonObject1.getString("refund_policy");
                                }
                                else if(tag.equals(Variables.CONTACT))
                                {
                                    data = jsonObject1.getString("contact_us");
                                }
                                else {
                                    data = jsonObject1.getString("terms");
                                }


                                if(data.equals(""))
                                {
                                    txtnotfound.setVisibility(View.VISIBLE);
                                }
                                else {
                                    txtnotfound.setVisibility(View.GONE);
                                }


                                data = data.replaceAll("&#39;","'");

                                String szMessage = "<font face= \"trebuchet\" size=3 color=\"#fff\"><b>"
                                        + data
                                        + "</b></font>";


                                webview.getSettings().setJavaScriptEnabled(true);
                                webview.loadDataWithBaseURL("",szMessage, "text/html", "UTF-8","");


                            } else {
                                if (jsonObject.has("message")) {

                                    txtnotfound.setVisibility(View.VISIBLE);

                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            txtnotfound.setVisibility(View.VISIBLE);

                        }

                        rlt_progress.setVisibility(View.GONE);



                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                Functions.showToast(context, "Something went wrong");
                txtnotfound.setVisibility(View.VISIBLE);
                rlt_progress.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }



}
