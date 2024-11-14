package com.example.myapplication;

import android.content.Context;
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
    private ArrayList<String> list,list1;
    private Map<String, Boolean> selectedMap = new HashMap<>();
    private ViewHolder holder = null;
    private SetOnClickDialogListener mSetOnClickDialogListener;


    public void OnSetOnClickDialogListener(SetOnClickDialogListener listener) {
        this.mSetOnClickDialogListener = listener;
    }
    public interface SetOnClickDialogListener{
        void onClickDialogListener(int type, boolean boolClick);
    }

    public MyBaseAdapter(Context mContext, ArrayList<String> mList,ArrayList<String> mList1,Map<String, Boolean> mSelectedMap){
        this.context = mContext;
        this.list = mList;
        this.list1=mList1;
        this.selectedMap = mSelectedMap;
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

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.date_list_res,parent,false);
            holder = new ViewHolder();
            holder.switch_alarm =  (Switch)convertView.findViewById(R.id.switch_alarm);
            holder.time =   (TextView) convertView.findViewById(R.id.time);
            holder.repeat= (TextView) convertView.findViewById(R.id.repeat);//绑定ViewHolder和items
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.time.setText(list.get(position));
        holder.repeat.setText(list1.get(position));
        holder.switch_alarm.setChecked(selectedMap.get(list.get(position)));
        holder.switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed()){
                    return;
                }
                if(mSetOnClickDialogListener != null){
                    mSetOnClickDialogListener.onClickDialogListener(position, isChecked);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        Switch switch_alarm;
        TextView time;
        TextView repeat;
    }

}

