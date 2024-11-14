package com.example.myapplication;


public class Model {
    private boolean ischeck;
    private String st;

    public Model(String st) {
        this.st = st;
    }


    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }
}

