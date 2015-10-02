package com.theteam.zf.poussette;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by DAOUDR on 19/08/2015.
 */
class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread exchangingMessages;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    public ConnectThread(BluetoothDevice device,BluetoothAdapter mBluetoothAdapter,Handler handler) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        mHandler = handler;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket,mHandler);
    }

    public void manageConnectedSocket(BluetoothSocket socket,Handler handler){
        Log.v(MainActivity.TAG, "manageConnectedSocket");
        exchangingMessages = new ConnectedThread(socket,handler);
        exchangingMessages.start();
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes){
        exchangingMessages.write(bytes);
    }

    public  void write(int Byte){
        exchangingMessages.write(Byte);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
