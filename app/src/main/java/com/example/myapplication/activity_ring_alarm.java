package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class activity_ring_alarm extends Activity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    ImageButton endButton;
    EmoRecog emoRecogHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_emo);
       // MediaUtil.playRing(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeEmoRecog();
        }
        // 获取传递过来的闹钟ID
        Intent intent = getIntent();
        int alarmId = intent.getIntExtra("alarmId", -1);
        //Toast.makeText(this,Integer.toString(alarmId),Toast.LENGTH_SHORT).show();
         endButton=(ImageButton) findViewById(R.id.stop);
         endButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 MediaUtil.stopRing();
                 VibrateUtil.stopVibration();
                 boolean ring=false;
                 for(Alarm alarm:main_alarm_activity.alarms)
                 {
                     if(alarm.isRing)
                     {
                         ring=true;
                         break;
                     }
                 }
                 main_alarm_activity.willring= ring;
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
    }
    private void initializeEmoRecog() {
        emoRecogHelper = new EmoRecog();
        ViewGroup parent = findViewById(R.id.camera_frame);
        emoRecogHelper.initialize(this, parent, R.id.camera_view);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeEmoRecog();
            } else {
                Log.e("Camera", "Camera permission denied");
            }
        }
    }
}