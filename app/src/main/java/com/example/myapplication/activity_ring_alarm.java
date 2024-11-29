package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

public class activity_ring_alarm extends Activity {
    ImageButton endButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_alarm);
       // MediaUtil.playRing(this);

        // 获取传递过来的闹钟ID
        Intent intent = getIntent();
        int alarmId = intent.getIntExtra("alarmId", -1);
        //Toast.makeText(this,Integer.toString(alarmId),Toast.LENGTH_SHORT).show();
         endButton=(ImageButton) findViewById(R.id.stop);
         endButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 MediaUtil.stopRing();
                 finish();
             }
         });
        ImageButton sleepMoreButton = findViewById(R.id.sleep_more);
        sleepMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayAlarm();
                MediaUtil.stopRing();
                finish();
            }
        });
        // 检查所有在同一时间点的、不重复的闹钟，并将其关闭
        if (alarmId != -1) {
            Alarm ringingAlarm = null;
            for (Alarm alarm : main_alarm_activity.alarms) {
                if (alarm.id == alarmId) {
                    ringingAlarm = alarm;
                    break;
                }
            }

            if (ringingAlarm != null) {
                for (Alarm alarm : main_alarm_activity.alarms) {
                    if (alarm.getHour() == ringingAlarm.getHour() &&
                        alarm.getMinute() == ringingAlarm.getMinute() &&
                        !alarm.getRepeat().contains(true)) {
                        alarm.setRing(false);
                        // !!! main_alarm_activity.updateAlarm(alarm);
                        //按钮关闭还没绑好
                    }
                }
            }
        }

        // TODO: UI、响铃逻辑、关闭/延迟（懒人模式）逻辑、铃声选择逻辑
    }
    public void delayAlarm() {
        SQLiteDatabase db = main_alarm_activity.dbHelper.getWritableDatabase();
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 1); // 延迟1分钟
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        // 更新数据库中的隐藏闹钟
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_ALARM_HOUR, String.valueOf(hour));
        values.put(DataBaseHelper.COLUMN_ALARM_MINUTE, String.valueOf(minute));
        values.put(DataBaseHelper.COLUMN_ALARM_RING, "1");  //打开

        db.update(DataBaseHelper.TABLE_NAME, values, "isHidden = ?", new String[]{"1"});

        // 更新内存中的隐藏闹钟
        for (Alarm alarm : main_alarm_activity.alarms) {
            if (alarm.isHidden) {
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setRing(true);
                break;
            }
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   MediaUtil.stopRing();
    }
}