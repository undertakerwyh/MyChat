package com.wyh.mychat.entity;

/**
 * Created by Administrator on 2016/11/7.
 */

public class Message {
    private String name;
    private long time;
    private String content;
    private int type;
    private int errorType = 0;
    private String bitmapPath;

    public String getBitmapPath() {
        return bitmapPath;
    }

    public void setBitmapPath(String bitmapPath) {
        this.bitmapPath = bitmapPath;
    }


    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    private boolean isNew = false;


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    /**
     * 0为自己发送的信息,1为对方发送的信息.
     */

    public Message(String name, long time, String content, int type) {
        this.name = name;
        this.time = time;
        this.content = content;
        this.type = type;
    }
    public Message(String name, long time,int type, String bitmapPath){
        this.name = name;
        this.time = time;
        this.bitmapPath = bitmapPath;
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
