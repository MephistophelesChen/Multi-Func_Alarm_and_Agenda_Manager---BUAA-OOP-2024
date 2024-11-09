package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ButtonManager{
      //普通button
      public void switchToActivity_btn( View btn, Activity from, Class<? extends AppCompatActivity> toClass) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(from, toClass);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                from.startActivity(intent);
            }
        });
    }



}
