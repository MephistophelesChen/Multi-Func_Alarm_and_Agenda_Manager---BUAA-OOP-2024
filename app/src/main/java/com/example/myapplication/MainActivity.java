package com.example.myapplication;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);
        // 创建一个MaterialDatePicker实例
        ArrayList<String> ring_content= new ArrayList<>();
        String a="1231233";
        ring_content.add("闹钟1_____time1___________________switch_button1");
        ring_content.add("闹钟2_____time2___________________switch_button2");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟4_____time4___________________switch_button4");   ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("闹钟3_____time3___________________switch_button3");
        ring_content.add("......");

        ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,ring_content);
        ListView listView=(ListView)findViewById(R.id.list_test);
        listView.setAdapter(adapter);
        adapter.insert(a,3);
    }
//我们需要的实例的类：闹钟类，日程事件类，


}