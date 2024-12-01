package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class music_adapter extends BaseAdapter{
    Context context;
    ArrayList<String> list;
    private LayoutInflater inflater;
    public static HashMap<Integer, Boolean> states = new HashMap<Integer, Boolean>();  //储存已改变的选项数据
    public HashMap<Integer,Boolean> if_play=new HashMap<Integer,Boolean>();
    ListView listView;

    boolean isVisiable = true;

    public music_adapter(Context context,ArrayList<String> list,ListView listView){
        this.listView=listView;
        this.context = context;
        this.list = list;
    for(int i=0;i<list.size();i++)
    {
        states.put(i,false);
        if_play.put(i,false);
    }
        inflater = LayoutInflater.from(context);
    }

    public void refresh(ArrayList<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        holder = new Holder();
        convertView = inflater.inflate(R.layout.music_item_res, null);
        holder.advice = (TextView) convertView.findViewById(R.id.music_name);
        holder.raButton = (RadioButton) convertView.findViewById(R.id.radio);
        holder.playButton=(ImageButton) convertView.findViewById(R.id.play_button);
        if(!if_play.get(position))
        {
            holder.playButton.setImageResource(R.drawable.end);
        }
        holder.raButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把所有的按钮的状态设置为没选中
                for (int i = 0; i < getCount(); i++) {
                    states.put(i, false);
                }
                //然后设置点击的那个按钮设置状态为选中
                states.put(position, true);    //这样所有的条目中只有一个被选中！
                notifyDataSetChanged();//刷新适配器
            }
        });
        if (states.get((Integer) position) == null || states.get((Integer) position) == false) {  //true说明没有被选中
            holder.raButton.setChecked(false);
        } else {
            holder.raButton.setChecked(true);
        }
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!if_play.get((Integer) position)) {
                 //   if(MediaUtil.mediaPlayer.isPlaying())
                    MediaUtil.stopRing();
                    Uri uri=Uri.parse("android.resource://" + context.getPackageName() + "/" + music_select_activity.music_list.get(position));
                    MediaUtil.playRing(context, uri);
                   for(int i=0;i<parent.getChildCount();i++)
                   {
                       ImageButton button=listView.getChildAt(i).findViewById(R.id.play_button);
                       button.setImageResource(R.drawable.end);
                   }
                    holder.playButton.setImageResource(R.drawable.start);
                   if_play.put((Integer) position,true);
                }
                else {
                    MediaUtil.stopRing();
                    holder.playButton.setImageResource(R.drawable.end);
                    if_play.put((Integer) position,false);
                }
            }
        });
        convertView.setTag(holder);
        holder.advice.setText(list.get(position));
        final RadioButton raButton = (RadioButton) convertView.findViewById(R.id.radio);
        holder.raButton = raButton;
        return convertView;
    }

    protected class Holder{
        TextView advice;
        ImageButton playButton;
        RadioButton raButton;
    }
    //返回已改变的选项数据

    //获取字符串借口

}