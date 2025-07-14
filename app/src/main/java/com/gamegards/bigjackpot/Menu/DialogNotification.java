package com.gamegards.bigjackpot.Menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;

public class DialogNotification {

    Context context;
    Callback callback;
    String imageURL;
    private static DialogNotification mInstance;


    public static DialogNotification getInstance(Context context,String imageURL) {
        if (null == mInstance) {
            synchronized (DialogNotification.class) {
                if (null == mInstance) {
                    mInstance = new DialogNotification(context,imageURL);
                }
            }
        }

        if(mInstance != null)
            mInstance.init(context);

        return mInstance;

    }

    /**
     * initialization of context, use only first time later it will use this again and again
     *
     * @param context app context: first time
     */

    public DialogNotification init(Context context) {
        try {

            if (context != null) {
                this.context = context;
                initDialog();
            }

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return mInstance;
    }

    private DialogNotification initDialog() {
        dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_app_notification);
        dialog.setTitle("Title...");

        ImageView imgclose = (ImageView) dialog.findViewById(R.id.imgclosetop);
        TextView txtheader = (TextView) dialog.findViewById(R.id.tv_heading);
        ImageView imgcontent = (ImageView) dialog.findViewById(R.id.imgcontent);

        Glide.with(context).load(Const.popup_img + imageURL).into(imgcontent);

        imgclose.setOnClickListener(view -> dialog.dismiss());

        return mInstance;
    }


    public DialogNotification(Context context, String imageURL) {
        this.context = context;
        this.imageURL = imageURL;
    }


    Dialog dialog;

    public DialogNotification show() {


        dialog.setCancelable(true);
        dialog.show();
        Functions.setDialogParams(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return mInstance;
    }

    public void dismiss(){
        if(dialog != null)
            dialog.dismiss();
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

}
