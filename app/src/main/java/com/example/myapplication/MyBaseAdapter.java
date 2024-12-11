package com.example.myapplication;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyBaseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list, list1;
    private Map<String, Boolean> selectedMap = new HashMap<>();
    private ArrayList<Alarm> alarms;
    private ViewHolder holder = null;
    private SetOnClickDialogListener mSetOnClickDialogListener;
    private main_alarm_activity mactivity;
    SQLiteDatabase db;
    DataBaseHelper dbHelper;
    public void OnSetOnClickDialogListener(SetOnClickDialogListener listener) {
        this.mSetOnClickDialogListener = listener;
    }
    public void setMactivity(main_alarm_activity mactivity)
    {
        this.mactivity=mactivity;
    }

    public interface SetOnClickDialogListener {
        void onClickDialogListener(int type, boolean boolClick);
    }

    public MyBaseAdapter(Context mContext, ArrayList<String> mList, ArrayList<String> mList1, Map<String, Boolean> mSelectedMap, ArrayList<Alarm> alarms,SQLiteDatabase db,DataBaseHelper dbHelper) {
        this.context = mContext;
        this.list = mList;
        this.list1 = mList1;
        this.selectedMap = mSelectedMap;
        this.alarms = alarms;
        this.db=db;
        this.dbHelper=dbHelper;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.date_list_res, parent, false);
            holder = new ViewHolder();
            holder.switch_alarm = (Switch) convertView.findViewById(R.id.switch_alarm);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.repeat = (TextView) convertView.findViewById(R.id.repeat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 如果是隐藏的闹钟，仅设置最小高度
        if (alarms.get(position).isHidden()) {
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            } else {
                params.height = 1;
            }
            convertView.setLayoutParams(params);
            return convertView;
        }else {
            ViewGroup.LayoutParams params=convertView.getLayoutParams();
            if(params==null)
            {
                params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,233);
            }else
            {
                params.height=233;
            }
            convertView.setLayoutParams(params);
        }

        holder.time.setText(list.get(position).substring(0,5));
        holder.repeat.setText(list1.get(position));
        holder.switch_alarm.setChecked(selectedMap.get(list.get(position)));

        holder.switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                if (mSetOnClickDialogListener != null) {
                    mSetOnClickDialogListener.onClickDialogListener(position, isChecked);
                }
                selectedMap.put(list.get(position), isChecked);
                alarms.get(position).setRing(isChecked);
                updataSQL(position,isChecked);
                ((main_alarm_activity) context).updateNextRingTime();
                boolean ring=false;
                for(Alarm alarm:main_alarm_activity.alarms)
                {
                    if(alarm.isRing)
                    {
                       ring=true;
                        break;
                    }
                }
                main_alarm_activity.willring= ring;
            }
        });

        if (mactivity.getlist().isItemChecked(position) && main_alarm_activity.isMultipleSelectionMode) {
                convertView.setBackgroundColor(0xffaeaeae);
        } else {
            if(main_alarm_activity.isChecked)
            {
                convertView.setBackgroundColor(0x696965);
            }
            else
            {
                convertView.setBackgroundColor(0xffffffff);
            }
        }
        return convertView;
    }

  public  void updataSQL(int position,boolean isChecked)
  {
      db=dbHelper.getWritableDatabase();
      String updateSQL = "UPDATE string_table SET string_value_ring = ? WHERE _id = ?";
      long idToUpdate = alarms.get(position).id;
      String newValue;
      if(isChecked) {
          newValue = "1";
      }
      else {
          newValue = "0";
      }

      db.execSQL(updateSQL, new String[]{newValue,  String.valueOf(idToUpdate)});
  }



    private class ViewHolder {
        Switch switch_alarm;
        TextView time;
        TextView repeat;
    }

}