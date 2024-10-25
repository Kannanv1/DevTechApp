package com.sundar.devtech;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.sundar.devtech.DatabaseController.RequestURL;
import com.sundar.devtech.Masters.MotorMaster;
import com.sundar.devtech.Scanner.CaptureAct;
import com.sundar.devtech.Services.CustomAlertDialog;

import java.util.HashMap;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity {

    private ImageView BACK_PRESS,Login_BTN;
    private TextView APPBAR_TITLE;
    private AppCompatButton scannerButton;
    private String employeeId;
    private boolean isDialogShowing = false;
    private boolean isExitConfirmed = false;
    public static String PREFS_NAME="myProfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Login_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText(getApplicationContext().getString(R.string.app_name));

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        BACK_PRESS.setVisibility(View.GONE);
        Login_BTN.setImageResource(R.drawable.settings);

        scannerButton = findViewById(R.id.barcode_btn);

        Login_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerActivity.this, LoginActivity.class));
            }
        });

        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanBarCode();
            }
        });

    }

    public void ScanBarCode(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan a barcode");
        options.setCameraId(0);  // Use the current camera (rear/front)
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
        result -> {
            if (result.getContents() == null) {
                Toast.makeText(ScannerActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                employeeId = result.getContents();
                userLogin();
            }
        });

    public void userLogin(){
        // Show progress dialog
        CustomAlertDialog dialog = new CustomAlertDialog(this);
        AlertDialog progressDialog = dialog.alterDialog();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.emp_login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equalsIgnoreCase("Login successfully")){

                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("EMPLOYEE_ID", employeeId);
                            editor.apply();
                            progressDialog.dismiss();

                            Intent intent = new Intent(ScannerActivity.this, SlotDetailActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            Toast.makeText(ScannerActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ScannerActivity.this, response, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(ScannerActivity.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScannerActivity.this, "ENetwork error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", employeeId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ScannerActivity.this);
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {

        if (!isDialogShowing && !isExitConfirmed) {
            isDialogShowing = true;

            // Create an AlertDialog to confirm exit
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Confirm Exit");
            adb.setMessage("Are you sure you want to exit?");
            adb.setCancelable(false);
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isDialogShowing = false;
                    isExitConfirmed = true; // Set the flag to indicate exit is confirmed
                    finish();
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isDialogShowing = false;
                }
            });
            AlertDialog alertDialog = adb.create();
            alertDialog.show();
        }
    }
}
