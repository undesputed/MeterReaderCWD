package com.example.meterreadercwd;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ZoneActivity extends AppCompatActivity {
    private final ArrayList<Zone_Helper> list = new ArrayList<>();
    private SearchView searchView;
    private CustomAdapter_1 adapter_v1;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchView = (SearchView) findViewById(R.id.zone_filter_SV);
        ListView resultsList = (ListView) findViewById(R.id.results_listview_v1);
        adapter_v1 = new CustomAdapter_1(this, list);


        getZoneInfo();

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
            {
//                countAllApplicant(adapter_v1.getZoneid(position));
//                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

//                size = Integer.parseInt(PreferenceManager.getInstance(getApplicationContext()).getApp_total().trim());
//                size = Integer.parseInt(PreferenceManager.getInstance(getApplicationContext()).getApp_total().trim());
                Intent intent = new Intent(ZoneActivity.this, SyncActivity.class);
                intent.putExtra("zone_id", adapter_v1.getZoneid(position));
                intent.putExtra("zone_number", adapter_v1.getZoneNumber(position));
                intent.putExtra("zone_loc", adapter_v1.getZoneName(position));
                startActivity(intent);

//                AlertDialog.Builder builder = new AlertDialog.Builder(ZoneActivity.this);
//                builder.setTitle("Zone: " + adapter_v1.getZoneNumber(position));
//
//                builder.setMessage("Sync data on: \n" + adapter_v1.getZoneName(position)
//                    + "\nTotal no. of applicants: " + size);
//                final EditText input_limit = new EditText(ZoneActivity.this);
//                input_limit.setPadding(50, 10, 50, 10);
//                input_limit.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                builder.setView(input_limit);
//                builder.setPositiveButton(R.string.action_sync, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                getApplicant(adapter_v1.getZoneid(position)); ..
//
////                                countAllApplicant(adapter_v1.getZoneid(position));
//
//                                try {
////                                    size = Integer.parseInt(PreferenceManager.getInstance(getApplicationContext()).getApp_total().trim());
////                                    runner.execute(adapter_v1.getZoneid(position), size+"");
//                                    AsyncTaskRunner runner = new AsyncTaskRunner();
//                                    runner.execute(adapter_v1.getZoneid(position), input_limit.getText().toString());
//                                    setTransDate();
//                                    setZoneInfo(adapter_v1.getZoneNumber(position), adapter_v1.getZoneName(position));
//                                } catch (Exception e) {
//                                    Toast.makeText(ZoneActivity.this, "No records found! \n"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                Toast.makeText(ZoneActivity.this, "trans date: " + PreferenceManager.getInstance(getApplicationContext()).getTrans_date(), Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .setIcon(R.drawable.ic_place)
//                        .show();
            }
        });

        resultsList.setAdapter(adapter_v1);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter_v1.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // getting zone info
    private void getZoneInfo() {
        String ROOT_URL_ZONE = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_GET_ZONE_DATA;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_ZONE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if(jsonArray != null) {
                                JSONArray var_temp = jsonArray.getJSONArray(1);
                                if(var_temp != null) {
                                    boolean result = true;
                                    final int size = var_temp.length();
                                    String[] temp = new String[3];
                                    for(int i = 0; i < size; i++) {
                                        JSONObject jsonObject = var_temp.getJSONObject(i);
                                        temp[0] = jsonObject.getString("name");
                                        temp[1] = jsonObject.getString("location");
                                        temp[2] = jsonObject.getString("id");

                                        list.add(new Zone_Helper(temp[0], temp[1], temp[2]));

                                        if(!result){
                                            Toast.makeText(getApplicationContext(),"Data sync failed! ERROR:1.3", Toast.LENGTH_SHORT).show();}
                                    }
                                    System.gc();
                                    if(result){Toast.makeText(getApplicationContext(),"Data sync success!", Toast.LENGTH_SHORT).show();}

                                }else {
                                    Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.2", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.1", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        System.gc();
    }

//    private void getApplicant(String zone_number) {
//
//        countAllApplicant(zone_number);
//
//        final int size = Integer.parseInt(PreferenceManager.getInstance(getApplicationContext()).getApp_total().trim());
////        String total = PreferenceManager.getInstance(getApplicationContext()).getApp_total();
////        Toast.makeText(ZoneActivity.this, "total : "+ total, Toast.LENGTH_SHORT).show();
//        for(int i = 0; i < size; i++) {
//            getZoneApplicant(zone_number, i+"");
//        }
//
////        new ProcessAsyncTask(this).execute(zone_number);
////            getZoneApplicant(zone_number);
//    }
//
//
//    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
//
//        private String resp;
//        ProgressDialog progressDialog;
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
////                int time = Integer.parseInt(params[0])*1000;
//                countAllApplicant(params[0]);
//
//                final int size = Integer.parseInt(params[1]) + 100;
//
//                final int temp = Integer.parseInt(params[1]) - 1 < 0? 0 : Integer.parseInt(params[1]) - 1;
//                resp = "Downloading data.";
//                for(int i = temp; i < size; i++) {
//                    getZoneApplicant(params[0], i+"");
//                }
//
////                Thread.sleep(1500);
//                resp = "Updating important data's";
//
////                publishProgress("Updating required data's...3");
//                getWaterRate();
//
//                Thread.sleep(1500);
//
////                resp = "Slept for " + params[0] + " seconds";
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            } catch (Exception e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            }
//            return resp;
//        }
//
//
//        @Override
//        protected void onPostExecute(String result) {
//            // execution of result of Long time consuming operation
//            progressDialog.dismiss();
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(ZoneActivity.this,
//                    "Downloading data",
//                    "Please wait...");
//        }
//
//
//        @Override
//        protected void onProgressUpdate(String... text) {
//            progressDialog.setMessage("onProgressUpdate right now..." + text[0]);
//
//        }
//    }


//    // COUNTING ALL APPLICANTS IN THE ZONE
//    private void countAllApplicant(final String zone_number) {
//        final String ROOT_URL_TOTAL = PreferenceManager.getInstance(getApplicationContext())
//                .getServerIpV4() + Constants.ROOT_URL_TOTAL_APPLICANT;
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                ROOT_URL_TOTAL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//
//                            JSONArray jsonArray = new JSONArray(response);
//                            if(jsonArray != null) {
//                                JSONArray var_temp = jsonArray.getJSONArray(1);
//                                if(var_temp != null) {
//                                    JSONObject jsonObject = var_temp.getJSONObject(0);
//
//                                    final String total = jsonObject.getString("total");
//                                    PreferenceManager.getInstance(getApplicationContext()).setApp_number(total);
////                                    Toast.makeText(ZoneActivity.this, "total :"+total, Toast.LENGTH_SHORT).show();
//
//                                }else {
//                                    Toast.makeText(ZoneActivity.this, "error", Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.2", Toast.LENGTH_SHORT).show();
//                                }
//                            }else {
//                                Toast.makeText(ZoneActivity.this, "error.", Toast.LENGTH_SHORT).show();
////                                Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.1", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(
//                                getApplicationContext(),
//                                error.getMessage(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                    }
//                }
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("zone_id", zone_number);
//                return params;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//        System.gc();
//    }
//
//
//    private class ProcessAsyncTask extends AsyncTask<String, Void, Void> {
//        private ProgressDialog dialog;
//
//        public ProcessAsyncTask(ZoneActivity activity) {
//            dialog = new ProgressDialog(activity);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            dialog.setMessage("Doing something, please wait.");
//            dialog.show();
//        }
//
//        protected Void doInBackground(String... params) {
//            // get the string in the params arr
//            String zone_id = params[0];
//
//            final int total = Integer.parseInt(PreferenceManager.getInstance(getApplicationContext()).getApp_total().trim());
////            for(int i = 0; i < total; i++) {
//                getZoneApplicant(zone_id, 0+"");
////            }
//
//            // do background work here
//            return null;
//        }
//
//        protected void onPostExecute(Void result) {
//            // do UI work here
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        }
//    }
//
//    private void setZoneInfo(String zone_id, String zone_location) {
//        PreferenceManager.getInstance(getApplicationContext()).setZone(zone_id, zone_location);
//    }

//    private void getZoneApplicant(final String zoneNumber, final String offset) {
//        // URL
//        String ROOT_URL_APPLICANT = PreferenceManager.getInstance(getApplicationContext())
//                .getServerIpV4() + Constants.URL_GET_APPLICANT;
//
//        final Applicant_DB applicant_db = new Applicant_DB(this);
//
//            // REQUEST
//            StringRequest stringRequest = new StringRequest(
//                    Request.Method.POST,
//                    ROOT_URL_APPLICANT,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONArray jsonArray = new JSONArray(response);
//                                if (jsonArray != null) {
//                                    JSONArray var_temp = jsonArray.getJSONArray(1);
//                                    if (var_temp != null) {
//                                        boolean result = true;
//                                        final int size = var_temp.length();
//                                        String[] temp = new String[15];
//
//                                            JSONObject jsonObject = var_temp.getJSONObject(0);
//                                            temp[0] = jsonObject.getString("id");
//                                            temp[1] = jsonObject.getString("app_no");
//                                            temp[2] = jsonObject.getString("account_no");
//                                            temp[3] = jsonObject.getString("applicant");
//                                            temp[4] = jsonObject.getString("address");
//                                            temp[5] = jsonObject.getString("rate_id");
//                                            temp[6] = jsonObject.getString("mtr_no");
//                                            temp[7] = jsonObject.getString("prev_reading");
//                                            temp[8] = jsonObject.getString("book_id");
//                                            temp[9] = jsonObject.getString("advance");
//                                            temp[10] = jsonObject.getString("is_senior_citizen");
//                                            temp[11] = jsonObject.getString("is_pwd");
//                                            temp[12] = jsonObject.getString("sequence_no");
//                                            temp[13] = jsonObject.getString("znacct_no");
//                                            temp[14] = jsonObject.getString("arrear") == "None"? "0" : jsonObject.getString("arrear");
//
////                                        Toast.makeText(ZoneActivity.this, ""+temp[14], Toast.LENGTH_SHORT).show();
//
//                                            result = applicant_db.insertData(Integer.parseInt(temp[0]), temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7], temp[8], temp[9], temp[10], temp[11], temp[12], temp[13], temp[14]);
//
//                                            if (!result) {
//                                                Toast.makeText(getApplicationContext(),"Data sync failed!", Toast.LENGTH_SHORT).show();
////                                                Intent intent = new Intent(ZoneActivity.this, MainActivity.class);
////                                                intent.putExtra("ERROR_ZONE", "DATA SYNC FAILED! ERROR 1.2");
////                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                                startActivity(intent);
////                                                finish();
//                                            }
////                                        if(result){Toast.makeText(getApplicationContext(),"Data sync success!", Toast.LENGTH_SHORT).show();}
////                                        Intent intent = new Intent(ZoneActivity.this, MainActivity.class);
////                                        intent.putExtra("ERROR_ZONE", "DATA SYNC SUCCESS!");
////                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                        startActivity(intent);
////                                        finish();
//
//                                    } else {
//                                        Toast.makeText(getApplicationContext(),"No data recovered", Toast.LENGTH_SHORT).show();
////                                        Intent intent = new Intent(ZoneActivity.this, MainActivity.class);
////                                        intent.putExtra("ERROR_ZONE", "NO DATA RECOVERED! ERROR 1.3");
////                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                        startActivity(intent);
////                                        finish();
//                                    }
//                                } else {
//                                    Toast.makeText(getApplicationContext(),"Data sync failed! 1.4", Toast.LENGTH_SHORT).show();
//
////                                    Intent intent = new Intent(ZoneActivity.this, MainActivity.class);
////                                    intent.putExtra("ERROR_ZONE", "DATA SYNC FAILED! ERROR 1.4");
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                    startActivity(intent);
////                                    finish();
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(),"DATA SYNC FAILED! ERROR 1.4", Toast.LENGTH_SHORT).show();
////                            Intent intent = new Intent(ZoneActivity.this, MainActivity.class);
////                            intent.putExtra("ERROR_ZONE", "DATA SYNC FAILED! ERROR 1.4");
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                            startActivity(intent);
////                            finish();
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("zone_name", zoneNumber);
//                    params.put("offset", offset);
//                    return params;
//                }
//            };
//            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//            System.gc();
//    }
//
//    private void getWaterRate() {
//        String ROOT_URL_RATE = PreferenceManager.getInstance(getApplicationContext())
//                .getServerIpV4() + Constants.URL_GET_WATER_RATE;
//
//        final WaterRate_DB waterRate_db = new WaterRate_DB(this);
//
//        waterRate_db.truncateData();
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                ROOT_URL_RATE,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            if(jsonArray != null) {
//                                JSONArray var_temp = jsonArray.getJSONArray(1);
//                                if(var_temp != null) {
//                                    boolean result = true;
//                                    for(int i = 0; i < var_temp.length(); i++) {
//                                        JSONObject jsonObject = var_temp.getJSONObject(i);
//                                        String col_1 = jsonObject.getString("id");
//                                        String col_2 = jsonObject.getString("classification_id");
//                                        String col_3 = jsonObject.getString("class_name");
//                                        String col_4 = jsonObject.getString("sizes");
//                                        String col_5 = jsonObject.getString("minimum_charge");
//                                        String col_6 = jsonObject.getString("commodity_charge1120");
//                                        String col_7 = jsonObject.getString("commodity_charge2130");
//                                        String col_8 = jsonObject.getString("commodity_charge3140");
//                                        String col_9 = jsonObject.getString("commodity_charge41up");
//
//                                        result = waterRate_db.insertData(Integer.parseInt(col_1), col_2, col_3, col_4, col_5, col_6, col_7, col_8, col_9);
//
//                                        if(!result){Toast.makeText(getApplicationContext(),"Data sync failed! ERROR:1.3", Toast.LENGTH_SHORT).show();}
//                                    }
//                                    if(result){Toast.makeText(getApplicationContext(),"Data sync success!", Toast.LENGTH_SHORT).show();}
//                                }else {
//                                    Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.2", Toast.LENGTH_SHORT).show();
//                                }
//                            }else {
//                                Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.1", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(
//                                getApplicationContext(),
//                                error.getMessage(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                    }
//                }
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                return params;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//    }
//
//    private void setTransDate() {
//        String ROOT_URL_TRANS_DATE = PreferenceManager.getInstance(getApplicationContext())
//                .getServerIpV4() + Constants.URL_TRANS_DATE;
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                ROOT_URL_TRANS_DATE,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//
//                            JSONArray jsonArray = new JSONArray(response);
//                            if(jsonArray != null) {
//                                JSONArray var_temp = jsonArray.getJSONArray(1);
//                                if(var_temp != null) {
//                                    JSONObject jsonObject = var_temp.getJSONObject(0);
//
//                                    final String trans_date = jsonObject.getString("trans_date");
//                                    PreferenceManager.getInstance(getApplicationContext()).setDateReq(trans_date);
//
////                                    TextView zone_temp = (TextView) findViewById(R.id.textViewZone);
////                                    zone_temp.setText(PreferenceManager.getInstance(getApplicationContext()).getTrans_date());
//
//                                }else {
//                                    Toast.makeText(ZoneActivity.this, "error", Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.2", Toast.LENGTH_SHORT).show();
//                                }
//                            }else {
//                                Toast.makeText(ZoneActivity.this, "error.", Toast.LENGTH_SHORT).show();
////                                Toast.makeText(getApplicationContext(),"No datas found. ERROR:1.1", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(
//                                getApplicationContext(),
//                                error.getMessage(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                    }
//                }
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("password", "createdbywsean");
//                return params;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//    }
}
