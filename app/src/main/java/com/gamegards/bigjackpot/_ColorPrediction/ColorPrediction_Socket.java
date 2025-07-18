package com.gamegards.bigjackpot._ColorPrediction;

import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;
import static com.gamegards.bigjackpot.Utils.Functions.ANIMATION_SPEED;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.gamegards.bigjackpot.Activity.BuyChipsList;
import com.gamegards.bigjackpot.Activity.Homepage;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.ChipsPicker;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Animations;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.Utils.Variables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class ColorPrediction_Socket extends BaseActivity {

    RecyclerView recycle_bots;
    ArrayList<Bots_list> bots_lists = new ArrayList<>();
    BotsAdapter botsAdapter;
    LinearLayout lnrfollow, lnrfollowColr, lnrfollowColr2, lnrfollowColr3;
    RelativeLayout rltline;
    Context context = this;
    CountDownTimer counttimerforstartgame;
    CountDownTimer counttimerforcards;
    CountDownTimer counttimerforcardsforAnimation;
    int count = 0;
    private MediaPlayer mp;
    String tagamountselected = "", coloramountselected = "";
    int betcountandar = 1;
    int betcountbahar = 1;
    int countvaue = 0;
    int countvaueforani = 0;
    boolean isConfirm = false;
    boolean IsFirsttimeCall = true;
    String betvalue = "";
    int widthandar = -430;
    int countwidhtbahar = 41;
    int widthbahar = -430;
    int countwidhtanadar = 41;
    int firstcount = 0;
    TextView txtGameFinish, txtName, txtBallence, txt_catander, txt_catbahar, txt_room, txt_gameId, txt_min_max, txt_many_cards;
    ImageView imgmaincard, txtGameBets, txtGameRunning;
    Typeface helvatikaboldround;

    ArrayList<String> aaraycards;
    boolean isGameBegning = false;
    int timertime = 4000;
    int cardAnimationIntervel = 200;
    public String game_id;
    public String room_id;
    public String min_coin;
    public String max_coin;
    public String main_card;
    public String winning;
    public String status = "";
    String bet_id = "", str_small, str_big;
    private String added_date;
    int time_remaining;
    private int fakeUserCount = -1; // Store fake user count to avoid regenerating
    private int timer_interval = 1000;
    private String user_id, name, wallet;
    Timer timerstatus;
    ImageView imgCardsandar, imgCardsbahar;
    Animation setanimation;
    ImageView imgmaincardsvaluehiostory;
    String betplace = "";
    boolean canbet = false;
    boolean isInPauseState = false;
    boolean isCardsDisribute = false;
    Button btnCANCEL, btnRepeat, btnDouble, btnconfirm;
    RelativeLayout rltandarbet, rltbaharbet, rltmainviewander, rltmainviewbahar, rlt41more, rlt1to5, rlt6to10, rlt11to15, rlt16to25, rlt26to30, rlt31to35, rlt36to40;

    ImageView imgpl1circle, img_big, img_small;
    LinearLayout lnrOnlineUser, lnrOnlineUserNew;
    View ChipstoUser;
    private boolean isCountDownStart;
    RelativeLayout rlt_centercard;
    ProgressDialog progressDialog;
    private static final String[] sectors = {"1 red",
            "2 white", "3 red", "4 white", "5 red", "6 white", "7 red", "8 white",
            "9 red", "10 white", "11 red", "12 white", "13 red", "14 white", "15 red",
            "16 white", "17 red", "18 white", "19 red", "20 white", "21 red",
            "22 white", "23 red", "24 white"};
  //  @BindView(R.id.spinBtn)
    Button spinBtn;
   // @BindView(R.id.resultTv)
    TextView resultTv;
   // @BindView(R.id.wheel)
    ImageView wheel;
    // We create a Random instance to make our wheel spin randomly
    private static final Random RANDOM = new Random();
    private int degree = 0, degreeOld = 0;
    // We have 37 sectors on the wheel, we divide 360 by this value to have angle for each sector
    // we divide by 2 to have a half sector
    private static final float HALF_SECTOR = 360f / 24f / 2f;
    Button spin;
    TextView tvsmallCoins, tvBigCoins, tvonlineuser;

    private static final String URL = Const.SOCKET_IP + "color_prediction";
    Socket mSocket;
    public String token = "";
    int version = 0;
    JSONArray last_winning = null;
    boolean isHistoryShown = true;

    // Variables to track user's bet status
    private boolean userPlacedBet = false;
    private double userWinningAmount = 0.0;
    private boolean userWon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_prediction);
        ButterKnife.bind(this);
        Log.d("ColorPrediction_Socket", "Socket version started");
        Toast.makeText(context, "CP Socket Started", Toast.LENGTH_SHORT).show();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};
            mSocket = IO.socket(URL, opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.connect();
        mSocket.emit("color_prediction_timer", "color_prediction_timer");

        initSoundPool();
        initview();
        initiAnimation();
        initDisplayMetrics();
        setDataonUser();

        recycle_bots = findViewById(R.id.recycle_bots);
        spinBtn = findViewById(R.id.spinBtn);
        resultTv = findViewById(R.id.resultTv);
        wheel = findViewById(R.id.wheel);

        recycle_bots.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        recycle_bots.setHasFixedSize(true);

        mSocket.on("color_prediction_timer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String resp = String.valueOf(args[0]);
                        Log.e("RES_Timer_Response", resp);
                        //     restartGame();

                        //    aaraycards.clear();
                        Integer intger = (Integer) args[0];
                        time_remaining = intger;

                        if (intger <= 0) {

                            txtGameFinish.setText("0");
                            txtGameBets.setVisibility(View.GONE);

                            enableDisableView(lnrfollowColr,false);
                            enableDisableView(lnrfollowColr2,false);
                            enableDisableView(lnrfollowColr3,false);

                        } else {

                            enableDisableView(lnrfollowColr,true);
                            enableDisableView(lnrfollowColr2,true);
                            enableDisableView(lnrfollowColr3,true);

                            txtGameFinish.setText(intger + "");
                            txtGameRunning.setVisibility(View.GONE);
                            txtGameBets.setVisibility(View.VISIBLE);

                        }

                    }
                });

            }
        });

        mSocket.on("color_prediction_status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String resp = String.valueOf(args[0]);
                        Log.e("RES_Socket_Response", resp);

                        //    startGameCountDown();

                        try {
                            bots_lists.clear();

                            JSONObject jsonObject = new JSONObject(resp);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equalsIgnoreCase("200")) {

                                JSONArray bot_user = jsonObject.getJSONArray("bot_user");
                                JSONArray arraygame_dataa = jsonObject.getJSONArray("game_data");
                                Log.e("fuckkk", arraygame_dataa.toString() );

                                JSONArray online_users = jsonObject.getJSONArray("online_users");
                                int my_ander_bet = jsonObject.optInt("my_ander_bet");
                                int my_bahar_bet = jsonObject.optInt("my_bahar_bet");
                                last_winning = jsonObject.getJSONArray("last_winning");

                                // Parse last_bet array to check if user placed a bet and determine win/loss
                                JSONArray last_bet = jsonObject.optJSONArray("last_bet");
                                parseLastBetAmount(last_bet);

//                            for (int i = 0; i < bot_user.length() ; i++) {
//                                JSONObject jsonObject1 = bot_user.getJSONObject(i);
//                                bots_lists.add(new Bots_list(
//                                        jsonObject1.getString("id"),
//                                        jsonObject1.getString("name"),
//                                        jsonObject1.getString("coin"),
//                                        jsonObject1.getString("avatar")));
//                            }
//
//                            botsAdapter = new BotsAdapter(ColorPrediction.this, bots_lists);
//                            recycle_bots.setAdapter(botsAdapter);

//                            show_bots_user(bot_user);


                                if(last_winning.length() > 0 && isHistoryShown)
                                {
                                    addLastWinBetonView(last_winning);
                                }

                                txt_catander.setText("" + my_ander_bet);
                                txt_catbahar.setText("" + my_bahar_bet);
                                int online = jsonObject.getInt("online");

                                // Comment out existing online user code
                                // ((TextView) findViewById(R.id.tvonlineuser))
                                //         .setText("" + online_users.length());
                                // ((TextView) findViewById(R.id.tvonlineuserNew))
                                //         .setText("" + online_users.length());

                                // Generate fake user count only once, then reuse it
                                if (fakeUserCount == -1) {
                                    Random random = new Random();
                                    fakeUserCount = random.nextInt(151) + 50; // Random between 50-200
                                }

                                ((TextView) findViewById(R.id.tvonlineuser))
                                        .setText("" + fakeUserCount);
                                ((TextView) findViewById(R.id.tvonlineuserNew))
                                        .setText("" + fakeUserCount);
                                for (int i = 0; i < arraygame_dataa.length(); i++) {
                                    JSONObject welcome_bonusObject = arraygame_dataa.getJSONObject(i);

                                    //  GameStatus model = new GameStatus();
                                    game_id = welcome_bonusObject.getString("id");

                                    status = welcome_bonusObject.getString("status");
                                    winning = welcome_bonusObject.getString("winning");
                                    Log.d("winner_", winning);
                                    String end_datetime = welcome_bonusObject.getString("end_datetime");
                                    added_date = welcome_bonusObject.getString("added_date");
                                    //    time_remaining  = welcome_bonusObject.optInt("time_remaining");
                                    //  updated_date  = welcome_bonusObject.getString("updated_date");


//                                    String uri1 = "@drawable/" + main_card.toLowerCase();  // where myresource " +
//                                    int imageResource1 = getResources().getIdentifier(uri1, null,
//                                            getPackageName());
//                                    Picasso.get().load(imageResource1).into(imgmaincard);
//                                    Picasso.get().load(imageResource1).into(imgmaincardsvaluehiostory);

                                }

//                                JSONArray arrayprofile = jsonObject.getJSONArray("profile");
//
//                                for (int i = 0; i < arrayprofile.length() ; i++) {
//                                    JSONObject profileObject = arrayprofile.getJSONObject(i);
//
//                                    //  GameStatus model = new GameStatus();
//                                    user_id  = profileObject.getString("id");
                                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                user_id_player1 = prefs.getString("user_id", "");
//                                    name  = profileObject.getString("name");
//                                    wallet  = profileObject.getString("wallet");
//
//                                    profile_pic  = profileObject.getString("profile_pic");
//
//                                    Picasso.get().load(Const.IMGAE_PATH + profile_pic).into(imgpl1circle);
//
//                                    //  txtBallence.setText(wallet);
//                                    txtName.setText(name);
//
//                                }


                                JSONArray arraypgame_cards = jsonObject.getJSONArray("game_cards");

                                for (int i = 0; i < arraypgame_cards.length(); i++) {
                                    JSONObject cardsObject = arraypgame_cards.getJSONObject(i);

                                    String card = cardsObject.getString("card");
                                    String color_prediction_id = cardsObject.getString("color_prediction_id");
                                    aaraycards.add(card);
                                    
                                    // Show ID beside ? image
                                    TextView tvColorPredictionId = findViewById(R.id.txt_colorPredictionId);
                                    tvColorPredictionId.setText(color_prediction_id);
                                }
//New Game Started here ------------------------------------------------------------------------
                                Functions.LOGE("GameActivity", "status : " + status + " isGameBegning : " + isGameBegning);
                                if (status.equals("0") && !isGameBegning) {

                                    VisiblePleasewaitforNextRound(false);
                                    if (counttimerforcards != null) {
                                        counttimerforcards.cancel();
                                    }

                                    if (counttimerforcardsforAnimation != null) {
                                        counttimerforcardsforAnimation.cancel();
                                    }

                                    restartGame();

                                } else if (status.equals("0") && isGameBegning) {
                                    UserProfile();
                                }

//Game Started here
                                if (status.equals("1") && !isGameBegning) {
                                    VisiblePleasewaitforNextRound(true);


                                }

                                if (status.equals("1") && isGameBegning) {


                                    isGameBegning = false;
                                    Log.v("game", "stoped");

                                    // Only show win/loss if user actually placed a bet
                                    if (userPlacedBet) {
                                        if (userWon) {
                                            Log.e("RES_GameResult", "User WON with winning amount: " + userWinningAmount);
                                            makeWinnertoPlayer(user_id_player1);
                                        } else {
                                            Log.e("RES_GameResult", "User LOST with winning amount: " + userWinningAmount);
                                            makeLosstoPlayer(user_id_player1);
                                        }
                                    } else {
                                        Log.e("RES_GameResult", "User did not place any bet - no win/loss notification");
                                        // Just show the wheel animation without win/loss notification
                                        Animation animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                                        wheel.startAnimation(animRotate);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                wheel.clearAnimation();
                                                spin_wheel();
                                                restartRuleView();
                                                highlightWinView();
                                            }
                                        }, 3000);
                                    }

//                                    if (aaraycards.size() > 0){
//                                        if (counttimerforcardsforAnimation != null){
//                                            counttimerforcardsforAnimation.cancel();
//                                        }
//
//                                        if (counttimerforcards != null){
//                                            counttimerforcards.cancel();
//                                        }
//
//
//                                        counttimerforcards = new CountDownTimer(aaraycards.size()*cardAnimationIntervel, cardAnimationIntervel) {
//
//                                            @Override
//                                            public void onTick(long millisUntilFinished) {
//                                                isCardsDisribute = true;
//                                                int y = countvaue % 2;
//
//                                                makeWinnertoPlayer("");
//                                                makeLosstoPlayer("");
//
//                                                countvaue++;
//                                            }
//
//                                            @Override
//                                            public void onFinish() {
//
//
//                                                VisiblePleasewaitforNextRound(true);
//                                                isCardsDisribute = false;
//
//                                                makeWinnertoPlayer(user_id_player1);
//
//
//                                            }
//
//
//                                        };
//
//                                        counttimerforcards.start();
//
//
//                                    }


                                } else {


                                }

                            } else {
                                if (jsonObject.has("message")) {

                                    Functions.showToast(context, message);


                                }


                            }

//                            if (status.equals("1")) {
//                            //     txtGameRunning.setVisibility(View.VISIBLE);
//                                txtGameBets.setVisibility(View.GONE);
//                            } else {
//                            //     txtGameRunning.setVisibility(View.GONE);
//                                txtGameBets.setVisibility(View.VISIBLE);
//
//                                makeWinnertoPlayer("");
//                                makeLosstoPlayer("");
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spin_wheel();
            }
        });

        img_small.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (betValidation()) {
                    if (tagamountselected.equals("")) {
                        Toast.makeText(context, "Please select a valid amount to Bet", Toast.LENGTH_SHORT).show();
                    } else {
                        putbet("15");
                        //---
//                        HashMap params = new HashMap<String, String>();
//                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                        params.put("user_id", prefs.getString("user_id", ""));
//                        params.put("token", prefs.getString("token", ""));
//                        params.put("room_id", "1");
//                        ApiRequest.Call_Api(context, Const.ColorPrediction, params, new Callback() {
//                            @Override
//                            public void Responce(String response, String type, Bundle bundle) {
//                                if (response != null) {
//                                    try {
//                                        Functions.LOGE("andar_bahar", "" + response);
//                                        Log.d("getStatus_type_", response);
//
//                                        JSONObject jsonObject = new JSONObject(response);
//                                        String code = jsonObject.getString("code");
//                                        String message = jsonObject.getString("message");
//
//                                        if (code.equalsIgnoreCase("200")) {
//                                            progressDialog.dismiss();
//                                          //  str_small = jsonObject.getString("my_bet_small");
//                                            str_small = jsonObject.getString("my_bet_small");
//                                            Log.e("str_small", "" + str_small);
//                                            findViewById(R.id.rlt_smallchip).setVisibility(View.VISIBLE);
//                                            tvsmallCoins.setText(str_small);
//
//                                        } else {
//                                            progressDialog.dismiss();
//                                            if (jsonObject.has("message")) {
//                                                Functions.showToast(context, message);
//
//                                            }
//                                        }
//
//                                    } catch (JSONException e) {
//                                        progressDialog.dismiss();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
                    }

                }

            }
        });

        img_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (betValidation()) {
                    if (tagamountselected.equals("")) {
                        Toast.makeText(context, "Please select a valid amount to Bet", Toast.LENGTH_SHORT).show();
                    } else {
                        putbet("16");
                        //---
//                        HashMap params = new HashMap<String, String>();
//                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                        params.put("user_id", prefs.getString("user_id", ""));
//                        params.put("token", prefs.getString("token", ""));
//                        params.put("room_id", "1");
//                        ApiRequest.Call_Api(context, Const.ColorPrediction, params, new Callback() {
//                            @Override
//                            public void Responce(String response, String type, Bundle bundle) {
//                                if (response != null) {
//                                    try {
//                                        Log.d("getStatus_type_", response);
//
//                                        JSONObject jsonObject = new JSONObject(response);
//                                        String code = jsonObject.getString("code");
//                                        String message = jsonObject.getString("message");
//
//                                        if (code.equalsIgnoreCase("200")) {
//                                            progressDialog.dismiss();
//                                            str_big = jsonObject.getString("my_bet_big");
//                                            Log.e("str_big", "" + str_big);
//                                            findViewById(R.id.rlt_bigchip).setVisibility(View.VISIBLE);
//                                            tvBigCoins.setText(str_big);
//
//                                        } else {
//                                            progressDialog.dismiss();
//                                            if (jsonObject.has("message")) {
//                                                Functions.showToast(context, message);
//
//                                            }
//                                        }
//
//                                    } catch (JSONException e) {
//                                        progressDialog.dismiss();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
                    }

                }

            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                getStatus_bots();
            }
        }, 5000);
    }

    private void spin_wheel() {
        degreeOld = degree % 360;
        // we calculate random angle for rotation of our wheel
//        degree = RANDOM.nextInt(360) + 720;           //random win
//        degree = 270+360;                             //custom win
        if (winning.equals("0")) {
            degree = 53 + 360;
        }
        if (winning.equals("1")) {
            degree = 20 + 360;
        }
        if (winning.equals("2")) {
            degree = 345 + 360;
        }
        if (winning.equals("3")) {
            degree = 307 + 360;
        }
        if (winning.equals("4")) {
            degree = 270 + 360;
        }
        if (winning.equals("5")) {
            degree = 235 + 360;
        }
        if (winning.equals("6")) {
            degree = 195 + 360;
        }
        if (winning.equals("7")) {
            degree = 160 + 360;
        }
        if (winning.equals("8")) {
            degree = 127 + 360;
        }
        if (winning.equals("9")) {
            degree = 90 + 360;
        }
        Log.d("degree_new", String.valueOf(degree));
        Log.d("degree_old", String.valueOf(degreeOld));
        // rotation effect on the center of the wheel
        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(1250);
        //    rotateAnim.setDuration(2500);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // we empty the result text view when the animation start
                resultTv.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // we display the correct sector pointed by the triangle at the end of the rotate animation
                resultTv.setText(getSector(360 - (degree % 360)));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // we start the animation
        wheel.startAnimation(rotateAnim);
    }

    private void spin_wheelNew() {       //before actual spin
        degreeOld = degree % 360;
        // we calculate random angle for rotation of our wheel
//        degree = RANDOM.nextInt(360) + 720;           //random win
//        degree = 270+360;                             //custom win
//        if (winning.equals("0")){
//            degree = 360+7680;
//        }
        if (winning.equals("0")) {
            degree = 360 + 360;
        }
        if (winning.equals("1")) {
            degree = 320 + 360;
        }
        if (winning.equals("2")) {
            degree = 287 + 360;
        }
        if (winning.equals("3")) {
            degree = 255 + 360;
        }
        if (winning.equals("4")) {
            degree = 215 + 360;
        }
        if (winning.equals("5")) {
            degree = 180 + 360;
        }
        if (winning.equals("6")) {
            degree = 145 + 360;
        }
        if (winning.equals("7")) {
            degree = 107 + 360;
        }
        if (winning.equals("8")) {
            degree = 75 + 360;
        }
        if (winning.equals("9")) {
            degree = 37 + 360;
        }
        Log.d("degree_new", String.valueOf(degree));
        Log.d("degree_old", String.valueOf(degreeOld));
        // rotation effect on the center of the wheel
        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(10000);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // we empty the result text view when the animation start
                resultTv.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // we display the correct sector pointed by the triangle at the end of the rotate animation
                resultTv.setText(getSector(360 - (degree % 360)));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // we start the animation
        wheel.startAnimation(rotateAnim);
    }

    private String getSector(int degrees) {
        int i = 0;
        String text = null;

        do {
            // start and end of each sector on the wheel
            float start = HALF_SECTOR * (i * 2 + 1);
            float end = HALF_SECTOR * (i * 2 + 3);
            Log.d("_start", String.valueOf(start));
            Log.d("_end", String.valueOf(end));
//            float start = 45;
//            float end   = 50;

            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                text = sectors[i];
            }

            i++;
            // now we can test our Android Roulette Game :)
            // That's all !
            // In the second part, you will learn how to add some bets on the table to play to the Roulette Game :)
            // Subscribe and stay tuned !

        } while (text == null && i < sectors.length);

        return text;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        //  builder.setTitle("");
//        builder.setMessage("Do you want to exit the game  ");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                DestroyGames();
//                releaseSoundpoll();
//                finish();
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        builder.show();

        super.onBackPressed();
        Functions.Dialog_CancelAppointment(ColorPrediction_Socket.this, "Confirmation", "Leave ?", new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp.equals("yes")) {
                    DestroyGames();
                    releaseSoundpoll();
                    mSocket.disconnect();
                    finish();
                }
            }
        });
    }

    Animation blinksAnimation;
    boolean isBlinkStart = false;

    private void BlinkAnimation(final View view) {

        if (isBlinkStart)
            return;

        isBlinkStart = true;
        view.startAnimation(blinksAnimation);
    }

    private void restartGame() {
        str_0 = "";
        str_1 = "";
        str_2 = "";
        str_3 = "";
        str_4 = "";
        str_5 = "";
        str_6 = "";
        str_7 = "";
        str_8 = "";
        str_9 = "";
        str_10 = "";
        str_11 = "";
        str_12 = "";
        tagamountselected = "";
        canbet = true;
        txtBallence.setText(wallet);
        firstcount = 0;
        count = 0;
        countwidhtanadar = 41;
        widthandar = -430;
        countwidhtbahar = 41;
        widthbahar = -430;
        isConfirm = false;
        bet_id = "";
        betplace = "";
        aaraycards.clear();
        countvaue = 0;
        betcountandar = 1;
        isGameBegning = true;
        betvalue = "";
        VisiblePleasewaitforNextRound(false);
        removeBetChips();
        Log.v("startgame", "start");
        removeChips();
        andharPutBetVisiblity(false);
        txt_catander.setText("0");
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove("tag").commit();
        setSelectedType("");
        setSelectedTypeColor("");

        baharPutBetVisiblity(false);
        txt_catbahar.setText("0");
        rlt1to5.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt6to10.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt11to15.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt16to25.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt26to30.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt31to35.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt36to40.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        rlt41more.setBackground(ContextCompat.getDrawable(context, R.drawable.background_border));
        restartRuleView();
        UserProfile();
        tvBigCoins.setText("");
        tvsmallCoins.setText("");
        findViewById(R.id.rlt_bigchip).setVisibility(View.GONE);
        findViewById(R.id.rlt_smallchip).setVisibility(View.GONE);

        str_big = "0";
        str_small = "0";

        enableDisableView(lnrfollowColr,false);
        enableDisableView(lnrfollowColr2,false);
        enableDisableView(lnrfollowColr3,false);
    }

    private void removeBetChips() {
        andharPutBetVisiblity(false);
        baharPutBetVisiblity(false);
    }

    private void andharPutBetVisiblity(boolean visible) {
        rltmainviewander.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void baharPutBetVisiblity(boolean visible) {
        rltmainviewbahar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void initiAnimation() {
        blinksAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
        blinksAnimation.setDuration(1000);
        blinksAnimation.setRepeatCount(Animation.INFINITE);
        blinksAnimation.setStartOffset(700);
    }

    private void VisiblePleasewaitforNextRound(boolean visible) {

        if (blinksAnimation != null) {
            isBlinkStart = false;
            txtGameRunning.clearAnimation();
            blinksAnimation.cancel();
        }

        txtGameRunning.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (visible) {
//            if(!Functions.checkisStringValid(((TextView) findViewById(R.id.txtcountdown)).getText().toString().trim()))
//                pleasewaintCountDown.start();

            //BlinkAnimation(txtGameRunning);
        } else {
//            pleasewaintCountDown.cancel();
//            pleasewaintCountDown.onFinish();
        }

    }

    private String profile_pic;
    View rtllosesymble1;

    public void initview() {

        // setanimation= AnimationUtils.loadAnimation(this,R.anim.set);

        rl_AnimationView = ((RelativeLayout) findViewById(R.id.sticker_animation_layout));

        rltwinnersymble1 = findViewById(R.id.rltwinnersymble1);
        rtllosesymble1 = findViewById(R.id.rtllosesymble1);

        spin = findViewById(R.id.spinBtn);

        lnrOnlineUser = findViewById(R.id.lnrOnlineUser);
        lnrOnlineUserNew = findViewById(R.id.lnrOnlineUserNew);
        lnrOnlineUser.setVisibility(View.INVISIBLE);
        lnrOnlineUserNew.setVisibility(View.VISIBLE);
        imgpl1circle = findViewById(R.id.imgpl1circle);
        ChipstoUser = findViewById(R.id.ChipstoUser);
        profile_pic = SharePref.getInstance().getString(SharePref.u_pic);
        Picasso.get().load(Const.IMGAE_PATH + profile_pic).into(imgpl1circle);

        imgmaincardsvaluehiostory = findViewById(R.id.imgmaincardsvaluehiostory);


        imgCardsandar = findViewById(R.id.imgCardsandar);
        imgCardsbahar = findViewById(R.id.imgCardsbahar);
        lnrfollow = findViewById(R.id.lnrfollow);
        lnrfollowColr = findViewById(R.id.lnrfollowColr);
        lnrfollowColr2 = findViewById(R.id.lnrfollowColr2);
        lnrfollowColr3 = findViewById(R.id.lnrfollowColr3);
        rltline = findViewById(R.id.rltline);

        txt_room = findViewById(R.id.txt_room);
        txt_gameId = findViewById(R.id.txt_gameId);
        txt_min_max = findViewById(R.id.txt_min_max);
        txt_many_cards = findViewById(R.id.txt_many_cards);
        txtGameFinish = findViewById(R.id.tvStartTimer);

        txtGameBets = findViewById(R.id.txtGameBets);
        Glide.with(context).asGif()
                .load(R.drawable.place_your_bet).into(txtGameBets);
        txtGameRunning = findViewById(R.id.txtGameRunning);
        Glide.with(context).asGif()
                .load(R.drawable.waiting_for_next).into(txtGameRunning);

        imgmaincard = findViewById(R.id.imgmaincard);
        rltandarbet = findViewById(R.id.rltandarbet);
        rlt_centercard = findViewById(R.id.rlt_centercard);
        rltmainviewander = findViewById(R.id.rltmainviewander);
        rltmainviewbahar = findViewById(R.id.rltmainviewbahar);
        rlt1to5 = findViewById(R.id.rlt1to5);
        rlt6to10 = findViewById(R.id.rlt6to10);
        rlt11to15 = findViewById(R.id.rlt11to15);
        rlt16to25 = findViewById(R.id.rlt16to25);

        rlt26to30 = findViewById(R.id.rlt26to30);
        rlt31to35 = findViewById(R.id.rlt31to35);
        rlt36to40 = findViewById(R.id.rlt36to40);
        rlt41more = findViewById(R.id.rlt41more);
        btnRepeat = findViewById(R.id.btnRepeat);

        min_coin = getIntent().getStringExtra("min_coin");
        max_coin = getIntent().getStringExtra("max_coin");
        room_id = getIntent().getStringExtra("room_id");

        img_big = findViewById(R.id.img_big);
        img_small = findViewById(R.id.img_small);

        tvBigCoins = findViewById(R.id.tvBigCoins);
        tvsmallCoins = findViewById(R.id.tvsmallCoins);

        txt_room.setText("ROOM ID " + room_id);

        txt_min_max.setText("Min-Max: " + min_coin + " - " + max_coin);

//        imgCards.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgCards.startAnimation(setanimation);
//            }
//        });

        rlt_centercard.setVisibility(View.GONE);
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                //  Functions.showToast(context, "Need to Discuss");

                if (betplace.length() > 0) {

                    Functions.showToast(context, "You Already Placed Bet");

                } else {

//                    repeatBet();

                }


            }
        });

        btnDouble = findViewById(R.id.btnDouble);
        btnDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish();
                // Functions.showToast(context, "Need to Discuss");

                if (betplace.length() > 0) {
                    float valedou = Float.parseFloat(betvalue);
                    float dobff = valedou * 2;
                    betvalue = dobff + "";
//                    txt_catander.setText(""+betvalue);
//                    txt_catbahar.setText(""+betvalue);
                } else {
                    Functions.showToast(context, "You have not Bet yet. ");

                }


            }
        });


//        btnconfirm = findViewById(R.id.btnconfirm);
//        btnconfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //  finish();
//                //Functions.showToast(context, "Need to Discuss");
//
////                putbetonView();
//            }
//        });

//        btnCANCEL = findViewById(R.id.btnCANCEL);
//        btnCANCEL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                betvalue = "";
//                betplace = "";
//                if (bet_id.length() > 0){
//
//                    cancelbet();
//
//                }else {
//
//                    Functions.showToast(context, "You have not Bet yet. ");
//                    removeBetChips();
//
//
//                }
//
//            }
//        });

        txt_catander = findViewById(R.id.txt_catander);
        txt_catbahar = findViewById(R.id.txt_catbahar);
        rltandarbet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConfirm) {

                    Functions.showToast(context, "Bet Already Confirmed So Not Allowed to Put again");


                } else {

                    if (canbet) {
                        if (tagamountselected.length() > 0) {

//                            baharPutBetVisiblity(false);
//                            txt_catbahar.setText("");

                            if (betplace.equals("1")) {

                                betvalue = "";

                            } else {

                            }

                            betplace = "0";
                            float valedou = 0;
                            if (betvalue.length() > 0) {
                                valedou = Float.parseFloat(betvalue);
                            } else {

                                valedou = 0;
                            }
                            float valedoutagamountselected = 0;
                            if (tagamountselected.length() > 0) {
                                valedoutagamountselected = Float.parseFloat(tagamountselected);
                            } else {

                                valedoutagamountselected = 0;
                            }


                            float betvalueint = valedou + valedoutagamountselected;


                            betvalue = betvalueint + "";
                            andharPutBetVisiblity(true);
//                            txt_catander.setText("" + betvalue);

//                            putbetonView();


                        } else {

                            Functions.showToast(context, "Please Select Bet amount First");

                        }
                    } else {

                        Functions.showToast(context, "Game Already Started You can not Bet");

                    }
                }


            }
        });
        rltbaharbet = findViewById(R.id.rltbaharbet);
        rltbaharbet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isConfirm) {

                    Functions.showToast(context, "Bet Already Confirmed So Not Allowed to Put again");


                } else {


                    if (canbet) {

                        if (tagamountselected.length() > 0) {


//                            andharPutBetVisiblity(false);
//                            txt_catander.setText("");

                            if (betplace.equals("0")) {

                                betvalue = "";

                            } else {


                            }

                            float valedou = 0;
                            if (betvalue.length() > 0) {
                                valedou = Float.parseFloat(betvalue);
                            } else {

                                valedou = 0;
                            }
                            float valedoutagamountselected = 0;
                            if (tagamountselected.length() > 0) {
                                valedoutagamountselected = Float.parseFloat(tagamountselected);
                            } else {

                                valedoutagamountselected = 0;
                            }

                            float betvalueint = valedou + valedoutagamountselected;
                            betvalue = betvalueint + "";
                            baharPutBetVisiblity(true);
//                            txt_catbahar.setText("" + betvalue);
                            //putbet("1");
                            betplace = "1";

//                            putbetonView();

                        } else {

                            Functions.showToast(context, "Please Select Bet amount First");

                        }

                    } else {

                        Functions.showToast(context, "Game Already Started You can not Bet");

                    }
                }

            }
        });
        txtName = findViewById(R.id.txtName);
        //txtGameRunning = findViewById(R.id.txtGameRunning);
        txtBallence = findViewById(R.id.txtBallence);
        helvatikaboldround = Typeface.createFromAsset(getAssets(), "fonts/helvetica-rounded-bold-5871d05ead8de.otf");
        //    txtGameFinish.setTypeface(helvatikaboldround);
        // txtGameRunning.setTypeface(helvatikaboldround);
        //    txtGameBets.setTypeface(helvatikaboldround);
        aaraycards = new ArrayList<>();
        addChipsonView();
        addChipsonViewColor();
        addChipsonViewColor2();
        addChipsonViewColor3();

        findViewById(R.id.imgback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void show_bots_user(JSONArray bot_user) {
        for (int i = 0; i < bot_user.length(); i++) {
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = bot_user.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert jsonObject1 != null;
                bots_lists.add(new Bots_list(
                        jsonObject1.getString("id"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("coin"),
                        jsonObject1.getString("avatar")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        botsAdapter = new BotsAdapter(ColorPrediction_Socket.this, bots_lists);
        recycle_bots.setAdapter(botsAdapter);
    }


    private void addCardinLayout(String cat, int countnumber, ViewGroup lnrLayout) {

        View view = LayoutInflater.from(context).inflate(R.layout.ab_cards_layout, null);
        ImageView imgcards = view.findViewById(R.id.cards);
        view.setTag(cat + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String) view.getTag();
                Functions.showToast(context, tag);
            }
        });
        String uri1 = "@drawable/" + cat.toLowerCase();  // where myresource " +
        int imageResource1 = getResources().getIdentifier(uri1, null,
                getPackageName());
        Picasso.get().load(imageResource1).into(imgcards);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0,
                0,
                -35, 0);

        lnrLayout.addView(view, layoutParams);
        playSound(CARD_SOUND, false);


    }

    boolean animationon = false;
    RelativeLayout rl_AnimationView;
    int coins_count = 10;

    private void ChipsAnimations(View mfromView, View mtoView, ViewGroup rl_AnimationView) {

        animationon = true;


        final View fromView, toView;

        fromView = mfromView;
        toView = mtoView;


        int fromLoc[] = new int[2];
        fromView.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];

//        int toLoc[] = new int[2];
//        toView.getLocationOnScreen(toLoc);
//        float destX = toLoc[0];
//        float destY = toLoc[1];

        Rect myViewRect = new Rect();
        toView.getGlobalVisibleRect(myViewRect);
        float destX = myViewRect.left;
        float destY = myViewRect.top;

        rl_AnimationView.setVisibility(View.VISIBLE);

        ImageView image_chips = creatDynamicChips();

        rl_AnimationView.addView(image_chips);

        if (chips_width <= 0) {
            chips_width = 96;
        }

        int centreX = (int) (andharX + andharWidth / 2) - (chips_width / 2);
        int centreY = (int) (andharY + andharHeight / 2) - (chips_width / 2);

        if (chips_width > 0) {
            destX = destX + new Random().nextInt(andharWidth - chips_width);
        } else
            destX += centreX;

        destY += centreY;

        Animations anim = new Animations();
        float finalDestX = destX;
        float finalDestY = destY;

        Animation a = anim.fromAtoB(startX, startY, destX, destY, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                animationon = false;


//                image_chips.setX(finalDestX);
//                image_chips.setY(finalDestY);

//                if(andharWidth > 0 && andharHeight > 0)
//                {
//
//                }
//
//                rl_AnimationView.removeView(dummyUserleft);
//                rl_AnimationView.removeView(dummyUserright);

            }
        });
        anim.setAnimationView(image_chips);
        image_chips.setAnimation(a);
        a.startNow();

        playSound(CHIPS_SOUND, false);

    }

    private void ChipsAnimations(View mfromView, View mtoView) {

        animationon = true;


        final View fromView, toView, shuttleView;

        fromView = mfromView;
        toView = mtoView;


        int fromLoc[] = new int[2];
        fromView.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];

        int toLoc[] = new int[2];
        toView.getLocationOnScreen(toLoc);
        float destX = toLoc[0];
        float destY = toLoc[1];

        rl_AnimationView.setVisibility(View.VISIBLE);
//        rl_AnimationView.removeAllViews();
        final ImageView sticker = new ImageView(this);

        int stickerId = Functions.GetResourcePath("ic_dt_chips", context);

        int chips_size = (int) getResources().getDimension(R.dimen.chips_size);

        if (stickerId > 0 && Functions.isActivityExist(context))
            LoadImage().load(stickerId).into(sticker);

        sticker.setLayoutParams(new ViewGroup.LayoutParams(chips_size, chips_size));
        rl_AnimationView.addView(sticker);

        shuttleView = sticker;

        Animations anim = new Animations();
        Animation a = anim.fromAtoB(startX, startY, destX, destY - 100, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                shuttleView.setVisibility(View.GONE);
                fromView.setVisibility(View.VISIBLE);
                animationon = false;
                sticker.setVisibility(View.GONE);
                if (coins_count <= 0) {
                    removeChips();
                    rl_AnimationView.removeAllViews();
//                    if(!iswin)
//                        makeLosstoPlayer(SharePref.getU_id());
//                    else
//                        makeWinnertoPlayer(SharePref.getU_id());
                }

            }
        });
        sticker.setAnimation(a);
        a.startNow();


        Rect fromRect = new Rect();
        Rect toRect = new Rect();
        fromView.getGlobalVisibleRect(fromRect);
        toView.getGlobalVisibleRect(toRect);

        playSound(CHIPS_SOUND, false);


    }

    private RequestManager LoadImage() {
        return Glide.with(context);
    }


    private void removeChips() {
        rl_AnimationView.removeAllViews();
    }


    public void PlaySaund(int saund) {

        if (!SharePref.getInstance().isSoundEnable())
            return;

        if (!isInPauseState) {
//            final MediaPlayer mp = MediaPlayer.create(this,
//                    saund);
//            mp.start();
//            try {
//                if (mp.isPlaying()) {
//                    mp.stop();
//                    mp.release();
//                    mp = MediaPlayer.create(context, saund);
//                }
//                mp.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            stopPlaying();
            mp = MediaPlayer.create(this, saund);
            mp.start();

        }


    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }


    private void addCategoryInView(String cat) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview, null);
        TextView txtview = view.findViewById(R.id.txt_cat);
        ImageView ivChips = view.findViewById(R.id.ivChips);
        ivChips.setBackground(Functions.getDrawable(context, ChipsPicker.getInstance().getChip()));
        txtview.setText(cat + "");
        view.setTag(cat + "");


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                // SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                //  String Tag = prefs.getString("tag", "");
                // if (Tag.equals("")) {
//                    Functions.showToast(context ,"Tag=" +Tag);
                tagamountselected = (String) view1.getTag();
                TextView txt = view1.findViewById(R.id.txt_cat);
                txt.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("tag", tagamountselected);
                editor.apply();
//                      Functions.showToast(context ,"tag="+ tag);
                setSelectedType(tagamountselected);

            }
        });


        //  Log.d("101010","title :"+cat+" "+view.getHeight()+" "+view.getWidth());
        lnrfollow.addView(view);


    }

    private void addChipsonView() {
        lnrfollow.removeAllViews();
        chipsDefaultSelection = false;
        addCategoryInView("10", R.drawable.coin_10);
        addCategoryInView("50", R.drawable.coin_50);
        addCategoryInView("100", R.drawable.coin_100);
//        addCategoryInView("500", R.drawable.coin_500);
        addCategoryInView("1000", R.drawable.coin_1000);
        addCategoryInView("5000", R.drawable.coin_5000);
//        addCategoryInView("7500");
    }

    private void addChipsonViewColor() {
        lnrfollowColr.removeAllViews();
        addCategoryInViewColor("12", R.drawable.join_red);
        addCategoryInViewColor("11", R.drawable.join_violet);
        addCategoryInViewColor("10", R.drawable.join_green);
//        addCategoryInView("7500");
    }

    private void addChipsonViewColor2() {
        lnrfollowColr2.removeAllViews();
        addRulesColorinViewColor("0", R.drawable.join_zero_update, lnrfollowColr2);
        addRulesColorinViewColor("1", R.drawable.join_one_update, lnrfollowColr2);
        addRulesColorinViewColor("2", R.drawable.join_two_update, lnrfollowColr2);
        addRulesColorinViewColor("3", R.drawable.join_three_update, lnrfollowColr2);
        addRulesColorinViewColor("4", R.drawable.join_four_update, lnrfollowColr2);
//        addCategoryInView("7500");
    }

    private void addChipsonViewColor3() {
        lnrfollowColr3.removeAllViews();
        addRulesColorinViewColor("5", R.drawable.join_five_update, lnrfollowColr3);
        addRulesColorinViewColor("6", R.drawable.join_six_update, lnrfollowColr3);
        addRulesColorinViewColor("7", R.drawable.join_seven_update, lnrfollowColr3);
        addRulesColorinViewColor("8", R.drawable.join_eight_update, lnrfollowColr3);
        addRulesColorinViewColor("9", R.drawable.join_nine_update, lnrfollowColr3);
//        addCategoryInView("7500");
    }

    private String rulesName(int rule) {
        switch (rule) {
            case 0:
                return "Red-Purple";
            case 1:
                return "Green";
            case 2:
                return "Red";
            case 3:
                return "Green";
            case 4:
                return "Red";
            case 5:
                return "Purple-Green";
            case 6:
                return "Red";
            case 7:
                return "Green";
            case 8:
                return "Red";
            case 9:
                return "Green";
            case 10:
                return "Join Green";
            case 11:
                return "Join Violet";
            case 12:
                return "Join Red";

            default:
                return "" + rule;
        }
    }

    boolean chipsDefaultSelection = false;

    private void addCategoryInView(String cat, int img) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview_chip_bg, null);
        TextView txtview = view.findViewById(R.id.txt_cat);
//        txtview.setVisibility(View.INVISIBLE);
        txtview.setText(cat + "");
        txtview.setBackgroundResource(img);
        view.setTag(cat + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                tagamountselected = (String) view1.getTag();
                TextView txt = view1.findViewById(R.id.txt_cat);
//                txt.setTextColor(Color.parseColor("#ffffff"));
                SharedPreferences.Editor editor = getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("tag", tagamountselected);
                editor.apply();
                setSelectedType(tagamountselected);
//                GameAmount = Integer.parseInt(tagamountselected);
            }
        });

        if (!chipsDefaultSelection) {
            chipsDefaultSelection = true;
//            tagamountselected = (String) view.getTag();           //new code for validation
            SharedPreferences.Editor editor = getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("tag", tagamountselected);
            editor.apply();
            setSelectedType(tagamountselected);
        }
        lnrfollow.addView(view);
    }

    String str_10 = "", str_11 = "", str_12 = "";

    private void addCategoryInViewColor(String cat, int img) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview_chip_bg_clr, null);
        RelativeLayout rltAddedChpisview = view.findViewById(R.id.rltAddedChpisview);
        rltAddedChpisview.setVisibility(View.GONE);
        TextView txtview = view.findViewById(R.id.txt_cat);
//        txtview.setVisibility(View.INVISIBLE);
        txtview.setText(cat + "");
        txtview.setBackgroundResource(img);
        view.setTag(cat + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (betValidation()) {
                    coloramountselected = (String) view1.getTag();
                    TextView txt = view1.findViewById(R.id.txt_cat);
                    TextView tvDragonCoins = view1.findViewById(R.id.tvDragonCoins);
                    if (tagamountselected.equals("")) {
                        Toast.makeText(context, "Please select a valid amount to Bet", Toast.LENGTH_SHORT).show();
                    } else {
                        betplace = coloramountselected; // Set bet FIRST
                        Log.d("BETPLACE_SET", "Bet placed: " + betplace);
                        progressDialog.show();
                        putbet(coloramountselected);
                        //   setSelectedTypeColor(coloramountselected);
                        HashMap params = new HashMap<String, String>();
                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        params.put("user_id", prefs.getString("user_id", ""));
                        params.put("token", prefs.getString("token", ""));
                        params.put("room_id", "1");
                        ApiRequest.Call_Api(context, Const.ColorPrediction, params, new Callback() {
                            @Override
                            public void Responce(String response, String type, Bundle bundle) {
                                if (response != null) {
                                    try {
                                        Functions.LOGE("andar_bahar", "" + response);

                                        JSONObject jsonObject = new JSONObject(response);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        if (code.equalsIgnoreCase("200")) {
                                            progressDialog.dismiss();

                                            str_10 = jsonObject.getString("my_bet_10");
                                            str_11 = jsonObject.getString("my_bet_11");
                                            str_12 = jsonObject.getString("my_bet_12");


                                            Log.d("RES_str_10", "" + str_10);
                                            Log.d("RES_str_11", "" + str_11);
                                            Log.d("RES_str_12", "" + str_12);

                                            if (coloramountselected.equalsIgnoreCase("10")) {
                                                if (!str_10.equals("0")) {
                                                    rltAddedChpisview.setVisibility(View.VISIBLE);
                                                    tvDragonCoins.setText(str_10);
                                                }
                                            }
                                            if (coloramountselected.equalsIgnoreCase("11")) {
                                                if (!str_11.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_11);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("12")) {
                                                if (!str_12.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_12);
                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            if (jsonObject.has("message")) {
                                                Functions.showToast(context, message);

                                            }
                                        }

                                    } catch (JSONException e) {
                                        progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                }


            }
        });
        lnrfollowColr.addView(view);
    }

    TextView winnerView = null;


    //    private void addRulesColorinViewColor(String cat, int img,ViewGroup parent){
//        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview_chip_bg_btn, null);
//        RelativeLayout rltAddedChpisview = view.findViewById(R.id.rltAddedChpisview);
//        rltAddedChpisview.setVisibility(View.GONE);
//        TextView txtview = view.findViewById(R.id.txt_cat);
//        txtview.clearAnimation();
////        txtview.setVisibility(View.INVISIBLE);
//        txtview.setText(cat + "");
//        txtview.setBackgroundResource(img);
//        view.setTag(cat + "");
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view1) {
//
//                if(betValidation())
//                {
//                    rltAddedChpisview.setVisibility(View.VISIBLE);
//                    coloramountselected = (String) view1.getTag();
//                    TextView txt = view1.findViewById(R.id.txt_cat);
//                    lnrfollowColr.removeAllViews();
//                    addChipsonViewColor();
//                    if(tagamountselected.equals("")){
//                        Toast.makeText(context, "Please select a valid amount to Bet", Toast.LENGTH_SHORT).show();
//                    }else{
//                        putbet(coloramountselected);
//                        Toast.makeText(context, ""+coloramountselected, Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//            }
//        });
//        parent.addView(view);
//
//    }
    String str_0 = "", str_1 = "", str_2 = "", str_3 = "", str_4 = "", str_5 = "", str_6 = "", str_7 = "", str_8 = "", str_9 = "";

    private void addRulesColorinViewColor(String cat, int img, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview_chip_bg_btn, null);
        RelativeLayout rltAddedChpisview = view.findViewById(R.id.rltAddedChpisview);
        rltAddedChpisview.setVisibility(View.GONE);
        TextView txtview = view.findViewById(R.id.txt_cat);
        txtview.clearAnimation();
//        txtview.setVisibility(View.INVISIBLE);
        txtview.setText(cat + "");
        txtview.setBackgroundResource(img);
        view.setTag(cat + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (betValidation()) {
                    coloramountselected = (String) view1.getTag();
                    TextView txt = view1.findViewById(R.id.txt_cat);
                    TextView tvDragonCoins = view1.findViewById(R.id.tvDragonCoins);
                    //                   tvDragonCoins.setText(tagamountselected);
//                    lnrfollowColr.removeAllViews();
//                    addChipsonViewColor();
                    if (tagamountselected.equals("")) {
                        Toast.makeText(context, "Please select a valid amount to Bet", Toast.LENGTH_SHORT).show();
                    } else {
                        betplace = coloramountselected; // Set bet FIRST
                        Log.d("BETPLACE_SET", "Bet placed: " + betplace);
                        progressDialog.show();
                        putbet(coloramountselected);
                        //---
                        HashMap params = new HashMap<String, String>();
                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        params.put("user_id", prefs.getString("user_id", ""));
                        params.put("token", prefs.getString("token", ""));
                        params.put("room_id", "1");
                        ApiRequest.Call_Api(context, Const.ColorPrediction, params, new Callback() {
                            @Override
                            public void Responce(String response, String type, Bundle bundle) {
                                if (response != null) {
                                    try {
                                        Functions.LOGE("andar_bahar", "" + response);
                                        Log.d("getStatus_type_", response);

                                        JSONObject jsonObject = new JSONObject(response);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        if (code.equalsIgnoreCase("200")) {
                                            progressDialog.dismiss();

                                            str_0 = jsonObject.getString("my_bet_0");
                                            str_1 = jsonObject.getString("my_bet_1");
                                            str_2 = jsonObject.getString("my_bet_2");
                                            str_3 = jsonObject.getString("my_bet_3");
                                            str_4 = jsonObject.getString("my_bet_4");
                                            str_5 = jsonObject.getString("my_bet_5");
                                            str_6 = jsonObject.getString("my_bet_6");
                                            str_7 = jsonObject.getString("my_bet_7");
                                            str_8 = jsonObject.getString("my_bet_8");
                                            str_9 = jsonObject.getString("my_bet_9");

                                            Log.d("str_0_", "" + str_0);
                                            Log.d("str_1_", "" + str_1);
                                            
                                            // Test win logic with current data
                                            Log.d("WIN_TEST_SOCKET", "Current winning: " + winning + ", betplace: " + betplace);
                                            if (!betplace.isEmpty()) {
                                                boolean testWin = betplace.equals(winning);
                                                Log.d("WIN_TEST_SOCKET", "Would win: " + testWin);
                                            }

                                            if (coloramountselected.equalsIgnoreCase("0")) {
                                                if (!str_0.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_0);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("1")) {
                                                if (!str_1.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_1);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("2")) {
                                                if (!str_2.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_2);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("3")) {
                                                if (!str_3.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_3);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("4")) {
                                                if (!str_4.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_4);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("5")) {
                                                if (!str_5.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_5);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("6")) {
                                                if (!str_6.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_6);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("7")) {
                                                if (!str_7.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_7);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("8")) {
                                                if (!str_8.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_8);
                                            }
                                            if (coloramountselected.equalsIgnoreCase("9")) {
                                                if (!str_9.equals("0"))
                                                rltAddedChpisview.setVisibility(View.VISIBLE);
                                                tvDragonCoins.setText(str_9);
                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            if (jsonObject.has("message")) {
                                                Functions.showToast(context, message);

                                            }
                                        }

                                    } catch (JSONException e) {
                                        progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }

            }
        });
        parent.addView(view);

    }

    private boolean betValidation() {

        if (isConfirm) {

            Functions.showToast(context, "Bet Already Confirmed So Not Allowed to Put again");
            return false;

        } else if (!canbet) {
            Functions.showToast(context, "Game Already Started You can not Bet");
            return false;

        }

        return true;
    }

    private boolean isWine(String cat) {
        return Functions.isStringValid(winning) && winning.equalsIgnoreCase(cat);
    }

    private void setSelectedType(String type) {
        LinearLayout lnrfollow = findViewById(R.id.lnrfollow);
        for (int i = 0; i < lnrfollow.getChildCount(); i++) {
            View view = lnrfollow.getChildAt(i);
            TextView txtview = view.findViewById(R.id.txt_cat);
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeChip);
            if (txtview.getText().toString().equalsIgnoreCase(type)) {
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg);
//                txtview.setTextColor(Color.parseColor("#ffffff"));
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                int _20 = (int) getResources().getDimension(R.dimen.chip_up);
                mlp.setMargins(0, _20, 0, 0);
            } else {
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg_transparent);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                mlp.setMargins(0, 0, 0, 0);
//                txtview.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            }
        }
    }

    private void setSelectedTypeColor(String type) {
        LinearLayout lnrfollow = findViewById(R.id.lnrfollowColr);
        for (int i = 0; i < lnrfollow.getChildCount(); i++) {
            View view = lnrfollow.getChildAt(i);
            TextView txtview = view.findViewById(R.id.txt_cat);
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeChip);
            if (txtview.getText().toString().equalsIgnoreCase(type)) {
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg);
//                txtview.setTextColor(Color.parseColor("#ffffff"));
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                int _20 = (int) getResources().getDimension(R.dimen.chip_up);
                mlp.setMargins(0, _20, 0, 0);
            } else {
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg_transparent);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                mlp.setMargins(0, 0, 0, 0);
//                txtview.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            }
        }
    }


    private void getStatus_bots() {
        HashMap params = new HashMap<String, String>();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        params.put("room_id", "1");
        ApiRequest.Call_Api(context, Const.ColorPrediction, params, new Callback() {
            @Override
            public void Responce(String response, String type, Bundle bundle) {
                if (response != null) {
                    try {
                        bots_lists.clear();
                        Functions.LOGE("AndharBahar", "" + response);
                        Log.v("responce", response);

                        JSONObject jsonObject = new JSONObject(response);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");

                        if (code.equalsIgnoreCase("200")) {

                            JSONArray bot_user = jsonObject.getJSONArray("bot_user");
                            JSONArray arraygame_dataa = jsonObject.getJSONArray("game_data");

                            show_bots_user(bot_user);


                        } else {
                            if (jsonObject.has("message")) {

                                Functions.showToast(context, message);

                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    LinearLayout lnrcancelist;

    private void addLastWinBetonView(JSONArray last_bet) throws JSONException {
        Log.e("RES_last_winning", String.valueOf(last_bet));
        isHistoryShown = false;

        lnrcancelist = findViewById(R.id.lnrcancelist);
        lnrcancelist.removeAllViews();
        for (int i = 0; i < last_bet.length(); i++) {

            String lastbet = last_bet.getJSONObject(i).getString("winning");
            addLastWinBet(lastbet, i);
        }

    }

    /**
     * Parse last_bet array to determine if user placed a bet and won/lost
     * Based on the socket response structure provided by the user
     */
    private void parseLastBetAmount(JSONArray last_bet) {
        Log.e("RES_parseLastBetAmount", "Parsing last_bet: " + String.valueOf(last_bet));

        // Reset bet status
        userPlacedBet = false;
        userWinningAmount = 0.0;
        userWon = false;

        try {
            if (last_bet != null && last_bet.length() > 0) {
                // User placed a bet
                userPlacedBet = true;

                JSONObject lastBetObject = last_bet.getJSONObject(0);
                userWinningAmount = lastBetObject.optDouble("winning_amount", 0.0);

                // Determine if user won based on winning_amount
                // If winning_amount > 0, user won; if <= 0, user lost
                userWon = userWinningAmount > 0;

                Log.e("RES_BetStatus", "User placed bet: " + userPlacedBet +
                      ", Winning amount: " + userWinningAmount +
                      ", User won: " + userWon);
            } else {
                // User did not place any bet
                Log.e("RES_BetStatus", "User did not place any bet");
            }
        } catch (JSONException e) {
            Log.e("RES_parseLastBetAmount", "Error parsing last_bet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addLastWinBet(String items, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dragon_tiger_lastbet, null);
        TextView tvItems = view.findViewById(R.id.tvItems);
        TextView tvItems2 = view.findViewById(R.id.tvItems2);
        ImageView ivlastwinbg = view.findViewById(R.id.ivlastwinbg);
        int itemValue = Integer.parseInt(items);

//        try {
//            ivlastwinbg.setBackground(Functions.getDrawable(context,jackpotRuleStrip[itemValue]));
//        }
//        catch (IndexOutOfBoundsException e)
//        {e.printStackTrace(); }
//        catch (Exception e)
//        {e.printStackTrace(); }
//        ivlastwinbg.setBackground(Functions.getDrawable(context,R.drawable.ic_jackpot_strip_green));
//
//        if(itemValue == 1)
//        {
//            ivlastwinbg.setBackground(Functions.getDrawable(context,R.drawable.ic_jackpot_strip_orange));
//        }

        //  tvItems.setText(""+rulesName(itemValue));
        tvItems2.setVisibility(View.VISIBLE);
        tvItems.setBackground(getResources().getDrawable(rulesImage(itemValue)));
        tvItems2.setBackground(getResources().getDrawable(rulesImage2(itemValue)));
        //    ivlastwinbg.setImageDrawable(getResources().getDrawable(rulesImage(itemValue)));

        if (Functions.isStringValid(Functions.getStringFromTextView(tvItems)))
            lnrcancelist.addView(view);
    }

    private int rulesImage(int ruleVal) {
        switch (ruleVal) {
            case 0:
                return R.drawable.join_zero_update;
            case 1:
                return R.drawable.join_one_update;
            case 2:
                return R.drawable.join_two_update;
            case 3:
                return R.drawable.join_three_update;
            case 4:
                return R.drawable.join_four_update;
            case 5:
                return R.drawable.join_five_update;
            case 6:
                return R.drawable.join_six_update;
            case 7:
                return R.drawable.join_seven_update;
            case 8:
                return R.drawable.join_eight_update;
            case 9:
                return R.drawable.join_nine_update;
            case 10:
                return R.drawable.color_green;
            case 11:
                return R.drawable.color_violet;
            case 12:
                return R.drawable.color_red;
            case 15:
                return R.drawable.color_small;
            case 16:
                return R.drawable.color_big;


            default:
                return 0;
        }
    }

    private int rulesImage2(int ruleVal) {
        switch (ruleVal) {
            case 0:
                return R.drawable.color_big;
            case 1:
                return R.drawable.color_big;
            case 2:
                return R.drawable.color_big;
            case 3:
                return R.drawable.color_big;
            case 4:
                return R.drawable.color_big;
            case 5:
                return R.drawable.color_small;
            case 6:
                return R.drawable.color_small;
            case 7:
                return R.drawable.color_small;
            case 8:
                return R.drawable.color_small;
            case 9:
                return R.drawable.color_small;

            default:
                return 0;
        }
    }


    DisplayMetrics metrics;
    int andharWidth = 0, andharHeight = 0;
    View lnrAndharBoard, lnrBaharBoard;
    float andharX, andharY;

    private void initDisplayMetrics() {
        lnrAndharBoard = findViewById(R.id.lnrAndharBoard);
        lnrBaharBoard = findViewById(R.id.lnrBaharBoard);
        metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lnrAndharBoard.post(new Runnable() {
            @Override
            public void run() {
                andharWidth = lnrAndharBoard.getWidth();
                andharHeight = lnrAndharBoard.getHeight();

                andharX = lnrAndharBoard.getX();
                andharY = lnrAndharBoard.getY();
            }
        });
    }

    int chips_width = 0;

    private ImageView creatDynamicChips() {
        ImageView chips = new ImageView(this);

        int chips_size = (int) getResources().getDimension(R.dimen.chips_size);

        chips.setImageDrawable(Functions.getDrawable(context, ChipsPicker.getInstance().getChip()));

        chips.setLayoutParams(new ViewGroup.LayoutParams(chips_size, chips_size));

        chips.post(new Runnable() {
            @Override
            public void run() {
                chips_width = chips.getWidth();
            }
        });

        return chips;
    }


    private void putbet(final String type) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.CP_PUTBET,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Functions.LOGE("putbet", Const.CP_PUTBET + "\n" + response);
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");


                            if (code.equalsIgnoreCase("200")) {
                                bet_id = jsonObject.getString("bet_id");
                                wallet = jsonObject.getString("wallet");
                                txtBallence.setText(wallet);
//                                Functions.showToast(context, ""+message);

//                                if (type.equals("0")){
//                                    andharPutBetVisiblity(true);
////                                    txt_catander.setText(""+betvalue);
//
//                                }else {
//                                    baharPutBetVisiblity(true);
////                                    txt_catbahar.setText(""+betvalue);
//                                }
                                if (type.equals("16")) {
                                    str_big = String.valueOf((Integer.parseInt(str_big) + Integer.parseInt(tagamountselected)));
                                    findViewById(R.id.rlt_bigchip).setVisibility(View.VISIBLE);
                                    tvBigCoins.setText(str_big);
                                }

                                if (type.equals("15")) {
                                    str_small = String.valueOf((Integer.parseInt(str_small) + Integer.parseInt(tagamountselected)));
                                    findViewById(R.id.rlt_smallchip).setVisibility(View.VISIBLE);
                                    tvsmallCoins.setText(str_small);
                                }

                                betvalue = "";


                            } else {
                                progressDialog.dismiss();
                                if (jsonObject.has("message")) {

                                    Functions.showToast(context, message);


                                }


                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Functions.showToast(context, "Something went wrong");

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));
                params.put("game_id", game_id);
                params.put("bet", type);
//                params.put("amount", betvalue);
                params.put("amount", tagamountselected);    //new
                //  params.put("token", "9959cbfce148e91db099d4936b1a9b66");
                Functions.LOGE("putbet", Const.CP_PUTBET + "\n" + params);
                Log.d("getParams_place_bet:", String.valueOf(params));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void cancelbet() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.CP_CENCEL_BET,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equals("200")) {

                                wallet = jsonObject.getString("wallet");
                                txtBallence.setText(wallet);
                                removeBetChips();


                            }
                            Functions.showToast(context, message);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                Functions.showToast(context, "Something went wrong");

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));

                params.put("game_id", game_id);
                params.put("bet_id", bet_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void repeatBet() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.CP_REPEAT_BET,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        Log.v("Repeat Responce", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equals("200")) {

                                wallet = jsonObject.getString("wallet");
                                String bet = jsonObject.getString("bet");
                                // bet_id = jsonObject.getString("bet_id");
                                String amount = jsonObject.getString("amount");
                                txtBallence.setText(wallet);
                                betvalue = amount;
                                betplace = bet;
                                if (bet.equals("0")) {

                                    andharPutBetVisiblity(true);

//                                    txt_catander.setText(""+amount);
                                } else {
//                                    txt_catbahar.setText(""+amount);

                                    baharPutBetVisiblity(true);
                                }

                            }
                            Functions.showToast(context, message);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                Functions.showToast(context, "Something went wrong");

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("token", prefs.getString("token", ""));

                params.put("game_id", game_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    @Override
    protected void onDestroy() {
        DestroyGames();
        releaseSoundpoll();
        super.onDestroy();
    }

    private void DestroyGames() {

        if (counttimerforstartgame != null) {
            counttimerforstartgame.cancel();
        }

        if (counttimerforcards != null) {
            counttimerforcards.cancel();
        }

        if (timerstatus != null) {
            timerstatus.cancel();
        }

        stopPlaying();
        releaseSoundpoll();
        mSocket.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInPauseState = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isInPauseState = true;
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        pauseSoundPool();
    }

    void pauseSoundPool() {
        if (mSoundPool != null) {
            mSoundPool.pause(COUNTDOWN_SOUND);
            mSoundPool.pause(CHIPS_SOUND);
            mSoundPool.pause(CARD_SOUND);
        }
    }

    String user_id_player1 = "";
    RelativeLayout rltwinnersymble1;

    public void makeWinnertoPlayer(String chaal_user_id) {

        rltwinnersymble1.setVisibility(View.GONE);
        restartWiningAnimation();

        if (chaal_user_id.equals(user_id_player1)) {
            // Play win sound since this method is only called when user actually won
            PlaySaund(R.raw.tpb_battle_won);

            Animation animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            wheel.startAnimation(animRotate);
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wheel.clearAnimation();
                    spin_wheel();
                    restartRuleView();
                    highlightWinView();
                }
            }, 3000);
        }

    }

    private void restartWiningAnimation() {
        if (winnerView != null) {
            winnerView.setText("");
            winnerView.setBackground(null);
            winnerView.setVisibility(View.GONE);
        }
    }

    public void makeLosstoPlayer(String chaal_user_id) {

        rltwinnersymble1.setVisibility(View.GONE);
        rtllosesymble1.setVisibility(View.GONE);

        if (chaal_user_id.equals(user_id_player1)) {
            // Show loss animation and sound
            PlaySaund(R.raw.tpb_battle_won); // You might want to use a different sound for loss

            // Show wheel animation for loss as well
            Animation animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            wheel.startAnimation(animRotate);
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wheel.clearAnimation();
                    spin_wheel();
                    restartRuleView();
                    highlightWinView();
                }
            }, 3000);
        }

    }

    public static final int COUNTDOWN_SOUND = 0;
    public static final int CHIPS_SOUND = 1;
    public static final int CARD_SOUND = 2;

    SoundPool mSoundPool;
    HashMap<Integer, Integer> mSoundMap;

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        mSoundMap = new HashMap<Integer, Integer>();

        if (mSoundPool != null) {
            mSoundMap.put(COUNTDOWN_SOUND, mSoundPool.load(this, R.raw.teenpattitick, 1));
            mSoundMap.put(CHIPS_SOUND, mSoundPool.load(this, R.raw.teenpattichipstotable, 1));
            mSoundMap.put(CARD_SOUND, mSoundPool.load(this, R.raw.teenpatticardflip_android, 1));
        }
    }

    /*
     *Call this function from code with the sound you want e.g. playSound(SOUND_1);
     */
    boolean isTimerStar;

    public void playSound(int sound, boolean loop) {

        if (!SharePref.getInstance().isSoundEnable())
            return;

        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        if (mSoundPool != null) {
            mSoundPool.play(mSoundMap.get(sound), volume, volume, 1, loop ? -1 : 0, 1.0f);
        }
    }


    public void stopSound(int sound) {
        if (mSoundMap != null && mSoundMap.size() > 0 && mSoundPool != null)
            mSoundPool.stop(mSoundMap.get(sound));
    }

    public void releaseSoundpoll() {
        stopSound(COUNTDOWN_SOUND);
        stopSound(CARD_SOUND);
        stopSound(CHIPS_SOUND);


        if (mSoundPool != null) {
            mSoundPool.unload(COUNTDOWN_SOUND);
            mSoundPool.unload(CARD_SOUND);
            mSoundPool.unload(CHIPS_SOUND);
            mSoundMap.clear();
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    public void openBuyChipsActivity(View view) {
        openBuyChipsListActivity();
    }

    private void openBuyChipsListActivity() {
        startActivity(new Intent(context, BuyChipsList.class));
    }

    public void openGameRules(View view) {
        DialogRulesColor.getInstance(ColorPrediction_Socket.this).show();
    }

    private void highlightWinView() {
        if (winning.matches("[0-13]+")) {
            Log.d("winning__", winning);
        }
//        spin_wheel();
        int winnerview = Integer.parseInt(winning);

        ViewGroup viewtype = winnerview <= 4 ? lnrfollowColr2 : lnrfollowColr3;
        for (int i = 0; i < viewtype.getChildCount(); i++) {
            View ruleview = viewtype.getChildAt(i);

            TextView txt_cat = ruleview.findViewById(R.id.txt_cat);
            TextView txtWinnerview = ruleview.findViewById(R.id.txtWinnerview);

            if (isWine(String.valueOf(txt_cat.getText()))) {
                txtWinnerview.clearAnimation();
                txtWinnerview.setBackground(Functions.getDrawable(context, R.drawable.background_border_highlight));
                txtWinnerview.startAnimation(blinksAnimation);
                txt_cat.setVisibility(View.INVISIBLE);
                txtWinnerview.setText(txt_cat.getText().toString().trim());
                winnerView = txtWinnerview;
                // Use the new bet validation logic instead of old betplace comparison
                boolean iswin = userPlacedBet && userWon;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimationUtils(iswin);
                        txtGameRunning.setVisibility(View.VISIBLE);
                        if (last_winning.length() > 0) {
                            try {
                                addLastWinBetonView(last_winning);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 3000);
                break;
            }

        }

    }

    private void restartRuleView() {
        addChipsonViewColor();
        addChipsonViewColor2();
        addChipsonViewColor3();
    }

    private void AnimationUtils(boolean iswin) {

        coins_count = 10;

        // Only show win/loss message if user actually placed a bet
        if (userPlacedBet) {
            if (iswin) {
                Functions.showToast(context, "🎉 YOU WON! 🎉");
            } else {
                Functions.showToast(context, "😞 YOU LOST! 😞");
            }
        } else {
            // User didn't place any bet, no win/loss message
            Log.d("RES_AnimationUtils", "No bet placed, skipping win/loss message");
        }

        // Only animate chips if user placed a bet
        if (userPlacedBet) {
            View toView = null;

            if (iswin) {
                toView = ChipstoUser;
            } else {
                toView = lnrOnlineUser;
            }

            View finalToView = toView;
            new CountDownTimer(2000, 200) {
                @Override
                public void onTick(long millisUntilFinished) {
                    coins_count--;
                    ChipsAnimations(winnerView, finalToView);
//                    ChipsAnimations(rltbaharbet, finalToView);
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

    private void setDataonUser() {

        txtName.setText("" + SharePref.getInstance().getString(SharePref.u_name));
        txtBallence.setText(Variables.CURRENCY_SYMBOL + "" + SharePref.getInstance().getString(SharePref.wallet));

        Glide.with(getApplicationContext())
                .load(Const.IMGAE_PATH + SharePref.getInstance().getString(SharePref.u_pic))
                .placeholder(R.drawable.avatar)
                .into(imgpl1circle);

    }

    private void UserProfile() {

        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE);
        params.put("id", prefs.getString("user_id", ""));
        params.put("fcm", token);

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        params.put("app_version", version + "");
        params.put("token", prefs.getString("token", ""));

        ApiRequest.Call_Api(context, Const.PROFILE, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                if (resp != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        Log.d("profile_res", resp);
                        String code = jsonObject.getString("code");
                        if (code.equalsIgnoreCase("200")) {
                            JSONObject jsonObject0 = jsonObject.getJSONArray("user_data").getJSONObject(0);
                            wallet = jsonObject0.optString("wallet");
                            txtBallence.setText(wallet);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

}