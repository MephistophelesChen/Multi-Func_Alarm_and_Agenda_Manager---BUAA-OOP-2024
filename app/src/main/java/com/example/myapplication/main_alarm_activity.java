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
        a.add(true);
        a.add(false);
        a.add(true);
        a.add(false);
        a.add(true);
        a.add(false);
        a.add(true);
        alarms.add(new Alarm(8,4,a,2));

        init();

        ListView alarmList=(ListView)findViewById(R.id.list_test);
        adapter=new MyBaseAdapter(main_alarm_activity.this,this.time,repeat,map1);
        alarmList.setAdapter(adapter);

        add_alarm_btn=(ImageButton) findViewById(R.id.add_alarm_btn);
        btnManager.switchToActivity_btn(add_alarm_btn,main_alarm_activity.this,create_alarm_time_activity.class);

        to_date_btn=(Button) findViewById(R.id.to_date_btn);
        btnManager.switchToActivity_btn(to_date_btn,main_alarm_activity.this,main_date_activity.class);

        to_timer_btn=(Button) findViewById(R.id.to_timer_btn);
        btnManager.switchToActivity_btn(to_timer_btn,main_alarm_activity.this, main_timer_activity.class);

        to_setting_btn=(Button) findViewById(R.id.to_setting_btn);
        btnManager.switchToActivity_btn(to_setting_btn,main_alarm_activity.this,main_setting_activity.class);



    }
    private void init()
    {
        for(Alarm alarm:alarms)
        {
           String time=Integer.toString(alarm.hour24)+":";
            if(alarm.minute<10)
            {
                time+="0";
            }
            time+=Integer.toString(alarm.minute);
            this.time.add(time);


            String repeat="";
            if(alarm.repeat.contains(true))
            {
                repeat=Tool.addrepeat(alarm.repeat);
            }
            else {
                repeat+="不重复";
            }
            this.repeat.add(repeat);
        }
        for (int i = 0; i < this.time.size(); i++) {
            this.map1.put(this.time.get(i), false);
        }
    }

//我们需要的实例的类：闹钟类，日程事件类，

}