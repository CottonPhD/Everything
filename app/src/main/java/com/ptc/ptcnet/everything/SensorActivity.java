package com.ptc.ptcnet.everything;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ListView;


public class SensorActivity extends MainActivity implements SensorEventListener {

    Context context;
    SensorManager sm;
    HandlerThread mHanderThread = new HandlerThread("sensorThread");
    Handler handler;

    ListView lv;
    public static String[] sensorTypes = {"TYPE_ACCELEROMETER", "TYPE_LIGHT", "TYPE_MAGNETIC_FIELD"};
    public static String[] titles = {"Accelerometer", "Light Sensor", "Magnetic Field"};
    public static int[] values = {2,3,4};

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_main);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        mHanderThread.start();
        handler = new Handler(mHanderThread.getLooper());

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL, handler);

        context=this;

        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new LazyAdapter(this, titles, values, sm, sensorTypes, mHanderThread));
    }

    @Override
    protected void onPause(){
        System.out.println("onPause...........................");
        super.onPause();
        sm.unregisterListener(this);
        mHanderThread.quit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            try {
                thing.setProperty("Accelerometer", event.values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            try {
                thing.setProperty("MagneticField", event.values[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            thing.updateSubscribedProperties(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
