package com.ptc.ptcnet.everything;

import android.content.Context;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class SensorActivity extends MainActivity implements SensorEventListener {

    Context context;
    SensorManager sm;
    HandlerThread mHanderThread = new HandlerThread("sensorThread");
    Handler handler;
    LinearLayout ll;

    ListView lv;
    public static String[] sensorTypes = {"TYPE_ACCELEROMETER", "TYPE_LIGHT", "TYPE_MAGNETIC_FIELD", "TYPE_GYROSCOPE"};
    public static String[] titles = {"Accelerometer", "Light Sensor", "Magnetic Field", "Gyroscope"};
    public static int[] values = {2,3,4,5};
    public static int[] drawabls = {R.drawable.speedometer, R.drawable.sun, R.drawable.compass, R.drawable.power};

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_main);

        ll = (LinearLayout) findViewById(R.id.lowerLinearLayout);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        mHanderThread.start();
        handler = new Handler(mHanderThread.getLooper());

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL, handler);


        context=this;

        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new LazyAdapter(this,drawabls, titles, values, sm, sensorTypes, mHanderThread));

    }

    public void newUiElement(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("New Property Settigns");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16,0,16,0);

        final EditText propertyName = new EditText(this);
        propertyName.setHint("ThingWorx Property Name");
        layout.addView(propertyName);

        final EditText propertyDescription = new EditText(this);
        propertyDescription.setHint("ThingWorx Property Description");
        layout.addView(propertyDescription);

        final EditText propertyValueType = new EditText(this);
        propertyValueType.setHint("NUMBER, STRING ...");
        layout.addView(propertyValueType);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Seekbar");
        spinnerArray.add("RadioButton");
        spinnerArray.add("TextField");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        Spinner spinner = new Spinner(this);
        spinner.setAdapter(spinnerArrayAdapter);
        layout.addView(spinner);

        builder.setView(layout);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                thing.createNewProperty(propertyName.getText().toString(), propertyDescription.getText().toString());
                Toast.makeText(context, "Lelelel", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        builder.show();


    }

    public void newSeekBar(){
        LayoutParams layoutParams = new LayoutParams(300,100);

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(100);
        seekBar.setProgress(100);
//        seekBar.setMinimumHeight(60);
//        seekBar.setMinimumWidth(200);
        seekBar.setLayoutParams(layoutParams);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ll.addView(seekBar);
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
