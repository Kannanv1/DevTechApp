package com.sundar.devtech.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.sundar.devtech.Masters.MotorMaster;
import com.sundar.devtech.Models.EmployeeModel;
import com.sundar.devtech.Models.MotorModel;
import com.sundar.devtech.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotorAdapter extends RecyclerView.Adapter<MotorAdapter.ViewHolder>{
    private Context context;
    private List<MotorModel> motorModels = new ArrayList<>();
    private AlertDialog alertDialog;
    private TextInputEditText MOTOR_NO, MOTOR_RUN_HEX, MOTOR_STATUS_HEX;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;

    public MotorAdapter(Context context, List<MotorModel> motorModels) {
        this.context = context;
        this.motorModels = motorModels;
    }
    // filter from search bar start
    public void setFilteredList(List<MotorModel> filteredList){
        this.motorModels = filteredList;
        notifyDataSetChanged();
    }
    // filter from search bar End
    @NonNull
    @Override
    public MotorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_motor,parent,false);
        return new MotorAdapter.ViewHolder(view);
    }

    
    @Override
    public void onBindViewHolder(@NonNull MotorAdapter.ViewHolder holder, int position) {
        final MotorModel motorModel = motorModels.get(position);

        holder.MOTOR_NO.setText(motorModel.getMotor_no());
        holder.RUN_HEX.setText(motorModel.getRun_hex());
        holder.STATUS_HEX.setText(motorModel.getStatus_hex());

        if (motorModel.getActive().equals("1")) {
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
                        Delete(motorModel.getMotor_id(),motorModel.getMotor_no());
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
                View view = LayoutInflater.from(context).inflate(R.layout.motor_dialog,
                        (LinearLayout)holder.itemView.findViewById(R.id.motor_dialog));
                builder.setView(view);

                alertDialog = builder.create();


                MOTOR_NO = view.findViewById(R.id.motor_no);
                MOTOR_RUN_HEX = view.findViewById(R.id.run_hex);
                MOTOR_STATUS_HEX = view.findViewById(R.id.status_hex);
                ACTIVE = view.findViewById(R.id.motor_active);

                SAVE = view.findViewById(R.id.motor_insert_btn);
                CANCEL = view.findViewById(R.id.motor_cancel_btn);

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Update Motor");
                SAVE.setText("Update");

                active_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.active));
                active_adapter.setDropDownViewResource(R.layout.item_drop_down);
                ACTIVE.setAdapter(active_adapter);

                MOTOR_NO.setText(motorModel.getMotor_no());
                MOTOR_RUN_HEX.setText(motorModel.getRun_hex());
                MOTOR_STATUS_HEX.setText(motorModel.getStatus_hex());

                String statusValue = motorModel.getActive();
                if (statusValue.equals("1")) {
                    statusValue = "ENABLE";
                } else if (statusValue.equals("2")) {
                    statusValue = "DISABLE";
                }
                int position = active_adapter.getPosition(statusValue);
                ACTIVE.setSelection(position);

                SAVE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       update(motorModel.getMotor_id(),motorModel.getMotor_no());
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
        return motorModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView MOTOR_NO,RUN_HEX,STATUS_HEX,ACTIVE;
        private ImageButton EDIT,DELETE;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            MOTOR_NO = itemView.findViewById(R.id.motorNo);
            RUN_HEX = itemView.findViewById(R.id.motor_run_hex);
            STATUS_HEX = itemView.findViewById(R.id.motor_status_hex);
            ACTIVE = itemView.findViewById(R.id.motor_active);
            EDIT = itemView.findViewById(R.id.motor_edit);
            DELETE = itemView.findViewById(R.id.motor_delete);
        }
    }

    public void Delete(String motor_id,String motor_no){

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.motor_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("Deleted Successfully!")){
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            ((MotorMaster) context).select();
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
                params.put("motor_id", motor_id);
                params.put("motor_no", motor_no);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public void update(String motor_id,String motor_no) {

        String run_hex = MOTOR_RUN_HEX.getText().toString().trim();
        String status_hex = MOTOR_STATUS_HEX.getText().toString().trim();
        String active = ACTIVE.getSelectedItem().toString().trim();

        if (active.equals("ENABLE")) {
            active = "1";
        }else {
            active = "2";
        }
        String finalActive = active;

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.motor_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        if (response.trim().equalsIgnoreCase("Update Successfully!")){
                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            ((MotorMaster) context).select();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Updated Failed", Toast.LENGTH_SHORT).show();
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
                params.put("motor_id", motor_id);
                params.put("motor_no", motor_no);
                params.put("run_hex", run_hex);
                params.put("status_hex",status_hex);
                params.put("active",finalActive);
                params.put("user",MotorMaster.LOGGED_USER);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
