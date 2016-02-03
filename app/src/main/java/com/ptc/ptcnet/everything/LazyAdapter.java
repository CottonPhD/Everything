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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by pdahmen on 02.02.2016.
 */
public class LazyAdapter extends BaseAdapter {

    private SensorActivity activity;
    private String[] array;
    private int[] values;
    private int[] drawables;
    private static LayoutInflater inflater = null;
    private SensorManager sensorManager;
    public String[] sensorNames;
    private HandlerThread handlerThread;
    private Handler handler;
    Holder holder;

    public LazyAdapter(SensorActivity a, int[] nDrawables, String[] nTitle, int[] nValues, SensorManager sm, String[] sensors, HandlerThread ht) {
        handlerThread = ht;
        sensorManager = sm;
        drawables = nDrawables;
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
        Switch onOffSwitch;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new Holder();

        View vi;
        vi = inflater.inflate(R.layout.list_detail, null);

        holder.onOffSwitch = (Switch) vi.findViewById(R.id.switchKey);
        holder.tv = (TextView) vi.findViewById(R.id.sensorName);
        holder.tv2 = (TextView) vi.findViewById(R.id.sensorValue);
        holder.imageView = (ImageView) vi.findViewById(R.id.onOff);


        holder.onOffSwitch.setChecked(true);
        holder.imageView.setImageResource(drawables[position]);
        holder.imageView.setColorFilter(Color.parseColor("#1565c0"));
        holder.tv.setText(array[position]);
        holder.tv2.setText(Integer.toString(values[position]));

        holder.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String name = sensorNames[position];

                if (isChecked) {
                    if (name == "TYPE_MAGNETIC_FIELD") {
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, handler);
                    }
                    else if(name =="TYPE_ACCELEROMETER"){
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, handler);
                    }
                    else if(name =="TYPE_LIGHT"){
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL, handler);
                    }
                    else if(name =="TYPE_GYROSCOPE"){
                        sensorManager.registerListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL, handler);
                    }
                } else {
                    if (name == "TYPE_MAGNETIC_FIELD") {
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
                    }
                    else if(name=="TYPE_ACCELEROMETER"){
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                    }
                    else if(name=="TYPE_LIGHT"){
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
                    }
                    else if(name=="TYPE_GYROSCOPE"){
                        sensorManager.unregisterListener(activity, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

                    }
                }
            }
        });

        return vi;
    }
}
