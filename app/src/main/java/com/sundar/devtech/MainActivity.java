package com.sundar.devtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.sundar.devtech.Internet.NetworkChangeListener;
import com.sundar.devtech.Masters.EmployeeMaster;
import com.sundar.devtech.Masters.MotorMaster;
import com.sundar.devtech.Masters.ProductMaster;
import com.sundar.devtech.Masters.ReportsActivity;
import com.sundar.devtech.Services.DateAndTime;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageButton burgerIcon;
    private TextView DATE,TIME;
    private String currentTime;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isDialogShowing = false;
    private boolean isExitConfirmed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.side_navigation);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        //header image view start
        View nav_head = navigationView.getHeaderView(0);
        burgerIcon = findViewById(R.id.burgerIcon);
        drawer = findViewById(R.id.drawer_layout);

        DATE = findViewById(R.id.date);
        TIME = findViewById(R.id.time);

        DATE.setText(DateAndTime.getDateAndDay());
        startLiveTime();

        burgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });


    }

    private void startLiveTime() {
       runnable = new Runnable() {
            @Override
            public void run() {
                currentTime = DateAndTime.getTimeAndMarker();
                TIME.setText(currentTime);
                // Repeat this runnable code block again every 1000 milliseconds (1 second)
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        IntentFilter filter =new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkChangeListener,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
//        unregisterReceiver(networkChangeListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_prod_master) {
            startActivity(new Intent(MainActivity.this, ProductMaster.class));
        }else if (id == R.id.nav_emp_master){
            startActivity(new Intent(MainActivity.this, EmployeeMaster.class));
        }else if (id == R.id.nav_motor_master){
            startActivity(new Intent(MainActivity.this, MotorMaster.class));
        }else if (id == R.id.reports) {
            startActivity(new Intent(MainActivity.this, ReportsActivity.class));
        }else if (id == R.id.nav_logout) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
            View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.warning_dialog,
                    (ConstraintLayout)findViewById(R.id.warning_dialog));

            builder.setView(view1);
            ((TextView) view1.findViewById(R.id.dialog_title)).setText("Logout");
            ((TextView) view1.findViewById(R.id.dialog_message)).setText("Are you sure you want to logout");
            ((Button) view1.findViewById(R.id.dialog_cancel)).setText("NO");
            ((Button) view1.findViewById(R.id.dialog_submit)).setText("YES");
            ((ImageView) view1.findViewById(R.id.dialog_image)).setImageResource(R.drawable.logout);

            final android.app.AlertDialog alertDialog = builder.create();

            view1.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            view1.findViewById(R.id.dialog_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
                    finish();

                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
            if (alertDialog.getWindow() !=null){
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            alertDialog.show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
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
}