package com.example.myapplication;

import java.time.LocalDate;

public class date_attribute {


    private String name;
    private String tips;
    private LocalDate localDate;
    private boolean isSwitchOn;
    private int  id;
    private boolean zhongyao;
    private boolean jinji;

    // private int icon;

    public date_attribute(){

    }
    public date_attribute(String name, String tips){
        this.tips=tips;
        this.name=name;
        this.isSwitchOn=false;
        this.zhongyao = false;
        this.jinji = false;
    }
    public date_attribute(String name, String tips, boolean isSwitchOn){
        this.tips=tips;
        this.name=name;
        this.isSwitchOn=isSwitchOn;
    }
    public date_attribute(String name,String tips,boolean isSwitchOn,boolean zhongyao,boolean jinji){
        this.tips=tips;
        this.name=name;
        this.isSwitchOn=isSwitchOn;
        this.zhongyao=zhongyao;
        this.jinji=jinji;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public boolean getIsSwitchOn() {return isSwitchOn;}

    public void setIsSwitchOn(boolean switchOn) {
        isSwitchOn = switchOn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isZhongyao() {
        return zhongyao;
    }

    public void setZhongyao(boolean zhongyao) {
        this.zhongyao = zhongyao;
    }

    public boolean isJinji() {
        return jinji;
    }

    public void setJinji(boolean jinji) {
        this.jinji = jinji;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
    public int getYear(){
        return localDate.getYear();
    }
    public int getMonth(){
        return localDate.getMonthValue();
    }
    public int getDayOfMonth(){
        return localDate.getDayOfMonth();
    }

    //    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }
}