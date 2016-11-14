package com.wyh.mychat.util;

/**
 * Created by Administrator on 2016/11/12.
 */

public class TimeNoteUtil{
    private final long timeDuration = 1*60*1000;
    private long timeRef;
    private long saveTime;
    private boolean isFirst = true;

    public String start(long time) {
        String timeStr = null;
        if(isFirst){
            timeRef = time;
            isFirst = false;
        }
        if(saveTime-time>timeDuration){
            timeStr = CommonUtil.getTimeSelect(saveTime);
            timeRef = time;
            saveTime = time;
            return timeStr;
        }
        saveTime = time;
        return timeStr;
    }

    public String end() {
        return CommonUtil.getTimeSelect(saveTime);
    }
}
