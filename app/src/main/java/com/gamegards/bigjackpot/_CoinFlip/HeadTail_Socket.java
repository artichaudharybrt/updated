package com.gamegards.bigjackpot._CoinFlip;

import static com.gamegards.bigjackpot.Utils.Functions.ANIMATION_SPEED;
import static com.gamegards.bigjackpot._AdharBahar.Fragments.GameFragment.MY_PREFS_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
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
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.gamegards.bigjackpot.Activity.BuyChipsListCrypto;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.BaseActivity;
import com.gamegards.bigjackpot.ChipsPicker;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Animations;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.Utils.Sound;
import com.gamegards.bigjackpot.Utils.Variables;
import com.gamegards.bigjackpot._CoinFlip.cointoss.Cointoss;
import com.gamegards.bigjackpot._ColorPrediction.BotsAdapter;
import com.gamegards.bigjackpot._ColorPrediction.BotsAdapterRight;
import com.gamegards.bigjackpot._ColorPrediction.Bots_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class HeadTail_Socket extends BaseActivity implements View.OnClickListener {

    RecyclerView recycle_bots, recycle_botsRight;
    ArrayList<Bots_list> bots_lists = new ArrayList<>();
    ArrayList<Bots_list> bots_listsRight = new ArrayList<>();
    BotsAdapter botsAdapter;
    BotsAdapterRight botsAdapterRight;

    Activity context = this;

    TextView txtName, txtBallence, txt_gameId, tvWine, tvLose;
    Button btGameAmount;
    ImageView imgpl1circle, ivTigerCard, ivDragonCard, ivGadhi, ivWine, ivLose, txtGameRunning, txtGameBets;

    View rltTiger, rltDragon, rltTie;
    View rltTigerChips, rltDragonChips, rltTieChips;

    View ChipstoDealer, ChipstoUser;


    private final String COIN_HEAD = "Head";
    private final String COIN_TAIL = "Tail";
    private final String TIE = "tie";

    private String BET_ON = "";

    private int minGameAmt = 50;
    private int maxGameAmt = 500;
    private int GameAmount = 50;
    private int StepGameAmount = 50;
    private int _30second = 30000;
    private int GameTimer = 30000;
    private int timer_interval = 1000;

    private String game_id = "";
    CountDownTimer pleasewaintCountDown;

    Sound soundMedia;
    LinearLayout lnrfollow;
    LinearLayout lnrOnlineUser;

    ImageView ivcoin;

    private static final String URL = Const.SOCKET_IP+"head_tail";
    Socket mSocket;

    public String token = "";
    int version = 0 ;

    // Variables to track user's bet status
    private boolean userPlacedBet = false;
    private double userWinningAmount = 0.0;
    private boolean userWon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_tail);

        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};
            mSocket = IO.socket(URL, opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.connect();
        mSocket.emit("head_tail_timer", "head_tail_timer");

        Initialization();
        initSoundPool();
        setDataonUser();
        addChipsonView();

        recycle_bots = findViewById(R.id.recycle_bots);
        recycle_bots.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
        recycle_bots.setHasFixedSize(true);
        recycle_botsRight = findViewById(R.id.recycle_bots_right);
        recycle_botsRight.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
        recycle_botsRight.setHasFixedSize(true);

        mSocket.on("head_tail_timer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //     restartGame();
                        //    aaraycards.clear();

                        String resp = String.valueOf(args[0]);
                        Log.v("RES_Timer_Response", resp);

                        Integer intger = (Integer) args[0];
                        time_remaining = intger;


                        if (intger <= 13){

                            getTextView(R.id.tvStartTimer).setText(intger+"");
                            txtGameBets.setVisibility(View.GONE);
                            if (intger < 0){
                                getTextView(R.id.tvStartTimer).setText("0");
                            }
                        }
                        else {

                            getTextView(R.id.tvStartTimer).setText(intger+"");
                            txtGameBets.setVisibility(View.VISIBLE);

                        }

                    }
                });

            }
        });

        mSocket.on("head_tail_status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String resp = String.valueOf(args[0]);
                        Log.v("RES_Socket_Response", resp);

                        try {

                            JSONObject jsonObject = new JSONObject(resp);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equalsIgnoreCase("200")) {

                                JSONArray arraygame_dataa = jsonObject.getJSONArray("game_data");
                                JSONArray online_users = jsonObject.getJSONArray("online_users");

                                dragon_bet = jsonObject.optInt("dragon_bet");
                                tiger_bet = jsonObject.optInt("tiger_bet");
                                tie_bet = jsonObject.optInt("tie_bet");


                                int online = jsonObject.getInt("online");
                                ((TextView) findViewById(R.id.tvonlineuser)).setText("" + online);

                                JSONArray last_winning = jsonObject.getJSONArray("last_winning");

                                // Parse last_bet array to check if user placed a bet and determine win/loss
                                JSONArray last_bet = jsonObject.optJSONArray("last_bet");
                                parseLastBetAmount(last_bet);

                                for (int i = 0; i < arraygame_dataa.length(); i++) {
                                    JSONObject welcome_bonusObject = arraygame_dataa.getJSONObject(i);

                                    if (last_winning.length() > 0 && !game_id.equals(welcome_bonusObject.getString("id"))) {
                                        addLastWinBetonView(last_winning);
                                    }

                                    game_id = welcome_bonusObject.getString("id");
                                    txt_gameId.setText("GAME ID " + game_id);


                                    main_card = welcome_bonusObject.getString("main_card");
                                    // txt_min_max.setText("Min-Max: "+main_card);
                                    status = welcome_bonusObject.getString("status");
                                    winning = welcome_bonusObject.getString("winning");
                                    Log.d("winner_", winning);
                                    String end_datetime = welcome_bonusObject.getString("end_datetime");
                                    added_date = welcome_bonusObject.getString("added_date");
                                    //    time_remaining  = welcome_bonusObject.optInt("time_remaining");

                                    //  updated_date  = welcome_bonusObject.getString("updated_date");

                                    String uri1 = "@drawable/" + main_card.toLowerCase();  // where myresource " +
                                    int imageResource1 = getResources().getIdentifier(uri1, null,
                                            getPackageName());

                                }
                                String onlineuSer = jsonObject.getString("online");
//                            txt_online.setText("Online User "+onlineuSer);
//                                JSONArray arrayprofile = jsonObject.getJSONArray("profile");
//
////                            for (int i = 0; i < arrayprofile.length() ; i++) {
//                                JSONObject profileObject = arrayprofile.getJSONObject(0);
//
//                                //  GameStatus model = new GameStatus();
//                                user_id  = profileObject.getString("id");
//                                user_id_player1 = user_id;
//                                name  = profileObject.getString("name");
//                                wallet  = profileObject.getString("wallet");
//
//                                profile_pic  = profileObject.getString("profile_pic");
//
//                                Glide.with(getApplicationContext()).load(Const.IMGAE_PATH + profile_pic).into(imgpl1circle);
//
//                                //  txtBallence.setText(wallet);
//                                txtName.setText(name);
//
////                            }

                                JSONArray arraypgame_cards = jsonObject.getJSONArray("game_cards");

                                for (int i = 0; i < arraypgame_cards.length(); i++) {
                                    JSONObject cardsObject = arraypgame_cards.getJSONObject(i);

                                    //  GameStatus model = new GameStatus();
                                    String card = cardsObject.getString("card");
                                    aaraycards.add(card);

                                }

//New Game Started here ------------------------------------------------------------------------
                                Log.v("RES_gamestatus", status + " " + isGameBegning);
                                if (status.equals("0") && !isGameBegning) {

                                    RestartGame(false);

                                    if (time_remaining > 0) {
                                        if (dragon_bet > 0)
                                            ((TextView) findViewById(R.id.tvDragonTotalAmt)).setText("" + dragon_bet);
                                        if (tiger_bet > 0)
                                            ((TextView) findViewById(R.id.tvTigerTotalAmt)).setText("" + tiger_bet);
                                        if (tie_bet > 0)
                                            ((TextView) findViewById(R.id.tvTieTotalAmt)).setText("" + tie_bet);

                                    }

                                } else if (status.equals("0") && isGameBegning) {

                                    if (dragon_bet > 0)
                                        ((TextView) findViewById(R.id.tvDragonTotalAmt)).setText("" + dragon_bet);
                                    if (tiger_bet > 0)
                                        ((TextView) findViewById(R.id.tvTigerTotalAmt)).setText("" + tiger_bet);
                                    if (tie_bet > 0)
                                        ((TextView) findViewById(R.id.tvTieTotalAmt)).setText("" + tie_bet);

                                    addDummyChipsonAndharBahar();
                                    addDummyChipsonAndharBahar();
                                    addDummyChipsonAndharBahar();
                                    addDummyChipsonAndharBahar();
                                    addDummyChipsonAndharBahar();
                                }

//Game Started here
                                if (status.equals("1") && !isGameBegning) {
                                    VisiblePleasewaitforNextRound(true);

                                }

                                if (status.equals("1") && isGameBegning) {


                                    isGameBegning = false;
                                    Log.v("game", "stoped");
                                    if (aaraycards.size() > 0) {

                                        if (counttimerforcards != null) {
                                            counttimerforcards.cancel();
                                        }

                                        counttimerforcards = new CountDownTimer(aaraycards.size() * 1000, 1000) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                isCardsDisribute = true;

                                                makeWinnertoPlayer("");
                                                makeLosstoPlayer("");

                                            }

                                            @Override
                                            public void onFinish() {

                                                VisiblePleasewaitforNextRound(true);

                                                isCardsDisribute = false;


                                                // Only show win/loss if user actually placed a bet
                                                if (userPlacedBet) {
                                                    if (userWon) {
                                                        Log.e("RES_GameResult", "User WON with winning amount: " + userWinningAmount);
                                                        makeWinnertoPlayer(user_id_player1);
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                winGameChipsAnimation(true);
                                                            }
                                                        }, 2000);
                                                    } else {
                                                        Log.e("RES_GameResult", "User LOST with winning amount: " + userWinningAmount);
                                                        makeLosstoPlayer(user_id_player1);
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                winGameChipsAnimation(false);
                                                            }
                                                        }, 2000);
                                                    }
                                                } else {
                                                    Log.e("RES_GameResult", "User did not place any bet - no win/loss notification");
                                                    // Just show the coin flip animation without win/loss notification
                                                }


                                            }


                                        };

//                                    counttimerforcards.start();
                                        stopBetAnim();


                                    }


                                } else {


                                }

                            } else {
                                if (jsonObject.has("message")) {

                                    Functions.showToast(context, message);


                                }


                            }

                            if (status.equals("1")) {
//                            VisiblePleasewaitforNextRound(true);
                                VisiblePleaseBetsAmount(false);
                            } else {
                                VisiblePleasewaitforNextRound(false);

                                if (!isConfirm)
                                    VisiblePleaseBetsAmount(true);

                                makeWinnertoPlayer("");
                                makeLosstoPlayer("");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // this code will be executed after 2 seconds
//                getStatus_bots();
//            }
//        }, 500);
    }

    private void getStatus_bots() {
        HashMap params = new HashMap<String, String>();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        params.put("room_id", "1");
        ApiRequest.Call_Api(context, Const.HeadTailStatus, params, new Callback() {
            @Override
            public void Responce(String response, String type, Bundle bundle) {
                if (response != null) {
                    try {
                        bots_lists.clear();
                        bots_listsRight.clear();
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

        botsAdapter = new BotsAdapter(this, bots_lists);
        recycle_bots.setAdapter(botsAdapter);

        for (int i = 3; i < bot_user.length(); i++) {
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = bot_user.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert jsonObject1 != null;
                bots_listsRight.add(new Bots_list(
                        jsonObject1.getString("id"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("coin"),
                        jsonObject1.getString("avatar")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        botsAdapterRight = new BotsAdapterRight(this, bots_listsRight);
        recycle_botsRight.setAdapter(botsAdapterRight);
    }

    private void addChipsonView() {

        lnrfollow.removeAllViews();
        addCategoryInView("10", R.drawable.coin_10);
        addCategoryInView("50", R.drawable.coin_50);
        addCategoryInView("100", R.drawable.coin_100);
//        addCategoryInView("500", R.drawable.coin_500);
        addCategoryInView("1000", R.drawable.coin_1000);
        addCategoryInView("5000", R.drawable.coin_5000);
//        addCategoryInView("7500");
    }

    String tagamountselected = "";

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
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("tag", tagamountselected);
                editor.apply();
                setSelectedType(tagamountselected);
                GameAmount = Integer.parseInt(tagamountselected);
            }
        });


        lnrfollow.addView(view);
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


    private void addDummyChipsonAndharBahar() {

        View AndharFromView = lnrOnlineUser;
        View AndharToView = rltDragon;

        View BaharFromView = lnrOnlineUser;
        View BaharToView = rltTiger;

        // View from left user bots to andhar board
        View AndharFromView1 = findViewById(R.id.left_user1);
        View AndharFromView2 = findViewById(R.id.left_user2);
        View AndharFromView3 = findViewById(R.id.left_user3);
        View AndharFromView4 = findViewById(R.id.right_user2);
        View AndharFromView5 = findViewById(R.id.right_user3);

        // View from right user bots to bahar board
        View BaharFromView1 = findViewById(R.id.right_user1);
        View BaharFromView2 = findViewById(R.id.right_user2);
        View BaharFromView3 = findViewById(R.id.right_user3);
        View BaharFromView4 = findViewById(R.id.left_user2);
        View BaharFromView5 = findViewById(R.id.left_user1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                DummyChipsAnimations(AndharFromView,AndharToView,rl_AnimationView);
//                DummyChipsAnimations(BaharFromView,BaharToView,rl_AnimationView);

                // Chip Animation From Left Users
                DummyChipsAnimations(AndharFromView1, AndharToView, rl_AnimationView);
                DummyChipsAnimations(AndharFromView2, AndharToView, rl_AnimationView);
                DummyChipsAnimations(AndharFromView3, AndharToView, rl_AnimationView);
                DummyChipsAnimations(AndharFromView4, AndharToView, rl_AnimationView);
                DummyChipsAnimations(AndharFromView5, AndharToView, rl_AnimationView);

                // Chip Animation From Right Users
                DummyChipsAnimations(BaharFromView1, BaharToView, rl_AnimationView);
                DummyChipsAnimations(BaharFromView2, BaharToView, rl_AnimationView);
                DummyChipsAnimations(BaharFromView3, BaharToView, rl_AnimationView);
                DummyChipsAnimations(BaharFromView4, BaharToView, rl_AnimationView);
                DummyChipsAnimations(BaharFromView5, BaharToView, rl_AnimationView);

            }
        }, 500);

    }

    DisplayMetrics metrics;
    int dragonWidth = 0, dragonHeight = 0;
    int tigerWidth = 0, tigerHeight = 0;
    int tieWidth = 0, tieHeight = 0;
    View lnrDragonBoard, lnrTigerBoard, lnrTieBoard;
    float dragonX, dragonY;
    float tigerX, tigerY;
    float tieX, tieY;

    private void initDisplayMetrics() {
        lnrDragonBoard = findViewById(R.id.rltDragon);
        lnrTigerBoard = findViewById(R.id.rltTiger);
        lnrTieBoard = findViewById(R.id.rltTie);
        metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lnrDragonBoard.post(new Runnable() {
            @Override
            public void run() {
                dragonWidth = lnrDragonBoard.getWidth();
                dragonHeight = lnrDragonBoard.getHeight();

                dragonX = lnrDragonBoard.getX();
                dragonY = lnrDragonBoard.getY();
            }
        });

        lnrTigerBoard.post(new Runnable() {
            @Override
            public void run() {
                tigerWidth = lnrTigerBoard.getWidth();
                tigerHeight = lnrTigerBoard.getHeight();

                tigerX = lnrTigerBoard.getX();
                tigerY = lnrTigerBoard.getY();
            }
        });


        lnrTieBoard.post(new Runnable() {
            @Override
            public void run() {
                tieWidth = lnrTieBoard.getWidth();
                tieHeight = lnrTieBoard.getHeight();

                tieX = lnrTieBoard.getX();
                tieY = lnrTieBoard.getY();
            }
        });
    }


    private void DummyChipsAnimations(View mfromView, View mtoView, ViewGroup rl_AnimationView) {
        animationon = true;

        final View fromView = mfromView;
        final View toView = mtoView;

        // Get the starting coordinates of the chip animation
        int[] fromLoc = new int[2];
        fromView.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];

        // Get the destination coordinates of the chip animation
        Rect toViewRect = new Rect();
        toView.getGlobalVisibleRect(toViewRect);
        float toViewLeft = toViewRect.left;
        float toViewTop = toViewRect.top;
        float toViewRight = toViewRect.right;
        float toViewBottom = toViewRect.bottom;

        // Calculate the dimensions of the destination view
        float toViewWidth = toViewRight - toViewLeft;
        float toViewHeight = toViewBottom - toViewTop;

        // Apply 20% padding to the dimensions
        float paddingX = toViewWidth * 0.05f;
        float paddingY = toViewHeight * 0.20f;

        // Adjust the boundaries with the padding
        float paddedLeft = toViewLeft + paddingX;
        float paddedTop = toViewTop + paddingY;
        float paddedRight = toViewRight - paddingX;
        float paddedBottom = toViewBottom - paddingY;

        // Make the animation view visible
        rl_AnimationView.setVisibility(View.VISIBLE);

        // Create the dynamic chip view and add it to the animation view
        ImageView image_chips = creatDynamicChips();
        rl_AnimationView.addView(image_chips);

        // Set default chip width if not set
        if (chips_width <= 0) {
            chips_width = 96;
        }

        // Initialize board dimensions and coordinates
        int boardWidth = dragonWidth, boardHeight = dragonHeight;
        float boardX = dragonX, boardY = dragonY;

        // Adjust board dimensions and coordinates if the destination view is 'rltTie'
        if (toView.getId() == R.id.rltTie) {
            boardWidth = tieWidth;
            boardHeight = tieHeight;
            boardX = tieX;
            boardY = tieY;
        }

        // Calculate the random destination coordinates within the padded area
        Random random = new Random();
        float destX = paddedLeft + random.nextInt((int) (paddedRight - paddedLeft - chips_width));
        float destY = paddedTop + random.nextInt((int) (paddedBottom - paddedTop - chips_width));

        // Create and start the chip animation
        Animations anim = new Animations();
        Animation a = anim.fromAtoB(startX, startY, destX, destY, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                animationon = false;
            }
        });
        image_chips.setAnimation(a);
        a.startNow();

        // Play the chip movement sound effect
        playSound(CHIPS_SOUND, false);
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


    private void Initialization() {

        soundMedia = new Sound();
        lnrOnlineUser = findViewById(R.id.lnrOnlineUser);
        rl_AnimationView = ((RelativeLayout) findViewById(R.id.sticker_animation_layout));
        ChipstoDealer = findViewById(R.id.ChipstoDealer);
        ivcoin = findViewById(R.id.coin);
        ChipstoUser = findViewById(R.id.ChipstoUser);
        btGameAmount = findViewById(R.id.btGameAmount);
        lnrfollow = findViewById(R.id.lnrfollow);

        txtName = findViewById(R.id.txtName);
        imgpl1circle = findViewById(R.id.imgpl1circle);

        ivDragonCard = findViewById(R.id.ivDragonCard);
        ivTigerCard = findViewById(R.id.ivTigerCard);
        ivGadhi = findViewById(R.id.ivGadhi);

        txtBallence = findViewById(R.id.txtBallence);
        txt_gameId = findViewById(R.id.txt_gameId);
        txtGameRunning = findViewById(R.id.txtGameRunning);
        txtGameBets = findViewById(R.id.txtGameBets);

        txtGameRunning = findViewById(R.id.txtGameRunning);
        Glide.with(context).asGif()
                .load(R.drawable.waiting_for_next).into(txtGameRunning);

        txtGameBets = findViewById(R.id.txtGameBets);
        Glide.with(context).asGif()
                .load(R.drawable.place_your_bet).into(txtGameBets);


        ivWine = findViewById(R.id.ivWine);
        ivLose = findViewById(R.id.ivlose);
        tvWine = findViewById(R.id.tvWine);
        tvLose = findViewById(R.id.tvlose);

        rltwinnersymble1 = findViewById(R.id.rltwinnersymble1);
        rtllosesymble1 = findViewById(R.id.rtllosesymble1);

        rltTiger = findViewById(R.id.rltTiger);
        rltDragon = findViewById(R.id.rltDragon);
        rltTie = findViewById(R.id.rltTie);

        rltTigerChips = findViewById(R.id.rltTigerChips);
        rltDragonChips = findViewById(R.id.rltDragonChips);
        rltTieChips = findViewById(R.id.rltTieChips);

        rltTiger.setOnClickListener(this::onClick);
        rltDragon.setOnClickListener(this::onClick);
        rltTie.setOnClickListener(this::onClick);
        findViewById(R.id.imgback).setOnClickListener(this::onClick);
        findViewById(R.id.imgpl1plus).setOnClickListener(this::onClick);
        findViewById(R.id.imgpl1minus).setOnClickListener(this::onClick);

        initDisplayMetrics();


        pleaswaitTimer();
        RestartGame(true);

        setDataonUser();

        //  startService();

        initiAnimation();

    }

    private void initiAnimation() {
        blinksAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
        blinksAnimation.setDuration(1000);
        blinksAnimation.setRepeatCount(Animation.INFINITE);
        blinksAnimation.setStartOffset(700);
    }

    boolean isCardsDisribute = false;

    private TextView getTextView(int id) {

        return ((TextView) findViewById(id));
    }

    @Override
    protected void onDestroy() {
        DestroyGames();
        releaseSoundpoll();
        super.onDestroy();
    }

    private void DestroyGames() {

        stopPlaying();
        releaseSoundpoll();
        mSocket.disconnect();
    }

    public String main_card;
    public String status = "";
    public String winning;
    private String added_date;
    private String user_id, name, wallet;
    private String profile_pic;
    ArrayList<String> aaraycards = new ArrayList<>();
    boolean isGameBegning = false;
    boolean isConfirm = false;
    String bet_id = "";
    String betplace = "";
    boolean canbet = false;
    CountDownTimer counttimerforstartgame;
    CountDownTimer counttimerforcards;
    int time_remaining;
    boolean isCardDistribute = false;

    int dragon_bet = 0;
    int tiger_bet = 0;
    int tie_bet = 0;

    LinearLayout lnrcancelist;

    private void addLastWinBetonView(JSONArray last_bet) throws JSONException {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_lastbet, null);
        TextView tvItems = view.findViewById(R.id.tvItems);
        ImageView ivlastwinbg = view.findViewById(R.id.ivlastwinbg);
        int itemValue = Integer.parseInt(items);
//        try {
//            ivlastwinbg.setBackground(Functions.getDrawable(context,jackpotRuleStrip[itemValue]));
//        }
//        catch (IndexOutOfBoundsException e)
//        {e.printStackTrace(); }
//        catch (Exception e)
//        {e.printStackTrace(); }

        ivlastwinbg.setBackground(Functions.getDrawable(context, R.drawable.ic_head_bg));

        if (itemValue == 1) {
            ivlastwinbg.setBackground(Functions.getDrawable(context, R.drawable.ic_tail_bg));
        }

        tvItems.setText("" + rulesName(itemValue));
        if (Functions.checkStringisValid(Functions.getStringFromTextView(tvItems)))
            lnrcancelist.addView(view);
    }

    private String rulesName(int ruleVal) {
        switch (ruleVal) {
            case 0:
                return COIN_HEAD;
            case 1:
                return COIN_TAIL;
            case 2:
                return TIE;

            default:
                return "";
        }
    }


    int coins_count = 10;
    int cards_count = 2;
    boolean isbetonDragon = false;
    boolean isbetonTiger = false;
    boolean isbetTie = false;
    boolean isAnimationUtilscall = false;

    private void winGameChipsAnimation(boolean iswin) {

        findViewById(R.id.rltFlipCoins)
                .setVisibility(View.GONE);

        if (isAnimationUtilscall)
            return;

        isAnimationUtilscall = true;

        AnimationUtils(iswin, rltDragonChips);
        AnimationUtils(iswin, rltTigerChips);
//        AnimationUtils(iswin, rltTieChips);
    }

    private void AnimationUtils(boolean iswin, View fromView) {

        coins_count = 10;
        isbetonDragon = false;
        isbetonTiger = false;
        isbetTie = false;

        isbetonDragon = BET_ON.equals(COIN_HEAD) ? true : false;
        isbetonTiger = BET_ON.equals(COIN_TAIL) ? true : false;
        isbetTie = BET_ON.equals(TIE) ? true : false;

        // Only show win/loss dialog if user actually placed a bet
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

            View finalFromView = fromView;
            View finalToView = toView;

            new CountDownTimer(2000, 200) {
                @Override
                public void onTick(long millisUntilFinished) {
                    coins_count--;
                    ChipsAnimations(finalFromView, finalToView, iswin);
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
    }

    private void VisiblePleaseBetsAmount(boolean visible) {

        //txtGameBets.setVisibility(visible ? View.VISIBLE : View.GONE);
        txtGameBets.setVisibility(View.GONE);
    }

    private void VisiblePleasewaitforNextRound(boolean visible) {

        if (blinksAnimation != null) {
            isBlinkStart = false;
            txtGameRunning.clearAnimation();
            blinksAnimation.cancel();
        }

        txtGameRunning.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (visible) {
            if (!Functions.checkisStringValid(((TextView) findViewById(R.id.txtcountdown)).getText().toString().trim()))
                pleasewaintCountDown.start();

            BlinkAnimation(txtGameRunning);
        } else {
            pleasewaintCountDown.cancel();
            pleasewaintCountDown.onFinish();
        }


    }

    private void pleaswaitTimer() {
        pleasewaintCountDown = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long second = millisUntilFinished / 1000;

                ((TextView) findViewById(R.id.txtcountdown)).setText(second + "s");

            }

            @Override
            public void onFinish() {
                ((TextView) findViewById(R.id.txtcountdown)).setText("");
            }
        };
    }

    Animation blinksAnimation;
    boolean isBlinkStart = false;

    private void BlinkAnimation(final View view) {

        if (isBlinkStart)
            return;

        isBlinkStart = true;
        view.startAnimation(blinksAnimation);
    }

    private void addCardsTiger(String image_name, int countvaue) {

        int path = 0;
        if (Functions.checkisStringValid(image_name))
            path = Functions.GetResourcePath("" + image_name, context);

        Glide.with(getApplicationContext())
                .load(path > 0 ? path : null)
//                .placeholder(R.drawable.ic_dt_tiger_card)
                .into(ivTigerCard);

    }

    private void addCardDragon(String image_name, int countvaue) {
        int path = 0;
        if (Functions.checkisStringValid(image_name))
            path = Functions.GetResourcePath("" + image_name, context);

        Glide.with(getApplicationContext())
                .load(path > 0 ? path : null)
//                    .placeholder(R.drawable.ic_dt_dragon_card)
                .into(ivDragonCard);
    }


    private void putbet(final String type) {


        HashMap params = new HashMap();
        params.put("user_id", SharePref.getInstance().getString("user_id", ""));
        params.put("token", SharePref.getInstance().getString("token", ""));
        params.put("game_id", game_id);
        params.put("bet", type);
        params.put("amount", "" + GameAmount);

        ApiRequest.Call_Api(context, Const.HeadTailPUTBET, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                Log.v("RES_CHECK","CHECK_BET"+resp);

                if (resp != null) {

                    try {


                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");


                        if (code.equalsIgnoreCase("200")) {
                            bet_id = jsonObject.getString("bet_id");
                            wallet = jsonObject.getString("wallet");
                            txtBallence.setText(wallet);
//                            Functions.showToast(context, "Bet has been added successfully!");

//                            GameAmount = 50;
//                            isConfirm = true;

                            int my_head_bet = jsonObject.optInt("my_head_bet");
                            int my_tail_bet = jsonObject.optInt("my_tail_bet");
                            int my_tie_bet = jsonObject.optInt("my_tie_bet");


                            if (my_head_bet > 0)
                                ((TextView) findViewById(R.id.tvDragonAddedAmt)).setText("" + my_head_bet);
                            if (my_tail_bet > 0)
                                ((TextView) findViewById(R.id.tvTigerAddedAmt)).setText("" + my_tail_bet);
                            if (my_tie_bet > 0)
                                ((TextView) findViewById(R.id.tvTieAddedAmt)).setText("" + my_tie_bet);


                            VisiblePleaseBetsAmount(false);

                        } else {
                            RemoveChips();

                            if (jsonObject.has("message")) {

                                Functions.showToast(context, message);


                            }


                        }

                        //    CALL_API_getGameStatus();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RemoveChips();
                    }
                }


            }
        });
    }

    private void cancelbet() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.HeadTailCENCEL_BET,
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

    private void setDataonUser() {

        txtName.setText("" + SharePref.getInstance().getString(SharePref.u_name));
        txtBallence.setText(Variables.CURRENCY_SYMBOL + "" + SharePref.getInstance().getString(SharePref.wallet));

        Glide.with(getApplicationContext())
                .load(Const.IMGAE_PATH + SharePref.getInstance().getString(SharePref.u_pic))
                .placeholder(R.drawable.avatar)
                .into(imgpl1circle);


    }

    String user_id_player1 = "";
    RelativeLayout rltwinnersymble1;
    View rtllosesymble1;
    boolean isWinnershow = false;

    public void makeWinnertoPlayer(String chaal_user_id) {

//        if(isWinnershow)
//            return;

//        isWinnershow = true;

        rltwinnersymble1.setVisibility(View.GONE);
        addWinLoseImageonView();

        if (chaal_user_id.equals(user_id_player1)) {

            int sound = BET_ON.equals(COIN_HEAD)
                    ? R.raw.coin_flip : R.raw.coin_flip;

            PlaySaund(sound);
//            rltwinnersymble1.setVisibility(View.VISIBLE);

        }

    }

    public void makeLosstoPlayer(String chaal_user_id) {

        rltwinnersymble1.setVisibility(View.GONE);
        rtllosesymble1.setVisibility(View.GONE);
        addWinLoseImageonView();

        int sound = BET_ON.equals(COIN_HEAD)
                ? R.raw.coin_flip : R.raw.coin_flip;


        if (chaal_user_id.equals(user_id_player1)) {
            PlaySaund(sound);
//            rtllosesymble1.setVisibility(View.VISIBLE);

        }

    }

    public void addWinLoseImageonView() {

        if (!Functions.isStringValid(winning))
            return;

        int win = Integer.parseInt(winning);
        Log.v("RES_CHECK_WIN" , "VALUE" + win);
        // 0 Head 1 Tail
        isbetonDragon = win == 0;
        isbetonTiger = win == 1;

        Glide.with(getApplicationContext()).load(isbetonDragon ?
                Functions.getDrawable(context, R.drawable.heads)
                : isbetonTiger ? Functions.getDrawable(context, R.drawable.tails)
                : Functions.getDrawable(context, R.drawable.winner_patti)).into(ivWine);

        ivLose.setImageDrawable(isbetonDragon ?
                Functions.getDrawable(context, R.drawable.heads)
                : isbetonTiger ? Functions.getDrawable(context, R.drawable.tails)
                : Functions.getDrawable(context, R.drawable.ic_dt_tiegame));

        tvWine.setText(isbetonDragon ? "Head Win"
                : isbetonTiger ? "Tail Win"
                : "Win");

        tvLose.setText(isbetonDragon ? "Head Lose"
                : isbetonTiger ? "Head Lose"
                : "Lose");

    }

    private MediaPlayer mp;
    boolean isInPauseState = false;

    public void PlaySaund(int saund) {

        if (!SharePref.getInstance().isSoundEnable())
            return;

        if (!isInPauseState) {
            stopPlaying();
            mp = MediaPlayer.create(this, saund);
            mp.start();

        }


    }

    public void PlaySaund(int sound, boolean isloop) {

        if (!SharePref.getInstance().isSoundEnable())
            return;

        if (!isInPauseState) {
            stopPlaying();

            soundMedia.PlaySong(sound, isloop, context);
        }


    }


    private void stopPlaying() {
        try {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }


        soundMedia.StopSong();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInPauseState = false;
    }

    @Override
    public void onPause() {
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


    @Override
    public void onClick(View v) {

/*
        switch (v.getId()) {
            case R.id.imgback: {
                onBackPressed();
            }
            break;

            case R.id.rltTiger: {
                AddBet(COIN_TAIL);
            }
            break;

            case R.id.rltDragon: {
                AddBet(COIN_HEAD);
            }
            break;

            case R.id.rltTie: {
//                Functions.showToast(context,"Comming Soon!");
                AddBet(TIE);
            }
            break;

            case R.id.imgpl1plus: {

                ChangeGameAmount(true);
            }
            break;

            case R.id.imgpl1minus: {
                ChangeGameAmount(false);
            }
            break;
        }
*/


        if (v.getId() == R.id.imgback) {
            onBackPressed();
        } else if (v.getId() == R.id.rltTiger) {
            AddBet(COIN_TAIL);
        } else if (v.getId() == R.id.rltDragon) {
            AddBet(COIN_HEAD);
        } else if (v.getId() == R.id.rltTie) {
            //                Functions.showToast(context,"Comming Soon!");
            AddBet(TIE);
        } else if (v.getId() == R.id.imgpl1plus) {
            ChangeGameAmount(true);
        } else if (v.getId() == R.id.imgpl1minus) {
            ChangeGameAmount(false);
        }

    }

    private void ChangeGameAmount(boolean isPlus) {


        if (isConfirm) {
            return;

        }

        if (isPlus && GameAmount < maxGameAmt) {
            GameAmount = GameAmount + StepGameAmount;
        } else if (!isPlus && GameAmount > minGameAmt) {
            GameAmount = GameAmount - StepGameAmount;
        }

        btGameAmount.setText(Variables.CURRENCY_SYMBOL + "" + GameAmount);
    }

    private void AddBet(String beton) {

        if (!betValidation())
            return;

        BET_ON = beton;
        String betvalue = beton.equals(COIN_HEAD) ? "0" : beton.equals(COIN_TAIL) ? "1" : "2";
//        if(Functions.checkStringisValid(betvalue))
//            betplace = betplace +","+betvalue;
//        else
        betplace = betvalue;


        if (beton.equals(COIN_TAIL))
            rltTigerChips.setVisibility(beton.equals(COIN_TAIL) ? View.VISIBLE : View.GONE);
        else if (beton.equals(COIN_HEAD))
            rltDragonChips.setVisibility(beton.equals(COIN_HEAD) ? View.VISIBLE : View.GONE);
        else if (beton.equals(TIE))
            rltTieChips.setVisibility(beton.equals(TIE) ? View.VISIBLE : View.GONE);


        if (GameAmount > 0 && betValidation())
            putbet(betplace);
        else {
            rltTigerChips.setVisibility(View.GONE);
            rltTieChips.setVisibility(View.GONE);
            rltDragonChips.setVisibility(View.GONE);
            Functions.showToast(context, "First Select Bet amount");
        }

    }

    private void RestartGame(boolean isFromonCreate) {

        // Hide coin flip overlay when new round starts
        findViewById(R.id.rltFlipCoins).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.tvDragonTotalAmt)).setText("");
        ((TextView) findViewById(R.id.tvTigerTotalAmt)).setText("");
        ((TextView) findViewById(R.id.tvTieTotalAmt)).setText("");


        ((TextView) findViewById(R.id.tvDragonAddedAmt)).setText("");
        ((TextView) findViewById(R.id.tvTigerAddedAmt)).setText("");
        ((TextView) findViewById(R.id.tvTieAddedAmt)).setText("");

        dragon_bet = 0;
        tiger_bet = 0;
        tie_bet = 0;

        RemoveChips();
        rl_AnimationView.removeAllViews();
        addCardDragon("0", 0);
        addCardsTiger("0", 1);

        VisiblePleasewaitforNextRound(false);

        //cancelStartGameTimmer();
        isAnimationUtilscall = false;
        isCardDistribute = false;
        isWinnershow = false;
        txtBallence.setText(wallet);

        removeBet();

        aaraycards.clear();
        if (!isFromonCreate)
            isGameBegning = true;

        setSelectedType("");

        UserProfile();
        getStatus_bots();

    }

    private void removeBet() {
        canbet = true;
        isConfirm = false;
        bet_id = "";
        betplace = "";
        GameAmount = 0;
    }

    private void RemoveChips() {
        BET_ON = "";
        rltTigerChips.setVisibility(View.GONE);
        rltDragonChips.setVisibility(View.GONE);
        rltTieChips.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        // super.onBackPressed();
        Functions.Dialog_CancelAppointment(context, "Confirmation", "Leave ?", new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp.equals("yes")) {
                    releaseSoundpoll();
                    stopPlaying();
                    finish();
                    mSocket.disconnect();
                }
            }
        });
    }

    boolean animationon = false;
    RelativeLayout rl_AnimationView;

    private void ChipsAnimations(View mfromView, View mtoView, boolean iswin) {

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
        Animation a = anim.fromAtoB(startX, startY, destX, destY, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                shuttleView.setVisibility(View.GONE);
                fromView.setVisibility(View.VISIBLE);
                animationon = false;
                sticker.setVisibility(View.GONE);
                if (coins_count <= 0) {
                    RemoveChips();
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

    private void CardAnimationAnimations(View mfromView, View mtoView, boolean isTiger) {

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

        int stickerId = Functions.GetResourcePath("backside_card", context);

        int cards_width = (int) getResources().getDimension(R.dimen.dt_card_width);
        int cards_size = (int) getResources().getDimension(R.dimen.dt_card_hight);

        if (stickerId > 0 && Functions.isActivityExist(context))
            LoadImage().load(stickerId).into(sticker);

        sticker.setLayoutParams(new ViewGroup.LayoutParams(cards_size, cards_size));
        rl_AnimationView.addView(sticker);

        shuttleView = sticker;

        Animations anim = new Animations();
        Animation a = anim.fromAtoB(startX, startY, destX, destY, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                shuttleView.setVisibility(View.GONE);
                fromView.setVisibility(View.VISIBLE);
                animationon = false;
                sticker.setVisibility(View.GONE);
                rl_AnimationView.removeAllViews();

                if (!isTiger) {
                    addCardDragon(aaraycards.get(0), 0);
                } else {
                    addCardsTiger(aaraycards.get(1), 1);
                }

            }
        });
        sticker.setAnimation(a);
        a.startNow();


        Rect fromRect = new Rect();
        Rect toRect = new Rect();
        fromView.getGlobalVisibleRect(fromRect);
        toView.getGlobalVisibleRect(toRect);


        PlaySaund(R.raw.teenpatticardflip_android);


    }

    private RequestManager LoadImage() {
        return Glide.with(getApplicationContext());
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

    public void confirmBooking(View view) {

        if (Functions.checkisStringValid(betplace) && betValidation())
            putbet(betplace);
    }


    public void cancelBooking(View view) {

        cancelbet();
        removeBet();
        RemoveChips();
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
        if (mSoundMap.get(sound) != null)
            mSoundPool.stop(mSoundMap.get(sound));
    }

    public void releaseSoundpoll() {
        stopSound(COUNTDOWN_SOUND);
        stopSound(CARD_SOUND);
        stopSound(CHIPS_SOUND);
        if (mSoundPool != null) {
            mSoundMap.clear();
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    public void openBuyChipsActivity(View view) {
        openBuyChipsListActivity();
    }

    private void openBuyChipsListActivity() {
        startActivity(new Intent(context, BuyChipsListCrypto.class));
    }

    public void openGameRules(View view) {
        DialogRulesHeadtail.getInstance(context).show();
    }

    public void openJackpotLasrWinHistory(View view) {

    }

    ImageView ivBetStatus;

    private void stopBetAnim() {
        ivBetStatus = findViewById(R.id.ivBetStatus);
        findViewById(R.id.rltBetStatus).setVisibility(View.VISIBLE);
        ivBetStatus.setBackgroundResource(R.drawable.iv_bet_stops);

        ivBetStatus.bringToFront();
        ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(200);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        ivBetStatus.startAnimation(fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivBetStatus.clearAnimation();

                int mWinning = Integer.parseInt(winning);
                int mBetPlace = Functions.checkisStringValid(betplace)
                        ? Integer.parseInt(betplace) : -1;

                findViewById(R.id.rltFlipCoins)
                        .setVisibility(View.VISIBLE);
                Cointoss cointoss = new Cointoss(getApplicationContext(), ivcoin,
                        mWinning,
                        mBetPlace, new Callback() {
                    @Override
                    public void Responce(String resp, String type, Bundle bundle) {
                        if (resp.equals("animation_complete")) {
                            // Keep coin visible for 3 seconds to show Head/Tail result
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    counttimerforcards.start();
                                }
                            }, 3000); // 3 second delay to show the result clearly
                        }
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rltBetStatus).setVisibility(View.GONE);
            }
        }, 1250);
    }

    public void UserProfile() {

        HashMap<String, String> params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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

                if(resp != null)
                {
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

}