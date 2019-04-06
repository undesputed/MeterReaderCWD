package com.example.meterreadercwd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class Print_Helper extends AppCompatActivity{

    // android built in classes for bluetooth operations
    BluetoothAdapter mBLuetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;


    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    // this will find a bluetooth printer device
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
            Toast.makeText(this, "Bluetooth device found.", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();
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
//                                                myLabel.setText(data);
                                                Toast.makeText(Print_Helper.this, "Printing please wait...", Toast.LENGTH_SHORT).show();
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

    void sendData(String msg) throws IOException {
        try {
            mmOutputStream.write(msg.getBytes());

            //tell the user data were sent
//            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"something went wrong.", Toast.LENGTH_LONG).show();
        }
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(this, "Bluetooth Closed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
