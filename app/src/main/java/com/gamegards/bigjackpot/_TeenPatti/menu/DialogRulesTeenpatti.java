package com.gamegards.bigjackpot._TeenPatti.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.gamegards.bigjackpot.Interface.Callback;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot._LuckJackpot.JackpotRulesModel;

import java.util.ArrayList;

public class DialogRulesTeenpatti {

    Context context;
    Callback callback;
    private static DialogRulesTeenpatti mInstance;


    public static DialogRulesTeenpatti getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DialogRulesTeenpatti.class) {
                if (null == mInstance) {
                    mInstance = new DialogRulesTeenpatti(context);
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
    public DialogRulesTeenpatti init(Context context) {
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

    LinearLayout lnrRuleslist ;
    TextView txtheader;
    ArrayList<JackpotRulesModel> rulesModelArrayList = new ArrayList<>();
    private DialogRulesTeenpatti initDialog() {
        dialog = Functions.DialogInstance(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_rulesteenpatti);

        txtheader = dialog.findViewById(R.id.txtheader);
        txtheader.setText("How To Play");

        return mInstance;
    }


    public DialogRulesTeenpatti(Context context) {
        this.context = context;
    }

    public DialogRulesTeenpatti() {
    }
    Dialog dialog;

    public DialogRulesTeenpatti show() {

        dialog.findViewById(R.id.imgclosetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
