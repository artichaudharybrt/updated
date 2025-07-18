package com.gamegards.bigjackpot._rouleteGame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.bigjackpot.Interface.OnItemClickListener;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;

import java.util.Arrays;
import java.util.List;

public class RouleteRulesAdapter extends RecyclerView.Adapter<RouleteRulesAdapter.holder> {
    List<RouleteRulesModel> arraylist;
    OnItemClickListener callback;
    Context context;

    public RouleteRulesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position > 2 ? RouleteRulesModel.TYPE_HIGH : RouleteRulesModel.TYPE_LOW;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType== RouleteRulesModel.TYPE_HIGH) {
            view = LayoutInflater.from(context).inflate(R.layout.item_roulete_setamount, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_roulete_setamount, parent, false);
        }
        return new holder(view, viewType);
    }

    public void setArraylist(List<RouleteRulesModel> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }

    public void onItemSelectListener(OnItemClickListener callback){
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        RouleteRulesModel model = arraylist.get(position);

        if(model != null)
            holder.bind(model);

    }

    @Override
    public int getItemCount() {
        return arraylist != null ? arraylist.size() : 0;
    }

    class holder extends RecyclerView.ViewHolder{
        public holder(@NonNull View itemView, int viewType) {
            super(itemView);
        }

        void bind(RouleteRulesModel model){
            int postion = getAdapterPosition();
            ImageView ivContainerbg = itemView.findViewById(R.id.ivContainerbg);

            TextView tvJackpotHeading = itemView.findViewById(R.id.tvJackpotHeading);
            TextView tvJackpotamount = itemView.findViewById(R.id.tvJackpotamount);
            TextView tvJackpotSelectamount = itemView.findViewById(R.id.tvJackpotSelectamount);
            TextView tvUsersAddedAmount = itemView.findViewById(R.id.tvUsersAddedAmount);
            RelativeLayout rltAmountadded = itemView.findViewById(R.id.rltAmountadded);
            LinearLayout lnrJackportamountparent = itemView.findViewById(R.id.lnrJackportamountparent);
            TextView btninto = itemView.findViewById(R.id.btninto);
            TextView tv_cat_BetAmount = itemView.findViewById(R.id.tv_cat_BetAmount);

            tvJackpotHeading.setText(model.rule_type);
            int textColor = R.color.white;

            tv_cat_BetAmount.setVisibility(View.GONE);
            lnrJackportamountparent.setBackground(Functions.getDrawable(context,R.drawable.glow_circle_bg_transparent));

/*
            int textColor = R.color.white;
            if(model.rule_value % 2 != 0)
            {
                textColor = R.color.red;
            }*/

         /*   lnrJackportamountparent.setBackground(context.getResources().getDrawable(R.drawable.rol_black));
            if(model.rule_value % 2 != 0)
            {
                lnrJackportamountparent.setBackground(context.getResources().getDrawable(R.drawable.rol_red));

            }*/

            // List of numbers you want to be red
            List<Integer> redNumbers = Arrays.asList(32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3);

// Check if the current number is in the list of red numbers
            if (redNumbers.contains(model.rule_value)) {
                // Set red background for these numbers
                lnrJackportamountparent.setBackground(context.getResources().getDrawable(R.drawable.rol_red));
            } else {
                // Set black background for all other numbers
                lnrJackportamountparent.setBackground(context.getResources().getDrawable(R.drawable.rol_black));
            }

            tvJackpotHeading.setTextColor(Functions.getColor(context,textColor));

            tvJackpotamount.setText(""+model.added_amount);
            tvJackpotSelectamount.setText(""+model.select_amount);
            btninto.setText(model.into);

            if (!tvJackpotSelectamount.getText().equals("0")){
                tv_cat_BetAmount.setText(""+model.select_amount);
                tv_cat_BetAmount.setVisibility(View.VISIBLE);
                tv_cat_BetAmount.setBackground(Functions.getDrawable(context,R.drawable.ic_dt_chips));
                //   tv_cat_BetAmount.setBackgroundResource(R.drawable.ic_dt_chips);
            }

            itemView.findViewById(R.id.rltContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((RouleteGame_Socket)context).GameAmount!=0) {

                        itemView.findViewById(R.id.tv_cat_BetAmount).setVisibility(View.VISIBLE);
                        itemView.findViewById(R.id.tv_cat_BetAmount).setBackgroundResource(R.drawable.ic_dt_chips);
                        //    ((RouleteGame_A)context).playButtonTouchSound();
                        ((RouleteGame_Socket) context).playButtonTouchSound();
                        Animation animBounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
                        animBounce.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

//                    itemView.startAnimation(animBounce);

//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                callback.Response(v, postion, model);
//                            }
//                        }, 750);

                        callback.Response(v,postion,model);

                    }else {
                        Functions.showToast(context,"First Select Bet amount");
                    }

                }
            });

            rltAmountadded.setVisibility(View.GONE);

//            if(model.isAnimatedAddedAmount()
//                    && model.getLast_added_id() != SharePref.getInstance().getInt(SharePref.lastAmountAddedID))
//            {
//                SharePref.getInstance().putInt(SharePref.lastAmountAddedID,model.getLast_added_id());
//                rltAmountadded.setVisibility(View.VISIBLE);
//                tvUsersAddedAmount.setText("+"+model.getLast_added_amount());
////                SlideToAbove(rltAmountadded,model);
//            }

            if(model.isWine())
            {
                model.setWine(false);
                lnrJackportamountparent.setBackground(Functions.getDrawable(context,R.drawable.ic_win_rule_bg_selected));
                initiAnimation();
                lnrJackportamountparent.startAnimation(blinksAnimation);
            //    ((RouleteGame_A)context).removebetcoinlayout();
                ((RouleteGame_Socket)context).removebetcoinlayout();
            }
        }
    }

    Animation blinksAnimation;

    private void initiAnimation() {
        blinksAnimation = AnimationUtils.loadAnimation(context,R.anim.blink);
        blinksAnimation.setDuration(500);
        blinksAnimation.setRepeatCount(Animation.INFINITE);
//        blinksAnimation.setStartOffset(700);
    }



    public void SlideToAbove(View animationView, RouleteRulesModel model) {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

        slide.setDuration(1000);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        animationView.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animationView.clearAnimation();
                animationView.setVisibility(View.GONE);
                model.setAnimatedAddedAmount(false);

//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                        rl_footer.getWidth(), rl_footer.getHeight());
//                // lp.setMargins(0, 0, 0, 0);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                rl_footer.setLayoutParams(lp);

            }

        });

    }

    public void SlideToDown(View animationView) {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        animationView.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

//                rl_footer.clearAnimation();
//
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                        rl_footer.getWidth(), rl_footer.getHeight());
//                lp.setMargins(0, rl_footer.getWidth(), 0, 0);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                rl_footer.setLayoutParams(lp);

            }

        });

    }

}
