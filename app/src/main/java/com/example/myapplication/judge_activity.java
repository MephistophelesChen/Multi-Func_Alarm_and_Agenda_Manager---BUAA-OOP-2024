package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.Format;

public class judge_activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.judge);
        RatingBar ratingBar=findViewById(R.id.star);
        TextView textView=findViewById(R.id.score);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                String string;
                string= String.format("%.2f",v);
                textView.setText(string);
            }
        });
        Button button=findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(judge_activity.this,"thanks",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        Button button1=findViewById(R.id.cheat);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingBar.setRating(5.0f);
                textView.setText("2147483647");
                Toast.makeText(judge_activity.this,"thanks!!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
