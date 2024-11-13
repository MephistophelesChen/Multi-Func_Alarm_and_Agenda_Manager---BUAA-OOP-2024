package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class create_alarm_time_activity extends AppCompatActivity {
    private TimePicker timePicker;
    ButtonManager btnManager=new ButtonManager();
@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm_time);
        ImageButton back=(ImageButton) findViewById(R.id.back);
        timePicker = (TimePicker) findViewById(R.id.time_picker);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton ensure=(ImageButton) findViewById(R.id.yes);

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "成功创建闹钟", Toast.LENGTH_SHORT).show();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                ArrayList<Boolean> repeatDays = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    repeatDays.add(true); // 默认每一天重复
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("hour", hour);
                resultIntent.putExtra("minute", minute);
                resultIntent.putExtra("repeatDays", repeatDays);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
