package com.gamegards.bigjackpot.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gamegards.bigjackpot.Interface.itemClick;
import com.gamegards.bigjackpot.ApiClasses.Const;
import com.gamegards.bigjackpot.R;
import com.gamegards.bigjackpot.model.GiftModel;
//import com.paytm.pgsdk.Log;

import java.util.ArrayList;

public class GiftsAdapter extends RecyclerView.Adapter<GiftsAdapter.myholder> {


    Context context;
    ArrayList<GiftModel> giftModelArrayList;
    itemClick onGitsClick;

    public GiftsAdapter(Context context, ArrayList<GiftModel> giftModelArrayList, itemClick onGitsClick) {
        this.context = context;
        this.giftModelArrayList = giftModelArrayList;
        this.onGitsClick = onGitsClick;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview = LayoutInflater.from(context).inflate(R.layout.gift_itemview,parent,false);

        return new myholder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, final int position) {

        final GiftModel model = giftModelArrayList.get(position);

        // Set gift name and amount
        holder.txtgiftname.setText(model.getName());
        ((TextView)holder.itemView.findViewById(R.id.tvAmount)).setText(model.getCoin() + " Coins");

        // Load gift image/GIF with proper error handling and placeholder
        String imageUrl = Const.IMGAE_PATH + model.getImage();

        Log.d("GiftsAdapter", "Gift: " + model.getName() + ", Image URL: " + imageUrl);


        Glide.with(context)
                .asGif() // Support for GIF animations
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.gift_boxnew) // Default gift placeholder
                        .error(R.drawable.gift_boxnew) // Error fallback image
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized
                        .centerCrop())
                .into(holder.imggift);

        // Set click listener for gift selection
        holder.cd_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGitsClick.OnDailyClick(model.getId(), model.getCoin(), model.getImage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return giftModelArrayList.size();
    }

    class myholder extends RecyclerView.ViewHolder{

        TextView txtgiftname;
        ImageView imggift;
        View cd_items;

        public myholder(@NonNull View itemView) {
            super(itemView);

            txtgiftname = itemView.findViewById(R.id.txtgiftname);
            imggift = itemView.findViewById(R.id.imggift);
            cd_items = itemView.findViewById(R.id.cd_items);

        }
    }

}
