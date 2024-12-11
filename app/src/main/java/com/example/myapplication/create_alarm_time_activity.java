package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class create_alarm_time_activity extends AppCompatActivity {
    private TimePicker timePicker;
    private BottomAdapter adapter;
    private List<Model> repeatDay =new ArrayList<>();
    ButtonManager btnManager=new ButtonManager();
    public boolean mIsFromItem=false;
     mNumberPicker numberPicker;
@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_alarm_time);
        ImageButton back=(ImageButton) findViewById(R.id.back);
        timePicker = (TimePicker) findViewById(R.id.time_picker);

        Resources systemResources = Resources.getSystem();
        int hourNumberPickerId = systemResources.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = systemResources.getIdentifier("minute", "id", "android");
        int dividerId = Resources.getSystem().getIdentifier("divider", "id", "android");
        int ampmid=systemResources.getIdentifier("amPm","id","android");
        NumberPicker ampmPicker=(NumberPicker)timePicker.findViewById(ampmid);
        NumberPicker hourNumberPicker = (NumberPicker) timePicker.findViewById(hourNumberPickerId);
        NumberPicker minuteNumberPicker = (NumberPicker) timePicker.findViewById(minuteNumberPickerId);
        TextView divider=(TextView) timePicker.findViewById(dividerId);
        divider.setText("");
        hourNumberPicker.setSelectionDividerHeight(4);
        hourNumberPicker.setTextColor(getColor(R.color.blue_4));
        hourNumberPicker.setTextSize(80);
        hourNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minuteNumberPicker.setSelectionDividerHeight(4);
        minuteNumberPicker.setTextColor(getColor(R.color.blue_4));
        minuteNumberPicker.setTextSize(80);
        ampmPicker.setSelectionDividerHeight(4);
        ampmPicker.setTextColor(getColor(R.color.blue_4));
        ampmPicker.setTextSize(50);
        ViewGroup.LayoutParams params = hourNumberPicker.getLayoutParams();
        params.width = 300; // 或者一个具体的像素值，如 300
        hourNumberPicker.setLayoutParams(params);
        minuteNumberPicker.setLayoutParams(params);
        ampmPicker.setLayoutParams(params);



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
