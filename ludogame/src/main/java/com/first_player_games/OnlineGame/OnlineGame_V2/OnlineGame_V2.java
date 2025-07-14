package com.first_player_games.OnlineGame.OnlineGame_V2;

import static com.first_player_games.Api.EndPoints.SOCKET_IP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.first_player_games.Api.EndPoints;
import com.first_player_games.Api.LudoApiRepository;
import com.first_player_games.Api.Resource;
import com.first_player_games.Api.RetrofitClient;
import com.first_player_games.BaseActivity;
import com.first_player_games.ModelResponse.LudoWinners;
import com.first_player_games.ModelResponse.UserdataModelResponse;
import com.first_player_games.OnlineGame.Lobby.RoomCreationActivity;
import com.first_player_games.Others.CommonUtils;
import com.first_player_games.Others.Functions;
import com.first_player_games.Others.ProgressPleaseWait;
import com.first_player_games.Others.SharePref;
import com.first_player_games.Others.Sound;
import com.first_player_games.R;
import com.first_player_games.ludoApi.model.LudoViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineGame_V2 extends BaseActivity {
    //private WebSocket webSocket;
    //private String SERVER_PATH = "ws://SERVER-IP-HERE:PORT-NUMBER-HERE";
    DotsView dotview;
    Gamestate state;
    LinearLayout dicecontainer;
    LinearLayout playernamecontainer;
    TextView turnnameview;
    ImageView dice;
    DiceHandler diceHandler;
    EditText cheatview;
    Socket mSocket;
    boolean[] players;
    int playernumber = -1;
    String roomid;
    String table_id, user_id, token;
    Sound soundMedia = new Sound();
    //    DatabaseReference room,action;
    FirebaseFunctions functions;
    MediaPlayer diceSound;
    boolean isChaaldone = true;
    int[] playenameview = new int[]{
            R.id.online_game_playername_red,
            R.id.online_game_playername_yellow,
            R.id.online_game_playername_blue,
            R.id.online_game_playername_green
    };
    ImageView[] dices;
    ImageView arrow;
    String[] names;
    AlertDialog dialogue;
    int duration = 30;
    int count = 50;
    int tempVar = 0;
    CountDownTimer timer;
    int timertime = 6000;
    ProgressDialog dialog;
    int[] turnsSkipped;
    int diamonds = -1;
    boolean gotReward = false;
    int game_id;
    LudoViewModel ludoViewModel;
    LudoApiRepository ludoApiRepository;
    public static final String MY_PREFS_NAME = "Login_data";
    ImageView imgGameReconnect;
    RelativeLayout rltGameReconnect;
    SharedPreferences prefs;
    int counter = 0;
    String winner_name, looser_name, room_code, winner_id;
    boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game__v2);
        socketconnection();
        //initiateSocketConnection();
        ludoApiRepository = LudoApiRepository.getInstance().init(getApplicationContext());
        ludoViewModel = LudoViewModel.getInstance().init(getApplicationContext());
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id = prefs.getString("user_id", "");
        token = prefs.getString("token", "");
        imgGameReconnect = findViewById(R.id.imgGameReconnect);
        rltGameReconnect = findViewById(R.id.rltGameReconnect);
        Glide.with(OnlineGame_V2.this).asGif()
                .load(R.drawable.reconnect).into(imgGameReconnect);
        diamonds = getIntent().getIntExtra("diamonds", -1);

        findViewById(R.id.imgback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        System.out.println("DIAMONDS IN GAME ARE    " + diamonds);
        dotview = findViewById(R.id.ludodotview);
        dicecontainer = findViewById(R.id.dicecontainer);
        cheatview = findViewById(R.id.cheatview);
        diceSound = MediaPlayer.create(OnlineGame_V2.this, R.raw.dice_rolling_effect);
        playernamecontainer = findViewById(R.id.playernameCotainer);
        //    turnnameview = findViewById(R.id.playernameview);
        arrow = findViewById(R.id.arrow_view);
        dialog = ProgressPleaseWait.getDialogue(OnlineGame_V2.this);
        dices = new ImageView[]{
                findViewById(R.id.dice_red),
                findViewById(R.id.dice_yellow),
                findViewById(R.id.dice_blue),
                findViewById(R.id.dice_green)
        };
//        timer = new CountDownTimer(duration*1000,1000) {
//            @Override
//            public void onTick(long l) {
//                ((TextView) findViewById(R.id.timeview)).setText(String.valueOf(count));
//                count--;
//            }
//
//            @Override
//            public void onFinish() {
//                webSocketNextTurn();
//            }
//        };
        turnsSkipped = new int[4];
        players = getIntent().getExtras().getBooleanArray("players");
        names = getIntent().getStringArrayExtra("names");
        for (int i = 0; i < 4; i++)
            if (players[i]) ((TextView) findViewById(playenameview[i])).setText(names[i]);
            else findViewById(playenameview[i]).setVisibility(View.INVISIBLE);
        playernumber = getIntent().getIntExtra("playernumber", -1);
        Log.e("playernumber", "playernumber  =" + playernumber);
        roomid = getIntent().getStringExtra("roomid");
        Log.e("RES_VALUE", "roomid  =" + roomid);
        table_id = getIntent().getStringExtra("table_id");
//        room = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomid);
//        action = room.child("action");
        functions = FirebaseFunctions.getInstance();
        dice = findViewById(R.id.dice);
        if (playernumber != 0)
            dice.setVisibility(View.INVISIBLE);
        if (playernumber == 0)
            arrow.setVisibility(View.VISIBLE);
        showDice(0);
        dotview.post(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                state = new Gamestate(dotview.positionHandler, getApplicationContext(), players) {
                    public void onNextTurn() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showDice(state.turn);
                                setPlayerName(state.turn);
                                if (state.turn == playernumber) dice.setVisibility(View.VISIBLE);
                                if (state.turn == playernumber) arrow.setVisibility(View.VISIBLE);
                                else dice.setVisibility(View.INVISIBLE);
                            }
                        }, 400);
                    }

                    public void onGameWon(int playerwon) {
                        state.won = true;
//                        HashMap<String,String> map = new HashMap<>();
//                        map.put("playernumber","0");
//                        map.put("action_name","game_won");
//                        map.put("action_value",String.valueOf(playerwon));
//                        map.put("action_salt","0");
//                        action.setValue(map);
                        //    ludoApiRepository.call_api_getMakeWinner(SharePref.getU_id(),table_id);
                        webSocketGameWin(playerwon);
                    }

                    public void resetTimer() {
                        OnlineGame_V2.this.restartTimer(tempVar);
                    }

                    public void onMovementFinished() {
                        dotview.setEnabled(false);
                        if (state.turn == playernumber) {
                            arrow.setVisibility(View.VISIBLE);
                        }
                    }
                };
                restartTimer(tempVar);
                dicecontainer.setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(), state.player[playernumber].colorResId));
                setPlayerName(0);
                state.initTurns();
                diceHandler = new DiceHandler(getApplicationContext(), dice, dices, diceSound) {
                    public void onDiceResule() {
                        state.diceRolled(dotview);
                        restartTimer(tempVar);
                        dotview.setEnabled(true);
                        isChaaldone = true;
                        Log.e("RES_VALUE", "DiceHandler: " + isChaaldone);
                    }
                };
                dotview.setGamestate(state);
                state.setDiceHandler(diceHandler);
                // addListnersToFirebase();
            }
        });
        dicecontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!diceHandler.rolled && state.turn == playernumber && count > 1) {
                    resetTimerReduest();
                    diceHandler.diceAnimationInitial.start();
//                    firebaeRollDice();
                    webSocketRollDice();
                }
            }
        });
        ludoApiRepository.call_api_getStartGame().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> stringResource) {
                switch (stringResource.status) {
                    case SUCCESS: {
                        try {
                            JSONObject jsonObject = new JSONObject(stringResource.data);
                            int code = jsonObject.getInt("code");
                            if (code == 200)
                                game_id = jsonObject.getInt("game_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("RES_CHECK", "ON-CONNECT");
                        rltGameReconnect.setVisibility(View.GONE);
                        if (counter > 1) {
                            JSONObject map = new JSONObject();
                            try {
                                map.put("user_id", user_id);
                                map.put("token", token);
                                map.put("counter", counter);
                                mSocket.emit("get-step", map);
                                Log.e("RES_CHECK", "EMIT-getStep");
                                Log.e("RES_VALUE", "getStep" + map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.on("get-step", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String args1 = String.valueOf(args[0]);
                                            Log.e("RES_CHECK ", "ON-getStep");
                                            Log.e("RES_VALUE", "getStep - " + args1);
                                            ///
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(args1.toString());
                                                JSONArray jsonArray = jsonObject.optJSONArray("game_log");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject game_logObject = jsonArray.getJSONObject(i);
                                                    String chaal = game_logObject.getString("chaal");
                                                    Log.e("RES_VALUE", "chaal - " + chaal);
                                                    JSONObject jsonObjectchaal = new JSONObject(chaal);
                                                    Log.e("RES_VALUE", "chaalJsonObject - " + jsonObjectchaal);
                                                    socketOnMessage(jsonObjectchaal);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        mSocket.on("connect_error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rltGameReconnect.setVisibility(View.VISIBLE);
                        Log.v("RES_CHECK ", " connect_error" + args);
                        //Toast.makeText(context, "Socket Connection Error " + args, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Log.e("RES_CHECK","4");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dotview.setOnTouchListener(new DotTouchListner(state.player, players, state) {
                            public void touchDetected(int playernumber, int piecenumber) {
                                dotview.setOnTouchListener(null);
                                if (diceHandler.rolled && state.turn == playernumber && count > 1) {
                                    state.pieceClicked(playernumber, piecenumber, dotview);
                                    webSocketMove(playernumber, piecenumber);
                                    isChaaldone = false;
//                            state.pieceClicked(playernumber, piecenumber);
//                            webSocketMove(piecenumber);
                                }
                                Log.e("RES_VALUE", "dotview: " + isChaaldone);
                            }
                        });
                        String args1 = String.valueOf(args[0]);
                        Log.e("RES_CHECK ", "ON-message ");
                        Log.e("RES_VALUE", "message - " + args1);
                        try {
                            Log.e("SocketListener ", "SocketListener" + args1.toString());
                            JSONObject jsonObject = new JSONObject(args1.toString());
                            socketOnMessage(jsonObject);
                        } catch (JSONException e) {
                            Functions.LOGD("OnlineGame", "" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void socketOnMessage(JSONObject jsonObject) {
        Functions.LOGD("WebSocket", "response : " + jsonObject);
        String gameroomid = jsonObject.optString("roomid", "");
        String gameplayernumber = jsonObject.optString("playernumber", "");
        String steps = jsonObject.optString("steps");
        String action_name = jsonObject.optString("action_name", "");
        String action_value = jsonObject.optString("action_value", "");
        String resettimer = jsonObject.optString("resettimer", "");
        counter = Integer.parseInt(jsonObject.optString("counter", ""));
        if (gameroomid.equalsIgnoreCase(roomid)) {
            Log.e("RES_CHECK", "roomid");
            if (Functions.checkStringisValid(action_name)) {
                Log.e("RES_CHECK", "action_name" + action_name);
                if (!gameplayernumber.equals(String.valueOf(playernumber))) {
                    Log.e("RES_CHECK", "playernumber" + playernumber);
                    if (action_name.equalsIgnoreCase("roll_dice") && !state.won) {
                        int val = Integer.parseInt(steps);
                        if (val >= 1 && val <= 6) {
                            diceHandler.rollDice(val);
                        }
                        turnsSkipped[state.turn] = 0;
                        Log.e("RES_CHECK", "roll_dice");
                    } else if (action_name.equals("move") && !state.won) {
                        int piece = Integer.parseInt(action_value);
                        state.player[state.turn].move(piece, diceHandler.steps, dotview);
                        turnsSkipped[state.turn] = 0;
                        Log.e("RES_CHECK", "move");
                    } else if (action_name.equals("restart")) {
                        Log.e("RES_CHECK", "restart");
                        state.startNewGame();
                        try {
                            dialogue.dismiss();
                        } catch (Exception e) {
                        }
                    } else if (action_name.equals("next_turn") && !state.won) {
                        state.nextTurn();
                        Log.e("RES_CHECK", "next_turn");
                    }
                }
                if (action_name.equals("game_won") && !isShown) {
                    Log.e("RES_CHECK", "game_won");
                    int player = Integer.parseInt(action_value);
                    Log.e("RES_VALUE","players "+player+" "+playernumber);
                    if (playernumber == player) {
                        gameWon(player);
                        isShown = true;
                    }
                    else {

                        Log.e("RES_CHECK", "Lose Game" + playernumber);
                        isShown = true;

                        LinearLayout linerlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_online_game_lost, null);
                        AlertDialog alert = new AlertDialog.Builder(OnlineGame_V2.this).setView(linerlayout).create();
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ((TextView) linerlayout.findViewById(R.id.gamewon_playername)).setText(" You Lost ");
                        linerlayout.findViewById(R.id.game_won_trophy)
                                .setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(), state.player[player].colorResId));
                        linerlayout.findViewById(R.id.exitview).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                finish();
                            }
                        });
                        alert.setCanceledOnTouchOutside(false);
                        alert.setCancelable(false);
                        if (Functions.isActivityExist(OnlineGame_V2.this))
                            alert.show();
                    }
                }
                Log.e("RES_CHECK", "gameWon5");
            }
            if (action_name.equals("leave-table")) {
                Log.e("RES_CHECK", "leave-table");
                getGameStatusData();
                // mSocket.disconnect();
            }
            tempVar++;
            if (!state.won)
                restartTimer(tempVar);
        } else if (Functions.checkisStringValid(resettimer) && resettimer.equals("resettimer")) {
            Log.e("RES_CHECK", "restartTimer");
            restartTimer(tempVar);
        }
    }


    public void socketconnection(){

        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[] {WebSocket.NAME};
            mSocket = IO.socket(SOCKET_IP,opts);
        } catch (URISyntaxException e) {
            Log.e("RES_CHECK" , ""+e);
            throw new RuntimeException(e);
        }
        mSocket.connect();
    }

//    private void webSocketNextTurn() {
//        Log.e("RES_CHECK" , "webSocketNextTurn");
//
//        JSONObject map = new JSONObject();
//        try {
//            map.put("roomid",roomid);
//            map.put("playernumber","-1");
//            map.put("action_name","next_turn");
//            map.put("action_value","-1");
//            map.put("action_salt","-1");
//           // webSocket.send(String.valueOf(map));
//            Log.e("RES_CHECK" , "webSocketNextTurn");
//            Log.e("RES_VALUE" , "webSocketNextTurn"+map);
//            mSocket.emit("message",String.valueOf(map));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void webSocketGameWin(int playerwon) {
        Log.v("WebsocketCall" , "webSocketGameWin");

        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("playernumber","0");
            map.put("action_name","game_won");
            map.put("action_value",String.valueOf(playerwon));
            map.put("action_salt","0");
            map.put("user_id", user_id);
            map.put("token", token);
            map.put("counter", counter);
            // webSocket.send(String.valueOf(map));
            mSocket.emit("message",String.valueOf(map));
            Log.e("RES_CHECK" , "EMIT-webSocketGameWin");
            Log.e("RES_VALUE" , "webSocketGameWin"+map);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void webSocketLeave() {

        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("playernumber","0");
            map.put("action_name","leave-table");
            map.put("user_id", user_id);
            map.put("token", token);
            map.put("counter", counter);
            mSocket.emit("message",String.valueOf(map));
            Log.e("RES_CHECK" , "EMIT-webSocketLeave");
            Log.e("RES_VALUE" , "webSocketLeave"+map);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        if(timer != null)
            timer.cancel();
        super.onDestroy();
    }

    String apponent_id;
    private void getGameStatusData() {
        Log.v("ApiCall " , "getGameStatusData");

        Functions.LOGD("OnlineGame","table_id : "+table_id);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ludoApiRepository.call_api_gameStatus(""+table_id,game_id).observe(OnlineGame_V2.this, new Observer<Resource<UserdataModelResponse>>() {
                    @Override
                    public void onChanged(Resource<UserdataModelResponse> userdataModelResponseResource) {
                        switch (userdataModelResponseResource.status)
                        {
                            case SUCCESS:
                            {
                                UserdataModelResponse userDatum = userdataModelResponseResource.data;
                                if(userDatum.getCode() == 200)
                                {
                                    if(userDatum.getTable_users().size() <= 1 && !gotReward)
                                    {
                                        Log.e("RES_VALUE", "onResponse: "+userdataModelResponseResource.data );
                                        Log.e("RES_VALUE", "onResponse: "+userdataModelResponseResource );
                                        gameWon(playernumber);
                                        Log.e("RES_CHECK","gameWon1");
                                    }
                                }
                                else if(userDatum.getCode() == 407)
                                {
                                    if(!gotReward)
                                    {
                                        gameWon(playernumber);
                                        Log.e("RES_CHECK","gameWon2");
                                    }
                                }
                                else if(userDatum.getCode() == 403)
                                {
                                    if(!gotReward)
                                    {
                                        gameWon(playernumber);
                                    //    webSocketGameWin(playernumber);
                                        Log.e("RES_CHECK","gameWon3");
                                    }
                                }
                                if(userDatum.getTable_users() != null)
                                {
                                    for (UserdataModelResponse.UserDatum userdata :userDatum.getTable_users()) {

                                        if(Functions.checkStringisValid(userdata.getId()) && !userdata.getId().equals(SharePref.getU_id()))
                                        {
                                            apponent_id = userdata.getId();
                                        }

                                    }
                                }

                            }
                        }
                    }
                });
            }
        });
    }


    public void webSocketRollDice(){

        arrow.setVisibility(View.INVISIBLE);
        if(!diceSound.isPlaying())
            diceSound.start();
        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("playernumber",String.valueOf(playernumber));
            map.put("steps",String.valueOf(diceHandler.steps));

            // webSocket.send(String.valueOf(map));
            //mSocket.emit("message",String.valueOf(map));
//            Log.e("RES_CHECK" , "EMIT-webSocketRollDice");
//            Log.e("RES_VALUE" , "webSocketRollDice"+map);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int res = diceHandler.getRan();
//        int res = Integer.parseInt(cheatview.getText().toString());
        diceHandler.rollDice(res);
        diceHandler.rolled = true;

        JSONObject mapparams = new JSONObject();
        try {
            mapparams.put("roomid",roomid);
            mapparams.put("playernumber",String.valueOf(playernumber));
            mapparams.put("steps",String.valueOf(res));
            mapparams.put("action_name","roll_dice");
            mapparams.put("action_salt",String.valueOf(generateSalt()));
            mapparams.put("user_id", user_id);
            mapparams.put("token", token);
            mapparams.put("counter", counter);
            //  webSocket.send(String.valueOf(mapparams));
            mSocket.emit("message",String.valueOf(mapparams));
            Log.e("RES_CHECK" , "EMIT-Chaal");
            Log.e("RES_VALUE" , "EMIT-Chaal"+mapparams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    public void webSocketMove(int playernumber, int piece){
        Log.v("WebsocketCall" , "webSocketMove");

        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("playernumber",playernumber);
            //   map.put("playernumber",String.valueOf(this.playernumber));
            map.put("action_name","move");
            map.put("action_value",String.valueOf(piece));
            map.put("action_salt",String.valueOf(generateSalt()));
            map.put("user_id", user_id);
            map.put("token", token);
            map.put("counter", counter);
            //  webSocket.send(String.valueOf(map));
            mSocket.emit("message",String.valueOf(map));
            Log.e("RES_CHECK" , "EMIT-webSocketMove");
            Log.e("RES_VALUE" , "webSocketMove"+map);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    public void firebaseGameRestartMove(){
//        HashMap<String,String> map = new HashMap<>();
//        map.put("playernumber",String.valueOf(playernumber));
//        map.put("action_name","restart");
//        map.put("action_value","restart");
//        map.put("action_salt",String.valueOf(generateSalt()));
////        action.setValue(map);
//    }

    public int generateSalt(){
        return diceHandler.random.nextInt(100000);
    }

    public void showDice(int num){
        if(num != playernumber)
            dices[num].setVisibility(View.VISIBLE);
        else
            dices[num].setVisibility(View.INVISIBLE);
        for(int i=0;i<dices.length;i++)
            if(i!=num)
                dices[i].setVisibility(View.INVISIBLE);
    }

    public void setPlayerName(int turn){
//        turnnameview.setText(names[turn]+"'s Turn");
//        playernamecontainer
//                .setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(),state.player[turn].colorResId));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.alert_exit_game,null);
        final AlertDialog alert = new AlertDialog.Builder(OnlineGame_V2.this).setView(relativeLayout).create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        relativeLayout.findViewById(R.id.alert_exit_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                ludoApiRepository.call_api_leaveTable();
                webSocketLeave();
                alert.dismiss();
                finish();
            }
        });
        relativeLayout.findViewById(R.id.alert_exit_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
        if(Functions.isActivityExist(OnlineGame_V2.this))
            alert.show();
    }


    public void restartTimer(final int tempVar){

        dialog.dismiss();
        count = duration;
        try {
            if (timer != null){
                timer.cancel();
            }


        }catch (Exception e){}
        timer = new CountDownTimer(duration*2000,1000) {
            @Override
            public void onTick(long l) {
                ((TextView) findViewById(R.id.timeview)).setText(String.valueOf(count));
                if (count < 1) {
                    timerFinished(tempVar);
                    Log.v("RES_CHECK ", "timerFinished6"+count);
                }
                else count--;
            }
            @Override public void onFinish() {
                Log.v("RES_CHECK " , "timerFinished4");
            }
        };
        if(!state.won)
            timer.start();
    }

    public void timerFinished(final int temp){



        if(Functions.isActivityExist(this))
            //dialog.show();

            if(state.turn == playernumber) {
                Log.e("RES_VALUE","players "+state.turn+" "+playernumber);

                dialog.dismiss();
                ludoApiRepository.call_api_leaveTable();
                webSocketLeave();
                Log.v("RES_CHECK " , "timerFinished2");
                finish();
            }
            else {
                Log.v("RES_CHECK " , "timerFinished3");
                gameWon(playernumber);
                //webSocketGameWin(playernumber);
                Log.e("RES_CHECK","gameWon4");
            }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //  dialog.dismiss();
                if(temp == tempVar) {
                    if(turnsSkipped[state.turn] >= 2){
                        players[state.turn] = false;
                    }

                    turnsSkipped[state.turn]++;
                    webSocketTimeFinished();

                    try {
                        timer.cancel();
                    } catch (Exception e) {
                    }

                    if(state.checkWon() != -1 )
                        state.onGameWon(state.checkWon());
                    else if(state.singlePLayerLeft() != -1){
                        state.onGameWon(state.singlePLayerLeft());
                    }
                }

            }
        },2000);

    }




    private void webSocketTimeFinished() {
        Log.v("WebsocketCall" , "webSocketTimeFinished");

        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("playernumber", "-1");
            map.put("action_name", "next_turn");
            map.put("action_value", String.valueOf(state.turn));
            map.put("action_salt", "-1");
            map.put("user_id", user_id);
            map.put("token", token);
            map.put("counter", counter);
            //webSocket.send(String.valueOf(map));
            mSocket.emit("message",String.valueOf(map));
            Log.e("RES_CHECK" , "EMIT-webSocketTimeFinished");
            Log.e("RES_VALUE" , "webSocketTimeFinished"+map);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void gameWon(final int player){

        if(Functions.isActivityExist(OnlineGame_V2.this))
            // dialog.show();
            if(!gotReward) {
                gotReward = true;
                //   ludoApiRepository.call_api_getMakeWinner(String.valueOf(player),table_id);
                //  if(!state.won){
                ludoApiRepository.call_api_getMakeWinner(SharePref.getU_id(),table_id);
                // }
                state.won = true;
                //    CALL_API_Ludowinners();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CALL_API_Ludowinners();
                    }
                }, 2000);

//                ludoApiRepository.call_api_getMakeWinner(SharePref.getU_id(),table_id);

//                if (player == playernumber) {
////This is for creation table win
//
//                    gotReward = true;
//
//                    LinearLayout linerlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_online_game_won, null);
//                    final AlertDialog alert = new AlertDialog.Builder(OnlineGame_V2.this).setView(linerlayout).create();
//                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    ((TextView)linerlayout.findViewById(R.id.gamewon_playername)).setText(names[player]+" Won");
//                    linerlayout.findViewById(R.id.game_won_trophy)
//                            .setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(),state.player[player].colorResId));
//                    linerlayout.findViewById(R.id.claim_diamonds_button).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alert.dismiss();
//                            finish();
//
//                        }
//                    });
//                    alert.setCancelable(true);
//                    alert.setCanceledOnTouchOutside(true);
//                    if(Functions.isActivityExist(OnlineGame_V2.this))
//                        alert.show();
//
//
//
//                } else {
//
//                    LinearLayout linerlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_online_game_lost, null);
//                    AlertDialog alert = new AlertDialog.Builder(OnlineGame_V2.this).setView(linerlayout).create();
//                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    ((TextView)linerlayout.findViewById(R.id.gamewon_playername)).setText(names[player]+" Won");
//                    linerlayout.findViewById(R.id.game_won_trophy)
//                            .setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(),state.player[player].colorResId));
//                    linerlayout.findViewById(R.id.exitview).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alert.dismiss();
//                            finish();
//                        }
//                    });
//                    alert.setCanceledOnTouchOutside(false);
//                    alert.setCancelable(false);
//                    if(Functions.isActivityExist(OnlineGame_V2.this))
//                        alert.show();
//
//                }
            }
    }

    public void resetTimerReduest(){
        webSocketResetTimer();
    }

    private void webSocketResetTimer() {
        Log.v("WebsocketCall" , "webSocketResetTimer");
        counter++;

        JSONObject map = new JSONObject();
        try {
            map.put("roomid",roomid);
            map.put("resettimer", "resettimer");
            map.put("resettimerSalt", String.valueOf(generateSalt()));
            map.put("user_id", user_id);
            map.put("token", token);
            map.put("counter", counter);
            // webSocket.send(String.valueOf(map));
            mSocket.emit("message",String.valueOf(map));
            Log.e("RES_CHECK" , "EMIT-webSocketResetTimer");
            Log.e("RES_VALUE" , "webSocketResetTimer"+map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void playSound(int sound, boolean isloop) {

        soundMedia.PlaySong(sound, isloop, getApplicationContext());

    }

    public void playHomeSound() {
        playSound(R.raw.sound_overlap,false);
    }

    private void CALL_API_Ludowinners() {

        Call<LudoWinners> call = RetrofitClient.getInstance().getApi().ludo_winners(SharePref.getU_id(),table_id);
        Log.v("RES_Winner_Params", ""+SharePref.getU_id()+" "+table_id);

        call.enqueue(new Callback<LudoWinners>() {
            @Override
            public void onResponse(Call<LudoWinners> call, Response<LudoWinners> response) {

                LudoWinners resp = response.body();
                Log.v("RES_addpresponse", String.valueOf(response));
                if (resp != null) {
                    String token = String.valueOf(resp.getCode());
                    Log.e("TOekn", "onResponse: " + token);
                    if (token.equals("200")) {

                        winner_name = resp.getWinner().get(0).getWinner();
                        looser_name = resp.getWinner().get(0).getLost();
                        room_code = resp.getWinner().get(0).getRoomCode();
                        winner_id = resp.getWinner().get(0).getWinner_id();

                        LinearLayout linerlayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_game_winner, null);
                        final AlertDialog alert = new AlertDialog.Builder(OnlineGame_V2.this).setView(linerlayout).create();
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        ((TextView)linerlayout.findViewById(R.id.tv_first_player)).setText(winner_name);
                        ((TextView)linerlayout.findViewById(R.id.tv_second_player)).setText(looser_name);
                        ((TextView)linerlayout.findViewById(R.id.tv_room_code)).setText(room_code);

                        if(winner_id.equals(String.valueOf(SharePref.getU_id()))){
                            ((TextView)linerlayout.findViewById(R.id.tv_congo_header)).setText("Congratualtions");
                        }else {
                            ((TextView)linerlayout.findViewById(R.id.tv_congo_header)).setText("You Lost");
                        }

                        linerlayout.findViewById(R.id.claim_diamonds_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                finish();

                            }
                        });
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(true);
                        if(Functions.isActivityExist(OnlineGame_V2.this))
                            alert.show();

                    } else {
                        Toast.makeText(OnlineGame_V2.this, "" + resp.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<LudoWinners> call, Throwable throwable) {
            }
        });


    }

}