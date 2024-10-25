package com.sundar.devtech.Services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.sundar.devtech.Interfaces.MotorCommandCallback;
import com.sundar.devtech.Interfaces.MotorController;

import java.io.IOException;

public class MotorService implements MotorController {

    private UsbManager mUsbManager;
    private static final String ACTION_USB_PERMISSION = "com.sundar.devtech.Services.USB_PERMISSION";
    private UsbDevice targetDevice = null;
    private Context context;
    private USBDevices usbDevices;

    public MotorService(Context context){
        this.context = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        usbDevices = new USBDevices(context);
    }

    @Override
    public boolean FindUSBDevice() {
        for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            if (usbDevices.AvailableUSBDevices(usbDevice)) {
                targetDevice = usbDevice;
                break;
            }
        }

        if (targetDevice != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            mUsbManager.requestPermission(targetDevice, permissionIntent);
            return true;
        } else {
            Toast.makeText(context, "No USB device connected.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void MotorOn(String startCommand, String statusCommand, MotorCommandCallback callback) {
       StartCommand("01 05 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 70 48");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String result = StatusCommand("01 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 D0 E8");
                if (callback != null) {
                    callback.onStatusCommandResult(result);
                }
            }
        }, 5000);
    }


    @Override
    public String StartCommand(String startCommand) {
        return executeCommand(startCommand);
    }

    @Override
    public String StatusCommand(String statusCommand) {
        return executeCommand(statusCommand);
    }

    private String executeCommand(String command) {
        if (!FindUSBDevice()) return "Not Connected!";

        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(targetDevice);
        if (driver == null) {
            Toast.makeText(context, "Failed to find USB driver.", Toast.LENGTH_SHORT).show();
            return "Failed to find USB driver.";
        }

        try (UsbSerialPort serialPort = driver.getPorts().get(0)) {
            serialPort.open(mUsbManager.openDevice(targetDevice));
            serialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            byte[] commandBytes = ByteConvertor.hexStringToByteArray(command.replaceAll(" ", ""));
            usbDevices.sendCommand(serialPort, commandBytes);

            CustomAlertDialog alertDialog = new CustomAlertDialog(context);
            AlertDialog progressDialog = alertDialog.alterDialog();
            progressDialog.show();

            String response = usbDevices.listenForResponse(serialPort);
            progressDialog.dismiss();

            return response;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error opening serial port.", Toast.LENGTH_SHORT).show();
            return "Connection Error!";
        }
    }
}
