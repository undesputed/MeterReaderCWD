package com.example.meterreadercwd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private EditText usernameLoginEditText, passwordLoginEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameLoginEditText = (EditText) findViewById(R.id.usernameLoginEditText);
        passwordLoginEditText = (EditText) findViewById(R.id.passwordLoginEditText);

        progressDialog = new ProgressDialog(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set_ip) {
            startActivity(new Intent(LoginActivity.this, ServerSettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if(v == loginBtn) {
            checkLogin();
            System.gc();
        }
    }

    private void checkLogin() {
        final String username = usernameLoginEditText.getText().toString().trim();
        final String password = passwordLoginEditText.getText().toString().trim();

        String ROOT_URL = PreferenceManager.getInstance(getApplicationContext())
                .getServerIpV4() + Constants.URL_LOGIN;

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")) {

                                PreferenceManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                jsonObject.getInt("id"),
                                                jsonObject.getString("username"),
                                                jsonObject.getString("password"),
                                                jsonObject.getString("user_fullname"),
                                                jsonObject.getString("type_user"),
                                                jsonObject.getString("employee_id"),
                                                jsonObject.getString("dept_code"),
                                                jsonObject.getString("proj_code"),
                                                jsonObject.getString("proj_main")
                                        );


                                Toast.makeText(
                                        getApplicationContext(),
                                        "Login successful!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                System.gc();
                                finish();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("msg") + "ERROR_CODE:4.1",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            if(username == PreferenceManager.getInstance(getApplicationContext()).getUser_name() ||
                                    password == PreferenceManager.getInstance(getApplicationContext()).getSecurityKey() ){

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "ERROR_CODE:4.2", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
