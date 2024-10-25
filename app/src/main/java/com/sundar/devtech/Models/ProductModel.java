package com.sundar.devtech.Models;

public class ProductModel {
    private String motor_no,prod_id,prod_name,prod_spec,prod_desc,prod_qty,active,prod_image;

    public ProductModel(String motor_no, String prod_id, String prod_name, String prod_spec, String prod_desc, String prod_qty, String active, String prod_image) {
        this.motor_no = motor_no;
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_spec = prod_spec;
        this.prod_desc = prod_desc;
        this.prod_qty = prod_qty;
        this.active = active;
        this.prod_image = prod_image;
    }

    public ProductModel(String motor_no, String prod_name, String prod_spec, String prod_desc, String prod_image) {
        this.motor_no = motor_no;
        this.prod_name = prod_name;
        this.prod_spec = prod_spec;
        this.prod_desc = prod_desc;
        this.prod_image = prod_image;
    }

    public String getMotor_no() {
        return motor_no;
    }

    public void setMotor_no(String motor_no) {
        this.motor_no = motor_no;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_spec() {
        return prod_spec;
    }

    public void setProd_spec(String prod_spec) {
        this.prod_spec = prod_spec;
    }

    public String getProd_desc() {
        return prod_desc;
    }

    public void setProd_desc(String prod_desc) {
        this.prod_desc = prod_desc;
    }

    public String getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getProd_image() {
        return prod_image;
    }

    public void setProd_image(String prod_image) {
        this.prod_image = prod_image;
    }
}
