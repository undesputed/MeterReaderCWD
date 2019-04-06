//package com.example.meterreadercwd;
//
//import android.database.Cursor;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.Toast;
//
//public class Calculate_Helper extends AppCompatActivity {
//    WaterRate_DB water_rate_db;
//
//    Calculate_Helper(Double min_charge)
//
//    private void Compute(String prev, String curr, String arr, String stag, String adv) {
//        water_rate_db = new WaterRate_DB(this);
//        double previous, current, arrears, staggered, advance;
//        double total_bill = 0.00;
//
//        try { previous = Double.parseDouble(prev); } catch (Exception e) { previous = 0.00; }
//        try { current = Double.parseDouble(curr); } catch (Exception e) { current = 0.00;  }
//        try { arrears = Double.parseDouble(arr); } catch (Exception e) { arrears = 0.00; }
//        try { staggered = Double.parseDouble(stag); } catch (Exception e) { staggered = 0.00; }
//        try { advance = Double.parseDouble(adv); } catch (Exception e) { advance = 0.00; }
//
//        int usage_tmp;
//
//        try {
//            usage_tmp = (int) (current - current);
//        } catch (Exception e) {
//            usage_tmp = 0;
//        }
//        double com_bracket = usage_tmp / 10;
//
//
//        if(com_bracket >= 0 && com_bracket < 1) {
//            total_bill = min_charge;
//        } else if(com_bracket >= 1 && com_bracket < 2) {
//            charge_1120 = (usage_tmp - 10) * commodity_charge1120;
//            total_bill = charge_1120 + min_charge;
//        } else if(com_bracket >= 2 && com_bracket < 3) {
//            charge_1120 =  commodity_charge1120 * 10;
//            charge_2130 = (usage_tmp - 20) * commodity_charge2130;
//            total_bill = charge_1120 + charge_2130 + min_charge;
//        } else if(com_bracket >= 3 && com_bracket < 4) {
//            charge_1120 = commodity_charge1120 * 10;
//            charge_2130 = commodity_charge2130 * 10;
//            charge_3140 = (usage_tmp - 30) * commodity_charge3140;
//            total_bill = charge_1120 + charge_2130 + charge_3140 + min_charge;
//        } else if(com_bracket >= 4) {
//            charge_1120 = commodity_charge1120 * 10;
//            charge_2130 = commodity_charge2130 * 10;
//            charge_3140 = commodity_charge3140 * 10;
//            charge_41up = (usage_tmp - 40) * commodity_charge41up;
//            total_bill = charge_1120 + charge_2130 + charge_3140 + charge_41up + min_charge;
//        }
//
//
//        orignal_bill = total_bill;
//        //arrears
//        try {
//            total_bill = total_bill + arrears_tmp;
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        //staggered
//        try {
//            total_bill = total_bill + staggered_tmp;
//        } catch (Exception e) {
//            e.getMessage();
//        }
//
//        if(usage_tmp < 31 || usage_tmp > 1) {
//            if(is_snr || is_pwd) {
//                total_bill = total_bill * (5/100);
//            }
//        }
////        if(arrears_tv.getText().toString() != "None") {
////            total_bill = total_bill + Double.parseDouble(arrears_tv.getText().toString());
////        }
//
//        // if applicant has advance payment
////        total_bill = total_bill - Double.parseDouble(advance_et.getText().toString());
//        total_bill = total_bill - adv_tmp;
//
//        String arr_tmp = "None";
//
//        usage.setText(usage_tmp+"");
//        advance_et.setText(numberFormat.format(adv_tmp)+"");
//        try {
//            if(Double.parseDouble(arrears_tmp+"") > 0 ) {
//                arr_tmp = arrears_tmp +"";
//            }
//        } catch (Exception e) { }
//        arrears_et.setText(arr_tmp+"");
//        try {
//            staggered_et.setText(numberFormat.format(staggered_tmp)+"");
//        } catch (Exception e) {
//            staggered_et.setText(staggered_tmp+"");
//        }
//
//        bill_amount.setText(numberFormat.format(total_bill) + "");
//    }
//}
