package com.example.mijnboot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.SystemClock;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    Thread workerThread;
    private static String ROBOT_CONTROLS_LOG = "robot_controls";
    private BluetoothSocket btSocket;
    private OutputStream btOutputStream;
    private InputStream btInputStream;

    private String stdSerialPortId;
    private TextView btCommandsListText;

    private int speed = 0;
    private int direction = 90;
    private boolean progressChanged = false;
    private int readBufferPosition;
    private int transmitInterval;
    volatile boolean stopWorker;
    private byte[] readBuffer;

    private SeekBar speedSeekBar;
    private SeekBar directionSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        speedSeekBar = findViewById(R.id.speedSeekBar);
        directionSeekBar = findViewById(R.id.directionSeekBar);

        setSupportActionBar(toolbar);

        String message = getIntent().getStringExtra(BluetoothListing.BLUETOOTH_CONNECT_STRING);
        String btConnData[] = message.split("\\r?\\n");

        speedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                speed = progresValue;
                progressChanged = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        directionSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                direction = progresValue;
                progressChanged = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        connectToBluetooth(btConnData[1]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public void buttonClick(View view) {
        speedSeekBar.setProgress(0);
        directionSeekBar.setProgress(90);
        progressChanged = true;
    }

    private void connectToBluetooth(String serial) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(serial);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            btSocket = device.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            btOutputStream = btSocket.getOutputStream();
        } catch (IOException e) {
            communicationError();
        }
        sendDirectionsPeriodically.post(runnableCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            btSocket.close();
        }catch (IOException e) {
            Log.e(ROBOT_CONTROLS_LOG, e.getMessage());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    private void communicationError(){
        Toast.makeText(getApplicationContext(), "Bluetooth communication error occured",
                Toast.LENGTH_SHORT).show();
        SystemClock.sleep(2000);
        super.onBackPressed();
    }

    /**
     * this handler runs every TRANSMIT_INTERVAL millis and transmits data over bluetooth
     */
    Handler sendDirectionsPeriodically = new Handler();

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                if (progressChanged) {
                    String newDirection = "<direction " + direction +">";
                    btOutputStream.write(newDirection.getBytes());
                    Toast.makeText(getApplicationContext(), newDirection,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                communicationError();
            }
            progressChanged = false;
            sendDirectionsPeriodically.postDelayed(runnableCode, transmitInterval);
        }
    };
}
