package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class main_alarm_activity extends AppCompatActivity  {
    private static final int REQUEST_CODE_CREATE_ALARM = 1;
    private ArrayList<String> time = new ArrayList<>();//pai
    private ArrayList<String> repeat = new ArrayList<>();//pai
    private MyBaseAdapter adapter;
    private Map<String, Boolean> map1 = new HashMap<>();//pai
    private ArrayList<Alarm> alarms = new ArrayList<>();//pai
    private TextView nextRingTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    ListView alarmList;
    static boolean isMultipleSelectionMode=false;
    private ImageButton add_alarm_btn;
    private Button cancle;
    private int alarm_id;
    DataBaseHelper dbHelper;
    ContentValues values;

    public SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alarm);
        alarm_id=0;
        dbHelper=new DataBaseHelper(this);

        alarmList = findViewById(R.id.list_test);
        adapter = new MyBaseAdapter(main_alarm_activity.this, this.time, repeat, map1, alarms,db,dbHelper);
        adapter.setMactivity(this);
        alarmList.setAdapter(adapter);



        loadFromSQL();
        adapter.notifyDataSetChanged();

        setlongclick(alarmList);
        setOnScroll(alarmList);
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
                   for(int i=0,j=0;i<alarms.size();i++,j++)
                   {
                       if(alarmList.isItemChecked(j))
                       {
                           deleteSQL(i);
                           delete_alarm(i);
                           i=i-1;
                       }
                   }
                    adapter.notifyDataSetChanged();
                    alarmList.invalidate();
                    updateNextRingTime();
                }

            }
        });
        updateNextRingTime();
    }

    @Override
    protected void onStart() {
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
            newAlarm.id=alarm_id++;
            alarms.add(newAlarm);
            String timeStr = String.format("%02d:%02d%d", hour, minute,alarm_id);
            time.add(timeStr);
            String repeatStr = Tool.addrepeat(repeatDays);
            repeat.add(repeatStr);
            map1.put(timeStr, false);

            sort_alarm();
            saveToSQL(timeStr,repeatStr,newAlarm);
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
    void setOnScroll(ListView list)
    {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if(isMultipleSelectionMode)
                    {
                     for(int i=firstVisibleItem;i<=visibleItemCount-1;i++)
                     {
                         ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
                         layout.findViewById(R.id.switch_alarm).setVisibility(View.INVISIBLE);
                     }
                    }
                    else
                    {
                        for(int i=firstVisibleItem;i<visibleItemCount-1;i++)
                        {
                            ConstraintLayout layout = (ConstraintLayout) alarmList.getChildAt(i);
                            layout.findViewById(R.id.switch_alarm).setVisibility(View.VISIBLE);
                        }
                    }
            }
        });
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
        for(int i=0;i< Math.min(alarmList.getCount(),7);i++) {
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
        for(int i=0;i<Math.min(alarmList.getCount(),7);i++) {
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
    void saveToSQL(String s1,String s2,Alarm al)
    {
        ContentValues values = new ContentValues();
        db=dbHelper.getWritableDatabase();
        values.put(DataBaseHelper.COLUMN_STRING1, s1);

        values.put(DataBaseHelper.COLUMN_STRING2,s2);

        values.put(DataBaseHelper.COLUMN_ALARM_HOUR, String.valueOf(al.hour));
        values.put(DataBaseHelper.COLUMN_ALARM_MINUTE,String.valueOf(al.minute));
        values.put(DataBaseHelper.COLUMN_ID,String.valueOf(al.id));
        values.put(DataBaseHelper.COLUMN_ALARM_RING,al.isRing?"1":"0");
        values.put(DataBaseHelper.COLUMN_ALARM_REPEAT,Tool.booleanToString(al.repeat));

        db.insert(DataBaseHelper.TABLE_NAME, null, values);
    }

    void loadFromSQL()
    {
        db=dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DataBaseHelper.TABLE_NAME,   // 表名
                new String[]{DataBaseHelper.COLUMN_ID, DataBaseHelper.COLUMN_STRING1,DataBaseHelper.COLUMN_STRING2,DataBaseHelper.COLUMN_ALARM_RING,DataBaseHelper.COLUMN_ALARM_HOUR,DataBaseHelper.COLUMN_ALARM_MINUTE,DataBaseHelper.COLUMN_ALARM_REPEAT}, // 要返回的列
                null,                          // WHERE子句的条件
                null,                          // WHERE子句的参数
                null,                          // 分组依据
                null,                          // 过滤条件
                null                           // 排序依据
        );

        while (cursor.moveToNext()) {
            String s,t;
            int i;
            Alarm al=new Alarm();
            boolean bool;
            boolean re=false;
            s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID));
            i=Integer.parseInt(s);
            al.id=i;

            for(Alarm a:alarms)
            {
                if(a.id==i)
                {
                   re=true;
                }
            }

            if(re==false) {
                t = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_STRING1));
                time.add(t);
                s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_STRING2));
                repeat.add(s);
                s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_HOUR));
                i = Integer.parseInt(s);
                al.hour = i;
                s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_MINUTE));
                i = Integer.parseInt(s);
                al.minute = i;

                s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_RING));
                i = Integer.parseInt(s);
                al.isRing = i == 1;

                map1.put(t, al.isRing);
                s = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ALARM_REPEAT));
                al.repeat = Tool.StringToBoolean(s);
                alarms.add(al);
            }
            alarm_id++;
        }
        sort_alarm();
    }

    ListView getlist()
    {
        return this.alarmList;
    }
 void deleteSQL(int position)
 {
     long id=alarms.get(position).id;
     String idToDelete = String.valueOf(id); // 确保这个值来自可信的源，或者已经过适当的清理和转义
     String removeSQL = "DELETE FROM string_table WHERE _id = '" + idToDelete + "'";
     db.execSQL(removeSQL);
 }

   SQLiteDatabase getSQL()
   {
       return this.db;
   }

   void setSQL(SQLiteDatabase db)
   {
       this.db=db;
   }
}