package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    List<Map<String,Object>> listItems;
    HashMap<String,Object> map;
    SimpleAdapter simpleAdapter;
    private String[] time = new String[]{"11:00","10:00","11:58","11:34"};
    private String[] time_1=new String[]{"AM","PM","AM","PM"};

    private static final String TAG = "MainActivity";
    private ImageButton imbtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);
        // 创建一个MaterialDatePicker实例
        ListView a=(ListView)findViewById(R.id.list_test);
        listItems=new ArrayList<Map<String, Object>>();
        for(int i=0;i<time.length;i++)
        {
            map=new HashMap<String, Object>();
            map.put("time", time[i]);
            map.put("time_1", time_1[i]);
            //把列表项加进列表集合
            listItems.add(map);
        }
     //  simpleAdapter =new SimpleAdapter(this,listItems,R.layout.data_list_res,new String[]{"time","time_1"},new int[]);
      //  a.setAdapter(simpleAdapter);

        imbtn1=(ImageButton) findViewById(R.id.add_alarm_btn);
        imbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, create_alarm_data_activity.class);
                // 启动Activity2
                startActivity(intent);
            }
        });
    }
//我们需要的实例的类：闹钟类，日程事件类，


}