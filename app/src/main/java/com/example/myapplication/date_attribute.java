package com.example.myapplication;

public class date_attribute{


    private String name;
    private String tips;
    // private int icon;

    public date_attribute(){

    }
    public date_attribute(String name,String tips){
        // this.icon=icon;
        this.tips=tips;
        this.name=name;
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

//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }
}