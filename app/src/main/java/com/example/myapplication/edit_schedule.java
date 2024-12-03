package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;

public class edit_schedule extends AppCompatActivity {
    private EditText create_schedule_name;
    private EditText create_schedule_tips;
    private Button create_confirm;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_schedule);

        create_schedule_name=findViewById(R.id.create_schedule_name);
        create_schedule_tips=findViewById(R.id.create_schedule_tips);
        create_confirm = findViewById(R.id.create_confirm);
        create_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_attribute dateAttribute;
                dateAttribute=create();
                if(dateAttribute!=null){
                    main_date_activity.getDateMap().computeIfAbsent(main_date_activity.getLocalDate(), k -> new LinkedList<date_attribute>());
                    main_date_activity.getDateMap().get(main_date_activity.getLocalDate()).add(dateAttribute);
                    finish();
                }
                for (Map.Entry<LocalDate, LinkedList<date_attribute>> entry : main_date_activity.getDateMap().entrySet()) {
                    Log.d("help","Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }

            }
        });

    }


    public date_attribute create(){
           String name = create_schedule_name.getText().toString();
           String tips = create_schedule_tips.getText().toString();
           date_attribute dateAttribute;
           if(name.isEmpty()){
               Toast toast = Toast.makeText(getApplicationContext(),"日程名称为空",Toast.LENGTH_SHORT);
               toast.show();
               return null;
           }
           else{
               dateAttribute = new date_attribute(name,tips);
               return dateAttribute;
           }

    }
}
