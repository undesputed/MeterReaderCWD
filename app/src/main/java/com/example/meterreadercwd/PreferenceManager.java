package com.example.meterreadercwd;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static PreferenceManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "user_reference_info";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String SECURITY_KEY = "password";
    private static final String USER_FULLNAME = "user_fullname";
    private static final String TYPE_USER = "type_user";
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String DEPT_CODE = "dept_code";
    private static final String PROJ_CODE = "proj_code";
    private static final String PROJ_MAIN = "proj_main";
    private static final String ZONE_ID = "zone_id";
    private static final String ZONE_LOCATION = "zone_location";
    private static final String SERVER_IP_V4 = "server_ip";
    private static final String DATE_REQUEST = "date_req";
    private static final String APP_TOTAL = "total_app";


    private PreferenceManager(Context context) {
        mCtx = context;
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreferenceManager(context);
        }
        return mInstance;
    }

    public boolean userLogin(int id, String username, String password, String user_fullname,
                             String type_user, String employee_id, String dept_code,
                             String proj_code, String proj_main) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USER_ID, id + "");
        editor.putString(KEY_USERNAME, username);
        editor.putString(SECURITY_KEY, password);
        editor.putString(USER_FULLNAME, user_fullname);
        editor.putString(TYPE_USER, type_user);
        editor.putString(EMPLOYEE_ID, employee_id);
        editor.putString(DEPT_CODE, dept_code);
        editor.putString(PROJ_CODE, proj_code);
        editor.putString(PROJ_MAIN, proj_main);

        editor.apply();

        return true;
    }

    public boolean setApp_number(String total) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(APP_TOTAL, total);
        editor.apply();
        return true;
    }

    public String getApp_total() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(APP_TOTAL, "0");
    }

    public boolean setDateReq(String date_req) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE_REQUEST, date_req);
        editor.apply();
        return true;
    }

    public boolean setZone(String zone_id, String zone_location) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ZONE_ID, zone_id);
        editor.putString(ZONE_LOCATION, zone_location);
        editor.apply();

        return true;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERNAME, null) != null) {
            return true;
        }
        return false;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        return true;
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }


    public String getUser_name() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_FULLNAME, "ERROR: PREF_MANAGER");
    }

    public String getTrans_date() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DATE_REQUEST, "ERROR: PREF_MANAGER");
    }

    public String getZone_ID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ZONE_ID, "ERROR: PREF_MANAGER");
    }

    public String getUser_id() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMPLOYEE_ID, "");
    }

    public String getSecurityKey() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SECURITY_KEY, null);
    }

    public boolean setServerIPV4(String new_ip) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SERVER_IP_V4, new_ip);
        editor.apply();
        return true;
    }

    public String getServerIpV4() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SERVER_IP_V4, null);
    }
}
