package com.gamegards.gaming27.Adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gamegards.gaming27.ApiClasses.Const;
import com.gamegards.gaming27.R;
import com.gamegards.gaming27._ColorPrediction.Bots_list;

import java.util.ArrayList;

public class DummyPayoutAdapter extends RecyclerView.Adapter<DummyPayoutAdapter.ViewHolder>{

    Context context;
    ArrayList<Bots_list> list;
    private Handler mHandler;
    private RecyclerView mRecyclerView;

    private int mScrollPosition = 0;
    private final int SCROLL_AMOUNT_PX = 10; // Adjust scrolling speed here
    private final long SCROLL_DELAY_MS = 100; // Adjust delay between scrolls here

    public DummyPayoutAdapter(Context context, ArrayList<Bots_list> list, RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.mRecyclerView = recyclerView;
        mHandler = new Handler();
        startAutoScroll();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.dummy_layout_winners, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Bots_list bots_list = list.get(position);

        holder.tv_name.setText(bots_list.getName());
        holder.txt_wallet.setText("₹ "+bots_list.getCoin());

        Glide.with(context).load(Const.IMGAE_PATH + bots_list.getAvatar()).into(holder.img_profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void startAutoScroll() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int itemCount = getItemCount();
                if (itemCount == 0) {
                    return;
                }
                mScrollPosition += SCROLL_AMOUNT_PX;
                if (mScrollPosition >= mRecyclerView.computeHorizontalScrollRange()) {
                    mScrollPosition = 0;
                    mRecyclerView.scrollToPosition(0);
                }
                mRecyclerView.smoothScrollBy(SCROLL_AMOUNT_PX, 0);
                mHandler.postDelayed(this, SCROLL_DELAY_MS);
            }
        }, SCROLL_DELAY_MS);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView tv_name,txt_wallet;
       ImageView img_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name=itemView.findViewById(R.id.txt_name);
            txt_wallet=itemView.findViewById(R.id.txt_wallet);
            img_profile=itemView.findViewById(R.id.img_profile);

        }
    }

}