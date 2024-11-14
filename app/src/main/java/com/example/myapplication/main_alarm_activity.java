package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class main_alarm_activity extends AppCompatActivity {
    private static final int REQUEST_CODE_CREATE_ALARM = 1;
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> repeat = new ArrayList<>();
    private MyBaseAdapter adapter;
    private Map<String, Boolean> map1 = new HashMap<>();
    private ArrayList<Alarm> alarms = new ArrayList<>();
    private TextView nextRingTime;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ImageButton add_alarm_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);

        ListView alarmList = findViewById(R.id.list_test);
        adapter = new MyBaseAdapter(main_alarm_activity.this, this.time, repeat, map1, alarms);
        alarmList.setAdapter(adapter);

        nextRingTime = findViewById(R.id.next_ring_time);

        add_alarm_btn = findViewById(R.id.add_alarm_btn);
        add_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_alarm_activity.this, create_alarm_time_activity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_ALARM);
            }
        });
        updateNextRingTime();
    }

    void updateNextRingTime() {
        Calendar now = Calendar.getInstance();
        long delay = 60 - now.get(Calendar.SECOND) * 1000L;
        calculateNextRingTime();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                calculateNextRingTime();
                updateNextRingTime();
            }
        }, delay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_ALARM && resultCode == RESULT_OK) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            ArrayList<Boolean> repeatDays = (ArrayList<Boolean>) data.getSerializableExtra("repeatDays");

            Alarm newAlarm = new Alarm(hour, minute, repeatDays,false);
            alarms.add(newAlarm);

            String timeStr = String.format("%02d:%02d", hour, minute);
            time.add(timeStr);

            String repeatStr = Tool.addrepeat(repeatDays);
            repeat.add(repeatStr);

            map1.put(timeStr, false);

            adapter.notifyDataSetChanged();
            updateNextRingTime();
        }
    }

    void calculateNextRingTime() {
        Calendar now = Calendar.getInstance();
        long minDiff = Long.MAX_VALUE;
        String nextTime = "无启用的闹钟";

        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).isRing()) {
                Alarm alarm = alarms.get(i);
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                alarmTime.set(Calendar.MINUTE, alarm.getMinute());
                alarmTime.set(Calendar.SECOND, 0);

                // 处理重复天数
                ArrayList<Boolean> repeatDays = alarm.getRepeat();
                int today = now.get(Calendar.DAY_OF_WEEK) - 1; // 将星期天设为0，星期一设为1，依此类推
                boolean found = false;

                // 如果闹钟不重复而开启，且今天的闹钟时间已经过去，则将闹钟时间设为明天
                if (!repeatDays.contains(true) && alarmTime.before(now)) {
                    alarmTime.add(Calendar.DAY_OF_MONTH, 1);
                }
                else if(repeatDays.contains(true)){  // 闹钟按照星期重复
                    for (int j = 0; j <= 7; j++) {
                        int dayIndex = (today + j) % 7;
                        if (repeatDays.get(dayIndex) && alarmTime.before(now)) {
                            alarmTime.add(Calendar.DAY_OF_MONTH, j);
                        }
                        if (alarmTime.after(now)) {
                            found = true;
                            break;
                        }
                    }
                }

                long diff = alarmTime.getTimeInMillis() - now.getTimeInMillis();
                if (diff < minDiff) {
                    minDiff = diff;
                    long hours = diff / (1000 * 60 * 60);
                    long minutes = (diff / (1000 * 60)) % 60;
                    if (diff % (1000 * 60) != 0) {
                        minutes++; // 如果有剩余的秒数，则向上取整
                    }
                    hours += minutes / 60;
                    minutes %= 60;
                    nextTime = String.format("还有%d小时%d分钟响铃", hours, minutes);
                }
            }
        }

        nextRingTime.setText(nextTime);
    }
}