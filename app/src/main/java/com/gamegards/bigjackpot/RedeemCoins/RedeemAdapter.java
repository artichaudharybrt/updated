package com.gamegards.bigjackpot.RedeemCoins;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.gamegards.bigjackpot.Fragments.UserInformation_BT;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.CommonFunctions;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.model.ChipsBuyModel;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.ViewHolder> {
    Activity context;
    private static final String MY_PREFS_NAME = "Login_data";

    ArrayList<ChipsBuyModel> historyModelArrayList;
    ProgressDialog progressDialog;


    public RedeemAdapter(Activity context, ArrayList<ChipsBuyModel> historyModelArrayList) {
        this.context = context;
        this.historyModelArrayList = historyModelArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.redeem_buy_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChipsBuyModel model = historyModelArrayList.get(position);
        progressDialog = new ProgressDialog(context);

        ((ViewHolder) holder).bind(model, position);


    }

    @Override
    public int getItemCount() {

        return historyModelArrayList.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView btnCoinValue, txtWithdrawAmount;
        ImageView imalucky;
        RelativeLayout rel_layout;
        Typeface helvatikabold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imalucky = itemView.findViewById(R.id.imalucky);
            btnCoinValue = itemView.findViewById(R.id.btnCoinValue);
            txtWithdrawAmount = itemView.findViewById(R.id.txtWithdrawAmount);
            rel_layout = itemView.findViewById(R.id.rel_layout);
            helvatikabold = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Helvetica-Bold.ttf");
        }

        public void bind(final ChipsBuyModel model, int position) {
            int val = position % 2;

            String uri2 = "";
            if (val == 1) {
                uri2 = "@drawable/bulkchipsgreen";
            } else {
                uri2 = "@drawable/bulkchipsred";
            }

            uri2 = "@drawable/bulkchipsred";

            int imageResource2 = context.getResources().getIdentifier(uri2,
                    null,
                    context.getPackageName());

            String image_path = model.Image;

            if(!Functions.isStringValid(image_path))
                Picasso.get().load(imageResource2).into(imalucky);
            else
                Picasso.get().load(Const.REDEEM_IMGAE_PATH + image_path).into(imalucky);

            // Extract only the numeric value from getProname()
            String proname = model.getProname();
            if (proname != null && proname.contains("coins")) {
                proname = proname.replace("coins", "").trim();
            }

            // Set only the numeric value in button
            btnCoinValue.setText(proname);
            btnCoinValue.setTextColor(context.getResources().getColor(android.R.color.white));
            btnCoinValue.setTypeface(helvatikabold);

            // Set withdraw amount in format "Withdraw: 200 ₹"
            String amount = model.getAmount();
            if (amount != null && amount.contains("coins")) {
                amount = amount.replace("coins", "").trim();
            }
            txtWithdrawAmount.setText("" + amount + " "+"₹");
            txtWithdrawAmount.setTypeface(helvatikabold);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckUserData(model);
                }
            });

            btnCoinValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckUserData(model);
                }
            });
        }
    }


    public void CheckUserData(final ChipsBuyModel model) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        HashMap<String, String> params = new HashMap<>();
        //  params.put("token", prefs.getString("token", ""));
        params.put("user_id", prefs.getString("user_id", ""));

        progressDialog.show();

        ApiRequest.Call_Api(context, Const.check_adhar, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    final String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (message.equals("1")) {
                        progressDialog.dismiss();
                        getChipsList(model.getId());

                    } else {

                        CommonFunctions.showAlertDialog(context,"Alert message","Please Fill the Profile First",true
                                ,"Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                UserInformation_BT userInformation_bt = new UserInformation_BT(new Callback() {
                                    @Override
                                    public void Responce(String resp, String type, Bundle bundle) {

                                    }
                                });
                                userInformation_bt.show(((RedeemActivity) context).getSupportFragmentManager(),"redeem");
//                                Funtions.DialogUserInfo(context);
                            }
                        }, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        Functions.showToast(context, "Please Fill the Profile First");
                        progressDialog.dismiss();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }
        });
    }
    public void getChipsList(final String id) {

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        HashMap<String, String> params = new HashMap<>();
        params.put("token", prefs.getString("token", ""));
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("redeem_id", id);
        params.put("mobile", prefs.getString("mobile", ""));

        progressDialog.show();

        ApiRequest.Call_Api(context, Const.SEND_USER_REDEEM_DATA, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (code.equalsIgnoreCase("200")) {
                        progressDialog.dismiss();

                        ShowDialogs();
                               /* Funtions.showToast(context, "" + message);
                                context.finish();*/

                    } else {
                         Functions.showToast(context, ""+message);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void ShowDialogs() {


        new SmartDialogBuilder(context)
                .setTitle("Successfully Withdrawn")
                //.setSubTitle("This is the alert dialog to showing alert to user")
                .setCancalable(true)
                //.setTitleFont("Do you want to Logout?") //set title font
                // .setSubTitleFont(subTitleFont) //set sub title font
                .setNegativeButtonHide(true) //hide cancel button
                .setPositiveButton("Ok", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        // Funtions.showToast(context,"Ok button Click",Toast.LENGTH_SHORT)

                        context.finish();

                        smartDialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new SmartDialogClickListener() {
            @Override
            public void onClick(SmartDialog smartDialog) {
                // Funtions.showToast(context,"Cancel button Click");
                smartDialog.dismiss();

            }
        }).build().show();
    }


}

