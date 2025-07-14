package com.gamegards.bigjackpot.Details.Menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.bigjackpot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DepositHistoryAdapter extends RecyclerView.Adapter<DepositHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DepositHistoryModel> depositHistoryList;

    public DepositHistoryAdapter(Context context, ArrayList<DepositHistoryModel> depositHistoryList) {
        this.context = context;
        this.depositHistoryList = depositHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deposit_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DepositHistoryModel model = depositHistoryList.get(position);

        // Set Serial Number (position + 1)
        holder.tvSerial.setText(String.valueOf(position + 1));

        // Set up View button click listener
        holder.btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String utrId = model.getUtr();
                Log.d("DepositHistory", "View button clicked for UTR ID: " + utrId);

                if (utrId != null && !utrId.isEmpty() && !utrId.equals("null")) {
                    // Navigate to BSC scan transaction page
                    String bscScanUrl = "https://bscscan.com/tx/" + utrId;
                    Log.d("DepositHistory", "Opening BSC scan URL: " + bscScanUrl);

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bscScanUrl));
                        context.startActivity(intent);
                        Toast.makeText(context, "Opening transaction details...", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("DepositHistory", "Error opening BSC scan URL: " + e.getMessage());
                        Toast.makeText(context, "Error opening transaction details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w("DepositHistory", "UTR ID is null or empty: " + utrId);
                    Toast.makeText(context, "Transaction ID not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set Full Phone Number
        String phone = model.getPhone();
        holder.tvGames.setText(phone);

        // Set Amount with currency
        String amount = model.getMoney();
        if (amount.equals("0")) {
            holder.txtAmount.setText("₹0");
            holder.txtAmount.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.txtAmount.setText("₹" + amount);
            holder.txtAmount.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }

        // Set Type
        holder.tvType.setText(model.getType());

        // Set Status with color coding
        String status = model.getStatus();
        holder.tvStatus.setText(status);

        // Color code based on status (Updated for new status values)
        if (status.equalsIgnoreCase("Approved")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (status.equalsIgnoreCase("Pending")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (status.equalsIgnoreCase("Cancelled")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        // Set Time (formatted)
        String time = model.getTime();
        String formattedTime = formatDateTime(time);
        holder.tvTime.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return depositHistoryList.size();
    }

    private String formatDateTime(String dateTimeString) {
        try {
            // First try to parse as Unix timestamp (like 1748647729)
            if (dateTimeString.matches("\\d{10}")) {
                long timestamp = Long.parseLong(dateTimeString) * 1000L; // Convert to milliseconds
                Date date = new Date(timestamp);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM\nHH:mm", Locale.getDefault());
                return outputFormat.format(date);
            }

            // Try to parse as ISO 8601 date format from API
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM\nHH:mm", Locale.getDefault());

            Date date = inputFormat.parse(dateTimeString);
            return outputFormat.format(date);
        } catch (Exception e) {
            // If parsing fails, try without milliseconds
            try {
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy\nhh:mm a", Locale.getDefault());

                Date date = inputFormat2.parse(dateTimeString);
                return outputFormat.format(date);
            } catch (Exception e2) {
                // If all parsing fails, try as 13-digit timestamp (milliseconds)
                try {
                    if (dateTimeString.matches("\\d{13}")) {
                        long timestamp = Long.parseLong(dateTimeString);
                        Date date = new Date(timestamp);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy\nhh:mm a", Locale.getDefault());
                        return outputFormat.format(date);
                    }
                } catch (Exception e3) {
                    // Return truncated original string as fallback
                    if (dateTimeString.length() > 10) {
                        return dateTimeString.substring(0, 10);
                    }
                }
                return dateTimeString;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvGames, txtAmount, tvType, tvStatus, tvTime;
        Button btnViewTransaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Map to the actual IDs in item_deposit_history.xml
            tvSerial = itemView.findViewById(R.id.tvSerial);
            btnViewTransaction = itemView.findViewById(R.id.btnViewTransaction);
            tvGames = itemView.findViewById(R.id.tvGames);
            txtAmount = itemView.findViewById(R.id.txtammount);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
