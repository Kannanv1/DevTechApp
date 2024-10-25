package com.sundar.devtech;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.sundar.devtech.DatabaseController.RequestURL;
import com.sundar.devtech.Interfaces.MotorCommandCallback;
import com.sundar.devtech.Models.ProductModel;
import com.sundar.devtech.R;
import com.sundar.devtech.Services.CustomAlertDialog;
import com.sundar.devtech.Services.MotorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity {

    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE;
    private MaterialButton BUY,CANCEL;
    private CustomAlertDialog customAlertDialog;
    private ImageView PROD_IMAGE;
    private TextView PROD_NAME,PROD_DESC,PROD_SPEC;
    private String run_hex,status_hex;
    private MotorService motorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        customAlertDialog = new CustomAlertDialog(ConfirmActivity.this);
        motorService = new MotorService(ConfirmActivity.this);

        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText(getApplicationContext().getString(R.string.app_name));

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        APPBAR_BTN.setVisibility(View.GONE);
        BACK_PRESS.setVisibility(View.GONE);

        PROD_IMAGE = findViewById(R.id.product_img);
        PROD_NAME = findViewById(R.id.product_name);
        PROD_DESC = findViewById(R.id.description);
        PROD_SPEC = findViewById(R.id.specification);

        BUY = findViewById(R.id.buy);
        CANCEL = findViewById(R.id.cancel);

        BUY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = customAlertDialog.confirmDialog();

                builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show progress dialog
                        CustomAlertDialog alertDialog = new CustomAlertDialog(ConfirmActivity.this);
                        AlertDialog progressDialog = alertDialog.alterDialog();
                        progressDialog.show();

                        runCommand();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = customAlertDialog.cancelDialog();

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ConfirmActivity.this, ScannerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    public void fetchProduct(){
        ArrayList<ProductModel> productModels = SlotDetailActivity.productModels;

        for (ProductModel productModel:productModels){
            Bitmap bitmap = decodeBase64(productModel.getProd_image());
            PROD_IMAGE.setImageBitmap(bitmap);
            PROD_NAME.setText(productModel.getProd_name());
            PROD_DESC.setText(productModel.getProd_desc());
            PROD_SPEC.setText(productModel.getProd_spec());
        }

        Intent intent = getIntent();

        run_hex = intent.getStringExtra("run_hex");
        status_hex = intent.getStringExtra("status_hex");
    }

    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void runCommand() {
        motorService.MotorOn(run_hex, status_hex, new MotorCommandCallback() {
            @Override
            public void onStatusCommandResult(String response) {

                TextView titleView = new TextView(ConfirmActivity.this);
                titleView.setText(response);

                AlertDialog.Builder alert = new AlertDialog.Builder(ConfirmActivity.this);
                alert.setCustomTitle(titleView);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchProduct();
    }
}