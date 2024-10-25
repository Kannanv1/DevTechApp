package com.sundar.devtech;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sundar.devtech.DatabaseController.RequestURL;
import com.sundar.devtech.Masters.MotorMaster;
import com.sundar.devtech.Models.MotorModel;
import com.sundar.devtech.Models.ProductModel;
import com.sundar.devtech.Services.CustomAlertDialog;
import com.sundar.devtech.Services.MotorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlotDetailActivity extends AppCompatActivity {
    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE,LOGGED_USER;
    private TextInputEditText SLOT_NUMBERS;
    private String OLD_SLOT_NUMBER;
    private MaterialButton NUM0,NUM1,NUM2,NUM3,NUM4,NUM5,NUM6,NUM7,NUM8,NUM9;
    private AppCompatButton CLEAR,CANCEL,CONFIRM;
    private MotorService motorService;
    public static ArrayList<ProductModel> productModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_detail);

        motorService = new MotorService(SlotDetailActivity.this);

        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        SLOT_NUMBERS = findViewById(R.id.slotNumber);
        APPBAR_TITLE.setText(getApplicationContext().getString(R.string.app_name));

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        BACK_PRESS.setVisibility(View.GONE);
        APPBAR_BTN.setVisibility(View.GONE);
        LOGGED_USER = findViewById(R.id.logged_user);

        NUM0 = findViewById(R.id.btn_0);
        NUM1 = findViewById(R.id.btn_1);
        NUM2 = findViewById(R.id.btn_2);
        NUM3 = findViewById(R.id.btn_3);
        NUM4 = findViewById(R.id.btn_4);
        NUM5 = findViewById(R.id.btn_5);
        NUM6 = findViewById(R.id.btn_6);
        NUM7 = findViewById(R.id.btn_7);
        NUM8 = findViewById(R.id.btn_8);
        NUM9 = findViewById(R.id.btn_9);
        CLEAR = findViewById(R.id.clear);
        CANCEL = findViewById(R.id.cancel);
        CONFIRM = findViewById(R.id.confirm);

        SLOT_NUMBERS.setText("");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String employeeId = sharedPreferences.getString("EMPLOYEE_ID", null);

        if (employeeId != null) {
           LOGGED_USER.setText("Logged in Staff id - "+employeeId);
        }


        ArrayList<MaterialButton> numbers = new ArrayList<>();
        numbers.add(NUM0);
        numbers.add(NUM1);
        numbers.add(NUM2);
        numbers.add(NUM3);
        numbers.add(NUM4);
        numbers.add(NUM5);
        numbers.add(NUM6);
        numbers.add(NUM7);
        numbers.add(NUM8);
        numbers.add(NUM9);

        for (MaterialButton b : numbers) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentText = SLOT_NUMBERS.getText().toString();

                    // Check if the current text length is less than 2
                    if (currentText.length() < 2) {
                        // If SLOT_NUMBERS is empty or equals to "0", replace it with the button's text
                        if (currentText.isEmpty()) {
                            SLOT_NUMBERS.setText(b.getText().toString());
                        } else {
                            SLOT_NUMBERS.setText(currentText + b.getText().toString());
                        }
                        OLD_SLOT_NUMBER = SLOT_NUMBERS.getText().toString();
                    } else {
                        Toast.makeText(SlotDetailActivity.this, "Maximum Allow Two Digits", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        CLEAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = SLOT_NUMBERS.getText().toString();
                if (num.length() >1){
                    SLOT_NUMBERS.setText(num.substring(0, num.length()-1));
                }else if (num.length() == 1){
                    SLOT_NUMBERS.setText("");
                }
            }
        });

        CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlotDetailActivity.this, ScannerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        CONFIRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slot_number = SLOT_NUMBERS.getText().toString();
                if (slot_number.isEmpty()){
                    Toast.makeText(SlotDetailActivity.this, "Please Enter The Slot Number", Toast.LENGTH_SHORT).show();
                }else if (slot_number.length() < 2){
                    Toast.makeText(SlotDetailActivity.this, "Please Enter The Correct Slot Number", Toast.LENGTH_SHORT).show();
                }else {
                    productChecking();
                }
            }
        });
    }

    public void productChecking() {
        String motor_no = SLOT_NUMBERS.getText().toString();

        // Show progress dialog
        CustomAlertDialog dialog = new CustomAlertDialog(this);
        AlertDialog progressDialog = dialog.alterDialog();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.product,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.has("message")) {
                                Toast.makeText(SlotDetailActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (jsonResponse.has("products")) {

                                JSONArray jsonArray = jsonResponse.getJSONArray("products");
                                productModels.clear();

                                String run_hex = null;
                                String status_hex = null;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String MOTOR_NO = object.getString("motor_no");
                                    String PROD_NAME = object.getString("prod_name");
                                    String PROD_SPEC = object.getString("prod_spec");
                                    String PROD_DESC = object.getString("prod_desc");
                                    String IMAGE = object.getString("image");
                                    String RUN_HEX = object.getString("run_hex");
                                    String STATUS_HEX = object.getString("status_hex");

                                    run_hex = RUN_HEX;
                                    status_hex =STATUS_HEX;

                                    productModels.add(new ProductModel(MOTOR_NO, PROD_NAME, PROD_SPEC, PROD_DESC, IMAGE));
                                }

                                if (!productModels.isEmpty()) {
                                    Intent intent = new Intent(SlotDetailActivity.this, ConfirmActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("run_hex",run_hex);
                                    intent.putExtra("status_hex",status_hex);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SlotDetailActivity.this, "No products available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SlotDetailActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SlotDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("motor_no", motor_no);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}