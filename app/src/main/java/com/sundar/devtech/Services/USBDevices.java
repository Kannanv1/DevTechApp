package com.sundar.devtech.Services;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;

public class USBDevices {

    private Context context;

    public USBDevices(Context context){
        this.context=context;
    }

    public boolean AvailableUSBDevices(UsbDevice device) {
        int vendorId = device.getVendorId();
        int productId = device.getProductId();

        return (vendorId == 4362 && productId == 4368) || (vendorId == 4292 && productId == 60000) ||
                (vendorId == 0x110A && productId == 0x1110) ||
                (vendorId == 0x10C4 && (productId == 0xEA60 || productId == 0xEA70 || productId == 0xEA71)) ||
                (vendorId == 0x0403 && (productId == 0x6001 || productId == 0x6010 || productId == 0x6011 || productId == 0x6014 || productId == 0x6015)) ||
                (vendorId == 0x067B && (productId == 0x2303 || productId == 0x23A3 || productId == 0x23B3 || productId == 0x23C3 || productId == 0x23D3 || productId == 0x23E3 || productId == 0x23F3)) ||
                (vendorId == 0x1A86 && (productId == 0x5523 || productId == 0x7523 || productId == 0x55D4));
    }

    public void sendCommand(UsbSerialPort serialPort, byte[] command) {
        try {
            serialPort.write(command, command.length);
            Toast.makeText(context, "Command sent!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send command", Toast.LENGTH_SHORT).show();
        }
    }

    public String listenForResponse(UsbSerialPort serialPort) {
        try {
            byte[] buffer = new byte[20]; // Adjust size based on expected response
            int bytesRead = serialPort.read(buffer, 3000); // Read with a timeout
            if (bytesRead > 0) {
                return ByteConvertor.byteArrayToHex(buffer);
            } else {
                Toast.makeText(context, "No response received", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to read response", Toast.LENGTH_SHORT).show();
        }
        return "no response";
    }
}
