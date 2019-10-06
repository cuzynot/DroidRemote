package com.example.droidremoteclient;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //initialize socket and input stream
    private String address = "172.18.52.213";
    private int port = 9999;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

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

        Log.v("rand", "running test");

        OutputThread ot = new OutputThread();
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

    private class OutputThread extends Thread{
        public void run(){
            try {
                socket = new Socket(address, port);
                Log.v("rand", "Connected");

                // takes input from terminal
                input = new DataInputStream(System.in);

                // sends output to the socket
                output = new DataOutputStream(socket.getOutputStream());

                String line = "";

                // keep reading until "Over" is input
                while (!line.equals("Over")){
                    try {
                        Thread.sleep(1000);
                        // line = input.readLine();
                        output.writeUTF("yeet");
                        output.flush();
                    } catch(IOException i) {
                        System.out.println(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch(IOException i) {
                System.out.println(i);
            }
        }
    }
}
