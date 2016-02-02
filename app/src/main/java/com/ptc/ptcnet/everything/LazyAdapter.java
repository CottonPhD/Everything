package com.ptc.ptcnet.everything;

import android.app.Activity;
import android.content.Context;
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

    public LazyAdapter(SensorActivity a, String[] nTitle, int[] nValues) {
        activity = a;
        values = nValues;
        array = nTitle;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        View vi;
        vi = inflater.inflate(R.layout.list_detail, null);

        holder.tv = (TextView) vi.findViewById(R.id.sensorName);
        holder.tv2 = (TextView) vi.findViewById(R.id.sensorValue);

        ImageView image = (ImageView) vi.findViewById(R.id.onOff);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "lelel", Toast.LENGTH_LONG).show();

            }
        });

        holder.tv.setText(array[position]);
        holder.tv2.setText( Integer.toString(values[position]));
        

        return vi;
    }
}
