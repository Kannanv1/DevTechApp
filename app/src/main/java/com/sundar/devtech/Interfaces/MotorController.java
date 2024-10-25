package com.sundar.devtech.Interfaces;

public interface MotorController {
    boolean FindUSBDevice();
    void MotorOn(String startCommand, String statusCommand, MotorCommandCallback callback); // Change to void
    String StartCommand(String startCommand);
    String StatusCommand(String statusCommand);
}

