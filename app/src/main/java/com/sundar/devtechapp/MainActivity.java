package com.sundar.devtechapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button connectButton, onButton;
    private Spinner port;
    private ArrayAdapter<String> serial_adapter;
    private UsbManager mUsbManager;
    private static final String ACTION_USB_PERMISSION = "com.sundar.devtechapp.USB_PERMISSION";
    private UsbDevice targetDevice = null; // Store the found device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        connectButton = findViewById(R.id.connect);
        onButton = findViewById(R.id.On);
        port = findViewById(R.id.port);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToUsbDevice();
            }
        });

        serial_adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.port));
        serial_adapter.setDropDownViewResource(R.layout.item_drop_down);
        port.setAdapter(serial_adapter);


        // Button to send command to the connected device
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetDevice != null) {
                    connectToMotor("01 05 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 70 48");
                } else {
                    Toast.makeText(MainActivity.this, "No RS-232 device found or no serial port detected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to connect to the USB device
    private void connectToUsbDevice() {
        for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            int vendorId = usbDevice.getVendorId();
            int productId = usbDevice.getProductId();

//            // Display Vendor and Product IDs
//            Toast.makeText(MainActivity.this, "Vendor ID: " + vendorId, Toast.LENGTH_LONG).show();
//            Toast.makeText(MainActivity.this, "Product ID: " + productId, Toast.LENGTH_LONG).show();

            // Check if the device matches the RS-232 criteria
            if (isRS232Device(usbDevice)) {
                targetDevice = usbDevice; // Store the found device
                break;
            }
        }

        if (targetDevice != null) {
            // Request permission to access the USB device
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    MainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            mUsbManager.requestPermission(targetDevice, permissionIntent);

            // Show the vendor ID and product ID in a Toast message
            Toast.makeText(MainActivity.this, "Device found! Vendor ID: " + targetDevice.getVendorId() + ", Product ID: " + targetDevice.getProductId(), Toast.LENGTH_LONG).show();

        } else {
            // Handle case where no device is connected
            Toast.makeText(MainActivity.this, "No RS-232 device found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if the USB device is an RS-232 compatible device
    private boolean isRS232Device(UsbDevice device) {
        int vendorId = device.getVendorId();
        int productId = device.getProductId();

        return (vendorId == 4362 && productId == 4368) || (vendorId == 0x110A && productId == 0x1110) || (vendorId == 0x10C4 && (productId == 0xEA60 || productId == 0xEA70 || productId == 0xEA71)) ||
                (vendorId == 0x0403 && (productId == 0x6001 || productId == 0x6010 || productId == 0x6011 || productId == 0x6014 || productId == 0x6015)) ||
                (vendorId == 0x067B && (productId == 0x2303 || productId == 0x23A3 || productId == 0x23B3 || productId == 0x23C3 || productId == 0x23D3 || productId == 0x23E3 || productId == 0x23F3)) ||
                (vendorId == 0x1A86 && (productId == 0x5523 || productId == 0x7523 || productId == 0x55D4));
    }


    // Method to directly access and communicate with the USB device (RS-232)
    private void connectToMotor(String command) {
        try {
            String ports = port.getSelectedItem().toString().trim();
            String commPort = "/dev/"+ports;

            SerialPort serialPort = SerialPort.getCommPort(commPort);
            serialPort.setBaudRate(9600);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(1);
            serialPort.setParity(SerialPort.NO_PARITY);

            if (serialPort.openPort()) {
                Toast.makeText(MainActivity.this, "device connected!", Toast.LENGTH_SHORT).show();

                byte[] commandBytes = hexStringToByteArray(command.replaceAll(" ", ""));
                sendCommand(serialPort, commandBytes); // Send command to the device

                listenForResponse(serialPort); // Listen for response
            } else {
                Toast.makeText(MainActivity.this, "Failed to connect to RS-232 device.", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Send the command to the motor control board
    private void sendCommand(SerialPort serialPort, byte[] command) {
        try {
            OutputStream outputStream = serialPort.getOutputStream();
            outputStream.write(command);
            outputStream.flush();
            Toast.makeText(this, "Command sent!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
        }
    }

    // Listen for a response from the motor control board
    private void listenForResponse(SerialPort serialPort) {
        try {
            byte[] buffer = new byte[20]; // Assuming 20 bytes response based on protocol
            int bytesRead = serialPort.getInputStream().read(buffer);
            if (bytesRead > 0) {
                Toast.makeText(this, "Response received: " + byteArrayToHex(buffer), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No response received", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read response", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert hex string to byte array
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // Helper method to convert byte array to hex string for display
    private String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
