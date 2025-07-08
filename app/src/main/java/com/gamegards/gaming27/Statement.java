package com.gamegards.gaming27;

import static com.gamegards.gaming27.Activity.Homepage.MY_PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.gaming27.RedeemCoins.RedeemActivity;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Utils.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Statement extends AppCompatActivity {

    TextView txtheader;
    ImageView imgclosetop;
    ProgressDialog progressDialog;
    private Spinner spinner;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        progressDialog = new ProgressDialog(Statement.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        txtheader = findViewById(R.id.txtheader);
        txtheader.setText("Statement");

//        Spinner dropdown = findViewById(R.id.spinner1);
        spinner =findViewById(R.id.spinner1);

//        String[] items = new String[]{"Games",""};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(alert.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
//        spinner.setAdapter(adapter);

        imgclosetop = findViewById(R.id.imgclosetop);
        imgclosetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View applyButton = findViewById(R.id.btn_apply);
        if (applyButton != null) {
            applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Statement.this, RedeemActivity.class);
                        // Add flags to create a new task and clear top to avoid issues with existing instances
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("Statement", "Error starting RedeemActivity", e);
                        Toast.makeText(Statement.this, "Error opening withdrawal page", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e("Statement", "btn_apply view not found");
        }

//        getList();
    }

    public void onResume(){
        super.onResume();
        getList();
    }

    double wallet=0;
    LinearLayout lnrcancelist;
    public void showList(JSONArray last_bet) throws JSONException {
        Log.d("json_list", String.valueOf(last_bet));
        lnrcancelist = findViewById(R.id.lnrlastView);
        lnrcancelist.removeAllViews();

        Set<String> sourcesSet = new HashSet<>(); // To store unique sources for Spinner

        // List to store all the bets so that we can filter them later based on Spinner selection
        List<JSONObject> allBets = new ArrayList<>();

        // Parse the first item to get the wallet value
        JSONObject jsonObject = last_bet.getJSONObject(0);
        wallet = Double.parseDouble(jsonObject.getString("current_wallet"));
        Log.d("user_wallet_", String.valueOf(wallet));

        // Loop through the JSON array and store the data
        for (int i = 0; i < last_bet.length(); i++) {
            JSONObject bet = last_bet.getJSONObject(i);
            String game = bet.getString("source");

            // Add the source to the sources set (ensures no duplicates)
            sourcesSet.add(game);

            // Add the entire bet object to the allBets list for future filtering
            allBets.add(bet);
        }

        // Display all bets initially
        displayBets(allBets);

        // Convert the set to a list for the spinner adapter
        List<String> sourcesList = new ArrayList<>(sourcesSet);
        sourcesList.add(0, "All"); // Add "All" option to the spinner for showing all bets

        // Set the adapter for the Spinner with the unique source list
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Statement.this, android.R.layout.simple_spinner_dropdown_item, sourcesList);
        spinner.setAdapter(spinnerAdapter);

        // Add a listener to the spinner to filter the list when an item is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected source
                String selectedSource = sourcesList.get(position);

                // If "All" is selected, show the full list; otherwise, filter by source
                if (selectedSource.equals("All")) {
                    try {
                        displayBets(allBets);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        filterListBySource(selectedSource, allBets);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Method to display bets
    private void displayBets(List<JSONObject> bets) throws JSONException {
        lnrcancelist.removeAllViews(); // Clear the previous list
        for (JSONObject bet : bets) {
            addLastWinBet("", bet.getString("source"), bet.getString("source_id"),
                    bet.getString("current_wallet"), bet.getString("amount"),
                    bet.getString("added_date"));
        }
    }

    // Method to filter the list based on the selected source
    private void filterListBySource(String source, List<JSONObject> allBets) throws JSONException {
        List<JSONObject> filteredBets = new ArrayList<>();
        for (JSONObject bet : allBets) {
            if (bet.getString("source").equals(source)) {
                filteredBets.add(bet);
            }
        }
        displayBets(filteredBets); // Display the filtered list
    }


    int pos = 1;
    private void addLastWinBet(String id, String game, String ref_id, String total, String brack_val, String date) {
        View view = LayoutInflater.from(Statement.this).inflate(R.layout.view_color_statement_list,null);
        TextView tvFiled1 = view.findViewById(R.id.tvFiled1);
        TextView tvFiled2 = view.findViewById(R.id.tvFiled2);
        TextView tvFiled3 = view.findViewById(R.id.tvFiled3);
        TextView tvFiled4 = view.findViewById(R.id.tvFiled4);
        TextView tvFiled5 = view.findViewById(R.id.tvFiled5);
        TextView tvFiled6 = view.findViewById(R.id.tvFiled6);

        tvFiled1.setText(""+pos++);
        tvFiled2.setText(""+String.valueOf(game));
        tvFiled3.setText("#"+String.valueOf(ref_id));
        tvFiled4.setText("₹ "+wallet);

//        double final_val = (wallet) - (Double.parseDouble(brack_val));
        if (brack_val.contains("-")){
            tvFiled6.setTextColor(Color.parseColor("#D50000"));
            tvFiled6.setText("("+brack_val+")");
        }else{
            tvFiled6.setText("("+"+"+brack_val+")");
        }
        tvFiled4.setText("₹ "+total);
        tvFiled5.setText(""+date);

        lnrcancelist.addView(view);
    }

//    private void getList() {      //list
//        progressDialog.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_WALLET_HISTORY,
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//                        // progressDialog.dismiss();
//                        Log.d("DATA_CHECK_LIST_REPORT", "onResponse: " + response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String code = jsonObject.getString("code");
//                            if (code.equalsIgnoreCase("200")) {
//                                progressDialog.dismiss();
//                                JSONArray data = jsonObject.getJSONArray("GameLog");
//                                int data_ = data.length();
//                                if (data_==0){
//                                    Toast.makeText(Statement.this, "No Logs found!", Toast.LENGTH_LONG).show();
//                                }
//                                showList(data);
//
//                            } else {
//                                if (jsonObject.has("message")) {
//                                    String message = jsonObject.getString("message");
//                                    Functions.showToast(Statement.this, message);
//                                    progressDialog.dismiss();
//                                }
//
//                            }
//                        } catch (JSONException e) {
//                            progressDialog.dismiss();
//                            e.printStackTrace();
//                        }
//                    }
//
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //  progressDialog.dismiss();
//                Functions.showToast(Statement.this, "Something went wrong");
//                progressDialog.dismiss();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                SharedPreferences prefs = Statement.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                params.put("user_id", prefs.getString("user_id", ""));
//                params.put("token", prefs.getString("token", ""));
//                Log.d("paremter_java_with_list", "getParams: " + params);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("token", Const.TOKEN);
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(Statement.this).add(stringRequest);
//
//    }

    private void getList() {
        // Show the loader before making the network request
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_WALLET_STATEMENT_NEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response here
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equalsIgnoreCase("200")) {
                                JSONArray data = jsonObject.getJSONArray("statement");
                                if (data.length() == 0) {
                                    Toast.makeText(Statement.this, "No Logs found!", Toast.LENGTH_LONG).show();
                                } else {
                                    showList(data);  // Load the data into the UI
                                }
                            } else {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    Functions.showToast(Statement.this, message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            // Dismiss the loader after processing the response or handling an exception
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response here
                Functions.showToast(Statement.this, "Something went wrong");
                // Always dismiss the loader in case of error
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Sending parameters with the request
                SharedPreferences prefs = Statement.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Sending headers with the request
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(Statement.this).add(stringRequest);
    }

}