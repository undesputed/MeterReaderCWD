package com.example.meterreadercwd;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class UploadedActivity extends AppCompatActivity {
    private Intent intent;
    private SearchView search_list;
    private CustomAdapter_2 adapter_v2;
    private ArrayList<Applicant_Helper> list = new ArrayList<>();
    Applicant_DB applicant_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Uploaded");

        search_list = (SearchView) findViewById(R.id.searchView_uploaded);
        applicant_db = new Applicant_DB(this);


        ListView readed_list = (ListView) findViewById(R.id.results_listview_v2_uploaded);
        adapter_v2 = new CustomAdapter_2(this, list);
        listUploadedApplicant();
        readed_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(UploadedActivity.this, CalculateActivity.class);
                intent.putExtra("account_no", adapter_v2.getAccountNumber(position));
                intent.putExtra("uploaded", true);
                startActivity(intent);
                finish();
            }
        });
        readed_list.setAdapter(adapter_v2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void listUploadedApplicant() {
        Cursor res = applicant_db.getApplicantInfoUploaded();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No datas yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            list.add(new Applicant_Helper(res.getString(0), res.getString(1)));
        }
    }
}
