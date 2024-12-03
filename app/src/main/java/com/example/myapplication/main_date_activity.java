package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.*;

import java.time.LocalDate;
import java.util.*;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class main_date_activity extends AppCompatActivity {
    private Button to_alarm_btn;
    private Button to_setting_btn;
    private ImageButton add_schedule;
    private LinkedList<date_attribute> mDate = null;
    private Context mContext;
    private date_adapter mAdapter = null;
    private ListView listView;
    private CalendarView calendarView;
    private static LocalDate selectedDate;
    private static Map<LocalDate,LinkedList<date_attribute>> dateMap = new HashMap<>();
    String TAG="mtTag";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);

        mContext=main_date_activity.this;
        listView=findViewById(R.id.data_list);
        listView.setAdapter(mAdapter);
        mDate = new LinkedList<date_attribute>();

        Log.d(TAG,"onCreate: ");

        to_alarm_btn=findViewById(R.id.to_alarm_btn);
        to_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
//        Calendar calendar = Calendar.getInstance();
//
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);

        add_schedule = findViewById(R.id.add_schedule);
        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSchedule();
            }
        });


        //---------------------------------------------------------------
        mAdapter = new date_adapter(mDate,this,R.layout.schedule_list_item);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year,month,dayOfMonth);
//                view.setDate(calendar.getTimeInMillis());
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
               // Log.d("mtTag",String.valueOf(view.getDate()));

                Log.d("help1",selectedDate.toString());
                if(dateMap.get(selectedDate)!=null) {
                    mDate=dateMap.get(selectedDate);
                    mAdapter = new date_adapter(mDate,main_date_activity.this,R.layout.schedule_list_item);
                    listView.setAdapter(mAdapter);

                }
                else{
                    dateMap.put(selectedDate,new LinkedList<date_attribute>());
                    mDate=dateMap.get(selectedDate);
                    mAdapter = new date_adapter(mDate,main_date_activity.this,R.layout.schedule_list_item);
                    listView.setAdapter(mAdapter);
                }
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
        System.out.println(selectedDate);
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

    public void createSchedule(){
        Intent intent = new Intent();
        intent.setClass(main_date_activity.this, edit_schedule.class);
        startActivity(intent);
    }

    public static LocalDate getLocalDate(){
        return selectedDate;
    }
}
