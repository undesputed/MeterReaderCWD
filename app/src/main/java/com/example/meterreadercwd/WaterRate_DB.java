package com.example.meterreadercwd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WaterRate_DB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "water_rate.db";
    public static final String TABLE_NAME = "water_rate";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "CLASSIFICATION_ID";
    public static final String COL_3 = "CLASS_NAME";
    public static final String COL_4 = "SIZES";
    public static final String COL_5 = "MINIMUM_CHARGE";
    public static final String COL_6 = "COMMODITY_CHARGE1120";
    public static final String COL_7 = "COMMODITY_CHARGE2130";
    public static final String COL_8 = "COMMODITY_CHARGE3140";
    public static final String COL_9 = "COMMODITY_CHARGE41UP";

    public WaterRate_DB( Context context) {
        super(context,DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY, CLASSIFICATION_ID TEXT, CLASS_NAME TEXT, SIZES TEXT, MINIMUM_CHARGE TEXT, COMMODITY_CHARGE1120 TEXT, COMMODITY_CHARGE2130 TEXT, COMMODITY_CHARGE3140 TEXT, COMMODITY_CHARGE41UP TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int col1, String col2, String col3, String col4, String col5, String col6, String col7, String col8, String col9) {
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
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void truncateData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public Cursor getRate(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT MINIMUM_CHARGE, COMMODITY_CHARGE1120, COMMODITY_CHARGE2130, COMMODITY_CHARGE3140, COMMODITY_CHARGE41UP FROM water_rate WHERE ID = '" + id + "'", null);

        return res;
    }
}
