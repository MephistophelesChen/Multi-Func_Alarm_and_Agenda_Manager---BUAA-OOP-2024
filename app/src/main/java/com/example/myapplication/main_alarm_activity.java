package com.example.myapplication;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    ListView alarmList;
    static boolean isMultipleSelectionMode=false;
    private ImageButton add_alarm_btn;
    private Button cancle;
    private int alarm_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);

        alarmList = findViewById(R.id.list_test);
        adapter = new MyBaseAdapter(main_alarm_activity.this, this.time, repeat, map1, alarms);
        adapter.setMactivity(this);
        alarmList.setAdapter(adapter);
        setlongclick(alarmList);
        nextRingTime = findViewById(R.id.next_ring_time);
        cancle = findViewById(R.id.cancle_button);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               cancle_delete();
            }
        });
        add_alarm_btn = findViewById(R.id.add_alarm_btn);
        add_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMultipleSelectionMode) {
                    Intent intent = new Intent(main_alarm_activity.this, create_alarm_time_activity.class);
                    startActivityForResult(intent, REQUEST_CODE_CREATE_ALARM);
                } else {
                    cancle_delete();
                   for(int i=alarmList.getCount()-1;i>=0;i--)
                   {
                       if(alarmList.isItemChecked(i))
                       {
                           delete_alarm(i);
                           sort_alarm();
                           adapter.notifyDataSetChanged();
                           alarmList.invalidate();
                       }
                   }

                    updateNextRingTime();
                }

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
                if(calculateNextRingTime()-now.getTimeInMillis()<=1000)
                    checkAndRing();
                updateNextRingTime();
            }
        }, delay);
    }

    void sort_alarm()
    {
        boolean swap;
        for(int i=0;i<alarms.size()-1;i++)
        {
            swap=false;
            for(int j=0;j<alarms.size()-1;j++)
            {
                if(alarms.get(j).compareTo(alarms.get(j+1))>0)
                {
                    Alarm temp = alarms.get(j);
                    alarms.set(j,alarms.get(j+1));
                    alarms.set(j+1,temp);

                    String stemp= time.get(j);
                    time.set(j,time.get(j+1));
                    time.set(j+1,stemp);

                    String temp1=repeat.get(j);
                    repeat.set(j, repeat.get(j+1));
                    repeat.set(j+1,temp1);

                    swap=true;
                }
            }
            if (!swap) break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_ALARM && resultCode == RESULT_OK) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            ArrayList<Boolean> repeatDays = (ArrayList<Boolean>) data.getSerializableExtra("repeatDays");

            Alarm newAlarm = new Alarm(hour, minute, repeatDays,false);
            newAlarm.id=alarm_id++;
            alarms.add(newAlarm);
            String timeStr = String.format("%02d:%02d%d", hour, minute,alarm_id);
            time.add(timeStr);

            String repeatStr = Tool.addrepeat(repeatDays);
            repeat.add(repeatStr);

            map1.put(timeStr, false);
            sort_alarm();
            adapter.notifyDataSetChanged();
            updateNextRingTime();
        }
    }

    long calculateNextRingTime() {
        Calendar now = Calendar.getInstance();
        String nextTime = "无启用的闹钟";
        long NextTimeInMillis = Long.MAX_VALUE;

        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).isRing()) {   // 如果闹钟开启
                Alarm alarm = alarms.get(i);
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                alarmTime.set(Calendar.MINUTE, alarm.getMinute());
                alarmTime.set(Calendar.SECOND, 0);

                // 处理重复天数
                ArrayList<Boolean> repeatDays = alarm.getRepeat();
                int today = now.get(Calendar.DAY_OF_WEEK) - 1; // 将星期天设为0，星期一设为1，依此类推

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
                            break;
                        }
                    }
                }

                long diff = alarmTime.getTimeInMillis() - now.getTimeInMillis();
                long hours = diff / (1000 * 60 * 60);
                long minutes = (diff / (1000 * 60)) % 60;
                if (diff % (1000 * 60) != 0) {
                    minutes++; // 如果有剩余的秒数，则向上取整
                }
                hours += minutes / 60;
                minutes %= 60;
                nextTime = String.format("还有%d小时%d分钟响铃", hours, minutes);
                NextTimeInMillis = alarmTime.getTimeInMillis();
            }
        }

        nextRingTime.setText(nextTime);
        return NextTimeInMillis;
    }

    void checkAndRing(){
        Intent intent = new Intent(this, activity_ring_alarm.class);
        startActivity(intent);

    }

    void setlongclick(ListView list)
    {
        setItem(list);
    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cancle.setVisibility(View.VISIBLE);
        isMultipleSelectionMode=true;
        add_alarm_btn.setBackgroundResource(R.drawable.delete_button);
        add_alarm_btn.setImageResource(R.drawable.trash);
        for(int i=0;i<alarmList.getCount();i++) {
            ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
            layout.findViewById(R.id.switch_alarm).setVisibility(View.INVISIBLE);
        }
       return false;
    }
    });

    }
    void setItem(ListView list)
    {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isMultipleSelectionMode) {
                    if(list.isItemChecked(position)) {
                        view.setBackgroundColor(0xffaeaeae);
                    }
                    else {
                        view.setBackgroundColor(0xffffffff);
                    }
                }

            }
        });
    }

    private View findView(int position, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    private void cancle_delete()
    {
        isMultipleSelectionMode=false;
        add_alarm_btn.setBackgroundResource(R.drawable.round_button);
        add_alarm_btn.setImageResource(R.drawable.plus);
        cancle.setVisibility(View.INVISIBLE);
        for(int i=0;i<alarmList.getCount();i++) {
            ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
            layout.findViewById(R.id.switch_alarm).setVisibility(View.VISIBLE);
        }
        for(int i=0;i<alarms.size();i++)
        {
            findView(i,alarmList).setBackgroundColor(0xffffffff);
        }
    }
    private void delete_alarm(int position)
    {
        alarms.remove(position);
        map1.remove(time.get(position));
        time.remove(position);
        repeat.remove(position);
    }
    ListView getlist()
    {
        return this.alarmList;
    }

}