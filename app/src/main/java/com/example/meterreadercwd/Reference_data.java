package com.example.meterreadercwd;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class Reference_data {
    static int index = 1;
    static double overall_total = 0.00;
    static int number_of_read = 0;
    Context context;
    Applicant_DB applicant_db;

    public Reference_data(Context c) {
        context = c;
    }

    public int update_index() {
        return index++;
    }

    public String getTransNo() {
        String trans_code = "MB-";
        String temp = PreferenceManager.getInstance(context).getUser_id();
        trans_code += PreferenceManager.getInstance(context).getTrans_date().substring(2,4);
        trans_code += PreferenceManager.getInstance(context).getTrans_date().substring(5,7);
        trans_code += "-";
        trans_code += temp.substring(temp.length() -1);
        trans_code += (int)(Math.random() * 999 + 100);
        trans_code += "-";
        trans_code += String.format("%04d", index);

        return trans_code;
    }

    public Double getOverAllBill() {
        double total = 0.00;
        Cursor res = applicant_db.getApplicantInfoUploaded();
        while (res.moveToNext()) {
            try {
                total += Double.parseDouble(res.getString(0));
            } catch (Exception E) { E.getMessage(); }
        }

        return total;
    }
}















