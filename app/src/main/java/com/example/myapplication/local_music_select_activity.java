package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class local_music_select_activity extends AppCompatActivity {
    private ListView musicListView;
    private ArrayList<String> musicFiles;
    private ArrayList<String> musicPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_select_local_music);
        musicListView = findViewById(R.id.music_list);
        musicFiles = new ArrayList<>();
        musicPath=new ArrayList<>();
        int sum=0;
        // 查询设备上的音乐文件
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(musicUri, projection, null, null, null);

        if (cursor != null) {
            TextView no=findViewById(R.id.no);
            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String[] path=filePath.split("/");
                String name=path[path.length-1];
                // 可以选择将文件名或文件路径添加到列表中
                 musicFiles.add(name);
                 musicPath.add(filePath);
                no.setVisibility(View.INVISIBLE);
            }
            cursor.close();
        }
        ImageButton back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaUtil.stopRing();
                finish();
            }
        });

        // 设置ListView的适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musicFiles);
        musicListView.setAdapter(adapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("ring_music",musicPath.get(position)).apply();
                editor.putString("music_name","本地音乐").apply();
                finish();
            }
        });
    }

}
