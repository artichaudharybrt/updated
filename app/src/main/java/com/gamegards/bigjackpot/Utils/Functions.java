package com.gamegards.bigjackpot.Utils;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamegards.bigjackpot.Adapter.GiftsAdapter;
import com.gamegards.bigjackpot.Comman.CommonAPI;
import com.gamegards.bigjackpot.Comman.ProgressbarManager;
import com.gamegards.bigjackpot.GameApplication;
import com.gamegards.bigjackpot.Helper.PaymentGatewayHelper;
import com.gamegards.bigjackpot.Interface.ApiRequest;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.Interface.itemClick;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.ApiClasses.CommonFunctions;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.ApiClasses.Dealer;

import com.gamegards.bigjackpot.model.GiftModel;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class Functions {

    public static int getDimens(int dimens){
        return (int) GameApplication.appLevelContext.getResources().getDimension(dimens);
    }

    public static String formateAmount(String amount){

        try {
            Float _amount = Float.parseFloat(amount);
            return NumberFormat.getNumberInstance(Locale.US).format(_amount);
        }
        catch (Exception e)
        {
            return amount;
        }

    }

    public static Dialog DialogInstance(Context context){
        return new Dialog(context);
    }

    public static void setDialogParams(Dialog mdialog){
        if(mdialog == null)
            return;

        mdialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Window window = mdialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Enable outside touch to dismiss dialog
        mdialog.setCanceledOnTouchOutside(true);

    }

    public static boolean isActivityExist(Context context){

        return context != null && !((Activity)context).isFinishing() ? true : false;
    }

    public static ProgressbarManager progressbarManager;
    public static void showLoader(Context context,boolean cancelable,boolean touchable){
        if(progressbarManager == null)
            progressbarManager = new ProgressbarManager(context);

        progressbarManager.setCancelable(cancelable);
        progressbarManager.setCanceledOnTouchOutside(touchable);

        try {
            if(progressbarManager != null)
                progressbarManager.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void dismissLoader(){
        try {
            if(progressbarManager != null)
                progressbarManager.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    public static void Dialog_CancelAppointment(Activity context, String title , String second_text, final Callback callback) {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dailog_exit_layout);


        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.Responce("yes","",null);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.Responce("no","",null);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    public static void Dialog_EnterTableId(Activity context, String title , String second_text, final Callback callback) {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_enter_tableid);

        EditText edtTableId = dialog.findViewById(R.id.edtTableId);
        ImageView imgJoinTable = dialog.findViewById(R.id.imgJoinTable);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        View dialogRootLayout = dialog.findViewById(R.id.dialog_root_layout);
        View dialogContentLayout = dialog.findViewById(R.id.dialog_content_layout);
//        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.Responce("yes","",null);
//                dialog.dismiss();
//            }
//        });
//
//        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.Responce("no","",null);
//                dialog.dismiss();
//            }
//        });

        imgJoinTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.Responce(edtTableId.getText().toString(),"",null);
                dialog.dismiss();
            }
        });

        // Close button click listener
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Root layout touch listener for outside touch dismissal
        dialogRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Check if touch is outside the content area
                    int[] contentLocation = new int[2];
                    dialogContentLayout.getLocationOnScreen(contentLocation);

                    float touchX = event.getRawX();
                    float touchY = event.getRawY();

                    if (touchX < contentLocation[0] ||
                            touchX > contentLocation[0] + dialogContentLayout.getWidth() ||
                            touchY < contentLocation[1] ||
                            touchY > contentLocation[1] + dialogContentLayout.getHeight()) {
                        dialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });


        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public static void Dialog_CancelAppointmentNew(Activity context, String title, String second_text, final Callback callback) {
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_alertuidesgin);

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);
        TextView tv_subheading = dialog.findViewById(R.id.tv_subheading);

        // HTML anchor tag in the first_text and second_text
        String first_text = "<a href='https://t.me/Pk106008'>Click here</a> to contact us. ";

        // Set the text with HTML formatting
        tv_heading.setText(Html.fromHtml(title));
        tv_subheading.setText(Html.fromHtml(first_text + second_text));

        // Enable links to be clickable
        tv_subheading.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.findViewById(R.id.btn_yes).setVisibility(View.GONE);
        dialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.Responce("yes", "", null);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_no).setVisibility(View.GONE);
        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.Responce("no", "", null);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static Drawable getDrawable(Context context, int drawable) {

        return context.getResources().getDrawable(drawable);
    }

    public static String getString(Context context,int string) {

        return context.getResources().getString(string);
    }


    public static int getColor(Context context,int color) {

        return GameApplication.appLevelContext.getResources().getColor(color);
    }

    public static String getStringFromEdit(EditText editText) {

        return editText.getText().toString().trim();
    }

    public static boolean showToast(Context context,String message) {
        Toast.makeText(context,""+message,Toast.LENGTH_SHORT).show();
        return false;
    }

    public static String makeFistLaterCaptial(String text) {

        if (text.length() > 0) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();

            return text;
        }

        return "";
    }

    public static String convertnulltoZero(String tag)
    {

        if(tag != null && !tag.trim().equals("")&& !tag.trim().equalsIgnoreCase("null") && !tag.trim().equalsIgnoreCase("0"))
            return tag;

        return "0";
    }

    public static boolean checkisStringValid(String tag)
    {

        if(tag != null && !tag.trim().equals("")&& !tag.trim().equalsIgnoreCase("null") && !tag.trim().equalsIgnoreCase("0"))
            return true;

        return false;
    }

    public static boolean isStringValid(String tag)
    {

        if(tag != null && !tag.trim().equals("")&& !tag.trim().equalsIgnoreCase("null"))
            return true;

        return false;
    }


    private static final String MY_PREFS_NAME = "Login_data";

    private static final boolean isDebug = true;

    public static final int ANIMATION_SPEED = 1000;
    public static final int Home_Page_Animation = 500;

    public static boolean checkStringisValid(String text) {

        if (text != null && !text.equals("") && !text.equals("null") && !text.equals("0")) {
            return true;
        }

        return false;
    }


    public static String inviteTableLink(Context context,String table_id,String table_name){

        final String appPackageName = context.getPackageName();
        String applink = Variables.invite_link;

        if(applink.contains("google"))
            applink = applink + appPackageName;

        String refferal_link = SharePref.getInstance().getString(SharePref.referral_link);
        if(Functions.checkisStringValid(refferal_link))
            applink = refferal_link;

        String app_name = context.getString(R.string.app_name);
        String deep_link_url = ""+context.getString(R.string.deep_link_url);

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String referal_code = prefs.getString("referal_code", "");

        String strshareMessage = context.getString(R.string.share_message);

        String shareMessage = strshareMessage
                +" Use the referral code  " +
                referal_code + " Download the App now. Link:-"
                + applink + " . To join table use this link :- " +
                "https://"+deep_link_url+"/"+table_name+"?table_id=" + table_id;

        return shareMessage;
    }

    public static CountDownTimer onUserCountDownTimer(Context context, int MaxTime, int Interval, final Callback callback){

        CountDownTimer countDownTimer = new CountDownTimer(MaxTime,Interval) {
            @Override
            public void onTick(long millisUntilFinished) {

                callback.Responce("onTick","",null);

            }

            @Override
            public void onFinish() {

                callback.Responce("onFinish","",null);

            }
        };

        return countDownTimer;
    }

    public static Animation AnimationListner(Context context,int url_animation ,final Callback callback){

        Animation animation =  AnimationUtils.loadAnimation(context,
                url_animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callback.Responce("end","",null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return animation;
    }


    public static int getCardResource(String imagename) {

        int cardId = 0;

        switch(imagename.toLowerCase()) {
            case "bla":
                cardId = R.drawable.bla;
                break;
            case "bl2":
                cardId = R.drawable.bl2;
                break;
            case "bl3":
                cardId = R.drawable.bl3;
                break;
            case "bl4":
                cardId = R.drawable.bl4;
                break;
            case "bl5":
                cardId = R.drawable.bl5;
                break;
            case "bl6":
                cardId = R.drawable.bl6;
                break;
            case "bl7":
                cardId = R.drawable.bl7;
                break;
            case "bl8":
                cardId = R.drawable.bl8;
                break;
            case "bl9":
                cardId = R.drawable.bl9;
                break;
            case "bl10":
                cardId = R.drawable.bl10;
                break;
            case "blj":
                cardId = R.drawable.blj;
                break;
            case "blq":
                cardId = R.drawable.blq;
                break;
            case "blk":
                cardId = R.drawable.blk;
                break;

            case "bpa":
                cardId = R.drawable.bpa;
                break;
            case "bp2":
                cardId = R.drawable.bp2;
                break;
            case "bp3":
                cardId = R.drawable.bp3;
                break;
            case "bp4":
                cardId = R.drawable.bp4;
                break;
            case "bp5":
                cardId = R.drawable.bp5;
                break;
            case "bp6":
                cardId = R.drawable.bp6;
                break;
            case "bp7":
                cardId = R.drawable.bp7;
                break;
            case "bp8":
                cardId = R.drawable.bp8;
                break;
            case "bp9":
                cardId = R.drawable.bp9;
                break;
            case "bp10":
                cardId = R.drawable.bp10;
                break;
            case "bpj":
                cardId = R.drawable.bpj;
                break;
            case "bpq":
                cardId = R.drawable.bpq;
                break;
            case "bpk":
                cardId = R.drawable.bpk;
                break;

            case "rsa":
                cardId = R.drawable.rsa;
                break;
            case "rs2":
                cardId = R.drawable.rs2;
                break;
            case "rs3":
                cardId = R.drawable.rs3;
                break;
            case "rs4":
                cardId = R.drawable.rs4;
                break;
            case "rs5":
                cardId = R.drawable.rs5;
                break;
            case "rs6":
                cardId = R.drawable.rs6;
                break;
            case "rs7":
                cardId = R.drawable.rs7;
                break;
            case "rs8":
                cardId = R.drawable.rs8;
                break;
            case "rs9":
                cardId = R.drawable.rs9;
                break;
            case "rs10":
                cardId = R.drawable.rs10;
                break;
            case "rsj":
                cardId = R.drawable.rsj;
                break;
            case "rsq":
                cardId = R.drawable.rsq;
                break;
            case "rsk":
                cardId = R.drawable.rsk;
                break;

            case "rpa":
                cardId = R.drawable.rpa;
                break;
            case "rp2":
                cardId = R.drawable.rp2;
                break;
            case "rp3":
                cardId = R.drawable.rp3;
                break;
            case "rp4":
                cardId = R.drawable.rp4;
                break;
            case "rp5":
                cardId = R.drawable.rp5;
                break;
            case "rp6":
                cardId = R.drawable.rp6;
                break;
            case "rp7":
                cardId = R.drawable.rp7;
                break;
            case "rp8":
                cardId = R.drawable.rp8;
                break;
            case "rp9":
                cardId = R.drawable.rp9;
                break;
            case "rp10":
                cardId = R.drawable.rp10;
                break;
            case "rpj":
                cardId = R.drawable.rpj;
                break;
            case "rpq":
                cardId = R.drawable.rpq;
                break;
            case "rpk":
                cardId = R.drawable.rpk;
                break;

            default:
                cardId = R.drawable.backside_card;
                break;
        }

        return cardId;

    }


    public static int GetResourcePath(String imagename,Context context){

        String uri1 = "@drawable/" + imagename.toLowerCase();  // where myresource " +
        int imageResource = context.getResources().getIdentifier(uri1, null,
                context.getPackageName());

        return imageResource;
    }

    public static void SetBackgroundImageAsDisplaySize(Activity context, RelativeLayout relativeLayout,int drawable){

        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(),drawable),size.x,size.y,true);

        ImageView imageview = new ImageView(context);
        RelativeLayout relativelayout = relativeLayout;
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Add image path from drawable folder.
        imageview.setImageBitmap(bmp);
        imageview.setLayoutParams(params);

        if(relativeLayout != null)
            relativelayout.addView(imageview);

    }

    public static AnimatorSet getViewToViewScalingAnimator(final RelativeLayout parentView,
                                                           final View viewToAnimate,
                                                           final Rect fromViewRect,
                                                           final Rect toViewRect,
                                                           final long duration,
                                                           final long startDelay) {
        // get all coordinates at once
        final Rect parentViewRect = new Rect(), viewToAnimateRect = new Rect();
        parentView.getGlobalVisibleRect(parentViewRect);
        viewToAnimate.getGlobalVisibleRect(viewToAnimateRect);

        viewToAnimate.setScaleX(1f);
        viewToAnimate.setScaleY(1f);

        // rescaling of the object on X-axis
        final ValueAnimator valueAnimatorWidth = ValueAnimator.ofInt(fromViewRect.width(), toViewRect.width());
        valueAnimatorWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Get animated width value update
                int newWidth = (int) valueAnimatorWidth.getAnimatedValue();

                // Get and update LayoutParams of the animated view
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewToAnimate.getLayoutParams();

                lp.width = newWidth;
                viewToAnimate.setLayoutParams(lp);
            }
        });

        // rescaling of the object on Y-axis
        final ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(fromViewRect.height(), toViewRect.height());
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Get animated width value update
                int newHeight = (int) valueAnimatorHeight.getAnimatedValue();

                // Get and update LayoutParams of the animated view
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewToAnimate.getLayoutParams();
                lp.height = newHeight;
                viewToAnimate.setLayoutParams(lp);
            }
        });

        // moving of the object on X-axis
        ObjectAnimator translateAnimatorX = ObjectAnimator.ofFloat(viewToAnimate, "X", fromViewRect.left - parentViewRect.left, toViewRect.left - parentViewRect.left);

        // moving of the object on Y-axis
        ObjectAnimator translateAnimatorY = ObjectAnimator.ofFloat(viewToAnimate, "Y", fromViewRect.top - parentViewRect.top, toViewRect.top - parentViewRect.top);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(1f));
        animatorSet.setDuration(duration); // can be decoupled for each animator separately
        animatorSet.setStartDelay(startDelay); // can be decoupled for each animator separately
        animatorSet.playTogether(valueAnimatorWidth, valueAnimatorHeight, translateAnimatorX, translateAnimatorY);

        return animatorSet;
    }

    public static void LOGE(String classname, String message) {

        if (!Variables.IS_LOG_ENABLE)
            return;

        try {
            if (message.length() > 4000) {

                Log.e("" + classname, "" + message.substring(0, 4000));

                LOGE(classname, message.substring(4000));
            } else
                Log.e("" + classname, "" + message);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public static void LOGD(String classname, String message) {

        if (!Variables.IS_LOG_ENABLE)
            return;

        try {
            if (message.length() > 4000) {

                Log.e("" + classname, "" + message.substring(0, 4000));

                LOGD(classname, message.substring(4000));
            } else
                Log.d("" + classname, "" + message);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }
    public static void showTipsDialog(final Context context, final Dealer dealer, final ImageView imgampire, final Callback callback) {
        // custom dialog
        final int[] tips = {100};

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_sendtips);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ((ImageView) dialog.findViewById(R.id.imgclosetop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final TextView txtTips = dialog.findViewById(R.id.txtTips);
        ((TextView) dialog.findViewById(R.id.txtheader)).setText("DEALER");

        txtTips.setText("TIPS: "+Variables.CURRENCY_SYMBOL+dealer.tips+" CHIPS");

        final TextView txttime = dialog.findViewById(R.id.txttime);

        txttime.setText("Dealer since "+ TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - dealer.timeStamp)+" min ago");
        final ImageView imgperson = dialog.findViewById(R.id.imgperson);
        imgperson.setImageDrawable(context.getDrawable(Dealer.dealerImages[dealer.currentDealerPos]));


        dialog.findViewById(R.id.btn_change_dealer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealer.showDialog(context, 3, new Dealer.CallBack() {
                    @Override
                    public void onDealerChanged(int drawable) {
                        imgperson.setImageDrawable(context.getDrawable(drawable));
                        imgampire.setImageDrawable(context.getDrawable(drawable));
                        txttime.setText("Dealer Changed Just Now");
                        txtTips.setText("TIPS: "+Variables.CURRENCY_SYMBOL+dealer.tips+" CHIPS");
//                        Funtions.showToast(context, "Dealer Selected");
                    }
                });
            }
        });

        ImageView imgpl1minus = dialog.findViewById(R.id.imgpl1minus);
        final Button btnpl1number = dialog.findViewById(R.id.btnpl1number);
        ImageView imgpl1plus = dialog.findViewById(R.id.imgpl1plus);

        btnpl1number.setText("" + tips[0]);

        imgpl1plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                tips[0] = tips[0] + 100;
                btnpl1number.setText("" + tips[0]);
            }
        });


        imgpl1minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (tips[0] > 100) {
                    tips[0] = tips[0] - 100;
                    btnpl1number.setText("" + tips[0]);
                }


            }
        });

        ((Button) dialog.findViewById(R.id.btnTips)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendTips(tips[0],context,callback);
                dialog.dismiss();
            }
        });


        dialog.show();
        Functions.setDialogParams(dialog);
    }

    public static String GiftSendto_User = "";
    public static void showGiftDialog(final Context context, final String player, final Callback callback) {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_gift);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ((ImageView) dialog.findViewById(R.id.imgclosetop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView txtnotfound = dialog.findViewById(R.id.txtnotfound);
        TextView txtheader = dialog.findViewById(R.id.txtheader);
        txtheader.setText("Gifts");

        RecyclerView recyclerView_gifts = dialog.findViewById(R.id.recylerview_gifts);
        recyclerView_gifts.setLayoutManager(new GridLayoutManager(context, 5));

        itemClick OnDailyClick = new itemClick() {
            @Override
            public void OnDailyClick(String id,String conis, String url) {
                dialog.dismiss();
                SendGits(id, conis,url, player,context,callback);
            }
        };

        GetGiftList(recyclerView_gifts, dialog, OnDailyClick,txtnotfound,context);

        dialog.show();
        Functions.setDialogParams(dialog);
    }

    public static void GetGiftList(final RecyclerView recyclerView, Dialog dialog,
                                   final itemClick onGitsClick ,
                                   final TextView txtnotfound,
                                   final Context context) {


        final RelativeLayout rlt_progress = dialog.findViewById(R.id.rlt_progress);
        rlt_progress.setVisibility(View.VISIBLE);

        final ArrayList<GiftModel> giftModelArrayList = new ArrayList();

        HashMap params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));

        ApiRequest.Call_Api(context, Const.GIFTS_LIST, params, new Callback() {
            @Override
            public void Responce(String resp,String type, Bundle bundle) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    if (code.equalsIgnoreCase("200")) {

                        txtnotfound.setVisibility(View.GONE);

                        JSONArray welcome_bonusArray = jsonObject.getJSONArray("Gift");

                        for (int i = 0; i < welcome_bonusArray.length(); i++) {
                            JSONObject welcome_bonusObject = welcome_bonusArray.getJSONObject(i);

                            GiftModel model = new GiftModel();
                            model.setId(welcome_bonusObject.getString("id"));
                            model.setName(welcome_bonusObject.getString("name"));
                            model.setImage(welcome_bonusObject.getString("image"));
                            model.setCoin(welcome_bonusObject.getString("coin"));

                            giftModelArrayList.add(model);
                        }

                        GiftsAdapter adapter = new GiftsAdapter(context, giftModelArrayList, onGitsClick);
                        recyclerView.setAdapter(adapter);

                    } else {
                        if (jsonObject.has("message")) {
                            String message = jsonObject.getString("message");
//                                    Funtions.showToast(PublicTable.this, message,
//                                            Toast.LENGTH_LONG).show();
                        }

                        txtnotfound.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    txtnotfound.setVisibility(View.VISIBLE);

                }

                rlt_progress.setVisibility(View.GONE);


            }
        });
    }

    public static void SendGits(final String gifts_id,
                                final String gif_coins,
                                final String gifturl,
                                final String playerno,
                                final Context context,
                                final Callback requestback) {


        HashMap params = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("gift_id", gifts_id);
        params.put("to_user_id", GiftSendto_User);
        params.put("token", prefs.getString("token", ""));
        params.put("tip", "" + gif_coins);

        ApiRequest.Call_Api(context, Const.GAME_TIPS, params, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");

                    Bundle bundle1 = new Bundle();
                    bundle1.putString("gifturl",gifturl);

                    requestback.Responce(resp,playerno,bundle1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public static void SendTips(final int tips, final Context context, final Callback backresponse) {

        HashMap params = new HashMap();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        params.put("user_id", prefs.getString("user_id", ""));
        params.put("token", prefs.getString("token", ""));
        params.put("tip", "" + tips);
        params.put("gift_id", "0");
        params.put("to_user_id", "0");


        ApiRequest.Call_Api(context, Const.GAME_TIPS, params, new Callback() {
            @Override
            public void Responce(String resp,String type, Bundle bundle) {

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");

                    backresponse.Responce(""+tips,"",null);

//                    dealer.tips = dealer.tips + tips;

                    String coins = "0";
                    if (jsonObject.has("coin"))
                        coins = jsonObject.getString("coin");

                    if (code.equalsIgnoreCase("200")) {

                        SmartAlertDialog(context, "Thanks you for tip", "", "Okay", "", new Callback() {
                            @Override
                            public void Responce(String resp, String type, Bundle bundle) {

                            }
                        });

//                        Funtions.showToast(context, "" + message);


                    } else {
                        if (jsonObject.has("message")) {

                            Functions.showToast(context, message);


                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public static void SmartAlertDialog(Context context,
                                        String title,
                                        String message,
                                        final String first_btn,
                                        final String second_btn,
                                        final Callback callback){

        new SmartDialogBuilder(context)
                .setTitle(""+title)
//                .setSubTitle(""+message)
                .setCancalable(true)
                //.setTitleFont("Do you want to Logout?") //set title font
                // .setSubTitleFont(subTitleFont) //set sub title font
                .setNegativeButtonHide(true) //hide cancel button
                .setPositiveButton(""+first_btn, new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        callback.Responce(first_btn,"",null);
                        smartDialog.dismiss();
                    }
                })/*.setNegativeButton(""+second_btn, new SmartDialogClickListener() {
            @Override
            public void onClick(SmartDialog smartDialog) {
                callback.Responce(second_btn,"",null);
                smartDialog.dismiss();

            }
        })*/.build().show();


    }

    /**
     * Centralized Add Cash method that can be called from anywhere in the app
     * Uses existing add cash implementation with CommonAPI.CALL_API_UserDetails and PaymentGatewayHelper
     */
    public static void showAddCash(Context context) {
        Log.d("AddCash", "Add cash called from: " + context.getClass().getSimpleName());

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading user data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Call user data API first before navigating to payment screen
        CommonAPI.CALL_API_UserDetails((Activity) context, new Callback() {
            @Override
            public void Responce(String resp, String type, Bundle bundle) {
                progressDialog.dismiss();

                if (resp != null) {
                    Log.d("AddCash", "User data fetched successfully, showing payment options");
                    // Use PaymentGatewayHelper to show appropriate payment options
                    PaymentGatewayHelper.showPaymentOptions(context);
                } else {
                    Log.e("AddCash", "Failed to fetch user data - response is null");
                    Functions.showToast(context, "Failed to load user data. Please try again.");
                }
            }
        });
    }

    /**
     * Show insufficient balance dialog with Add Cash option for private tables
     * Directly shows add cash functionality without intermediate dialogs
     */
    public static void SmartAlertDialogWithAddCash(Context context,
                                                   String title,
                                                   String message,
                                                   final Callback callback){

        new SmartDialogBuilder(context)
                .setTitle(""+title)
                .setSubTitle(""+message)
                .setCancalable(true)
                .setNegativeButtonHide(false) // Show both buttons
                .setPositiveButton("Add Cash", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        smartDialog.dismiss();
                        // Directly show add cash functionality for private tables
                        Log.d("PrivateTable", "Add cash clicked - directly showing payment options");
                        showAddCash(context);
                    }
                })
                .setNegativeButton("Cancel", new SmartDialogClickListener() {
                    @Override
                    public void onClick(SmartDialog smartDialog) {
                        smartDialog.dismiss();
                        if (callback != null) {
                            callback.Responce("Cancel", "", null);
                        }
                    }
                })
                .build().show();
    }

    public static void showDialoagonBack(Context context, final Callback callback) {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);

        dialog.setContentView(R.layout.custom_dialog_close);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView btnclose = (ImageView) dialog.findViewById(R.id.btnclose);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView btnexitgame = (ImageView) dialog.findViewById(R.id.btnexitgame);
        ImageView btnexitloby = (ImageView) dialog.findViewById(R.id.btnexitloby);
        btnexitgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.Responce("","exit",null);

            }
        });

        btnexitloby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.Responce("","next",null);


            }
        });

        ImageView btnswitchtabel = (ImageView) dialog.findViewById(R.id.btnswitchtabel);
        btnswitchtabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callback.Responce("","switch",null);
            }
        });


        dialog.show();
        Functions.setDialogParams(dialog);
    }

    public static void showDialogSetting(final Context context,Callback callback) {
        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.custom_dialog_setting);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView imgclose = (ImageView) dialog.findViewById(R.id.imgclosetop);
        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        final Switch switchd = (Switch) dialog.findViewById(R.id.switch1);
        final SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String value = prefs.getString("issoundon", "1");
        final ImageView ivVolume = (ImageView) dialog.findViewById(R.id.ivVolume);
        final TextView tvVolume = (TextView) dialog.findViewById(R.id.tvVolume);
        if (value.equals("0")) {

            switchd.setChecked(true);
            ivVolume.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_silent));
            tvVolume.setText("Game Valume off");

        } else {

            switchd.setChecked(false);
            ivVolume.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_volume_up));
            tvVolume.setText("Game Valume on");
        }

        ivVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String value = prefs.getString("issoundon", "0");

                if (value.equals("0")) {

                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("issoundon", "1");
                    editor.apply();


                    switchd.setChecked(false);
                    ivVolume.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_volume_up));
                    tvVolume.setText("Game Valume on");




                } else {


                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("issoundon", "0");
                    editor.apply();

                    switchd.setChecked(true);
                    ivVolume.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_silent));
                    tvVolume.setText("Game Valume off");


                }

            }
        });

        if (value.equals("0")) {

            switchd.setChecked(true);

        } else {

            switchd.setChecked(false);
        }

        switchd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("issoundon", "0");
                    editor.apply();


                    // Funtions.showToast(PublicTable.this, "On");

                } else {
                    // Funtions.showToast(PublicTable.this, "Off");
                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("issoundon", "1");
                    editor.apply();

                }
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);
    }


    public static void DialogUserInfo(final Activity context) {

        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_user);
        dialog.setTitle("Title...");

        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        ((View) dialog.findViewById(R.id.imgclosetop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final EditText edtUsername;


        ImageView img_diaProfile = dialog.findViewById(R.id.img_diaProfile);
        TextView txt_diaName = dialog.findViewById(R.id.txt_diaName);
        TextView txt_diaPhone = dialog.findViewById(R.id.txt_diaPhone);
        TextView txt_bank = dialog.findViewById(R.id.txt_bank);
        TextView txt_adhar = dialog.findViewById(R.id.txt_adhar);
        TextView txt_upi = dialog.findViewById(R.id.txt_upi);
        edtUsername = dialog.findViewById(R.id.edtUsername);

        final EditText edtUserbank = dialog.findViewById(R.id.edtUserbank);
        final EditText edtUserupi = dialog.findViewById(R.id.edtUserupi);
        final EditText edtUseradhar = dialog.findViewById(R.id.edtUseradhar);

        final LinearLayout lnrUserinfo = dialog.findViewById(R.id.lnr_userinfo);
        final LinearLayout lnr_updateuser = dialog.findViewById(R.id.lnr_updateuser);

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        String name = prefs.getString("name","");
        String bank_detail = prefs.getString("bank_detail","");
        String upi = prefs.getString("upi","");
        String adhar_card = prefs.getString("adhar_card","");
        String mobile = prefs.getString("mobile","");
        String profile_pic = prefs.getString("profile_pic","");

        edtUsername.setText("" + name);
        edtUserbank.setText("" + bank_detail);
        edtUserupi.setText("" + upi);
        edtUseradhar.setText("" + adhar_card);

        txt_diaName.setText("Name: " + name);
        txt_diaPhone.setText("Ph.No.:" + mobile);
        txt_bank.setText("Bank Details:" + bank_detail);
        txt_adhar.setText("Addhar No.: " + adhar_card);
        txt_upi.setText("UPI:" + upi);
        Picasso.get().load(Const.IMGAE_PATH + profile_pic).into(img_diaProfile);


        ((View) dialog.findViewById(R.id.img_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lnrUserinfo.setVisibility(View.GONE);
                lnr_updateuser.setVisibility(View.VISIBLE);

            }
        });

        ((ImageView) dialog.findViewById(R.id.imgsub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!edtUsername.getText().toString().trim().equals("")) {
                    lnrUserinfo.setVisibility(View.VISIBLE);
                    lnr_updateuser.setVisibility(View.GONE);

                    UserUpdateProfile(edtUsername.getText().toString().trim(), edtUserbank.getText().toString().trim(),
                            edtUserupi.getText().toString().trim(), edtUseradhar.getText().toString().trim(),context);

                    dialog.dismiss();
                } else {
                    Functions.showToast(context, "Input field in empty!");
                }

            }
        });


        dialog.show();
        Functions.setDialogParams(dialog);

    }

    public static void UserUpdateProfile(final String username,
                                         final String user_bank,
                                         final String user_upi,
                                         final String user_adhar,
                                         final Activity context) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.USER_UPDATE,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.d("DATA_CHECK", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");

                            if (code.equalsIgnoreCase("200")) {

                                CommonFunctions.showAlertDialog(context,"Alert message","Profile Updated Successfully!",true,"Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }, null,null);

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
                params.put("bank_detail", user_bank);
                params.put("upi", user_upi);
                params.put("adhar_card", user_adhar);
                params.put("name", username);


                params.put("token", prefs.getString("token", ""));
                Log.d("paremter_java", "getParams: " + params);
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



    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

//        Funtions.LOGE("MainActivity","DP : "+dp+" = "+px);

        return px;
    }
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static boolean check_permissions(Activity context) {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(PERMISSIONS, 2);
            }
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String Bitmap_to_base64(Activity activity,Bitmap imagebitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos .toByteArray();
        String base64= Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static void openWhatsappContact(Context context,String number) {
        String appPackage="";
        if (appInstalledOrNot(context, "com.whatsapp")) {
            appPackage = "com.whatsapp";
            //do ...
        }
        else
        if (appInstalledOrNot(context, "com.whatsapp.w4b")) {
            appPackage = "com.whatsapp.w4b";
            //do ...
        }
        else {
            Functions.showToast(context, "whatsApp is not installed");
        }

        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage(appPackage);
        context.startActivity(Intent.createChooser(i, ""));
    }

    public static boolean appInstalledOrNot(Context context,String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public static View CreateDynamicViews(int layout, ViewGroup addingview, Context context){
        View view  = LayoutInflater.from(context).inflate(layout,null);

        addingview.addView(view);
        return view;
    }

    public static String getStringFromTextView(TextView title) {
        return title.getText().toString().trim();
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static boolean isActivityVisible(Context context) {
        return context != null && !((Activity)context).isFinishing();
    }

    public static String[] LOCATION_PERMISSIONS;

    public static boolean check_location_permissions(Context context) {

        LOCATION_PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (!hasPermissions(context, LOCATION_PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                context.requestPermissions(PERMISSIONS, 123);
            }
        } else {

            return true;
        }

        return false;
    }

    public static String decimal_to_Roman(int num) {

        String[] m = new String[]{"", "M", "MM", "MMM"};
        String[] c = new String[]{ "", "C", "CC", "CCC", "CD", "D",
                "DC", "DCC", "DCCC", "CM"};

        String[] x = new String[]{ "", "X", "XX", "XXX", "XL", "L",
                "LX", "LXX", "LXXX", "XC"};

        String[] i = new String[]{  "", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX"};


        // Converting to roman
        String thousands = m[num / 1000];
        String hundreds = c[num % 1000 / 100];
        String tens = x[num % 100 / 10];
        String ones = i[num % 10];

        return thousands + hundreds + tens + ones;
    }

    public static String maskString(String input) {
        if (input.length() <= 2) {
            // If the string has 0 or 1 characters, no replacement needed
            return input;
        }

        char firstChar = input.charAt(0);
        char lastChar = input.charAt(input.length() - 1);

        // Replace characters between the first and last characters with '*'
        StringBuilder maskedString = new StringBuilder();
        maskedString.append(firstChar);
        for (int i = 1; i < input.length() - 1; i++) {
            maskedString.append('*');
        }
        maskedString.append(lastChar);

        return maskedString.toString();
    }

}
