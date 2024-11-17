package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class main_setting_activity extends AppCompatActivity {
    ButtonManager btnManager = new ButtonManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_setting);
        Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
        btnManager.switchToActivity_btn(to_alarm_btn, main_setting_activity.this, main_alarm_activity.class);

        Button to_timer_btn = (Button) findViewById(R.id.to_timer_btn);
        btnManager.switchToActivity_btn(to_timer_btn, main_setting_activity.this, main_timer_activity.class);

        Button to_date_btn = (Button) findViewById(R.id.to_date_btn);
        btnManager.switchToActivity_btn(to_date_btn, main_setting_activity.this, main_date_activity.class);
    }
}
