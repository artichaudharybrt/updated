package com.first_player_games.OnlineGame.Lobby;

import static com.first_player_games.Api.EndPoints.SOCKET_IP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.first_player_games.Api.LudoApiRepository;
import com.first_player_games.Api.Resource;
import com.first_player_games.BaseActivity;
import com.first_player_games.ModelResponse.UserdataModelResponse;
import com.first_player_games.OnlineGame.OnlineGame_V2.OnlineGame_V2;
import com.first_player_games.Others.ProgressPleaseWait;
import com.first_player_games.Others.SharePref;
import com.first_player_games.R;
import com.first_player_games.RandomString;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class JoinRoomSocket extends BaseActivity {

    Socket mSocket;
    private String roomid;
    TextView  tvamount,timer;
    private int timer_interval = 1000;

    ImageView img_dice;
    String boot_value;
    int time_remaining = 50;
    ImageView back_btn;
    LudoApiRepository ludoApiRepository;
    private List<String[]> playersList;

    private int[][] views = new int[][]{
            {R.id.roomcreation_player_1, R.id.roomcreation_player_name_1},
            {R.id.roomcreation_player_2, R.id.roomcreation_player_name_2},
            {R.id.roomcreation_player_3, R.id.roomcreation_player_name_3},
            {R.id.roomcreation_player_4, R.id.roomcreation_player_name_4}
    };

    private ProgressDialog dialog;
    private int diamonds = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_creation);

        ludoApiRepository = LudoApiRepository.getInstance().init(getApplicationContext());

        tvamount =findViewById(R.id.tvamount);
        img_dice =findViewById(R.id.img_dice);
        tvamount =findViewById(R.id.tvamount);
        boot_value=getIntent().getStringExtra("boot_value");
        tvamount.setText(boot_value);

        back_btn =findViewById(R.id.back_btn);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        timer = findViewById(R.id.timer);
        new CountDownTimer((time_remaining * timer_interval),timer_interval) {

            public void onTick(long millisUntilFinished) {
                timer.setText("00:"+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();

        socketconnection();
        playersList = new ArrayList<>();

        dialog = ProgressPleaseWait.getDialogue(this);

        RandomString randomString = new RandomString();
        String randoRoomid = randomString.nextString();
        if(randoRoomid.length() > 0)
        {
            JoinRoomSocket.this.roomid = randoRoomid;
            getTable();
        }

        mSocket.on("trigger", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resp = String.valueOf(args[0]);
                        Log.e("RES_CHECK", "ON-TRIGER "+resp);

                        if (resp.equals("call_status")) {
                            getGameStatusData();
                        }

                    }
                });

            }
        });

        mSocket.on("on-connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("RES_CHECK ", "ON-connect" + args);
                    }
                });

            }
        });

        mSocket.on("get-table", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("RES_CHECK ", "RES_GET_TABLE " + args);
                        // rltGameReconnect.setVisibility(View.GONE);

                    }
                });

            }
        });

    }

    private void getTable() {

        JSONObject map = new JSONObject();
        try {
            map.put("user_id", SharePref.getU_id());
            map.put("token",SharePref.getAuthorization());
            map.put("boot_value",getIntent().getStringExtra("boot_value"));
            map.put("invite_code",roomid);
            map.put("table_id",roomid);

            mSocket.emit("get-table",map);
            Log.e("RES_CHECK" , "EMIT-webSocketLeave");
            Log.e("RES_VALUE" , "webSocketLeave"+map);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void getGameStatusData() {
        Log.v("ApiCall " , "getGameStatusData");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ludoApiRepository.call_api_gameStatus("",-1).observe(JoinRoomSocket.this, new Observer<Resource<UserdataModelResponse>>() {
                    @Override
                    public void onChanged(Resource<UserdataModelResponse> userdataModelResponseResource) {
                        switch (userdataModelResponseResource.status)
                        {
                            case SUCCESS:
                            {
                                UserdataModelResponse userDatum = userdataModelResponseResource.data;
                                if(userDatum.getCode() == 200)
                                {

                                    // Log.e("RES_VALUE" , "RES-" +);
                                    playersList.clear();
                                    for (int i = 0; i < userDatum.getTable_users().size(); i++) {
                                        UserdataModelResponse.UserDatum userData = userDatum.getTable_users().get(i);
                                        roomid = userDatum.getTable().getInviteCode();
                                        String userid = userDatum.getseat_number();
                                        Log.e("RES_VALUE" , "RES-" +userid);
                                        String[] username = new String[2];

                                        username[0] = ""+(i != 0 ? 2 : 0);
                                        username[1] = userData.getName();

                                        playersList.add(username);
                                        Log.e("UserNameList" , userid);
                                        Log.e("UserNameList1" , username[1]);

                                    }

                                    updateUI();
                                    if(playersList.size() >= 2)
                                    {

                                        String seatNumber = userDatum.getseat_number();

                                        if (seatNumber.equals("1")){

                                            gameStartButton(0);
                                        }else {

                                            gameStartButton(2);
                                        }


//                                        for (int i = 0 ; i < playersList.size();i++){
//
//                                        if (playersList.get(i)){
//                                            gameStartButton("0");
//                                        }else {
//                                            gameStartButton("2");
//                                        }

                                        //}



                                    }

                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void updateUI(){
        for(int i=0;i<4;i++){
            if(i<playersList.size()) {
                findViewById(views[i][0]).setVisibility(View.VISIBLE);
                ((TextView) findViewById(views[i][1])).setText(playersList.get(i)[1]);
            }
            else {
                findViewById(views[i][0]).setVisibility(View.INVISIBLE);
            }
        }
    }

    public void gameStartButton(int tempplayerNumber){

        if(playersList.size() > 1)
        {
            //  timerstatus.cancel();
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolean[] players= {true, false, true, false};
                    String[] names = new String[4];
                    int position = 0;
                    for (int i = 0; i < playersList.size(); i++) {
//                        names[i] = null;
                        names[position+i] = playersList.get(i)[1];
                        position++;
                    }
                    dialog.dismiss();
                    Intent intent = new Intent(JoinRoomSocket.this, OnlineGame_V2.class);
                    intent.putExtra("roomid",roomid);
                    intent.putExtra("table_id",roomid);
                    intent.putExtra("playernumber",tempplayerNumber);
                    intent.putExtra("players",players);
                    intent.putExtra("names",names);
                    intent.putExtra("diamonds", diamonds);
                    startActivity(intent);
                    finish();

//                    functions.startGame(roomid);
                }
            },2000);
        }
        else {
            Toast.makeText(this, "Please Wait for Others User.", Toast.LENGTH_SHORT).show();
        }


    }

}
