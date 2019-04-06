package com.example.meterreadercwd;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class CalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView account_num, account_name, usage, bill_amount, advance_tv, arrears_tv, staggered_tv;
    private Button printBtn;
    private EditText current_reading, prev_reading, staggered_et, arrears_et, advance_et;
    private Intent intent;
    private Bundle bundle;
    private String isRead;
    Applicant_DB app_DB;
    WaterRate_DB water_rate_db;

    private String rate_id;
    private Double min_charge;
    private Double commodity_charge1120;
    private Double commodity_charge2130;
    private Double commodity_charge3140;
    private Double commodity_charge41up;
    private Double orignal_bill;
    private Double arrears;
    private boolean is_snr, is_pwd;
    private DecimalFormat numberFormat = new DecimalFormat("#0.00");
    private double curr_val;
    private double prev_val;
    private String arrears_val;
    private double advance_val;
    private String account_number;
    private String rdg_date, due_date, penalty_date, disc_date, sequence_no, address, trans_no, class_code, meter_no, meter_brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        app_DB = new Applicant_DB(this);
        water_rate_db = new WaterRate_DB(this);

        account_num = (TextView) findViewById(R.id.textViewAccNum);
        account_name = (TextView) findViewById(R.id.textViewAccName);
        prev_reading = (EditText) findViewById(R.id.editTextPrevReading);
        current_reading = (EditText) findViewById(R.id.editTextCurrentReading);
        printBtn = (Button) findViewById(R.id.btnSave);
        usage = (TextView) findViewById(R.id.textViewUsage);
//        usage_et = (EditText) findViewById(R.id.textViewUsage);
        bill_amount = (TextView) findViewById(R.id.textViewBillAmount);
        advance_tv = (TextView) findViewById(R.id.textViewAdvance);
//        advance_et = (EditText) findViewById(R.id.textViewAdvance);
        arrears_tv = (TextView) findViewById(R.id.textViewArrears);
//        arrears_et = (EditText) findViewById(R.id.textViewArrears);
        staggered_tv = (TextView) findViewById(R.id.textViewStaggered);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            account_num.setText(bundle.getString("meter_no"));
            Cursor res = app_DB.getDataForProcess(bundle.getString("meter_no"));
            res.moveToFirst();

            if (res.getCount() == 0) {
                Toast.makeText(this, "No datas recovered!", Toast.LENGTH_SHORT).show();
            } else {
                account_name.setText(res.getString(0));
                prev_reading.setText(res.getString(1));
                rate_id = res.getString(2);
                is_snr = res.getString(3).equals("YES");
                is_pwd = res.getString(4).equals("YES");
//                advance_et.setText(res.getString(5));
//                arrears_et.setText(res.getString(6));
                try {
                    advance_val = Double.parseDouble(res.getString(5));
                } catch (Exception e) {
                    advance_val = 0.00;
                }
                arrears_val = res.getString(6);
                advance_tv.setText(advance_val +"");
                arrears_tv.setText(arrears_val);
//                advance_et.setText(res.getString(5));
//                arrears_et.setText(res.getString(6));
//                isRead = res.getString(7).equals("YES") || res.getString(7).equals("UPLOADED");
                trans_no = res.getString(9);
                isRead = res.getString(7);
                staggered_tv.setText(res.getString(10));

                if(isRead.equals("YES") || isRead.equals("UPLOADED")) {
                    current_reading.setText(res.getString(8));
                    calculateReading();
                }
            }
            getApplicantExtras(account_num.getText().toString());
            current_reading.requestFocus();
        }

        printBtn.setOnClickListener(this);


        current_reading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateReading();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        prev_reading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 calculateReading();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            if(isRead.equals("YES")) {
                startActivity(new Intent(getApplicationContext(), ReadActivity.class));
                finish();
            } else if(isRead.equals("UPLOADED")) {
                startActivity(new Intent(getApplicationContext(), UploadedActivity.class));
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }

        return true;
    }

    private void setCustomerRate() {
        Cursor res = water_rate_db.getRate(rate_id);
        res.moveToFirst();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No datas recovered on customer rate.", Toast.LENGTH_SHORT).show();
        } else {
            min_charge = Double.parseDouble(res.getString(0));
            commodity_charge1120 = Double.parseDouble(res.getString(1));
            commodity_charge2130 = Double.parseDouble(res.getString(2));
            commodity_charge3140 = Double.parseDouble(res.getString(3));
            commodity_charge41up = Double.parseDouble(res.getString(4));

        }
    }

    @Override
    public void onClick(View v) {
        if(v == printBtn) {
//            Toast.makeText(this, ""+staggered_et.getText(), Toast.LENGTH_SHORT).show();
            intent = new Intent(CalculateActivity.this, PrintActivity.class);
            intent.putExtra("account_num", account_number);
            intent.putExtra("applicant_name", account_name.getText().toString());
            intent.putExtra("prev_reading", prev_reading.getText().toString());
            intent.putExtra("usage", usage.getText().toString());
            intent.putExtra("bill_amount", bill_amount.getText().toString());
            intent.putExtra("current_reading", current_reading.getText().toString());
            intent.putExtra("arrears", arrears_tv.getText().toString());
            intent.putExtra("advance", advance_tv.getText().toString());
            intent.putExtra("isRead", isRead);
            intent.putExtra("reading_date", rdg_date);
            intent.putExtra("due_date", due_date);
            intent.putExtra("penalty_date", penalty_date);
            intent.putExtra("disc_date", disc_date);
            intent.putExtra("sequence_no", sequence_no);
            intent.putExtra("address", address);
            intent.putExtra("trans_no", trans_no);
            intent.putExtra("class_code", class_code);
            intent.putExtra("meter_no", meter_no);
            intent.putExtra("meter_brand", meter_brand);
            intent.putExtra("original_bill", numberFormat.format(orignal_bill));
            intent.putExtra("staggered", staggered_tv.getText()+"");
            startActivity(intent);
        }
    }

    private void getApplicantExtras(String id) {
        Cursor res = app_DB.getApplicantExtras(bundle.getString("meter_no"));
        res.moveToFirst();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No extra data's recovered!", Toast.LENGTH_SHORT).show();
        } else {
            rdg_date = res.getString(0);
            due_date = res.getString(1);
            penalty_date = res.getString(2);
            disc_date = res.getString(3);
            sequence_no = res.getString(4);
            address = res.getString(5);
            class_code = res.getString(6);
            meter_no = res.getString(7);
            meter_brand = res.getString(8);
            account_number = res.getString(9);
        }
    }

    private void calculateReading() {
        double arrears_tmp, adv_tmp, staggered_tmp;
        int usage_tmp;
        setCustomerRate();

        try { prev_val = Double.parseDouble(prev_reading.getText().toString().trim()); } catch (Exception e) { prev_val = 0.00; }
        try { curr_val = Double.parseDouble(current_reading.getText().toString().trim());  } catch (Exception e) { curr_val = 0.00; }
        try { arrears_tmp = Double.parseDouble(arrears_tv.getText().toString().trim()); } catch (Exception e) { arrears_tmp = 0.00; }
        try { adv_tmp = Double.parseDouble(advance_tv.getText().toString().trim()); } catch (Exception e) { adv_tmp = 0.00; }
        try { staggered_tmp = Double.parseDouble(staggered_tv.getText().toString().trim()); } catch (Exception e) { staggered_tmp = 0.00; }

        try {
            usage_tmp = (int) (curr_val - prev_val);
        } catch (Exception e) {
            usage_tmp = 0;
        }
        double com_bracket = usage_tmp / 10;
        double total_bill = 0.00;
        double charge_1120 = 0.00;
        double charge_2130 = 0.00;
        double charge_3140 = 0.00;
        double charge_41up = 0.00;


        if(com_bracket >= 0 && com_bracket < 1) {
            total_bill = min_charge;
        } else if(com_bracket >= 1 && com_bracket < 2) {
            charge_1120 = (usage_tmp - 10) * commodity_charge1120;
            total_bill = charge_1120 + min_charge;
        } else if(com_bracket >= 2 && com_bracket < 3) {
            charge_1120 =  commodity_charge1120 * 10;
            charge_2130 = (usage_tmp - 20) * commodity_charge2130;
            total_bill = charge_1120 + charge_2130 + min_charge;
        } else if(com_bracket >= 3 && com_bracket < 4) {
            charge_1120 = commodity_charge1120 * 10;
            charge_2130 = commodity_charge2130 * 10;
            charge_3140 = (usage_tmp - 30) * commodity_charge3140;
            total_bill = charge_1120 + charge_2130 + charge_3140 + min_charge;
        } else if(com_bracket >= 4) {
            charge_1120 = commodity_charge1120 * 10;
            charge_2130 = commodity_charge2130 * 10;
            charge_3140 = commodity_charge3140 * 10;
            charge_41up = (usage_tmp - 40) * commodity_charge41up;
            total_bill = charge_1120 + charge_2130 + charge_3140 + charge_41up + min_charge;
        }


        orignal_bill = total_bill;
        //arrears
        try {
            total_bill = total_bill + arrears_tmp;
        } catch (Exception e) {
            e.getMessage();
        }
        //staggered
        try {
            total_bill = total_bill + staggered_tmp;
        } catch (Exception e) {
            e.getMessage();
        }

        if(usage_tmp < 31 || usage_tmp > 1) {
            if(is_snr || is_pwd) {
                total_bill = total_bill * (5/100);
            }
        }
//        if(arrears_tv.getText().toString() != "None") {
//            total_bill = total_bill + Double.parseDouble(arrears_tv.getText().toString());
//        }

        // if applicant has advance payment
//        total_bill = total_bill - Double.parseDouble(advance_et.getText().toString());
        total_bill = total_bill - adv_tmp;

        String arr_tmp = "None";

        usage.setText(usage_tmp+"");
        advance_tv.setText(numberFormat.format(adv_tmp)+"");
        try {
            if(Double.parseDouble(arrears_tmp+"") > 0 ) {
                arr_tmp = arrears_tmp +"";
            }
        } catch (Exception e) { }
        arrears_tv.setText(arr_tmp+"");
        try {
            staggered_tv.setText(numberFormat.format(staggered_tmp)+"");
        } catch (Exception e) {
            staggered_tv.setText(staggered_tmp+"");
        }

        bill_amount.setText(numberFormat.format(total_bill) + "");
    }
}
