package com.gamegards.bigjackpot._Poker.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.gamegards.bigjackpot.Activity.Homepage.MY_PREFS_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot._Poker.PokerGame_A;
import com.gamegards.bigjackpot.model.TableModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
//import com.paytm.pgsdk.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PokerActiveTables_BF extends BottomSheetDialogFragment {


    public PokerActiveTables_BF() {
        // Required empty public constructor
    }

    ArrayList<TableModel> usermodelsList = new ArrayList<>();

    String game_tag = "";
    public PokerActiveTables_BF(String tag) {
        this.game_tag = tag;
        // Required empty public constructor
    }

    public interface StickerListener {
        void onStickerClick(String bitmap, String ammount);
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
    Activity context;
    UserPointAdapter adapter;
    public static String SEL_TABLE = "sel_table";
    public static String VIEWER_OPEN = "Viewer open";
    public static String VIEWER_CLOSE = "Viewer close";

    public static String PUBLIC_GAME = "public game";
    public static String PRIVATE_GAME = "private game";
    int tabposition  = 0;

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
               //setupFullHeight(bottomSheetDialog);

            }
        });
        return  dialog;
    }

    /*@SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_poker_active_table, null);
        dialog.setContentView(contentView);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

       // dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        context = getActivity();

        contentView.findViewById(R.id.imgclosetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView rec_user_points = contentView.findViewById(R.id.rec_user_points);
        rec_user_points.setLayoutManager(new LinearLayoutManager(context));
        adapter = new UserPointAdapter(this,usermodelsList);
        rec_user_points.setAdapter(adapter);


        contentView.findViewById(R.id.rltParentView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contentView.findViewById(R.id.lnrCreateTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Dialog_CreateTable();
            }
        });

        TabLayout tabLayout = contentView.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("New Games"));
        tabLayout.addTab(tabLayout.newTab().setText("Private Games"));
        tabLayout.addTab(tabLayout.newTab().setText("OnGoing Games"));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabposition = tab.getPosition();
                CALL_API_getTableList();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        CALL_API_getTableList();
    }*/



    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        // Inflate your custom layout
        contentView = View.inflate(getContext(), R.layout.fragment_poker_active_table, null);
        dialog.setContentView(contentView);

        // Ensure the content layout is wrapped in a CoordinatorLayout
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        // If the behavior is BottomSheetBehavior, apply it
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        // Set system UI visibility to immersive mode (optional)
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Get the context for further use
        context = getActivity();

        // Set close button functionality
        contentView.findViewById(R.id.imgclosetop).setOnClickListener(v -> dismiss());

        // RecyclerView setup for user points
        RecyclerView rec_user_points = contentView.findViewById(R.id.rec_user_points);
        rec_user_points.setLayoutManager(new LinearLayoutManager(context));
        adapter = new UserPointAdapter(this, usermodelsList);
        rec_user_points.setAdapter(adapter);

        // Dismiss when clicking outside certain areas
        contentView.findViewById(R.id.rltParentView).setOnClickListener(v -> dismiss());

        // Set up Create Table button (currently commented)
        contentView.findViewById(R.id.lnrCreateTable).setOnClickListener(v -> {
            // Uncomment and implement if needed
            // Dialog_CreateTable();
        });

        // Set up TabLayout for New Games, Private Games, OnGoing Games
        TabLayout tabLayout = contentView.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("New Games"));
        tabLayout.addTab(tabLayout.newTab().setText("Private Games"));
        tabLayout.addTab(tabLayout.newTab().setText("OnGoing Games"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabposition = tab.getPosition();
                CALL_API_getTableList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });

        // Call the API to get the table list
        CALL_API_getTableList();

        // Adjust bottom sheet height to full screen
        setupFullHeight(dialog);
    }

    private void setupFullHeight(Dialog dialog) {
        // Get the root view of the dialog, which should be a CoordinatorLayout
        View bottomSheetView = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottomSheetView != null) {
            // Get the BottomSheetBehavior from the bottom sheet view
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetView);

            // Set the bottom sheet to fully expand
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // Set the bottom sheet's height to the screen's height
            ViewGroup.LayoutParams layoutParams = bottomSheetView.getLayoutParams();
            int windowHeight = getWindowHeight();
            layoutParams.height = windowHeight;  // Set to full screen height
            bottomSheetView.setLayoutParams(layoutParams);
        }
    }













    public void VerifyPassword(TableModel tableModel) {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_forget_password);
        dialog.setTitle("Title...");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView imgclose = (ImageView) dialog.findViewById(R.id.imgclose);
        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        tv_heading.setText("Verify Password");
        EditText edit_OTP = (EditText) dialog.findViewById(R.id.edit_OTP);
        edit_OTP.setTextColor(Functions.getColor(context,R.color.white));
        edit_OTP.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_OTP.setHint("Enter password");

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        View imglogin = dialog.findViewById(R.id.imglogin);

        imglogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_OTP.getText().toString().length() > 0) {

                    if(tableModel.getPassword().equalsIgnoreCase(Functions.getStringFromEdit(edit_OTP)))
                    {
                        dialog.dismiss();
                        Functions.showToast(context,"Password confirm!");
                        Bundle params = new Bundle();
                        params.putSerializable("tableModel", (Serializable) tableModel);
                        openPockerGame(params);
                    }
                    else {
                        Functions.showToast(context,"Password not match!");
                    }

                } else {
                    Toast.makeText(context, "Please Enter Code",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        Functions.setDialogParams(dialog);

    }

    private void openPockerGame(Bundle bundle){
        Intent intent = new Intent(context, PokerGame_A.class);
        if(bundle != null)
            intent.putExtras(bundle);

        if(context != null && !context.isFinishing())
            context.startActivity(intent);
    }



    SharedPreferences prefs;
    private void CALL_API_getTableList() {

        HashMap<String, String> params = new HashMap<String, String>();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        String URL = "";
        URL = Const.poker_get_table_master;

        ApiRequest.Call_Api(context, URL, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                if(resp != null)
                {
                    ParseResponse(resp);
                    Log.v("Res_response","Res_Check"+resp);
                }

            }
        });

        getTexView(contentView,R.id.txtheader).setText(getString(R.string.poker_game));

    }

    private void ParseResponse(String resp) {
        usermodelsList.clear();
        try {
            JSONObject jsonObject = new JSONObject(resp);
            Log.v("Res_Response","Res_Check"+resp);

            int code = jsonObject.optInt("code",404);
            String message = jsonObject.optString("message","Something went wrong");

            if(code == 205  || message.equals("You are Already On Table"))
            {

                JSONObject tableObject = jsonObject.getJSONArray("table_data").getJSONObject(0);
                TableModel tableModel = new TableModel();
                tableModel.setId(tableObject.getString("poker_table_id"));
                Bundle bundle = new Bundle();
                bundle.putString("table_id",tableModel.getId());

                openPockerGame(bundle);

                dismiss();
                return;
            }

            if (code == 200)
            {

                JSONArray tableArray = jsonObject.getJSONArray("table_data");

                for (int i = 0; i < tableArray.length() ; i++) {
                    JSONObject tableObject = tableArray.getJSONObject(i);
                    TableModel model = new TableModel();
                    model.setId(tableObject.optString("id",""));
                    model.setBootValue(tableObject.optString("boot_value","0"));
                    model.setMaximumBlind(tableObject.optString("maximum_blind","0"));
                    model.setChaalLimit(tableObject.optString("chaal_limit","0"));
                    model.setBlind(tableObject.optString("blind_1","0"));
                    model.setPotLimit(tableObject.optString("pot_limit", "0"));
                    model.setOnlineMembers(tableObject.optString("online_members","0"));
                    model.setPoint_value(tableObject.optString("point_value","0"));
                    model.setMax_player(tableObject.optString("max_player","0"));
                    model.setTable_name("Poker-Game");


                    float poin_value = Float.parseFloat(model.getPoint_value());
                    model.setChaalLimit(""+(poin_value * 100));
                    model.setPotLimit("5");


                    int tabtype = tableObject.optInt("private",0);
                    int ongoing = tableObject.optInt("ongoing");

                    model.setOngoin(ongoing);

                    if((tabtype ==tabposition && ongoing == 0) || (tabposition == 2 && ongoing == 1))
                        usermodelsList.add(model);
                }


            }
            else {
                Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
            }

            adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private TextView getTexView(View view, int id) {

        return ((TextView) view.findViewById(id));
    }

    private View getView(View view, int id) {

        return ((View) view.findViewById(id));
    }



    public class UserPointAdapter extends RecyclerView.Adapter<UserPointAdapter.ViewHolder> {

        ArrayList<TableModel> arrayList;
        PokerActiveTables_BF giftBSFragment;

        public UserPointAdapter(PokerActiveTables_BF coupansFragment, ArrayList<TableModel> arrayList) {
            this.arrayList = arrayList;
            this.giftBSFragment = coupansFragment;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rummy_active_table, parent, false);

//            int height = parent.getMeasuredHeight() / 4;
//            int width = parent.getMeasuredWidth();

//            view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            View view = holder.itemView;
            final TableModel usermodel = arrayList.get(position);

            View lnrListingbg= view.findViewById(R.id.lnrListingbg);

//            if(position % 2== 0)
//            {
//                lnrListingbg.setBackgroundColor(context.getResources().getColor(R.color.table_list_color));
//            }
//            else {
//                lnrListingbg.setBackgroundColor(context.getResources().getColor(R.color.table_list_color2));
//            }

            getTexView(view,R.id.tvValue1).setText(""+usermodel.getId());
            getTexView(view,R.id.tvValue2).setText(""+usermodel.getTable_name());
            getTexView(view,R.id.tvValue3).setText(""+usermodel.getBootValue());
            getTexView(view,R.id.tvValue4).setText(""+usermodel.getMax_player());
            getTexView(view,R.id.tvValue5).setText(""+usermodel.getOnlineMembers());
            getTexView(view,R.id.tvValue6).setText(""+usermodel.getTableType());
            getTexView(view,R.id.tvValue7).setText(""+usermodel.getViewer_status());

            if(usermodel.getOngoin() == 1)
                getTexView(view,R.id.tvValue8).setText(""+getString(R.string.view_game));

            getTexView(view,R.id.tvValue8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float min_try = 0;

                    min_try = Float.parseFloat(usermodel.getBootValue());
                    float wallet_amount = Float.parseFloat(prefs.getString("wallet","0"));

                    if(min_try > wallet_amount)
                    {
                        Log.d("PrivateTable", "Insufficient balance in Poker private table - showing add cash dialog");
                        Functions.SmartAlertDialogWithAddCash(context,
                            "Insufficient Balance",
                            "You need ₹" + min_try + " to join this table. Your current balance is ₹" + wallet_amount + ".",
                            new Callback() {
                                @Override
                                public void Responce(String resp, String type, Bundle bundle) {
                                    Log.d("PrivateTable", "Add cash dialog response: " + resp);
                                }
                            });
                        return;
                    }

                    dismiss();


                    if(usermodel.getTableType().equalsIgnoreCase(PRIVATE_GAME))
                    {
                        VerifyPassword(usermodel);
                    }
                    else{
                        Intent intent = null;


                        Bundle bundle = new Bundle();
                        bundle.putString("table_id",""+usermodel.getId());
                        bundle.putInt("ongoing",usermodel.getOngoin());
                        bundle.putString("min_entry",""+min_try);
                        bundle.putSerializable("tableModel", (Serializable) usermodel);
                        bundle.putString(SEL_TABLE,usermodel.getBootValue());
                        bundle.putString("blind_1", usermodel.getBlind());
                        openPockerGame(bundle);


                        if(intent != null)
                        {
                            intent.putExtra(SEL_TABLE,usermodel.getBootValue());

                            startActivity(intent);
                        }

                    }

                }
            });

        }

        private TextView getTexView(View view, int id) {

            return ((TextView) view.findViewById(id));
        }

        private View getView(View view, int id) {

            return ((View) view.findViewById(id));
        }


        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(View itemView) {
                super(itemView);

            }


        }
    }

/*
    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
*/

    public void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        // Inflate your custom bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet2, null);
        bottomSheetDialog.setContentView(bottomSheetView);  // Set the content view

        // Now you can find the bottom sheet container (design_bottom_sheet) from the inflated view
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        FrameLayout bottomSheet = bottomSheetView.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Get the window's height for full screen usage
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }
   /* private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }*/


}