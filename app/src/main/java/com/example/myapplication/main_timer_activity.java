package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class main_timer_activity extends AppCompatActivity {
ButtonManager btnManager=new ButtonManager();
@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_timer);
    Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
    btnManager.switchToActivity_btn(to_alarm_btn,main_timer_activity.this,main_alarm_activity.class);

    Button to_date_btn=(Button) findViewById(R.id.to_date_btn);
    btnManager.switchToActivity_btn(to_date_btn,main_timer_activity.this,main_date_activity.class);

    Button to_setting_btn =(Button) findViewById(R.id.to_setting_btn);
    btnManager.switchToActivity_btn(to_setting_btn,main_timer_activity.this,main_setting_activity.class);
}
}
