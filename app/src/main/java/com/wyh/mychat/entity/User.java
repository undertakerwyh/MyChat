package com.wyh.mychat.entity;

/**
 * Created by Administrator on 2016/11/5.
 */

public class User {
    private String Icon;
    private String name;
    private String content;
    private String time;

    public User(String icon, String name, String content, String time) {
        Icon = icon;
        this.name = name;
        this.content = content;
        this.time = time;
    }

    @Override
    public String toString() {
        return "User{" +
                "Icon='" + Icon + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }
}
