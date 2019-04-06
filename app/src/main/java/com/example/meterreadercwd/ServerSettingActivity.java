package com.example.meterreadercwd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServerSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText text_input_ip;
    private Button btn_save_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);

        text_input_ip = findViewById(R.id.editText_ip);
        btn_save_ip = findViewById(R.id.btn_save_ip);
        btn_save_ip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == btn_save_ip) {
            String ROOT_URL = "http://" + text_input_ip.getText().toString().trim() + "/cwd/";
            PreferenceManager.getInstance(getApplicationContext()).setServerIPV4(ROOT_URL);

            startActivity(new Intent(ServerSettingActivity.this, MainActivity.class));
        }
    }

}
