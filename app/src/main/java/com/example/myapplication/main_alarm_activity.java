package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main_alarm_activity extends AppCompatActivity {
    private static final int REQUEST_CODE_CREATE_ALARM = 1;
   ArrayList<String> time=new ArrayList<>();
   ArrayList<String> repeat=new ArrayList<>();
    private MyBaseAdapter adapter;
    Map<String,Boolean> map1=new HashMap<>();
    ArrayList<Alarm> alarms=new ArrayList<>();

    SimpleAdapter simpleAdapter;
    ButtonManager btnManager= new ButtonManager();
    //为列表增加内容
    private static final String TAG = "MainActivity";
    private ImageButton add_alarm_btn;
    private Button to_date_btn,to_timer_btn,to_setting_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);

        ArrayList<Boolean> a=new ArrayList<>();
        // 星期一到星期日重复情况
        //init();

        ListView alarmList = findViewById(R.id.list_test);
        adapter = new MyBaseAdapter(main_alarm_activity.this, this.time, repeat, map1);
        alarmList.setAdapter(adapter);

        add_alarm_btn = findViewById(R.id.add_alarm_btn);
        add_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_alarm_activity.this, create_alarm_time_activity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_ALARM);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_ALARM && resultCode == RESULT_OK) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            ArrayList<Boolean> repeatDays = (ArrayList<Boolean>) data.getSerializableExtra("repeatDays");

            Alarm newAlarm = new Alarm(hour, minute, repeatDays);
            alarms.add(newAlarm);

            String timeStr = String.format("%02d:%02d", hour, minute);
            time.add(timeStr);

            String repeatStr = Tool.addrepeat(repeatDays);
            repeat.add(repeatStr);

            map1.put(timeStr, false);

            adapter.notifyDataSetChanged();
        }
    }
//我们需要的实例的类：闹钟类，日程事件类，

}