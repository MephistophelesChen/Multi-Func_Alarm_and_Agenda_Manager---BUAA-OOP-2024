package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main_date_activity extends Activity {
    //-----------------------------------------------------------------按钮逻辑部分
    private Button to_alarm_btn,to_timer_btn,to_setting_btn;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);
        to_alarm_btn=(Button) findViewById(R.id.to_alarm_btn);
        to_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_date_activity.this, main_alarm_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
