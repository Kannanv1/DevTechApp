package com.sundar.devtech.Masters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.SearchView;
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
import com.sundar.devtech.Adapter.EmployeeAdapter;
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

public class EmployeeMaster extends AppCompatActivity {

    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE;
    private TextInputEditText EMP_ID, EMP_NAME, EMP_MOBILE;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;
    private RecyclerView EMP_RECYCLER;
    private RecyclerView.LayoutManager EMP_MANAGER;
    private List<EmployeeModel> EMP;
    private EmployeeAdapter EMP_ADAPTER;
    public static String LOGGED_USER = null;
    private TextInputEditText SEARCHVIEW;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_master);


        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText("Employee Master");

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        APPBAR_BTN.setImageResource(R.drawable.add);
        BACK_PRESS.setOnClickListener(view -> EmployeeMaster.super.onBackPressed());

        EMP_RECYCLER = findViewById(R.id.employee_recycler);
        EMP_MANAGER = new GridLayoutManager(EmployeeMaster.this, 1);
        EMP_RECYCLER.setLayoutManager(EMP_MANAGER);
        EMP = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        if (userId != null) {
            LOGGED_USER = userId;
        }else {
            LOGGED_USER = "admin";
        }

        /* filter from search bar start*/
        SEARCHVIEW = findViewById(R.id.searchView_emp);
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

                List<EmployeeModel> filteredList = new ArrayList<>();
                for (EmployeeModel item : EMP) {
                    if (item.getEmp_id().contains(text.toLowerCase()) ||
                            item.getEmp_name().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(EmployeeMaster.this, "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    EMP_ADAPTER.setFilteredList(filteredList);
                }
            }
        });

        /* filter from search bar End*/


        APPBAR_BTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeMaster.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(EmployeeMaster.this).inflate(R.layout.employee_dialog,
                        (LinearLayout)findViewById(R.id.emp_dialog));
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Add New Employee");

                EMP_ID = view.findViewById(R.id.emp_id);
                EMP_NAME = view.findViewById(R.id.emp_name);
                EMP_MOBILE = view.findViewById(R.id.emp_mobile);

                ACTIVE = view.findViewById(R.id.emp_active);

                SAVE = view.findViewById(R.id.emp_insert_btn);
                CANCEL = view.findViewById(R.id.emp_cancel_btn);

                active_adapter = new ArrayAdapter<>(EmployeeMaster.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.active));
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
    public void insert() {
        String emp_id = EMP_ID.getText().toString().trim();
        String emp_name = EMP_NAME.getText().toString().trim();
        String mobile = EMP_MOBILE.getText().toString().trim();
        String active = ACTIVE.getSelectedItem().toString().trim();

        if (active.equals("ENABLE")) {
            active = "1";
        }else {
            active = "2";
        }
        String finalActive = active;

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.emp_insert,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("success")){
                            Toast.makeText(EmployeeMaster.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                            EMP_ID.setText("");
                            EMP_NAME.setText("");
                            EMP_MOBILE.setText("");
                            EMP_ID.requestFocus();
                            select();
                        }
                        else {
                            Toast.makeText(EmployeeMaster.this, "Inserted Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(EmployeeMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmployeeMaster.this, "ENetwork error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
                params.put("emp_name", emp_name);
                params.put("mobile",mobile);
                params.put("active", finalActive);
                params.put("user",LOGGED_USER);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(EmployeeMaster.this);
        requestQueue.add(request);
    }

    public void select() {

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.emp_fetch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            EMP.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String EMP_ID = object.getString("emp_id");
                                String EMP_NAME = object.getString("emp_name");
                                String MOBILE = object.getString("mobile");
                                String ACTIVE = object.getString("active");

                                EmployeeModel employeeModel = new EmployeeModel(EMP_ID, EMP_NAME, MOBILE, ACTIVE);
                                EMP.add(employeeModel);
                            }

                            EMP_ADAPTER = new EmployeeAdapter(EmployeeMaster.this, EMP);
                            EMP_RECYCLER.setAdapter(EMP_ADAPTER);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EmployeeMaster.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(EmployeeMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmployeeMaster.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(EmployeeMaster.this);
        requestQueue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        select();
    }
}