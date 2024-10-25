package com.sundar.devtech.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import com.sundar.devtech.Masters.EmployeeMaster;
import com.sundar.devtech.Models.EmployeeModel;
import com.sundar.devtech.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private Context context;
    private List<EmployeeModel> employeeModels = new ArrayList<>();
    private TextInputEditText EMP_ID, EMP_NAME, EMP_MOBILE;
    private Spinner ACTIVE;
    private ArrayAdapter<String> active_adapter;
    private MaterialButton SAVE,CANCEL;
    private AlertDialog alertDialog;

    public EmployeeAdapter(Context context, List<EmployeeModel> employeeModels) {
        this.context = context;
        this.employeeModels = employeeModels;
    }

    // filter from search bar start
    public void setFilteredList(List<EmployeeModel> filteredList){
        this.employeeModels = filteredList;
        notifyDataSetChanged();
    }
    // filter from search bar End
    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee,parent,false);
        return new EmployeeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeAdapter.ViewHolder holder, int position) {
        final EmployeeModel employeeModel = employeeModels.get(position);
        holder.EMP_ID.setText(employeeModel.getEmp_id());
        holder.EMP_NAME.setText(employeeModel.getEmp_name());
        holder.EMP_MOBILE.setText(employeeModel.getMobile_no());

        if (employeeModel.getActive().equals("1")) {
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
                        Delete(employeeModel.getEmp_id());
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
                View view = LayoutInflater.from(context).inflate(R.layout.employee_dialog,
                        (LinearLayout)holder.itemView.findViewById(R.id.emp_dialog));
                builder.setView(view);

                alertDialog = builder.create();

                EMP_ID = view.findViewById(R.id.emp_id);
                EMP_NAME = view.findViewById(R.id.emp_name);
                EMP_MOBILE = view.findViewById(R.id.emp_mobile);
                ACTIVE = view.findViewById(R.id.emp_active);

                SAVE = view.findViewById(R.id.emp_insert_btn);
                CANCEL = view.findViewById(R.id.emp_cancel_btn);

                ((TextView) view.findViewById(R.id.dialog_title)).setText("Update Motor");
                SAVE.setText("Update");

                EMP_ID.setEnabled(false);

                active_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.active));
                active_adapter.setDropDownViewResource(R.layout.item_drop_down);
                ACTIVE.setAdapter(active_adapter);

                EMP_ID.setText(employeeModel.getEmp_id());
                EMP_NAME.setText(employeeModel.getEmp_name());
                EMP_MOBILE.setText(employeeModel.getMobile_no());

                String statusValue = employeeModel.getActive();
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
                        update(employeeModel.getEmp_id());
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
        return employeeModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView EMP_ID,EMP_NAME,EMP_MOBILE,ACTIVE;
        private ImageButton EDIT,DELETE;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            EMP_ID = itemView.findViewById(R.id.emp_id);
            EMP_NAME = itemView.findViewById(R.id.emp_name);
            EMP_MOBILE = itemView.findViewById(R.id.mobile_no);
            ACTIVE = itemView.findViewById(R.id.emp_active);
            EDIT = itemView.findViewById(R.id.emp_edit);
            DELETE = itemView.findViewById(R.id.emp_delete);
        }
    }

    public void Delete(String emp_id){

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.emp_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("Deleted Successfully!")){
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            ((EmployeeMaster) context).select();
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
                params.put("emp_id", emp_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public void update(String emp_id) {
        String emp_name = EMP_NAME.getText().toString().trim();
        String mobile = EMP_MOBILE.getText().toString().trim();
        String active = ACTIVE.getSelectedItem().toString().trim();

        if (active.equals("ENABLE")) {
            active = "1";
        }else {
            active = "2";
        }
        String finalActive = active;

        StringRequest request = new StringRequest(Request.Method.POST, RequestURL.emp_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equalsIgnoreCase("Update Successfully!")){
                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            ((EmployeeMaster) context).select();
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
                params.put("emp_id", emp_id);
                params.put("emp_name", emp_name);
                params.put("mobile",mobile);
                params.put("active", finalActive);
                params.put("user",EmployeeMaster.LOGGED_USER);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
