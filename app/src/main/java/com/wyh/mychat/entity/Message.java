package com.wyh.mychat.entity;

/**
 * Created by Administrator on 2016/11/7.
 */

public class Message {
    private String name;
    private String time;
    private String content;



    private int errorType=0;
    /**
     * 0为自己发送的信息,1为对方发送的信息.
     */
    private int type;

    public Message(String name, String time, String content, int type) {
        this.name = name;
        this.time = time;
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
