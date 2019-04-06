package com.example.meterreadercwd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView zone_num, zone_loc, total_app;
    private EditText from, upto;
    private Button btn_sync;
    private Bundle bundle;
    private String zone_id;
    Intent intent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        zone_num = (TextView) findViewById(R.id.tv_zone_num);
        zone_loc = (TextView) findViewById(R.id.tv_location);
        total_app = (TextView) findViewById(R.id.tv_total_app);
        from = (EditText) findViewById(R.id.get_from);
        upto = (EditText) findViewById(R.id.get_upto);
        btn_sync = (Button) findViewById(R.id.sync_btn);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            countAllApplicant(bundle.getString("zone_id"));
            zone_id = bundle.getString("zone_id");
            zone_num.setText(bundle.getString("zone_number"));
            zone_loc.setText(bundle.getString("zone_loc"));
        } else {
            Toast.makeText(this, "No data's recovered!", Toast.LENGTH_SHORT).show();
        }
        btn_sync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int limit_from, limit_upto;
        try {
            limit_from = Integer.parseInt(from.getText().toString());
            limit_upto = Integer.parseInt(upto.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Oops! please check your inputs.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(limit_from >= Integer.parseInt(total_app.getText().toString())) {
            Toast.makeText(this, "Oops! 'from' must be lesser than\nthe total no of applicants.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (limit_from <= 0 || limit_upto <= 0) {
            Toast.makeText(this, "Oops! Inputs must not be lesser than one.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(limit_from > limit_upto) {
            Toast.makeText(this, "Oops! 'from' must be lesser than 'up to'.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(limit_from == limit_upto) {
            Toast.makeText(this, "Oops! 'from' must be equal to 'up to'.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(limit_upto > Integer.parseInt(total_app.getText().toString())) {
            upto.setText(total_app.getText().toString());
            limit_upto = Integer.parseInt(upto.getText().toString());
        }

        limit_from--;
        limit_upto--;

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(zone_id, String.valueOf(limit_from), String.valueOf(limit_upto));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return true;
    }

    // COUNTING ALL APPLICANTS IN THE ZONE
    private void countAllApplicant(final String zone_number) {
        final String ROOT_URL_TOTAL = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.ROOT_URL_TOTAL_APPLICANT;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_TOTAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            if(jsonArray != null) {
                                JSONArray var_temp = jsonArray.getJSONArray(1);
                                if(var_temp != null) {
                                    JSONObject jsonObject = var_temp.getJSONObject(0);
                                    total_app.setText(jsonObject.getString("total"));
                                } else {
                                    Toast.makeText(SyncActivity.this, "ERR:01 total applicant not recovered.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SyncActivity.this, "ERR:02 total applicant not recovered.", Toast.LENGTH_SHORT).show();
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
                params.put("zone_id", zone_number);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        System.gc();
    }

    // Sync data
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {
                resp = "Downloading data.";

                final int from = Integer.parseInt(params[1]);
                final int upto = Integer.parseInt(params[2]);
                final int size = upto - from;

                for(int i = from; i <= upto; i++) {
                    getZoneApplicant(params[0], i+"");
                }

                Thread.sleep(size*50);
                setTransDate();
                Thread.sleep(100);
                getWaterRate();
                Thread.sleep(300);

            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SyncActivity.this,
                    "Downloading data",
                    "Please wait...");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            progressDialog.setMessage("onProgressUpdate right now..." + text[0]);

        }
    }

    private void getZoneApplicant(final String zoneNumber, final String offset) {
        // URL
        final String ROOT_URL_APPLICANT = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_GET_APPLICANT;

        final Applicant_DB applicant_db = new Applicant_DB(this);

        // REQUEST
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_APPLICANT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray != null) {
                                JSONArray var_temp = jsonArray.getJSONArray(1);
                                if (var_temp != null) {
                                    boolean result = true;
                                    final int size = var_temp.length();
                                    String[] temp = new String[27];

                                    JSONObject jsonObject = var_temp.getJSONObject(0);
                                    temp[0] = jsonObject.getString("id");
                                    temp[1] = jsonObject.getString("customer_id");
                                    temp[2] = jsonObject.getString("customer_name");
                                    temp[3] = jsonObject.getString("customer_address");
                                    temp[4] = jsonObject.getString("account_no");
                                    temp[5] = jsonObject.getString("acct_no");
                                    temp[6] = jsonObject.getString("rate_id");
                                    temp[7] = jsonObject.getString("rate_name");
                                    temp[8] = jsonObject.getString("meter_no");
                                    temp[9] = jsonObject.getString("meter_brand");
                                    temp[10] = jsonObject.getString("sequence_no");
                                    temp[11] = jsonObject.getString("average_usage");
                                    temp[12] = jsonObject.getString("care_of");
                                    temp[13] = jsonObject.getString("schedule_id");
                                    temp[14] = jsonObject.getString("previous_reading_date");// == "None"? "0" : jsonObject.getString("schedule_id"); // == "None"? "0" : jsonObject.getString("schedule_id");
                                    temp[15] = jsonObject.getString("present_reading_date");
                                    temp[16] = jsonObject.getString("due_date");
                                    temp[17] = jsonObject.getString("disconnection_date");
                                    temp[18] = jsonObject.getString("penalty_date");
                                    temp[19] = jsonObject.getString("previous_reading");
                                    temp[20] = jsonObject.getString("advance");
                                    temp[21] = jsonObject.getString("arrears");
                                    temp[22] = jsonObject.getString("staggard");
                                    temp[23] = jsonObject.getString("is_senior_citizen");
                                    temp[24] = jsonObject.getString("is_pwd");
                                    temp[25] = jsonObject.getString("zone_id");
                                    temp[26] = jsonObject.getString("book_id");
                                    ////////////////////////////////////////////////////
//                                    temp[0] = jsonObject.getString("id");
//                                    temp[1] = jsonObject.getString("app_no");
//                                    temp[2] = jsonObject.getString("account_no");
//                                    temp[3] = jsonObject.getString("applicant");
//                                    temp[4] = jsonObject.getString("address");
//                                    temp[5] = jsonObject.getString("rate_id");
//                                    temp[6] = jsonObject.getString("mtr_no");
//                                    temp[7] = jsonObject.getString("prev_reading");
//                                    temp[8] = jsonObject.getString("book_id");
//                                    temp[9] = jsonObject.getString("advance");
//                                    temp[10] = jsonObject.getString("is_senior_citizen");
//                                    temp[11] = jsonObject.getString("is_pwd");
//                                    temp[12] = jsonObject.getString("sequence_no");
//                                    temp[13] = jsonObject.getString("znacct_no");
//                                    temp[14] = jsonObject.getString("arrear") == "None"? "0" : jsonObject.getString("arrear");
//                                    temp[15] = jsonObject.getString("reading_date");
//                                    temp[16] = jsonObject.getString("due_date");
//                                    temp[17] = jsonObject.getString("penalty_date");
//                                    temp[18] = jsonObject.getString("disconnection_date");
//                                    temp[19] = jsonObject.getString("class_code");
//                                    temp[20] = jsonObject.getString("meter_no");
//                                    temp[21] = jsonObject.getString("meter_brand");
//                                    temp[22] = jsonObject.getString("zone_id");
//                                    temp[23] = jsonObject.getString("staggered");
                                    result = applicant_db.insertData(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), temp[2], temp[3], temp[4], temp[5],
                                            temp[6], temp[7], temp[8], temp[9], temp[10], temp[11], temp[12], temp[13], temp[14], temp[15], temp[16], temp[17],
                                            temp[18], temp[19], temp[20], temp[21], temp[22], temp[23], temp[24], temp[25], temp[26]);
                                    if (!result) {
                                        Toast.makeText(getApplicationContext(),"Data sync failed!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),"No data recovered", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"Data sync failed! 1.4", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"DATA SYNC FAILED! ERROR 1.4", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("zone_id", zoneNumber);
                params.put("offset", offset);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        System.gc();
    }

    private void getWaterRate() {
        String ROOT_URL_RATE = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_GET_WATER_RATE;

        final WaterRate_DB waterRate_db = new WaterRate_DB(this);

        waterRate_db.truncateData();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_RATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if(jsonArray != null) {
                                JSONArray var_temp = jsonArray.getJSONArray(1);
                                if(var_temp != null) {
                                    boolean result = true;
                                    for(int i = 0; i < var_temp.length(); i++) {
                                        JSONObject jsonObject = var_temp.getJSONObject(i);
                                        String col_1 = jsonObject.getString("id");
                                        String col_2 = jsonObject.getString("classification_id");
                                        String col_3 = jsonObject.getString("class_name");
                                        String col_4 = jsonObject.getString("sizes");
                                        String col_5 = jsonObject.getString("minimum_charge");
                                        String col_6 = jsonObject.getString("commodity_charge1120");
                                        String col_7 = jsonObject.getString("commodity_charge2130");
                                        String col_8 = jsonObject.getString("commodity_charge3140");
                                        String col_9 = jsonObject.getString("commodity_charge41up");

                                        result = waterRate_db.insertData(Integer.parseInt(col_1), col_2, col_3, col_4, col_5, col_6, col_7, col_8, col_9);

                                        if(!result){Toast.makeText(getApplicationContext(),"Data sync failed! ERROR:1.3", Toast.LENGTH_SHORT).show();}
                                    }
                                    if(result){
                                        Intent intent = new Intent(SyncActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
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
    }

    private void setTransDate() {
        String ROOT_URL_TRANS_DATE = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_TRANS_DATE;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ROOT_URL_TRANS_DATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if(jsonArray != null) {
                                JSONArray var_temp = jsonArray.getJSONArray(1);
                                if(var_temp != null) {
                                    JSONObject jsonObject = var_temp.getJSONObject(0);
                                    final String trans_date = jsonObject.getString("trans_date");
                                    PreferenceManager.getInstance(getApplicationContext()).setDateReq(trans_date);
                                }else {
                                    Toast.makeText(SyncActivity.this, "error", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(SyncActivity.this, "error.", Toast.LENGTH_SHORT).show();
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
                params.put("password", "createdbywsean");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
