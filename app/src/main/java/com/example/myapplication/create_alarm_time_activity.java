package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class create_alarm_time_activity extends AppCompatActivity {
ButtonManager btnManager=new ButtonManager();
@Override
    public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_alarm_time);
    ImageButton back=(ImageButton) findViewById(R.id.back);
    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

    Button ensure=(Button) findViewById(R.id.alarm_time_ensure);

    ensure.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "成功创建闹钟", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(create_alarm_time_activity.this,main_alarm_activity.class);
            create_alarm_time_activity.this.startActivity(intent);
        }
    });
}
}
