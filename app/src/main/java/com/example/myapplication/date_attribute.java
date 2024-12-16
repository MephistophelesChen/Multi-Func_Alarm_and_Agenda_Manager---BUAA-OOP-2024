package com.example.myapplication;

public class date_attribute {


    private String name;
    private String tips;
    private boolean isSwitchOn;
    private int  id;


    // private int icon;

    public date_attribute(){

    }
    public date_attribute(String name, String tips){
        this.tips=tips;
        this.name=name;
        this.isSwitchOn=true;
    }
    public date_attribute(String name, String tips, boolean isSwitchOn){
        this.tips=tips;
        this.name=name;
        this.isSwitchOn=isSwitchOn;
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
//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }
}