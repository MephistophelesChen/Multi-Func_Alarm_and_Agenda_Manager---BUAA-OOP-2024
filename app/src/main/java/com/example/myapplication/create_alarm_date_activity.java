package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class create_alarm_date_activity extends AppCompatActivity {
private ButtonManager btnManager=new ButtonManager();
private ImageButton back;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm_date);
    back=(ImageButton) findViewById(R.id.back);
        btnManager.switchToActivity_btn(back,create_alarm_date_activity.this,main_date_activity.class);
    }
}
