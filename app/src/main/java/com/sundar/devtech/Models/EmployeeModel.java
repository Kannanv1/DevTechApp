package com.sundar.devtech.Models;

public class EmployeeModel {
    private String emp_id,emp_name,mobile_no,active;

    public EmployeeModel(String emp_id, String emp_name, String mobile_no,String active) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.mobile_no = mobile_no;
        this.active = active;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
