package com.example.meterreadercwd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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

public class ReadActivity extends AppCompatActivity {
    private Intent intent;
    private SearchView search_list;
    private CustomAdapter_2 adapter_v2;
    private ArrayList<Applicant_Helper> list = new ArrayList<>();
    Applicant_DB applicant_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Read applicants");

        search_list = (SearchView) findViewById(R.id.searchView_readed);

        applicant_db = new Applicant_DB(this);

        ListView readed_list = (ListView) findViewById(R.id.results_listview_v2_readed);
        adapter_v2 = new CustomAdapter_2(this, list);
        listReadedApplicant();

        readed_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ReadActivity.this, CalculateActivity.class);
                intent.putExtra("account_no", adapter_v2.getAccountNumber(position));
                intent.putExtra("read", true);
                startActivity(intent);
                finish();
            }
        });

        readed_list.setAdapter(adapter_v2);

        search_list.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter_v2.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else if (id == R.id.upload_data) {
//            upload_data();
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else if (id == R.id.delete_uploadData) {
            applicant_db.deleteReadData();
        }

        return true;
    }

    public void listReadedApplicant() {
        Cursor res = applicant_db.getApplicantInfoRead();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No datas yet.", Toast.LENGTH_SHORT).show();
//            list.add(new Applicant("NO", "data's recovered!"));
            return;
        }

        while (res.moveToNext()) {
            // todo...
            list.add(new Applicant_Helper(res.getString(0), res.getString(1)));
        }
    }
    public void upload_data() {
        String adv_temp;
        Cursor res = applicant_db.getDataForUpload();
        if(res.getCount() == 0) {
            Toast.makeText(this, "No datas found!", Toast.LENGTH_SHORT).show();
            return;
        }

        res.moveToFirst();
        adv_temp = res.getString(8).equals("0")? "NO" : res.getString(8);
        Toast.makeText(this, "id: " + res.getString(0) +
                "\nzone_id: " + res.getString(1) +
                "\nbook_id: " + res.getString(2) +
                "\ntrans_date: " + PreferenceManager.getInstance(getApplicationContext()).getTrans_date() +
                "\nprev_rdg: " + res.getString(3) +
                "\ncurr_rdg: " + res.getString(4) +
                "\nusage: " + res.getString(5) +
                "\nbillamout: " + res.getString(6) +
                "\noriginal_bill: " + res.getString(7) +
                "\nadvance: " + adv_temp, Toast.LENGTH_SHORT).show();
    }


    class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... voids) {
            String adv_temp;
            Cursor res = applicant_db.getDataForUpload();
            if(res.getCount() == 0) {
                Toast.makeText(ReadActivity.this, "No datas found!", Toast.LENGTH_SHORT).show();
                return null;
            }

            while (res.moveToNext()) {
                adv_temp = res.getString(8).equals("0")? "NO" : res.getString(8);
                uploadApplicantData(res.getString(0), res.getString(1),
                        res.getString(2), PreferenceManager.getInstance(getApplicationContext()).getTrans_date(),
                        res.getString(3), res.getString(4), res.getString(5),
                        res.getString(6), res.getString(7), adv_temp, res.getString(9));

                applicant_db.moveToUploaded(res.getString(0));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ReadActivity.this,
                    "Uploading data",
                    "Please wait...");
        }

        @Override
        protected void onPostExecute(Void param) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            Toast.makeText(ReadActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
        }

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

    private void update_data(String id) {
    }
}
