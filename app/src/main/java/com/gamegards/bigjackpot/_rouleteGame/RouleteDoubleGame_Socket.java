package com.gamegards.bigjackpot._rouleteGame;

import static com.gamegards.bigjackpot.Utils.Functions.ANIMATION_SPEED;
import static com.gamegards.bigjackpot._AdharBahar.Fragments.GameFragment.MY_PREFS_NAME;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
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
import com.gamegards.bigjackpot.Interface.OnItemClickListener;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Animations;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;
import com.gamegards.bigjackpot.Utils.Sound;
import com.gamegards.bigjackpot.Utils.Variables;
import com.gamegards.bigjackpot._RedBlack.menu.DialogRedBlackHistory;
import com.gamegards.bigjackpot._RedBlack.menu.DialogRedBlacklastWinHistory;
import com.gamegards.bigjackpot._rouleteGame.menu.DialogRouletteRules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class RouleteDoubleGame_Socket extends BaseActivity implements View.OnClickListener{
    // sectors of our wheel (look at the image to see the sectors)
    private static final String[] sectors = {"32 red", "15 black",
            "19 red", "4 black", "21 red", "2 black", "25 red", "17 black", "34 red",
            "6 black", "27 red", "13 black", "36 red", "11 black", "30 red", "8 black",
            "23 red", "10 black", "5 red", "24 black", "16 red", "33 black",
            "1 red", "20 black", "14 red", "31 black", "9 red", "22 black",
            "18 red", "29 black", "7 red", "28 black", "12 red", "35 black",
            "3 red", "26 black", "zero"};
    // @BindView(R.id.spinBtn)
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
    private static final float HALF_SECTOR = 360f / 37f / 2f;

    Activity context = this;
    TextView txtName,txtBallence,txt_gameId,tvWine,tvLose;
    Button btGameAmount;
    ImageView ivWine,ivLose;
    ImageView imgpl1circle,txtGameRunning,txtGameBets;
    JSONArray last_winning;
    View ChipstoUser;


    private final String RED = "RED";
    private final String BLACK = "BLACK";
    private final String SET = "SET";
    private final String PURE_SEQ = "PURE SEQ";
    private final String SEQ = "SEQ";
    private final String COLOR = "COLOR";
    private final String PAIR = "PAIR";
//    private final String HIGH_CARD = "HIGH";

    private String BET_ON = "";

    private int minGameAmt = 50;
    private int maxGameAmt = 500;
    //   private int GameAmount = 50;
    int GameAmount = 0;
    private int StepGameAmount = 50;
    private int _30second = 30000;
    private int GameTimer = 30000;
    private int timer_interval = 1000;

    private String game_id = "";
    CountDownTimer pleasewaintCountDown;
    LinearLayout lnrfollow;

    RouleteRulesModel rouleteRulesModel;


    int[] jackpotRuleStrip={
            R.drawable.ic_jackpot_strip_blue,
            R.drawable.ic_jackpot_strip_green,
            R.drawable.ic_jackpot_strip_orange,
            R.drawable.ic_jackpot_strip_purple,
            R.drawable.ic_jackpot_strip_shade,
            R.drawable.ic_jackpot_strip_green
    };

    private final String DEFAULT_CARD = "backside_card";
    View lnrOnlineUser;

    // @BindView(R.id.tvZeroRule)
    View tvZeroRule;

    private static final  String URL = Const.SOCKET_IP+"roulette";
    Socket mSocket;

    public String token = "";
    int version = 0 ;

    int bet1, bet2, bet3, bet4, bet5, bet6, bet7, bet8, bet9, bet10, bet11, bet12, bet13, bet14, bet15, bet16, bet17, bet18, bet19, bet20, bet21, bet22, bet23, bet24, bet25, bet26, bet27, bet28, bet29, bet30, bet31, bet32, bet33, bet34, bet35, bet36, bet37, bet38, bet39, bet40, bet41, bet42, bet43, bet44, bet45, bet46, bet47, bet48, bet0;
    int adapteramount;

    MediaPlayer soundPlayer;
    int totalBetAmount;
    double currentWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulete_double_game_socket);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[] { WebSocket.NAME };
            mSocket = IO.socket(URL,opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.connect();
        mSocket.emit("roulette_timer", "roulette_timer");

        Initialization();

        setDataonUser();

        mSocket.on("roulette_timer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String resp = String.valueOf(args[0]);
                        Log.e("RES_Timer_Response",resp);

                        Integer intger= (Integer)args[0];
                        time_remaining = intger;

                        if (intger < 0){

                            cancelStartGameTimmer();
                            getTextView(R.id.tvStartTimer).setText("0");
                            txtGameBets.setVisibility(View.GONE);
                            //    enableDisableView(rec_rules,false);

                        }else {

                            if (intger >= 15) {
                                // startBetAnim();
                            }

                            getTextView(R.id.tvStartTimer).setText(intger+"");
                            txtGameBets.setVisibility(View.VISIBLE);
                            startBetAnim();

                        }

                    }
                });

            }
        });

        mSocket.on("roulette_status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String resp = String.valueOf(args[0]);
                        Log.e("RES_Socket_Response",resp);

                        try {

                            JSONObject jsonObject = new JSONObject(resp);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equalsIgnoreCase("200")) {

                                JSONArray arraygame_dataa = jsonObject.getJSONArray("game_data");
                                JSONArray online_users = jsonObject.getJSONArray("online_users");
                                int online = jsonObject.getInt("online");

                                // Generate fake user count only once, then reuse it
                                if (fakeUserCount == -1) {
                                    Random random = new Random();
                                    fakeUserCount = random.nextInt(151) + 50; // Random between 50-200
                                }

                                ((TextView) findViewById(R.id.tvonlineuser))
                                        .setText(""+fakeUserCount);
                                last_winning = jsonObject.getJSONArray("last_winning");

//                            if(last_winning.length() > 0)
//                            {
//                                addLastWinBetonView(last_winning);
//                            }

                                for (int i = 0; i < arraygame_dataa.length() ; i++) {
                                    JSONObject welcome_bonusObject = arraygame_dataa.getJSONObject(i);

                                    game_id  = welcome_bonusObject.getString("id");

                                    status  = welcome_bonusObject.getString("status");
                                    winning  = welcome_bonusObject.getString("winning");
                                    // 0 = Red,1 = black
                                    winning_rule  = welcome_bonusObject.optInt("winning_rule");
                                    String end_datetime  = welcome_bonusObject.getString("end_datetime");
                                    added_date  = welcome_bonusObject.getString("added_date");
                                    //    time_remaining  = welcome_bonusObject.optInt("time_remaining");

                                }

//                                JSONArray arrayprofile = jsonObject.getJSONArray("profile");
//
//                                for (int i = 0; i < arrayprofile.length() ; i++) {
//                                    JSONObject profileObject = arrayprofile.getJSONObject(i);
//
//                                    //  GameStatus model = new GameStatus();
//                                    user_id  = profileObject.getString("id");
//                                    user_id_player1 = user_id;
//                                    name  = profileObject.getString("name");
//                                    wallet  = profileObject.getString("wallet");
//
//                                    profile_pic  = profileObject.getString("profile_pic");
//
//                                    Picasso.get()
//                                            .load(Const.IMGAE_PATH + profile_pic)
//                                            .into(imgpl1circle);
//
//
//                                    txtName.setText(name);
//
//                                }


                                SharedPreferences prefs = getSharedPreferences(Homepage.MY_PREFS_NAME, MODE_PRIVATE);
                                user_id_player1 = prefs.getString("user_id", "");

                                JSONArray arraypgame_cards = jsonObject.getJSONArray("game_cards");

                                for (int i = 0; i < arraypgame_cards.length() ; i++) {
                                    JSONObject cardsObject = arraypgame_cards.getJSONObject(i);

                                    //  GameStatus model = new GameStatus();
                                    String card  = cardsObject.getString("card");
                                    aaraycards.add(card);

                                }
//New Game Started here ------------------------------------------------------------------------
                                Functions.LOGE("GameActivity", "status : " + status + " isGameBegning : " + isGameBegning);
                                if (status.equals("0") && !isGameBegning){
                                    //    enableDisableView(rec_rules,false);

                                    RestartGame(false);

//                                    if(time_remaining > 0)
//                                    {
////                                    parseGameUsersAmount(jsonObject);
//                                        startBetAnim();
//                                        CardsDistruButeTimer();
//                                    }
//                                    else {
//                                        cancelStartGameTimmer();
//                                    }

                                }
                                else if (status.equals("0") && isGameBegning){
                                    parseGameUsersAmount(jsonObject);
                                    //    enableDisableView(rec_rules,true);

                                }

//Game Started here
                                if (status.equals("1") && !isGameBegning){
                                    VisiblePleasewaitforNextRound(true);
                                    //    enableDisableView(rec_rules,false);
                                }

                                if (status.equals("1") && isGameBegning){
                                    //   enableDisableView(rec_rules,false);
                                    isGameBegning = false;
                                    Log.v("game" ,"stoped");
                                    if (aaraycards.size() > 0){

                                        cancelStartGameTimmer();
                                        if (counttimerforcards != null){
                                            counttimerforcards.cancel();
                                        }

                                        counttimerforcards = new CountDownTimer(aaraycards.size()*400, 400) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
//                                            isCardsDisribute = true;

                                                makeWinnertoPlayer("");
                                                makeLosstoPlayer("");

                                                if(aaraycards != null && aaraycards.size() >= 2)
                                                {

                                                    isCardDistribute = true;
                                                }

                                                cardDistributeCount++;
                                            }

                                            @Override
                                            public void onFinish() {
//                                                make_spin();
//                                                getStatus();
                                                //secondtimestart(18);
                                                GameAmount = 0;
                                                VisiblePleasewaitforNextRound(true);
                                                UserProfile();

                                                final Handler handler = new Handler(Looper.getMainLooper());
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean iswin = false;

                                                        if(totalBetAmount>0) {
                                                            Log.e("RES_VALUE","total_wallet - "+total_wallet+" "+currentWalletAmount);
                                                            if (Double.parseDouble(total_wallet) > currentWalletAmount) {
                                                                iswin = true;
                                                                findViewById(R.id.tvlose).setVisibility(View.VISIBLE);
                                                            } else {
                                                                iswin = false;
                                                                findViewById(R.id.tvlose).setVisibility(View.VISIBLE);
                                                            }
                                                        }else {
                                                            //  Toast.makeText(context, "no bet", Toast.LENGTH_SHORT).show();
                                                            findViewById(R.id.tvlose).setVisibility(View.INVISIBLE);
                                                        }
                                                        AnimationUtils(iswin);
                                                    }
                                                }, 1000);

//                                                cardDistributeCount = 0;
//                                                isCardsDisribute = false;
//                                                playySound();

                                            }

                                        };

                                        playySound();
                                        Animation animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                                        wheel.startAnimation(animRotate);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                wheel.clearAnimation();
                                                stopBetAnim();
                                            }
                                        }, 3000);

                                    }


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

                                parseLastBetAmount(jsonObject);

                                if(!isConfirm)
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


    }

    public void make_spin() {
        degreeOld = degree % 360;
        // we calculate random angle for rotation of our wheel
//        degree = RANDOM.nextInt(360) + 720;           //random win
//        degree = 270+360;                             //custom win
        if (winning.equals("0")){
            degree = 360+360;
        }
        if (winning.equals("1")){
            degree = 135+360;
        }
        if (winning.equals("2")){
            degree = 300+360;
        }
        if (winning.equals("3")){
            degree = 20+360;
        }
        if (winning.equals("4")){
            degree = 320+360;
        }
        if (winning.equals("5")){
            degree = 175+360;
        }
        if (winning.equals("6")){
            degree = 260+360;
        }
        if (winning.equals("7")){
            degree = 58+360;
        }
        if (winning.equals("8")){
            degree = 205+360;
        }
        if (winning.equals("9")){
            degree = 98+360;
        }
        if (winning.equals("10")){
            degree = 185+360;
        }
        if (winning.equals("11")){
            degree = 225+360;
        }
        if (winning.equals("12")){
            degree = 40+360;
        }
        if (winning.equals("13")){
            degree = 242+360;
        }
        if (winning.equals("14")){
            degree = 118+360;
        }
        if (winning.equals("15")){
            degree = 340+360;
        }
        if (winning.equals("16")){
            degree = 155+360;
        }
        if (winning.equals("17")){
            degree = 283+360;
        }
        if (winning.equals("18")){
            degree = 78+360;
        }
        if (winning.equals("19")){
            degree = 330+360;
        }
        if (winning.equals("20")){
            degree = 127+360;
        }
        if (winning.equals("21")){
            degree = 311+360;
        }
        if (winning.equals("22")){
            degree = 88+360;
        }
        if (winning.equals("23")){
            degree = 195+360;
        }
        if (winning.equals("24")){
            degree = 165+360;
        }
        if (winning.equals("25")){
            degree = 292+360;
        }
        if (winning.equals("26")){
            degree = 10+360;
        }
        if (winning.equals("27")){
            degree = 253+360;
        }
        if (winning.equals("28")){
            degree = 50+360;
        }
        if (winning.equals("29")){
            degree = 70+360;
        }
        if (winning.equals("30")){
            degree = 214+360;
        }
        if (winning.equals("31")){
            degree = 108+360;
        }
        if (winning.equals("32")){
            degree = 350+360;
        }
        if (winning.equals("33")){
            degree = 146+360;
        }
        if (winning.equals("34")){
            degree = 272+360;
        }
        if (winning.equals("35")){
            degree = 30+360;
        }
        if (winning.equals("36")){
            degree = 233+360;
        }

        Log.d("degree_new", String.valueOf(degree));
        Log.d("degree_old", String.valueOf(degreeOld));
        // rotation effect on the center of the wheel
        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnim.setDuration(1250);
        rotateAnim.setDuration(2500);
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
                highlistWinRules(Integer.parseInt(winning),winning_rule);
                resultTv.setText(getSector(360 - (degree % 360)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(last_winning.length() > 0)
                        {
                            try {
                                stopSound();
                                addLastWinBetonView(last_winning);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        txtBallence.setText(wallet);
                        findViewById(R.id.rltOngoinGame).setVisibility(View.VISIBLE);
                    }
                }, 1000);
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
            float end   = HALF_SECTOR * (i * 2 + 3);
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
    private void Initialization() {
        spinBtn = findViewById(R.id.spinBtn);
        resultTv = findViewById(R.id.resultTv);
        wheel = findViewById(R.id.wheel);
        tvZeroRule = findViewById(R.id.tvZeroRule);

        lnrOnlineUser = findViewById(R.id.lnrOnlineUser);
        sound = new Sound();
        initSoundPool();
        rl_AnimationView = ((RelativeLayout)findViewById(R.id.sticker_animation_layout));
        ChipstoUser = findViewById(R.id.ChipstoUser);
        btGameAmount = findViewById(R.id.btGameAmount);
        lnrfollow = findViewById(R.id.lnrfollow);

        initRulesSelectAdapter();

        txtName = findViewById(R.id.txtName);
        imgpl1circle = findViewById(R.id.imgpl1circle);

        txtBallence = findViewById(R.id.txtBallence);
        txt_gameId = findViewById(R.id.txt_gameId);

        txtGameBets = findViewById(R.id.txtGameBets);
        Glide.with(context) .asGif()
                .load(R.drawable.place_your_bet).into(txtGameBets);
        txtGameRunning = findViewById(R.id.txtGameRunning);
        Glide.with(context) .asGif()
                .load(R.drawable.waiting_for_next).into(txtGameRunning);

        ivWine = findViewById(R.id.ivWine);
        ivLose = findViewById(R.id.ivlose);
        tvWine = findViewById(R.id.tvWine);
        tvLose = findViewById(R.id.tvlose);

        rltwinnersymble1=findViewById(R.id.rltwinnersymble1);
        rtllosesymble1=findViewById(R.id.rtllosesymble1);

        findViewById(R.id.imgback).setOnClickListener(this::onClick);
        findViewById(R.id.imgpl1plus).setOnClickListener(this::onClick);
        findViewById(R.id.imgpl1minus).setOnClickListener(this::onClick);
        findViewById(R.id.iv_add).setOnClickListener(this::onClick);

        addChipsonView();

        pleaswaitTimer();
        RestartGame(true);

        setDataonUser();

        initiAnimation();

    }


    public static final int SOUND_1 = 1;
    public static final int SOUND_2 = 2;

    SoundPool mSoundPool;
    HashMap<Integer, Integer> mSoundMap;
    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        mSoundMap = new HashMap<Integer, Integer>();

        if(mSoundPool != null){
            mSoundMap.put(SOUND_1, mSoundPool.load(this, R.raw.buttontouchsound, 1));
            mSoundMap.put(SOUND_2, mSoundPool.load(this, R.raw.teenpattichipstotable, 1));
        }
    }
    /*
     *Call this function from code with the sound you want e.g. playSound(SOUND_1);
     */
    public void playSound(int sound) {

        if(!SharePref.getInstance().isSoundEnable())
            return;

        AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        if(mSoundPool != null){
            mSoundPool.play(mSoundMap.get(sound), volume, volume, 1, 0, 1.0f);
        }
    }


    RouleteRulesAdapter jackpotRulesAdapter;
    int TOTAL_CELLS_PER_ROW = 36;
    RecyclerView rec_rules;
    private void initRulesSelectAdapter() {
        rec_rules= findViewById(R.id.rec_rules);
        final GridLayoutManager mng_layout = new GridLayoutManager(this, TOTAL_CELLS_PER_ROW/3);

        rec_rules.setLayoutManager(mng_layout);
        jackpotRulesAdapter = new RouleteRulesAdapter(context);
        jackpotRulesAdapter.onItemSelectListener(new OnItemClickListener() {
            @Override
            public void Response(View v, int position, Object object) {
                RouleteRulesModel rouleteRulesModel = (RouleteRulesModel) object;
                betplace = ""+ rouleteRulesModel.rule_value;
                putBetonRules("", rouleteRulesModel);
            }
        });
//        tvZeroRule.setOnClickListener(view -> {
//            onButtonClickAnimation(view);
//            betplace ="0";
//            putBetonRules();
//        });

        rec_rules.setAdapter(jackpotRulesAdapter);
        setSetRulesValue();
    }

    private void onButtonClickAnimation(View view) {
        playButtonTouchSound();
        Animation animBounce = AnimationUtils.loadAnimation(context,
                R.anim.bounce);
        animBounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animBounce);
    }

    private void putBetonRules(String whichbet, RouleteRulesModel model) {
        if (Functions.isStringValid(betplace) && betValidation()) {
            if (GameAmount > 0) {
                putbet("" + betplace, whichbet, model);
            } else {
                Functions.showToast(context, "First Select Bet amount");
            }

        }
    }

    List<RouleteRulesModel> rulesModelList = new ArrayList<>();
    private void setSetRulesValue(){
        rulesModelList.clear();
        for (int i = 1; i <= TOTAL_CELLS_PER_ROW; i++) {

            int ruleValue = (int) (((i%12) * 3) - Math.floor(i / 12));

            if(ruleValue < 0)
                ruleValue = (TOTAL_CELLS_PER_ROW + 1) + ruleValue;

            rulesModelList.add(new RouleteRulesModel(""+ruleValue,ruleValue,0,0,"", RouleteRulesModel.TYPE_HIGH));
        }
        jackpotRulesAdapter.setArraylist(rulesModelList);

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

        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview_chip_bg,  null);
        TextView txtview = view.findViewById(R.id.txt_cat);
//        txtview.setVisibility(View.INVISIBLE);
        txtview.setText(cat+"");
        txtview.setBackgroundResource(img);
        view.setTag(cat+"");


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

            if(txtview.getText().toString().equalsIgnoreCase(type)){
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg);
//                txtview.setTextColor(Color.parseColor("#ffffff"));
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                int _20 = (int)getResources().getDimension(R.dimen.chip_up);
                mlp.setMargins(0, _20, 0, 0);
            }else{
                relativeLayout.setBackgroundResource(R.drawable.glow_circle_bg_transparent);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                mlp.setMargins(0, 0, 0, 0);
//                txtview.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            }

        }

    }



    private void initiAnimation() {
        blinksAnimation = AnimationUtils.loadAnimation(context,R.anim.blink);
        blinksAnimation.setDuration(1000);
        blinksAnimation.setRepeatCount(Animation.INFINITE);
        blinksAnimation.setStartOffset(700);
    }

    boolean isCardsDisribute = false;

    CountDownTimer gameStartTime;
    boolean isGameTimerStarted = false;
    private void CardsDistruButeTimer(){

        if(isGameTimerStarted && getTextView(R.id.tvStartTimer).getVisibility() == View.VISIBLE)
            return;

        gameStartTime = new CountDownTimer((time_remaining * timer_interval),timer_interval) {
            @Override
            public void onTick(long millisUntilFinished) {

                isGameTimerStarted = true;
                float count = millisUntilFinished/timer_interval;

                getTextView(R.id.tvStartTimer).setVisibility(View.VISIBLE);
                getTextView(R.id.tvStartTimer).setText("Betting "+count+"s");

//                PlaySaund(R.raw.teenpattitick);

                for (int i = 0; i < rec_rules.getChildCount(); i++) {
                    RouleteRulesAdapter.holder holder = (RouleteRulesAdapter.holder) rec_rules.getChildViewHolder(rec_rules.getChildAt(i));
                    View view = holder.itemView.findViewById(R.id.tvJackpotHeading);
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if (view != null)
                            {
//                                DummyChipsAnimations(lnrOnlineUser, view, rl_AnimationView);
//                                DummyChipsAnimations(lnrOnlineUser, view, rl_AnimationView);
//                                DummyChipsAnimations(lnrOnlineUser, view, rl_AnimationView);
//                                DummyChipsAnimations(lnrOnlineUser, view, rl_AnimationView);
                            }
                        }
                    });

                }
            }

            @Override
            public void onFinish() {
                stopPlaying();
                isGameTimerStarted = false;
                GameAmount = 0;

            }
        };


        gameStartTime.start();

    }

    private void cancelStartGameTimmer(){
        if(gameStartTime != null)
        {
            gameStartTime.cancel();
            gameStartTime.onFinish();
        }
    }

    private TextView getTextView(int id){

        return ((TextView) findViewById(id));
    }

    @Override
    protected void onDestroy() {
        DestroyGames();
        super.onDestroy();
    }

    private void DestroyGames(){

        cancelStartGameTimmer();

        stopPlaying();
        mSocket.disconnect();
    }

    public String status = "";
    public String winning;
    public int winning_rule ;
    private String added_date;
    private String user_id,name,wallet,total_wallet;
    private String profile_pic;
    private int fakeUserCount = -1; // Store fake user count to avoid regenerating
    ArrayList<String> aaraycards  = new ArrayList<>();
    boolean isGameBegning = false;
    boolean isConfirm = false;
    String bet_id = "";
    String betplace = "";
    boolean canbet = false;
    String betvalue = "";
    CountDownTimer counttimerforstartgame;
    CountDownTimer counttimerforcards;
    int time_remaining;
    boolean isCardDistribute = false;
    boolean isBetputes = false;
    int cardDistributeCount = 0;

    LinearLayout lnrcancelist;
    private void addLastWinBetonView(JSONArray last_bet) throws JSONException {
        lnrcancelist = findViewById(R.id.lnrcancelist);
        lnrcancelist.removeAllViews();
        for (int i = 0; i < last_bet.length() ; i++) {

            String lastbet = last_bet.getJSONObject(i).getString("winning");

            addLastWinBet(lastbet,i);
        }

    }

    private void addLastWinBet(String items, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_last_bet,null);
        TextView tvItems = view.findViewById(R.id.tvItems);
        ImageView ivlastwinbg = view.findViewById(R.id.ivlastwinbg);
        int itemValue = Integer.parseInt(items);
        try {
            ivlastwinbg.setBackground(Functions.getDrawable(context,jackpotRuleStrip[new Random().nextInt(0)]));
        }
        catch (IndexOutOfBoundsException e)
        {e.printStackTrace(); }
        catch (Exception e)
        {e.printStackTrace(); }
        tvItems.setText(""+rulesName(itemValue));
        if(Functions.checkStringisValid(Functions.getStringFromTextView(tvItems)))
            lnrcancelist.addView(view);
    }

    private void highlistWinRules(int winning_rule_value,int ruleWin) {

        try {
            for (RouleteRulesModel model: rulesModelList) {

                if(ruleWin == model.getWining_rule_value())
                {
                    model.setWine(true);
                }

                if(model.rule_value == winning_rule_value)
                {
                    model.setWine(true);
                }

            }

            if(Functions.isStringValid(betplace) && (winning_rule_value == Integer.parseInt(betplace)))
            {
                PlaySaund(R.raw.tpb_battle_won);
            }
            else if(Functions.isStringValid(betplace)) {
            }

            jackpotRulesAdapter.notifyDataSetChanged();

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private String rulesName(int ruleVal){

        for (RouleteRulesModel rouleteRulesModel : rulesModelList) {
            int rule_value = rouleteRulesModel.rule_value;

            if(ruleVal == rule_value)
            {
                return rouleteRulesModel.getRule_type();
            }

        }

        return ""+ruleVal;
    }

    private void setHighlightedBackgroundforCard(int winning_rule_value) {
//        ivCardbg.setBackground(Functions.getDrawable(context,R.drawable.ic_jackpot_cardsbg_selected));
    }

    private void parseLastBetAmount(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("last_bet");
            if(jsonArray.length() > 0)
            {
                JSONObject lastbetObject = jsonArray.getJSONObject(0);
                int id = lastbetObject.getInt("id");
                int bet = lastbetObject.getInt("bet");
                int amount = lastbetObject.getInt("amount");

                animatedUsersPutAmount(id,bet,amount);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeTotalAddedAmount(){
        red_amount = 0;
        black_amount = 0;
        pair_amount = 0;
        color_amount = 0;
        sequence_amount = 0;
        pure_sequence_amount = 0;

        for (RouleteRulesModel model: rulesModelList) {
            model.added_amount = 0;
        }

        //   jackpotRulesAdapter.notifyDataSetChanged();
    }

    private void animatedUsersPutAmount(int id,int bet, int amount) {
        for (RouleteRulesModel model: rulesModelList) {
            if(model.rule_value == bet)
            {
                model.setLast_added_id(id);
                model.setLast_added_rule_value(bet);
                model.setLast_added_amount(amount);
                model.setAnimatedAddedAmount(true);
                break;
            }
        }

        //   jackpotRulesAdapter.notifyDataSetChanged();

    }

    int set_amount;
    int pure_sequence_amount;
    int sequence_amount;
    int color_amount;
    int pair_amount;
    int black_amount;
    int red_amount;
    private void parseGameUsersAmount(JSONObject jsonObject) throws JSONException {
        int total_jackpot_amount = 0;

        red_amount = 0;
        black_amount = 0;
        pair_amount = 0;
        color_amount = 0;
        sequence_amount = 0;
        pure_sequence_amount = 0;

        for (RouleteRulesModel model: rulesModelList) {

            if(model.rule_type.equalsIgnoreCase(SET))
            {
                set_amount = jsonObject.optInt("set_amount");
                total_jackpot_amount = total_jackpot_amount + set_amount;
                model.added_amount = set_amount;
            }
            else if(model.rule_type.equalsIgnoreCase(PURE_SEQ))
            {
                pure_sequence_amount = jsonObject.optInt("pure_sequence_amount");
                total_jackpot_amount = total_jackpot_amount + pure_sequence_amount;
                model.added_amount = pure_sequence_amount;
            }else if(model.rule_type.equalsIgnoreCase(SEQ))
            {
                sequence_amount = jsonObject.optInt("sequence_amount");
                total_jackpot_amount = total_jackpot_amount + sequence_amount;
                model.added_amount = sequence_amount;
            }else if(model.rule_type.equalsIgnoreCase(COLOR))
            {
                color_amount = jsonObject.optInt("color_amount");
                total_jackpot_amount = total_jackpot_amount + color_amount;
                model.added_amount = color_amount;
            }else if(model.rule_type.equalsIgnoreCase(PAIR))
            {
                pair_amount = jsonObject.optInt("pair_amount");
                total_jackpot_amount = total_jackpot_amount + pair_amount;
                model.added_amount = pair_amount;
            }else if(model.rule_type.equalsIgnoreCase(BLACK))
            {
                red_amount = jsonObject.optInt("red_amount");
                total_jackpot_amount = total_jackpot_amount + pair_amount;
                model.added_amount = pair_amount;
            }else if(model.rule_type.equalsIgnoreCase(RED))
            {
                black_amount = jsonObject.optInt("black_amount");
                total_jackpot_amount = total_jackpot_amount + pair_amount;
                model.added_amount = pair_amount;
            }

        }

        //   jackpotRulesAdapter.notifyDataSetChanged();
    }

    int coins_count = 10;

    private void VisiblePleaseBetsAmount(boolean visible){

//        findViewById(R.id.rltOngoinGame).setVisibility(visible ? View.VISIBLE : View.GONE);

    }

    boolean isPleasewaitShowed = false;
    private void VisiblePleasewaitforNextRound(boolean visible){

        if(isPleasewaitShowed && visible)
            return;

        if(visible)
            isPleasewaitShowed = true;

        if(blinksAnimation != null)
        {
            isBlinkStart = false;
            txtGameRunning.clearAnimation();
            blinksAnimation.cancel();
        }

        findViewById(R.id.rltOngoinGame).setVisibility(visible ? View.VISIBLE : View.GONE);
//        txtGameRunning.setVisibility(visible ? View.VISIBLE : View.GONE);

        if(visible)
        {
            if(!Functions.checkisStringValid(((TextView) findViewById(R.id.txtcountdown)).getText().toString().trim()))
                pleasewaintCountDown.start();

//            BlinkAnimation(txtGameRunning);
        }
        else {
            pleasewaintCountDown.cancel();
            pleasewaintCountDown.onFinish();
        }


    }

    private void pleaswaitTimer(){
        pleasewaintCountDown = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long second = millisUntilFinished/1000;

//                ((TextView) findViewById(R.id.txtcountdown)).setText(second+"s");

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

        if(isBlinkStart)
            return;

        view.setVisibility(View.VISIBLE);
        isBlinkStart = true;
        view.startAnimation(blinksAnimation);
    }

    private void TranslateLayout(ImageView imageView, int path){

        if(path > 0)
            cardflipSound();

        int distance = 8000;

        float scale = context.getResources().getDisplayMetrics().density;
        imageView.setCameraDistance(distance * scale);


        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.out_animation);
        set.setTarget(imageView);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

//                Glide.with(context)
//                        .load(path > 0 ? path : R.drawable.card_bg)
//                        .placeholder(R.drawable.card_bg)
//                        .into(imageView);

                imageView.setImageDrawable(path > 0 ? getDrawable(path) : getDrawable(R.drawable.card_bg));

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                Glide.with(context)
//                        .load(path > 0 ? path : R.drawable.card_bg)
//                        .placeholder(R.drawable.card_bg)
//                        .into(imageView);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();


    }


    private void putbet(final String typee, String whichbet, RouleteRulesModel model) {


        HashMap params = new HashMap();
        params.put("user_id", SharePref.getInstance().getString("user_id", ""));
        params.put("token", SharePref.getInstance().getString("token", ""));
        params.put("game_id", game_id);
        params.put("bet", ""+typee);
        params.put("amount", ""+GameAmount);

        ApiRequest.Call_Api(context, Const.RoulettePUTBET, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                if(resp != null)
                {

                    try {


                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");


                        if (code.equalsIgnoreCase("200")) {
                            playChipsSound();
                            bet_id = jsonObject.getString("bet_id");
                            wallet = jsonObject.getString("wallet");
                            txtBallence.setText(wallet);
                            //  Functions.showToast(context, "Bet has been added successfully!");
                            adapteramount = 0;

                            if (typee.equals("1")) {
                                bet1 += GameAmount;
                                adapteramount = bet1;
                            } else if (typee.equals("2")) {
                                bet2 += GameAmount;
                                adapteramount = bet2;
                            } else if (typee.equals("3")) {
                                bet3 += GameAmount;
                                adapteramount = bet3;
                            } else if (typee.equals("4")) {
                                bet4 += GameAmount;
                                adapteramount = bet4;
                            } else if (typee.equals("5")) {
                                bet5 += GameAmount;
                                adapteramount = bet5;
                            } else if (typee.equals("6")) {
                                bet6 += GameAmount;
                                adapteramount = bet6;
                            } else if (typee.equals("7")) {
                                bet7 += GameAmount;
                                adapteramount = bet7;
                            } else if (typee.equals("8")) {
                                bet8 += GameAmount;
                                adapteramount = bet8;
                            } else if (typee.equals("9")) {
                                bet9 += GameAmount;
                                adapteramount = bet9;
                            } else if (typee.equals("10")) {
                                bet10 += GameAmount;
                                adapteramount = bet10;
                            } else if (typee.equals("11")) {
                                bet11 += GameAmount;
                                adapteramount = bet11;
                            } else if (typee.equals("12")) {
                                bet12 += GameAmount;
                                adapteramount = bet12;
                            } else if (typee.equals("13")) {
                                bet13 += GameAmount;
                                adapteramount = bet13;
                            } else if (typee.equals("14")) {
                                bet14 += GameAmount;
                                adapteramount = bet14;
                            } else if (typee.equals("15")) {
                                bet15 += GameAmount;
                                adapteramount = bet15;
                            } else if (typee.equals("16")) {
                                bet16 += GameAmount;
                                adapteramount = bet16;
                            } else if (typee.equals("17")) {
                                bet17 += GameAmount;
                                adapteramount = bet17;
                            } else if (typee.equals("18")) {
                                bet18 += GameAmount;
                                adapteramount = bet18;
                            } else if (typee.equals("19")) {
                                bet19 += GameAmount;
                                adapteramount = bet19;
                            } else if (typee.equals("20")) {
                                bet20 += GameAmount;
                                adapteramount = bet20;
                            } else if (typee.equals("21")) {
                                bet21 += GameAmount;
                                adapteramount = bet21;
                            } else if (typee.equals("22")) {
                                bet22 += GameAmount;
                                adapteramount = bet22;
                            } else if (typee.equals("23")) {
                                bet23 += GameAmount;
                                adapteramount = bet23;
                            } else if (typee.equals("24")) {
                                bet24 += GameAmount;
                                adapteramount = bet24;
                            } else if (typee.equals("25")) {
                                bet25 += GameAmount;
                                adapteramount = bet25;
                            } else if (typee.equals("26")) {
                                bet26 += GameAmount;
                                adapteramount = bet26;
                            } else if (typee.equals("27")) {
                                bet27 += GameAmount;
                                adapteramount = bet27;
                            } else if (typee.equals("28")) {
                                bet28 += GameAmount;
                                adapteramount = bet28;
                            } else if (typee.equals("29")) {
                                bet29 += GameAmount;
                                adapteramount = bet29;
                            } else if (typee.equals("30")) {
                                bet30 += GameAmount;
                                adapteramount = bet30;
                            } else if (typee.equals("31")) {
                                bet31 += GameAmount;
                                adapteramount = bet31;
                            } else if (typee.equals("32")) {
                                bet32 += GameAmount;
                                adapteramount = bet32;
                            } else if (typee.equals("33")) {
                                bet33 += GameAmount;
                                adapteramount = bet33;
                            } else if (typee.equals("34")) {
                                bet34 += GameAmount;
                                adapteramount = bet34;
                            } else if (typee.equals("35")) {
                                bet35 += GameAmount;
                                adapteramount = bet35;
                            } else if (typee.equals("36")) {
                                bet36 += GameAmount;
                                adapteramount = bet36;
                            }

                            try {
                                model.select_amount = adapteramount;
                                jackpotRulesAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                            }

                            betvalue = "";
                            totalBetAmount+= Integer.parseInt(tagamountselected);
                            currentWalletAmount = Double.parseDouble(wallet);
                            Log.e("RES_VALUE","currentWalletAmount - "+currentWalletAmount);
//                            isConfirm = true;
                            isBetputes = true;

                            VisiblePleaseBetsAmount(false);
                            addbetcoinlayout(whichbet);

                        } else {
                            RemoveChips();
                            removebetcoinlayout();
                            GameAmount = 0;

                            if (jsonObject.has("message")) {

                                Functions.showToast(context, message);

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        RemoveChips();
                        removebetcoinlayout();
                        GameAmount = 0;
                    }
                }


            }
        });
    }

    private void cancelbet() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.RouletteCENCEL_BET,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");

                            if (code.equals("200")){

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

        txtName.setText(""+ SharePref.getInstance().getString(SharePref.u_name));
        txtBallence.setText(Variables.CURRENCY_SYMBOL+""+ SharePref.getInstance().getString(SharePref.wallet));
        txtBallence.setText(SharePref.getInstance().getString(SharePref.wallet));


        Glide.with(context)
                .load(Const.IMGAE_PATH + SharePref.getInstance().getString(SharePref.u_pic))
                .placeholder(R.drawable.avatar)
                .into(imgpl1circle);


    }

    String user_id_player1 = "";
    RelativeLayout rltwinnersymble1;
    View rtllosesymble1;
    public void makeWinnertoPlayer(String chaal_user_id) {

        rltwinnersymble1.setVisibility(View.GONE);

        if (chaal_user_id.equals(user_id_player1)) {
            PlaySaund(R.raw.tpb_battle_won);
            rltwinnersymble1.setVisibility(View.VISIBLE);

        }

    }

    public void makeLosstoPlayer(String chaal_user_id) {

        rltwinnersymble1.setVisibility(View.GONE);
        rtllosesymble1.setVisibility(View.GONE);

        if (chaal_user_id.equals(user_id_player1)) {
          //  PlaySaund(R.raw.game_loos_track);
            rtllosesymble1.setVisibility(View.VISIBLE);

        }

    }

    private MediaPlayer mp;
    boolean isInPauseState = false;
    public void PlaySaund(int saund) {

        if(!SharePref.getInstance().isSoundEnable())
            return;

        if (!isInPauseState) {
            stopPlaying();
            mp = MediaPlayer.create(this, saund);
            mp.start();

        }


    }

    Sound sound;
    public void cardflipSound(){

        if (!isInPauseState) {
            mp = MediaPlayer.create(this, R.raw.teenpatticardflip_android);
            mp.start();
        }
    }

    public void playChipsSound(){
        if (!isInPauseState) {
            playSound(SOUND_2);
        }
    }

    public void playButtonTouchSound() {
        if (!isInPauseState) {
            playSound(SOUND_1);
        }
    }

    public void playCountDownSound(){

//        if(!isInPauseState && !sound.isSoundPlaying())
//        {
//            sound.PlaySong(R.raw.count_down_timmer,true,context);
//        }

    }



    private void stopPlaying() {
        if (mp != null) {
            sound.StopSong();
            mp.stop();
            mp.release();
            mp = null;
        }
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

    }

    @Override
    public void onClick(View v) {

       /* switch (v.getId())
        {
            case R.id.imgback:
            {
                onBackPressed();
            }
            break;

            case R.id.imgpl1plus:
            {

                ChangeGameAmount(true);
            }
            break;

            case R.id.imgpl1minus:
            {
                ChangeGameAmount(false);
            }
            break;

            case R.id.iv_add:{
                openBuyChipsListActivity();
            }
        }*/

        if (v.getId() == R.id.imgback) {
            onBackPressed();
        } else if (v.getId() == R.id.imgpl1plus) {
            ChangeGameAmount(true);
        } else if (v.getId() == R.id.imgpl1minus) {
            ChangeGameAmount(false);
        } else if (v.getId() == R.id.iv_add) {
            openBuyChipsListActivity();
        }

    }

    private void openBuyChipsListActivity() {
        startActivity(new Intent(context, BuyChipsList.class));
    }

    private void ChangeGameAmount(boolean isPlus){


        if (isConfirm) {
            return;

        }

        if(isPlus && GameAmount < maxGameAmt)
        {
            GameAmount = GameAmount + StepGameAmount ;
        }
        else if(!isPlus && GameAmount > minGameAmt)
        {
            GameAmount = GameAmount - StepGameAmount ;
        }

        btGameAmount.setText(Variables.CURRENCY_SYMBOL+""+GameAmount);
    }

    private void RestartGame(boolean isFromonCreate){

        removeTotalAddedAmount();


        rl_AnimationView.removeAllViews();
        RemoveChips();
        setSetRulesValue();

        VisiblePleasewaitforNextRound(false);

        cancelStartGameTimmer();

        isCardDistribute = false;

        txtBallence.setText(wallet);

        removeBet();
        aaraycards.clear();
        if(!isFromonCreate)
            isGameBegning = true;

        bet1 = 0;
        bet2 = 0;
        bet3 = 0;
        bet4 = 0;
        bet5 = 0;
        bet6 = 0;
        bet7 = 0;
        bet8 = 0;
        bet9 = 0;
        bet10 = 0;
        bet11 = 0;
        bet12 = 0;
        bet13 = 0;
        bet14 = 0;
        bet15 = 0;
        bet16 = 0;
        bet17 = 0;
        bet18 = 0;
        bet19 = 0;
        bet20 = 0;
        bet21 = 0;
        bet22 = 0;
        bet23 = 0;
        bet24 = 0;
        bet25 = 0;
        bet26 = 0;
        bet27 = 0;
        bet28 = 0;
        bet29 = 0;
        bet30 = 0;
        bet31 = 0;
        bet32 = 0;
        bet33 = 0;
        bet35 = 0;
        bet34 = 0;
        bet36 = 0;
        bet37 = 0;
        bet39 = 0;
        bet40 = 0;
        bet41 = 0;
        bet42 = 0;
        bet43 = 0;
        bet44 = 0;
        bet45 = 0;
        bet46 = 0;
        bet47 = 0;
        bet48 = 0;
        bet0 = 0;
        tagamountselected = "0";
        setSelectedType("");
        GameAmount = 0;
        UserProfile();
        totalBetAmount = 0;
        currentWalletAmount = 0.0f;
        //   enableDisableView(rec_rules,false);

    }

    private void removeBet(){
        canbet = true;
        isConfirm = false;
        bet_id = "";
        betplace="";
        betvalue = "";
    }

    private void RemoveChips(){
        BET_ON = "";
        addChipsonView();
    }

    @Override
    public void onBackPressed() {

        // super.onBackPressed();
        Functions.Dialog_CancelAppointment(context, "Confirmation", "Leave ?", new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if(resp.equals("yes"))
                {
                    stopPlaying();
                    finish();
                    mSocket.disconnect();
                }
            }
        });
    }

    boolean animationon = false;
    RelativeLayout rl_AnimationView;
    private void ChipsAnimations(View mfromView,View mtoView,boolean iswin){

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

        int stickerId = Functions.GetResourcePath("ic_dt_chips",context);

        int chips_size = (int) getResources().getDimension(R.dimen.chips_size);

        if(stickerId > 0)
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
                if(coins_count <= 0)
                {
                    RemoveChips();
                    rl_AnimationView.removeAllViews();
                    if(!iswin)
                        makeLosstoPlayer(SharePref.getU_id());
                    else
                        makeWinnertoPlayer(SharePref.getU_id());
                }

            }
        });
        sticker.setAnimation(a);
        a.startNow();


        Rect fromRect = new Rect();
        Rect toRect = new Rect();
        fromView.getGlobalVisibleRect(fromRect);
        toView.getGlobalVisibleRect(toRect);

        Log.e("MainActivity","FromView : "+fromRect);
        Log.e("MainActivity","toView : "+toRect);

        PlaySaund(R.raw.teenpattichipstotable);


    }

    private void startBetAnim(){
        resultTv.setText("");
        txtGameBets.setVisibility(View.VISIBLE);
        Glide.with(context) .asGif()
                .load(R.drawable.place_your_bet).into(txtGameBets);
        txtGameBets.bringToFront();
        ScaleAnimation fade_in =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(1200);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        //     txtGameBets.startAnimation(fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtGameBets.clearAnimation();
                txtGameBets.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                txtGameBets.setVisibility(View.GONE);
            }
        }, 1500);
    }
    private void stopBetAnim(){
        txtGameBets.setVisibility(View.VISIBLE);
        txtGameBets.setImageDrawable(null);

        make_spin();

//        Animation sgAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_grow);
//        txtGameBets.startAnimation(sgAnimation);
//        txtGameBets.startAnimation(sgAnimation);

        txtGameBets.bringToFront();
        ScaleAnimation fade_in =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(200);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        txtGameBets.startAnimation(fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtGameBets.clearAnimation();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardDistributeCount = 0;
                        counttimerforcards.start();
                    }
                }, 1500);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(context, "nice", Toast.LENGTH_SHORT).show();
                txtGameBets.setVisibility(View.GONE);
            }
        }, 1250);
    }
    private void CardAnimationAnimations(View mfromView,View mtoView,boolean isTiger){
//        Toast.makeText(context, "stops", Toast.LENGTH_SHORT).show();

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

        int stickerId = Functions.GetResourcePath("backside_card",context);

        int cards_size = (int) getResources().getDimension(R.dimen.jackpot_card_size);

        if(stickerId > 0)
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


//                addJackpotCard1(aaraycards.get(0),0);
//                addJackpotCard2(aaraycards.get(1),1);
//                addJakpotCard3(aaraycards.get(2),2);

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

    private RequestManager LoadImage()
    {
        return  Glide.with(context);
    }

    private boolean betValidation(){

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

//        if(Functions.isStringValid(betplace) && betValidation())
//            putbet(betplace);
    }


    public void cancelBooking(View view) {

        cancelbet();
        removeBet();
        RemoveChips();
    }

    public void openGameRules(View view) {
        DialogRouletteRules.getInstance(context).show();
    }

    public void openJackpotHistory(View view) {
        DialogRedBlackHistory.getInstance(context).show();
    }

    public void openJackpotLasrWinHistory(View view) {
        DialogRedBlacklastWinHistory.getInstance(context).setRoomid(game_id).show();
    }


    private void addCardonParentView(ViewGroup parentview,String image_card){
        View view  = LayoutInflater.from(context).inflate(R.layout.item_redblack_card,null);
        ImageView imageView = view.findViewById(R.id.imgcard);
        int imageid = Functions.GetResourcePath(image_card,context);
        imageView.setImageDrawable(Functions.getDrawable(context,imageid));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int cardMarginRight = (int) context.getResources().getDimension(R.dimen.dp35);

        layoutParams.setMargins(-cardMarginRight,
                0,
                0, 0);

        parentview.addView(view,layoutParams);
    }

    private void updateCardonViewGroup(ViewGroup parentview,int cardposition,String image_card){
        View parentView = parentview.getChildAt(cardposition);
        ImageView imageView = parentView.findViewById(R.id.imgcard);
        int imageid = Functions.GetResourcePath(image_card,context);
        imageView.setImageDrawable(Functions.getDrawable(context,imageid));
    }




    private View getBettingView(int position) {
        View rltContainer = null;
        RouleteRulesAdapter.holder holder = (RouleteRulesAdapter.holder)
                rec_rules.findViewHolderForAdapterPosition(position);
        if (null != holder) {
            rltContainer = findViewById(R.id.ivContainerbg);
        }
        return rltContainer;
    }

    private void DummyChipsAnimations(View mfromView, View mtoView, ViewGroup rl_AnimationView) {

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

        int boardWidth = myViewRect.width(), boardHeight = myViewRect.height();


        int centreX = (int) (boardWidth / 2) - (chips_width / 2);
        int centreY = (int) (boardHeight / 2) - (chips_width / 2);

        if (chips_width > 0) {
            destX = destX + new Random().nextInt(boardWidth - chips_width);
            destY = destY + new Random().nextInt(boardHeight - chips_width);
        } else
        {
            destX += centreX;
            destY += centreY;
        }



        Animations anim = new Animations();
        float finalDestX = destX;
        float finalDestY = destY;
        Animation a = anim.fromAtoB(startX, startY, destX, destY, null, ANIMATION_SPEED, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                animationon = false;


            }
        });
        image_chips.setAnimation(a);
        a.startNow();

        playSound(SOUND_2);


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

    private int ZERO_VALUE = 0;
    private int _1ST_12 = 37;
    private int _2ND_12 = 38;
    private int _3RD_12 = 39;
    private int _1TO18 = 40;
    private int _19TO36 = 41;
    private int _EVEN = 43;
    private int _ODD = 42;
    private int _RED = 44;
    private int _BLACK = 45;
    private int _1_WALA_ROW = 46;
    private int _2_WALA_ROW = 47;
    private int _3_WALA_ROW = 48;

    // @OnClick(R.id.betOnZero)
    public void betOnZero(View view) {
        onButtonClickAnimation(view);
        betplace =""+ZERO_VALUE;
        putBetonRules("0",rouleteRulesModel);
    }
    //  @OnClick(R.id.betOn1to12)
    public void betOn1to12(View view) {
        onButtonClickAnimation(view);
        betplace =""+_1ST_12;
        putBetonRules("1to12",rouleteRulesModel);
    }
    //  @OnClick(R.id.betOn2to12)
    public void betOn2to12(View view) {
        onButtonClickAnimation(view);
        betplace =""+_2ND_12;
        putBetonRules("2to12",rouleteRulesModel);
    }

    // @OnClick(R.id.betOn3to12)
    public void betOn3to12(View view) {
        onButtonClickAnimation(view);
        betplace =""+_3RD_12;
        putBetonRules("3to12",rouleteRulesModel);
    }

    //  @OnClick(R.id.betOn1to18)
    public void betOn1to18(View view) {
        onButtonClickAnimation(view);
        betplace =""+_1TO18;
        putBetonRules("1to18",rouleteRulesModel);
    }

    //  @OnClick(R.id.betOnEven)
    public void betOnEven(View view) {
        onButtonClickAnimation(view);
        betplace =""+_EVEN;
        putBetonRules("even",rouleteRulesModel);
    }

    //  @OnClick(R.id.betOnRed)
    public void betOnRed(View view) {
        onButtonClickAnimation(view);
        betplace =""+_RED;
        putBetonRules("red",rouleteRulesModel);
    }

    //  @OnClick(R.id.betOnBlack)
    public void betOnBlack(View view) {
        onButtonClickAnimation(view);
        betplace =""+_BLACK;
        putBetonRules("black",rouleteRulesModel);
    }

    // @OnClick(R.id.betOnOdd)
    public void betOnOdd(View view) {
        onButtonClickAnimation(view);
        betplace =""+_ODD;
        putBetonRules("odd",rouleteRulesModel);
    }

    // @OnClick(R.id.betOn19to36)
    public void betOn19to36(View view) {
        onButtonClickAnimation(view);
        betplace =""+_19TO36;
        putBetonRules("19to36",rouleteRulesModel);
    }

    //  @OnClick(R.id.betOnFirstRow)
    public void betOnFirstRow(View view) {
        onButtonClickAnimation(view);
        betplace =""+_2_WALA_ROW;
        putBetonRules("1strow",rouleteRulesModel);
    }

    // @OnClick(R.id.betOnSecondRow)
    public void betOnSecondRow(View view) {
        onButtonClickAnimation(view);
        betplace =""+_1_WALA_ROW;
        putBetonRules("2ndrow",rouleteRulesModel);
    }

    // @OnClick(R.id.betOnThirdRow)
    public void betOnThirdRow(View view) {
        onButtonClickAnimation(view);
        betplace =""+_3_WALA_ROW;
        putBetonRules("3rdrow",rouleteRulesModel);
    }

    public void addbetcoinlayout(String whichbet){
        if(whichbet.equals("0")){
            findViewById(R.id.tvZeroRule).setBackgroundResource(R.drawable.ic_dt_chips);
        }else  if(whichbet.equals("1to18")){
            findViewById(R.id.chipOn1to18).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("even")){
            findViewById(R.id.chipOnEven).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("odd")){
            findViewById(R.id.chipOnOdd).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("19to36")){
            findViewById(R.id.chipOn19to36).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("1to12")){
            findViewById(R.id.chipOn1to12).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("2to12")){
            findViewById(R.id.chipOn2to12).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("3to12")){
            findViewById(R.id.chipOn3to12).setVisibility(View.VISIBLE);
        }else  if(whichbet.equals("3to12")){
            findViewById(R.id.chipOn3to12).setVisibility(View.VISIBLE);
        }else if(whichbet.equals("red")){
            findViewById(R.id.redcard).setBackgroundResource(R.drawable.ic_dt_chips);
        }else if(whichbet.equals("black")){
            findViewById(R.id.blackcard).setBackgroundResource(R.drawable.ic_dt_chips);
        }else if(whichbet.equals("1strow")){
            findViewById(R.id.chipOnFirstRow).setVisibility(View.VISIBLE);
        }else if(whichbet.equals("2ndrow")){
            findViewById(R.id.chipOnSecondRow).setVisibility(View.VISIBLE);
        }else if(whichbet.equals("3rdrow")){
            findViewById(R.id.chipOnThirdRow).setVisibility(View.VISIBLE);
        }

    }

    public void removebetcoinlayout(){
        findViewById(R.id.tvZeroRule).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.chipOn1to18).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOnEven).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOnOdd).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOn19to36).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOn1to12).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOn2to12).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOn3to12).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOnFirstRow).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOnSecondRow).setVisibility(View.INVISIBLE);
        findViewById(R.id.chipOnThirdRow).setVisibility(View.INVISIBLE);
        findViewById(R.id.redcard).setBackgroundResource(android.R.color.transparent);
        findViewById(R.id.blackcard).setBackgroundResource(android.R.color.transparent);
    }

    public void UserProfile() {

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

                if(resp != null)
                {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        Log.d("profile_res", resp);
                        String code = jsonObject.getString("code");
                        if (code.equalsIgnoreCase("200")) {
                            JSONObject jsonObject0 = jsonObject.getJSONArray("user_data").getJSONObject(0);
                            wallet = jsonObject0.optString("wallet");
                            total_wallet = jsonObject0.optString("wallet");
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

    private void playySound() {
      //  soundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.roulette_audio);
       // soundPlayer.start();
    }
    private void stopSound() {
        if (soundPlayer != null) {
            soundPlayer.stop();
            soundPlayer.release();
            soundPlayer = null;
        }
    }

    private void AnimationUtils(boolean iswin) {
        coins_count = 10;
        if (!iswin)
            makeLosstoPlayer(SharePref.getU_id());
        else
            makeWinnertoPlayer(SharePref.getU_id());
    }

}