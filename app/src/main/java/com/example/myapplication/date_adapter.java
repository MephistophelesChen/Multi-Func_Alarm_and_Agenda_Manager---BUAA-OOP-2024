package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.date_attribute;
import com.example.myapplication.main_date_activity;

import java.util.LinkedList;

//mData 是副本
public class date_adapter extends BaseAdapter {
       private LinkedList<date_attribute> mData;
       private Context mContext;
       private int resource;


       private static class ViewHolder{
           TextView xingxing;
           TextView tips;
            CheckBox checkBox;
           int position;
       }
       public date_adapter(LinkedList<date_attribute> mData, Context context, int resource){
           this.mContext=context;
           this.mData=mData;
           this.resource=resource;
       }
       @Override
       public int getCount(){
           return mData.size();
       }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    //null?
       @Override
       public long getItemId(int position){
           return position;
       }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.xingxing = convertView.findViewById(R.id.schedule_name);
            holder.tips = convertView.findViewById(R.id.schedule_tips);
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position=position;
        date_attribute dateAttribute = (date_attribute) getItem(position);
        holder.xingxing.setText(dateAttribute.getName());
        holder.tips.setText(dateAttribute.getTips());
        holder.checkBox.setChecked(dateAttribute.getIsSwitchOn());


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentIndex = holder.position; // 使用holder中的position
            date_attribute currentItem = (date_attribute) getItem(currentIndex);
            currentItem.setIsSwitchOn(isChecked); // 更新数据模型
            main_date_activity.getDateMap().get(main_date_activity.getLocalDate()).get(currentIndex).setIsSwitchOn(isChecked);
            // 更新数据库（注意：这里应该在一个异步任务中执行，以避免阻塞UI线程）
            int id = currentItem.getId();
            Log.d("mtTag",String.valueOf(id));
            if (id != -1) {
                main_date_activity.updateIsSwitchOnById(main_date_activity.getDbHelper().getWritableDatabase(), id, isChecked);
            } else {
                Log.d("mtTag", "No matching record found for attributes");
            }
        });

        return convertView;
    }

    public void updateDate(LinkedList<date_attribute> Date){
           this.mData.clear();
           this.mData.addAll(Date);
           notifyDataSetChanged();

    }



}