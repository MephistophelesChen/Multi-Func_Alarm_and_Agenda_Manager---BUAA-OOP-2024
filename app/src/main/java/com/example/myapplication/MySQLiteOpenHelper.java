package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

public class MySQLiteOpenHelper extends SQLiteOpenHelper{
    private static final String db_name = "MySchedule";//自定义的数据库名；
    private static final int version = 1;//版本号

    public MySQLiteOpenHelper(Context context) {
        super(context, db_name, null, version);
    }

    // 该方法会自动调用，首先系统会检查该程序中是否存在数据库名为‘MySchedule’的数据库
    // 如果存在则不会执行该方法，如果不存在则会执行该方法。
    @Override
    public void onCreate(SQLiteDatabase db) {
        String  sql ="create table schedules(" +
                "id Integer primary key autoincrement," +     //id自增,只支持integer不支持int
                "scheduleDetail varchar(50)," +
                "time varchar(30)" +
                ")";
        db.execSQL(sql);
    }


    //数据库版本更新时执行该方法,如果表已存在则先删除再调用onCreate重新创建
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists schedules");
        onCreate(db);
    }

}

