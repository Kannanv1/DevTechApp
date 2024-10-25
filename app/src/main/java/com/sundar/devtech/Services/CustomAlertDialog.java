package com.sundar.devtech.Services;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.sundar.devtech.R;

public class CustomAlertDialog{
    private Context context;

    public CustomAlertDialog(Context context) {
        this.context = context;
    }

    public AlertDialog alterDialog(){
        AlertDialog progressDialog = new AlertDialog.Builder(context)
                .setTitle("Processing")
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();
        return progressDialog;
    }

    public AlertDialog.Builder confirmDialog(){
        TextView titleView = new TextView(context);
        titleView.setText("Confirm");
        titleView.setTextSize(20);
        titleView.setTextColor(context.getResources().getColor(R.color.green_200));
        titleView.setPadding(50, 40, 20, 20);

        AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
        progressDialog.setCustomTitle(titleView);
        progressDialog.setMessage("Are you sure you want to buy this product?");

        return progressDialog;
    }

    public AlertDialog.Builder cancelDialog(){
        TextView titleView = new TextView(context);
        titleView.setText("Cancel");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.RED);
        titleView.setPadding(50, 40, 20, 20);

        AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
        progressDialog.setCustomTitle(titleView);
        progressDialog.setMessage("Are you sure you want to cancel this product?");

        return progressDialog;
    }


}
