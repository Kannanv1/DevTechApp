package com.sundar.devtechapp.Communications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.moxa.mxuportapi.MxUPort;

import java.util.List;

public class Broadcast {
//
//    private static final String ACTION_USB_PERMISSION = "com.sundar.devtechapp.Communications.USB_PERMISSION";
//    private List<MxUPort> mPortList = null;
//    private MxUPort mCurrentUPort = null;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//
//        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//
//            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//            boolean isDetached = false;
//            int deviceId = device.getDeviceId();
//
//            for (MxUPort p : mPortList) {
//                if (p.getUsbDevice().getDeviceId() == deviceId) {
//                    isDetached = true;
//                }
//            }
//
//            if (mCurrentUPort != null) {
//                if (mCurrentUPort.getUsbDevice().getDeviceId() == deviceId) {
//                    doClosePort();
//                }
//            }
//
//            if (isDetached) {
//                Toast.makeText(context, "USB device detached!", Toast.LENGTH_SHORT).show();
//                mPortListAdapter.clear();
//                MxUPortService.requestPermission(context, mUsbManager, ACTION_USB_PERMISSION, 0, 0, mPermissionReceiver);
//            }
//
//        }
//    }
//    private void doClosePort() {
//        mCloseButton.setEnabled(false);
//
//        mExitThread = true;
//        if (mCurrentUPort != null) {
//            try {
//                mCurrentUPort.close();
//                mCurrentUPort = null;
//            } catch (MxException e) {
//                mLogText.append("Failed to close\n");
//            }
//        }
//    }
}
