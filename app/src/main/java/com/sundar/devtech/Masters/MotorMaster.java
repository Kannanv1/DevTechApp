package com.sundar.devtech.Masters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.sundar.devtech.Adapter.MotorAdapter;
import com.sundar.devtech.DatabaseController.RequestURL;
import com.sundar.devtech.Models.EmployeeModel;
import com.sundar.devtech.Models.MotorModel;
import com.sundar.devtech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotorMaster extends AppCompatActivity {
    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE;
    private TextInputEditText MOTOR_NO, MOTOR_RUN_HEX, MOTOR_STATUS_HEX;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;
    private RecyclerView MOTOR_RECYCLER;
    private RecyclerView.LayoutManager MOTOR_MANAGER;
    private List<MotorModel> MOTOR;
    private MotorAdapter MOTOR_ADAPTER;
    public static String LOGGED_USER = null;
    private TextInputEditText SEARCHVIEW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_master);

        MOTOR_RECYCLER = findViewById(R.id.motor_recycler);
        MOTOR_MANAGER = new GridLayoutManager(MotorMaster.this, 1);
        MOTOR_RECYCLER.setLayoutManager(MOTOR_MANAGER);
        MOTOR = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        if (userId != null) {
            LOGGED_USER = userId;
        }else {
            LOGGED_USER = "admin";
        }

        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText("Motor Master");

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        APPBAR_BTN.setImageResource(R.drawable.add);
        BACK_PRESS.setOnClickListener(view -> MotorMaster.super.onBackPressed());

        /* filter from search bar start*/
        SEARCHVIEW = findViewById(R.id.searchView_motor);
        SEARCHVIEW.clearFocus();
        SEARCHVIEW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fileList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    SEARCHVIEW.setCursorVisible(false);
                    // Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(SEARCHVIEW.getWindowToken(), 0);
                } else {
                    // Enable the cursor pointer when there is text in the search view
                    SEARCHVIEW.setCursorVisible(true);
                }
            }

            private void fileList(String text) {

                List<MotorModel> filteredList = new ArrayList<>();
                for (MotorModel item : MOTOR) {
                    if (item.getMotor_no().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(MotorMaster.this, "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    MOTOR_ADAPTER.setFilteredList(filteredList);
                }
            }
        });

        /* filter from search bar End*/


        APPBAR_BTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MotorMaster.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(MotorMaster.this).inflate(R.layout.motor_dialog,
                        (LinearLayout)findViewById(R.id.motor_dialog));
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Add New Motor");

                MOTOR_NO = view.findViewById(R.id.motor_no);
                MOTOR_RUN_HEX = view.findViewById(R.id.run_hex);
                MOTOR_STATUS_HEX = view.findViewById(R.id.status_hex);
                ACTIVE = view.findViewById(R.id.motor_active);

                SAVE = view.findViewById(R.id.motor_insert_btn);
                CANCEL = view.findViewById(R.id.motor_cancel_btn);

                active_adapter = new ArrayAdapter<>(MotorMaster.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.active));
                active_adapter.setDropDownViewResource(R.layout.item_drop_down);
                ACTIVE.setAdapter(active_adapter);

                SAVE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        insert();
                    }
                });

                CANCEL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        select();
    }

    public void insert() {
        String motor_no = MOTOR_NO.getText().toString().trim();
        String run_hex = MOTOR_RUN_HEX.getText().toString().trim();
        String status_hex = MOTOR_STATUS_HEX.getText().toString().trim();
        String active = ACTIVE.getSelectedItem().toString().trim();

        if (active.equals("ENABLE")) {
            active = "1";
        }else {
            active = "2";
        }
        String finalActive = active;

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.motor_insert,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("success")){
                            Toast.makeText(MotorMaster.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                            MOTOR_NO.setText("");
                            MOTOR_RUN_HEX.setText("");
                            MOTOR_STATUS_HEX.setText("");
                            MOTOR_NO.requestFocus();
                            select();
                        }
                        else {
                            Toast.makeText(MotorMaster.this, "Inserted Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(MotorMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MotorMaster.this, "ENetwork error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("motor_no", motor_no);
                params.put("run_hex", run_hex);
                params.put("status_hex",status_hex);
                params.put("active", finalActive);
                params.put("user",LOGGED_USER);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MotorMaster.this);
        requestQueue.add(request);
    }

    public void select() {

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.motor_fetch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            MOTOR.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String MOTOR_ID = object.getString("motor_id");
                                String MOTOR_NO = object.getString("motor_no");
                                String RUN_HEX = object.getString("run_hex");
                                String STATUS_HEX = object.getString("status_hex");
                                String ACTIVE = object.getString("active");

                                MotorModel motorModel = new MotorModel(MOTOR_ID, MOTOR_NO, RUN_HEX, STATUS_HEX, ACTIVE);
                                MOTOR.add(motorModel);
                            }

                            MOTOR_ADAPTER = new MotorAdapter(MotorMaster.this, MOTOR);
                            MOTOR_RECYCLER.setAdapter(MOTOR_ADAPTER);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MotorMaster.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(MotorMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MotorMaster.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MotorMaster.this);
        requestQueue.add(request);
    }

}