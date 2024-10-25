package com.sundar.devtech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sundar.devtech.DatabaseController.UserController;

public class LoginActivity extends AppCompatActivity {
    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE;
    private TextInputEditText USER_NAME,USER_PASS;
    private ImageView PASS_ICON;
    private boolean passwordShowing = false;
    private AppCompatButton LOGIN_BTN;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userController = new UserController(LoginActivity.this);

        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText(getApplicationContext().getString(R.string.app_name));

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        APPBAR_BTN.setVisibility(View.GONE);
        BACK_PRESS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ScannerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        USER_NAME = findViewById(R.id.user_name);
        USER_PASS = findViewById(R.id.user_pass);
        PASS_ICON = findViewById(R.id.password_icon);
        LOGIN_BTN = findViewById(R.id.login);

        PASS_ICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // password is show or not
                if (passwordShowing){
                    passwordShowing = false;
                    USER_PASS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    PASS_ICON.setImageResource(R.drawable.eye);
                }else {
                    passwordShowing = true;
                    USER_PASS.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    PASS_ICON.setImageResource(R.drawable.eye_off);
                }
                USER_PASS.setSelection(USER_PASS.length());
            }
        });

        LOGIN_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserName = USER_NAME.getText().toString();
                String UserPass = USER_PASS.getText().toString();

                String response = userController.loginUsers(UserName,UserPass);
                if (response.equals("Login Successfully")){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}