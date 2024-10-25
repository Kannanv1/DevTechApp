package com.sundar.devtech.Services;

import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {

    // Correct date format for months
    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(new Date());
    }

    // Correct method for 12-hour time format
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        return dateFormat.format(new Date());
    }

    // Date with day name
    public static String getDateAndDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy / EEEE");
        return dateFormat.format(new Date());
    }

    // Time with AM/PM marker
    public static String getTimeAndMarker() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        return dateFormat.format(new Date());
    }

    // SQL date format
    public static String getSqlDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }
}
