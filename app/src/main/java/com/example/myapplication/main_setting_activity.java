package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class main_setting_activity extends AppCompatActivity {
    ButtonManager btnManager = new ButtonManager();
    Button btn_music;
    Button btn_vibrate;
    Switch switch_vibrate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_setting);
        switch_vibrate=(Switch) findViewById(R.id.switch_vibrate);
        switch_vibrate.bringToFront();
        switch_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                main_alarm_activity.isVibrating=isChecked;
                System.out.println(main_alarm_activity.isVibrating);
            }
        });
        Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
        btnManager.switchToActivity_btn(to_alarm_btn, main_setting_activity.this, main_alarm_activity.class);

        Button to_date_btn = (Button) findViewById(R.id.to_date_btn);
        btnManager.switchToActivity_btn(to_date_btn, main_setting_activity.this, main_date_activity.class);

        btn_music=(Button) findViewById(R.id.ring_music);
        btn_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(main_setting_activity.this,btn_music);
                popup.getMenuInflater().inflate(R.menu.menu_music, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.one) {
                            Intent intent = new Intent(main_setting_activity.this, music_select_activity.class);
                            startActivity(intent);
                        } else if (itemId == R.id.two) {
                            Intent intent= new Intent(main_setting_activity.this, local_music_select_activity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
        TextView music_name=(TextView) findViewById(R.id.music_name);
        music_name.setText(sharedPreferences.getString("music_name","默认铃声"));
    }
}

