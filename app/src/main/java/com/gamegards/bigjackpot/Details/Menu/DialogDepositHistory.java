package com.gamegards.bigjackpot.Details.Menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.Comman.CommonAPI;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogDepositHistory {

    Dialog alert;
    Context context;
    TextView nofound;
    ProgressBar progressBar;
    RecyclerView rec_winning;

    // Pagination variables
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalRecords = 0;
    private int limit = 20;
    private String userToken = ""; // Records per page
    private ArrayList<DepositHistoryModel> depositHistoryModelArrayList;
    private DepositHistoryAdapter depositHistoryAdapter;

    public DialogDepositHistory(Context context) {
        this.context = context;
        alert = Functions.DialogInstance(context);
        alert.setContentView(R.layout.dialog_deposit_redeem);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nofound = alert.findViewById(R.id.txtnotfound);
        progressBar = alert.findViewById(R.id.progressBar);
        rec_winning = alert.findViewById(R.id.rec_winning);
        rec_winning.setLayoutManager(new LinearLayoutManager(context));

        // Initialize pagination data
        depositHistoryModelArrayList = new ArrayList<>();

        // Customize headers for Amount History
        customizeHeadersForAmountHistory();

        // Show all table columns for Amount History
        View lnrType = alert.findViewById(R.id.lnrType);
        View lnrStatus = alert.findViewById(R.id.lnrStatus);
        View lnrTime = alert.findViewById(R.id.lnrTime);

        lnrType.setVisibility(View.VISIBLE);
        lnrStatus.setVisibility(View.VISIBLE);
        lnrTime.setVisibility(View.VISIBLE);

        // Add pagination controls
        addPaginationControls();

        alert.findViewById(R.id.imgclosetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        SwipeRefreshLayout swiperefresh = alert.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1; // Reset to first page on refresh
                CALL_API_GET_LIST();
                swiperefresh.setRefreshing(false);
            }
        });

        CALL_API_GET_LIST();
    }

    public void show(){
        alert.show();
        Functions.setDialogParams(alert);
    }
    
    public void dismiss(){
        alert.dismiss();
    }

    private void CALL_API_GET_LIST(){
        Log.d("AmountHistory", "Calling getRechargeHistory API - Page: " + currentPage);

        NoDataVisible(false);
        depositHistoryModelArrayList.clear(); // Clear existing data

        // First get user data to extract token
        Log.d("AmountHistory", "Getting user data to extract token");
        CommonAPI.CALL_API_UserDetails((Activity) context, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                Log.d("AmountHistory", "=== USER DATA API RESPONSE FOR TOKEN ===");
                Log.d("AmountHistory", "Response: " + resp);

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code", "");

                    if (code.equalsIgnoreCase("200")) {
                        JSONArray userDataArray = jsonObject.optJSONArray("user_data");
                        if (userDataArray != null && userDataArray.length() > 0) {
                            JSONObject userData = userDataArray.getJSONObject(0);
                            String userToken = userData.optString("token", "");
          if (!userToken.isEmpty()) {
                                // Store token for pagination
                                DialogDepositHistory.this.userToken = userToken;
                                // Now call recharge history API with the token
                                callRechargeHistoryAPI(userToken, depositHistoryModelArrayList);
                            } else {
                                 HideProgressBar(true);
                                NoDataVisible(true);
                            }
                        } else {
                            HideProgressBar(true);
                            NoDataVisible(true);
                        }
                    } else {
                        HideProgressBar(true);
                        NoDataVisible(true);
                    }
                } catch (Exception e) {
                      e.printStackTrace();
                    HideProgressBar(true);
                    NoDataVisible(true);
                }
            }
        });
    }

    private void callRechargeHistoryAPI(String userToken, ArrayList<DepositHistoryModel> depositHistoryModelArrayList) {
          // Create JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("auth", userToken);
            requestBody.put("page", currentPage);
            requestBody.put("limit", limit);
            requestBody.put("type", "all"); // Get all types

          } catch (JSONException e) {
              HideProgressBar(true);
            NoDataVisible(true);
            return;
        }

        // Make API call using Volley
        String url = Const.GET_RECHARGE_HISTORY;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        boolean status = response.optBoolean("status", false);
                        String message = response.optString("message", "");

                            if (status) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                JSONArray recharges = data.optJSONArray("recharges");

                                if (recharges != null) {
                                    for (int i = 0; i < recharges.length(); i++) {
                                        JSONObject recharge = recharges.getJSONObject(i);

                                        DepositHistoryModel model = new DepositHistoryModel();

                                        // Map API response to model
                                        JSONObject user = data.optJSONObject("user");
                                        String userId = user != null ? user.optString("id", "") : "";
                                        String phone = user != null ? user.optString("mobile", "") : "";

                                        model.setUserId(userId);
                                        model.setTransactionId(recharge.optString("transactionId", ""));
                                        model.setUtr(recharge.optString("utr", ""));
                                        model.setPhone(phone);
                                        model.setMoney(recharge.optString("amount", ""));
                                        model.setType(recharge.optString("type", ""));

                                        // Use statusCode field for the actual status (Pending/Approved/Cancelled)
                                        String actualStatus = recharge.optString("statusCode", "");
                                        model.setStatus(actualStatus);
                                        model.setTime(recharge.optString("date", ""));
                                        depositHistoryModelArrayList.add(model);
                                       }
                                }

                                // Extract and store pagination info
                                JSONObject pagination = data.optJSONObject("pagination");
                                if (pagination != null) {
                                    currentPage = pagination.optInt("currentPage", 1);
                                    totalPages = pagination.optInt("totalPages", 1);
                                    totalRecords = pagination.optInt("totalRecords", 0);
                                    // Update pagination controls
                                    updatePaginationControls();
                                }

                                // Log summary info
                                JSONObject summary = data.optJSONObject("summary");
                                if (summary != null) {
                                       }
                            }
                        } else {
                         }

                    } catch (JSONException e) {
                         e.printStackTrace();
                    }

                    if(depositHistoryModelArrayList.size() <= 0) {
                        NoDataVisible(true);
                     } else {

                        // Create and set adapter to display the data
                        if (depositHistoryAdapter == null) {
                            depositHistoryAdapter = new DepositHistoryAdapter(context, depositHistoryModelArrayList);
                            rec_winning.setAdapter(depositHistoryAdapter);
                        } else {
                            depositHistoryAdapter.notifyDataSetChanged();
                        }

                    }

                    HideProgressBar(true);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null) {
                         try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                         } catch (Exception e) {
                         }
                    }

                    HideProgressBar(true);
                    NoDataVisible(true);
                }
            }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonRequest);
    }

    private void NoDataVisible(boolean visible){
        nofound.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void HideProgressBar(boolean gone){
        progressBar.setVisibility(!gone ? View.VISIBLE : View.GONE);
    }

    private void customizeHeadersForAmountHistory() {
        try {
            // Find and update header TextViews to show Amount History columns
            TextView tvSerial = alert.findViewById(R.id.lnrSerial).findViewById(android.R.id.text1);
            if (tvSerial == null) {
                // Try to find TextView in the LinearLayout
                LinearLayout lnrSerial = alert.findViewById(R.id.lnrSerial);
                if (lnrSerial != null && lnrSerial.getChildCount() > 0) {
                    View child = lnrSerial.getChildAt(0);
                    if (child instanceof TextView) {
                        tvSerial = (TextView) child;
                    }
                }
            }
            if (tvSerial != null) {
                tvSerial.setText("User ID");
            }

            TextView tvDate = alert.findViewById(R.id.lnrDate).findViewById(android.R.id.text1);
            if (tvDate == null) {
                LinearLayout lnrDate = alert.findViewById(R.id.lnrDate);
                if (lnrDate != null && lnrDate.getChildCount() > 0) {
                    View child = lnrDate.getChildAt(0);
                    if (child instanceof TextView) {
                        tvDate = (TextView) child;
                    }
                }
            }
            if (tvDate != null) {
                tvDate.setText("Transaction ID");
            }

            TextView tvGame = alert.findViewById(R.id.tvGame);
            if (tvGame != null) {
                tvGame.setText("Phone");
            }

            TextView tvCash = alert.findViewById(R.id.tvCash);
            if (tvCash != null) {
                tvCash.setText("Money");
            }

            // Column 5: Type
            TextView tvType = alert.findViewById(R.id.tvType);
            if (tvType != null) {
                tvType.setText("Type");
            }

            // Column 6: Status
            TextView tvStatus = alert.findViewById(R.id.tvStatus);
            if (tvStatus != null) {
                tvStatus.setText("Status");
            }

            // Column 7: Time
            TextView tvTime = alert.findViewById(R.id.tvTime);
            if (tvTime != null) {
                tvTime.setText("Time");
            }

            // Make all columns visible for Amount History
            View lnrType = alert.findViewById(R.id.lnrType);
            if (lnrType != null) {
                lnrType.setVisibility(View.VISIBLE);
            }

            View lnrStatus = alert.findViewById(R.id.lnrStatus);
            if (lnrStatus != null) {
                lnrStatus.setVisibility(View.VISIBLE);
            }

            View lnrTimeView = alert.findViewById(R.id.lnrTime);
            if (lnrTimeView != null) {
                lnrTimeView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
                        e.printStackTrace();
        }
    }

    /**
     * Add pagination controls to the dialog
     */
    private void addPaginationControls() {
        try {
            // Find the main container
            RelativeLayout mainContainer = alert.findViewById(R.id.lnr_box);

            // Create pagination layout
            LinearLayout paginationLayout = new LinearLayout(context);
            paginationLayout.setOrientation(LinearLayout.HORIZONTAL);
            paginationLayout.setGravity(Gravity.CENTER);
            paginationLayout.setPadding(12, 12, 12, 12);
            paginationLayout.setId(View.generateViewId());

            // Previous button with custom styling
            TextView btnPrevious = new TextView(context);
            btnPrevious.setText("◀ PREV");
            btnPrevious.setId(View.generateViewId());
            btnPrevious.setTextSize(11);
            btnPrevious.setTextColor(context.getResources().getColor(android.R.color.white));
            btnPrevious.setPadding(20, 8, 20, 8);
            btnPrevious.setGravity(Gravity.CENTER);
            btnPrevious.setTypeface(null, android.graphics.Typeface.BOLD);

            // Set clickable background
            try {
                btnPrevious.setBackgroundResource(R.drawable.bg_gredient_white_border);
            } catch (Exception e) {
                btnPrevious.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }

            btnPrevious.setOnClickListener(v -> {
                if (currentPage > 1) {
                    currentPage--;
                    Log.d("AmountHistory", "Going to previous page: " + currentPage);
                    callRechargeHistoryAPI(userToken, depositHistoryModelArrayList);
                }
            });

            // Page info text with elegant styling
            TextView tvPageInfo = new TextView(context);
            tvPageInfo.setId(View.generateViewId());
            tvPageInfo.setTextSize(11);
            tvPageInfo.setPadding(24, 8, 24, 8);
            tvPageInfo.setGravity(Gravity.CENTER);
            tvPageInfo.setTextColor(context.getResources().getColor(R.color.coloryellow));
            tvPageInfo.setTypeface(null, android.graphics.Typeface.BOLD);

            // Next button with custom styling
            TextView btnNext = new TextView(context);
            btnNext.setText("NEXT ▶");
            btnNext.setId(View.generateViewId());
            btnNext.setTextSize(11);
            btnNext.setTextColor(context.getResources().getColor(android.R.color.white));
            btnNext.setPadding(20, 8, 20, 8);
            btnNext.setGravity(Gravity.CENTER);
            btnNext.setTypeface(null, android.graphics.Typeface.BOLD);

            // Set clickable background
            try {
                btnNext.setBackgroundResource(R.drawable.bg_gredient_white_border);
            } catch (Exception e) {
                btnNext.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }

            btnNext.setOnClickListener(v -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    Log.d("AmountHistory", "Going to next page: " + currentPage);
                    callRechargeHistoryAPI(userToken, depositHistoryModelArrayList);
                }
            });

            // Create layout params for buttons
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(8, 0, 8, 0);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.setMargins(16, 0, 16, 0);

            // Add buttons to pagination layout
            paginationLayout.addView(btnPrevious, buttonParams);
            paginationLayout.addView(tvPageInfo, textParams);
            paginationLayout.addView(btnNext, buttonParams);

            // Store references for later updates
            paginationLayout.setTag("pagination_layout");
            btnPrevious.setTag("btn_previous");
            tvPageInfo.setTag("tv_page_info");
            btnNext.setTag("btn_next");

            // Add pagination layout to main container
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(10, 8, 10, 12);

            mainContainer.addView(paginationLayout, params);

            Log.d("AmountHistory", "Pagination controls added successfully");

        } catch (Exception e) {
            Log.e("AmountHistory", "Error adding pagination controls: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update pagination controls based on current state
     */
    private void updatePaginationControls() {
        try {
            RelativeLayout mainContainer = alert.findViewById(R.id.lnr_box);
            LinearLayout paginationLayout = mainContainer.findViewWithTag("pagination_layout");

            if (paginationLayout != null) {
                TextView btnPrevious = paginationLayout.findViewWithTag("btn_previous");
                TextView tvPageInfo = paginationLayout.findViewWithTag("tv_page_info");
                TextView btnNext = paginationLayout.findViewWithTag("btn_next");

                if (btnPrevious != null) {
                    btnPrevious.setEnabled(currentPage > 1);
                    btnPrevious.setAlpha(currentPage > 1 ? 1.0f : 0.4f);
                    if (currentPage <= 1) {
                        btnPrevious.setTextColor(context.getResources().getColor(R.color.colordullwhite));
                    } else {
                        btnPrevious.setTextColor(context.getResources().getColor(android.R.color.white));
                    }
                }

                if (tvPageInfo != null) {
                    String pageText = "Page " + currentPage + " of " + totalPages +
                                    "\n(" + totalRecords + " records)";
                    tvPageInfo.setText(pageText);
                }

                if (btnNext != null) {
                    btnNext.setEnabled(currentPage < totalPages);
                    btnNext.setAlpha(currentPage < totalPages ? 1.0f : 0.4f);
                    if (currentPage >= totalPages) {
                        btnNext.setTextColor(context.getResources().getColor(R.color.colordullwhite));
                    } else {
                        btnNext.setTextColor(context.getResources().getColor(android.R.color.white));
                    }
                }

                Log.d("AmountHistory", "Pagination controls updated - Page " + currentPage + "/" + totalPages);
            }

        } catch (Exception e) {
            Log.e("AmountHistory", "Error updating pagination controls: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
