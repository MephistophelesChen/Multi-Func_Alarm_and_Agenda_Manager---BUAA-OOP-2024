package com.example.myapplication;

import java.util.ArrayList;

public class Alarm {
    public int hour;
    public int minute;
    ArrayList<Boolean> repeat = new ArrayList<Boolean>();//例如repeat.get（0）==true表示每周一重复
    boolean isRing = false;
    public boolean isHidden = false; //是否隐藏
    public int id;//id每增加一个闹铃都会增加，便于删除
    boolean is_checked = false;//是否被选中

    public Alarm(int hour, int minute, ArrayList<Boolean> repeat, boolean isRing) {
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.isRing = isRing;
    }

    public Alarm() {
    }

    public boolean isHidden() {
        return isHidden;
    }
    public int compareTo(Alarm other) {
        if (this.hour != other.hour) {
            return Integer.compare(this.hour, other.hour);
        } else {
            return Integer.compare(this.minute, other.minute);
        }
    }

    public ArrayList<Boolean> getRepeat() {
        return repeat;
    }

    public void setRepeat(ArrayList<Boolean> repeat) {
        this.repeat = repeat;
    }

    public boolean isRing() {
        return isRing;
    }

    public void setRing(boolean ring) {
        isRing = ring;  // ring = TRUE or False
    }

    public void switchRing() {
        isRing = !isRing; // switch the ring
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int time) {
        this.minute = time;
    }
}
