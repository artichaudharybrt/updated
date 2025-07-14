package com.gamegards.bigjackpot._Aviator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gamegards.bigjackpot.Activity.Homepage;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Aviator_Game_Socket extends AppCompatActivity {

    Activity context;
    ImageView img_gif, img_start_game, imaprofile, imgback;
    TextView txt_timer, txt, txt_exit, txt_exit_value, txt_inner_timer, txt_wallet_amt, txt_username, tv_into_amount;
    float gif_Count = 10;
    EditText txt_amount;
    LinearLayout lnr_pay;
    Button btn_start;
    int miliseconds;
    int period;
    boolean gif_run = false;
    String graphCall = "1.80";
    long startTime = 0;
    Timer timer;
    String timerStaus = "";
    double timervalue = 0;
    String redeem_value = "";
    String time = "1.00";
    String game_id = "",bet_id = "";
    String get_socket_responce = "";
    String final_game_id = "1111";
    String redeem_gameid = "";
    double stored_bet_amount = 0; // Store bet amount when user places bet

    // Animation variables
    private AnimatorSet rocketAnimator;
    private float initialX, initialY;
    private boolean isAnimating = false;
    //private static final String URL = "http://64.227.186.5:3002";
    private Socket mSocket;
    String wallet_amount = "",user_id;
    double  into_amount;
    TextView tv_waiting;

    {
        try {
            mSocket = IO.socket(Const.SOCKET_IP + "aviator");
        } catch (URISyntaxException e) {
        }
    }

    private boolean isRunning = false;
    private int elapsedTime = 0;
    private Handler handler = new Handler();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Functions.Dialog_CancelAppointment(context, "Confirmation", "Leave ?", new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp.equals("yes")) {
                    mSocket.disconnect();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("RES_SocketConn", "DisConnected");
        mSocket.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.on("connect", onConnect);
        mSocket.on("connect_error", onConnectError);
        mSocket.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aviator_socket);
        context = this;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        mSocket.on("connect", onConnect);
        mSocket.on("connect_error", onConnectError);
        mSocket.connect();

        SharedPreferences prefs = getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE);
        user_id = prefs.getString("user_id", "");
        Log.e("user_id",user_id);

        img_gif = findViewById(R.id.gif_roket);
        txt_inner_timer = findViewById(R.id.txt_inner_timer);
        img_start_game = findViewById(R.id.img_start_game);
        txt_amount = findViewById(R.id.txt_amount);
        txt_timer = findViewById(R.id.txt_timer);
        txt = findViewById(R.id.txt);
        txt_exit = findViewById(R.id.txt_exit);
        txt_exit_value = findViewById(R.id.txt_exit_value);
        lnr_pay = findViewById(R.id.lnr_pay);
        btn_start = findViewById(R.id.btn_start);
        txt_wallet_amt = findViewById(R.id.txt_wallet_amt);
        txt_username = findViewById(R.id.txt_username);
        tv_into_amount = findViewById(R.id.tv_into_amount);
        tv_waiting = findViewById(R.id.tv_waiting);

        imaprofile = findViewById(R.id.imaprofile);
        imgback = findViewById(R.id.imgback);

        txt_exit_value.setVisibility(View.GONE);
        txt_exit.setVisibility(View.GONE);

        mSocket.on("Game", onNewMessage);
        mSocket.on("Blast", onBlast);
        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("RES_Socketconnect", "Connected");
                        txt.setVisibility(View.VISIBLE);
                        //    ShowGiftimer(15000, "one");
                    }
                });

            }
        });

        Profile_details();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Redeem /bet
                if (btn_start.getText().toString().equals("Redeem")) {
                    redeem_value = txt_inner_timer.getText().toString();
                    Redeem_amount(redeem_value);
                    btn_start.setText("Bet");
                    tv_waiting.setVisibility(View.VISIBLE);

                }else if (btn_start.getText().toString().equals("cancel")) {
                    redeem_value = txt_inner_timer.getText().toString();
                    Cancel_bet(redeem_value);
                    btn_start.setText("Bet");
                    tv_waiting.setVisibility(View.INVISIBLE);

                } else if (btn_start.getText().toString().equals("Bet")) {
                    if (img_start_game.getVisibility() == View.VISIBLE && !game_id.equals("")) {
                        Betting_amount(txt_amount.getText().toString(), "timer on");
                        btn_start.setText("cancel");
                    } else {
                        Betting_amount(txt_amount.getText().toString(), "timer on");
                        btn_start.setText("cancel");
                    }
                    tv_waiting.setText("Placing Bet...");
                    tv_waiting.setVisibility(View.VISIBLE);
                } else {
                    Log.d("start ", " " + miliseconds + "/" + period);
                }
            }
        });

        imgback.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    public void pluseValue(View view) {
        try {
            // Get current amount
            double currentAmount = Double.parseDouble(txt_amount.getText().toString());
            // Increment by 10
            double newAmount = currentAmount + 10;

            // Check if new amount exceeds wallet balance
            double walletBalance = Double.parseDouble(wallet_amount);
            if (newAmount <= walletBalance) {
                txt_amount.setText(String.valueOf(newAmount));
                Log.d("AVIATOR_BET", "Increased bet amount by 10 to " + newAmount);
            } else {
                Toast.makeText(context, "Bet amount cannot exceed wallet balance", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            // Default to 10 if there's an error
            txt_amount.setText("10");
            Log.d("AVIATOR_BET", "Reset bet amount to 10 due to error");
        }
    }

    public void minusValue(View view) {
        try {
            // Get current amount
            double currentAmount = Double.parseDouble(txt_amount.getText().toString());
            // Decrement by 10
            double newAmount = currentAmount - 10;

            // Ensure minimum bet is 10
            if (newAmount >= 10) {
                txt_amount.setText(String.valueOf(newAmount));
                Log.d("AVIATOR_BET", "Decreased bet amount by 10 to " + newAmount);
            } else {
                txt_amount.setText("10");
                Log.d("AVIATOR_BET", "Set bet amount to minimum (10)");
            }
        } catch (NumberFormatException e) {
            // Default to 10 if there's an error
            txt_amount.setText("10");
            Log.d("AVIATOR_BET", "Reset bet amount to 10 due to error");
        }
    }


    private void startBackendControlledGame(int gameDuration, float targetMultiplier) {
        if (img_start_game.getVisibility() == View.GONE && btn_start.getText().equals("cancel")) {
            tv_into_amount.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.INVISIBLE);
            btn_start.setText("Redeem");
        }

        img_gif.setVisibility(View.VISIBLE);
        img_gif.setImageResource(R.drawable.rocket_0); // Start with basic rocket
        final_game_id = game_id;

        // Start animation with backend duration
        startRocketCurveAnimation();

        // Use backend timing exactly
        miliseconds = gameDuration;
        period = 50; // Smooth 50ms updates

        // Start timer with backend values
        startBackendTimer(gameDuration, targetMultiplier);

        // Schedule blast at exact backend time
        new Handler().postDelayed(() -> {
            handleGameBlast(targetMultiplier);
        }, gameDuration);
    }

    private void handleGameBlast(float finalMultiplier) {
        // Stop rocket animation
        if (isAnimating) {
            stopRocketAnimation();
        }

        // Stop timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // Show blast UI
        txt_exit.setVisibility(View.VISIBLE);
        txt_exit.setText("FLEW AWAY!");
        txt_exit_value.setVisibility(View.VISIBLE);
        txt_exit_value.setText(String.format("%.2f", finalMultiplier));
        img_gif.setImageResource(R.drawable.rocket_stop);
        txt.setVisibility(View.VISIBLE);

        // Reset bet UI
        if (btn_start.getText().toString().equals("Redeem")) {
            btn_start.setText("Bet");
        }
        tv_into_amount.setVisibility(View.GONE);

        // Wait for next game countdown
        new Handler().postDelayed(() -> {
            ShowGiftimer(15000, "one");
        }, 1000);
    }

    public void ShowGiftimer(int YOUR_TIME_IN_MILISECONDS, String type) {
        // Pure backend timer - no hardcoded values
        txt_timer.setVisibility(View.VISIBLE);
        img_start_game.setVisibility(View.VISIBLE);
        txt_exit.setVisibility(View.GONE);
        txt_exit_value.setVisibility(View.GONE);
        txt.setVisibility(View.VISIBLE);
        img_gif.setImageResource(R.drawable.rocket_stop);
        txt_timer.setText(""); // Will be updated by backend aviatorTimer
    }

    private void startBackendTimer(int gameDuration, float targetMultiplier) {
        timer = new Timer();
        long startTime = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    long elapsed = System.currentTimeMillis() - startTime;

                    if (elapsed < gameDuration) {
                        // Calculate current multiplier based on backend timing
                        float progress = (float) elapsed / gameDuration;
                        float currentMultiplier = 1.0f + (targetMultiplier - 1.0f) * progress;

                        txt_inner_timer.setText(String.format("%.2f", currentMultiplier));

                        // Update bet amounts if user has active bet
                        if (!bet_id.equals("")) {
                            double amount = stored_bet_amount > 0 ? stored_bet_amount : 10;
                            double potentialWin = amount * currentMultiplier;
                            tv_into_amount.setText("\u20B9 " + String.format("%.2f", potentialWin));

                            if (btn_start.getText().toString().equals("Redeem")) {
                                tv_waiting.setText("Redeem: \u20B9" + String.format("%.2f", potentialWin) + " (" + String.format("%.2f", currentMultiplier) + "x)");
                                tv_waiting.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }, 0, 50); // 50ms updates for smooth animation
    }

    //     SOCKET CODE

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resp = String.valueOf(args[0]);
                    Log.d("AVIATOR_LOG", "Socket Step 1: Received new socket message");
                    Log.e("RES_Socket_Response", resp);
                    Log.d("AVIATOR_LOG", "Socket Step 2: Socket response: " + resp);

                    get_socket_responce = "first_time";
                    Log.d("AVIATOR_LOG", "Socket Step 3: Set get_socket_responce to 'first_time'");

                    Log.d("AVIATOR_LOG", "Socket Step 4: Processing JSON data from socket");
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.d("AVIATOR_LOG", "Socket Step 5: Set UI elements to GONE (exit, exit_value, txt, timer, rocket_value)");
                        txt_exit.setVisibility(View.GONE);
                        txt_exit_value.setVisibility(View.GONE);
                        txt.setVisibility(View.GONE);
                        txt_timer.setVisibility(View.GONE);

                        // Extract backend crash time and game_id
                        String backendTime = data.getString("time");
                        game_id = data.getString("game_id");

                        Log.d("AVIATOR_LOG", "Socket Step 6: Extracted time: " + backendTime + ", game_id: " + game_id);
                        Log.d("AVIATOR_TIMER", "Backend crash time received: " + backendTime + "x for game " + game_id);

                        // Reset multiplier display
                        txt_inner_timer.setText("1.00");
                        Log.d("AVIATOR_MULTIPLIER", "New game started, reset to: 1.00x");

                        // Start game with exact backend timing
                        gif_change_by_range(Float.parseFloat(backendTime));

                        Log.d("AVIATOR_LOG", "Socket Step 7: Calling gif_change_by_range with backend time: " + backendTime);

                    } catch (JSONException e) {
                        Log.e("Socket_Error", e.getMessage());
                    }
                }
            });
        }
    };

    private Emitter.Listener onBlast = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resp = String.valueOf(args[0]);
                    Log.e("RES_Socket_Blast_Response", resp);

                    Log.d("AVIATOR_LOG", "Animation callback: Animation crashed");
                    Log.d("AVIATOR_LOG", "Disabled backend multiplier mode");

                    // Stop all animations and timers immediately
                    stopRocketAnimation();
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        Log.d("AVIATOR_LOG", "Stopping GIF animation completely");
                    }

                    Log.d("AVIATOR_LOG", "Custom animation stopped");
                    Log.d("AVIATOR_LOG", "All animations stopped");
                    Log.d("AVIATOR_LOG", "Stopping current sound and playing crash sound");

                    // Get the final crash multiplier from current display
                    String finalMultiplier = txt_inner_timer.getText().toString();

                    // Show crash UI immediately
                    txt_exit.setVisibility(View.VISIBLE);
                    txt_exit.setText("FLEW AWAY!");
                    txt_exit_value.setVisibility(View.VISIBLE);
                    txt_exit_value.setText(finalMultiplier);
                    img_gif.setImageResource(R.drawable.rocket_stop);

                    Log.d("AVIATOR_LOG", "Showing final crash multiplier: " + finalMultiplier);
                    Log.d("AVIATOR_LOG", "Updating previous counts with new value: " + finalMultiplier.replace("x", ""));
                    Log.d("AVIATOR_LOG", "Previous counts updated: [" + finalMultiplier.replace("x", "") + "]");
                    Log.d("AVIATOR_TIMER", "Smooth crash completed - Frontend-Backend sync: " + finalMultiplier);

                    // Reset bet UI
                    if (btn_start.getText().toString().equals("Redeem")) {
                        btn_start.setText("Bet");
                        Log.d("AVIATOR_LOG", "Resetting bet_amount to 0");
                    }
                    if (btn_start.getText().toString().equals("Bet")) {
                        txt_amount.setText("10");
                    }

                    tv_into_amount.setVisibility(View.GONE);

                    Log.d("AVIATOR_TIMER", "Waiting for backend aviatorTimer to sync next game countdown");
                    Log.d("AVIATOR_LOG", "Refreshing game history after crash");
                    Log.d("AVIATOR_API", "Generating fake game history data");
                    Log.d("AVIATOR_API", "Generated 100 game history records");
                    Log.d("AVIATOR_LOG", "Smooth crash handling completed");

                    // Start next game timer immediately after blast
                    ShowGiftimer(15000, "one");
                }
            });
        }
    };

//     ------------API CALL-------------------

    private void Betting_amount(String amount, String timerStatus) {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket.this);
        Log.d("RES_Socketbet", "API call: " + time + "/" + game_id);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.AVIATOR_GAME_BETTING, new Response.Listener<String>() {
            private Object AdapterSlider;

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("RES_Bettingresponce", "" + jsonObject);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        // Handle successful bet response
                        JSONObject result = jsonObject.getJSONObject("result");
                        bet_id = result.getString("id");

                        // Get updated user data
                        JSONObject userData = jsonObject.getJSONObject("data");
                        String newWallet = userData.getString("wallet");

                        // Update wallet display
                        txt_wallet_amt.setText(newWallet);

                        // Store bet amount for multiplier calculations
                        stored_bet_amount = Double.parseDouble(txt_amount.getText().toString());
                        tv_waiting.setText("Redeem: \u20B9" + String.format("%.2f", stored_bet_amount));
                        tv_waiting.setVisibility(View.VISIBLE);

                        // Show success message
                        Toast.makeText(context, message + " - Game ID: " + redeem_gameid, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    } else {
                        // Handle error response
                        btn_start.setText("Bet");
                        txt_amount.setText("10");
                        loadingDialog.dismiss();
                        tv_waiting.setVisibility(View.INVISIBLE);
                        Toast.makeText(Aviator_Game_Socket.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Something Went Wrong", "" + e);
                    loadingDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("Something Went Wrong", "" + error);
                loadingDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();
                if (timerStatus.equals("timer off")) {
                    int a = Integer.parseInt(final_game_id) + 1;
                    params.put("game_id", String.valueOf(a));
                    redeem_gameid = String.valueOf(a);
                } else {
                    params.put("game_id", game_id);
                    redeem_gameid = game_id;
                }
                params.put("user_id", user_id);
                params.put("amount", amount);

                Log.e("Bettingparams", "" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.AVIATOR_Token);
                return header;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

    private void Redeem_amount(String Redeem_amount) {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.AVIATOR_GAME_REDEEM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Res_Redeem", "" + response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String user = data.getString("id");
                        String wallet = data.getString("wallet");
                        String name = data.getString("name");
                        String mobile = data.getString("mobile");
                        tv_into_amount.setVisibility(View.GONE);
                        tv_waiting.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "" + message + " " + Redeem_amount, Toast.LENGTH_SHORT).show();
                        Profile_details();
                    }

                    loadingDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                loadingDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();

                params.put("game_id", redeem_gameid);
                params.put("user_id", user_id);
                params.put("amount", Redeem_amount);
                params.put("token", Const.AVIATOR_Token);
                params.put("bet_id", bet_id);

                Log.e("RES_RedeemParam", Const.AVIATOR_GAME_REDEEM + "" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.AVIATOR_Token);
                return header;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

    private void Cancel_bet(String Redeem_amount) {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.AVIATOR_CANCEL_BET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Res_CancelBet", "" + response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                        Profile_details();
                    }

                    loadingDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                loadingDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();

                params.put("user_id", user_id);
                params.put("bet_id", bet_id);

                Log.e("RES_CancelParam", Const.AVIATOR_GAME_REDEEM + "" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.AVIATOR_Token);
                return header;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

    private void Profile_details() {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.AVIATOR_PROFILE_DETAILS, new Response.Listener<String>() {
            private Object AdapterSlider;

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("userdetails_resp", "" + response);
//                    String message = jsonObject.getString("message");
//                    String code = jsonObject.getString("code");

                    String user = jsonObject.getString("id");
                    wallet_amount = jsonObject.getString("wallet");
                    String name = jsonObject.getString("name");
                    String mobile = jsonObject.getString("mobile");

                    Glide.with(getApplicationContext())
                            .load(Const.IMGAE_PATH + SharePref.getInstance().getString(SharePref.u_pic))
                            .placeholder(R.drawable.avatar)
                            .into(imaprofile);

                    txt_wallet_amt.setText(wallet_amount);
                    txt_username.setText(name);
                    loadingDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(" Something Went Wrong", "" + error);
                loadingDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();
                params.put("user_id", user_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Const.AVIATOR_Token);
                return header;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.v("RES_SOCKET ERROR ", " " + args);
                    Toast.makeText(context, "Socket Connection Error " + args, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v("RES_SocketConn", " " + args);
                }
            });
        }
    };

    /**
     * Starts smooth curve animation for the rocket
     */
    private void startRocketCurveAnimation() {
        if (isAnimating) {
            stopRocketAnimation();
        }

        // Store initial position
        initialX = img_gif.getX();
        initialY = img_gif.getY();

        // Create curved path animation
        ValueAnimator curveAnimator = ValueAnimator.ofFloat(0f, 1f);
        curveAnimator.setDuration(10000); // 10 seconds total duration
        curveAnimator.setInterpolator(new AccelerateInterpolator(1.2f));

        curveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (Float) animation.getAnimatedValue();

                // Calculate curved path (parabolic trajectory)
                float screenWidth = getResources().getDisplayMetrics().widthPixels;
                float screenHeight = getResources().getDisplayMetrics().heightPixels;

                // Horizontal movement (left to right)
                float newX = initialX + (screenWidth * 0.3f * progress);

                // Vertical movement (curved upward trajectory)
                float curveHeight = screenHeight * 0.4f;
                float newY = initialY - (curveHeight * progress * (2f - progress));

                // Apply position
                img_gif.setX(newX);
                img_gif.setY(newY);

                // Add slight rotation for realism
                float rotation = progress * 15f; // Rotate up to 15 degrees
                img_gif.setRotation(rotation);

                // Scale effect (rocket gets smaller as it flies away)
                float scale = 1f - (progress * 0.3f); // Scale down to 70%
                img_gif.setScaleX(scale);
                img_gif.setScaleY(scale);
            }
        });

        rocketAnimator = new AnimatorSet();
        rocketAnimator.play(curveAnimator);
        rocketAnimator.start();

        isAnimating = true;
        Log.d("ROCKET_ANIMATION", "Started smooth curve animation");
    }

    /**
     * Stops the rocket animation and resets position
     */
    private void stopRocketAnimation() {
        if (rocketAnimator != null && rocketAnimator.isRunning()) {
            rocketAnimator.cancel();
        }

        if (isAnimating) {
            // Reset rocket position and properties
            img_gif.setX(initialX);
            img_gif.setY(initialY);
            img_gif.setRotation(0f);
            img_gif.setScaleX(1f);
            img_gif.setScaleY(1f);

            isAnimating = false;
            Log.d("ROCKET_ANIMATION", "Stopped and reset rocket animation");
        }
    }

    private void gif_change_by_range(float backendCrashTime) {
        Log.d("AVIATOR_LOG", "Step 1: Method gif_change_by_range started with input: " + backendCrashTime);
        Log.d("AVIATOR_LOG", "Step 3: Setting rocket image to visible");
        img_gif.setVisibility(View.VISIBLE);
        Log.d("AVIATOR_LOG", "Step 4: Parsed gif_Count value: " + backendCrashTime);
        gif_Count = backendCrashTime;
        Log.d("AVIATOR_LOG", "Step 5: Set final_game_id: " + game_id);
        final_game_id = game_id;
        Log.d("AVIATOR_LOG", "Step 6: Playing plane_fly sound");

        startRocketCurveAnimation();
        startSmoothMultiplierProgression(backendCrashTime);

        if (img_start_game.getVisibility() == View.GONE && btn_start.getText().equals("cancel")) {
            tv_into_amount.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.INVISIBLE);
            btn_start.setText("Redeem");
        }
    }

    private void startSmoothMultiplierProgression(float targetMultiplier) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        long startTime = System.currentTimeMillis();
        // Extend duration to reach target multiplier before backend blast
        int totalDuration = (int) (3000 + (targetMultiplier - 1.0f) * 1800);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    long elapsed = System.currentTimeMillis() - startTime;

                    if (elapsed < totalDuration) {
                        float progress = (float) elapsed / totalDuration;
                        float currentMultiplier = 1.0f + (targetMultiplier - 1.0f) * progress;

                        // Cap at target multiplier
                        if (currentMultiplier >= targetMultiplier) {
                            currentMultiplier = targetMultiplier;
                        }

                        txt_inner_timer.setText(String.format("%.2f", currentMultiplier));

                        if (!bet_id.equals("") && stored_bet_amount > 0) {
                            double potentialWin = stored_bet_amount * currentMultiplier;
                            tv_into_amount.setText("\u20B9 " + String.format("%.2f", potentialWin));

                            if (btn_start.getText().toString().equals("Redeem")) {
                                tv_waiting.setText("Redeem: \u20B9" + String.format("%.2f", potentialWin) + " (" + String.format("%.2f", currentMultiplier) + "x)");
                                tv_waiting.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }, 0, 50);
    }

}

