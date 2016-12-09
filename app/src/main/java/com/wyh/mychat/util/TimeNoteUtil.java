package com.wyh.mychat.util;

/**
 * Created by Administrator on 2016/11/12.
 */

public class TimeNoteUtil {
    public static final long timeDuration = 1 * 60 * 1000;
    private long saveTime;
    private boolean isFirst = true;

    private static TimeNoteUtil timeNoteUtil = null;

    synchronized public static TimeNoteUtil getTimeNoteUtil() {
        if (timeNoteUtil == null) {
            timeNoteUtil = new TimeNoteUtil();
        }
        return timeNoteUtil;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    private boolean isRefresh = true;

    public String start(long time) {
        String timeStr = null;
        if (time - saveTime > timeDuration || isFirst) {
            saveTime = time;
            if(isRefresh){
                sendSaveTime = time;
            }
            timeStr = CommonUtil.getTimeSelect(saveTime);
            isFirst = false;
        }
        return timeStr;
    }

    private long sendSaveTime;

    public String sendStart(long time) {
        String timeStrs = null;
        if (time - sendSaveTime > timeDuration) {
            timeStrs = CommonUtil.getTimeSelect(time);
            sendSaveTime = time;
        }
        return timeStrs;
    }
}
