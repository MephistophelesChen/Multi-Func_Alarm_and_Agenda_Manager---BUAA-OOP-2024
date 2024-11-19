package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class edit_alarm_time_activity extends AppCompatActivity {
    private TimePicker timePicker;
    private BottomAdapter bottomAdapter;
    private List<Model> repeatDay = new ArrayList<>();
    private Alarm alarm;
    private int alarmId;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_alarm_time);

        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();

        ImageButton back = findViewById(R.id.back);
        timePicker = findViewById(R.id.time_picker);
        Button repeat = findViewById(R.id.repeatButton);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_bottom_sheet);
        ListView listView = dialog.findViewById(R.id.bottom_sheet);
        init();
        bottomAdapter = new BottomAdapter(this, repeatDay);
        listView.setAdapter(bottomAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        dialog.setOnCancelListener(dialog1 -> repeatDay = bottomAdapter.getData());
        dialog.setOnDismissListener(dialog12 -> repeatDay = bottomAdapter.getData());

        ImageButton ensure = findViewById(R.id.yes);
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "成功修改闹钟", Toast.LENGTH_SHORT).show();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                ArrayList<Boolean> repeatDays = new ArrayList<>();
                repeatDay = bottomAdapter.getData();
                for (int i = 0; i < 7; i++) {
                    repeatDays.add(repeatDay.get(i).ischeck());
                }

                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setRepeat(repeatDays);
                updateAlarm(alarm); // 更新数据库中的闹钟信息

                Intent resultIntent = new Intent();
                resultIntent.putExtra("alarmId", alarmId);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        // 获取传递的闹钟ID并加载闹钟信息
        Intent intent = getIntent();
        alarmId = intent.getIntExtra("alarmId", -1);
        if (alarmId != -1) {
            for (Alarm a : main_alarm_activity.alarms) {
                if (a.id == alarmId) {
                    alarm = a;
                    break;
                }
            }
            if (alarm != null) {
                timePicker.setCurrentHour(alarm.getHour());
                timePicker.setCurrentMinute(alarm.getMinute());
                for (int i = 0; i < 7; i++) {
                    repeatDay.get(i).setIscheck(alarm.getRepeat().get(i));
                }
            }
        }
    }

    private void init() {
        repeatDay.add(new Model("星期一"));
        repeatDay.add(new Model("星期二"));
        repeatDay.add(new Model("星期三"));
        repeatDay.add(new Model("星期四"));
        repeatDay.add(new Model("星期五"));
        repeatDay.add(new Model("星期六"));
        repeatDay.add(new Model("星期日"));
    }
    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_ALARM_HOUR, alarm.getHour());
        values.put(DataBaseHelper.COLUMN_ALARM_MINUTE, alarm.getMinute());
        values.put(DataBaseHelper.COLUMN_ALARM_REPEAT, Tool.booleanToString(alarm.getRepeat()));
        values.put(DataBaseHelper.COLUMN_ALARM_RING, alarm.isRing() ? 1 : 0);
        db.update(DataBaseHelper.TABLE_NAME, values, DataBaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(alarm.id)});

    }

}