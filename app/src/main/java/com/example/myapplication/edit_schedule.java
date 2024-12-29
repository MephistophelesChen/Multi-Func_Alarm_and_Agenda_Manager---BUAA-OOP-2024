package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;

public class edit_schedule extends AppCompatActivity {
    private EditText create_schedule_name;
    private EditText create_schedule_tips;
    private Button create_confirm;
    private ImageButton create_schedule_back;
    private CheckBox important;
    private CheckBox urgent;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_schedule);
        create_schedule_back=findViewById(R.id.back);
        create_schedule_name=findViewById(R.id.create_schedule_name);
        create_schedule_tips=findViewById(R.id.create_schedule_tips);
        create_confirm = findViewById(R.id.create_confirm);
        important = findViewById(R.id.Important);
        urgent = findViewById(R.id.Urgent);
        create_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_attribute dateAttribute;
                dateAttribute=create();
                if(dateAttribute!=null){

                    main_date_activity.getDateMap().computeIfAbsent(main_date_activity.getLocalDate(), k -> new LinkedList<date_attribute>());
                    insertData(main_date_activity.getLocalDate(),dateAttribute,main_date_activity.getDbHelper());
                    main_date_activity.getDateMap().get(main_date_activity.getLocalDate()).add(dateAttribute);
                    main_date_activity.getmAdapter().updateDate(main_date_activity.getDateMap().get(main_date_activity.getLocalDate()));

                    finish();
                }
                for (Map.Entry<LocalDate, LinkedList<date_attribute>> entry : main_date_activity.getDateMap().entrySet()) {
                    Log.d("help","Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }

            }
        });
        create_schedule_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public date_attribute create(){
           String name = create_schedule_name.getText().toString();
           String tips = create_schedule_tips.getText().toString();
           boolean Important = important.isChecked();
           boolean Urgent = urgent.isChecked();
           date_attribute dateAttribute;
           if(name.isEmpty()){
               Toast toast = Toast.makeText(getApplicationContext(),"日程名称为空",Toast.LENGTH_SHORT);
               toast.show();
               return null;
           }
           else{
               dateAttribute = new date_attribute(name,tips,false,Important,Urgent);
               dateAttribute.setLocalDate(main_date_activity.getLocalDate());
               return dateAttribute;
           }

    }


    public static int insertDateAttribute(String name, String tips,boolean important,boolean urgent ,String localDate,MySQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = null;
        int dateAttributeId = -1; // 初始化为一个无效的值，比如-1
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues dateAttributesValues = new ContentValues();
            dateAttributesValues.put("attribute1", name);
            dateAttributesValues.put("attribute2", tips);
            dateAttributesValues.put("isSwitchOn",0);
            dateAttributesValues.put("zhongyao",important?1:0);
            dateAttributesValues.put("jinji",urgent?1:0);
            dateAttributesValues.put("LocalDate",localDate);

            // 插入记录并获取生成的ID
            dateAttributeId = (int) db.insert("DateAttribute", null, dateAttributesValues);
            Log.d("mtTag",name+" "+tips+" "+String.valueOf(dateAttributeId));
        } catch (SQLException e) {
            // 捕获并处理SQL异常
            Log.d("mtTag", "Error inserting DateAttribute into database", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }




        return dateAttributeId; // 返回生成的ID，如果插入失败则返回-1或其他错误码
    }

    public static void insertData(LocalDate localDate, date_attribute dateAttribute, MySQLiteOpenHelper dbHelper) {
        // 首先插入DateAttribute并获取ID
        int dateAttributeId = insertDateAttribute(dateAttribute.getName(), dateAttribute.getTips(),dateAttribute.isZhongyao(), dateAttribute.isJinji(), dateAttribute.getLocalDate().toString(),dbHelper);
        if (dateAttributeId == -1) {
            // 处理插入DateAttribute失败的情况
            Log.d("mtTag", "Failed to insert DateAttribute");
            return;
        }
        dateAttribute.setId(dateAttributeId);

        main_date_activity.updateInSQLid(main_date_activity.getDbHelper().getWritableDatabase(),dateAttributeId);

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues localDateValues = new ContentValues();
            localDateValues.put("date", localDate.toString());
            localDateValues.put("dateAttributeId", dateAttributeId);

            // 插入LocalDateMap记录
            db.insert("LocalDateMap", null, localDateValues);

        } catch (SQLException e) {
            // 捕获并处理SQL异常
            Log.d("mtTag", "Error inserting LocalDateMap into database", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

}
