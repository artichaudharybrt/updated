package com.gamegards.bigjackpot._jhandhiMunda;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.bigjackpot.Interface.OnItemClickListener;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.Utils.Functions;
import com.gamegards.bigjackpot.Utils.SharePref;

import java.util.List;

public class JhandhiMundaAdapter extends RecyclerView.Adapter<JhandhiMundaAdapter.holder> {

    List<JhandhiMundaRulesModel> arraylist;
    OnItemClickListener callback;
    Context context;

    public JhandhiMundaAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position > 2 ? JhandhiMundaRulesModel.TYPE_HIGH : JhandhiMundaRulesModel.TYPE_LOW;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == JhandhiMundaRulesModel.TYPE_HIGH) {
            view = LayoutInflater.from(context).inflate(R.layout.item_jhandimunda_setamount, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_jhandimunda_setamount, parent, false);
        }
        return new holder(view, viewType);
    }

    public void setArraylist(List<JhandhiMundaRulesModel> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }

    public void onItemSelectListener(OnItemClickListener callback) {
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        JhandhiMundaRulesModel model = arraylist.get(position);

        if (model != null)
            holder.bind(model, position);

    }

    @Override
    public int getItemCount() {
        return arraylist != null ? arraylist.size() : 0;
    }

    class holder extends RecyclerView.ViewHolder {
        public holder(@NonNull View itemView, int viewType) {
            super(itemView);
        }

        void bind(JhandhiMundaRulesModel model, int position) {
            int postion = getAdapterPosition();
            ImageView ivContainerbg = itemView.findViewById(R.id.ivContainerbg);
            ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.ic_jackpot_rule_bg));
            ivContainerbg.clearAnimation();
            TextView tvJackpotHeading = itemView.findViewById(R.id.tvJackpotHeading);
            TextView tvJackpotamount = itemView.findViewById(R.id.tvJackpotamount);
            TextView tvJackpotSelectamount = itemView.findViewById(R.id.tvJackpotSelectamount);
            TextView tvUsersAddedAmount = itemView.findViewById(R.id.tvUsersAddedAmount);
            RelativeLayout rltAmountadded = itemView.findViewById(R.id.rltAmountadded);
            TextView btninto = itemView.findViewById(R.id.btninto);

            if (position == 0) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.heart));
            } else if (position == 1) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.spade));
            } else if (position == 2) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.diamond));
            } else if (position == 3) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.club));
            } else if (position == 4) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.face));
            } else if (position == 5) {
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.flag));
            }

            tvJackpotHeading.setText(model.rule_type);
            tvJackpotamount.setText("" + model.added_amount);
            tvJackpotSelectamount.setText("" + model.select_amount);
            btninto.setText(model.into);
            itemView.findViewById(R.id.rltContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //    ((JhandhiMunda_A) context).playButtonTouchSound();
                    ((JhandhiMunda_Socket) context).playButtonTouchSound();
                    Animation animBounce = AnimationUtils.loadAnimation(context,
                            R.anim.bounce);
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
                    itemView.startAnimation(animBounce);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callback.Response(v, postion, model);
                        }
                    }, 700);


                }
            });

            rltAmountadded.setVisibility(View.GONE);

            if (model.isAnimatedAddedAmount()
                    && model.getLast_added_id() != SharePref.getInstance().getInt(SharePref.lastAmountAddedID)) {
                SharePref.getInstance().putInt(SharePref.lastAmountAddedID, model.getLast_added_id());
                rltAmountadded.setVisibility(View.VISIBLE);
                tvUsersAddedAmount.setText("+" + model.getLast_added_amount());
                SlideToAbove(rltAmountadded, model);
            }

            if (model.isWine()) {
                model.setWine(false);
                ivContainerbg.setBackground(Functions.getDrawable(context, R.drawable.ic_jackpot_rule_bg_selected));
                ivContainerbg.setBackground(Functions.getDrawable(context, R.color.Golder_yellow));
                initiAnimation();
                ivContainerbg.startAnimation(blinksAnimation);
            }
        }
    }

    Animation blinksAnimation;

    private void initiAnimation() {
        blinksAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
        blinksAnimation.setDuration(500);
        blinksAnimation.setRepeatCount(Animation.INFINITE);
//        blinksAnimation.setStartOffset(700);
    }


    public void SlideToAbove(View animationView, JhandhiMundaRulesModel model) {
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
