package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "my_database.db";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "string_table";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_STRING1 = "string_value_time";
    static final String COLUMN_STRING2 = "string_value_repeat";
    static final String COLUMN_ALARM_HOUR = "string_value_hour";
    static final String COLUMN_ALARM_MINUTE = "string_value_minute";
    static final String COLUMN_ALARM_RING = "string_value_ring";
    static final String COLUMN_ALARM_REPEAT = "string_value_arepeat";
    static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STRING1 + " TEXT," +
                    COLUMN_STRING2 + " TEXT," +
                    COLUMN_ALARM_RING + " TEXT," +
                    COLUMN_ALARM_HOUR + "  TEXT," +
                    COLUMN_ALARM_REPEAT + " TEXT," +
                    COLUMN_ALARM_MINUTE + "  TEXT);";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_NAME);
        onCreate(db);
    }
}
