package com.gamegards.gaming27;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeConversion {

    public static String convertToIST(String timeInUTC) {
        try {
            // Define the input format (UTC time)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the UTC time
            Date date = inputFormat.parse(timeInUTC);

            // Define the output format (IST time)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone

            // Convert to IST and return
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
