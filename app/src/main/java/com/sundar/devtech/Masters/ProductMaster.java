package com.sundar.devtech.Masters;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.sundar.devtech.Adapter.ProductAdapter;
import com.sundar.devtech.DatabaseController.RequestURL;
import com.sundar.devtech.Models.ProductModel;
import com.sundar.devtech.R;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductMaster extends AppCompatActivity {
    private ImageView BACK_PRESS,APPBAR_BTN;
    private TextView APPBAR_TITLE,MOTOR_NO;
    private TextInputEditText PRODUCT_NAME, SPECIFICATION, DESCRIPTION, QTY;
    private ImageView PROD_IMAGE;
    private Button PRODUCT_IMAGE_BTN,FULL_LOAD;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;
    private static final int GALLERY_REQ_CODE = 1;
    private static final int CROP_IMAGE_REQ_CODE = 2;
    Bitmap bitmap;
    private RecyclerView PROD_RECYCLER;
    private RecyclerView.LayoutManager PROD_MANAGER;
    private List<ProductModel> PROD;
    private ProductAdapter PROD_ADAPTER;
    public static String LOGGED_USER = null;
    private TextInputEditText SEARCHVIEW;
    private Dialog DIALOG;
    private ArrayAdapter<String> adapter;
    public static List<String> motorList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_master);

        APPBAR_BTN = findViewById(R.id.appbar_btn);
        APPBAR_TITLE = findViewById(R.id.appbarTitle);
        APPBAR_TITLE.setText("Product Master");

        //back press activity
        BACK_PRESS = findViewById(R.id.backPress);
        APPBAR_BTN.setImageResource(R.drawable.add);
        BACK_PRESS.setOnClickListener(view -> ProductMaster.super.onBackPressed());

        PROD_RECYCLER = findViewById(R.id.product_recycler);
        PROD_MANAGER = new GridLayoutManager(ProductMaster.this, 1);
        PROD_RECYCLER.setLayoutManager(PROD_MANAGER);
        PROD = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        if (userId != null) {
            LOGGED_USER = userId;
        }else {
            LOGGED_USER = "admin";
        }

        /* filter from search bar start*/
        SEARCHVIEW = findViewById(R.id.searchView_prod);
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

                List<ProductModel> filteredList = new ArrayList<>();
                for (ProductModel item : PROD) {
                    if (item.getMotor_no().contains(text.toLowerCase()) ||
                            item.getProd_name().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(ProductMaster.this, "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    PROD_ADAPTER.setFilteredList(filteredList);
                }
            }
        });

        /* filter from search bar End*/

        APPBAR_BTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductMaster.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(ProductMaster.this).inflate(R.layout.product_dialog,
                        (LinearLayout)findViewById(R.id.prod_dialog));
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Add New Product");

                MOTOR_NO = view.findViewById(R.id.motor_no);
                PRODUCT_NAME = view.findViewById(R.id.prod_name);
                SPECIFICATION = view.findViewById(R.id.prod_sepc);
                DESCRIPTION = view.findViewById(R.id.prod_desc);
                QTY = view.findViewById(R.id.prod_qty);
                FULL_LOAD = view.findViewById(R.id.full_load_btn);
                PROD_IMAGE = view.findViewById(R.id.prod_image);
                PRODUCT_IMAGE_BTN = view.findViewById(R.id.prod_image_btn);
                ACTIVE = view.findViewById(R.id.prod_active);

                SAVE = view.findViewById(R.id.prod_insert_btn);
                CANCEL = view.findViewById(R.id.prod_cancel_btn);

                active_adapter = new ArrayAdapter<>(ProductMaster.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.active));
                active_adapter.setDropDownViewResource(R.layout.item_drop_down);
                ACTIVE.setAdapter(active_adapter);

                MOTOR_NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DIALOG = new Dialog(ProductMaster.this);
                        DIALOG.setContentView(R.layout.dialog_search_spinner);

                        // Set dialog width to match parent
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(DIALOG.getWindow().getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = 1000;
                        DIALOG.getWindow().setAttributes(layoutParams);

                        DIALOG.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        DIALOG.show();

                        TextView Tittle = DIALOG.findViewById(R.id.dialog_spinner_title);
                        Tittle.setText("Select Motor");
                        EditText editText = DIALOG.findViewById(R.id.spinner_search);
                        ListView listView = DIALOG.findViewById(R.id.spinner_list);

                        adapter = new ArrayAdapter<>(ProductMaster.this, android.R.layout.simple_list_item_1, motorList);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedMotor = adapter.getItem(position);

                                MOTOR_NO.setText(selectedMotor);

                                DIALOG.dismiss();
                            }
                        });
                    }
                });

                QTY.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s != null && s.length() > 1) {
                            try {
                                // Parse the input as an integer
                                int input = Integer.parseInt(s.toString());

                                // Validate if the number is between 00 and 11
                                if (input < 1 || input > 11) {
                                    QTY.setError("Please enter a number between 01 and 11");
                                } else if (s.length() < 2) {
                                    QTY.setError("Enter a 2-digit number (e.g.,01,02,03)");
                                } else {
                                    QTY.setError(null); // Clear any previous error
                                }
                            } catch (NumberFormatException e) {
                                QTY.setError("Invalid input");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                FULL_LOAD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QTY.setText("11");
                    }
                });

                PRODUCT_IMAGE_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY_REQ_CODE, null);
                    }
                });

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), "croppedImage.jpg")))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(800, 800)
                        .start(this, CROP_IMAGE_REQ_CODE);
            }
        }

        if (requestCode == CROP_IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            Uri croppedImageUri = UCrop.getOutput(data);
            if (croppedImageUri != null) {
                PROD_IMAGE.setImageURI(croppedImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String imageString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imaByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imaByte,Base64.DEFAULT).trim();
    }

    public void insert() {
        String motor_no = MOTOR_NO.getText().toString().trim().replace("Motor - ","");
        String prod_name = PRODUCT_NAME.getText().toString().trim();
        String prod_spec = SPECIFICATION.getText().toString().trim();
        String prod_desc = DESCRIPTION.getText().toString().trim();
        String prod_qty = QTY.getText().toString().trim();
        String active = ACTIVE.getSelectedItem().toString().trim().equals("ENABLE") ? "1" : "2";

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.prod_insert,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("success")){
                            Toast.makeText(ProductMaster.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();

                            MOTOR_NO.setText("");
                            PRODUCT_NAME.setText("");
                            SPECIFICATION.setText("");
                            DESCRIPTION.setText("");
                            QTY.setText("");
                            MOTOR_NO.requestFocus();
                            select();
                        }
                        else {
                            Toast.makeText(ProductMaster.this, response, Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(ProductMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductMaster.this, "ENetwork error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("motor_no", motor_no);
                params.put("prod_name", prod_name);
                params.put("prod_spec", prod_spec);
                params.put("prod_desc", prod_desc);
                params.put("prod_qty", prod_qty);
                params.put("prod_image",imageString(bitmap));
                params.put("active", active);
                params.put("user",LOGGED_USER);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProductMaster.this);
        requestQueue.add(request);
    }

    public void fetchMotor() {
        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.motorNo_fetch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        motorList.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String motor_no = object.getString("motor_no");

                                motorList.add("Motor - "+motor_no);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductMaster.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(ProductMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductMaster.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ProductMaster.this);
        requestQueue.add(request);

    }

    public void select() {

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.prod_fetch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            PROD.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String MOTOR_NO = object.getString("motor_no");
                                String PROD_ID = object.getString("prod_id");
                                String PROD_NAME = object.getString("prod_name");
                                String PROD_SPEC = object.getString("prod_spec");
                                String PROD_DESC = object.getString("prod_desc");
                                String QTY = object.getString("qty");
                                String IMAGE = object.getString("image");
                                String ACTIVE = object.getString("active");

                                ProductModel productModel = new ProductModel(MOTOR_NO, PROD_ID, PROD_NAME, PROD_SPEC, PROD_DESC, QTY, ACTIVE, IMAGE);
                                PROD.add(productModel);
                            }

                            PROD_ADAPTER = new ProductAdapter(ProductMaster.this, PROD);
                            PROD_RECYCLER.setAdapter(PROD_ADAPTER);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductMaster.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(ProductMaster.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductMaster.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ProductMaster.this);
        requestQueue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        select();
        fetchMotor();
    }
}