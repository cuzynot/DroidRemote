package com.example.droidremoteclient;

import android.content.Context;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // initialize socket and input stream
    private String address = "172.18.52.213";
    private int port = 9999;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private OutputThread ot;

    // sensor dectection
    private SensorManager sensorManager;
    private Sensor sensor;
    private double x, y, z;

    // other vars
    private boolean isActive;
    private final int DELAY = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // init sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor, DELAY);

        // init values
        isActive = true;

        ot = new OutputThread();
        ot.start();
    }

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
        Log.v("rand", "sensorChanged");
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
//        ot.sendMsg("x " + event.values[0]);
//        ot.sendMsg("y " + event.values[1]);
//        ot.sendMsg("z " + event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class OutputThread extends Thread{
        public void run(){
            try {
                socket = new Socket(address, port);
                Log.v("rand", "Connected");

                // takes input from terminal
                input = new DataInputStream(System.in);

                // sends output to the socket
                output = new DataOutputStream(socket.getOutputStream());

                // keep reading until "Over" is input
//                while (isActive){
//                    sendMsg("yeet");
//                }
            } catch(IOException i) {
                System.out.println(i);
            }

            while (isActive){
                sendMsg("x " + x);
                sendMsg("y " + y);
                sendMsg("z " + z);
            }
        }

        public void sendMsg(String msg){
            try {
                output.writeUTF(msg);
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
