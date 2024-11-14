package com.example.myapplication;

import java.util.ArrayList;

public class Alarm {
  public int hour;
  public int minute;
  ArrayList<Boolean> repeat=new ArrayList<Boolean>();//例如repeat.get（0）==true表示每周一重复
  boolean isRing=false;
  public int ap=1;//ap==1->AM,ap==2->PM
  public int hour24=0;//


    public Alarm(int hour, int minute, ArrayList<Boolean> repeat) {
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
    }


    public int getHour24() {
        return hour24;
    }

    public void setHour24(int hour24) {
        this.hour24 = hour24;
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

    public void switchRing(){
        isRing=!isRing; // switch the ring
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
