package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonManager{
      //普通button
      public void switchToActivity_btn( View btn, Activity from, Class<? extends AppCompatActivity> toClass) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(from, toClass);
                from.startActivity(intent);
            }
        });
    }



}
