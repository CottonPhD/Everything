package com.ptc.ptcnet.everything;

import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;


public class SensorActivity extends MainActivity implements SensorEventListener {

    Context context;
    SensorManager sm;
    HandlerThread mHanderThread = new HandlerThread("sensorThread");
    Handler handler;
    LinearLayout ll;
    RelativeLayout rl;
    Snackbar snackbar;

    ListView lv;
    public static String[] sensorTypes = {"TYPE_ACCELEROMETER", "TYPE_LIGHT", "TYPE_MAGNETIC_FIELD", "TYPE_GYROSCOPE"};
    public static String[] titles = {"Accelerometer", "Light Sensor", "Magnetic Field", "Gyroscope"};
    public static int[] values = {2, 3, 4, 5};
    public static int[] drawabls = {R.drawable.speedometer, R.drawable.sun, R.drawable.compass, R.drawable.power};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_coordinator);

        ll = (LinearLayout) findViewById(R.id.lowerLinearLayout);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        mHanderThread.start();
        handler = new Handler(mHanderThread.getLooper());

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL, handler);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL, handler);

        snackbar = Snackbar.make(findViewById(R.id.cordinatorLayout), "", Snackbar.LENGTH_LONG);


        context = this;

        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new LazyAdapter(this, drawabls, titles, values, sm, sensorTypes, mHanderThread));

    }

    public void newUiElement(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("New Property Settings");

        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(top,height,bottom,height);

        final EditText propertyName = new EditText(this);
        propertyName.setHint("Name");
        layout.addView(propertyName);

        final EditText propertyDescription = new EditText(this);
        propertyDescription.setHint("Description");
        layout.addView(propertyDescription);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Seekbar");
        spinnerArray.add("Switch");
        spinnerArray.add("Text Field");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        final Spinner spinner = new Spinner(this);
        spinner.setAdapter(spinnerArrayAdapter);
        layout.addView(spinner);

        builder.setView(layout);


        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (spinner.getSelectedItem().toString() == "Seekbar") {
                    thing.createNewNumberProperty(propertyName.getText().toString(), propertyDescription.getText().toString());
                    newSeekBar(propertyName.getText().toString());
                } else if (spinner.getSelectedItem().toString() == "Text Field") {
                    thing.createNewStringProperty(propertyName.getText().toString(), propertyDescription.getText().toString());
                    newEditText(propertyName.getText().toString());
                } else if(spinner.getSelectedItem().toString() == "Switch"){
                    thing.createNewBooleanProperty(propertyName.getText().toString(), propertyDescription.getText().toString());
                    newSwitchButton(propertyName.getText().toString());
                }

                snackbar.setText(propertyName.getText().toString());

                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Nothings gonna happen", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    public void newEditText(final String propertyName) {

        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(0, top, 0, 0);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        ll.addView(linearLayout);

        TextInputLayout v = new TextInputLayout(context);
        LayoutParams textInputParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        v.setLayoutParams(textInputParams);

        linearLayout.addView(v);

       final EditText editText = new EditText(context);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        LayoutParams editTextParams = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        editText.setLayoutParams(editTextParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        v.setHint("Message");

        v.addView(editText);


//        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
//        LayoutParams editTextParams = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final EditText editText = new EditText(context);
//        editText.setId(View.generateViewId());
//        editText.setInputType(InputType.TYPE_CLASS_TEXT);
//        editText.setHint("Your Text");
//
//        editText.setLayoutParams(editTextParams);
//
//        linearLayout.addView(editText);


        ImageButton button = new ImageButton(context);
        button.setImageResource(R.drawable.send);
        LayoutParams buttonParams = new LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(buttonParams);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    thing.setProperty(propertyName, editText.getText().toString());
                    thing.updateSubscribedProperties(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editText.clearFocus();
                editText.setText("");
            }
        });
        linearLayout.addView(button);

    }

    public void newSwitchButton(final String propertyName) {

        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(0, top, 0, 0);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ll.addView(linearLayout);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Switch nSwitch = new Switch(context);
        nSwitch.setChecked(false);
        nSwitch.setText("Your Title");
        nSwitch.setLayoutParams(layoutParams);

        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        thing.setProperty(propertyName, isChecked);
                        thing.updateSubscribedProperties(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        thing.setProperty(propertyName, isChecked);
                        thing.updateSubscribedProperties(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        linearLayout.addView(nSwitch);
    }

    public void newSeekBar(final String propertyName) {

        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(0, top, 0, 0);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ll.addView(linearLayout);

        LayoutParams layoutParams = new LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(100);
        seekBar.setProgress(100);
        seekBar.setLayoutParams(layoutParams);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    thing.setProperty(propertyName, progress);
                    thing.updateSubscribedProperties(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        linearLayout.addView(seekBar);

    }

    @Override
    protected void onPause() {
        System.out.println("onPause...........................");
        super.onPause();
        sm.unregisterListener(this);
        mHanderThread.quit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            try {
                thing.setProperty("Accelerometer", event.values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
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
