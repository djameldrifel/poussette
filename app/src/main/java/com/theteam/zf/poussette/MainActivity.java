package com.theteam.zf.poussette;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theteam.zf.poussette.geolocation.MapsActivity;

import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    Button bluetoothButton ;
    ImageButton notificationButton;
    ImageButton openMap;
    ImageButton berceuseButton;
    RotatingImageView poussetteImage;
    DistanceImageView distanceImage;
    Boolean berceuse = false;
    int NOTIFICATION_DISTANCE = 1001;

    static final String TAG = "DebugBluetooth";

    Communication communication ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Change the icon in the menu with infos of the user
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String personName = extras.getString("personName");
            String personPhoto = extras.getString("personPhoto");
            String personGooglePlusProfile = extras.getString("personGooglePlusProfile");

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(personName);

            Log.d(TAG, "personName:" + personName);
            Log.d(TAG, "personPhoto:" + personPhoto);
            Log.d(TAG, "personGooglePlusProfile:" + personGooglePlusProfile);

            new AsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... params) {

                    try {
                        URL url = new URL(params[0]);
                        InputStream in = url.openStream();
                        return BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        /* TODO log error */
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Log.d(TAG, "OnPostExecute:");
                    getSupportActionBar().setHomeAsUpIndicator(new BitmapDrawable(getResources(), bitmap));
                }
            }.execute(personPhoto);
        }
        communication = new Communication(this);

        Log.v(TAG, "Application started");
        openMap = (ImageButton) findViewById(R.id.open_map);
        notificationButton = (ImageButton) findViewById(R.id.notification);
        berceuseButton = (ImageButton) findViewById(R.id.berceuse_button);
        poussetteImage = (RotatingImageView) findViewById(R.id.poussette);
        distanceImage = (DistanceImageView) findViewById(R.id.distance);

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "ImageButton Clicked");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });
        //Read the bluetooth pairation
        communication.addListener(new CustomEventListener() {
            @Override
            public void doEvent(int e, Object o) {
                if (e == CustomEventListener.BLUETOOTH_PAIRED) {
                    Toast.makeText(MainActivity.this, "Bluetooth paired", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Read the gryroposition
        communication.addListener(new CustomEventListener() {
            @Override
            public void doEvent(int e,Object o) {
                if (e == CustomEventListener.STROLLER_GYRO_POSITION) {
                    poussetteImage.rotate((int) o);
                }
            }
        });

        //Read the baby distance
        communication.addListener(new CustomEventListener() {
            @Override
            public void doEvent(int e,Object o) {
                if (e == CustomEventListener.STROLLER_DISTANCE) {
                    distanceImage.setPourcentage((int) o);
                    if((int) o < 10){
                        Notification notification = new Notification.Builder(getApplicationContext())
                                .setContentTitle("Distance Notification")
                                .setSmallIcon(R.drawable.alerts_and_states_warning)
                                        //.setLargeIcon(R.drawable.danger_icon)
                                .setContentText("Attention obstacle.").build();
                        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(NOTIFICATION_DISTANCE, notification);
                    }
                }
            }
        });

        berceuseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "button berceuse called");
                if(!berceuse){
                    Log.v(TAG, "Start berceuse");
                    berceuse = !berceuse;
                    communication.sendPayload(35);
                }
                else{
                    Log.v(TAG, "Stop berceuse");
                    berceuse = !berceuse;
                    communication.sendPayload(40);
                }
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "button alarm called");
                communication.sendPayload(30);
            }
        });

        //TODO read from the poussette to draw the angle
        /*new Thread()
        {
            public void run() {
                try {
                    do {
                        sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                poussetteImage.rotate(30 - new Random().nextInt(60));
                                distanceImage.setPourcentage(new Random().nextInt(120));
                            }
                        });
                    }while (true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == Communication.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                communication.checkfordevices();
                Log.v(TAG, "RESULT_OK");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.bluetooth_pair) {
            communication.pairAndStart();
        }

        if (id == R.id.bluetooth_unpair) {
            communication.cancel();
        }

        return super.onOptionsItemSelected(item);
    }

}
