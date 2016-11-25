package com.wyh.mychat.util;

/**
 * Created by Administrator on 2016/11/12.
 */

public class TimeNoteUtil{
    public static final long timeDuration = 4*60*1000;
    private long saveTime;
    private boolean isFirst=true;

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String start(long time) {
        String timeStr = null;
        if(time-saveTime>timeDuration||isFirst){
            isFirst = false;
            saveTime = time;
            timeStr = CommonUtil.getTimeSelect(saveTime);
        }
        return timeStr;
    }
    private long sendSaveTime;

    public String sendStart(long time){
        String timeStr = null;
        if(time - sendSaveTime>timeDuration){
            timeStr = CommonUtil.getTimeSelect(sendSaveTime);
            sendSaveTime = time;
        }
        return timeStr;
    }
}
