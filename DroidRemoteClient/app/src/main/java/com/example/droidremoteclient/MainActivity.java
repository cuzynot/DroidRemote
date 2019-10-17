
package com.example.droidremoteclient;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // initialize socket and input stream
    private String ip = "-1";
    private int port = 9997;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private OutputThread ot;

    // sensor dectection
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    // UI
    private EditText ipField;
    private Toolbar toolbar;

    // other vars
    private boolean pausePressed = false;
    private final int DELAY = 20;
    private String fileString = "record.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // initialize sensors
        initSensors();

        // initialize ui components
        initUI();
    }

    // PRIVATE METHODS

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void initUI(){
        // ip address field
        ipField = findViewById(R.id.ipField);
        // initialize ip field text as previous ip address
        ipField.setText(readFromFile());

        // buttons
        Button connectButton = findViewById(R.id.connectButton);
        Button pauseButton = findViewById(R.id.pauseButton);

        // toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.BLACK);

        // connect button click listener
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // get new ip address
                ip = ipField.getText().toString();

                // record new ip address
                writeToFile(ip);
                Log.v("rand", "written " + ip);

                // initialize thread
                initThread();
            }
        });

        // pause button click listener
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pausePressed = !pausePressed;

                if (pausePressed) {
                    toolbar.setBackgroundColor(Color.GRAY);
                } else {
                    toolbar.setBackgroundColor(Color.GREEN);
                }
            }
        });
    }

    private void initThread() {
        // start output thread
        ot = new OutputThread();
        ot.start();
    }

    private String readFromFile() {

        String s = "";

        try {
            InputStreamReader isr = new InputStreamReader(openFileInput(fileString));
            BufferedReader br = new BufferedReader(isr);
            s = br.readLine();

            br.close();
            isr.close();
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return s;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(fileString, Context.MODE_PRIVATE));
            osw.write(data);
            osw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // PUBLIC METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;/**/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // INNER CLASSES

    private class OutputThread extends Thread{
        public void run(){

            boolean connected = false;

            while (!connected) {
                connected = true;
                try {
                    socket = new Socket(ip, port);
                    Log.v("rand", "Connected");

                    // takes input from terminal
                    input = new DataInputStream(System.in);

                    // sends output to the socket
                    output = new DataOutputStream(socket.getOutputStream());
                } catch (IOException i) {
                    System.out.println(i);
                    connected = false;
                    toolbar.setBackgroundColor(Color.RED);
                }
            }

            // connected successfully
            toolbar.setBackgroundColor(Color.GREEN);

            while (true) {

                if (!pausePressed) {
                    sendMsg("i 1");
                    sendMsg("z " + orientationAngles[0]);
                    sendMsg("x " + orientationAngles[1]);
                    sendMsg("y " + orientationAngles[2]);

//                sendMsg("a " + gyroscopeReading[0]);
//                sendMsg("b " + gyroscopeReading[1]);
//                sendMsg("g " + gyroscopeReading[2]);

                } else {
                    sendMsg("i 0");
                }

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMsg(String msg){
            try {
                output.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}