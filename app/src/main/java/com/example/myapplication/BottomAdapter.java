package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class BottomAdapter extends BaseAdapter {
    private List<Model> data;
    private Context context;

    public BottomAdapter(Context context, List<Model> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.bottom_sheet_res, null);
            hd.textView = (TextView) view.findViewById(R.id.week);
            hd.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            view.setTag(hd);
        }
        Model mModel = data.get(i);
        hd = (ViewHoder) view.getTag();
        hd.textView.setText(mModel.getSt());
        final ViewHoder hdFinal = hd;
        hd.checkBox.setChecked(mModel.ischeck());
        hd.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = hdFinal.checkBox;
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                } else if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                } else {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                }
            }
        });
        return view;
    }

    public List<Model> getData() {
        return data;
    }

    class ViewHoder {
        TextView textView;
        CheckBox checkBox;
    }

}

