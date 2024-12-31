package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
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
import androidx.appcompat.app.AppCompatDelegate;

public class main_setting_activity extends AppCompatActivity {
    ButtonManager btnManager = new ButtonManager();
    Button btn_music;
    Switch switch_vibrate;
    Button theme_select;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_setting);
        TextView tt=findViewById(R.id.sleeptime);
        SharedPreferences sharedPreferences=getSharedPreferences("sleeptime",Context.MODE_PRIVATE);
        String time=sharedPreferences.getString("sleep","1分钟(演示用)");
        tt.setText(time);
        switch_vibrate=(Switch) findViewById(R.id.switch_vibrate);
        switch_vibrate.setChecked(main_alarm_activity.EnableVibrate);
        switch_vibrate.bringToFront();
        switch_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                main_alarm_activity.EnableVibrate=isChecked;
                SharedPreferences sharedPreferences=getSharedPreferences("vibrate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("isVibrate",main_alarm_activity.EnableVibrate).apply();
                System.out.println(main_alarm_activity.EnableVibrate);
            }
        });
        Button to_alarm_btn = (Button) findViewById(R.id.to_alarm_btn);
        btnManager.switchToActivity_btn(to_alarm_btn, main_setting_activity.this, main_alarm_activity.class);

        Button to_date_btn = (Button) findViewById(R.id.to_date_btn);
        btnManager.switchToActivity_btn(to_date_btn, main_setting_activity.this, main_date_activity.class);
        Button to_todolist_btn=(Button) findViewById(R.id.to_todolist_btn);
        btnManager.switchToActivity_btn(to_todolist_btn,main_setting_activity.this,main_todolist_activity.class);
        theme_select=(Button) findViewById(R.id.theme);
        theme_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
PopupMenu popupMenu=new PopupMenu(main_setting_activity.this,theme_select);
popupMenu.getMenuInflater().inflate(R.menu.menu_theme,popupMenu.getMenu());
popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        SharedPreferences sharedPreferences=getSharedPreferences("theme",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        TextView textView=findViewById(R.id.themeText);
        int itemId=menuItem.getItemId();
        if(itemId==R.id.day){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            textView.setText("白天");
            editor.putInt("themeSelect",0);
        }else if(itemId==R.id.night){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            textView.setText("黑夜");
            editor.putInt("themeSelect",1);
        }else if(itemId==R.id.systemTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            textView.setText("跟随系统");
            editor.putInt("themeSelect",2);
        }
        editor.apply();
        return true;
    }
});popupMenu.show();
            }
        });
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
        Button facebutton=findViewById(R.id.face_identity);
        facebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               PopupMenu popupMenu=new PopupMenu(main_setting_activity.this,facebutton);
               popupMenu.getMenuInflater().inflate(R.menu.menu_sleeptime,popupMenu.getMenu());
               TextView textView=findViewById(R.id.sleeptime);
               SharedPreferences sharedPreferences=getSharedPreferences("sleeptime",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem menuItem) {
                       int id=menuItem.getItemId();
                       if(id==R.id.min1){
                           textView.setText("1分钟(演示用)");
                           editor.putString("sleep","1分钟(演示用)");
                       }else if(id==R.id.min5){
                           textView.setText("5分钟");
                           editor.putString("sleep","5分钟");
                       }else if(id==R.id.min10){
                           textView.setText("10分钟");
                           editor.putString("sleep","10分钟");
                       } else if (id==R.id.min20) {
                           textView.setText("20分钟");
                           editor.putString("sleep","20分钟");
                       }
                       return true;
                   }
               });
               popupMenu.show();
            }
        });
        Button showbutton=findViewById(R.id.show_music);
        showbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri alert;
                SharedPreferences sharedPreferences=getSharedPreferences("music",Context.MODE_PRIVATE);
                String path=sharedPreferences.getString("ring_music","null");
                if(!path.equals("null"))
                {
                    alert=Uri.parse(path);
                }
                else {
                    alert= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
               MediaUtil.stopRing();
                MediaUtil.playRing(main_setting_activity.this,alert);
            }
        });
        Button stopbutton=findViewById(R.id.stop);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtil.stopRing();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
        TextView music_name=(TextView) findViewById(R.id.music_name);
        music_name.setText(sharedPreferences.getString("music_name","默认铃声"));
        TextView textView=findViewById(R.id.themeText);
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO){
            textView.setText("白天");
        } else if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            textView.setText("黑夜");
        }else if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM){
            textView.setText("跟随系统");
        }
        switch_vibrate.setChecked(main_alarm_activity.EnableVibrate);
    }
}

