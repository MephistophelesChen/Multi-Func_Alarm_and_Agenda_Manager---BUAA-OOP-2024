package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class music_select_activity extends Activity {
    public static ArrayList<Integer> music_list=new ArrayList<Integer>();
    public static ArrayList<String> music_name=new ArrayList<String>();
    public ListView music_listview;
   public ImageButton ensure,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (music_list.isEmpty()) {
            init();
        }

       setContentView(R.layout.setting_select_music);
       music_listview=(ListView) findViewById(R.id.music_list);
        music_adapter musicAdapter=new music_adapter(music_select_activity.this,music_name,music_listview);
       music_listview.setAdapter(musicAdapter);
       ensure=(ImageButton) findViewById(R.id.yes);
       back=(ImageButton) findViewById(R.id.back);
       ensure.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               for(int i=0;i<music_adapter.states.size();i++)
               {
                   if(music_adapter.states.get(i))
                   {
                       main_alarm_activity.alert=Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" +music_list.get(i));
                       SharedPreferences sharedPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
                       SharedPreferences.Editor editor=sharedPreferences.edit();
                       editor.putString("ring_music","android.resource://"+getApplicationContext().getPackageName()+ "/" +music_list.get(i)).apply();
                       editor.putString("music_name",music_name.get(i)).apply();
                       break;
                   }
               }
               finish();
           }
       });
       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            MediaUtil.stopRing();
               finish();
           }
       });
    }
    void init()
    {
        music_list.add(R.raw.alarm_beep);
        music_list.add(R.raw.alarm_classic);
        music_list.add(R.raw.alarm_rooster);
        music_list.add(R.raw.bugle);
        music_list.add(R.raw.creamy);
        music_list.add(R.raw.fresh_air);
        music_list.add(R.raw.guitar_heaven);
        music_list.add(R.raw.hawaii);
        music_list.add(R.raw.huawei_alarm);
        music_list.add(R.raw.ios);
        music_list.add(R.raw.ripple);
        music_list.add(R.raw.sakura_drop);
        music_list.add(R.raw.timer_beep);
        music_name.add("哔哔");
        music_name.add("经典");
        music_name.add("公鸡");
        music_name.add("喇叭");
        music_name.add("奶油");
        music_name.add("新鲜空气");
        music_name.add("吉他天堂");
        music_name.add("夏威夷1");
        music_name.add("夏威夷2");
        music_name.add("ios同款");
        music_name.add("涟漪");
        music_name.add("樱花落");
        music_name.add("计时器");

    }

}
