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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PrintActivity extends AppCompatActivity {
    TextView ttlamnt_text,amnt_text,usage_text,prev_text,acc_text,name_text,republic,city,provice,carcar,san,tel,accnum,statement,border, classcode,classtext,bill,billtext,name,seq,seqtext,add,addtext,ave,avetext,border2,meter,prev,pres,prestext,due,duetext,
            discon,discontext,mread,mreadtext,mbrand,mbrandtext,mnum,mnumtext,prev2,prev2text,pres2,pres2text,usage,amnt,ttlamnt,border3,messag, messag1, messag2, messag3, messag4, messag5, arrears, staggered;
    String nametext;
    String prevtext;
    String usagetext;
    String current_rdg;
    String amnttext;
    String ttlamnttext;
    String acctext;
    String bill_no;
    String original_bill;
    TextView arrears_tv, staggered_tv;
    Bundle bundle;

    Intent intent;

    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBLuetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    Applicant_DB applicant_db;
    Print_Helper printer;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private String isRead;
    private DecimalFormat numberFormat = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        numberFormat.setGroupingUsed(true);
        numberFormat.setGroupingSize(3);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        applicant_db = new Applicant_DB(this);
        printer = new Print_Helper();

        Reference_data reference_data = new Reference_data(this);

        // getting extras on previous intent
        bundle = getIntent().getExtras();
        isRead = bundle.getString("isRead");
        if(isRead.equals("YES") || isRead.equals("UPLOADED")) {
            bill_no = bundle.getString("trans_no");
        } else {
            bill_no = reference_data.getTransNo();
        }


        original_bill = bundle.getString("original_bill");

        current_rdg = bundle.getString("current_reading");

        republic = (TextView) findViewById(R.id.textView1);
        city = (TextView) findViewById(R.id.textView2);
        provice = (TextView) findViewById(R.id.textView3);
        carcar = (TextView) findViewById(R.id.textView4);
        san = (TextView) findViewById(R.id.textView5);
        tel = (TextView) findViewById(R.id.textView6);
        statement = (TextView) findViewById(R.id.textView7);
        border = (TextView) findViewById(R.id.textView8);
        accnum = (TextView) findViewById(R.id.textView9);
        acctext = bundle.getString("account_num");
        acc_text = (TextView) findViewById(R.id.txtAccNo);
        acc_text.setText(acctext);

        classcode = (TextView) findViewById(R.id.textView10);
        classtext = (TextView) findViewById(R.id.txtClassCode);
        classtext.setText(bundle.getString("class_code"));
        bill = (TextView) findViewById(R.id.textView11);
        billtext = (TextView) findViewById(R.id.txtBillNo);
        billtext.setText(bill_no);
        name = (TextView) findViewById(R.id.textView12);
        nametext = bundle.getString("applicant_name");
        name_text = (TextView) findViewById(R.id.txtName);
        name_text.setText(nametext);

        seq = (TextView) findViewById(R.id.textView13);
        seqtext = (TextView) findViewById(R.id.txtSeqNo);
        seqtext.setText(bundle.getString("sequence_no"));
        add = (TextView) findViewById(R.id.textView14);
        addtext = (TextView) findViewById(R.id.txtAddress);
        addtext.setText(bundle.getString("address"));
        ave = (TextView) findViewById(R.id.textView15);
        avetext = (TextView) findViewById(R.id.txtAve);
        border2 = (TextView) findViewById(R.id.textView16);
        meter = (TextView) findViewById(R.id.textView17);
        prev = (TextView) findViewById(R.id.textView18);
        prevtext = bundle.getString("reading_date"); // previous reading date
        prev_text = (TextView) findViewById(R.id.txtPrevious);
        prev_text.setText(prevtext);

        pres = (TextView) findViewById(R.id.textView19);
        prestext = (TextView) findViewById(R.id.txtMDPresent);
        prestext.setText(PreferenceManager.getInstance(getApplicationContext()).getTrans_date());
        due = (TextView) findViewById(R.id.textView20);
        duetext = (TextView) findViewById(R.id.txtDueDate);
        duetext.setText(bundle.getString("due_date"));
        discon = (TextView) findViewById(R.id.textView21);
        discontext = (TextView) findViewById(R.id.txtDisconnection);
        discontext.setText(bundle.getString("disc_date"));
        mread = (TextView) findViewById(R.id.textView22);
        mreadtext = (TextView) findViewById(R.id.txtMReader);
        mreadtext.setText(PreferenceManager.getInstance(getApplicationContext()).getUser_name());
        mbrand = (TextView) findViewById(R.id.textView23);
        mbrandtext = (TextView) findViewById(R.id.txtMeterBrand);
        mbrandtext.setText(bundle.getString("meter_brand"));
        mnum = (TextView) findViewById(R.id.textView24);
        mnumtext = (TextView) findViewById(R.id.txtMeterNo);
        mnumtext.setText(bundle.getString("meter_no"));
        prev2 = (TextView) findViewById(R.id.textView25);
        prev2text = (TextView) findViewById(R.id.txtPrev);
        prev2text.setText(bundle.getString("prev_reading"));
        pres2 = (TextView) findViewById(R.id.textView26);
        pres2text = (TextView) findViewById(R.id.txtRDPresent);
        pres2text.setText(bundle.getString("current_reading"));
        usage = (TextView) findViewById(R.id.textView27);
        usagetext = bundle.getString("usage");
        usage_text =(TextView) findViewById(R.id.txtRDPrev);
        usage_text.setText(usagetext);

        amnt = (TextView) findViewById(R.id.textView28);
        amnttext = bundle.getString("advance");
        amnt_text = (TextView) findViewById(R.id.txtAdvance);
        amnt_text.setText(amnttext);

        arrears = (TextView) findViewById(R.id.textView281);
        arrears_tv = (TextView) findViewById(R.id.txtArrears);
        String temp_arr = bundle.getString("arrears");
        try {
            temp_arr = Double.parseDouble(temp_arr) + "";
        }catch (Exception e) {
            temp_arr = "0.00";
        }
        temp_arr = numberFormat.format(Double.parseDouble(temp_arr)) +"";
        arrears_tv.setText(temp_arr);

        staggered = (TextView) findViewById(R.id.textView282);
        staggered_tv = (TextView) findViewById(R.id.txtStaggered);
        temp_arr = bundle.getString("staggered");
        try {
            temp_arr = Double.parseDouble(temp_arr) + "";
        }catch (Exception e) {
            temp_arr = "0.00";
        }
        temp_arr = numberFormat.format(Double.parseDouble(temp_arr)) + "";
        staggered_tv.setText(temp_arr);

        ttlamnt = (TextView) findViewById(R.id.textView30);
        ttlamnttext = bundle.getString("bill_amount");
        ttlamnttext = numberFormat.format(Double.parseDouble(ttlamnttext)) +"";
        ttlamnt_text = (TextView) findViewById(R.id.txtAmtDue);
        ttlamnt_text.setText(ttlamnttext);

        border3 = (TextView) findViewById(R.id.textView31);
        messag = (TextView) findViewById(R.id.textView32);
        messag1 = (TextView) findViewById(R.id.textView33);
        messag2 = (TextView) findViewById(R.id.textView34);
        messag3 = (TextView) findViewById(R.id.textView35);
        messag4 = (TextView) findViewById(R.id.textView36);
        messag5 = (TextView) findViewById(R.id.textView37);

        Button upload = (Button) findViewById(R.id.btn_upload);
        if(!isRead.equals("UPLOADED")) { upload.setVisibility(View.GONE); }


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp_adv;
                Cursor res = applicant_db.getDataForUpload(acctext);
                res.moveToFirst();
//                Toast.makeText(PrintActivity.this, ""+res.getString(8), Toast.LENGTH_SHORT).show();
                tmp_adv = res.getString(8).equals("0.00")? "NO" : res.getString(8);

                uploadApplicantData(res.getString(0), res.getString(1),
                        res.getString(2), PreferenceManager.getInstance(getApplicationContext()).getTrans_date(),
                        res.getString(3), res.getString(4), res.getString(5),
                        res.getString(6), res.getString(7), tmp_adv, res.getString(9));
            }
        });

        if(isRead.equals("UPLOADED")) {
            applicant_db.updateUploadApplicant(acctext, usagetext, current_rdg, ttlamnttext, prev2text.getText().toString(), bill_no, original_bill, arrears_tv.getText().toString(), amnttext, staggered_tv.getText().toString());
        }
            try {

            Button print = (Button) findViewById(R.id.btn_print);


            print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reference_data reference_data = new Reference_data(getApplicationContext());
                    if(isRead.equals("UNREAD")) {
                        reference_data.update_index();
                        AddToReaded();
                    } else if(isRead.equals("YES")) {
                        AddToReaded();
                    }

                    try {
//                        if(!bundle.getBoolean("read")) {
//                            applicant_db.readApplicant(acctext.trim(), usagetext.trim(), bundle.getString("current_reading"), bundle.getString("bill_amount"));
//                            intent = new Intent(PrintActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }

                        printer.findBT();
                        printer.openBT();
                        printer.sendData(data_to_print());
                        printer.closeBT();
//                        findBT();
//                        openBT();
//                        sendData();
//                        closeBT();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    switch (isRead) {
                        case "YES":
                            intent = new Intent(PrintActivity.this, ReadActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                            break;
                        case "UPLOADED":
                            intent = new Intent(PrintActivity.this, UploadedActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                            break;
                    }

                    intent = new Intent(PrintActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return true;
    }



    void AddToReaded() {
        String tmp_adv;
        try {
            tmp_adv = Double.parseDouble(amnttext) == 0? "N0" : amnttext;
        } catch (Exception e) {
            tmp_adv = "NO";
        }
        applicant_db.readApplicant(acctext, usagetext, current_rdg, ttlamnttext, prev2text.getText().toString(), bill_no, original_bill, arrears_tv.getText().toString(), tmp_adv, staggered_tv.getText().toString());
    }

    public void uploadApplicantData(final String id, final String zone_id, final String book_id, final String trans_date,
                                    final String prev_rdg, final String curr_rdg, final String usage, final String bill_amount,
                                    final String original_bill, final String advance, final String trans_code) {
        String ROOT_URL_UPLOAD = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_UPLOAD_DATA;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", id);
                params.put("zone_id", zone_id);
                params.put("book_id", book_id);
                params.put("trans_date", trans_date);
                params.put("prev_rdg", prev_rdg);
                params.put("curr_rdg", curr_rdg);
                params.put("usage", usage);
                params.put("bill_amount", bill_amount);
                params.put("orig_bill_amount", original_bill);
                params.put("advance", advance);
                params.put("trans_code", trans_code);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String data_to_print() {
        String rep = republic.getText().toString();
        String cit = city.getText().toString();
        String prov = provice.getText().toString();
        String car = carcar.getText().toString();
        String vic = san.getText().toString();
        String phone = tel.getText().toString();
        String state = statement.getText().toString();
        String boord = border.getText().toString();
        String acclabel = accnum.getText().toString();
        String accset = acctext;
        String ccode = classcode.getText().toString();
        String ctext = classtext.getText().toString();
        String bil = bill.getText().toString();
        String bilT = billtext.getText().toString();
        String nm = name.getText().toString();
        String nmtext = nametext;
        String sequence = seq.getText().toString();
        String seqt = seqtext.getText().toString();
        String address = add.getText().toString();
        String addressT = addtext.getText().toString();
        String aven = ave.getText().toString();
        String avenT = avetext.getText().toString();
        String bord2 = border2.getText().toString();
        String mtr = meter.getText().toString();
        String prv = prev.getText().toString();
        String prvT = prevtext;
        String prs = pres.getText().toString();
        String prsT = prestext.getText().toString();
        String du = due.getText().toString();
        String duT = duetext.getText().toString();
        String dscon = discon.getText().toString();
        String dsconT = discontext.getText().toString();
        String mrd = mread.getText().toString();
        String mrdT = mreadtext.getText().toString();
        String mbrnd = mbrand.getText().toString();
        String mbrndT = mbrandtext.getText().toString();
        String mno = mnum.getText().toString();
        String mnoT = mnumtext.getText().toString();
        String prv2 = prev2.getText().toString();
        String prv2T = prev2text.getText().toString();
        String prs2 = pres2.getText().toString();
        String prs2T = pres2text.getText().toString();
        String use = usage.getText().toString();
        String useT = usagetext;
        String amount = amnt.getText().toString();
        String amountT = amnttext;
        String arrears_tmp = arrears.getText().toString();
        String arrears_val = arrears_tv.getText().toString();
        String staggered_tmp = staggered.getText().toString();
        String staggered_val = staggered_tv.getText().toString();
        String tamnt = ttlamnt.getText().toString();
        String tamntT = ttlamnttext;
        String bord3 = border3.getText().toString();
        String mess = messag.getText().toString()+"\n"+messag1.getText().toString()+"\n"+messag2.getText().toString()+"\n"+messag3.getText().toString()+"\n"+messag4.getText().toString()+"\n"+messag5.getText().toString();

        String header ="    "+rep+"\n"+"          "+cit+"\n"+"        "+prov+"\n"+"   "+car+"\n"+""+vic+"\n"+""+phone+"\n"+""+state+"\n"+boord+"\n";

        String msg = header+acclabel+" "+accset+"\n"+ccode+" "+ctext+"\n"+bil+" "+bilT+"\n"+nm+" "+nmtext+"\n"+sequence+" "+seqt+"\n"+address+" "+addressT+"\n"+aven+" "+avenT
                +"\n"+bord2+"\n"+mtr+"\n"+prv+"\n"+prvT+"\n"+prs+"\n"+prsT+"\n"+du+"\n"+duT+"\n"+dscon+"\n"+dsconT+"\n"+mrd+" "+mrdT+"\n"+mbrnd+" "+mbrndT+"\n"+mno+" "+mnoT+"\n"+prv2+" "+prv2T+"\n"+prs2+" "+prs2T+"\n"+use+" "+useT+"\n"+amount+" "+amountT
                +"\n" + arrears_tmp + " " + arrears_val +"\n" + staggered_tmp + " " + staggered_val + "\n" +tamnt+" "+tamntT+"\n"+bord3+"\n"+mess+"\n"+"\n"+"\n"+"\n";
        return msg;
    }

    // this will find a bluetooth printer device
    void findBT() {
        try {
            mBLuetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBLuetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
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

            myLabel.setText("Bluetooth device found.");
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

            myLabel.setText("Bluetooth Opened");
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
                                                myLabel.setText(data);
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
            // the text typed by the user
//            String msg = myTextbox.getText().toString();
//            msg += "\n";

            String rep = republic.getText().toString();
            String cit = city.getText().toString();
            String prov = provice.getText().toString();
            String car = carcar.getText().toString();
            String vic = san.getText().toString();
            String phone = tel.getText().toString();
            String state = statement.getText().toString();
            String boord = border.getText().toString();
            String acclabel = accnum.getText().toString();
            String accset = acctext;
            String ccode = classcode.getText().toString();
            String ctext = classtext.getText().toString();
            String bil = bill.getText().toString();
            String bilT = billtext.getText().toString();
            String nm = name.getText().toString();
            String nmtext = nametext;
            String sequence = seq.getText().toString();
            String seqt = seqtext.getText().toString();
            String address = add.getText().toString();
            String addressT = addtext.getText().toString();
            String aven = ave.getText().toString();
            String avenT = avetext.getText().toString();
            String bord2 = border2.getText().toString();
            String mtr = meter.getText().toString();
            String prv = prev.getText().toString();
            String prvT = prevtext;
            String prs = pres.getText().toString();
            String prsT = prestext.getText().toString();
            String du = due.getText().toString();
            String duT = duetext.getText().toString();
            String dscon = discon.getText().toString();
            String dsconT = discontext.getText().toString();
            String mrd = mread.getText().toString();
            String mrdT = mreadtext.getText().toString();
            String mbrnd = mbrand.getText().toString();
            String mbrndT = mbrandtext.getText().toString();
            String mno = mnum.getText().toString();
            String mnoT = mnumtext.getText().toString();
            String prv2 = prev2.getText().toString();
            String prv2T = prev2text.getText().toString();
            String prs2 = pres2.getText().toString();
            String prs2T = pres2text.getText().toString();
            String use = usage.getText().toString();
            String useT = usagetext;
            String amount = amnt.getText().toString();
            String amountT = amnttext;
            String arrears_tmp = arrears.getText().toString();
            String arrears_val = arrears_tv.getText().toString();
            String staggered_tmp = staggered.getText().toString();
            String staggered_val = staggered_tv.getText().toString();
            String tamnt = ttlamnt.getText().toString();
            String tamntT = ttlamnttext;
            String bord3 = border3.getText().toString();
            String mess = messag.getText().toString()+"\n"+messag1.getText().toString()+"\n"+messag2.getText().toString()+"\n"+messag3.getText().toString()+"\n"+messag4.getText().toString()+"\n"+messag5.getText().toString();

            String header ="     "+rep+"\n"+"            "+cit+"\n"+"               "+prov+"\n"+" "+car+"\n"+"   "+vic+"\n"+"         "+phone+"\n"+"             "+state+"\n"+boord+"\n";

            String msg = header+acclabel+" "+accset+"\n"+ccode+" "+ctext+"\n"+bil+" "+bilT+"\n"+nm+" "+nmtext+"\n"+sequence+" "+seqt+"\n"+address+" "+addressT+"\n"+aven+" "+avenT
                    +"\n"+bord2+"\n"+mtr+"\n"+prv+" "+prvT+"\n"+prs+" "+prsT+"\n"+du+" "+duT+"\n"+dscon+" "+dsconT+"\n"+mrd+" "+mrdT+"\n"+mbrnd+" "+mbrndT+"\n"+mno+" "+mnoT+"\n"+prv2+" "+prv2T+"\n"+prs2+" "+prs2T+"\n"+use+" "+useT+"\n"+amount+" "+amountT
                    +"\n" + arrears_tmp + " " + arrears_val +"\n" + staggered_tmp + " " + staggered_val + "\n" +tamnt+" "+tamntT+"\n"+bord3+"\n"+mess+"\n"+"\n"+"\n"+"\n";

            mmOutputStream.write(msg.getBytes());

            //tell the user data were sent
//            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"something went wrong.", Toast.LENGTH_LONG).show();
        }
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth CLosed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
