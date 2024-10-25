package com.sundar.devtech.Adapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import com.sundar.devtech.Masters.EmployeeMaster;
import com.sundar.devtech.Masters.ProductMaster;
import com.sundar.devtech.Models.ProductModel;
import com.sundar.devtech.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {

    private Context context;
    private List<ProductModel> productModels = new ArrayList<>();

    private TextView MOTOR_NO;
    private TextInputEditText PRODUCT_NAME, SPECIFICATION, DESCRIPTION, QTY;
    private Button PRODUCT_IMAGE_BTN,FULL_LOAD;
    private ImageView IMAGE;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;
    private AlertDialog alertDialog;
    Bitmap bitmap;
    private Dialog DIALOG;
    private ArrayAdapter<String> adapter;
    private static final int GALLERY_REQ_CODE = 1;
    public ProductAdapter(Context context, List<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }
    // filter from search bar start
    public void setFilteredList(List<ProductModel> filteredList){
        this.productModels = filteredList;
        notifyDataSetChanged();
    }
    // filter from search bar End

    @NonNull
    @Override
    public ProductAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product,parent,false);
        return new ProductAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.viewHolder holder, int position) {
        final ProductModel productModel = productModels.get(position);

        holder.MOTOR_NO.setText(productModel.getMotor_no());
        holder.PROD_NAME.setText(productModel.getProd_name());
        holder.PROD_DESC.setText(productModel.getProd_desc());
        holder.PROD_SPEC.setText(productModel.getProd_spec());
        holder.QTY.setText(productModel.getProd_qty());

        if (productModel.getProd_image() != null && !productModel.getProd_image().isEmpty()) {
            Bitmap bitmap = decodeBase64(productModel.getProd_image());
            holder.PROD_IMAGE.setImageBitmap(bitmap);
        } else {
            holder.PROD_IMAGE.setImageResource(R.drawable.logo);
        }

        if (productModel.getActive().equals("1")) {
            holder.ACTIVE.setText("ENABLE");
        } else {
            holder.ACTIVE.setText("DISABLE");
        }

        holder.DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(context).inflate(R.layout.warning_dialog,
                        (ConstraintLayout) holder.itemView.findViewById(R.id.warning_dialog));

                builder.setView(view);
                alertDialog = builder.create();
                ((TextView) view.findViewById(R.id.dialog_title)).setText("DELETE");
                ((TextView) view.findViewById(R.id.dialog_message)).setText("Are you sure you want to Delete");
                ((Button) view.findViewById(R.id.dialog_cancel)).setText("NO");
                ((Button) view.findViewById(R.id.dialog_submit)).setText("YES");

                view.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                view.findViewById(R.id.dialog_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Delete(productModel.getProd_id());
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();

            }

        });
        holder.EDIT.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(context).inflate(R.layout.product_dialog,
                        (LinearLayout)holder.itemView.findViewById(R.id.prod_dialog));
                builder.setView(view);

                alertDialog = builder.create();

                MOTOR_NO = view.findViewById(R.id.motor_no);
                PRODUCT_NAME = view.findViewById(R.id.prod_name);
                SPECIFICATION = view.findViewById(R.id.prod_sepc);
                DESCRIPTION = view.findViewById(R.id.prod_desc);
                QTY = view.findViewById(R.id.prod_qty);
                FULL_LOAD = view.findViewById(R.id.full_load_btn);
                IMAGE = view.findViewById(R.id.prod_image);
                PRODUCT_IMAGE_BTN = view.findViewById(R.id.prod_image_btn);
                ACTIVE = view.findViewById(R.id.prod_active);

                SAVE = view.findViewById(R.id.prod_insert_btn);
                CANCEL = view.findViewById(R.id.prod_cancel_btn);

                active_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.active));
                active_adapter.setDropDownViewResource(R.layout.item_drop_down);
                ACTIVE.setAdapter(active_adapter);

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Update Product");
                SAVE.setText("Update");

                MOTOR_NO.setText("Motor - "+productModel.getMotor_no());
                PRODUCT_NAME.setText(productModel.getProd_name());
                SPECIFICATION.setText(productModel.getProd_spec());
                DESCRIPTION.setText(productModel.getProd_desc());
                QTY.setText(productModel.getProd_qty());

                if (productModel.getProd_image() != null && !productModel.getProd_image().isEmpty()) {
                    Bitmap bitmap = decodeBase64(productModel.getProd_image());
                    IMAGE.setImageBitmap(bitmap);
                } else {
                    IMAGE.setImageResource(R.drawable.logo);
                }

                String statusValue = productModel.getActive();
                if (statusValue.equals("1")) {
                    statusValue = "ENABLE";
                } else if (statusValue.equals("2")) {
                    statusValue = "DISABLE";
                }
                int position = active_adapter.getPosition(statusValue);
                ACTIVE.setSelection(position);

                MOTOR_NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       fetchMotor();
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
                        startActivityForResult((ProductMaster) context, intent, GALLERY_REQ_CODE, null);
                    }
                });

                SAVE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        update(productModel.getProd_id());
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
    public int getItemCount() {
        return productModels.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView MOTOR_NO, PROD_NAME, PROD_DESC, PROD_SPEC, QTY, ACTIVE;
        private ImageView PROD_IMAGE;
        private ImageButton EDIT, DELETE;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            MOTOR_NO = itemView.findViewById(R.id.prod_motor_no);
            PROD_NAME = itemView.findViewById(R.id.prod_name);
            PROD_DESC = itemView.findViewById(R.id.prod_spec);
            PROD_SPEC = itemView.findViewById(R.id.prod_desc);
            QTY = itemView.findViewById(R.id.qty);
            PROD_IMAGE = itemView.findViewById(R.id.prod_image);
            ACTIVE = itemView.findViewById(R.id.prod_active);
            EDIT = itemView.findViewById(R.id.prod_edit);
            DELETE = itemView.findViewById(R.id.prod_delete);
        }
    }

    // Helper method to decode base64 string to a Bitmap
    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void Delete(String prod_id){

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.prod_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("Deleted Successfully!")){
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            ((ProductMaster) context).select();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Item deleted Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(context, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("prod_id", prod_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public void update(String prod_id) {
        String motor_no = MOTOR_NO.getText().toString().trim().replace("Motor - ", "");
        String prod_name = PRODUCT_NAME.getText().toString().trim();
        String prod_spec = SPECIFICATION.getText().toString().trim();
        String prod_desc = DESCRIPTION.getText().toString().trim();
        String prod_qty = QTY.getText().toString().trim();

        if (prod_qty.isEmpty() || Integer.parseInt(prod_qty) < 1 || Integer.parseInt(prod_qty) > 11) {
            QTY.setError("Please enter a valid quantity between 01 and 11");
            return;
        }

        String active = ACTIVE.getSelectedItem().toString().trim().equals("ENABLE") ? "1" : "2";

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.prod_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        if (response.trim().equalsIgnoreCase("Update Successfully!")) {
                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            ((ProductMaster) context).select();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                    Toast.makeText(context, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("prod_id", prod_id);
                params.put("motor_no", motor_no);
                params.put("prod_name", prod_name);
                params.put("prod_spec", prod_spec);
                params.put("prod_desc", prod_desc);
                params.put("prod_qty", prod_qty);
                params.put("prod_image", imageString(bitmap));
                params.put("active", active);
                params.put("user", ProductMaster.LOGGED_USER);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
    public void fetchMotor() {
        DIALOG = new Dialog(context);
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

        // Initialize the list outside the loop
        List<String> motorList = ProductMaster.motorList;
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, motorList);
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
    public String imageString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imaByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imaByte,Base64.DEFAULT).trim();
    }
}
