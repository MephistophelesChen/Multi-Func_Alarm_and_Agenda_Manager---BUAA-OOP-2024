package com.example.myapplication;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

public class ViewHolder {

    Switch aSwitch;
    private SparseArray<View> mViews;
    private View item;                  //存放convertView
    private int position;               //游标
    private Context context;

    private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
        mViews = new SparseArray<>();
        this.context = context;
        View convertView = LayoutInflater.from(context).inflate(layoutRes, parent,false);
        convertView.setTag(this);
        item = convertView;
        Switch switch1= (Switch) LayoutInflater.from(context).inflate(layoutRes,parent,false);
        switch1.setTag(this);
        aSwitch =switch1;
    }
    public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                  int layoutRes, int position) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder(context, parent, layoutRes);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.item = convertView;
        }
        holder.position = position;
        return holder;
    }

}
