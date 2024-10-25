package com.sundar.devtech.Models;

public class MotorModel {
    private String motor_id,motor_no,run_hex,status_hex,active;
    public MotorModel() {
        super();
    }

    public MotorModel(String motor_id, String motor_no, String run_hex, String status_hex, String active) {
        this.motor_id = motor_id;
        this.motor_no = motor_no;
        this.run_hex = run_hex;
        this.status_hex = status_hex;
        this.active = active;
    }

    public MotorModel(String run_hex, String status_hex) {
        this.run_hex = run_hex;
        this.status_hex = status_hex;
    }
    public String getMotor_id() {
        return motor_id;
    }

    public void setMotor_id(String motor_id) {
        this.motor_id = motor_id;
    }

    public String getMotor_no() {
        return motor_no;
    }

    public void setMotor_no(String motor_no) {
        this.motor_no = motor_no;
    }

    public String getRun_hex() {
        return run_hex;
    }

    public void setRun_hex(String run_hex) {
        this.run_hex = run_hex;
    }

    public String getStatus_hex() {
        return status_hex;
    }

    public void setStatus_hex(String status_hex) {
        this.status_hex = status_hex;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
