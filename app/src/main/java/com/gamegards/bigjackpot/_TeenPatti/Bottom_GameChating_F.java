package com.gamegards.bigjackpot._TeenPatti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.gamegards.bigjackpot.Adapter.MessageAdapter;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.model.Chats;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Bottom_GameChating_F extends BottomSheetDialogFragment {


    public Bottom_GameChating_F() {
        // Required empty public constructor
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    View contentView;

    EditText edtText;
    ImageView btnSendMessage;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    private static final String MY_PREFS_NAME = "Login_data";
    String Gameid,profile_pic;
    List<Chats> chatsArrayList;
    Activity context;

    public static Bottom_GameChating_F newInstence(String gameid, String profile_pic){
        Bottom_GameChating_F bottom_gameChating_f = new Bottom_GameChating_F();
        Bundle params = new Bundle();
        params.putString("gameid",gameid);
        params.putString("profile_pic",profile_pic);
        bottom_gameChating_f.setArguments(params);
        return bottom_gameChating_f;
    }

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.activity_game_chat, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
//        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        context = getActivity();

        Intilization();

    }

    private void Intilization(){
        recyclerView = contentView.findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(context,RecyclerView.VERTICAL,true);
        linearLayoutManager.setStackFromEnd(true); // Recent messages at bottom
        recyclerView.setLayoutManager(linearLayoutManager);
        chatsArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(context,chatsArrayList);
        recyclerView.setAdapter(messageAdapter);

        if(getArguments() != null)
        {
            Gameid = getArguments().getString("gameid");
            profile_pic = getArguments().getString("profile_pic");
        }

        edtText = contentView.findViewById(R.id.edtText);
        btnSendMessage = contentView.findViewById(R.id.btnSendMessage);

        // Handle "Send" action from keyboard
        edtText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                    String message = edtText.getText().toString().trim();
                    if(!message.equals("")) {
                        ChatListinAndSendMessages(message);
                        // Auto-scroll to bottom after sending message (position 0 in reverse layout)
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (chatsArrayList.size() > 0) {
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }
                        });
                    }
                    edtText.setText("");
                    return true;
                }
                return false;
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = edtText.getText().toString().trim();

                if(!message.equals(""))
                {
                    ChatListinAndSendMessages(message);
                    // Auto-scroll to bottom after sending message (position 0 in reverse layout)
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (chatsArrayList.size() > 0) {
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    });
                }
                edtText.setText("");

            }
        });

        ((ImageView)contentView.findViewById(R.id.imgclose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    Handler handler;
    Runnable runnable;

    @Override
    public void onResume() {
        super.onResume();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {


                ChatListinAndSendMessages("");


                handler.postDelayed(this, 10000);


            }
        };
        handler.postDelayed(runnable, 0) ;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(handler != null)
            handler.removeCallbacks(runnable);

    }

    private void ChatListinAndSendMessages(final String message){

        chatsArrayList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.GAME_CHATS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equalsIgnoreCase("200")) {

                                JSONArray ListArray = jsonObject.getJSONArray("list");

                                for (int i = 0; i < ListArray.length() ; i++) {
                                    JSONObject jsonObject2 = ListArray.getJSONObject(i);
                                    Chats  chatsmodel = new Chats();

                                    chatsmodel.setMessage(jsonObject2.getString("chat"));
                                    chatsmodel.setId(jsonObject2.getString("id"));
                                    chatsmodel.setUser_id(jsonObject2.getString("user_id"));
                                    chatsmodel.setSender(jsonObject2.getString("user_id"));
                                    chatsmodel.setSenderImage(profile_pic);
                                    chatsmodel.setMessage(jsonObject2.getString("chat"));

                                    chatsArrayList.add(chatsmodel);
                                }

//                                Collections.reverse(chatsArrayList);
                                messageAdapter.notifyDataSetChanged();

                                // Auto-scroll to latest message (position 0 in reverse layout = most recent)
                                if (chatsArrayList.size() > 0) {
                                    recyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                    });
                                }
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
                params.put("game_id", Gameid);
                if(message != null)
                    params.put("chat",message);
                else
                    params.put("chat","");
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







    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        try {
            FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

                int windowHeight = getWindowHeight();
                if (layoutParams != null) {
                    layoutParams.height = (int) (windowHeight * 0.85); // Use 85% of screen height
                }
                bottomSheet.setLayoutParams(layoutParams);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


}
