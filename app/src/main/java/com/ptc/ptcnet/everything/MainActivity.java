package com.ptc.ptcnet.everything;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.thingworx.communications.client.things.VirtualThing;

public class MainActivity extends ThingworxActivity {

    public static final int POLLING_RATE = 1000;

    private final String TAG = MainActivity.class.getName();
    public AndroidThing thing;
    Button sensorAcivityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        sensorAcivityButton =(Button) findViewById(R.id.sensorButton);

        try{
            thing = new AndroidThing("AndroidThing", "Thing", client);

            startProcessScanRequestThread(POLLING_RATE, new ConnectionStateObserver() {
                @Override
                public void onConnectionStateChanged(final boolean connected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //checkBoxConnected.setChecked(connected);
                        }
                    });
                }
            });

            if (!hasConnectionPreferences()) {
                // Show Preferences Activity
                connectionState = ConnectionState.DISCONNECTED;
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, 1);
                return;
            }

            connect(new VirtualThing[] {thing});


        }catch(Exception e){
            Log.e(TAG, "Failed to initialize with error.", e);
            onConnectionFailed("Failed to initialize with error : "+ e.getMessage());
        }

    }

    public void startSensorActivity(View view){
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG, "onResume() called.");
//        if(getConnectionState() == ConnectionState.DISCONNECTED) {
//            try {
//                connect(new VirtualThing[]{thing});
//            } catch (Exception e) {
//                Log.e(TAG, "Restart with new settings failed.", e);
//            }
//        }
    }
}
