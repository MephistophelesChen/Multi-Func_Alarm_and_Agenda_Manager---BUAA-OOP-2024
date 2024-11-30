package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CalendarView calendarView;
    private EditText scheduleInput;
    private Context context;
    private Button addSchedule,checkAdd;
    private String dateToday;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;
    private TextView mySchedule[]=new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_schedule);

        initView();
    }

    private void initView(){
        mySQLiteOpenHelper=new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        context=this;
        addSchedule = findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(this);
        checkAdd=findViewById(R.id.checkAdd);
        checkAdd.setOnClickListener(this);

        calendarView =findViewById(R.id.calendar);
        scheduleInput = findViewById(R.id.scheduleDetailInput);

        calendarView.setOnDateChangeListener(mySelectDate);

        mySchedule[0] = findViewById(R.id.schedule1);
        mySchedule[1] = findViewById(R.id.schedule2);
        mySchedule[2] = findViewById(R.id.schedule3);
        mySchedule[3] = findViewById(R.id.schedule4);
        mySchedule[4] = findViewById(R.id.schedule5);

        for(TextView v:mySchedule){
            v.setOnClickListener(this);
        }
    }

    private CalendarView.OnDateChangeListener mySelectDate =new CalendarView.OnDateChangeListener(){
        @Override
        public void onSelectedDayChange(CalendarView view,int year,int month,int dayOfMonth){
            dateToday = year+"-"+(month+1)+"-"+dayOfMonth;
            Toast.makeText(context, "你选择了:"+dateToday, Toast.LENGTH_SHORT).show();

            for(TextView v:mySchedule){
                v.setText("");
                v.setVisibility(View.GONE);
            }
            queryByDate(dateToday);
        }

    };

    private void queryByDate(String date){
        Cursor cursor = myDatabase.query("schedules",null,"time=?",new String[]{date},null,null,null);
        if(cursor.moveToFirst()){
            int scheduleCount=0;
            do{
                @SuppressLint("Range") String aScheduleDetail = cursor.getString(cursor.getColumnIndex("scheduleDetail"));
                mySchedule[scheduleCount].setText("日程"+(scheduleCount+1)+":"+aScheduleDetail);
                mySchedule[scheduleCount].setVisibility(View.VISIBLE);
                scheduleCount++;
                if(scheduleCount>=5)
                    break;
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.addSchedule) {
            addMySchedule();
        } else if (id == R.id.checkAdd) {
            checkAddSchedule();
        } else if (id == R.id.schedule1 || id == R.id.schedule2 || id == R.id.schedule3 || id == R.id.schedule4 || id == R.id.schedule5) {
            editSchedule(v);
        }
    }
    private void editSchedule(View v) {
        try {
            // 假设v是一个TextView的引用
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                String text = textView.getText().toString();

                // 分割文本，假设文本格式为"前缀:日程详情"
                String[] parts = text.split(":");
                if (parts.length > 1) {
                    String sch = parts[1]; // 获取日程详情

                    Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
                    intent.putExtra("schedule", sch);
                    startActivity(intent);
                } else {
                    // 如果分割后的数组长度不大于1，说明文本格式不正确
                    Toast.makeText(MainActivity.this, "文本格式错误，请检查日程详情是否正确输入。", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 如果v不是TextView的实例，抛出ClassCastException（虽然这里已经通过instanceof检查了，但为了完整性还是保留这个注释）
                // 实际上，由于前面已经检查了v的类型，这里不会执行到
                throw new ClassCastException("v不是TextView的实例");
            }
        } catch (NullPointerException e) {
            // 如果textView.getText()返回null，会抛出NullPointerException
            Toast.makeText(MainActivity.this, "日程详情文本为空，请检查是否已正确设置。", Toast.LENGTH_SHORT).show();
        } catch (ArrayIndexOutOfBoundsException e) {
            // 如果分割后的数组没有第二个元素（即parts[1]不存在），会抛出ArrayIndexOutOfBoundsException
            Toast.makeText(MainActivity.this, "日程详情部分缺失，请检查文本格式是否正确（应为'前缀:日程详情'）。", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // 捕获其他未预料的异常
            Toast.makeText(MainActivity.this, "发生未知错误，请稍后再试。", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // 打印异常堆栈信息，便于调试
        }

    }
    private void checkAddSchedule() {
        ContentValues values = new ContentValues();
        //第一个参数是表中的列名
        values.put("scheduleDetail",scheduleInput.getText().toString());
        values.put("time",dateToday);
        myDatabase.insert("schedules",null,values);
        scheduleInput.setVisibility(View.GONE);
        checkAdd.setVisibility(View.GONE);
        queryByDate(dateToday);
        //添加完以后把scheduleInput中的内容清除
        scheduleInput.setText("");
    }
    private void addMySchedule() {
        scheduleInput.setVisibility(View.VISIBLE);
        checkAdd.setVisibility(View.VISIBLE);
    }

}