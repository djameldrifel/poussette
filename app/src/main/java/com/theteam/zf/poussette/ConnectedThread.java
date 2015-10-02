package com.theteam.zf.poussette;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by DAOUDR on 19/08/2015.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static ByteBuffer dbuf = ByteBuffer.allocateDirect(1024);
    //Handler create from the View to receive the messages from the client
    private Handler mHandler;
    public static final int MESSAGE_READ = 9771;

    public ConnectedThread(BluetoothSocket socket,Handler handler) {
        this.mHandler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[16];  // buffer store for the stream
        int bytes; // bytes returned from read()


        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                //Log.v(MainActivity.TAG, new String(buffer));
                if(buffer[1] == 0x5F) {
                    Log.v(MainActivity.TAG, "InputStream received 0x5F");

                    // Send the obtained bytes to the UI activity
                    Message message = mHandler.obtainMessage();

                    Bundle bundle = new Bundle();
                    bundle.putByteArray("key", buffer);

                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }

                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    public  void write(int Byte){
        try {
            mmOutStream.write(Byte);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}