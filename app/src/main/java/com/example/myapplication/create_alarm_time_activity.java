package com.example.myapplication;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class create_alarm_time_activity extends AppCompatActivity {
    private TimePicker timePicker;
    private BottomAdapter adapter;
    private List<Model> repeatDay =new ArrayList<>();
    ButtonManager btnManager=new ButtonManager();
    public boolean mIsFromItem=false;

@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_alarm_time);
        ImageButton back=(ImageButton) findViewById(R.id.back);
        timePicker = (TimePicker) findViewById(R.id.time_picker);

        Button repeat=(Button) findViewById(R.id.repeatButton);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_bottom_sheet);

        ListView listView=dialog.findViewById(R.id.bottom_sheet);
        init();
        BottomAdapter bottomAdapter=new BottomAdapter(this, repeatDay);
        listView.setAdapter(bottomAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                repeatDay=bottomAdapter.getData();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                repeatDay=bottomAdapter.getData();
            }
        });

        ImageButton ensure=(ImageButton) findViewById(R.id.yes);

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "成功创建闹钟", Toast.LENGTH_SHORT).show();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                ArrayList<Boolean> repeatDays = new ArrayList<>();
                    repeatDay=bottomAdapter.getData();
                    for (int i = 0; i < 7; i++) {
                        if(repeatDay.get(i).ischeck()==true)
                        {
                            repeatDays.add(true);// 默认每一天重复
                        }
                        else {
                            repeatDays.add(false);
                        }
                    } // 默认每一天重复


                Intent resultIntent = new Intent();
                resultIntent.putExtra("hour", hour);
                resultIntent.putExtra("minute", minute);
                resultIntent.putExtra("repeatDays", repeatDays);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    private void init()
    {
    repeatDay.add(new Model("星期一"));
    repeatDay.add(new Model("星期二"));
    repeatDay.add(new Model("星期三"));
    repeatDay.add(new Model("星期四"));
    repeatDay.add(new Model("星期五"));
    repeatDay.add(new Model("星期六"));
    repeatDay.add(new Model("星期日"));
    }
    interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }
    }
