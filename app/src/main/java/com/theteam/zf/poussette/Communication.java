package com.theteam.zf.poussette;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by DAOUDR on 26/08/2015.
 */
public class Communication {
    public final static int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter mBluetoothAdapter;
    private ConnectThread connectThread;
    private Boolean connected = false;
    private String TAG;
    private Activity parent;

    private ArrayList<CustomEventListener> listeners;

    public Communication(Activity parent) {
        TAG = MainActivity.TAG;
        this.parent = parent;
        listeners = new ArrayList<>();
    }

    public boolean sendPayload(Integer payload){
        if(connected)
        connectThread.write(payload.byteValue());

        return true;
    }


    public void checkfordevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getAddress().equals("00:0B:CE:08:4D:0F")) {
                    //The handler will receive the messages from the bluetooth device
                    connectThread = new ConnectThread(device,mBluetoothAdapter,new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message inputMessage) {
                            if(!connected){
                                Log.v(MainActivity.TAG,"Bluetooth Connected");
                                connected = true;
                                fireListeners(CustomEventListener.BLUETOOTH_PAIRED);
                            }
                            // Gets the image task from the incoming Message object.
                            byte[] response = inputMessage.getData().getByteArray("key");

                            Log.v(MainActivity.TAG,"Stroller handled : "+String.valueOf(response[2]));
                            Log.v(MainActivity.TAG,"Baby in the stroller : " + String.valueOf(response[3]));
                            Log.v(MainActivity.TAG,"Ceinture fermé : "+String.valueOf(response[4]));
                            Log.v(MainActivity.TAG,"Inclinaison : "+String.valueOf(response[5]));

                            byte[] distance = new byte[4];
                            distance[0] = response[6];
                            distance[1] = response[7];
                            distance[2] = response[8];
                            distance[3] = response[9];
                            Log.v(MainActivity.TAG,"Distance : "+String.valueOf(distance));

                            fireListeners(CustomEventListener.STROLLER_DISTANCE, Float.parseFloat(String.valueOf(distance)));
                            fireListeners(CustomEventListener.STROLLER_HANDLED, new Byte(response[2]).intValue());
                            //fireListeners(CustomEventListener.STROLLER_, new Byte(response[6]).intValue());
                            fireListeners(CustomEventListener.STROLLER_GYRO_POSITION,new Byte(response[5]).intValue());

                            //Log.v(MainActivity.TAG,"Response in the handler : "+new String(response));
                        }
                    });
                    connectThread.start();
                }
            }
        }
    }

    public void cancel(){
        connected = false;
        connectThread.cancel();
    }

    public void pairAndStart() {
        Log.v(TAG, "Button Called");
        // Perform action on click
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.v(TAG, "Bluetooth doesn't exist");
        }else {
            Log.v(TAG, "Bluetooth activated");
            if (!mBluetoothAdapter.isEnabled()) {
                Log.v(TAG, "Bluetooth is not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                parent.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
                Log.v(TAG, "Bluetooth is enabled");
                checkfordevices();
            }
        }
    }

    public void addListener(CustomEventListener listener){
        listeners.add(listener);
    }

    private void fireListeners(int e,Object o){
        for(CustomEventListener listener:listeners){
            listener.doEvent(e,o);
        }
    }
    private void fireListeners(int e){
        for(CustomEventListener listener:listeners){
            listener.doEvent(e,null);
        }
    }
}
