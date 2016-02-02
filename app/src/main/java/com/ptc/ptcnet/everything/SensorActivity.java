package com.ptc.ptcnet.everything;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by pdahmen on 02.02.2016.
 */
public class SensorActivity extends MainActivity {

    Context context;

    ArrayList valueList;
    ListView lv;
    public static String[] titles = {"Accelerometer", "Light Sensor", "Magnetic Field"};
    public static int[] values = {2,3,4};

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_main);

        context=this;

        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new LazyAdapter(this, titles, values));
    }

}
