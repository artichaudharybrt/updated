package com.gamegards.gaming27.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.first_player_games.Api.LudoApiRepository;
import com.first_player_games.Home_Activity;
import com.first_player_games.LocalGame.LocalGame;
import com.first_player_games.LocalGame.VsComputer;
import com.first_player_games.Menu.ComputerGameMenu;
import com.first_player_games.Menu.LocalGameMenu;
import com.first_player_games.OnlineGame.Lobby.RoomCreationActivity;
import com.first_player_games.OnlineGame.Lobby.RoomJoinActivity;
import com.first_player_games.ludoApi.TableMaster;
import com.first_player_games.ludoApi.bottomFragment.LudoActiveTables_BF;
import com.gamegards.gaming27.Adapter.DummyPayoutAdapter;
import com.gamegards.gaming27.Adapter.HomegameListAdapter;
import com.gamegards.gaming27.BaseActivity;
import com.gamegards.gaming27.Comman.CommonAPI;
import com.gamegards.gaming27.Comman.DialogRestrictUser;
import com.gamegards.gaming27.Comman.DialogSettingOption;
import com.gamegards.gaming27.Details.GameDetails_A;
import com.gamegards.gaming27.Interface.ApiRequest;
import com.gamegards.gaming27.Interface.OnItemClickListener;
import com.gamegards.gaming27.LocationManager.GetLocationlatlong;
import com.gamegards.gaming27.LocationManager.GpsUtils;
import com.gamegards.gaming27.Menu.DialogDailyBonus;
import com.gamegards.gaming27.Menu.DialogNotification;
import com.gamegards.gaming27.Menu.DialogReferandEarn;
import com.gamegards.gaming27.Menu.DialogUserRanking;
import com.gamegards.gaming27.MyFlowLayout;
import com.gamegards.gaming27.RedeemCoins.WithdrawalList;
import com.gamegards.gaming27.Statement;
import com.gamegards.gaming27.Utils.Sound;
import com.gamegards.gaming27._AdharBahar.Andhar_Bahar_Socket;
import com.gamegards.gaming27._AnimalRoulate.AnimalRoulette_Socket;
import com.gamegards.gaming27._Aviator.Aviator_Game_Socket;
import com.gamegards.gaming27._Aviator.Aviator_Game_Socket_Vertical;
import com.gamegards.gaming27._CarRoullete.CarRoullete_Socket;
import com.gamegards.gaming27._CoinFlip.HeadTail_Socket;
import com.gamegards.gaming27._ColorPrediction.Bots_list;
import com.gamegards.gaming27._ColorPrediction.ColorPrediction1_Socket;
import com.gamegards.gaming27._ColorPrediction.ColorPrediction3_Socket;
import com.gamegards.gaming27._ColorPrediction.ColorPrediction_Socket;
import com.gamegards.gaming27._ColorPrediction.ColorPrediction30_Socket;
import com.gamegards.gaming27._DragonTiger.DragonTigerSocket;
import com.gamegards.gaming27.Fragments.ActiveTables_BF;
import com.gamegards.gaming27.Utils.SharePref;
import com.gamegards.gaming27.Utils.Variables;
import com.gamegards.gaming27.Helper.PaymentGatewayHelper;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.Interface.Callback;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.wallet.WalletConnectActivity;
import com.gamegards.gaming27.Utils.Animations;
import com.gamegards.gaming27.Utils.Functions;
import com.gamegards.gaming27._LuckJackpot.LuckJackPot_A_Socket;
import com.gamegards.gaming27._Poker.Fragment.PokerActiveTables_BF;
import com.gamegards.gaming27._RedBlack.RedBlackPot_Socket;
import com.gamegards.gaming27._Rummy.RummyPointSocket;
import com.gamegards.gaming27._RummyDeal.DialogDealType;
import com.gamegards.gaming27._RummyDeal.Fragments.RummyDealActiveTables_BF;
import com.gamegards.gaming27._RummyDeal.RummyDealGameSocket;
import com.gamegards.gaming27._RummyPull.Fragments.RummyActiveTables_BF;
import com.gamegards.gaming27._RummyPull.RummyPullGameSocket;
import com.gamegards.gaming27._SevenUpGames.SevenUp_Socket;
import com.gamegards.gaming27._TeenPatti.TeenPattiSocket;
import com.gamegards.gaming27._Tournament.TourList;
import com.gamegards.gaming27._baccarat.Baccarat_Socket;
import com.gamegards.gaming27._jhandhiMunda.JhandhiMunda_Socket;
import com.gamegards.gaming27._rouleteGame.RouleteDoubleGame_Socket;
import com.gamegards.gaming27._rouleteGame.RouleteGame_Socket;
import com.gamegards.gaming27.account.LoginScreen;
import com.gamegards.gaming27.model.BannerModel;
import com.gamegards.gaming27.model.HomescreenModel;
//import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.android.gms.maps.model.LatLng;
//import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import static com.gamegards.gaming27.GAMES.ANDHAR_BAHAR;
import static com.gamegards.gaming27.GAMES.ANIMAL_ROULETTE;
import static com.gamegards.gaming27.GAMES.AVIATOR_VERTICAL;
import static com.gamegards.gaming27.GAMES.BACCARAT;
import static com.gamegards.gaming27.GAMES.CAR_ROULETTE;
import static com.gamegards.gaming27.GAMES.COLOUR_PREDICTION;
import static com.gamegards.gaming27.GAMES.COLOUR_PREDICTION1;
import static com.gamegards.gaming27.GAMES.COLOUR_PREDICTION30;
import static com.gamegards.gaming27.GAMES.CUSTOM_TABLE;
import static com.gamegards.gaming27.GAMES.DEAL_RUMMY;
import static com.gamegards.gaming27.GAMES.DRAGON_TIGER;
import static com.gamegards.gaming27.GAMES.HEAD_TAIL;
import static com.gamegards.gaming27.GAMES.JACKPOT_3PATTI;
import static com.gamegards.gaming27.GAMES.JHANDHI_MUNDA;
import static com.gamegards.gaming27.GAMES.LUDO_GAME_COMPUTER;
import static com.gamegards.gaming27.GAMES.LUDO_GAME_ONLINE;
import static com.gamegards.gaming27.GAMES.LUDO_GAME_PLAY_LOCAL;
import static com.gamegards.gaming27.GAMES.POINT_RUMMY;
import static com.gamegards.gaming27.GAMES.POKER_GAME;
import static com.gamegards.gaming27.GAMES.POOL_RUMMY;
import static com.gamegards.gaming27.GAMES.PRIVATE_RUMMY;
import static com.gamegards.gaming27.GAMES.PRIVATE_TABLE;
import static com.gamegards.gaming27.GAMES.RED_BLACK;
import static com.gamegards.gaming27.GAMES.SEVEN_UP_DOWN;
import static com.gamegards.gaming27.GAMES.TEENPATTI;
import static com.gamegards.gaming27.GAMES.ROULETTE;
import static com.gamegards.gaming27.GAMES.TOURNAMENT;
import static com.gamegards.gaming27.GAMES.COLOUR_PREDICTION3;
import static com.gamegards.gaming27.LocationManager.GpsUtils.ENABLE_LOCATION_CODE;
import static com.gamegards.gaming27.Utils.Functions.convertDpToPixel;

import butterknife.ButterKnife;

public class Homepage extends BaseActivity {

    ComputerGameMenu computergamemenu;
    List<String> ban_states = new ArrayList<>();
    Animation animBounce, animBlink;
    public static final String MY_PREFS_NAME = "Login_data";
    ImageView imgLogout, imgshare, imaprofile;
    ImageView imgpublicGame, imgPrivategame, ImgCustomePage, ImgVariationGane, iv_andher, ivIcon;
    private String user_id, name, mobile, profile_pic, referral_code, wallet, game_played, bank_detail, adhar_card, upi,
            notification_image;
    private TextView txtName, txtwallet, txtproname;
    LinearLayout lnrbuychips, lnrinvite, lnrmail, lnrsetting, lnrvideo;
    Typeface helvatikaboldround, helvatikanormal;
    public String token = "";
    private String game_for_private, app_version;
    SeekBar sBar;
    SeekBar sBarpri;
    ImageView imgCreatetable, imgJointable, imgCreatetablecustom, imgclosetoppri, imgclosetop, homespin;
    int pval = 50;
    int pvalpri = 50;
    TextView txtStart, txtLimit, txtwalletchips,
            txtwalletchipspri, txtBootamount, txtPotLimit, txtNumberofBlind,
            txtMaximumbetvalue,tvUserversion;
    TextView txtStartpri, txtLimitpri, txtBootamountpri, txtPotLimitpri, txtNumberofBlindpri, txtMaximumbetvaluepri;
    RelativeLayout rltimageptofile;
    int version = 0;
    public static String profile_img = "";

    RelativeLayout rootView;
    RecyclerView rec_dummypayout;
    ArrayList<Bots_list> dummypayoutList=new ArrayList<>();
    DummyPayoutAdapter dummyPayoutAdapter;

    public static String str_colr_pred = "";
    String base_64 = "", str_filter_val = "";
    ProgressDialog progressDialog;
    Activity context = this;

   // AutoScrollViewPager headerViewPager;
    ArrayList<BannerModel> bannerModelArrayList = new ArrayList<>();
    //HorizontalInfiniteCycleViewPager viewPager;
    Timer timer;
    ViewPager viewpager;
    ImageView comingsoonimg;
    RecyclerView recGamesList;
    Random random = new Random();
    private View views;
    String[] PERMISSIONS = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public static final int MY_PEQUEST_CODE = 123;

    private void emitBubbles() {
        // It will create a thread and attach it to
        // the main thread
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                int size = random.nextInt(250);
//                bubbleEmitter.emitBubble(size);
//                bubbleEmitter.setColors(android.R.color.transparent,
//                        android.R.color.holo_blue_light,
//                        android.R.color.holo_blue_bright);
//                emitBubbles();
            }
        }, random.nextInt(500));
    }


//    BubbleEmitterView bubbleEmitter;

  //  @BindView(R.id.tvUserCategory)
    TextView tvUserCategory;

    //new
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);


        LudoApiRepository.getInstance().init(this);

        soundMedia = new Sound();
   //     initGamesTabs();

        SharePref.getInstance().init(context);

        // WalletConnect is now initialized in WalletApplication class

        // 🎯 Get Admin Settings FIRST - UI depends on this
        getAdminSettings();

        findViewById(R.id.rltDragonTiger).setVisibility(SharePref.getIsDragonTigerVisible()
                ? View.VISIBLE : View.GONE);

        findViewById(R.id.rltTeenpatti).setVisibility(SharePref.getIsTeenpattiVisible()
                ? View.VISIBLE : View.GONE);

        findViewById(R.id.rltPrivate).setVisibility(SharePref.getIsPrivateVisible()
                ? View.VISIBLE : View.GONE);

        findViewById(R.id.rltCustom).setVisibility(SharePref.getIsCustomVisible()
                ? View.VISIBLE : View.GONE);

        findViewById(R.id.rltAndharbhar).setVisibility(SharePref.getIsAndharBaharVisible()
                ? View.VISIBLE : View.GONE);

        findViewById(R.id.rltRummy).setVisibility(SharePref.getIsRummyVisible()
                ? View.VISIBLE : View.GONE);


        // Check if wallet connect button exists and add debugging
        View walletConnectLayout = findViewById(R.id.lnrWalletConnect);
        Log.e("WalletConnect", "=== DEBUGGING WALLET CONNECT BUTTON ===");

        if (walletConnectLayout != null) {
            Log.e("WalletConnect", "✅ Button FOUND!");
            Log.e("WalletConnect", "Visibility: " + walletConnectLayout.getVisibility());
            Log.e("WalletConnect", "Enabled: " + walletConnectLayout.isEnabled());
            Log.e("WalletConnect", "Clickable: " + walletConnectLayout.isClickable());

            // Set clickable properties
            walletConnectLayout.setClickable(true);
            walletConnectLayout.setFocusable(true);

            walletConnectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("WalletConnect","🔥🔥🔥 BUTTON CLICKED! - navigating to WalletConnect activity");
                    try {
                        startActivity(new Intent(context, WalletConnectActivity.class));
                        Log.e("WalletConnect","✅ Navigation completed successfully");
                    } catch (Exception e) {
                        Log.e("WalletConnect","❌ Navigation failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            // Add touch event debugging
            walletConnectLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("WalletConnect", "🔥 TOUCH EVENT DETECTED! Action: " + event.getAction());
                    return false; // Let the click event continue
                }
            });

            // Bring to front to avoid overlapping issues
            walletConnectLayout.bringToFront();

            // Also try setting click listener on the inner button
            View innerButton = findViewById(R.id.btnWalletConnect);
            if (innerButton != null) {
                Log.e("WalletConnect", "✅ Inner button found, setting click listener");
                innerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("WalletConnect","🔥🔥🔥 INNER BUTTON CLICKED!");
                        try {
                            startActivity(new Intent(context, WalletConnectActivity.class));
                            Log.e("WalletConnect","✅ Navigation from inner button completed");
                        } catch (Exception e) {
                            Log.e("WalletConnect","❌ Navigation from inner button failed: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }

            // Also check after layout is complete
            walletConnectLayout.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("WalletConnect", "=== POST-LAYOUT CHECK ===");
                    Log.e("WalletConnect", "Width: " + walletConnectLayout.getWidth());
                    Log.e("WalletConnect", "Height: " + walletConnectLayout.getHeight());
                    Log.e("WalletConnect", "X: " + walletConnectLayout.getX());
                    Log.e("WalletConnect", "Y: " + walletConnectLayout.getY());
                    Log.e("WalletConnect", "Final Visibility: " + walletConnectLayout.getVisibility());

                    // Bring to front again after layout
                    walletConnectLayout.bringToFront();
                }
            });

        } else {
            Log.e("WalletConnect", "❌ Button NOT FOUND!");
        }

        findViewById(R.id.rltJackpot).setVisibility(Variables.JACKPOTGAME_SHOW ? View.VISIBLE : View.GONE);
        findViewById(R.id.rltRummyDeal).setVisibility(Variables.RUMMYDEAL_SHOW ? View.VISIBLE : View.GONE);
        findViewById(R.id.rltRummyPool).setVisibility(Variables.RUMMPOOL_SHOW ? View.VISIBLE : View.GONE);
        findViewById(R.id.rltSeveUp).setVisibility(Variables.SEVENUPSDOWN_SHOW ? View.VISIBLE : View.GONE);
        findViewById(R.id.rltCarRoullete).setVisibility(Variables.CARROULETE_SHOW ? View.VISIBLE : View.GONE);
        findViewById(R.id.rltAnimalRoullete).setVisibility(Variables.ANIMALROULETE_SHOW ? View.VISIBLE : View.GONE);

       // viewPager = findViewById(R.id.view_pager);
       // headerViewPager = (AutoScrollViewPager) findViewById(R.id.pager);
        comingsoonimg = findViewById(R.id.comingsoonimg);
        recGamesList = findViewById(R.id.recGamesList);

        viewpager = findViewById(R.id.viewpager);
        tvUserCategory = findViewById(R.id.tvUserCategory);

        imgLogout = findViewById(R.id.imgLogout);
        initHomeScreenModel();
        ivIcon = findViewById(R.id.ivIcon);
        Glide.with(context).asGif()
                .load(R.drawable.home_lady).into(ivIcon);
        lnrbuychips = findViewById(R.id.lnrbuychips);
        lnrmail = findViewById(R.id.lnrmail);
        lnrinvite = findViewById(R.id.lnrinvite);
        lnrsetting = findViewById(R.id.lnrsetting);
        imaprofile = findViewById(R.id.imaprofile);
        tvUserversion = findViewById(R.id.tvUserversion);

    //    emitBubbles();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        FrameLayout home_container = findViewById(R.id.home_container);
        home_container.setVisibility(View.VISIBLE);


        rootView = findViewById(R.id.rlt_animation_layout);
        RelativeLayout relativeLayout = findViewById(R.id.rlt_parent);
//        SetBackgroundImageAsDisplaySize(context,relativeLayout,R.drawable.splash);


//        BonusView();


        // Set fullscreen
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        helvatikaboldround = Typeface.createFromAsset(getAssets(),
                "fonts/helvetica-rounded-bold-5871d05ead8de.otf");

        helvatikanormal = Typeface.createFromAsset(getAssets(),
                "fonts/Helvetica.ttf");

        rltimageptofile = findViewById(R.id.rltimageptofile);

        // Update profile functionality moved to Settings
        // rltimageptofile.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         UserInformation_BT userInformation_bt = new UserInformation_BT(new Callback() {
        //             @Override
        //             public void Responce(String resp, String type, Bundle bundle) {
        //                 UserProfile();
        //             }
        //         });
        //         userInformation_bt.setCancelable(false);
        //         userInformation_bt.show(getSupportFragmentManager(), userInformation_bt.getTag());
        //     }
        // });


        homespin = (ImageView) findViewById(R.id.homespin);
        Glide.with(context).asGif()
                .load(R.drawable.home_spin_win).into(homespin);
        imgpublicGame = (ImageView) findViewById(R.id.imgpublicGame);
        Glide.with(context).asGif()
                .load(R.drawable.home_public_img).into(imgpublicGame);

        imgPrivategame = (ImageView) findViewById(R.id.imgPrivategame);
        ImgCustomePage = (ImageView) findViewById(R.id.ImgCustomePage);

        ImgVariationGane = (ImageView) findViewById(R.id.ImgVariationGane);
        Glide.with(context).asGif()
                .load(R.drawable.home_rummy_point).into(ImgVariationGane);

        iv_andher = (ImageView) findViewById(R.id.iv_andher);
        Glide.with(context).asGif()
                .load(R.drawable.home_andherbahar).into(iv_andher);

        txtName = findViewById(R.id.txtName);
        txtName.setTypeface(helvatikaboldround);
        txtwallet = findViewById(R.id.txtwallet);
        txtwallet.setTypeface(helvatikanormal);
        txtproname = findViewById(R.id.txtproname);
        txtproname.setTypeface(helvatikaboldround);
        TextView txtMail = findViewById(R.id.txtMail);

        // load the animation
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        imgpublicGame.startAnimation(animBlink);
        imgpublicGame.startAnimation(animBounce);


        imgPrivategame.startAnimation(animBlink);
        imgPrivategame.startAnimation(animBounce);

        rec_dummypayout = findViewById(R.id.rec_dummypayout);
        String jsonString = "[\n" +
                "    {\n" +
                "        \"id\": \"1\",\n" +
                "        \"name\": \"pravin patil\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9729\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:45:42\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"2\",\n" +
                "        \"name\": \"rajesh suryavanshi\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5829\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:25\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"pritam patil\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1029\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:46\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"4\",\n" +
                "        \"name\": \"yuvraj mahadik\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5298\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:12\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"5\",\n" +
                "        \"name\": \"sangram mane\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7429\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:30\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"6\",\n" +
                "        \"name\": \"kishor jadhav\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1928\",\n" +
                "        \"avatar\": \"m_4.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"7\",\n" +
                "        \"name\": \"pramod shinde\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1750\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:05\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"8\",\n" +
                "        \"name\": \"abhay shetti\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"2218\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:15\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"9\",\n" +
                "        \"name\": \"akshay chavan\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3045\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"10\",\n" +
                "        \"name\": \"ajit bele\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5329\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"11\",\n" +
                "        \"name\": \"sanjay lohar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7582\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:07\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"12\",\n" +
                "        \"name\": \"sandip kumbhar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4329\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:29\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"13\",\n" +
                "        \"name\": \"paresh patel\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9127\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"14\",\n" +
                "        \"name\": \"hiren shah\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1820\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"15\",\n" +
                "        \"name\": \"momin mulla\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"583642\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:25\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"16\",\n" +
                "        \"name\": \"ajit sandi\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"32971\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:46\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"17\",\n" +
                "        \"name\": \"suraj kavtekar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1248\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:44\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"18\",\n" +
                "        \"name\": \"samadhan adsul\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1452\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:15\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"19\",\n" +
                "        \"name\": \"rohit katti\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1958\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:30\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"20\",\n" +
                "        \"name\": \"virupaksh pujari\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3229\",\n" +
                "        \"avatar\": \"m_4.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:35\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"21\",\n" +
                "        \"name\": \"kedar dalvi\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"2507\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:42\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"22\",\n" +
                "        \"name\": \"manoj surve\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3928\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:47\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"23\",\n" +
                "        \"name\": \"santosh patil\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4217\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:53\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"24\",\n" +
                "        \"name\": \"supriya patankar\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"2897\",\n" +
                "        \"avatar\": \"f_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:00\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"25\",\n" +
                "        \"name\": \"rohini majumdar\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"1735\",\n" +
                "        \"avatar\": \"f_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:10\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26\",\n" +
                "        \"name\": \"sarika parmne\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"2178\",\n" +
                "        \"avatar\": \"f_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:20\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"27\",\n" +
                "        \"name\": \"vaishali karekar\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"1895\",\n" +
                "        \"avatar\": \"f_4.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:28\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"28\",\n" +
                "        \"name\": \"devji jhameriya\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1186\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:35\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"29\",\n" +
                "        \"name\": \"john kamble\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4930\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:42\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"30\",\n" +
                "        \"name\": \"sandip pawar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7528\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:50:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"31\",\n" +
                "        \"name\": \"karan sarvade\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"6239\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:00\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"32\",\n" +
                "        \"name\": \"sujit mhatre\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3498\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:10\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"33\",\n" +
                "        \"name\": \"om gujar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5387\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:21\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"34\",\n" +
                "        \"name\": \"laxman tippanavr\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5212\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:29\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"35\",\n" +
                "        \"name\": \"ravindra hukkeri\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7243\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:39\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"36\",\n" +
                "        \"name\": \"sukumar shiratti\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4132\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:50\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"37\",\n" +
                "        \"name\": \"mallappa hiremat\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9814\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:51:58\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"38\",\n" +
                "        \"name\": \"sidram jarati\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1905\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"39\",\n" +
                "        \"name\": \"bhartesh badnikai\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1482\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:10\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"40\",\n" +
                "        \"name\": \"sanjay aski\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3472\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:15\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"41\",\n" +
                "        \"name\": \"rahul karandikar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9748\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:22\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"42\",\n" +
                "        \"name\": \"madan apte\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"8352\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:27\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"43\",\n" +
                "        \"name\": \"mallikarjun\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5481\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:33\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"44\",\n" +
                "        \"name\": \"aniket mhatre\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3342\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:38\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"45\",\n" +
                "        \"name\": \"rajan koli\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"2385\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:44\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"46\",\n" +
                "        \"name\": \"prajval mule\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1025\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:49\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"47\",\n" +
                "        \"name\": \"rekha nimkar\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"5200\",\n" +
                "        \"avatar\": \"f_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:52:56\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"48\",\n" +
                "        \"name\": \"shivraj mandlik\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7419\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:53:00\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"49\",\n" +
                "        \"name\": \"prachi desai\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"3459\",\n" +
                "        \"avatar\": \"f_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:53:10\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"50\",\n" +
                "        \"name\": \"rijvan mujavar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5640\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:53:22\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    }\n" +
                "]";




        String jsonString1 = "[\n" +
                "    {\n" +
                "        \"id\": \"764\",\n" +
                "        \"name\": \"ajay kumar\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9729\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:45:42\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +

                "    {\n" +
                "        \"id\": \"2\",\n" +
                "        \"name\": \"Ananya Sharma\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"5829\",\n" +
                "        \"avatar\": \"f_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:25\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"3\",\n" +
                "        \"name\": \"Ishaan Patel\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1029\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:46\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"4\",\n" +
                "        \"name\": \"Rhea Mehta\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"5298\",\n" +
                "        \"avatar\": \"f_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:12\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"5\",\n" +
                "        \"name\": \"Aryan Kulkarni\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7429\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:30\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"6\",\n" +
                "        \"name\": \"Tara Joshi\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"1928\",\n" +
                "        \"avatar\": \"f_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:47:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"7\",\n" +
                "        \"name\": \"Aditi Nair\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"1750\",\n" +
                "        \"avatar\": \"f_4.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:05\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"8\",\n" +
                "        \"name\": \"Rohan Bhat\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"2218\",\n" +
                "        \"avatar\": \"m_4.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:15\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"9\",\n" +
                "        \"name\": \"Sneha Kapoor\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"3045\",\n" +
                "        \"avatar\": \"f_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"10\",\n" +
                "        \"name\": \"Krishna Agarwal\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5329\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"11\",\n" +
                "        \"name\": \"Sanya Reddy\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"7582\",\n" +
                "        \"avatar\": \"f_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:07\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"12\",\n" +
                "        \"name\": \"Kunal Shah\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4329\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:29\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"13\",\n" +
                "        \"name\": \"Pooja Jain\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"9127\",\n" +
                "        \"avatar\": \"f_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"14\",\n" +
                "        \"name\": \"Aditya Bhagat\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1820\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:49:55\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +

                "    {\n" +
                "        \"id\": \"6815\",\n" +
                "        \"name\": \"vasim\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"583642\",\n" +
                "        \"avatar\": \"m_1.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:25\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"9796\",\n" +
                "        \"name\": \"mohan lal\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"32971\",\n" +
                "        \"avatar\": \"m_2.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:46\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"9514\",\n" +
                "        \"name\": \"chandan\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1248\",\n" +
                "        \"avatar\": \"m_7.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:46:44\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"shashikant\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"5758\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"Raju\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"8058\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"Simran\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9738\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"Suraj\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"61123\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"Renu\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"4410\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"26732\",\n" +
                "        \"name\": \"Ranbir\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"3509\",\n" +
                "        \"avatar\": \"m_5.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:40\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"28694\",\n" +
                "        \"name\": \"chhabil\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"7429\",\n" +
                "        \"avatar\": \"m_3.png\",\n" +
                "        \"added_date\": \"2023-02-13 12:48:51\",\n" +
                "        \"updated_date\": \"2023-02-13 12:52:03\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"345\",\n" +
                "        \"name\": \"John Doe\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"1590\",\n" +
                "        \"avatar\": \"m_6.png\",\n" +
                "        \"added_date\": \"2023-04-12 13:24:31\",\n" +
                "        \"updated_date\": \"2023-04-12 13:24:31\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"356\",\n" +
                "        \"name\": \"Alice Smith\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"2000\",\n" +
                "        \"avatar\": \"f_1.png\",\n" +
                "        \"added_date\": \"2023-04-13 14:15:42\",\n" +
                "        \"updated_date\": \"2023-04-13 14:15:42\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"457\",\n" +
                "        \"name\": \"Emma Johnson\",\n" +
                "        \"gender\": \"f\",\n" +
                "        \"coin\": \"1350\",\n" +
                "        \"avatar\": \"f_2.png\",\n" +
                "        \"added_date\": \"2023-04-14 16:25:54\",\n" +
                "        \"updated_date\": \"2023-04-14 16:25:54\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": \"1112\",\n" +
                "        \"name\": \"Robert Lang\",\n" +
                "        \"gender\": \"m\",\n" +
                "        \"coin\": \"9852\",\n" +
                "        \"avatar\": \"m_8.png\",\n" +
                "        \"added_date\": \"2023-05-01 10:15:23\",\n" +
                "        \"updated_date\": \"2023-05-01 10:15:23\",\n" +
                "        \"isDeleted\": \"0\"\n" +
                "    }\n" +
                "]";

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addDummyUsers(jsonArray);

        ImgCustomePage.startAnimation(animBlink);
        ImgCustomePage.startAnimation(animBounce);
        ImgCustomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCustomeTeenpatti(view);
            }
        });


        ImgVariationGane.startAnimation(animBlink);
        ImgVariationGane.startAnimation(animBounce);
        clickTask();

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        token = task.getResult();
//
//                        // Log and toast
//                        // String msg = getString(R.string.msg_token_fmt, token);
//                        // Log.d(TAG, msg);
//                        // Funtions.showToast(context, token);
//                        UserProfile();
//
//                    }
//                });


        //  rotation_animation(((ImageView) findViewById(R.id.imgsetting)), R.anim.rotationback_animation);

        findViewById(R.id.lnr_redeem).setOnClickListener(v -> startActivity(new Intent(context, WithdrawalList.class)));
        findViewById(R.id.lnr_statement).setOnClickListener(v -> startActivity(new Intent(context, Statement.class)));
        findViewById(R.id.lnr_spinner).setOnClickListener(v -> startActivity(new Intent(context, Spinner_Wheels.class)));
        findViewById(R.id.lnr_spin_win).setOnClickListener(v -> startActivity(new Intent(context, Spinner_Wheels_Reward.class)));
        findViewById(R.id.lnrhistory).setOnClickListener(view -> startActivity(new Intent(context, GameDetails_A.class)));
        findViewById(R.id.lnr_mail).setOnClickListener(view -> showDailyWinCoins());
        findViewById(R.id.lnr_first_reacharge).setOnClickListener(view -> showDailyWinCoins());





        findViewById(R.id.iv_add).setOnClickListener(v -> {
            Log.d("AddCash", "Add cash button clicked - fetching user data first");

            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading user data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Call user data API first before navigating to payment screen
            CommonAPI.CALL_API_UserDetails(context, new Callback() {
                @Override
                public void Responce(String resp, String type, Bundle bundle) {
                    progressDialog.dismiss();

                    if (resp != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            String code = jsonObject.getString("code");

                            if (code.equalsIgnoreCase("200")) {
                                // Check if user_data array exists and has elements
                                if (!jsonObject.has("user_data")) {
                                    showPayAddressNotFoundDialog();
                                    return;
                                }
                                JSONArray userDataArray = jsonObject.getJSONArray("user_data");
                                if (userDataArray.length() == 0) {
                                    showPayAddressNotFoundDialog();
                                    return;
                                }

                                // Parse the full response to update user data
                                ParseResponse(resp);
                                // 🎯 Use PaymentGatewayHelper to show appropriate payment options
                                Log.d("AddCash", "Using PaymentGatewayHelper from lnrbuychips click");
                                PaymentGatewayHelper.showPaymentOptions(context);
                            } else {
                                Functions.showToast(context, "Failed to load user data. Please try again.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Functions.showToast(context, "Failed to parse user data. Please try again.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.showToast(context, "An error occurred. Please try again.");
                        }
                    } else {
                        Functions.showToast(context, "Failed to load user data. Please try again.");
                    }
                }
            }, token);
        });



        findViewById(R.id.lnr_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        TextView btn_all, btn_multiplayer, btn_skills, btn_sports, btn_slots;
        LinearLayout lnr_all, lnr_multiplayer, lnr_skills, lnr_sports, lnr_slots;

        lnr_all = findViewById(R.id.lnr_all);
        lnr_multiplayer = findViewById(R.id.lnr_multiplayer);
        lnr_skills = findViewById(R.id.lnr_skills);
        lnr_sports = findViewById(R.id.lnr_sports);
        lnr_slots = findViewById(R.id.lnr_slots);

        btn_all = findViewById(R.id.btn_all);
        btn_multiplayer = findViewById(R.id.btn_multiplayer);
        btn_skills = findViewById(R.id.btn_skills);
        btn_sports = findViewById(R.id.btn_sports);
        btn_slots = findViewById(R.id.btn_slots);

        lnr_all.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                str_filter_val = "";
                recGamesList.setVisibility(View.VISIBLE);
                comingsoonimg.setVisibility(View.GONE);
                filter(str_filter_val.toString());
                btn_all.setTextColor(getColor(R.color.red));
                btn_multiplayer.setTextColor(getColor(R.color.black));
                btn_skills.setTextColor(getColor(R.color.black));
                btn_slots.setTextColor(getColor(R.color.black));
            }
        });

        lnr_multiplayer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                str_filter_val = "multi";
                recGamesList.setVisibility(View.GONE);
                comingsoonimg.setVisibility(View.VISIBLE);
                btn_all.setTextColor(getColor(R.color.black));
                btn_multiplayer.setTextColor(getColor(R.color.red));
                btn_skills.setTextColor(getColor(R.color.black));
                btn_slots.setTextColor(getColor(R.color.black));
            }
        });

        lnr_skills.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                str_filter_val = "skills";
                filter(str_filter_val.toString());
                btn_all.setTextColor(getColor(R.color.black));
                btn_multiplayer.setTextColor(getColor(R.color.black));
                btn_skills.setTextColor(getColor(R.color.red));
                btn_slots.setTextColor(getColor(R.color.black));
            }
        });

        lnr_slots.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                str_filter_val = "slots";
                filter(str_filter_val.toString());
                btn_all.setTextColor(getColor(R.color.black));
                btn_multiplayer.setTextColor(getColor(R.color.black));
                btn_skills.setTextColor(getColor(R.color.black));
                btn_slots.setTextColor(getColor(R.color.red));
            }
        });

        findViewById(R.id.btn_sports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogNotification.getInstance(context,notification_image).show();
            }
        }, 1000);

    }



    public void addDummyUsers(JSONArray bot_user){
        dummypayoutList.clear();
        for (int i = 0; i < bot_user.length() ; i++) {
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = bot_user.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert jsonObject1 != null;
                dummypayoutList.add(new Bots_list(
                        jsonObject1.getString("id"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("coin"),
                        jsonObject1.getString("avatar")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dummyPayoutAdapter = new DummyPayoutAdapter(this, dummypayoutList,rec_dummypayout);
        rec_dummypayout.setAdapter(dummyPayoutAdapter);
    }

    String[] gamelist = {
            "All",
            "Skills",
            "Sports",
    };

    MyFlowLayout lnrGamesTabs;
    int tabsCount = 0;

    private void initGamesTabs() {
        tabsCount = 0;
        lnrGamesTabs = findViewById(R.id.lnrGamesTabs);
        for (String tabs : gamelist) {
            CreateTabsLayout(tabsCount, tabs);
            tabsCount++;
        }
    }

    private void CreateTabsLayout(final int position, String name) {
        final View view = Functions.CreateDynamicViews(R.layout.item_gamehistory_tabs, lnrGamesTabs, context);
        String strtitle = name;
        view.setTag("" + strtitle);

        TextView title = view.findViewById(R.id.tvGameRecord);

        title.setText(strtitle);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPagerPostion(strtitle);
            }
        });

        if (position == 0)
            setPagerPostion(strtitle);
    }

    String selectedTab = "";

    private void setPagerPostion(String name) {
        for (int i = 0; i < lnrGamesTabs.getChildCount(); i++) {

            View view = lnrGamesTabs.getChildAt(i);
            TextView title = view.findViewById(R.id.tvGameRecord);

            if (Functions.getStringFromTextView(title).equalsIgnoreCase(name)) {
                if (homegameListAdapter != null)
                    homegameListAdapter.getFilter().filter(name);
                title.setTextColor(context.getResources().getColor(R.color.black));
                title.setBackground(context.getResources().getDrawable(R.drawable.btn_yellow_discard));
            } else {
                title.setTextColor(context.getResources().getColor(R.color.white));
                title.setBackground(context.getResources().getDrawable(R.drawable.d_white_corner));
            }

        }
    }

    HomegameListAdapter homegameListAdapter;

    private void initHomeScreenModel() {
       // recGamesList = findViewById(R.id.recGamesList);
        EditText edit_searchview = findViewById(R.id.edit_searchview);

        homegameListAdapter = new HomegameListAdapter(context);
        homegameListAdapter.setArrayList(getGameList());
        recGamesList.setLayoutManager(new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false));
        homegameListAdapter.setCallback(new OnItemClickListener() {
            @Override
                public void Response(View v, int position, Object object) {
                Enum gametype = (Enum) object;
                if (TEENPATTI.equals(gametype)) {
                    openPublicTeenpatti(null);
                } else if (DRAGON_TIGER.equals(gametype)) {
                    openDragonTiger(null);
                } else if (ANDHAR_BAHAR.equals(gametype)) {
                    openAndharbahar(null);
                } else if (POINT_RUMMY.equals(gametype)) {
                    openRummyGame(null);
                } else if (PRIVATE_RUMMY.equals(gametype)) {
                    openPrivateRummyTable();
//                    DialogRummyCreateTable.getInstance(context).show();
                } else if (POOL_RUMMY.equals(gametype)) {
                    openRummyPullGame(null);
                } else if (DEAL_RUMMY.equals(gametype)) {
                    openRummyDealGame(null);
                } else if (PRIVATE_TABLE.equals(gametype)) {
                    openPrivateTeenpatti(null);
                } else if (CUSTOM_TABLE.equals(gametype)) {
                    openCustomeTeenpatti(null);
                } else if (SEVEN_UP_DOWN.equals(gametype)) {
                    openSevenup(null);
                } else if (CAR_ROULETTE.equals(gametype)) {
                    openCarRoulette(null);
                } else if (JACKPOT_3PATTI.equals(gametype)) {
                    openLuckJackpotActivity(null);
                } else if (ANIMAL_ROULETTE.equals(gametype)) {
                    openAnimalRoullete(null);
                } else if (COLOUR_PREDICTION.equals(gametype)) {
                    openColorPred(null);
                } else if (POKER_GAME.equals(gametype)) {
                    openPokerGame(null);
                } else if (HEAD_TAIL.equals(gametype)) {
                    openHeadTailGame(null);
                } else if (RED_BLACK.equals(gametype)) {
                    openRedBlackGame(null);
                } else if (LUDO_GAME_PLAY_LOCAL.equals(gametype)) {
                    openLudoGames_LOCAL(null);
                } else if (LUDO_GAME_COMPUTER.equals(gametype)) {
                    openLudoGames_COMPUTER(null);
                } else if (LUDO_GAME_ONLINE.equals(gametype)) {
                    openLudoGames_ONLINE(null);
                } else if (BACCARAT.equals(gametype)) {
                    openBaccarat(null);
                } else if (JHANDHI_MUNDA.equals(gametype)) {
                    openJhandhiMunda(null);
                } else if (ROULETTE.equals(gametype)) {
                    openRoulette(null);
                }/*else if (ROULETTE_DOUBLE.equals(gametype)) {
                    openRoulette1(null);
                }*/
                else if (TOURNAMENT.equals(gametype)) {
                    openTournament();
                } else if (COLOUR_PREDICTION1.equals(gametype)) {
                    openColorPred1(null);
                } else if (COLOUR_PREDICTION3.equals(gametype)) {
                    openColorPred3(null);
                } else if (COLOUR_PREDICTION30.equals(gametype)) {
                    openColorPred30(null);
                } /*else if (AVIATOR.equals(gametype)) {
                    openAviator();
                }*/ else if (AVIATOR_VERTICAL.equals(gametype)) {
                    openAviatorVartical();
                } else {
                    Functions.showToast(context, "Coming soon!");
                }
            }


          /*  public void Response(View v, int position, Object object) {
                Enum gametype = (Enum) object;
                if (JACKPOT_3PATTI.equals(gametype)) {
                    openLuckJackpotActivity(null);
                } else if (COLOUR_PREDICTION1.equals(gametype)) {
                    openColorPred1(null);
                } else if (COLOUR_PREDICTION_VERTICAL.equals(gametype)) {
                    openColorPredVertical(null);
                } else if (DRAGON_TIGER.equals(gametype)) {
                    openDragonTiger(null);
                } else if (ROULETTE.equals(gametype)) {
                    openRoulette(null);
                } else if (SEVEN_UP_DOWN.equals(gametype)) {
                    openSevenup(null);
                }
                else if (AVIATOR_VERTICAL.equals(gametype)) {
                    openAviatorVartical();
                } else {
                    Functions.showToast(context, "Coming soon!");
                }
            }*/
        });
        recGamesList.setAdapter(homegameListAdapter);

        edit_searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

    }

    public void filter(String text) {
        List<HomescreenModel> temp = new ArrayList();
        for (HomescreenModel d : getGameList()) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getItemType().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        //update recyclerview
        homegameListAdapter.updateList(temp);
    }

    private void openJhandhiMunda(Object o) {
        //    startActivity(new Intent(getApplicationContext(), JhandhiMunda_A.class));
        startActivity(new Intent(getApplicationContext(), JhandhiMunda_Socket.class));
    }

    private void openBaccarat(Object o) {
        //    startActivity(new Intent(getApplicationContext(), Baccarat_A.class));
        startActivity(new Intent(getApplicationContext(), Baccarat_Socket.class));
    }

    private void openLudoGames(View view) {
        startActivity(new Intent(getApplicationContext(), Home_Activity.class));
    }

    private void openLudoGames_LOCAL(View view) {
        LocalGameMenu localgamemenu = new LocalGameMenu(this) {
            public void onGameStartAction(boolean[] players) {
                Intent intent = new Intent(Homepage.this, LocalGame.class);
                intent.putExtra("players", players);
                startActivity(intent);
            }
        };
        localgamemenu.showMenu();

    }

    private void openLudoGames_ONLINE(View view) {
        StartOnlineLudo();
    }

    private void StartOnlineLudo() {
        LudoActiveTables_BF ludoActiveTables_bf = new LudoActiveTables_BF();
        ludoActiveTables_bf.setCallback(new com.first_player_games.ClassCallback() {
            @Override
            public void Response(View v, int position, Object object) {
                if (object instanceof TableMaster.TableDatum) {
                    TableMaster.TableDatum tableDatum = (TableMaster.TableDatum) object;

                    if (!com.first_player_games.Others.Functions.checkStringisValid(tableDatum.getInvite_code()) && !tableDatum.getOnlineMembers().equals("1")) {
                        // Create Free Room
                        startActivity(new Intent(Homepage.this, RoomCreationActivity.class)
                                .putExtra("diamonds", 0).putExtra("boot_value", tableDatum.getBootValue()));
                    } else {
                        String roomid = tableDatum.getInvite_code();
                        Log.e("roomid", "Response: " + roomid);
                        Intent intent = new Intent(Homepage.this, RoomJoinActivity.class);
                        intent.putExtra("roomid", roomid);
                        startActivity(intent);
                    }

                }

            }
        });
        ludoActiveTables_bf.show(getSupportFragmentManager(), "lus");

    }

    private void openLudoGames_COMPUTER(View view) {
        computergamemenu = new ComputerGameMenu(Homepage.this) {
            public void onGameStartAction(int[] players) {
                Intent intent = new Intent(Homepage.this, VsComputer.class);
                intent.putExtra("players", players);
                startActivity(intent);
            }
        };
        computergamemenu.showMenu();
    }

    private void openRedBlackGame(View view) {
        //  startActivity(new Intent(getApplicationContext(), RedBlackPot_A.class));
        startActivity(new Intent(getApplicationContext(), RedBlackPot_Socket.class));
    }

    private void openHeadTailGame(View view) {
        //  startActivity(new Intent(getApplicationContext(), HeadTail_A.class));
        startActivity(new Intent(getApplicationContext(), HeadTail_Socket.class));
    }

    private void openPrivateRummyTable() {
        LoadTableFragments(ActiveTables_BF.RUMMY_PRIVATE_TABLE);
    }

  /*  private List<HomescreenModel> getGameList() {
        List<HomescreenModel> arraylist = new ArrayList<>();

     //     arraylist.add(new HomescreenModel(27, AVIATOR, R.drawable.aviator_icon, "slots"));
        if (SharePref.getIsHomeJackpotVisible())
            arraylist.add(new HomescreenModel(1, JACKPOT_3PATTI, R.drawable.home_jackpot, "All"));

        if (SharePref.getIsColorPrediction1Visible())
            arraylist.add(new HomescreenModel(2, COLOUR_PREDICTION1, R.drawable.color_pred_1min, "slots"));

        if (SharePref.getIsColorPredictionVerticalVisible())
            arraylist.add(new HomescreenModel(3, COLOUR_PREDICTION_VERTICAL, R.drawable.color_pred_vertical, "slots"));

        if (SharePref.getIsDragonTigerVisible())
            arraylist.add(new HomescreenModel(4, DRAGON_TIGER, R.drawable.home_tgdr, "slots"));

        if (SharePref.getRouletteVisible())
            arraylist.add(new HomescreenModel(5, ROULETTE, R.drawable.home_rol, "slots"));

        if (SharePref.getIsSevenUpVisible())
            arraylist.add(new HomescreenModel(11, SEVEN_UP_DOWN, R.drawable.home_seven, "slots"));

        if (SharePref.getIsAviatorVerticalVisible())
            arraylist.add(new HomescreenModel(7, AVIATOR_VERTICAL, R.drawable.aviator_vertical_icon, "slots"));

        return arraylist;

    }
*/

    private List<HomescreenModel> getGameList() {
        List<HomescreenModel> arraylist = new ArrayList<>();

        //     arraylist.add(new HomescreenModel(27, AVIATOR, R.drawable.aviator_icon, "slots"));
        if (SharePref.getIsTeenpattiVisible())
            arraylist.add(new HomescreenModel(1, TEENPATTI, R.drawable.home_teenpatti, "Multi"));

        if (SharePref.getIsPrivateVisible())
            arraylist.add(new HomescreenModel(2, PRIVATE_TABLE, R.drawable.home_privatetable, "Multi"));

        if (SharePref.getIsCustomVisible())
            arraylist.add(new HomescreenModel(3, CUSTOM_TABLE, R.drawable.home_customnboot, "Multi"));

        if (SharePref.getIsHomeJackpotVisible())
            arraylist.add(new HomescreenModel(4, JACKPOT_3PATTI, R.drawable.home_jackpot, "All"));

        if (SharePref.getIsPointRummyVisible())
            arraylist.add(new HomescreenModel(5, POINT_RUMMY, R.drawable.home_pointrummy, "Skills"));

        if (SharePref.getIsPrivateRummyVisible())
            arraylist.add(new HomescreenModel(6, PRIVATE_RUMMY, R.drawable.home_pvtrummy, "Skills"));

        if (SharePref.getIsPoolRummyVisible())
            arraylist.add(new HomescreenModel(7, POOL_RUMMY, R.drawable.home_poolrummy, "Skills"));

        if (SharePref.getIsDealRummyVisible())
            arraylist.add(new HomescreenModel(8, DEAL_RUMMY, R.drawable.home_rummydeal, "Skills"));

        if (SharePref.getIsDragonTigerVisible())
            arraylist.add(new HomescreenModel(9, DRAGON_TIGER, R.drawable.home_tgdr, "slots"));

        if (SharePref.getIsAndharBaharVisible())
            arraylist.add(new HomescreenModel(10, ANDHAR_BAHAR, R.drawable.home_andhar_bhar, "slots"));

        if (SharePref.getIsSevenUpVisible())
            arraylist.add(new HomescreenModel(11, SEVEN_UP_DOWN, R.drawable.home_seven, "slots"));

        if (SharePref.getIsCarRouletteVisible())
            arraylist.add(new HomescreenModel(12, CAR_ROULETTE, R.drawable.home_car, "slots"));

        if (SharePref.getIsAnimalRouletteVisible())
            arraylist.add(new HomescreenModel(13, ANIMAL_ROULETTE, R.drawable.home_animal, "slots"));

        if (SharePref.getIsColorPredictionVisible())
            arraylist.add(new HomescreenModel(14, COLOUR_PREDICTION, R.drawable.home_color, "slots"));

        if (SharePref.getIsPokerVisible())
            arraylist.add(new HomescreenModel(15, POKER_GAME, R.drawable.home_poker, "slots"));

        if (SharePref.getIsHeadTailVisible())
            arraylist.add(new HomescreenModel(16, HEAD_TAIL, R.drawable.home_headtales, "slots"));

        if (SharePref.getIsRedBlackVisible())
            arraylist.add(new HomescreenModel(17, RED_BLACK, R.drawable.home_redblack, "slots"));

        if (SharePref.getIsLudoVisible())
            arraylist.add(new HomescreenModel(18, LUDO_GAME_PLAY_LOCAL, R.drawable.home_localludo, "slots"));

        if (SharePref.getIsLudoOnlineVisible())
            arraylist.add(new HomescreenModel(19, LUDO_GAME_ONLINE, R.drawable.home_onlineludo, "multi"));

        if (SharePref.getIsLudoComputerVisible())
            arraylist.add(new HomescreenModel(20, LUDO_GAME_COMPUTER, R.drawable.home_ludocomputer, "Skills"));

        if (SharePref.getBacarateVisible())
            arraylist.add(new HomescreenModel(21, BACCARAT, R.drawable.home_baccarat, "slots"));

        if (SharePref.getJhandi_MundaVisible())
            arraylist.add(new HomescreenModel(22, JHANDHI_MUNDA, R.drawable.home_jundimundi, "slots"));

        if (SharePref.getRouletteVisible())
            arraylist.add(new HomescreenModel(23, ROULETTE, R.drawable.home_rol, "slots"));

   /*     if (SharePref.getRouletteVisible())
            arraylist.add(new HomescreenModel(24, ROULETTE_DOUBLE, R.drawable.home_rol, "Multi"));
*/
        if (SharePref.getTournamentVisible())
            arraylist.add(new HomescreenModel(24, TOURNAMENT, R.drawable.home_tournament, "Multi"));

        if (SharePref.getIsColorPrediction1Visible())
            arraylist.add(new HomescreenModel(25, COLOUR_PREDICTION1, R.drawable.color_pred_1min, "slots"));

        if (SharePref.getIsColorPrediction3Visible())
            arraylist.add(new HomescreenModel(24, COLOUR_PREDICTION3, R.drawable.color_pred_3min, "slots"));

        if (SharePref.getIsColorPrediction30Visible())
            arraylist.add(new HomescreenModel(26, COLOUR_PREDICTION30, R.drawable.color_pred_vertical, "slots"));
/*

        if (SharePref.getIsAviatorVisible())
            arraylist.add(new HomescreenModel(27, AVIATOR, R.drawable.aviator_icon, "slots"));
*/

        if (SharePref.getIsAviatorVerticalVisible())
            arraylist.add(new HomescreenModel(27, AVIATOR_VERTICAL, R.drawable.aviator_vertical_icon, "slots"));

//        arraylist.add(new HomescreenModel(15,CRICKET_GAME,R.drawable.home_cricket,"Sports"));
//        arraylist.add(new HomescreenModel(16,BEST_OF_5,R.drawable.home_best_of_5,"All"));
        return arraylist;

    }








    private void BonusView() {

        if (SharePref.getInstance().getBoolean(SharePref.isBonusShow))
            lnrmail.setVisibility(View.VISIBLE);
        else
            lnrmail.setVisibility(View.GONE);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String datevalue = prefs.getString("cur_date4", "12/06/2020");

        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate1 = df1.format(c);
        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), datevalue, formattedDate1);

        if (dateDifference > 0) {
            // catalog_outdated = 1;

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c);

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("cur_date4", formattedDate);
            editor.apply();

            if (SharePref.getInstance().getBoolean(SharePref.isBonusShow))
                showDailyWinCoins();

        } else {
            System.out.println("");
        }

        lnrmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getApplicationContext(), MaiUserListingActivity.class);
//                startActivity(intent);
//                Funtions.showToast(context, "Coming Soon",
//                        Toast.LENGTH_LONG).show();
                showDailyWinCoins();

            }
        });
    }

    private void showDailyWinCoins() {
        DialogDailyBonus.getInstance(context).returnCallback(new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                UserProfile();
            }
        }).show();
    }

    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.home_container, fragment).
                addToBackStack(null).
                commit();
    }

    private void BlinkAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        final Animation animationUtils = AnimationUtils.loadAnimation(context, R.anim.blink);
        animationUtils.setDuration(1000);
        animationUtils.setRepeatCount(1);
        animationUtils.setStartOffset(700);
        view.startAnimation(animationUtils);

        animationUtils.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void shineAnimation(final View view) {
        final Animation animationUtils = AnimationUtils.loadAnimation(context, R.anim.left_to_righ);
        animationUtils.setDuration(1500);
        view.startAnimation(animationUtils);

        animationUtils.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(animationUtils);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    LinearLayout lnr_2player, lnr_5player, lnr_private;
    TextView tv_2player, tv_5player;
    int selected_type = 5;
    Button btn_join_private;

    public void Dialog_SelectPlayer() {
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_select_palyer);
        lnr_2player = dialog.findViewById(R.id.lnr_2player);
        lnr_5player = dialog.findViewById(R.id.lnr_5player);
        lnr_private = dialog.findViewById(R.id.lnr_private);
        tv_2player = (TextView) dialog.findViewById(R.id.tv_2player);
        tv_5player = (TextView) dialog.findViewById(R.id.tv_5player);
        btn_join_private = (Button) dialog.findViewById(R.id.btn_join_private);
        tv_5player.setText("6 \nPlayer");
//        lnr_private.setVisibility(View.VISIBLE);

        Button btn_player = dialog.findViewById(R.id.btn_play);
        ImageView img_close = dialog.findViewById(R.id.img_close);


        btn_join_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);

                // builder.setTitle("Choose Type Of Material");
                View rowList = getLayoutInflater().inflate(R.layout.join_private, null);
                EditText et_code = rowList.findViewById(R.id.et_code);
                Button btn_join = rowList.findViewById(R.id.btn_join_private);


                builder.setView(rowList);
                AlertDialog dialog = builder.create();
                dialog.show();

                btn_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str_code = et_code.getText().toString().trim();
                        if (str_code.equals("")) {
                            Toast.makeText(Homepage.this, "Please enter valid code to join!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            join_privateTable(str_code);
                        }
                    }
                });

            }
        });

        ChangeTextviewColorChange(5);

        lnr_2player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTextviewColorChange(2);
            }
        });

        lnr_5player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTextviewColorChange(5);
            }
        });

        btn_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (selected_type == 2) {
                    LoadTableFragments(ActiveTables_BF.RUMMY2);

                } else {
                    LoadTableFragments(ActiveTables_BF.RUMMY5);


                }


            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


    }

    private void join_privateTable(String code) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.join_private,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.d("DATA_CHECK_PRIVATE", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equalsIgnoreCase("200")) {
                                String message = jsonObject.getString("message");
                                Functions.showToast(context, message);
                                Intent i = new Intent(Homepage.this, RummyPointSocket.class);
                                i.putExtra("player5", "player5");
                                i.putExtra("call_status", "1");
                                i.putExtra("private_table", "1");
//                                i.putExtra(SEL_TABLE,pool_boot);
                                i.putExtra("min_entry", "0");
                                i.putExtra("table_type", "0");
                                i.putExtra("hide_hint", "1");
                                startActivity(i);
                            } else {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    Functions.showToast(context, message);
                                }

                            }
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
                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("user_id", prefs.getString("user_id", ""));
                params.put("code", "" + code);
                params.put("token", prefs.getString("token", ""));
                Log.d("paremter_java_join", "getParams: " + params);
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

    public void Dialog_SelectPlayerPool() {
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_select_palyer);
        lnr_2player = dialog.findViewById(R.id.lnr_2player);
        lnr_5player = dialog.findViewById(R.id.lnr_5player);
        tv_2player = (TextView) dialog.findViewById(R.id.tv_2player);
        tv_5player = (TextView) dialog.findViewById(R.id.tv_5player);
        tv_5player.setText("6 \nPlayer");

        Button btn_player = dialog.findViewById(R.id.btn_play);
        ImageView img_close = dialog.findViewById(R.id.img_close);

        ChangeTextviewColorChange(5);

        lnr_2player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTextviewColorChange(2);
            }
        });

        lnr_5player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTextviewColorChange(5);
            }
        });

        btn_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (selected_type == 2) {
                    LoadPullRummyActiveTable(ActiveTables_BF.RUMMY2);

                } else {
                    LoadPullRummyActiveTable(ActiveTables_BF.RUMMY5);
                }


            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    private void LoadTableFragments(String TAG) {
        ActiveTables_BF activeTables_bf = new ActiveTables_BF(TAG);
        activeTables_bf.show(getSupportFragmentManager(), activeTables_bf.getTag());
    }

    private void LoadPointRummyActiveTable(String TAG) {
        RummyActiveTables_BF rummyActiveTables_bf = new RummyActiveTables_BF(TAG);
        rummyActiveTables_bf.show(getSupportFragmentManager(), rummyActiveTables_bf.getTag());
    }

    private void ChangeTextviewColorChange(int colortype) {

        selected_type = colortype;

        if (colortype == 2) {
            tv_2player.setTextColor(getResources().getColor(R.color.white));
            lnr_2player.setBackgroundColor(getResources().getColor(R.color.new_colorPrimary));

            tv_5player.setTextColor(getResources().getColor(R.color.black));
            lnr_5player.setBackgroundColor(getResources().getColor(R.color.white));

        } else {
            tv_2player.setTextColor(getResources().getColor(R.color.black));
            lnr_2player.setBackgroundColor(getResources().getColor(R.color.white));

            tv_5player.setTextColor(getResources().getColor(R.color.white));
            lnr_5player.setBackgroundColor(getResources().getColor(R.color.new_colorPrimary));

        }


    }


    private void rotation_animation(View view, int animation) {

        Animation circle = Functions.AnimationListner(context, animation, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

            }
        });
        view.setAnimation(circle);
        circle.startNow();

    }

    int Counts = 100;
    int postion = -100;

    private void CenterAnimationView(String imagename, View imgcards, int Home_Page_Animation) {


        int AnimationSpeed = Counts + Home_Page_Animation;
        Counts += 300;

        final View fromView, toView;
        rootView.setVisibility(View.VISIBLE);
//        rootView.removeAllViews();
//        View ivpickcard = findViewById(R.id.view_center);

        fromView = rootView;
        toView = imgcards;


        int fromLoc[] = new int[2];
        fromView.getLocationOnScreen(fromLoc);
        float startX = fromLoc[0];
        float startY = fromLoc[1];

        int toLoc[] = new int[2];
        toView.getLocationOnScreen(toLoc);
        float destX = toLoc[0];
        float destY = toLoc[1];

        final ImageView sticker = new ImageView(context);

        int stickerId = Functions.GetResourcePath(imagename, context);

        int card_width = (int) getResources().getDimension(R.dimen.home_card_width);
        int card_hieght = (int) getResources().getDimension(R.dimen.home_card_height);

        Picasso.get().load(stickerId).into(sticker);

        sticker.setLayoutParams(new ViewGroup.LayoutParams(card_width, card_hieght));
        rootView.addView(sticker);


        Animations anim = new Animations();
        Animation a = anim.fromAtoB(0, 0, convertDpToPixel(postion, context), 0, null, AnimationSpeed, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                fromView.setVisibility(View.VISIBLE);
                toView.setVisibility(View.VISIBLE);
                sticker.setVisibility(View.GONE);
            }
        });
        sticker.setAnimation(a);
        a.startNow();

        postion += 100;

    }

    @Override
    protected void onResume() {
        super.onResume();
        str_colr_pred = "";
//        EnableGPS();
        UserProfile();
        getAdminSettings(); // 🎯 Get admin settings including bank details
        GameLeave();
        UserLudoLeaveTable();
    }

    private void UserLudoLeaveTable() {
        LudoApiRepository.getInstance().call_api_leaveTable();
    }


    public void clickTask() {

        imgPrivategame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialoag();
                openPrivateTeenpatti(null);

            }
        });

        lnrsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogSettingOption dialogSettingOption = new DialogSettingOption(context) {
                    public void playstopSound() {

                        playSound(R.raw.game_soundtrack, true);

                    }
                };

                dialogSettingOption.showDialogSetting();
            }
        });


        imgLogout.setOnClickListener(new View.OnClickListener() {       //contact us
            @Override
            public void onClick(View view) {

//                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(Homepage.this);
////                builder.setTitle("Location")
//                builder.setMessage("Please connect us on +91 9999999999")
//                        .setCancelable(true)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                android.app.AlertDialog alert = builder.create();
//                alert.setTitle("Contact Us");
////                alert.show();

                String cont = SharePref.getInstance().getString(SharePref.contact_us);

                Functions.Dialog_CancelAppointmentNew(context, "Contact Us", cont, new Callback() {
                    @Override
                    public void Responce(String resp, String type, Bundle bundle) {
                        if (resp.equals("yes")) {
                            stopPlaying();
                            finish();
                        }
                    }
                });

            }
        });

        lnrinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogReferandEarn.getInstance(context).show();
            }
        });
        findViewById(R.id.ivIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogReferandEarn.getInstance(context).show();
            }
        });
    }

    public void openBuyChipsActivity(View view) {
        Log.d("AddCash", "Buy chips activity button clicked - fetching user data first");

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading user data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Call user data API first before navigating to payment screen
        CommonAPI.CALL_API_UserDetails(context, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                progressDialog.dismiss();

                if (resp != null) {
                    Log.d("AddCash", "User data fetched successfully from buy chips button, checking pay_address");

                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        // Check if code exists
                        if (!jsonObject.has("code")) {
                            Log.e("AddCash", "Response missing 'code' field from buy chips button");
                            Functions.showToast(context, "Invalid response format. Please try again.");
                            return;
                        }

                        String code = jsonObject.getString("code");
                        Log.d("AddCash", "API response code from buy chips button: " + code);

                        if (code.equalsIgnoreCase("200")) {
                            // Check if user_data array exists and has elements
                            if (!jsonObject.has("user_data")) {
                                Log.e("AddCash", "Response missing 'user_data' field from buy chips button");
                                showPayAddressNotFoundDialog();
                                return;
                            }

                            JSONArray userDataArray = jsonObject.getJSONArray("user_data");
                            if (userDataArray.length() == 0) {
                                Log.e("AddCash", "user_data array is empty from buy chips button");
                                showPayAddressNotFoundDialog();
                                return;
                            }

                            JSONObject jsonObject0 = userDataArray.getJSONObject(0);
                            String pay_address = jsonObject0.optString("pay_address", null);

                            Log.d("AddCash", "pay_address from buy chips button: " + pay_address);

                            // Parse the full response to update user data
                            ParseResponse(resp);

                            // 🎯 Use PaymentGatewayHelper to show appropriate payment options
                            Log.d("AddCash", "Using PaymentGatewayHelper to determine payment options");
                            Log.d("AddCash", "Payment Gateway Status: " + PaymentGatewayHelper.getPaymentGatewayStatus(context));
                            PaymentGatewayHelper.showPaymentOptions(context);
                        } else {
                            Log.e("AddCash", "API response code not 200 from buy chips button: " + code);
                            Functions.showToast(context, "Failed to load user data. Please try again.");
                        }
                    } catch (JSONException e) {
                        Log.e("AddCash", "JSON parsing error from buy chips button: " + e.getMessage());
                        e.printStackTrace();
                        Functions.showToast(context, "Failed to parse user data. Please try again.");
                    } catch (Exception e) {
                        Log.e("AddCash", "Unexpected error from buy chips button: " + e.getMessage());
                        e.printStackTrace();
                        Functions.showToast(context, "An error occurred. Please try again.");
                    }
                } else {
                    Log.e("AddCash", "Failed to fetch user data from buy chips button - response is null");
                    Functions.showToast(context, "Failed to load user data. Please try again.");
                }
            }
        }, token);
    }

    private void check_payment() {
        Dialog_AppVersion((Activity) context, "Select Payment Option", "", new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp.equals("Update")) {
                    finish();
                }
            }
        });
    }

    public void Dialog_AppVersion(Activity context, String title, String second_text, final Callback callback) {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_payment);

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        TextView tv_subheading = dialog.findViewById(R.id.tv_subheading);
        TextView txt = dialog.findViewById(R.id.yes);

        tv_heading.setText(Html.fromHtml(title));

        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homepage.this, BuyChipsList.class);
                i.putExtra("coin_type", "1");
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_no).setVisibility(View.VISIBLE);

        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🎯 Use PaymentGatewayHelper for payment options
                PaymentGatewayHelper.showPaymentOptions(Homepage.this);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void Dialog_AppUpdate() {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_app_update);

        String appName = getString(R.string.app_name);
        String updateMessage = appName + " needs an update";

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        tv_heading.setText(updateMessage);

        TextView tv_update = dialog.findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.MAIN));
                startActivity(browserIntent);
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void showPayAddressNotFoundDialog() {
        Log.d("AddCash", "Showing pay address not found dialog");

        try {
            final Dialog dialog = Functions.DialogInstance(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_payment);

            TextView tv_heading = dialog.findViewById(R.id.tv_heading);
            TextView btn_yes_text = dialog.findViewById(R.id.yes);

            if (tv_heading != null) {
                tv_heading.setText("Payment Wallet Not Set Up");
            } else {
                Log.e("AddCash", "tv_heading not found in dialog layout");
            }

            if (btn_yes_text != null) {
                btn_yes_text.setText("OK");
            } else {
                Log.e("AddCash", "yes button text not found in dialog layout");
            }

            View btnYes = dialog.findViewById(R.id.btn_yes);
            if (btnYes != null) {
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("AddCash", "User clicked OK on pay address not found dialog");
                        dialog.dismiss();
                        // You can add additional actions here if needed
                    }
                });
            } else {
                Log.e("AddCash", "btn_yes not found in dialog layout");
            }

            // Hide the second button since we only need one action
            View btnNo = dialog.findViewById(R.id.bt_no);
            if (btnNo != null) {
                btnNo.setVisibility(View.GONE);
            } else {
                Log.e("AddCash", "bt_no not found in dialog layout");
            }

            View ivClose = dialog.findViewById(R.id.ivClose);
            if (ivClose != null) {
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            } else {
                Log.e("AddCash", "ivClose not found in dialog layout");
            }

            dialog.show();
            Functions.setDialogParams(dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        } catch (Exception e) {
            Log.e("AddCash", "Error showing pay address not found dialog: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple toast message
            Functions.showToast(context, "Payment wallet not set up. Please contact support.");
        }
    }


    public void UserProfile() {
        CommonAPI.CALL_API_UserDetails(context, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                if (resp != null) {
                    ParseResponse(resp);
                }
            }
        }, token);

    }

    // 🎯 Get Admin Settings including Bank Details
    private void getAdminSettings() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.TERMS_CONDITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");

                            if (code.equalsIgnoreCase("200")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("setting");

                                // 🎯 COMPLETE TERMS_CONDITION Response - ALL DATA
                                Log.d("TERMS_RESPONSE_START", "=== STARTING COMPLETE RESPONSE ===");
                                int chunkSize = 1000; // Smaller chunks to show ALL data
                                for (int i = 0; i < response.length(); i += chunkSize) {
                                    int end = Math.min(response.length(), i + chunkSize);
                                    Log.d("TERMS_CHUNK_" + String.format("%02d", (i/chunkSize + 1)), response.substring(i, end));
                                }
                                Log.d("TERMS_RESPONSE_END", "=== COMPLETE RESPONSE FINISHED ===");
                                Log.d("TERMS_TOTAL_LENGTH", "Total response length: " + response.length() + " characters");

                                // 🎯 Extract and Store Bank Details & Payment Gateway Settings
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("bank_account_number", jsonObject1.optString("bank_account_number"));
                                editor.putString("bank_ifsc_code", jsonObject1.optString("bank_ifsc_code"));
                                editor.putString("bank_account_name", jsonObject1.optString("bank_account_name"));
                                editor.putString("bank_name", jsonObject1.optString("bank_name"));
                                editor.putString("bank_branch", jsonObject1.optString("bank_branch"));

                                // 🎯 Store Payment Gateway Settings
                                editor.putString("inr_payment_gateway", jsonObject1.optString("inr_payment_gateway"));
                                editor.putString("crypto_payment_gateway", jsonObject1.optString("crypto_payment_gateway"));
                                editor.apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ADMIN_SETTINGS", "Error fetching admin settings: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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

    private void ParseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = jsonObject.getString("code");
            if (code.equalsIgnoreCase("200")) {
                JSONObject jsonObject0 = jsonObject.getJSONArray("user_data").getJSONObject(0);
                user_id = jsonObject0.getString("id");
                name = jsonObject0.optString("name");
                mobile = jsonObject0.optString("mobile");
                profile_pic = jsonObject0.optString("profile_pic");
                profile_img = jsonObject0.optString("profile_pic");
                referral_code = jsonObject0.optString("referral_code");
                wallet = jsonObject0.optString("wallet");
                Log.d("wallet_home", wallet);
                String winning_wallet = jsonObject0.optString("winning_wallet");
                String spin_remaining = jsonObject0.getString("spin_remaining");
                Log.d("extra_spinner_", spin_remaining);
                SharePref.getInstance().setSpin_remaining(spin_remaining);
                game_played = jsonObject0.optString("game_played");
                bank_detail = jsonObject0.optString("bank_detail");
                upi = jsonObject0.optString("upi");
                adhar_card = jsonObject0.optString("adhar_card");

                // Log pay_address for debugging only
                String pay_address = jsonObject0.optString("pay_address", null);
                String pay_privatekey = jsonObject0.optString("pay_privatekey", null);
                Log.d("ParseResponse", "pay_address: " + pay_address + ", pay_privatekey: " + (pay_privatekey != null ? "***" : "null"));

                notification_image = jsonObject.optString("notification_image");
                Log.d("notification_image", "notification_image" + notification_image);


                /*------------------------- Banner Source code ------------------------- */
                bannerModelArrayList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("app_banner");

                Log.d("jsonArray", "jsonArray_" + jsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    BannerModel bannerModel = new BannerModel();
                    bannerModel.setId(jsonObject1.getString("id"));
                    bannerModel.setImg(jsonObject1.getString("banner"));
                    bannerModelArrayList.add(bannerModel);
                }

               /* SlidingImageAdapter adapter = new SlidingImageAdapter();
                viewPager.setFocusableInTouchMode(true);
                viewPager.setAdapter(adapter);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        viewpager.post(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem((viewPager.getCurrentItem()) % bannerModelArrayList.size());
                            }
                        });
                    }
                };
                timer = new Timer();
                timer.schedule(timerTask, 3000, 3000);
                headerViewPager.setAdapter(adapter);
                CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.titles);
                indicator.setViewPager(headerViewPager);
                headerViewPager.startAutoScroll();
                headerViewPager.setInterval(3000);
                headerViewPager.setScrollDurationFactor(5);*/



                SlidingImageAdapter adapter = new SlidingImageAdapter();
                viewpager.setFocusableInTouchMode(true);
                viewpager.setAdapter(adapter);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        viewpager.post(new Runnable() {
                            @Override
                            public void run() {
                                int currentItem = viewpager.getCurrentItem();
                                int nextItem = (currentItem + 1) % bannerModelArrayList.size(); // Ensure it loops
                                viewpager.setCurrentItem(nextItem, true); // Smooth scroll to next item
                            }
                        });
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask, 8000, 8000); // Start after 8 seconds, repeat every 8 seconds
                CirclePageIndicator indicator = findViewById(com.gamegards.gaming27.R.id.titles);
                indicator.setViewPager(viewpager);
                indicator.setCurrentItem(0);









                //------banner ends here -----

                // txtName.setText("Welcome Back "+name);

                if (Functions.isStringValid(wallet)) {
                    float f_wallet = Float.parseFloat(wallet);
                    Log.d("float_home", "" + f_wallet);
//                    long numberamount =  Float.parseFloat(f_wallet);
//                    long numberamount = (long) f_wallet;
                    txtwallet.setText("" + NumberFormat.getNumberInstance(Locale.US).format(f_wallet));

//                    if (wallet.length() >=4) {
//                        f_wallet = f_wallet/1000;
//                        txtwallet.setText(String.format("%.1f", f_wallet)+"k");
//                    }
//                        else{
//                        txtwallet.setText("" + f_wallet);
//                        }

                }

                ((TextView) findViewById(R.id.txt_user_id)).setText("ID:" + user_id);
                txtproname.setText(name);
                Picasso.get().load(Const.IMGAE_PATH + profile_pic).into(imaprofile);

                String setting = jsonObject.optString("setting");
                JSONObject jsonObjectSetting = new JSONObject(setting);
                JSONArray avatar = jsonObject.getJSONArray("avatar");

                game_for_private = jsonObjectSetting.optString("game_for_private");
                app_version = jsonObjectSetting.optString("app_version");
                String referral_amount = jsonObjectSetting.optString("referral_amount");
                String referral_link = jsonObjectSetting.optString("referral_link");
                String joining_amount = jsonObjectSetting.optString("joining_amount");
                String whats_no = jsonObjectSetting.optString("whats_no");
                String mBan_states = jsonObjectSetting.optString("ban_states");
                String extra_spinner = jsonObjectSetting.optString("extra_spinner");

                int admin_commission = jsonObjectSetting.optInt("admin_commission", 2);

                ban_states = Arrays.asList(mBan_states.split(","));
                String versionName = null;
//                checkForBanState();
                try {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    versionName = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if(!app_version.equals(versionName)){
                    Dialog_AppUpdate();
                }
                tvUserversion.setText("Version "+ app_version);

                BonusView();

                ((ImageView) findViewById(R.id.imgicon)).setImageDrawable(
                        Variables.CURRENCY_SYMBOL.equalsIgnoreCase(Variables.RUPEES) ? Functions.getDrawable(context, R.drawable.ic_currency_rupee) :
                                Variables.CURRENCY_SYMBOL.equalsIgnoreCase(Variables.DOLLAR) ? Functions.getDrawable(context, R.drawable.ic_currency_dollar) :
                                        Functions.getDrawable(context, R.drawable.ic_currency_chip));

                tvUserCategory.setText(SharePref.getInstance().getUSER_CATEGORY());

            } else if (code.equals("411")) {

                Intent intent = new Intent(context, LoginScreen.class);
                startActivity(intent);
                finishAffinity();
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", "");
                editor.putString("referal_code", "");
                editor.putString("img_name", "");
                editor.putString("game_for_private", "");
                editor.putString("app_version", "");
                editor.apply();

                Functions.showToast(context, "You are Logged in from another " +
                        "device.");


            } else {

                if (jsonObject.has("message")) {
                    String message = jsonObject.getString("message");
                    Functions.showToast(context, message);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        findViewById(R.id.iv_add).clearAnimation();
        Functions.dismissLoader();

    }

   // @OnClick(R.id.tvUserCategory)
    void openUserRanking() {
        DialogUserRanking.getInstance(context).setCallback(new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
            }
        }).show();
    }

    public void PlaySaund(int sound) {

        if (!SharePref.getInstance().isSoundEnable())
            return;

        final MediaPlayer mp = MediaPlayer.create(context,
                sound);
        mp.start();
    }

    public void showDialoagPrivettable() {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.custom_dialog_custon_boot);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        sBarpri = (SeekBar) dialog.findViewById(R.id.seekBar1);
        sBarpri.setProgress(0);
        sBarpri.incrementProgressBy(10);
        sBarpri.setMax(100);
        txtStartpri = (TextView) dialog.findViewById(R.id.txtStart);
        txtLimitpri = (TextView) dialog.findViewById(R.id.txtLimit);
        txtwalletchipspri = (TextView) dialog.findViewById(R.id.txtwalletchips);
        float numberamount = Float.parseFloat(wallet);
        txtwalletchipspri.setText("" + NumberFormat.getNumberInstance(Locale.US).format(numberamount));
        txtheader = (TextView) dialog.findViewById(R.id.txtheader);
        txtheader.setText("Private Table");
        // txtwalletchipspri.setText(wallet);
        txtBootamountpri = (TextView) dialog.findViewById(R.id.txtBootamount);
        txtPotLimitpri = (TextView) dialog.findViewById(R.id.txtPotLimit);
        txtNumberofBlindpri = (TextView) dialog.findViewById(R.id.txtNumberofBlind);
        txtMaximumbetvaluepri = (TextView) dialog.findViewById(R.id.txtMaximumbetvalue);
        imgclosetoppri = (ImageView) dialog.findViewById(R.id.imgclosetop);
        imgclosetoppri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imgCreatetable = (ImageView) dialog.findViewById(R.id.imgCreatetable);
        imgJointable = (ImageView) dialog.findViewById(R.id.imgJointable);

        imgCreatetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TeenPattiSocket.class);
//                Intent intent = new Intent(context, PublicTable_New.class);
                intent.putExtra("gametype", TeenPattiSocket.PRIVATE_TABLE);
                intent.putExtra("bootvalue", pvalpri + "");
                intent.putExtra("table_type", "create");
                startActivity(intent);
                dialog.dismiss();
            }
        });

        imgJointable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showCustomDialog();

                dialog.dismiss();

                Functions.Dialog_EnterTableId(context, "", "", new Callback() {
                    @Override
                    public void Responce(String resp, String type, Bundle bundle) {
                        Intent intent = new Intent(context, TeenPattiSocket.class);
                        intent.putExtra("gametype", TeenPattiSocket.PRIVATE_TABLE);
                        intent.putExtra("table_id", resp);
                        intent.putExtra("table_type", "join");
                        startActivity(intent);
                    }
                });
            }
        });

        // tView.setText(sBar.getProgress() + "/" + sBar.getMax());
        sBarpri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 10;
                if (progress == 1) {

                    pvalpri = 50;

                } else if (progress == 2) {
                    pvalpri = 100;
                } else if (progress == 3) {

                    pvalpri = 500;
                } else if (progress == 4) {

                    pvalpri = 1000;

                } else if (progress == 5) {

                    pvalpri = 5000;

                } else if (progress == 6) {

                    pvalpri = 10000;
                } else if (progress == 7) {

                    pvalpri = 25000;
                } else if (progress == 8) {

                    pvalpri = 50000;
                } else if (progress == 9) {

                    pvalpri = 100000;
                } else if (progress == 10) {

                    pvalpri = 250000;
                }

                //progress = progress * 10;
                // pvalpri = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtBootamountpri.setText("Boot amount : " + kvalue(pvalpri) + "");

                int valueforpot = pvalpri * 1024;
                int valueformaxi = pvalpri * 128;

                //long valueforpotlong= valueforpot;

                txtPotLimitpri.setText("Pot limit : " + kvalue(valueforpot) + "");
                txtMaximumbetvaluepri.setText("Maximumbet balue : " + kvalue(valueformaxi) + "");
                txtNumberofBlindpri.setText("Number of Blinds : 4");
                //tView.setText(pval + "/" + seekBar.getMax());
            }
        });
        txtBootamountpri.setText("Boot amount : " + kvalue(50) + "");
        int valueforpotinital = 50 * 1024;
        int valueformaxiinital = 50 * 128;
        txtPotLimitpri.setText("Pot limit : " + kvalue(valueforpotinital) + "");
        txtMaximumbetvaluepri.setText("Maximumbet balue : " + kvalue(valueformaxiinital) + "");
        txtNumberofBlindpri.setText("Number of Blinds : 4");

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    TextView txtheader;

    public void showDialoagonBack() {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.custom_dialog_custon_boot);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        sBar = (SeekBar) dialog.findViewById(R.id.seekBar1);
        sBar.setProgress(0);
        sBar.incrementProgressBy(10);
        sBar.setMax(100);
        txtStart = (TextView) dialog.findViewById(R.id.txtStart);
        txtLimit = (TextView) dialog.findViewById(R.id.txtLimit);
        txtwalletchips = (TextView) dialog.findViewById(R.id.txtwalletchips);
        float numberamount = Float.parseFloat(wallet);
        txtwalletchips.setText("" + NumberFormat.getNumberInstance(Locale.US).format(numberamount));
        txtBootamount = (TextView) dialog.findViewById(R.id.txtBootamount);
        txtPotLimit = (TextView) dialog.findViewById(R.id.txtPotLimit);
        txtNumberofBlind = (TextView) dialog.findViewById(R.id.txtNumberofBlind);
        txtMaximumbetvalue = (TextView) dialog.findViewById(R.id.txtMaximumbetvalue);
        txtheader = (TextView) dialog.findViewById(R.id.txtheader);
        txtheader.setText("Custom Boot");
        imgclosetop = (ImageView) dialog.findViewById(R.id.imgclosetop);
        imgclosetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView imgJointable = (ImageView) dialog.findViewById(R.id.imgJointable);
        imgJointable.setVisibility(View.GONE);
        imgCreatetablecustom = (ImageView) dialog.findViewById(R.id.imgCreatetable);
        imgCreatetablecustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("RES_VALUE", "bootvalue" + pval);
                Intent intent = new Intent(context, TeenPattiSocket.class);
//                Intent intent = new Intent(context, PublicTable_New.class);
                intent.putExtra("gametype", TeenPattiSocket.CUSTOME_TABLE);
                intent.putExtra("bootvalue", pval + "");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        // tView.setText(sBar.getProgress() + "/" + sBar.getMax());
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                progress = progress / 10;
                if (progress == 1) {

                    pval = 50;

                } else if (progress == 2) {
                    pval = 100;
                } else if (progress == 3) {

                    pval = 500;
                } else if (progress == 4) {

                    pval = 1000;

                } else if (progress == 5) {

                    pval = 5000;

                } else if (progress == 6) {

                    pval = 10000;
                } else if (progress == 7) {

                    pval = 25000;
                } else if (progress == 8) {

                    pval = 50000;
                } else if (progress == 9) {

                    pval = 100000;
                } else if (progress == 10) {

                    pval = 250000;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtBootamount.setText("Boot amount : " + kvalue(pval) + "");

                int valueforpot = pval * 1024;
                int valueformaxi = pval * 128;
                txtPotLimit.setText("Pot limit : " + kvalue(valueforpot) + "");
                txtMaximumbetvalue.setText("Maximumbet balue : " + kvalue(valueformaxi) + "");
                txtNumberofBlind.setText("Number of Blinds : 4");
                //tView.setText(pval + "/" + seekBar.getMax());
            }
        });

        txtBootamount.setText("Boot amount : " + kvalue(50) + "");

        int valueforpotinitl = 50 * 1024;
        int valueformaxiiniti = 50 * 128;
        txtPotLimit.setText("Pot limit : " + kvalue(valueforpotinitl) + "");
        txtMaximumbetvalue.setText("Maximumbet balue : " + kvalue(valueformaxiiniti) + "");
        txtNumberofBlind.setText("Number of Blinds : 4");

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    private void GameLeave() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.GAME_TABLE_LEAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        System.out.println("" + response);
                        // finish();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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

    public void openLuckJackpotActivity(View view) {
        startActivity(new Intent(context, LuckJackPot_A_Socket.class));
    }

    public void lnrWalletConnect(View view) {
        try {
            // Open the new WalletConnectActivity with real Reown AppKit
            Intent intent = new Intent(Homepage.this, WalletConnectActivity.class);
            startActivity(intent);

            // Show message
            Functions.showToast(context, "🔗 Opening Wallet Connect with real Reown AppKit...");

        } catch (Exception e) {
            e.printStackTrace();
            Functions.showToast(context, "❌ Failed to open wallet connection: " + e.getMessage());
        }
    }

    public void openSevenup(View view) {

        //    startActivity(new Intent(context, SevenUp_A.class));
        startActivity(new Intent(context, SevenUp_Socket.class));
    }

    public void openPublicTeenpatti(View view) {
        PlaySaund(R.raw.buttontouchsound);
        LoadTableFragments(ActiveTables_BF.TEENPATTI);
    }

    public void openPrivateTeenpatti(View view) {
        PlaySaund(R.raw.buttontouchsound);
        float gamecount = 0;
        if (game_played != null && !game_played.equals("")) {
            gamecount = Float.parseFloat(game_played);
        }
        float game_for_privatetemp = Float.parseFloat(game_for_private);
        if (gamecount > game_for_privatetemp) {

            showDialoagPrivettable();

        } else {

            Functions.showToast(context, "To Unblock Private Table you have to Play at least " + game_for_privatetemp +
                    " Games.");

        }
    }

    public void openCustomeTeenpatti(View view) {
        PlaySaund(R.raw.buttontouchsound);
        showDialoagonBack();
    }

    public void openRummyGame(View view) {
        SharedPreferences prefs;
        HashMap<String, String> params = new HashMap<String, String>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        params.put("no_of_players", "2");
        ApiRequest.Call_Api(context, Const.RummygetTableMaster, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                if (resp != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        int code = jsonObject.optInt("code", 404);
                        String message = jsonObject.optString("message", "Something went wrong");

                        if (code == 205 || message.equals("You are Already On Table")) {

                            String no_of_players = jsonObject.optString("no_of_players", "");


                            Intent intent = null;

                            intent = new Intent(context, RummyPointSocket.class);
                            if (no_of_players.equals("2")) {
                                intent.putExtra("player2", "player2");
                            } else {

                                intent.putExtra("player6", "player6");
                            }
                            intent.putExtra("private_table", "0");

                            if (intent != null)
                                startActivity(intent);


                            return;
                        } else {
                            Dialog_SelectPlayer();
                        }
                    } catch (JSONException e) {

                    }
                }

            }
        });


//        LoadPointRummyActiveTable(ActiveTables_BF.RUMMY5);
    }

//    public void openRummyPullGame(View view) {
//        Dialog_SelectPlayerPool();
//    }

    public void openRummyPullGame(View view) {
        SharedPreferences prefs;
        HashMap<String, String> params = new HashMap<String, String>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        params.put("no_of_players", "2");
        ApiRequest.Call_Api(context, Const.RummypoolgetTableMaster, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                if (resp != null) {
                    try {

                        Log.e("RES_VALUE", "API call for Check already on table" + resp);
                        JSONObject jsonObject = new JSONObject(resp);
                        int code = jsonObject.optInt("code", 404);
                        String message = jsonObject.optString("message", "Something went wrong");

                        if (code == 205 || message.equals("You are Already On Table")) {

                            String no_of_players = jsonObject.optString("no_of_players", "");

                            Intent intent = null;

                            intent = new Intent(context, RummyPullGameSocket.class);
                            if (no_of_players.equals("2")) {
                                intent.putExtra("player2", "player2");
                            } else {

                                intent.putExtra("player6", "player6");
                            }

                            if (intent != null)
                                startActivity(intent);


                            return;
                        } else {
                            Dialog_SelectPlayerPool();
                        }
                    } catch (JSONException e) {

                    }
                }

            }
        });


//        LoadPointRummyActiveTable(ActiveTables_BF.RUMMY5);
    }

    public void openPokerGame(View view) {
        LoadPokerGameActiveTable(ActiveTables_BF.RUMMY5);
    }


    public void openRummyDealGame(View view) {
//        LoadDealRummyActiveTable(ActiveTables_BF.RUMMY2);
        DialogDealType dialogDealType = new DialogDealType(this) {
            @Override
            protected void startGame(Bundle bundle) {
                openRummyGames(bundle);
            }
        };
        dialogDealType.show();
    }

    private void openRummyGames(Bundle bundle) {
        Intent intent = new Intent(context, RummyDealGameSocket.class);
        if (bundle != null)
            intent.putExtras(bundle);

        if (context != null && !context.isFinishing())
            context.startActivity(intent);
    }


    public void openAndharbahar(View view) {
//        startActivity(new Intent(context, Andhar_Bahar_NewUI.class).putExtra("room_id", "1"));
        startActivity(new Intent(context, Andhar_Bahar_Socket.class).putExtra("room_id", "1"));
    }

    public void openRoulette(View view) {
        //   startActivity(new Intent(context, RouleteGame_A.class).putExtra("room_id", "1"));
        startActivity(new Intent(context, RouleteGame_Socket.class).putExtra("room_id", "1"));
    }

    public void openRoulette1(View view) {
        //   startActivity(new Intent(context, RouleteGame_A.class).putExtra("room_id", "1"));
        startActivity(new Intent(context, RouleteDoubleGame_Socket.class).putExtra("room_id", "1"));
    }

    public void openTournament() {
        Intent intent = new Intent(context, TourList.class);
        context.startActivity(intent);
    }

    public void openAviator() {
        Intent intent = new Intent(context, Aviator_Game_Socket.class);
        context.startActivity(intent);
    }

    public void openAviatorVartical() {
        Intent intent = new Intent(context, Aviator_Game_Socket_Vertical.class);
        context.startActivity(intent);
    }

    public void openColorPred(View view) {
//        Intent intent = new Intent(context, ColorPrediction.class);
        Intent intent = new Intent(context, ColorPrediction_Socket.class);
        context.startActivity(intent);
    }

    public void openColorPred1(View view) {
        //    Intent intent = new Intent(context, ColorPrediction1.class);
        Intent intent = new Intent(context, ColorPrediction1_Socket.class);
        context.startActivity(intent);
    }

    public void openColorPred3(View view) {
        //    Intent intent = new Intent(context, ColorPrediction3.class);
        Intent intent = new Intent(context, ColorPrediction3_Socket.class);
        context.startActivity(intent);
    }

    public void openColorPred30(View view) {
        Intent intent = new Intent(context, ColorPrediction30_Socket.class);
        context.startActivity(intent);
    }

    public void openDragonTiger(View view) {
        //    startActivity(new Intent(context, DragonTiger_A.class));
        startActivity(new Intent(context, DragonTigerSocket.class));
    }

    public void openCarRoulette(View view) {
        //      startActivity(new Intent(context, CarRoullete_A.class));
        startActivity(new Intent(context, CarRoullete_Socket.class));
    }

    public void openAnimalRoullete(View view) {
        //   startActivity(new Intent(context, AnimalRoulette_A.class));
        startActivity(new Intent(context, AnimalRoulette_Socket.class));
    }

    public interface itemClick {
        void OnDailyClick(String id);
    }


    private void LoadPullRummyActiveTable(String TAG) {
        RummyActiveTables_BF rummyActiveTables_bf = new RummyActiveTables_BF(TAG);
        rummyActiveTables_bf.show(getSupportFragmentManager(), rummyActiveTables_bf.getTag());
    }

    private void LoadPokerGameActiveTable(String TAG) {
        PokerActiveTables_BF pokerActiveTables_bf = new PokerActiveTables_BF(TAG);
        pokerActiveTables_bf.show(getSupportFragmentManager(), pokerActiveTables_bf.getTag());
    }

    private void LoadDealRummyActiveTable(String TAG) {
        RummyDealActiveTables_BF rummyDealActiveTables_bf = new RummyDealActiveTables_BF(TAG);
        rummyDealActiveTables_bf.show(getSupportFragmentManager(), rummyDealActiveTables_bf.getTag());
    }


    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public String kvalue(int number) {

        String numberString = "";
        if (Math.abs(number / 1000000) > 1) {
            numberString = (number / 1000000) + "m";

        } else if (Math.abs(number / 1000) > 1) {
            numberString = (number / 1000) + "k";

        } else {
            numberString = number + "";

        }
        return numberString;
    }

    @Override
    protected void onStart() {
        super.onStart();

        playSound(R.raw.game_soundtrack, true);
    }

    @Override
    protected void onDestroy() {
        stopPlaying();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlaying();
    }

    Sound soundMedia;

    public void playSound(int sound, boolean isloop) {

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String value = prefs.getString("issoundon", "1");

        if (value.equals("1")) {
            soundMedia.PlaySong(sound, isloop, context);
        } else {
            stopPlaying();
        }


    }

    private void stopPlaying() {
        soundMedia.StopSong();
    }

    private void checkForBanState() {
        String user_state = "";
        if (Variables.latitude > 0 && Variables.longitude > 0) {
            Address address = getAddressFromLatLong(Variables.latitude, Variables.longitude, context);
            if (address != null)
                user_state = address.getAdminArea();
        }

        for (String state : ban_states) {
            if (state.trim().equalsIgnoreCase(user_state)) {
                DialogRestrictUser.getInstance(context).show();
                break;
            }
        }
    }

    public static Address getAddressFromLatLong(double lat, double long_temp, Context context) {

        Address address = null;

        if (lat <= 0 && long_temp <= 0)
            return address;

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, long_temp, 1);
            address = addresses.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            // new GetClass().execute();
        }

        return address;
    }

    private boolean beforeClickPermissionRat;
    private boolean afterClickPermissionRat;

    public void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            beforeClickPermissionRat = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            requestPermissions(Functions.LOCATION_PERMISSIONS, Variables.REQUESTCODE_LOCATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ENABLE_LOCATION_CODE: {
                if (resultCode == RESULT_OK) {

                    storeLatlong();

                } else {
                    finishAffinity();
                }
            }
            break;
        }
    }

    // before after
// FALSE  FALSE  =  Was denied permanently, still denied permanently --> App Settings
// FALSE  TRUE   =  First time deny, not denied permanently yet --> Nothing
// TRUE   FALSE  =  Just been permanently denied --> Changing my caption to "Go to app settings to edit permissions"
// TRUE   TRUE   =  Wasn't denied permanently, still not denied permanently --> Nothing
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Variables.REQUESTCODE_LOCATION:

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            afterClickPermissionRat = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                        if ((!afterClickPermissionRat && !beforeClickPermissionRat)) {
                            // user also CHECKED "never ask again"
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting

//                            showUserClearAppDataDialog();

//                            openMapActivity();
                            finishAffinity();
                            break;
                        } else if ((afterClickPermissionRat && !beforeClickPermissionRat)) {

//                            if(!Functions.isAndroid11())
//                            {
//                                openMapActivity();
//                                break;
//                            }

                        } else {
                            showRationale(permission, R.string.permission_denied_contacts);
                            // user did NOT check "never ask again"
                            // context is a good place to explain the user
                            // why you need the permission and ask if he wants
                            // to accept it (the rationale)
                        }
                    }
                }

                try {

                    if (isPermissionGranted(grantResults)) {
                        enable_permission();
                    } else {

                        if ((afterClickPermissionRat && !beforeClickPermissionRat)
                                || (afterClickPermissionRat && beforeClickPermissionRat)) {
                            EnableGPS();
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    public void EnableGPS() {

        if (Functions.check_location_permissions(context)) {
            if (!GpsUtils.isGPSENABLE(context)) {
                requestLocationAccess();
//                showFragment();
            } else {
                enable_permission();
            }
        } else {
//            showFragment();
            requestLocationAccess();
        }
    }

    public void requestLocationAccess() {

        if (Functions.check_location_permissions(context)) {
            enable_permission();
        } else {
            requestLocationPermissions();
        }
    }


    private void showRationale(String permission, int permission_denied_contacts) {
    }


    private boolean isPermissionGranted(int[] grantResults) {
        boolean isGranted = true;

        for (int result : grantResults) {

            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }

        }

        return isGranted;
    }


    private void storeLatlong() {
        LatLng latLng = getLatLong();
        Variables.latitude = latLng.latitude;
        Variables.longitude = latLng.longitude;

        checkForBanState();
    }

    public LatLng getLatLong() {
        if (getLocationlatlong != null)
            return getLocationlatlong.getLatlong();
        else {
            getLocationlatlong = new GetLocationlatlong(context);
        }

        return getLocationlatlong.getLatlong();
    }

    private void enable_permission() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {

            new GpsUtils(context).turnGPSOn(isGPSEnable -> {

                if (isGPSEnable)
                    enable_permission();

            });
        } else if (Functions.check_location_permissions(context)) {
//            dismissFragment();
            storeLatlong();
        }
    }

    GetLocationlatlong getLocationlatlong;

    private void initilizeLocation() {
        getLocationlatlong = new GetLocationlatlong(context);
    }

    private class SlidingImageAdapter extends PagerAdapter {
        LayoutInflater layoutInflater;

        public SlidingImageAdapter() {
//            layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.slider_images, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            Glide.with(getApplicationContext()).load(Const.banner_img + bannerModelArrayList.get(position).getImg()).into(imageView);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public int getCount() {
            return bannerModelArrayList.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }
    }






}



