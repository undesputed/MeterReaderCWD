package com.example.meterreadercwd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Applicant_DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "applicant.db";
    private static final String TABLE_NAME = "applicant";
//    private static final String COL_16 = "USAGE";
//    private static final String COL_17 = "CURR_RDG";
//    private static final String COL_18 = "BILL_PAYMENT";
//    private static final String COL_19 = "READ";
//    private static final String COL_24 = "TRANS_NO";
//    private static final String COL_28 = "ORIGINAL_BILL";

    private static final String COL_1 = "ID";
    private static final String COL_2 = "CUSTOMER_ID";
    private static final String COL_3 = "CUSTOMER_NAME";
    private static final String COL_4 = "CUSTOMER_ADDRESS";
    private static final String COL_5 = "ACCOUNT_NO";
    private static final String COL_6 = "ACCT_NO";
    private static final String COL_7 = "RATE_ID";
    private static final String COL_8 = "RATE_NAME"; // CLASSCODE
    private static final String COL_9 = "METER_NO";
    private static final String COL_10 = "METER_BRAND";
    private static final String COL_11 = "SEQUENCE_NO";
    private static final String COL_12 = "AVERAGE_USAGE";
    private static final String COL_13 = "CARE_OF";
    private static final String COL_14 = "SCHEDULE_ID";
    private static final String COL_15 = "PREVIOUS_READING_DATE";
    private static final String COL_16 = "PRESENT_READING_DATE";
    private static final String COL_17 = "DUE_DATE";
    private static final String COL_18 = "DISCONNECTION_DATE";
    private static final String COL_19 = "PENALTY_DATE";
    private static final String COL_20 = "PREVIOUS_READING";
    private static final String COL_21 = "ADVANCE";
    private static final String COL_22 = "ARREARS";
    private static final String COL_23 = "STAGGARD";
    private static final String COL_24 = "IS_SENIOR_CITIZEN";
    private static final String COL_25 = "IS_PWD";
    private static final String COL_26 = "ZONE_ID";
    private static final String COL_27 = "BOOK_ID";
    private static final String COL_28 = "USAGE";
    private static final String COL_29 = "CURRENT_RDG";
    private static final String COL_30 = "BILL_PAYMENT";
    private static final String COL_31 = "USAGE_BILL";
    private static final String COL_32 = "TRANSACTION_NO";
    private static final String COL_33 = "STATUS";

    public Applicant_DB( Context context) {
        super(context,DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER, APP_NO TEXT, ACCOUNT_NO TEXT, APPLICANT TEXT, ADDRESS TEXT, RATE_ID TEXT, MTR_NO TEXT, PREV_RDG TEXT, BOOK_ID TEXT, ADVANCE TEXT, SENIOR_CITIZEN TEXT, PWD TEXT, SEQUENCE_NO TEXT, ZNACCT_NO TEXT, ARREARS TEXT, USAGE TEXT, CURR_RDG TEXT, BILL_PAYMENT TEXT, READ TEXT, READING_DATE TEXT, DUE_DATE TEXT, PENALTY_DATE TEXT, DISCONNECTION_DATE TEXT, TRANS_NO TEXT, CLASS_CODE TEXT, METER_NO TEXT, METER_BRAND TEXT, ORIGINAL_BILL TEXT, ZONE_ID TEXT, STAGGERED TEXT)");
//        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER, CUSTOMER_ID INTEGER, CUSTOMER_NAME TEXT, CUSTOMER_ADDRESS TEXT, ACCOUNT_NO TEXT, ACCT_NO TEXT, RATE_ID TEXT, RATE_NAME TEXT, METER_NO TEXT," +
//                "METER_BRAND TEXT, SEQUENCE_NO TEXT, AVERAGE_USAGE TEXT, CARE_OF TEXT, SCHEDULE_ID TEXT, PREVIOUS_READING_DATE TEXT, PRESENT_READING_DATE TEXT, DUE_DATE TEXT, DISCONNECTION_DATE TEXT," +
//                "PENALTY_DATE TEXT, PREVIOUS_READING TEXT, ADVANCE TEXT, ARREARS TEXT, STAGGARD TEXT, IS_SENIOR_CITIZEN TEXT, IS_PWD TEXT, ZONE_ID TEXT, BOOK_ID TEXT, USAGE TEXT, CURRENT_RDG TEXT," +
//                "BILL_PAYMENT TEXT, USAGE_BILL TEXT, TRANSACTION_NO TEXT, STATUS TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER, CUSTOMER_ID INTEGER, CUSTOMER_NAME TEXT, CUSTOMER_ADDRESS TEXT, ACCOUNT_NO TEXT, ACCT_NO TEXT, RATE_ID TEXT," +
                "RATE_NAME TEXT, METER_NO TEXT, METER_BRAND TEXT, SEQUENCE_NO TEXT, AVERAGE_USAGE TEXT, CARE_OF TEXT, SCHEDULE_ID TEXT, PREVIOUS_READING_DATE TEXT, " +
                "PRESENT_READING_DATE TEXT, DUE_DATE TEXT, DISCONNECTION_DATE TEXT, PENALTY_DATE TEXT, PREVIOUS_READING TEXT, ADVANCE TEXT, ARREARS TEXT," +
                "STAGGARD TEXT, IS_SENIOR_CITIZEN TEXT, IS_PWD TEXT, ZONE_ID TEXT, BOOK_ID TEXT, USAGE TEXT, CURRENT_RDG TEXT, BILL_PAYMENT TEXT, USAGE_BILL TEXT, " +
                "TRANSACTION_NO TEXT, STATUS TEXT)");

    }
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER, APP_NO TEXT, ACCOUNT_NO TEXT, APPLICANT TEXT, ADDRESS TEXT)");
//    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void truncateData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public boolean insertData(int col1, int col2, String col3, String col4, String col5, String col6, String col7,
                              String col8, String col9, String col10, String col11, String col12, String col13, String col14,
                              String col15, String col16, String col17, String col18, String col19, String col20, String col21,
                              String col22, String col23, String col24, String col25, String col26, String col27) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        contentValues.put(COL_5, col5);
        contentValues.put(COL_6, col6);
        contentValues.put(COL_7, col7);
        contentValues.put(COL_8, col8);
        contentValues.put(COL_9, col9);
        contentValues.put(COL_10, col10);
        contentValues.put(COL_11, col11);
        contentValues.put(COL_12, col12);
        contentValues.put(COL_13, col13);
        contentValues.put(COL_14, col14);
        contentValues.put(COL_15, col15);
        contentValues.put(COL_16, col16);
        contentValues.put(COL_17, col17);
        contentValues.put(COL_18, col18);
        contentValues.put(COL_19, col19);
        contentValues.put(COL_20, col20);
        contentValues.put(COL_21, col21);
        contentValues.put(COL_22, col22);
        contentValues.put(COL_23, col23);
        contentValues.put(COL_24, col24);
        contentValues.put(COL_25, col25);
        contentValues.put(COL_26, col26);
        contentValues.put(COL_27, col27);
        contentValues.put(COL_28, "0");
        contentValues.put(COL_29, "0");
        contentValues.put(COL_30, "0.00");
        contentValues.put(COL_31, "0.00");
        contentValues.put(COL_32, "null");
        contentValues.put(COL_33, "UNREAD");
        final long result = db.insert(TABLE_NAME, null, contentValues);
        System.gc();
        return result != -1;
    }

    public Cursor getApplicantInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT METER_NO, CUSTOMER_NAME FROM applicant WHERE STATUS = 'UNREAD'", null);
        return res;
    }

    public Cursor getApplicantInfoRead() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT METER_NO, CUSTOMER_NAME FROM applicant WHERE STATUS = 'YES'", null);
        return res;
    }

    public Cursor getApplicantInfoUploaded() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT METER_NO, CUSTOMER_NAME FROM applicant WHERE STATUS = 'UPLOADED'", null);
        return res;
    }

    public Cursor getDataForUpload() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT CUSTOMER_ID, ZONE_ID, BOOK_ID, PREVIOUS_READING, CURRENT_RDG, USAGE, BILL_PAYMENT, USAGE_BILL, ADVANCE, TRANSACTION_NO FROM applicant WHERE STATUS = 'YES'", null);
        return res;
    }

    public Cursor getDataForUpload(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT CUSTOMER_ID, ZONE_ID, BOOK_ID, PREVIOUS_READING, CURRENT_RDG, USAGE, BILL_PAYMENT, USAGE_BILL, ADVANCE, TRANSACTION_NO FROM applicant WHERE ACCOUNT_NO = '" + id + "'", null);
        return res;
    }

    public Cursor getApplicantInfo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM applicant WHERE ACCOUNT_NO = '" + id + "'", null);
        return res;
    }

    public Cursor getReportInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT ZONE_ID FROM applicant LIMIT 1", null);
        return res;
    }

    public Cursor getAllBillValues() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT BILL_PAYMENT FROM applicant WHERE STATUS = 'YES' OR STATUS = 'UPLOADED'", null);
        return res;
    }

    public Cursor getTotalRead() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(ID) FROM applicant WHERE STATUS = 'YES' OR STATUS = 'UPLOADED'", null);
        return res;
    }

    public Cursor getDataForProcess(String var) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT CUSTOMER_NAME, PREVIOUS_READING, RATE_ID, IS_SENIOR_CITIZEN, IS_PWD, ADVANCE, ARREARS, STATUS, CURRENT_RDG, TRANSACTION_NO, STAGGARD FROM applicant WHERE METER_NO = '" + var + "'", null);
        return res;
    }

    public Cursor getApplicantExtras(String var) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT PRESENT_READING_DATE, DUE_DATE, PENALTY_DATE, DISCONNECTION_DATE, SEQUENCE_NO, CUSTOMER_ADDRESS, RATE_NAME, METER_NO, METER_BRAND, ACCOUNT_NO FROM applicant WHERE METER_NO = '" + var + "'", null);
        return res;
    }

    public boolean readApplicant(String id, String usage, String current, String bill, String prev, String trans_no, String orig_bill, String arrears, String advance, String staggered) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, prev);
        contentValues.put(COL_10, advance);
        contentValues.put(COL_15, arrears);
        contentValues.put(COL_16, usage);
        contentValues.put(COL_17, current);
        contentValues.put(COL_18, bill);
        contentValues.put(COL_19, "YES");
        contentValues.put(COL_24, trans_no);
        contentValues.put(COL_28, orig_bill);
        contentValues.put(COL_30, staggered);
        db.update(TABLE_NAME, contentValues, "ACCOUNT_NO = ?", new String[] { id });
        return true;
    }

    public boolean updateUploadApplicant(String id, String usage, String current, String bill, String prev, String trans_no, String orig_bill, String arrears, String advance, String staggered) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_8, prev);
        contentValues.put(COL_10, advance);
        contentValues.put(COL_15, arrears);
        contentValues.put(COL_16, usage);
        contentValues.put(COL_17, current);
        contentValues.put(COL_18, bill);
        contentValues.put(COL_24, trans_no);
        contentValues.put(COL_28, orig_bill);
        contentValues.put(COL_30, staggered);
        db.update(TABLE_NAME, contentValues, "ACCOUNT_NO = ?", new String[] { id });
        return true;
    }

    public boolean moveToUploaded(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_19, "UPLOADED");
        db.update(TABLE_NAME, contentValues, "CUSTOMER_ID = ?", new String[] { id });
        return true;
    }

    public void deleteUnreadData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "STATUS = ?", new String[] {"UNREAD"});
    }

    public void deleteReadData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "STATUS = ?", new String[] {"YES"});
    }
}
