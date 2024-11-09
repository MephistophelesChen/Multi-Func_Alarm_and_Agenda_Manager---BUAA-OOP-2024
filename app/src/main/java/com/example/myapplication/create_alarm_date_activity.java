package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class create_alarm_date_activity extends AppCompatActivity {
private ButtonManager btnManager=new ButtonManager();
private ImageButton back;
private Button ensure;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm_date);
        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ensure=(Button)findViewById(R.id.alarm_date_ensure) ;
        btnManager.switchToActivity_btn(ensure,create_alarm_date_activity.this,create_alarm_time_activity.class);
    }
}
