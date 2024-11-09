package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class main_date_activity extends AppCompatActivity {
    ButtonManager btnManager=new ButtonManager();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);
        //-----------------------------------------------------------------按钮逻辑部分
        Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
        btnManager.switchToActivity_btn(to_alarm_btn,main_date_activity.this,main_alarm_activity.class);

        Button to_timer_btn = (Button) findViewById(R.id.to_timer_btn);
        btnManager.switchToActivity_btn(to_timer_btn,main_date_activity.this, main_timer_activity.class);

        Button to_setting_btn = (Button) findViewById(R.id.to_setting_btn);
        btnManager.switchToActivity_btn(to_setting_btn,main_date_activity.this,main_setting_activity.class);

    }
}
