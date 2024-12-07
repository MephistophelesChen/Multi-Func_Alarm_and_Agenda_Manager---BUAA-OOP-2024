package com.example.myapplication;

import android.content.Context;

import android.widget.*;
import android.view.*;

import java.util.Calendar;
import java.util.LinkedList;

public class date_adapter extends BaseAdapter {
       private LinkedList<date_attribute> mData;
       private Context mContext;
       private int resource;


       public date_adapter(LinkedList<date_attribute> mData, Context context,int resource){
           this.mContext=context;
           this.mData=mData;
           this.resource=resource;
       }
       @Override
       public int getCount(){
           return mData.size();
       }
       @Override
       public Object getItem(int position){
           return null;
       }
       //null?
       @Override
       public long getItemId(int position){
           return position;
       }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
           if(convertView==null) {
               convertView = LayoutInflater.from(mContext).inflate(resource, parent, false);
           }
         //ImageView img_icon = (ImageView)convertView.findViewById(R.id.back);
        TextView xingxing=(TextView) convertView.findViewById(R.id.schedule_name);
        TextView tips=(TextView) convertView.findViewById(R.id.schedule_tips);
        //img_icon.setBackgroundResource(mData.get(position).getIcon());
        xingxing.setText(mData.get(position).getName());
        tips.setText(mData.get(position).getTips());
        return convertView;
    }

    public void updateDate(LinkedList<date_attribute> Date){
           this.mData.clear();
           this.mData.addAll(Date);
           notifyDataSetChanged();
    }
}

