package com.gamegards.bigjackpot.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.WalletApplication;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splashscreen extends BaseActivity{
    private static final String MY_PREFS_NAME = "Login_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

//        Mint.initAndStartSession(this.getApplication(), "cc552ad8");
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        RelativeLayout relativeLayout = findViewById(R.id.rlt_parent);
//        SetBackgroundImageAsDisplaySize(this,relativeLayout,R.drawable.splash);
        SharePref.getInstance().init(getApplicationContext());
//        SharePref.getInstance().putBoolean(SharePref.isDragonTigerVisible, true);
//        SharePref.getInstance().putBoolean(SharePref.isTeenpattiVisible, true);
//        SharePref.getInstance().putBoolean(SharePref.isPrivateVisible, true);
//        SharePref.getInstance().putBoolean(SharePref.isCustomVisible, true);
//        SharePref.getInstance().putBoolean(SharePref.isRummyVisible, true);
//        SharePref.getInstance().putBoolean(SharePref.isAndharBaharVisible, true);
        SharePref.getInstance().putBoolean(SharePref.isLoginWithPassword, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // Check wallet connection status first
                    checkWalletConnectionAndNavigate();
                }
            }
        }).start();
    }

    private void checkWalletConnectionAndNavigate() {
        // For now, let's simplify and just check if we have a stored wallet address
        // The actual wallet connection check will happen in WalletConnectActivity
        String existingWalletAddress = WalletApplication.getWalletAddress();

        if (existingWalletAddress != null && !existingWalletAddress.isEmpty()) {
            Log.d("Splashscreen", "Found existing wallet address - calling login API");
            callLoginWithWalletAddress(existingWalletAddress);
        } else {
            Log.d("Splashscreen", "No wallet address found - navigating to WalletConnect screen");
            // Navigate to wallet connect screen
            startActivity(new Intent(Splashscreen.this, com.gamegards.bigjackpot.wallet.WalletConnectActivity.class));
            finish();
        }
    }

    private void callLoginWithWalletAddress(String walletAddress) {
        Log.d("Splashscreen", "Calling login API with wallet address: " + walletAddress);

        HashMap<String, String> params = new HashMap<>();
        params.put("wallet_address", walletAddress);

        ApiRequest.Call_Api(this, Const.login_withpassword, params, new Callback() {
            @Override
            public void Responce(String response, String type, Bundle bundle) {
                Log.d("Splashscreen", "Login API Response: " + response);

                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String code = jsonObject.getString("code");

                        switch (code) {
                            case "201":
                                // Login successful - user exists
                                Log.d("Splashscreen", "Login successful - storing user data");

                                String token = jsonObject.getString("token");
                                if (jsonObject.has("user")) {
                                    org.json.JSONArray userArray = jsonObject.getJSONArray("user");
                                    if (userArray.length() > 0) {
                                        JSONObject userObject = userArray.getJSONObject(0);
                                        String id = userObject.getString("id");
                                        String name = userObject.getString("name");
                                        String mobile = userObject.getString("mobile");

                                        // Store user data in SharedPreferences
                                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                        editor.putString("user_id", id);
                                        editor.putString("name", name);
                                        editor.putString("mobile", mobile);
                                        editor.putString("token", token);
                                        editor.apply();

                                        Log.d("Splashscreen", "User data stored - navigating to Homepage");

                                        Intent intent = new Intent(Splashscreen.this, Homepage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        navigateToWalletConnect();
                                    }
                                } else {
                                    navigateToWalletConnect();
                                }
                                break;

                            case "200":
                                // Registration successful - new user
                                Log.d("Splashscreen", "Registration successful - storing user data");

                                org.json.JSONArray userDataArray = jsonObject.optJSONArray("user_data");
                                if (userDataArray != null && userDataArray.length() > 0) {
                                    JSONObject userDataObject = userDataArray.getJSONObject(0);
                                    String userToken = userDataObject.getString("token");
                                    String userId = userDataObject.getString("id");

                                    // Store user data in SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putString("user_id", userId);
                                    editor.putString("token", userToken);
                                    editor.apply();

                                    Log.d("Splashscreen", "User data stored - navigating to Homepage");

                                    Intent intent = new Intent(Splashscreen.this, Homepage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    navigateToWalletConnect();
                                }
                                break;

                            default:
                                // Login failed - go to wallet connect
                                Log.d("Splashscreen", "Login failed - navigating to WalletConnect");
                                navigateToWalletConnect();
                                break;
                        }
                    } catch (JSONException e) {
                        Log.e("Splashscreen", "Error parsing login response: " + e.getMessage());
                        navigateToWalletConnect();
                    }
                } else {
                    Log.e("Splashscreen", "Login API returned null response");
                    navigateToWalletConnect();
                }
            }
        });
    }

    private void navigateToWalletConnect() {
        startActivity(new Intent(Splashscreen.this, com.gamegards.bigjackpot.wallet.WalletConnectActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Check_game_on_off();
    }

    public void Check_game_on_off() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.game_on_off,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.d("game_response_", "onResponse: " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                            if (code.equalsIgnoreCase("200")) {
//
                                JSONObject game_setting = jsonObject.getJSONObject("game_setting");
                                String teen_patti = game_setting.getString("teen_patti");
                                if (teen_patti.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isTeenpattiVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isTeenpattiVisible, false);
                                }
                                String dragon_tiger = game_setting.getString("dragon_tiger");
                                if (dragon_tiger.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isDragonTigerVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isDragonTigerVisible, false);
                                }
                                String andar_bahar = game_setting.getString("andar_bahar");
                                if (andar_bahar.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isAndharBaharVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isAndharBaharVisible, false);
                                }
                                String point_rummy = game_setting.getString("point_rummy");
                                if (point_rummy.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isPointRummyVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isPointRummyVisible, false);
                                }
                                String private_rummy = game_setting.getString("private_rummy");
                                if (private_rummy.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isPrivateRummyVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isPrivateRummyVisible, false);
                                }
                                String pool_rummy = game_setting.getString("pool_rummy");
                                if (pool_rummy.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isPoolRummyVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isPoolRummyVisible, false);
                                }
                                String deal_rummy = game_setting.getString("deal_rummy");
                                if (deal_rummy.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isDealRummyVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isDealRummyVisible, false);
                                }
                                String private_table = game_setting.getString("private_table");
                                if (private_table.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isPrivateVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isPrivateVisible, false);
                                }
                                String custom_boot = game_setting.getString("custom_boot");
                                if (custom_boot.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isCustomVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isCustomVisible, false);
                                }
                                String seven_up_down = game_setting.getString("seven_up_down");
                                if (seven_up_down.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isSevenUpVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isSevenUpVisible, false);
                                }
                                String car_roulette = game_setting.getString("car_roulette");
                                if (car_roulette.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isCarRouletteVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isCarRouletteVisible, false);
                                }
                                String jackpot_teen_patti = game_setting.getString("jackpot_teen_patti");
                                if (jackpot_teen_patti.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isHomeJackpotVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isHomeJackpotVisible, false);
                                }
                                String animal_roulette = game_setting.getString("animal_roulette");
                                if (animal_roulette.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isAnimalRouletteVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isAnimalRouletteVisible, false);
                                }
                                String color_prediction = game_setting.getString("color_prediction");
                                if (color_prediction.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isColorPredictionVisible, true);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction1Visible, true);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction3Visible, true);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction30Visible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isColorPredictionVisible, false);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction1Visible, false);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction3Visible, false);
                                    SharePref.getInstance().putBoolean(SharePref.isColorPrediction30Visible, false);
                                }
                                String poker = game_setting.getString("poker");
                                if (poker.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isPokerVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isPokerVisible, false);
                                }
                                String head_tails = game_setting.getString("head_tails");
                                if (head_tails.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isHeadTailVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isHeadTailVisible, false);
                                }
                                String red_vs_black = game_setting.getString("red_vs_black");
                                if (red_vs_black.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isRedBlackVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isRedBlackVisible, false);
                                }
                                String ludo = game_setting.getString("ludo_local");
                                if (ludo.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isLudoVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isLudoVisible, false);
                                }
                                String ludo_online = game_setting.getString("ludo_online");
                                if (ludo_online.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isLudoOnlineVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isLudoOnlineVisible, false);
                                }
                                String ludo_computer = game_setting.getString("ludo_computer");
                                if (ludo_computer.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isLudoComputerVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isLudoComputerVisible, false);
                                }
                                String bacarate = game_setting.getString("bacarate");
                                if (bacarate.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isBacarateVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isBacarateVisible, false);
                                }
                                String jhandi_munda = game_setting.getString("jhandi_munda");
                                if (jhandi_munda.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isJhandi_MundaVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isJhandi_MundaVisible, false);
                                }
                                String roulette = game_setting.getString("roulette");
                                if (roulette.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isRouletteVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isRouletteVisible, false);
                                }
                                String tour = game_setting.getString("tournament_rummy");
                                if (tour.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isTournamentVisible, false);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isTournamentVisible, false);
                                }
                                String aviator = game_setting.getString("Aviator");
                                if (aviator.equals("1")){
                                    SharePref.getInstance().putBoolean(SharePref.isAviatorVisible, false);
                                    SharePref.getInstance().putBoolean(SharePref.isAviatorVerticalVisible, true);
                                }else{
                                    SharePref.getInstance().putBoolean(SharePref.isAviatorVisible, false);
                                    SharePref.getInstance().putBoolean(SharePref.isAviatorVerticalVisible, false);
                                }
                            } else {
                                Functions.showToast(Splashscreen.this, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                Functions.showToast(Splashscreen.this, "Something went wrong");
            }
        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                params.put("user_id", prefs.getString("user_id", ""));
//                params.put("token", prefs.getString("token", ""));
//                Log.d("paremter_java_kyc", "getParams: " + params);
//                return params;
//            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(Splashscreen.this).add(stringRequest);
    }

}
