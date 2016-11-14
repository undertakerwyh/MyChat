package com.wyh.mychat.entity;

/**
 * Created by Administrator on 2016/11/5.
 */

public class Friends {
    private String icon;
    private String name;

    @Override
    public String toString() {
        return "Friends{" +
                "icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Friends(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
