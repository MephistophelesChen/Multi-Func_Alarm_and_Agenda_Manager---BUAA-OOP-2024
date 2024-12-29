package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class main_todolist_activity extends AppCompatActivity {
    ButtonManager btnManager = new ButtonManager();
    static MySQLiteOpenHelper dbHelper;
    static SQLiteDatabase db;
    public static LinkedList<date_attribute> y1y2list=new LinkedList<>();
    public static LinkedList<date_attribute> y1n2list=new LinkedList<>();
    public static LinkedList<date_attribute> n1y2list=new LinkedList<>();
    public static LinkedList<date_attribute> n1n2list=new LinkedList<>();
    public static Map<LocalDate,LinkedList<date_attribute>> dateMap = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MySQLiteOpenHelper(this);
        setContentView(R.layout.main_todolist);
        Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
        btnManager.switchToActivity_btn(to_alarm_btn, main_todolist_activity.this, main_alarm_activity.class);

        Button to_date_btn = (Button) findViewById(R.id.to_date_btn);
        btnManager.switchToActivity_btn(to_date_btn, main_todolist_activity.this, main_date_activity.class);

        Button to_setting_btn = (Button) findViewById(R.id.to_setting_btn);
        btnManager.switchToActivity_btn(to_setting_btn, main_todolist_activity.this, main_setting_activity.class);
        dateMap.clear();
        y1y2list.clear();
        y1n2list.clear();
        n1y2list.clear();
        n1n2list.clear();
        dateMap=loadDateMapFromDatabase();
        for(LinkedList<date_attribute> link:dateMap.values()){
                for(date_attribute date:link){
                    if(date.isZhongyao()&&date.isJinji()){
                        y1y2list.add(date);
                    } else if (date.isZhongyao()&&!date.isJinji()) {
                        y1n2list.add(date);
                    } else if (!date.isZhongyao()&&date.isJinji()) {
                        n1y2list.add(date);
                    }else {
                        n1n2list.add(date);
                    }
                }
        }

        todolistAdapter y1y2adapter=new todolistAdapter(y1y2list,this,R.layout.todolist_res);
        todolistAdapter y1n2adapter=new todolistAdapter(y1n2list,this,R.layout.todolist_res);
        todolistAdapter n1y2adapter=new todolistAdapter(n1y2list,this,R.layout.todolist_res);
        todolistAdapter n1n2adapter=new todolistAdapter(n1n2list,this,R.layout.todolist_res);

        ListView yylistView=findViewById(R.id.y1y2list);
        ListView ynlistView=findViewById(R.id.y1n2list);
        ListView nylistView=findViewById(R.id.n1y2list);
        ListView nnlistView= findViewById(R.id.n1n2list);

        yylistView.setAdapter(y1y2adapter);
        ynlistView.setAdapter(y1n2adapter);
        nylistView.setAdapter(n1y2adapter);
        nnlistView.setAdapter(n1n2adapter);

    }
    public static Map<LocalDate,LinkedList<date_attribute>> loadDateMapFromDatabase(){
        dateMap.clear();
        Map<LocalDate,LinkedList<date_attribute>> dateMap = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor localDateCursor = db.query(
                "LocalDateMap",
                new String[]{"date", "dateAttributeId"},
                null, null, null, null, null
        );
        while (localDateCursor.moveToNext()) {
            String dateStr = localDateCursor.getString(localDateCursor.getColumnIndexOrThrow("date"));
            LocalDate localDate = LocalDate.parse(dateStr); // 假设你的日期格式是 ISO_LOCAL_DATE
            long dateAttributeId = localDateCursor.getLong(localDateCursor.getColumnIndexOrThrow("dateAttributeId"));

            // 使用 dateAttributeId 查询 DateAttribute 表以获取属性
            Cursor dateAttributeCursor = db.query(
                    "DateAttribute",
                    new String[]{"attribute1", "attribute2", "idx","isSwitchOn","zhongyao","jinji","LocalDate"},
                    "id = ?",
                    new String[]{String.valueOf(dateAttributeId)},
                    null, null, null
            );
            if (dateAttributeCursor.moveToNext()) {
                String attribute1 = dateAttributeCursor.getString(dateAttributeCursor.getColumnIndexOrThrow("attribute1"));
                String attribute2 = dateAttributeCursor.getString(dateAttributeCursor.getColumnIndexOrThrow("attribute2"));
                int id = dateAttributeCursor.getInt(dateAttributeCursor.getColumnIndexOrThrow("idx"));
                int isSwitch = dateAttributeCursor.getInt(dateAttributeCursor.getColumnIndexOrThrow("isSwitchOn"));
                int isImportant = dateAttributeCursor.getInt(dateAttributeCursor.getColumnIndexOrThrow("zhongyao"));
                int isUrgent = dateAttributeCursor.getInt(dateAttributeCursor.getColumnIndexOrThrow("jinji"));
                String date = dateAttributeCursor.getString(dateAttributeCursor.getColumnIndexOrThrow("LocalDate"));
                boolean isSwitchOn;
                boolean important = isImportant == 1;
                boolean urgent = isUrgent==1;
                if(isSwitch==1){
                    isSwitchOn=true;
                }
                else{
                    isSwitchOn=false;
                }
                date_attribute dateAttribute = new date_attribute(attribute1, attribute2 , isSwitchOn,important,urgent);
                dateAttribute.setId(id);
                dateAttribute.setLocalDate(LocalDate.parse(date));
                // 将 dateAttribute 添加到 dateMap 中对应的日期下
                dateMap.computeIfAbsent(localDate, k -> new LinkedList<>()).add(dateAttribute);
            }dateAttributeCursor.close();
        }
        localDateCursor.close();
        return dateMap;
    }
}

