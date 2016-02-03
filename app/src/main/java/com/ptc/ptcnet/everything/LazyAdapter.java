package com.ptc.ptcnet.everything;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pdahmen on 02.02.2016.
 */
public class LazyAdapter extends BaseAdapter {

    private SensorActivity activity;
    private String[] array;
    private int[] values;
    private static LayoutInflater inflater = null;
    private SensorManager sensorManager;
    public String[] sensorNames;
    private HandlerThread handlerThread;
    private Handler handler;
    Holder holder;

    private boolean accelerometer = true;
    private boolean magneticField = true;
    private boolean light = true;

    public LazyAdapter(SensorActivity a, String[] nTitle, int[] nValues, SensorManager sm, String[] sensors, HandlerThread ht) {
        handlerThread = ht;
        sensorManager = sm;
        activity = a;
        sensorNames = sensors;
        values = nValues;
        array = nTitle;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        handler = new Handler(handlerThread.getLooper());
    }

    public int getCount() {
        return array.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tv;
        TextView tv2;
        ImageView imageView;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new Holder();

        View vi;
        vi = inflater.inflate(R.layout.list_detail, null);

        holder.tv = (TextView) vi.findViewById(R.id.sensorName);
        holder.tv2 = (TextView) vi.findViewById(R.id.sensorValue);
        holder.imageView = (ImageView) vi.findViewById(R.id.onOff);

        holder.imageView.setColorFilter(Color.parseColor("#1565c0"));
        holder.imageView.setImageResource(R.drawable.power);
        holder.tv.setText(array[position]);
        holder.tv2.setText(Integer.toString(values[position]));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String name = sensorNames[position];
                System.out.println("Name: " + name);
                if(name == "TYPE_MAGNETIC_FIELD") {
                    if (magneticField == true) {
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
                        System.out.println("I work ...........");
                        holder.imageView.setColorFilter(Color.parseColor("#BDBDBD"));
                        magneticField = false;
                    } else {
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, handler);
                        holder.imageView.setColorFilter(Color.parseColor("#1565c0"));
                        magneticField = true;
                    }
                }
                else if(name == "TYPE_ACCELEROMETER"){
                    if(accelerometer == true){
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                        holder.imageView.setColorFilter(Color.parseColor("#BDBDBD"));
                        System.out.println(holder.imageView.getColorFilter());
                        accelerometer = false;
                    }
                    else {
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, handler);
                        holder.imageView.setColorFilter(Color.parseColor("#1565c0"));
                        accelerometer = true;
                    }
                }
            }
        });

        

        return vi;
    }
}
