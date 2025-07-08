package com.gamegards.gaming27.Comman;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gamegards.gaming27.Interface.Callback;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27.Utils.Functions;
import com.gamegards.gaming27.Utils.Variables;


public class DialogSelectPaymentType {

    Context context;
    Callback callback;
    public DialogSelectPaymentType(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void showSelectPaymentType(){

        // custom dialog
        final Dialog dialog = Functions.DialogInstance(context);
        dialog.setContentView(R.layout.dialog_select_payment_stype);
        dialog.setTitle("Title...");

        TextView tv_heading = dialog.findViewById(R.id.tv_heading);

        RadioGroup radioGroup = dialog.findViewById(R.id.rg_selectpay);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton razor_pay = (RadioButton) group.getChildAt(0);

                if(razor_pay.getId() == checkedId)
                {
                    callback.Responce(Variables.RAZOR_PAY,"",null);
                }
                else {
                    callback.Responce(Variables.CASH_FREE,"",null);
                }

                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Functions.setDialogParams(dialog);


    }
}
