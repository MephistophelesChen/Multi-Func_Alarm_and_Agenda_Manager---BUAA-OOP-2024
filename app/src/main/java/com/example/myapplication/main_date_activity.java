package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.*;

import java.time.LocalDate;
import java.util.*;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class main_date_activity extends AppCompatActivity {
    private Button to_alarm_btn;
    private Button to_setting_btn;
    private ImageButton add_schedule;
    private static LinkedList<date_attribute> mDate = null;
    private Context mContext;
    private date_adapter mAdapter = null;
    private ListView listView;
    private CalendarView calendarView;
    private static LocalDate selectedDate;
    private static Map<LocalDate,LinkedList<date_attribute>> dateMap = new HashMap<>();
    static MySQLiteOpenHelper dbHelper;
    String TAG="mtTag";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);
        dbHelper = new MySQLiteOpenHelper(this);
//        dbHelper.deleteSQL();
        loadDateMapFromDatabase();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate=LocalDate.of(year,month+1,day);

        mContext=main_date_activity.this;
        listView=findViewById(R.id.data_list);

        mDate = new LinkedList<date_attribute>();

        Log.d(TAG,"onCreate: ");

        to_alarm_btn=findViewById(R.id.to_alarm_btn);
        to_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(main_date_activity.this, main_alarm_activity.class);
                startActivity(intent);
            }
        });

        to_setting_btn=findViewById(R.id.to_setting_btn);
        to_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(main_date_activity.this, main_setting_activity.class);
                startActivity(intent);
            }
        });


        calendarView = findViewById(R.id.calendarView);

        add_schedule = findViewById(R.id.add_schedule);
        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSchedule();
            }
        });


        //---------------------------------------------------------------
        mAdapter = new date_adapter(mDate,this,R.layout.schedule_list_item);
        listView.setAdapter(mAdapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
               // Log.d("mtTag",String.valueOf(view.getDate()));

                Log.d("help1",selectedDate.toString());
                if(dateMap.get(selectedDate)!=null) {
                    mDate=dateMap.get(selectedDate);
                    mAdapter.updateDate(mDate);
                    listView.requestFocus();

                }
                else{
                    dateMap.put(selectedDate,new LinkedList<date_attribute>());
                    mDate=dateMap.get(selectedDate);
                    mAdapter.updateDate(mDate);
                    listView.requestFocus();
                }
            }
        });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.GRAY);
                Log.d(TAG,"长按了第"+(position+1)+"项");
                AlertDialog.Builder builder = new AlertDialog.Builder(main_date_activity.this);
                builder.setItems(new String[]{"删除", "编辑"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // 删除操作
                            Log.d(TAG, "删除第" + (position + 1) + "项");
                        } else if (which == 1) {
                            // 编辑操作
                            Log.d(TAG, "编辑第" + (position + 1) + "项");
                        }
                    }
                });
                builder.show();
                return true;

            }
        });



//----------------------------------------------------------


    }


    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart: ");
    }
    @Override
    public void onResume(){
        super.onResume();
        showOnResume(selectedDate);

        Log.d(TAG,"onResume: ");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause: ");
    }
    @Override
    public void  onStop(){
        super.onStop();
        Log.d(TAG,"onStop: ");

    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart: ");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy: ");
    }

    public static Map<LocalDate, LinkedList<date_attribute>> getDateMap() {
        return dateMap;
    }

    public static MySQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    public void createSchedule(){
        Intent intent = new Intent();
        intent.setClass(main_date_activity.this, edit_schedule.class);
        startActivity(intent);
    }

    public static LocalDate getLocalDate(){
        return selectedDate;
    }
    public void showOnResume(LocalDate selectedDate){
        if(dateMap.get(selectedDate)!=null) {
            mDate=dateMap.get(selectedDate);
            mAdapter.updateDate(mDate);
            listView.requestFocus();
        }
        else{
            dateMap.put(selectedDate,new LinkedList<date_attribute>());
            mDate=dateMap.get(selectedDate);
            mAdapter.updateDate(mDate);
            listView.requestFocus();
        }
    }

    private void loadDateMapFromDatabase(){
        dateMap.clear();
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
                    new String[]{"attribute1", "attribute2", "isSwitchOn"},
                    "id = ?",
                    new String[]{String.valueOf(dateAttributeId)},
                    null, null, null
            );
            if (dateAttributeCursor.moveToNext()) {
                String attribute1 = dateAttributeCursor.getString(dateAttributeCursor.getColumnIndexOrThrow("attribute1"));
                String attribute2 = dateAttributeCursor.getString(dateAttributeCursor.getColumnIndexOrThrow("attribute2"));
                int isSwitch = dateAttributeCursor.getInt(dateAttributeCursor.getColumnIndexOrThrow("isSwitchOn"));
                boolean isSwitchOn;
                if(isSwitch==1){
                    isSwitchOn=true;
                }
                else{
                    isSwitchOn=false;
                }
                date_attribute dateAttribute = new date_attribute(attribute1, attribute2 , isSwitchOn);

                // 将 dateAttribute 添加到 dateMap 中对应的日期下
                dateMap.computeIfAbsent(localDate, k -> new LinkedList<>()).add(dateAttribute);
            }dateAttributeCursor.close();
        }
        localDateCursor.close();

    }

        public static int getIdByAttributes(SQLiteDatabase db ,String attribute1,String attribute2){
        String[] selectionArgs = {attribute1,attribute2};
        Cursor cursor = db.query(
                "DateAttribute",
                new String[] {"id"},
                "attribute1 = ? AND attribute2 = ?",
                selectionArgs,
                null,
                null,
                null
        );

        try{
            if(cursor.moveToFirst()){
                int Index = cursor.getColumnIndexOrThrow("id");
                return cursor.getInt(Index);
            }else{
                return -1;
            }
        }finally {
            cursor.close();
        }

    }
    public static void updateIsSwitchOnById(SQLiteDatabase db,int id,boolean isSwitchOn){
        String updateQuery = "UPDATE DateAttribute SET isSwitchOn = ? WHERE id = ?";
        SQLiteStatement stmt = db.compileStatement(updateQuery);


        stmt.bindLong(1, isSwitchOn?1:0);
        stmt.bindLong(2, id);
        stmt.execute();
    }
    public void deleteSchedule(){

    }

}


