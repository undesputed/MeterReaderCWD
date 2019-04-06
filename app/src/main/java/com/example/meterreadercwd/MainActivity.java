package com.example.meterreadercwd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Intent intent;
    private ArrayList<Applicant_Helper> list = new ArrayList<>();
    private CustomAdapter_2 adapter_v2;
    private SearchView searchFilter;
    TextView meter_reader, zone_name;
    Applicant_DB applicant_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // IF USER IS NOT LOGGED IN
        if(!PreferenceManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.getBoolean("reload")) {
                finish();
                startActivity(getIntent());
            }
            if(bundle.containsKey("ERROR_ZONE")) {
                Toast.makeText(getApplicationContext(),bundle.getString("ERROR_ZONE"), Toast.LENGTH_SHORT).show();
            }
        }

        searchFilter = (SearchView) findViewById(R.id.searchView_main);
        searchFilter.setFocusable(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        zone_name = (TextView) findViewById(R.id.textViewZone);
//        zone_name.setText(PreferenceManager.getInstance(getApplicationContext()).getTrans_date());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView zone_name = (TextView) headerView.findViewById(R.id.textViewZone);
//        zone_name.setText(PreferenceManager.getInstance(getApplicationContext()).getTrans_date());
        zone_name.setText(PreferenceManager.getInstance(getApplicationContext()).getTrans_date());

        TextView meter_reader = (TextView) headerView.findViewById(R.id.textViewMReader);
        meter_reader.setText(PreferenceManager.getInstance(getApplicationContext()).getUser_name());

        applicant_db = new Applicant_DB(this);

        ListView resultList = (ListView) findViewById(R.id.results_listview_v2);
        adapter_v2 = new CustomAdapter_2(this, list);

        listApplicantInfo();

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, CalculateActivity.class);
                intent.putExtra("meter_no", adapter_v2.getAccountNumber(position));
                startActivity(intent);
                finish();

//                displayInfo(adapter_v2.getAccountNumber(position));
            }
        });

        resultList.setAdapter(adapter_v2);

        searchFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    protected void onResume() {
        super.onResume();
        list.removeAll(list);
        listApplicantInfo();
    }

    public void displayInfo(String id) {

        Cursor res = applicant_db.getApplicantInfo(id);
        if (res.getCount() == 0) {
            return;
        }

        while (res.moveToNext()) {
            Toast.makeText(MainActivity.this,
                    "ID" + res.getString(0) +
                            "\nAPP NO" + res.getString(1) +
                            "\nACCOUNT NO" + res.getString(2) +
                            "\nAPPLICANT" + res.getString(3) +
                            "\nADDRESS" + res.getString(4) +
                            "\nRATE ID" + res.getString(5) +
                            "\nMTR NO" + res.getString(6) +
                            "\nPREV RDG" + res.getString(7) +
                            "\nBOOK ID" + res.getString(8) +
                            "\nADVANCE" + res.getString(9) +
                            "\nSENIOR" + res.getString(10) +
                            "\nPWD" + res.getString(11) +
                            "\nSEQUENCE NO" + res.getString(12) +
                            "\nZNACCT NO" + res.getString(13), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();

            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // to sync activity
            Intent intent = new Intent(this, ZoneActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            // to read applicants activity
            startActivity(new Intent(this, ReadActivity.class));
        } else if(id == R.id.nav_reports) {
            Intent intent = new Intent(this, ReportsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, UploadedActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this, ServerSettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void listApplicantInfo() {
        Cursor res = applicant_db.getApplicantInfo();
        if (res.getCount() == 0) {
            return;
        }

            while (res.moveToNext()) {
                // todo...
            list.add(new Applicant_Helper(res.getString(0), res.getString(1)));
        }
    }

    private void clearData() {
        applicant_db.deleteUnreadData();
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... voids) {
            clearData();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Deleting data",
                    "Please wait...");
        }

        @Override
        protected void onPostExecute(Void param) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
        }

    }
}
