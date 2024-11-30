package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class main_date_activity extends AppCompatActivity {
    private Button to_alarm_btn;
    String TAG="mtTag";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_date);
        Log.d(TAG,"onCreate: ");
        to_alarm_btn=findViewById(R.id.to_alarm_btn);
        to_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart: ");
    }
    @Override
    public void onResume(){
        super.onResume();
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
        Log.d(TAG,"onDestroy: ");
    }


}
