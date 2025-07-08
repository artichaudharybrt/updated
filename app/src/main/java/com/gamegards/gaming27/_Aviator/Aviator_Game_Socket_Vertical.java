package com.gamegards.gaming27._Aviator;

import static com.gamegards.gaming27.Activity.Homepage.MY_PREFS_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gamegards.gaming27.Adapter.AviatorMyHistoryAdapter;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Interface.ApiRequest;
import com.gamegards.gaming27.Interface.Callback;
import com.gamegards.gaming27.MyAccountDetails.MyWinnigmodel;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.Utils.Functions;
import com.gamegards.gaming27.Utils.SharePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Aviator_Game_Socket_Vertical extends AppCompatActivity {

    Activity context;
    ImageView img_gif, img_start_game, imaprofile, imgback;
    TextView txt_timer, txt, txt_exit, txt_exit_value, txt_inner_timer, txt_wallet_amt, txt_username, tv_into_amount;
    TextView txt_rocket_value; // Added for displaying value on rocket
    AviatorAnimationView aviatorAnimationView; // Custom animation view
    // TextViews for previous gif_Count values
    TextView txt_prev_count1, txt_prev_count2, txt_prev_count3, txt_prev_count4, txt_prev_count5;
    // ArrayList to store previous gif_Count values
    ArrayList<String> previousCounts = new ArrayList<>();
    float gif_Count = 8;
    EditText txt_amount;
    LinearLayout lnr_pay;
    TextView btn_start;
    // Removed local timer variables - using backend timer only
    boolean gif_run = false;
    String graphCall = "1.80";
    long startTime = 0;
    // Timer to simulate new data coming in
    private Timer fakeDataTimer;
    private final int FAKE_DATA_UPDATE_INTERVAL = 3000; // 3 seconds
    private final Random random = new Random();
    private final String[] fakeNames = {
            "John D***", "Alice K***", "Bob S***", "Emma W***", "David L***",
            "Sophia R***", "Michael T***", "Olivia P***", "James B***", "Ava M***",
            "William H***", "Isabella C***", "Benjamin F***", "Mia J***", "Lucas G***",
            "Charlotte N***", "Henry V***", "Amelia S***", "Alexander D***", "Harper L***"
    };
    String timerStaus = "";
    // Removed timervalue - using backend timer only
    String redeem_value = "";
    String time = "1.00";
    String game_id = "",bet_id = "";
    String get_socket_responce = "";
    String final_game_id = "1111";
    String redeem_gameid = "";
    float currentBackendMultiplier = 1.0f; // Store current multiplier from backend
    //private static final String URL = "http://64.227.186.5:3002";
    private Socket mSocket;
    String wallet_amount = "",user_id;
    double  into_amount;
    TextView tv_waiting, tv_allBets, tv_myBets;

    ArrayList<MyWinnigmodel> myWinnigmodelArrayList;

    {
        try {
            mSocket = IO.socket(Const.SOCKET_IP + "aviator");
        } catch (URISyntaxException e) {

        }
    }

    private boolean isRunning = false;
    private int elapsedTime = 0;
    private Handler handler = new Handler();

    RecyclerView rec_winning;
    AviatorMyHistoryAdapter myWinningAdapte;
    String str_selected_bet="0",next_game_id,bet_amount="0";
    MediaPlayer soundPlayer;

    int time_remaining;
    boolean canbet = false;

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
        // Stop all timers
        stopFakeDataTimer();
        stopGameHistoryRefreshTimer();
        // Disconnect socket
        mSocket.disconnect();
        Log.d("AVIATOR_API", "Activity destroyed, stopped all timers and disconnected socket");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop all timers
        stopFakeDataTimer();
        stopGameHistoryRefreshTimer();
        // Disconnect socket
        Log.v("RES_SocketConn", "DisConnected");
        mSocket.disconnect();
        Log.d("AVIATOR_API", "Activity paused, stopped all timers and disconnected socket");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reconnect socket
        mSocket.on("connect", onConnect);
        mSocket.on("connect_error", onConnectError);
        mSocket.connect();

        // Refresh data based on current tab
        if (str_selected_bet.equals("0")) {
            // All bets view - refresh game history
            CALL_API_Game_History();
            Log.d("AVIATOR_API", "Activity resumed, refreshing game history");
        } else {
            // My bets view - refresh my bets
            CALL_API_MyBet_History();
            Log.d("AVIATOR_API", "Activity resumed, refreshing my bet history");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aviator_socket_vertical);
        context = this;

        // Set touch listener to hide keyboard
        findViewById(R.id.root_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        mSocket.on("connect", onConnect);
        mSocket.on("connect_error", onConnectError);
        mSocket.connect();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id = prefs.getString("user_id", "");
        Log.e("user_id",user_id);

        img_gif = findViewById(R.id.gif_roket);
        aviatorAnimationView = findViewById(R.id.aviator_animation_view);
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
        txt_rocket_value = findViewById(R.id.txt_rocket_value);

        // Initialize previous count TextViews
        txt_prev_count1 = findViewById(R.id.txt_prev_count1);
        txt_prev_count2 = findViewById(R.id.txt_prev_count2);
        txt_prev_count3 = findViewById(R.id.txt_prev_count3);
        txt_prev_count4 = findViewById(R.id.txt_prev_count4);
        txt_prev_count5 = findViewById(R.id.txt_prev_count5);

        imaprofile = findViewById(R.id.imaprofile);
        imgback = findViewById(R.id.imgback);

        tv_allBets = findViewById(R.id.tv_allBets);
        tv_myBets = findViewById(R.id.tv_myBets);

        rec_winning = findViewById(R.id.rec_winning);
        rec_winning.setLayoutManager(new LinearLayoutManager(context));

        txt_exit_value.setVisibility(View.GONE);
        txt_exit.setVisibility(View.GONE);

        mSocket.on("Game", onNewMessage);
        mSocket.on("Blast", onBlast);
        mSocket.on("next_game_id", nextGameId);
        mSocket.on("aviator_timer", aviatorTimer);
        mSocket.on("current_multiplier", onCurrentMultiplier); // Listen for real-time multiplier updates

        if (next_game_id == null) {
            btn_start.setText("Wait");
            btn_start.setEnabled(false);
        }

        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("RES_Socketconnect", "Connected");
//                        txt.setVisibility(View.VISIBLE);
//                        txt.setText("Next game starts soon");
                        //   ShowGiftimer(15000, "one");
                    }
                });

            }
        });

        Profile_details();
        CALL_API_Game_History();

        tv_allBets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_allBets.setBackground(ContextCompat.getDrawable(context, R.drawable.bet_selected));
                tv_myBets.setBackgroundResource(0);
                str_selected_bet="0";

                // Stop any existing timers
                stopGameHistoryRefreshTimer();

                // Call the real API for game history
                CALL_API_Game_History();

                Log.d("AVIATOR_API", "Switched to All Bets tab, refreshing game history");
            }
        });

        tv_myBets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_myBets.setBackground(ContextCompat.getDrawable(context, R.drawable.bet_selected));
                tv_allBets.setBackgroundResource(0);
                str_selected_bet="1";

                // Stop any existing timers
                stopGameHistoryRefreshTimer();

                // Call the real API for my bet history
                CALL_API_MyBet_History();

                Log.d("AVIATOR_API", "Switched to My Bets tab, refreshing my bet history");
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Redeem /bet
                if (btn_start.getText().toString().equals("Redeem")) {
                    // Get multiplier from the displayed rocket value
                    String rocketText = txt_rocket_value.getText().toString();
                    if (rocketText.contains("x")) {
                        redeem_value = rocketText.replace("x", "");
                    } else {
                        redeem_value = String.format("%.2f", currentBackendMultiplier);
                    }
                    Log.d("REDEEM_CLICK", "Redeem clicked - rocket display: " + rocketText + ", sending: " + redeem_value);
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
                    tv_waiting.setVisibility(View.VISIBLE);
                } else {
                    Log.d("start ", "Using backend timer only");
                }
            }
        });

        findViewById(R.id.txt_bet1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set exact amount to 100
                txt_amount.setText("100");

                // Log the action
                Log.d("AVIATOR_BET", "Set bet amount to 100");
            }
        });

        findViewById(R.id.txt_bet2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set exact amount to 200
                txt_amount.setText("200");

                // Log the action
                Log.d("AVIATOR_BET", "Set bet amount to 200");
            }
        });

        findViewById(R.id.txt_bet3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set exact amount to 500
                txt_amount.setText("500");

                // Log the action
                Log.d("AVIATOR_BET", "Set bet amount to 500");
            }
        });

        findViewById(R.id.txt_bet4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set exact amount to 1000
                txt_amount.setText("1000");

                // Log the action
                Log.d("AVIATOR_BET", "Set bet amount to 1000");
            }
        });
        imgback.setOnClickListener(v -> {
            onBackPressed();
        });

        // Add text change listener for manual input
        txt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Allow empty input while typing
                if (s.length() == 0) {
                    return;
                }

                try {
                    // Only validate if there's a valid number
                    double amount = Double.parseDouble(s.toString());
                    double walletBalance = Double.parseDouble(wallet_amount);

                    // Only show warning if amount exceeds wallet balance
                    if (amount > walletBalance) {
                        Toast.makeText(context, "Bet amount cannot exceed wallet balance", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Allow invalid input while typing
                }
            }
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


    public void gif_change_by_range(String gif_Count_str) {
        Log.d("AVIATOR_LOG", "Step 1: Method gif_change_by_range started with input: " + gif_Count_str);

        // Check UI state and update buttons if needed
        if (img_start_game.getVisibility() == View.GONE && btn_start.getText().equals("cancel")) {
            Log.d("AVIATOR_LOG", "Step 2: Changing button state from 'cancel' to 'Redeem'");
            tv_into_amount.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.INVISIBLE);
            btn_start.setText("Redeem");
            btn_start.setEnabled(true); // Ensure redeem button is enabled
        }

        // Check if we have an active bet and should enable redeem (only if bet_id is not empty)
        if (bet_id != null && !bet_id.isEmpty() && !bet_amount.equals("0") && !btn_start.getText().equals("Redeem")) {
            Log.d("AVIATOR_LOG", "Step 2b: Active bet detected, enabling Redeem button");
            btn_start.setText("Redeem");
            btn_start.setEnabled(true);
            tv_into_amount.setVisibility(View.VISIBLE);
        }

        // Make rocket image visible (fallback)
        Log.d("AVIATOR_LOG", "Step 3: Setting rocket image to visible");
        img_gif.setVisibility(View.GONE); // Hide GIF view
        aviatorAnimationView.setVisibility(View.VISIBLE); // Show custom animation

        // Parse the time value to get gif_Count
        float gif_Count = Float.parseFloat(time);
        Log.d("AVIATOR_LOG", "Step 4: Parsed gif_Count value: " + gif_Count);

        // Set game ID
        final_game_id = game_id;
        Log.d("AVIATOR_LOG", "Step 5: Set final_game_id: " + final_game_id);

        // Play sound effect
        Log.d("AVIATOR_LOG", "Step 6: Playing plane_fly sound");
        playySound(R.raw.plane_fly);

        // Log socket information
        Log.d("AVIATOR_LOG", "Step 7: Socket data - time: " + time + ", game_id: " + game_id);

        // Make sure any previous animation is stopped
        stopGifAnimation();
        aviatorAnimationView.resetAnimation();

        // Calculate animation duration based on backend crash time
        // Backend sends the crash multiplier, we need to calculate appropriate duration
        long animationDuration = calculateAnimationDuration(gif_Count);

        Log.d("AVIATOR_LOG", "Step 7a: Calculated animation duration based on crash multiplier " + gif_Count + "x: " + animationDuration + "ms");

        // Display the rocket value text starting with backend multiplier
        txt_rocket_value.setText(String.format("%.2fx", gif_Count));
        txt_rocket_value.setVisibility(View.VISIBLE);
        Log.d("AVIATOR_LOG", "Step 7b: Set initial rocket value to: " + String.format("%.2fx", gif_Count));

        // Start custom animation
        aviatorAnimationView.startAnimation(gif_Count, animationDuration);
        Log.d("AVIATOR_LOG", "Step 8: Started custom animation with target: " + gif_Count);

        // Set up animation callback
        aviatorAnimationView.setAnimationCallback(new AviatorAnimationView.AnimationCallback() {
            @Override
            public void onAnimationStart() {
                Log.d("AVIATOR_LOG", "Animation callback: Animation started");
            }

            @Override
            public void onAnimationUpdate(float currentMultiplier) {
                // Update the rocket value text with animation multiplier
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_rocket_value.setText(String.format("%.2fx", currentMultiplier));
                    }
                });
            }

            @Override
            public void onAnimationComplete(float finalMultiplier) {
                Log.d("AVIATOR_LOG", "Animation callback: Animation completed with multiplier: " + finalMultiplier);
            }

            @Override
            public void onAnimationCrash() {
                Log.d("AVIATOR_LOG", "Animation callback: Animation crashed");
            }
        });
    }

    /**
     * Calculate exact millisecond duration from multiplier with slower zone
     */
    private long calculateAnimationDuration(float crashMultiplier) {
        long duration;

        if (crashMultiplier >= 1.10f && crashMultiplier <= 3.50f) {
            // Slower zone: 1.10x - 3.50x gets fixed 10 seconds for slower UI
            duration = 5000; // Fixed 10 seconds for slower UI experience

            Log.d("AVIATOR_TIMER", "SLOW ZONE: " + String.format("%.2f", crashMultiplier) +
                    "x -> " + duration + "ms (fixed 10 seconds - slower UI)");
        } else {
            // Normal speed for other ranges
            duration = (long) ((crashMultiplier - 1.0f) * 1000);
            Log.d("AVIATOR_TIMER", "Normal speed: " + String.format("%.2f", crashMultiplier) +
                    "x -> " + duration + "ms");
        }

        duration = Math.max(50, duration); // Minimum 50ms
        return duration;
    }



    // Helper method to completely stop GIF animation
    private void stopGifAnimation() {
        Log.d("AVIATOR_LOG", "Stopping GIF animation completely");

        // Force stop any ongoing animations
        img_gif.clearAnimation();

        // Set the default stopped rocket image
        img_gif.setImageResource(R.drawable.rocket_stop);

        // Force a redraw of the image view
        img_gif.invalidate();

        // For GifImageView, we need to ensure it's not playing
        try {
            // This is a workaround to ensure the GIF animation stops
            // First set to null to clear any current GIF
            img_gif.setImageDrawable(null);
            // Then set the static image
            img_gif.setImageResource(R.drawable.rocket_stop);
            // Force another redraw
            img_gif.invalidate();
        } catch (Exception e) {
            Log.e("AVIATOR_LOG", "Error stopping GIF animation: " + e.getMessage());
        }

        // Also stop custom animation
        if (aviatorAnimationView != null) {
            aviatorAnimationView.stopAnimation();
            Log.d("AVIATOR_LOG", "Custom animation stopped");
        }

        Log.d("AVIATOR_LOG", "All animations stopped");
    }

    /**
     * Updates the previous gif_Count values display
     * @param newCount The new count value to add to the history
     */
    private void updatePreviousCounts(String newCount) {
        Log.d("AVIATOR_LOG", "Updating previous counts with new value: " + newCount);

        // Add the new count to the beginning of the list
        previousCounts.add(0, newCount);

        // Keep only the last 5 values
        while (previousCounts.size() > 5) {
            previousCounts.remove(previousCounts.size() - 1);
        }

        // Update the TextViews
        if (previousCounts.size() >= 1) {
            txt_prev_count1.setText(previousCounts.get(0) + "x");
        }

        if (previousCounts.size() >= 2) {
            txt_prev_count2.setText(previousCounts.get(1) + "x");
        }

        if (previousCounts.size() >= 3) {
            txt_prev_count3.setText(previousCounts.get(2) + "x");
        }

        if (previousCounts.size() >= 4) {
            txt_prev_count4.setText(previousCounts.get(3) + "x");
        }

        if (previousCounts.size() >= 5) {
            txt_prev_count5.setText(previousCounts.get(4) + "x");
        }

        Log.d("AVIATOR_LOG", "Previous counts updated: " + previousCounts.toString());
    }

    // This method has been replaced by reset(0, exit_value)
    // Keeping this stub for backward compatibility
    private void resetAnimationImmediately(String exit_value) {
        Log.d("AVIATOR_LOG", "Immediate Reset: Using reset(0, exit_value) instead");
        reset(0, exit_value);
    }

    public void reset(int YOUR_TIME_IN_MILISECONDS, String exit_value) {
        Log.d("AVIATOR_LOG", "Reset Step 1: Method reset called with parameters - milliseconds: " + YOUR_TIME_IN_MILISECONDS + ", exit_value: " + exit_value);

        // If YOUR_TIME_IN_MILISECONDS is 0, it means we're being called from the timer completion
        // In this case, we should stop the animation immediately
        if (YOUR_TIME_IN_MILISECONDS == 0) {
            Log.d("AVIATOR_LOG", "Reset Step 1b: Called from timer completion, stopping animation immediately");

            // No local timer to stop - using backend timer only
            Log.d("AVIATOR_LOG", "Reset Step 1c: No local timer to cancel - using backend timer");

            // Reset variables
            into_amount = 0;

            // Update UI
            txt_inner_timer.setText("");
            txt_rocket_value.setVisibility(View.GONE);

            // Show completion UI
            txt_exit.setVisibility(View.VISIBLE);
            txt_exit.setText("FLEW AWAY !");

            txt_exit_value.setVisibility(View.VISIBLE);
            txt_exit_value.setText(exit_value);

            // Update the previous counts display
            updatePreviousCounts(exit_value);

            // Stop the GIF animation completely
            stopGifAnimation();

            txt.setVisibility(View.VISIBLE);
            txt.setText("Next game starts in ");

            Log.d("AVIATOR_LOG", "Reset Step 1d: Animation stopped and completion UI shown");
            return;
        }

        // For normal reset calls (not from timer completion)
        // Calculate duration based on the crash multiplier from backend
        float crashMultiplier = 1.0f;
        try {
            crashMultiplier = Float.parseFloat(exit_value.replace("x", ""));
        } catch (NumberFormatException e) {
            Log.e("AVIATOR_LOG", "Error parsing exit_value: " + exit_value + ", using default 1.0x");
        }

        long calculatedDuration = calculateAnimationDuration(crashMultiplier);
        Log.d("AVIATOR_LOG", "Reset Step 1a: Using calculated animation duration based on " + crashMultiplier + "x: " + calculatedDuration + "ms");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // No local timer to stop - using backend timer only
                Log.d("AVIATOR_LOG", "Reset Step 3: No local timer to stop - using backend timer");

                // Reset variables
                into_amount = 0;

                txt_inner_timer.setText("");
                Log.d("AVIATOR_LOG", "Reset Step 6: Cleared inner timer text");

                // Make sure rocket value text is hidden
                txt_rocket_value.setVisibility(View.GONE);
                Log.d("AVIATOR_LOG", "Reset Step 6b: Hidden rocket value text");

                // Always show game completion UI
                Log.d("AVIATOR_LOG", "Reset Step 8: Showing game completion UI");

                txt_exit.setVisibility(View.VISIBLE);
                txt_exit.setText("FLEW AWAY !");
                Log.d("AVIATOR_LOG", "Reset Step 9: Set exit text to 'FLEW AWAY!'");

                txt_exit_value.setVisibility(View.VISIBLE);
                txt_exit_value.setText(exit_value);
                Log.d("AVIATOR_LOG", "Reset Step 10: Set exit value text to: " + exit_value);

                // Update the previous counts display
                updatePreviousCounts(exit_value);
                Log.d("AVIATOR_LOG", "Reset Step 10a: Updated previous counts display with: " + exit_value);

                // Stop the GIF animation completely
                stopGifAnimation();

                txt_rocket_value.setVisibility(View.GONE);
                Log.d("AVIATOR_LOG", "Reset Step 11: Stopped GIF animation completely and hid rocket value");

                txt.setVisibility(View.VISIBLE);
                txt.setText("Next game starts in ");
                Log.d("AVIATOR_LOG", "Reset Step 12: Set 'Next game starts in' text visible");

                Log.d("AVIATOR_LOG", "Reset Step 13: Reset method completed");
            }
        }, calculatedDuration); // Use the calculated duration based on backend crash time

        Log.d("AVIATOR_LOG", "Reset Step 14: Scheduled reset handler with calculated delay: " + calculatedDuration + "ms");
    }

    public void ShowGiftimer(int YOUR_TIME_IN_MILISECONDS, String type) {
        // Pure backend timer - no hardcoded values
        txt_timer.setVisibility(View.VISIBLE);
        txt_timer.setText(""); // Will be updated by backend aviatorTimer
    }

    // Removed local timer - using backend timer only
    // Backend sends current_multiplier events for real-time updates

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
                        txt_rocket_value.setVisibility(View.GONE);

                        // Extract backend crash time and game_id
                        time = data.getString("time");
                        game_id = data.getString("game_id");

                        Log.d("AVIATOR_LOG", "Socket Step 6: Extracted time: " + time + ", game_id: " + game_id);
                        Log.d("AVIATOR_TIMER", "Backend crash time received: " + time + "x for game " + game_id);

                        // Reset multiplier to 1.0 for new game
                        currentBackendMultiplier = 1.0f;
                        Log.d("AVIATOR_MULTIPLIER", "New game started, reset to: 1.00x");

                        // Start the animation with backend timing
                        Log.d("AVIATOR_LOG", "Socket Step 7: Calling gif_change_by_range with backend time: " + time);
                        gif_change_by_range(time);

                        // Enable precise backend multiplier mode for real-time updates
                        if (aviatorAnimationView != null) {
                            aviatorAnimationView.setUseBackendMultiplier(true);
                            Log.d("AVIATOR_LOG", "Socket Step 7a: Enabled precise backend multiplier mode");
                        }

                    } catch (JSONException e) {
                        Log.e("Socket_Error", e.getMessage());
                    }
                }
            });
        }
    };

    // Blast Code - Handle smooth crash animation

    private Emitter.Listener  onBlast = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("AVIATOR_LOG", "Blast Step 1: Received blast event from socket - preparing smooth crash");

                    // Log complete args array
                    Log.e("BLAST_ARGS_COUNT", "Total args received: " + args.length);
                    for (int i = 0; i < args.length; i++) {
                        Log.e("BLAST_ARG_" + i, "Arg[" + i + "]: " + String.valueOf(args[i]));
                        Log.e("BLAST_ARG_TYPE_" + i, "Type[" + i + "]: " + (args[i] != null ? args[i].getClass().getSimpleName() : "null"));
                    }

                    String resp = String.valueOf(args[0]);
                    Log.e("RES_Blast_Response", resp);
                    Log.d("AVIATOR_LOG", "Blast Step 2: Blast response: " + resp);

                    // Try to parse as JSON if possible
                    try {
                        if (resp.startsWith("{") && resp.endsWith("}")) {
                            JSONObject blastJson = new JSONObject(resp);
                            Log.e("BLAST_JSON_PARSED", "Parsed JSON: " + blastJson.toString(2));

                            // Log each key-value pair
                            java.util.Iterator<String> keys = blastJson.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Object value = blastJson.get(key);
                                Log.e("BLAST_JSON_" + key.toUpperCase(), key + ": " + value);
                            }
                        } else {
                            Log.e("BLAST_NOT_JSON", "Response is not JSON format: " + resp);
                        }
                    } catch (JSONException e) {
                        Log.e("BLAST_JSON_ERROR", "Failed to parse blast response as JSON: " + e.getMessage());
                    }

                    // Check if blast response contains timer/multiplier data
                    float blastMultiplier = 0.0f;
                    String blastGameId = "";
                    long blastTimestamp = 0;

                    try {
                        if (resp.startsWith("{") && resp.endsWith("}")) {
                            JSONObject blastData = new JSONObject(resp);

                            // Check for common blast timer fields
                            if (blastData.has("multiplier")) {
                                blastMultiplier = (float) blastData.getDouble("multiplier");
                                Log.e("BLAST_MULTIPLIER_FOUND", "Blast contains multiplier: " + blastMultiplier + "x");
                            }
                            if (blastData.has("time")) {
                                String blastTime = blastData.getString("time");
                                Log.e("BLAST_TIME_FOUND", "Blast contains time: " + blastTime);
                            }
                            if (blastData.has("game_id")) {
                                blastGameId = blastData.getString("game_id");
                                Log.e("BLAST_GAME_ID_FOUND", "Blast contains game_id: " + blastGameId);
                            }
                            if (blastData.has("timestamp")) {
                                blastTimestamp = blastData.getLong("timestamp");
                                Log.e("BLAST_TIMESTAMP_FOUND", "Blast contains timestamp: " + blastTimestamp);
                            }
                            if (blastData.has("crash_time")) {
                                String crashTime = blastData.getString("crash_time");
                                Log.e("BLAST_CRASH_TIME_FOUND", "Blast contains crash_time: " + crashTime);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("BLAST_PARSE_ERROR", "Error parsing blast JSON: " + e.getMessage());
                    }

                    // Don't immediately crash - let animation complete smoothly first
                    // Calculate remaining time for smooth crash
                    float finalMultiplier = 1.0f;
                    try {
                        // Use blast multiplier if available, otherwise use stored time
                        if (blastMultiplier > 0) {
                            finalMultiplier = blastMultiplier;
                            Log.d("AVIATOR_TIMER", "Using blast multiplier: " + finalMultiplier + "x");
                        } else {
                            finalMultiplier = Float.parseFloat(time);
                            Log.d("AVIATOR_TIMER", "Using stored crash multiplier: " + finalMultiplier + "x");
                        }
                    } catch (NumberFormatException e) {
                        Log.e("AVIATOR_TIMER", "Error parsing multiplier, using 1.0x");
                        finalMultiplier = 1.0f;
                    }

                    // Calculate how much time the animation needs to reach the crash point smoothly
                    long animationDuration = calculateAnimationDuration(finalMultiplier);
                    long currentTime = System.currentTimeMillis();
                    long animationStartTime = (aviatorAnimationView != null ? aviatorAnimationView.getAnimationStartTime() : currentTime);
                    long elapsedTime = currentTime - animationStartTime;
                    long remainingTime = Math.max(0, animationDuration - elapsedTime);

                    Log.d("AVIATOR_TIMER", "Smooth crash timing - Duration: " + animationDuration + "ms, Elapsed: " + elapsedTime + "ms, Remaining: " + remainingTime + "ms");
                    Log.e("BLAST_TIMING_DETAILS", "Current: " + currentTime + ", Start: " + animationStartTime + ", Final Multiplier: " + finalMultiplier + "x");

                    // Log blast timestamp vs current time if available
                    if (blastTimestamp > 0) {
                        long timeDiff = currentTime - blastTimestamp;
                        Log.e("BLAST_TIME_DIFF", "Blast timestamp diff: " + timeDiff + "ms (" + (timeDiff/1000.0) + "s)");
                    }

                    // Use blast timing for all multiplier ranges
                    if (remainingTime > 100) {
                        Log.d("AVIATOR_LOG", "Delaying crash by " + remainingTime + "ms for smooth animation completion");
                        Log.e("BLAST_DELAY_CRASH", "Delaying crash - Remaining: " + remainingTime + "ms, Final: " + finalMultiplier + "x");

                        // Continue showing real-time multiplier updates during the delay
                        Handler crashHandler = new Handler();
                        float finalMultiplier1 = finalMultiplier;
                        crashHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("BLAST_DELAYED_CRASH_EXECUTE", "Executing delayed crash at " + finalMultiplier1 + "x");
                                performSmoothCrash(finalMultiplier1);
                            }
                        }, remainingTime);
                    } else {
                        // Animation is close to completion, crash immediately
                        Log.d("AVIATOR_LOG", "Animation near completion, crashing immediately");
                        Log.e("BLAST_IMMEDIATE_CRASH", "Immediate crash - Remaining: " + remainingTime + "ms, Final: " + finalMultiplier + "x");
                        performSmoothCrash(finalMultiplier);
                    }
                }
            });
        }
    };

    /**
     * Perform the actual crash animation and UI updates smoothly
     */
    private void performSmoothCrash(float finalMultiplier) {
        Log.d("AVIATOR_LOG", "Performing smooth crash at " + finalMultiplier + "x");

        // Reset UI if in Redeem state
        if (btn_start.getText().toString().equals("Redeem")) {
            Log.d("AVIATOR_LOG", "Button was in 'Redeem' state, changing to 'Bet'");
            btn_start.setText("Bet");
            txt_amount.setText("10");
        }

        // Reset UI if in Bet state
        if (btn_start.getText().toString().equals("Bet")) {
            Log.d("AVIATOR_LOG", "Button was in 'Bet' state, resetting amount to 10");
            txt_amount.setText("10");
        }

        // Update UI visibility
        Log.d("AVIATOR_LOG", "Updating UI elements visibility for crash");
        tv_into_amount.setVisibility(View.GONE);
        txt_timer.setVisibility(View.VISIBLE);
        txt.setVisibility(View.VISIBLE);
        txt_rocket_value.setVisibility(View.GONE);

        // Trigger smooth crash animation
        if (aviatorAnimationView != null) {
            Log.d("AVIATOR_LOG", "Triggering smooth crash animation");
            aviatorAnimationView.crashAnimation();
            // Disable backend multiplier mode after crash
            aviatorAnimationView.setUseBackendMultiplier(false);
            Log.d("AVIATOR_LOG", "Disabled backend multiplier mode");
        }

        // Stop the GIF animation completely
        Log.d("AVIATOR_LOG", "Stopping GIF animation completely");
        stopGifAnimation();

        // Handle sound effects
        Log.d("AVIATOR_LOG", "Stopping current sound and playing crash sound");
        stopSound();
        playySound(R.raw.plane_blast);

        // Show the final multiplier value that the rocket reached
        Log.d("AVIATOR_LOG", "Showing final crash multiplier: " + finalMultiplier + "x");

        // Show the "FLEW AWAY" message with the final multiplier
        txt_exit.setVisibility(View.VISIBLE);
        txt_exit.setText("FLEW AWAY !");
        txt_exit_value.setVisibility(View.VISIBLE);
        txt_exit_value.setText(String.format("%.2fx", finalMultiplier));

        // Update the previous counts display with backend crash time
        updatePreviousCounts(String.format("%.2f", finalMultiplier));

        Log.d("AVIATOR_TIMER", "Smooth crash completed - Frontend-Backend sync: " + finalMultiplier + "x");

        // Reset bet amount
        Log.d("AVIATOR_LOG", "Resetting bet_amount to 0");
        bet_amount="0";

        // Show "Next game starts in" text - timer will be updated by backend aviatorTimer socket
        txt.setVisibility(View.VISIBLE);
        txt.setText("Next game starts in ");
        txt_timer.setVisibility(View.VISIBLE);
        txt_timer.setText(""); // Will be updated by backend aviatorTimer

        Log.d("AVIATOR_TIMER", "Waiting for backend aviatorTimer to sync next game countdown");


        // Refresh history based on selected tab
        if(str_selected_bet.equals("0")){
            Log.d("AVIATOR_LOG", "Refreshing game history after crash");
            CALL_API_Game_History();
        }else if(str_selected_bet.equals("1")){
            Log.d("AVIATOR_LOG", "Refreshing my bet history after crash");
            CALL_API_MyBet_History();
        }

        Log.d("AVIATOR_LOG", "Smooth crash handling completed");

        // Start next game timer immediately after blast
        ShowGiftimer(15000, "one");
    }

    // Next  Game Id

    private Emitter.Listener nextGameId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resp = String.valueOf(args[0]);
                    Log.e("RES_Value","Next_Game_Id "+resp);

                    next_game_id = resp;

                    if (next_game_id != null && !canbet) {
                        canbet = true;
                        btn_start.setText("Bet");
                        btn_start.setEnabled(true);
                    }

                }
            });
        }
    };


    // Aviator Timer
    private Emitter.Listener aviatorTimer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resp = String.valueOf(args[0]);
                    Log.e("RES_Value","aviatorTimer "+resp);

                    Integer intger= (Integer)args[0];
                    time_remaining = intger;

                    txt_timer.setText(""+time_remaining);

                }
            });
        }
    };

    // Current Multiplier - Real-time multiplier updates from backend with precise timing
    private Emitter.Listener onCurrentMultiplier = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("AVIATOR_MULTIPLIER", "Received precise current_multiplier event");

                        JSONObject data = (JSONObject) args[0];
                        String gameId = data.getString("game_id");
                        String multiplierStr = data.getString("multiplier");

                        float backendMultiplier = Float.parseFloat(multiplierStr);

                        // Store current multiplier with precise timing
                        currentBackendMultiplier = backendMultiplier;

                        Log.d("AVIATOR_MULTIPLIER", "Game ID: " + gameId + ", Precise multiplier: " +
                                String.format("%.3f", backendMultiplier) + "x");

                        // Update animation view with precise backend multiplier
                        if (aviatorAnimationView != null) {
                            aviatorAnimationView.updateMultiplierFromBackend(backendMultiplier);
                        }

                        // Update rocket value display with better precision
                        if (txt_rocket_value != null) {
                            // Show more precision for small values
                            if (backendMultiplier < 2.0f) {
                                txt_rocket_value.setText(String.format("%.3fx", backendMultiplier));
                            } else {
                                txt_rocket_value.setText(String.format("%.2fx", backendMultiplier));
                            }
                            txt_rocket_value.setVisibility(View.VISIBLE);
                        }

                        // Update inner timer with precise value
                        if (txt_inner_timer != null) {
                            txt_inner_timer.setText(String.format("%.3f", backendMultiplier));
                        }

                        // Calculate potential winnings with precise timing
                        if (!bet_id.equals("") && !bet_amount.equals("0")) {
                            try {
                                double amount = Double.parseDouble(bet_amount);
                                double potentialWinnings = amount * backendMultiplier;
                                tv_into_amount.setText("\u20B9 " + String.format("%.2f", potentialWinnings));
                                tv_into_amount.setVisibility(View.VISIBLE);

                                // Ensure redeem button is available with precise timing
                                if (!btn_start.getText().equals("Redeem")) {
                                    btn_start.setText("Redeem");
                                    btn_start.setEnabled(true);
                                }

                                Log.d("AVIATOR_MULTIPLIER", "Precise calculation: ₹" + amount + " × " +
                                        String.format("%.3f", backendMultiplier) + "x = ₹" +
                                        String.format("%.2f", potentialWinnings));
                            } catch (NumberFormatException e) {
                                Log.e("AVIATOR_MULTIPLIER", "Error in precise calculation: " + e.getMessage());
                                tv_into_amount.setText("\u20B9 0.00");
                            }
                        } else {
                            tv_into_amount.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        Log.e("AVIATOR_MULTIPLIER", "Error parsing precise multiplier: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        Log.e("AVIATOR_MULTIPLIER", "Error parsing precise multiplier value: " + e.getMessage());
                    }
                }
            });
        }
    };

//     ------------API CALL-------------------

    private void Betting_amount(String amount, String timerStatus) {
        // Validate minimum bet amount before placing bet
        try {
            double betAmount = Double.parseDouble(amount);
            if (betAmount < 10) {
                Toast.makeText(context, "Minimum bet amount is 10", Toast.LENGTH_SHORT).show();
                return;
            }

            double walletBalance = Double.parseDouble(wallet_amount);
            if (betAmount > walletBalance) {
                Toast.makeText(context, "Bet amount cannot exceed wallet balance", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket_Vertical.this);
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
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        bet_id = jsonObject1.getString("id");
                        redeem_gameid = jsonObject1.getString("dragon_tiger_id");

                        bet_amount = amount;
                        Profile_details();

                        // After successful bet, refresh the My Bets view if it's currently selected
                        if (str_selected_bet.equals("1")) {
                            Log.d("AVIATOR_API", "Refreshing My Bets view after successful bet");
                            CALL_API_MyBet_History();
                        }

                        //   Toast.makeText(context, "Bet success on Game id" + redeem_gameid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Bet success"/* + jsonObject1.toString()*/, Toast.LENGTH_SHORT).show();

                        loadingDialog.dismiss();
                    }else {
                        btn_start.setText("Bet");
                        txt_amount.setText("10");
                        loadingDialog.dismiss();
                        tv_waiting.setVisibility(View.INVISIBLE);
                        Toast.makeText(Aviator_Game_Socket_Vertical.this, ""+message, Toast.LENGTH_SHORT).show();
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
//                if (timerStatus.equals("timer off")) {
//                    int a = Integer.parseInt(final_game_id) + 1;
//                    params.put("game_id", String.valueOf(a));
//                    redeem_gameid = String.valueOf(a);
//                } else {
//                    params.put("game_id", game_id);
//                    redeem_gameid = game_id;
//                }

                params.put("game_id", next_game_id);
                params.put("user_id", user_id);
                params.put("amount", amount);

                Log.e("RES_Value", "Bettingparams " + params);
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
        // ─── ENTRY & API LOG ─────────────────────────────────
        Log.d("REDEEM_API", "→ Enter Redeem_amount()");
        Log.d("REDEEM_API", "   • Redeem_amount param = " + Redeem_amount);
        Log.d("REDEEM_API", "   • Current bet_amount   = " + bet_amount);
        Log.d("REDEEM_API", "   • API Endpoint         = POST " + Const.AVIATOR_GAME_REDEEM);

        // ─── SHOW LOADING ────────────────────────────────────
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket_Vertical.this);

        // ─── BUILD REQUEST ────────────────────────────────────
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Const.AVIATOR_GAME_REDEEM,

                response -> {
                    Log.d("REDEEM_API", "← onResponse()");
                    Log.d("REDEEM_API", "   • Raw response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String code    = jsonObject.getString("code");
                        String message = jsonObject.getString("message");
                        Log.d("REDEEM_API", "   • Parsed code=" + code + ", message=" + message);

                        if ("200".equals(code)) {
                            JSONObject data   = jsonObject.getJSONObject("data");
                            String user       = data.getString("id");
                            String wallet     = data.getString("wallet");

                            // Extract the actual winning amounts from backend response
                            String userWinningAmt = jsonObject.optString("user_winning_amt", "0");
                            String adminWinningAmt = jsonObject.optString("admin_winning_amt", "0");

                            Log.d("REDEEM_API", "   ✔ Success! user=" + user + ", wallet=" + wallet);
                            Log.d("REDEEM_API", "   ✔ User winning amount: ₹" + userWinningAmt);
                            Log.d("REDEEM_API", "   ✔ Admin commission: ₹" + adminWinningAmt);

                            // Show the actual winning amount from backend
                            try {
                                double actualWinnings = Double.parseDouble(userWinningAmt);
                                double stake = Double.parseDouble(bet_amount);
                                double multiplier = currentBackendMultiplier;

                                Toast.makeText(context,
                                        "🎉 WON ₹" + userWinningAmt + " (Multiplier: " + String.format("%.2fx", multiplier) + ")",
                                        Toast.LENGTH_LONG).show();

                                Log.d("REDEEM_API", "   ✔ Displayed winning amount: ₹" + userWinningAmt + " at " + multiplier + "x");
                            } catch (NumberFormatException e) {
                                Toast.makeText(context, "Redeemed successfully!", Toast.LENGTH_SHORT).show();
                            }

                            tv_into_amount.setVisibility(View.GONE);
                            tv_waiting.setVisibility(View.INVISIBLE);

                            // Reset bet variables after successful redeem
                            bet_id = "";
                            bet_amount = "0";
                            currentBackendMultiplier = 1.0f;
                            Profile_details();

                            // After successful redemption, refresh the My Bets view if it's currently selected
                            if (str_selected_bet.equals("1")) {
                                Log.d("AVIATOR_API", "Refreshing My Bets view after successful redemption");
                                CALL_API_MyBet_History();
                            }
                        } else {
                            Log.e("REDEEM_API", "   ✖ Failed redeem: " + message);
                        }
                    } catch (JSONException e) {
                        Log.e("REDEEM_API", "   ⚠ JSON parse error: " + e.getMessage(), e);
                    } finally {
                        loadingDialog.dismiss();
                        Log.d("REDEEM_API", "← Dismissed loading dialog (onResponse)");
                    }
                },

                error -> {
                    NetworkResponse nr = error.networkResponse;
                    if (nr != null) {
                        Log.e("REDEEM_API", "← HTTP ERROR " + nr.statusCode
                                + ": " + new String(nr.data));
                    } else {
                        Log.e("REDEEM_API", "← Volley error (no network response): " + error, error);
                    }
                    loadingDialog.dismiss();
                    Log.d("REDEEM_API", "← Dismissed loading dialog (onError)");
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d("REDEEM_API", "← HTTP STATUS CODE: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("REDEEM_API", "→ getParams()");
                HashMap<String, String> params = new HashMap<>();

                double winnings = 0, stake = 0;
                try {
                    winnings = Double.parseDouble(Redeem_amount);
                    stake    = Double.parseDouble(bet_amount);
                } catch (NumberFormatException e) {
                    Log.e("REDEEM_API", "   ⚠ Invalid number format: " + e.getMessage(), e);
                }

                String winStr   = String.valueOf(winnings);
                String stakeStr = String.valueOf(stake);
                String totalStr = String.valueOf(winnings + stake);

                Log.d("REDEEM_API", "   • Winnings = " + winStr);
                Log.d("REDEEM_API", "   • Stake    = " + stakeStr);
                Log.d("REDEEM_API", "   • Total    = " + totalStr);

                params.put("game_id",    redeem_gameid);
                params.put("user_id",    user_id);
                params.put("amount", Redeem_amount);  // send current multiplier
                params.put("bet_amount", stakeStr);   // original stake amount
                params.put("token",      Const.AVIATOR_Token);
                params.put("bet_id",     bet_id);

                Log.d("REDEEM_API", "   ✔ Sending multiplier: " + Redeem_amount + "x");
                Log.d("REDEEM_API", "   ✔ Sending bet amount: ₹" + stakeStr);

                Log.d("REDEEM_API", "   • Params map: " + params);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d("REDEEM_API", "→ getHeaders()");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.AVIATOR_Token);
                Log.d("REDEEM_API", "   • Headers map: " + headers);
                return headers;
            }
        };

        // ─── QUEUE & RETRY POLICY ────────────────────────────
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        stringRequest.setRetryPolicy(retryPolicy);

        // ─── FINAL URL LOG & QUEUE ───────────────────────────
        Log.d("REDEEM_API", "→ Final API URL: " + stringRequest.getUrl());
        Log.d("REDEEM_API", "→ Adding request to queue");
        requestQueue.add(stringRequest);
    }




    private void Cancel_bet(String Redeem_amount) {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.showDialog(Aviator_Game_Socket_Vertical.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.AVIATOR_CANCEL_BET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Res_CancelBet", "" + response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        // Reset bet variables after successful cancellation
                        bet_id = "";
                        bet_amount = "0";

                        Profile_details();

                        // After successful cancellation, refresh the My Bets view if it's currently selected
                        if (str_selected_bet.equals("1")) {
                            Log.d("AVIATOR_API", "Refreshing My Bets view after successful bet cancellation");
                            CALL_API_MyBet_History();
                        }
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
                params.put("bet_id", bet_id);//bet_id
                Log.e("RES_Value",  "CancelParam " + params);
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
        loadingDialog.showDialog(Aviator_Game_Socket_Vertical.this);

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

                    //    Toast.makeText(context, "Socket Connect " + mSocket.id(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    //        mSocket.on("connect_error", new Emitter.Listener() {
    //            @Override
    //            public void call(Object... args) {
    //                runOnUiThread(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        // String message = null;
    //                        String user = String.valueOf(args[0]);
    //
    //                        Log.v("RES_connect_errorR" , user);
    //
    //                    }
    //                });
    //
    //            }
    //        });


    private void CALL_API_MyBet_History() {
        Log.d("AVIATOR_API", "Calling real API for my bet history");

        // Stop any existing fake data timer
        if (fakeDataTimer != null) {
            fakeDataTimer.cancel();
            fakeDataTimer = null;
        }

        // Initialize the list
        myWinnigmodelArrayList = new ArrayList<>();

        // Set up parameters for API call
        HashMap params = new HashMap();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));

        Log.d("AVIATOR_API", "Calling API: " + Const.AVIATOR_MY_HISTORY + " with params: " + params);

        // Call the real API
        ApiRequest.Call_Api(context, Const.AVIATOR_MY_HISTORY, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                Log.d("AVIATOR_API", "API Response: " + resp);

                // Check if the response contains a server error
                if (resp != null && resp.contains("com.android.volley.ServerError")) {
                    Log.e("AVIATOR_API", "Server error detected: " + resp);
                    // Generate fake data as fallback
                    generateFakeUserBets(10);
                    Toast.makeText(context, "Server error. Showing sample data.", Toast.LENGTH_SHORT).show();
                } else if (resp == null) {
                    Log.e("AVIATOR_API", "Null response from server");
                    // Generate fake data as fallback
                    generateFakeUserBets(10);
                    Toast.makeText(context, "Connection error. Showing sample data.", Toast.LENGTH_SHORT).show();
                } else {
                    // Try to parse the response as JSON
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.getString("code");

                        Log.d("AVIATOR_API", "Response code: " + code);

                        if (code.equalsIgnoreCase("200")) {
                            // Parse the game data
                            JSONArray gameData = jsonObject.optJSONArray("game_data");
                            if (gameData != null && gameData.length() > 0) {
                                Log.d("AVIATOR_API", "Found " + gameData.length() + " game records");

                                for (int i = 0; i < gameData.length(); i++) {
                                    JSONObject betObject = gameData.getJSONObject(i);

                                    MyWinnigmodel model = new MyWinnigmodel();
                                    model.setId(betObject.optString("id"));
                                    model.setAnder_baher_id(betObject.optString("dragon_tiger_id"));
                                    model.setAdded_date(betObject.optString("added_date"));
                                    model.setAmount(betObject.optString("amount"));
                                    model.setWinning_amount(betObject.optString("winning_amount"));
                                    model.setBet(betObject.optString("bet"));
                                    model.setName(betObject.optString("name", "You"));
                                    model.setRoom_id(betObject.optString("room_id"));

                                    myWinnigmodelArrayList.add(model);
                                }
                            } else {
                                Log.d("AVIATOR_API", "No game records found for this user");
                                // Generate a few fake records as fallback
                                generateFakeUserBets(5);
                                Toast.makeText(context, "No bet history found. Showing sample data.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("AVIATOR_API", "API returned non-200 code: " + code);
                            // Generate fake data as fallback
                            generateFakeUserBets(10);
                            Toast.makeText(context, "Could not load bet history. Showing sample data.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("AVIATOR_API", "JSON parsing error: " + e.getMessage());
                        // Generate fake data as fallback
                        generateFakeUserBets(10);
                        Toast.makeText(context, "Error loading bet history. Showing sample data.", Toast.LENGTH_SHORT).show();
                    }
                }

                // Sort the list to show newest first if we have any data
                if (!myWinnigmodelArrayList.isEmpty()) {
                    // Sort by date (newest first)
                    Collections.sort(myWinnigmodelArrayList, new Comparator<MyWinnigmodel>() {
                        @Override
                        public int compare(MyWinnigmodel o1, MyWinnigmodel o2) {
                            try {
                                // Parse dates and compare (newer dates first)
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date1 = format.parse(o1.getAdded_date());
                                Date date2 = format.parse(o2.getAdded_date());
                                return date2.compareTo(date1); // Descending order (newest first)
                            } catch (Exception e) {
                                // If date parsing fails, use the original order
                                return 0;
                            }
                        }
                    });
                }

                // Set up the adapter
                myWinningAdapte = new AviatorMyHistoryAdapter(context, myWinnigmodelArrayList);
                rec_winning.setAdapter(myWinningAdapte);

                Log.d("AVIATOR_API", "Displayed " + myWinnigmodelArrayList.size() + " bet records");
            }
        });
    }

    /**
     * Generates fake user bets as a fallback when the API fails
     * @param count Number of fake bets to add
     */
    private void generateFakeUserBets(int count) {
        Log.d("AVIATOR_API", "Generating " + count + " fake user bets as fallback");

        for (int i = 0; i < count; i++) {
            // 90% chance of win for better user experience
            boolean isWin = random.nextInt(100) < 90;
            myWinnigmodelArrayList.add(generateFakeHistoryRecord(isWin, true));
        }
    }



    /**
     * Generates a random multiplier between 1.1x and 5.0x
     */
    private String generateRandomMultiplier() {
        // Generate multiplier between 1.1 and 5.0
        double multiplier = 1.1 + (random.nextDouble() * 3.9);
        return String.format("%.2f", multiplier);
    }

    /**
     * Generates a random amount for bets
     * @param isWin true for win (larger amount), false for loss (smaller amount)
     */
    private String generateRandomAmount(boolean isWin) {
        int min = isWin ? 1000 : 100;
        int max = isWin ? 5000 : 500;
        return String.valueOf(min + random.nextInt(max - min + 1));
    }

    /**
     * Generates a random winning amount
     * @param betAmount the original bet amount
     * @param multiplier the multiplier
     * @param isWin true if it's a win, false if it's a loss
     */
    private String generateWinningAmount(String betAmount, String multiplier, boolean isWin) {
        if (!isWin) {
            return "0.00"; // Loss
        }

        try {
            double bet = Double.parseDouble(betAmount);
            double multi = Double.parseDouble(multiplier);
            return String.format("%.2f", bet * multi);
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    /**
     * Generates a single fake game history record
     * @param isWin true for win, false for loss
     * @param isCurrentUser true if this record is for the current user
     */
    private MyWinnigmodel generateFakeHistoryRecord(boolean isWin, boolean isCurrentUser) {
        MyWinnigmodel model = new MyWinnigmodel();

        // Generate random ID
        model.setId(String.valueOf(10000 + random.nextInt(90000)));
        model.setAnder_baher_id(String.valueOf(1000 + random.nextInt(9000)));

        // Set timestamp with a random offset in the past (0-60 minutes)
        long currentTime = System.currentTimeMillis();
        long randomOffset = random.nextInt(60 * 60 * 1000); // Random offset up to 60 minutes in milliseconds
        model.setAdded_date(String.valueOf((currentTime - randomOffset) / 1000));

        // Generate bet amount
        String betAmount = generateRandomAmount(isWin);
        model.setAmount(betAmount);

        // Generate multiplier
        String multiplier = generateRandomMultiplier();
        model.setBet(multiplier);

        // Calculate winning amount
        String winningAmount = generateWinningAmount(betAmount, multiplier, isWin);
        model.setWinning_amount(winningAmount);

        // Set name - either current user or random name
        if (isCurrentUser) {
            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String userName = prefs.getString("name", "You");
            model.setName(userName);
        } else {
            model.setName(fakeNames[random.nextInt(fakeNames.length)]);
        }

        // Set room ID
        model.setRoom_id(String.valueOf(1 + random.nextInt(5)));

        return model;
    }

    /**
     * Adds a new fake record to the history and updates the UI
     */
    private void addNewFakeRecord() {
        // Only add new records if we're on the All Bets tab AND not My Bets
        if (!str_selected_bet.equals("0")) {
            return;
        }

        // Don't add fake records to My Bets history
        return;
    }

    /**
     * Starts the timer to simulate new data coming in
     */
    private void startFakeDataTimer() {
        // Cancel any existing timer
        if (fakeDataTimer != null) {
            fakeDataTimer.cancel();
            fakeDataTimer = null;
        }

        // Create new timer
        fakeDataTimer = new Timer();
        fakeDataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addNewFakeRecord();
                    }
                });
            }
        }, FAKE_DATA_UPDATE_INTERVAL, FAKE_DATA_UPDATE_INTERVAL);

        Log.d("AVIATOR_API", "Started fake data timer with interval: " + FAKE_DATA_UPDATE_INTERVAL + "ms");
    }

    /**
     * Stops the fake data timer
     */
    private void stopFakeDataTimer() {
        if (fakeDataTimer != null) {
            fakeDataTimer.cancel();
            fakeDataTimer = null;
            Log.d("FAKE_DATA", "Stopped fake data timer");
        }
    }

    // Timer for refreshing real game history data
    private Timer gameHistoryRefreshTimer;
    private final int GAME_HISTORY_REFRESH_INTERVAL = 10000; // 10 seconds

    /**
     * Starts a timer to periodically refresh the game history
     */
    private void startGameHistoryRefreshTimer() {
        // Cancel any existing timer
        if (gameHistoryRefreshTimer != null) {
            gameHistoryRefreshTimer.cancel();
            gameHistoryRefreshTimer = null;
        }

        // Create new timer to refresh game history every 10 seconds
        gameHistoryRefreshTimer = new Timer();
        gameHistoryRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (str_selected_bet.equals("0")) {
                            // Only refresh if we're on the All Bets tab
                            Log.d("AVIATOR_API", "Auto-refreshing game history");
                            CALL_API_Game_History(false); // Don't start timer again
                        }
                    }
                });
            }
        }, GAME_HISTORY_REFRESH_INTERVAL, GAME_HISTORY_REFRESH_INTERVAL);

        Log.d("AVIATOR_API", "Started game history refresh timer with interval: " + GAME_HISTORY_REFRESH_INTERVAL + "ms");
    }

    /**
     * Stops the game history refresh timer
     */
    private void stopGameHistoryRefreshTimer() {
        if (gameHistoryRefreshTimer != null) {
            gameHistoryRefreshTimer.cancel();
            gameHistoryRefreshTimer = null;
            Log.d("AVIATOR_API", "Stopped game history refresh timer");
        }
    }

    private void CALL_API_Game_History() {
        // Call with default behavior (start refresh timer)
        CALL_API_Game_History(true);
    }

    private void CALL_API_Game_History(boolean startRefreshTimer) {
        Log.d("AVIATOR_API", "Generating fake game history data");

        // Stop any existing fake data timer
        if (fakeDataTimer != null) {
            fakeDataTimer.cancel();
            fakeDataTimer = null;
        }

        // Initialize the list
        myWinnigmodelArrayList = new ArrayList<>();

        // Just generate fake data - don't mix with real user data
        generateFakeGameHistory();
    }

    /**
     * Generates fake game history data
     */
    private void generateFakeGameHistory() {
        Log.d("AVIATOR_API", "Generating fake game history data");

        // Generate 100 fake records
        for (int i = 0; i < 100; i++) {
            // 80% chance of win, 20% chance of loss
            boolean isWin = random.nextInt(100) < 80;

            // Add to the list
            myWinnigmodelArrayList.add(generateFakeHistoryRecord(isWin, false));
        }

        // Sort the list by date (newest first)
        if (!myWinnigmodelArrayList.isEmpty()) {
            Collections.sort(myWinnigmodelArrayList, new Comparator<MyWinnigmodel>() {
                @Override
                public int compare(MyWinnigmodel o1, MyWinnigmodel o2) {
                    try {
                        // For fake data, we use timestamps, so convert to long and compare
                        long time1 = Long.parseLong(o1.getAdded_date());
                        long time2 = Long.parseLong(o2.getAdded_date());
                        return Long.compare(time2, time1); // Descending order (newest first)
                    } catch (Exception e) {
                        // If parsing fails, use the original order
                        return 0;
                    }
                }
            });
        }

        // Set up the adapter
        myWinningAdapte = new AviatorMyHistoryAdapter(context, myWinnigmodelArrayList);
        rec_winning.setAdapter(myWinningAdapte);

        // Don't start fake data timer to prevent multiple entries
        // startFakeDataTimer();

        Log.d("AVIATOR_API", "Generated " + myWinnigmodelArrayList.size() + " game history records");
    }

    // Removed addFakeGameHistory method as we're now using only real data

    private void playySound(int sound) {
        soundPlayer = MediaPlayer.create(getApplicationContext(), sound);
        soundPlayer.start();
    }

    private void stopSound() {
        if (soundPlayer != null) {
            soundPlayer.stop();
            soundPlayer.release();
            soundPlayer = null;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

