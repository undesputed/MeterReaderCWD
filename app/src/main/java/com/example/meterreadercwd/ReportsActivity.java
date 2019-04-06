package com.example.meterreadercwd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;

public class ReportsActivity extends AppCompatActivity {
    private TextView zone_id, zone_loc, ttl_read, overall;
    Applicant_DB applicant_db;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    BluetoothAdapter mBLuetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    private DecimalFormat numberFormat = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Reports");

        applicant_db = new Applicant_DB(this);

        Reference_data reference_data = new Reference_data(getApplicationContext());

        zone_id = findViewById(R.id.tv_zone_id);
        zone_loc = findViewById(R.id.tv_location);
        ttl_read = findViewById(R.id.tv_ttlRead);
        overall = findViewById(R.id.tv_overallbill);

        Cursor zone_tmp = applicant_db.getReportInfo();
        zone_tmp.moveToFirst();
        zone_id.setText(zone_tmp.getString(0));

        Cursor total = applicant_db.getTotalRead();
        total.moveToFirst();
        ttl_read.setText(total.getString(0));

        overall.setText(numberFormat.format(getOverAllBill()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reports_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if(id == R.id.print_data) {
            try {
                findBT();
                openBT();
                sendData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Double getOverAllBill() {
        Double tmp = 0.00;
        Cursor overall_c = applicant_db.getAllBillValues();

        while(overall_c.moveToNext()) {
            try {
                tmp += Double.parseDouble(overall_c.getString(0));
            } catch (Exception e) { }
        }

        return tmp;
    }
    void findBT() {
        try {
            mBLuetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBLuetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),"No bluetooth adapter available", Toast.LENGTH_LONG).show();
            }
            if(!mBLuetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBLuetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for(BluetoothDevice device : pairedDevices) {
                    if(device.getName().equals("T9 BT Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Toast.makeText(getApplicationContext(),"bluetooth ready.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();

                            if(bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for(int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if(b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void sendData() throws IOException {
        try {
            String msg = "\n\nTotal No. Read: " + ttl_read.getText().toString() +
                    "\nOverall Bills: " + overall.getText().toString() + "\n\n";


            mmOutputStream.write(msg.getBytes());

            //tell the user data were sent
//            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"something went wrong.", Toast.LENGTH_LONG).show();
        }

    }
}
