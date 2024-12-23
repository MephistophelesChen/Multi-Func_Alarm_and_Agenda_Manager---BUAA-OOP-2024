package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class MySQLiteOpenHelper extends SQLiteOpenHelper{
   private static final String DATABASE_NAME = "mydatabase.db";
   private static final int DATABASE_VERSION=5;
   private static final String TABLE_NAME = "mytable";
   private static final String COLUMN_ID = "id";

   private static final String TABLE_DATE_ATTRIBUTE_CREATE =
           "CREATE TABLE DateAttribute("+
           "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
           "attribute1 TEXT,"+
           "attribute2 TEXT,"+
           "idx INTEGER,"+
           "isSwitchOn INTEGER);";
   public static final String TABLE_LOCAL_DATE_MAP_CREATE=
           "CREATE TABLE LocalDateMap("+
           "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
           "date TEXT ,"+
           "dateAttributeId INTEGER);";


   public MySQLiteOpenHelper(Context context){
       super(context,DATABASE_NAME,null,DATABASE_VERSION);
   }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_DATE_ATTRIBUTE_CREATE);
        db.execSQL(TABLE_LOCAL_DATE_MAP_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DateAttribute");
        db.execSQL("DROP TABLE IF EXISTS LocalDateMap");
       onCreate(db);
    }
        public void deleteSQL(){
       SQLiteDatabase db =this.getWritableDatabase();
       db.delete("LocalDateMap",null,null);
       db.delete("DateAttribute",null,null);
    }
}

