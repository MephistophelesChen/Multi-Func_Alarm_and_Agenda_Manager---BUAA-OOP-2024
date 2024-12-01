package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class main_alarm_activity extends AppCompatActivity {
    private static final int REQUEST_CODE_CREATE_ALARM = 1;
    private ArrayList<String> time = new ArrayList<>();//pai
    private ArrayList<String> repeat = new ArrayList<>();//pai
    private MyBaseAdapter adapter;
    static Map<String, Boolean> map1 = new HashMap<>();//pai
    static ArrayList<Alarm> alarms = new ArrayList<>();//pai
    private TextView nextRingTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    ListView alarmList;
    static boolean isMultipleSelectionMode = false;
    private ImageButton add_alarm_btn;
    private Button cancle;
    private int alarm_id;
    static DataBaseHelper dbHelper;
    ContentValues values;
    public static MediaUtil mediaUtil;
    public SQLiteDatabase db;
    public static Uri alert;
    Button to_setting;

    private Button to_date_btn;
    public static boolean EnableVibrate = true;
    public static boolean isVibrating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);

        alarm_id = 0;
        dbHelper = new DataBaseHelper(this);

        alarmList = findViewById(R.id.list_test);
        adapter = new MyBaseAdapter(main_alarm_activity.this, this.time, repeat, map1, alarms, db, dbHelper);
        adapter.setMactivity(this);
        alarmList.setAdapter(adapter);


        loadFromSQL();
        adapter.notifyDataSetChanged();

        setlongclick(alarmList);
        setOnScroll(alarmList);
        nextRingTime = findViewById(R.id.next_ring_time);
        to_setting=findViewById(R.id.to_setting_btn);
        to_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(main_alarm_activity.this,main_setting_activity.class);
                startActivity(intent);
            }
        });
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
                if (!isMultipleSelectionMode) {
                    Intent intent = new Intent(main_alarm_activity.this, create_alarm_time_activity.class);
                    startActivityForResult(intent, REQUEST_CODE_CREATE_ALARM);
                } else {
                    cancle_delete();
                    for (int i = 0; i < alarms.size(); i++) {
                        if (alarms.get(i).is_checked) {
                            deleteSQL(i);
                            delete_alarm(i);
                            i-=1;
                            adapter.notifyDataSetChanged();
                            alarmList.invalidate();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateNextRingTime();
                }

            }
        });
        updateNextRingTime();
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    //    alert=RingtoneManager.getDefaultUri(R.raw.alarm_beep);

        to_date_btn = findViewById(R.id.to_date_btn);
        to_date_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(main_alarm_activity.this, main_date_activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        alarm_id++;
        super.onStart();
        loadFromSQL();
        adapter.notifyDataSetChanged();

    }

    void updateNextRingTime() {
        Calendar now = Calendar.getInstance();
        long delay = 60 - now.get(Calendar.SECOND) * 1000L;
        calculateNextRingTime();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (calculateNextRingTime() - now.getTimeInMillis() <= 1000)
                    checkAndRing();
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

            Alarm newAlarm = new Alarm(hour, minute, repeatDays, true);
            newAlarm.id = alarm_id++;
            alarms.add(newAlarm);
            String timeStr = String.format("%02d:%02d%d", hour, minute, newAlarm.id);
            time.add(timeStr);
            String repeatStr = Tool.addrepeat(repeatDays);
            repeat.add(repeatStr);
            map1.put(timeStr, true);

            sort_alarm();
            saveToSQL(timeStr, repeatStr, newAlarm);
            adapter.notifyDataSetChanged();
            updateNextRingTime();
        }
    }

    long calculateNextRingTime() {
        Calendar now = Calendar.getInstance();
        String nextTime = "无启用的闹钟";
        long minDiff = Long.MAX_VALUE;

        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).isRing()) {   // 如果闹钟开启
                Alarm alarm = alarms.get(i);
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                alarmTime.set(Calendar.MINUTE, alarm.getMinute());
                alarmTime.set(Calendar.SECOND, 0);

                // 处理重复天数
                ArrayList<Boolean> repeatDays = alarm.getRepeat();
                int today = (now.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7; // 将星期天设为6，星期一设为0，依此类推

                // 如果闹钟不重复而开启，且今天的闹钟时间已经过去，则将闹钟时间设为明天
                if (!repeatDays.contains(true) && alarmTime.before(now)) {
                    alarmTime.add(Calendar.DAY_OF_MONTH, 1);
                } else if (repeatDays.contains(true) ) {  // 闹钟按照星期重复
                    for (int j = 0; j <= 7; j++) {
                        if(j==0 && repeatDays.get(today) && alarmTime.before(now)) {
                            continue;
                        }
                        int dayIndex = (today + j) % 7;
                        if (repeatDays.get(dayIndex) ) {
                            alarmTime.add(Calendar.DAY_OF_MONTH, j);
                            break;
                        }
                    }
                }

                long diff = alarmTime.getTimeInMillis() - now.getTimeInMillis();
                if (diff < minDiff) {
                    minDiff = diff;
                }
                long hours = minDiff / (1000 * 60 * 60);
                long minutes = (minDiff / (1000 * 60)) % 60;
                if (minDiff % (1000 * 60) >3 ) { //剩余秒数大于3秒
                    minutes++; // 如果有剩余的秒数，则向上取整
                }
                hours += minutes / 60;
                minutes %= 60;
                nextTime = String.format("还有%d小时%d分钟响铃", hours, minutes);
            }
        }

        nextRingTime.setText(nextTime);
        return minDiff+now.getTimeInMillis();
    }

    void checkAndRing() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE,
                "MyApp::AlarmWakeLock"
        );
        wakeLock.acquire(30000); // 保持唤醒30秒

        // 获取下一个要响铃的闹钟
        Alarm nextAlarm = getNextAlarmToRing();
        if (nextAlarm != null) {
            //Intent intent = new Intent(this, activity_ring_alarm.class);
            //Intent.putExtra("alarmId", nextAlarm.id);
            //Toast.makeText(this,Integer.toString(nextAlarm.id),Toast.LENGTH_SHORT).show();
            for(Alarm alarm:alarms)
            {
                if(alarm.hour==nextAlarm.hour && alarm.minute==nextAlarm.minute && (!alarm.repeat.contains(true)) )
                {//如果是不重复的闹钟，响铃后关闭
                    alarm.isRing=false;
                    String timeStr = String.format("%02d:%02d%d", alarm.hour, alarm.minute, alarm.id);
                    map1.replace(timeStr,false);
                }
            }
            adapter.notifyDataSetChanged();
            postNotification(nextAlarm.id);
            // 启动振动
            if (EnableVibrate && !isVibrating) {
                VibrateUtil.startVibration(this, new long[]{0, 1000, 1000}, 0);
                isVibrating = true;
            }
           // startActivity(intent);
        }

    }


    private Alarm getNextAlarmToRing() {
        Calendar now = Calendar.getInstance();
        for (Alarm alarm : alarms) {
            if (alarm.isRing()) {
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                alarmTime.set(Calendar.MINUTE, alarm.getMinute());
                alarmTime.set(Calendar.SECOND, 0);

                if (alarmTime.after(now)) {
                    return alarm;
                }
            }
        }
        return null;
    }

    void postNotification(int id)
    {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // 如果API级别 >= 26，创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel(Integer.toString(id), name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent=new Intent(this,activity_ring_alarm.class);
        intent.putExtra("alarmId",id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_MUTABLE);
        RemoteViews remoteViews=new RemoteViews("com.example.myapplication",R.layout.notification_res);

        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);

        Notification notification = new NotificationCompat.Builder(this, Integer.toString(id))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews)
                .setContentTitle("闹钟")
                .setCategory(Notification.CATEGORY_ALARM)
                .setCustomBigContentView(remoteViews)
                .setSmallIcon(R.drawable.chevron_left)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntent)
                .build();

        // 发送通知
        notificationManager.notify(id, notification);
        MediaUtil.playRing(this,alert);
        if(EnableVibrate && !isVibrating)
        {
            VibrateUtil.startVibration(this, new long[]{0, 1000, 1000}, 0);
            isVibrating=true;
        }
    }

    void setOnScroll(ListView list) {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isMultipleSelectionMode) {
                    for (int i = firstVisibleItem; i <= visibleItemCount - 1; i++) {
                        ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
                        layout.findViewById(R.id.switch_alarm).setVisibility(View.INVISIBLE);
                    }
                } else {
                    for (int i = firstVisibleItem; i <= visibleItemCount - 1; i++) {
                        ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
                        layout.findViewById(R.id.switch_alarm).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    void setlongclick(ListView list) {
        setItem(list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                cancle.setVisibility(View.VISIBLE);
                isMultipleSelectionMode = true;
                add_alarm_btn.setBackgroundResource(R.drawable.delete_button);
                add_alarm_btn.setImageResource(R.drawable.trash);

                for (int i = 0; i < alarmList.getChildCount(); i++) {
                    ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
                    layout.findViewById(R.id.switch_alarm).setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    void setItem(ListView list) {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isMultipleSelectionMode) {
                    if (list.isItemChecked(position)) {
                        view.setBackgroundColor(0xffaeaeae);
                        alarms.get(position).is_checked = true;
                    } else {
                        view.setBackgroundColor(0xffffffff);
                        alarms.get(position).is_checked = false;
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

    private void cancle_delete() {
        isMultipleSelectionMode = false;
        add_alarm_btn.setBackgroundResource(R.drawable.round_button);
        add_alarm_btn.setImageResource(R.drawable.plus);
        cancle.setVisibility(View.INVISIBLE);
        for (int i = 0; i < alarmList.getChildCount(); i++) {
            ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
            layout.findViewById(R.id.switch_alarm).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < alarms.size(); i++) {
            findView(i, alarmList).setBackgroundColor(0xffffffff);
        }
    }

    private void delete_alarm(int position) {
        alarms.remove(position);
        map1.remove(time.get(position));
        time.remove(position);
        repeat.remove(position);
    }

    void sort_alarm() {
        boolean swap;
        for (int i = 0; i < alarms.size() - 1; i++) {
            swap = false;
            for (int j = 0; j < alarms.size() - 1; j++) {
                if (alarms.get(j).compareTo(alarms.get(j + 1)) > 0) {
                    Alarm temp = alarms.get(j);
                    alarms.set(j, alarms.get(j + 1));
                    alarms.set(j + 1, temp);

                    String stemp = time.get(j);
                    time.set(j, time.get(j + 1));
                    time.set(j + 1, stemp);

                    String temp1 = repeat.get(j);
                    repeat.set(j, repeat.get(j + 1));
                    repeat.set(j + 1, temp1);

                    swap = true;
                }
            }
            if (!swap) break;
        }
    }

    void saveToSQL(String s1, String s2, Alarm al) {
        ContentValues values = new ContentValues();
        db = dbHelper.getWritableDatabase();
        values.put(DataBaseHelper.COLUMN_STRING1, s1);

        values.put(DataBaseHelper.COLUMN_STRING2, s2);

        values.put(DataBaseHelper.COLUMN_ALARM_HOUR, String.valueOf(al.hour));
        values.put(DataBaseHelper.COLUMN_ALARM_MINUTE, String.valueOf(al.minute));
        values.put(DataBaseHelper.COLUMN_ID, String.valueOf(al.id));
        values.put(DataBaseHelper.COLUMN_ALARM_RING, al.isRing ? "1" : "0");
        values.put(DataBaseHelper.COLUMN_ALARM_REPEAT, Tool.booleanToString(al.repeat));
        values.put(DataBaseHelper.COLUMN_IS_HIDDEN, al.isHidden ? "1" : "0");

        db.insert(DataBaseHelper.TABLE_NAME, null, values);
    }

    void loadFromSQL() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DataBaseHelper.TABLE_NAME,
                new String[]{DataBaseHelper.COLUMN_ID, DataBaseHelper.COLUMN_STRING1, DataBaseHelper.COLUMN_STRING2, DataBaseHelper.COLUMN_ALARM_RING, DataBaseHelper.COLUMN_ALARM_HOUR, DataBaseHelper.COLUMN_ALARM_MINUTE, DataBaseHelper.COLUMN_ALARM_REPEAT, DataBaseHelper.COLUMN_IS_HIDDEN},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String s, t;
            int i;
            Alarm al = new Alarm();
            boolean re = false;

            s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID));
            if (s != null) {
                try {
                    i = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    i = 0; // 或者根据需要处理错误
                }
                al.id = i;

                for (Alarm a : alarms) {
                    if (a.id == i) {
                        re = true;
                    }
                }

                if (!re) {
                    t = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_STRING1));
                    time.add(t != null ? t : "");
                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_STRING2));
                    repeat.add(s != null ? s : "");
                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_HOUR));
                    i = s != null ? Integer.parseInt(s) : 0;
                    al.hour = i;
                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_MINUTE));
                    i = s != null ? Integer.parseInt(s) : 0;
                    al.minute = i;

                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_RING));
                    i = s != null ? Integer.parseInt(s) : 0;
                    al.isRing = i == 1;

                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_IS_HIDDEN));
                    i = s != null ? Integer.parseInt(s) : 0;
                    al.isHidden = i == 1;

                    map1.put(t, al.isRing);
                    s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_REPEAT));
                    al.repeat = Tool.StringToBoolean(s != null ? s : "0000000");
                    alarms.add(al);
                }
                alarm_id++;
            }
        }
        sort_alarm();
    }

    ListView getlist() {
        return this.alarmList;
    }

    void deleteSQL(int position) {
        long id = alarms.get(position).id;
        String idToDelete = String.valueOf(id); // 确保这个值来自可信的源，或者已经过适当的清理和转义
        String removeSQL = "DELETE FROM string_table WHERE _id = '" + idToDelete + "'";
        db.execSQL(removeSQL);
    }


    SQLiteDatabase getSQL() {
        return this.db;
    }

    void setSQL(SQLiteDatabase db) {
        this.db = db;
    }
}