package com.example.myapplication;

import java.util.ArrayList;

public class Tool {
    public static String addrepeat(ArrayList<Boolean> repeat)
    {
        String a="";
        if(!repeat.contains(true))
            return "不重复";
        if(repeat.get(0))
        {
            a+=" 星期一";
        }
        if(repeat.get(1))
        {
            a+=" 星期二";
        }
        if(repeat.get(2))
        {
            a+=" 星期三";
        }
        if(repeat.get(3))
        {
            a+=" 星期四";
        } if(repeat.get(4))
        {
            a+=" 星期五";
        }
        if(repeat.get(5))
        {
            a+=" 星期六";
        }
        if(repeat.get(6))
        {
            a+=" 星期日";
        }
        return a;

    }
    public static String booleanToString(ArrayList<Boolean> ar)
    {
        String s="";
        for(Boolean b:ar)
        {
            s+=b?"1":"0";
        }
        return s;
    }

    public static ArrayList<Boolean> StringToBoolean(String s)
    {
        ArrayList<Boolean> ar=new ArrayList<Boolean>();
        char[] ch=s.toCharArray();
        for(int i=0;i<ch.length;i++)
        {
            ar.add(ch[i]=='1');
        }
        return ar;
    }
}
