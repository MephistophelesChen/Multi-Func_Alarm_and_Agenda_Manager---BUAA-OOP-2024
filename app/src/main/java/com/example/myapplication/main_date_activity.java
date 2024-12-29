package com.example.myapplication;

import android.annotation.SuppressLint;
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
    private Button delete_cancle;
    private ImageButton add_schedule;
    private ImageButton delete_schedule;
    private TextView nothing_to_do;
    private static LinkedList<date_attribute> mDate = null;
    private Context mContext;
    private static date_adapter mAdapter = null;
    private boolean isMultiSelectMode=false;
    private ListView listView;
    private CalendarView calendarView;
    private static LocalDate selectedDate;
    public static Map<LocalDate,LinkedList<date_attribute>> dateMap = new HashMap<>();
    static MySQLiteOpenHelper dbHelper;
    static SQLiteDatabase db;
  private ButtonManager btnManager=new ButtonManager();
    String TAG="mtTag";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);
        dbHelper = new MySQLiteOpenHelper(this);
//        dbHelper.deleteSQL();

        dateMap=loadDateMapFromDatabase();
        db=dbHelper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate=LocalDate.of(year,month+1,day);

        mContext=main_date_activity.this;
        listView=findViewById(R.id.data_list);
        Button to_todolist_btn=(Button) findViewById(R.id.to_todolist_btn);
        btnManager.switchToActivity_btn(to_todolist_btn,main_date_activity.this,main_todolist_activity.class);
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
        nothing_to_do = findViewById(R.id.nothing_to_do);
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
                    if(mDate.isEmpty()){
                        nothing_to_do.setVisibility(View.VISIBLE);
                    }
                    else{
                        nothing_to_do.setVisibility(View.GONE);
                    }

                }
                else{
                    dateMap.put(selectedDate,new LinkedList<date_attribute>());
                    mDate=dateMap.get(selectedDate);
                    mAdapter.updateDate(mDate);
                    listView.requestFocus();
                    if(mDate.isEmpty()){
                        nothing_to_do.setVisibility(View.VISIBLE);
                    }
                    else{
                        nothing_to_do.setVisibility(View.GONE);
                    }
                }
            }
        });

            delete_schedule=findViewById(R.id.deleteSchedule);
            delete_cancle=findViewById(R.id.delete_cancel);
        HashSet<Integer> selectedIdx = new HashSet<>();
        HashSet<Integer> selectedPosition = new HashSet<>();
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                add_schedule.setVisibility(View.GONE);
                delete_schedule.setVisibility(View.VISIBLE);
                delete_cancle.setVisibility(View.VISIBLE);
                isMultiSelectMode=true;
                view.setBackgroundColor(getResources().getColor(R.color.gray_2,getTheme()));
                selectedPosition.add(position);
                selectedIdx.add(((date_attribute)mAdapter.getItem(position)).getId());

//                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(isMultiSelectMode){
                        if(selectedPosition.contains(position)){
                            view.setBackgroundColor(Color.WHITE);
                            selectedPosition.remove(position);
                            selectedIdx.remove(((date_attribute)mAdapter.getItem(position)).getId());
                        }
                        else {

                            view.setBackgroundColor(getResources().getColor(R.color.gray_2,getTheme()));
                            selectedPosition.add(position);
                            selectedIdx.add(((date_attribute)mAdapter.getItem(position)).getId());
                        }
                    }
                }
            });
            delete_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int id : selectedIdx){
                        deleteInSQL(dbHelper.getWritableDatabase(),id);
                        removeDateInMap(id,dateMap.get(getLocalDate()));
                    }
                    mAdapter.updateDate(dateMap.get(getLocalDate()));
                    isMultiSelectMode=false;
                    for(int i=0;i<listView.getCount();i++){
                        View itemView = listView.getChildAt(i);
                        if(itemView!=null){
                            itemView.setBackgroundColor(Color.WHITE);
                        }
                    }
                    selectedPosition.clear();
                    selectedIdx.clear();
                    delete_schedule.setVisibility(View.GONE);
                    delete_cancle.setVisibility(View.GONE);
                    add_schedule.setVisibility(View.VISIBLE);
                }
            });
            delete_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMultiSelectMode=false;
                    delete_schedule.setVisibility(View.GONE);
                    delete_cancle.setVisibility(View.GONE);
                    add_schedule.setVisibility(View.VISIBLE);
                    for(int i=0;i<listView.getCount();i++){
                        View itemView = listView.getChildAt(i);
                        if(itemView!=null&&selectedPosition.contains(i)){
                            itemView.setBackgroundColor(Color.WHITE);
                        }
                    }
                    selectedPosition.clear();
                    selectedIdx.clear();
                }
            });
    }
//----------------------------------------------------------




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
        db.close();
        Log.d(TAG,"onDestroy: ");
    }

    public static Map<LocalDate, LinkedList<date_attribute>> getDateMap() {
        return dateMap;
    }

    public static MySQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    public static date_adapter getmAdapter() {
        return mAdapter;
    }

    public static SQLiteDatabase getDb() {
        return db;
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
            if(mDate.isEmpty()){
                nothing_to_do.setVisibility(View.VISIBLE);
            }
            else{
                nothing_to_do.setVisibility(View.GONE);
            }

        }
        else{
            dateMap.put(selectedDate,new LinkedList<date_attribute>());
            mDate=dateMap.get(selectedDate);
            mAdapter.updateDate(mDate);
            listView.requestFocus();
            if(mDate.isEmpty()){
                nothing_to_do.setVisibility(View.VISIBLE);
            }
            else{
                nothing_to_do.setVisibility(View.GONE);
            }
        }
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
    public static void updateInSQLid(SQLiteDatabase db,long ID){
        String updateQuery = "UPDATE DateAttribute SET idx = ? WHERE id = ?";
        SQLiteStatement stmt =db.compileStatement(updateQuery);

        stmt.bindLong(1,ID);
        stmt.bindLong(2,ID);
        stmt.execute();
        stmt.close();
    }

    public static void updateIsSwitchOnById(SQLiteDatabase db,int id,boolean isSwitchOn){
        String updateQuery = "UPDATE DateAttribute SET isSwitchOn = ? WHERE id = ?";
        SQLiteStatement stmt = db.compileStatement(updateQuery);


        stmt.bindLong(1, isSwitchOn?1:0);
        stmt.bindLong(2, id);
        stmt.execute();
        stmt.close();
    }
    public static void deleteInSQL(SQLiteDatabase db,int id){
        String deleteQuery1 = "DELETE FROM LocalDateMap WHERE dateAttributeId = ?";
        SQLiteStatement stmt1 = db.compileStatement(deleteQuery1);

        stmt1.bindLong(1,id);
        stmt1.execute();
        stmt1.close();

        String deleteQuery2 = "DELETE FROM DateAttribute WHERE id = ?";
        SQLiteStatement stmt2 = db.compileStatement(deleteQuery2);

        stmt2.bindLong(1,id);
        stmt2.execute();
        stmt2.close();
    }
    public void removeDateInMap(int id,LinkedList<date_attribute> list){
        Iterator<date_attribute> iterator = list.iterator();
        while(iterator.hasNext()){
            date_attribute dateAttribute = iterator.next();
            if(dateAttribute.getId() == id){
                iterator.remove();
                break;
            }
        }
    }

}


