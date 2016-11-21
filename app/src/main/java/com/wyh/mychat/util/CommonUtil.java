package com.wyh.mychat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常用工具类,时间转换
 */

public class CommonUtil {
    public final static String MYSEND = "DF23J34J32FD0R234";
    public final static int TYPE_LEFT = 0;
    public final static int TYPE_RIGHT = 1;
    public final static int TYPE_TIME = 2;

    private final static long dayLong = 1000*60*60*24;

    /**
     * 时间long型转换成String
     * @param time
     * @return
     */
    public static String getTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日hh:mm");
        String timeStr = simpleDateFormat.format(new Date(time));
        return timeStr;
    }

    public static String setDBSaveName(String name){
        return name+MYSEND;
    }
    public static String getDBLoadName(String name){
        String[] split = name.split(MYSEND);
        return split[0];
    }
    public static String getTime(){
        return String.valueOf(System.currentTimeMillis());
    }
    public static long getTimeLong(){
        return System.currentTimeMillis();
    }

    public static String getTimeSelect(long time){
        long nowTime = getTimeLong();
        String timeStr=null;
        if(nowTime-time<dayLong){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        }else if(nowTime-time>dayLong&&nowTime-time<dayLong*2){
            timeStr = "昨天";
        }else if(nowTime-time>dayLong*2&&nowTime-time<dayLong*3){
            timeStr = "前天";
        }else if(nowTime-time>dayLong&&nowTime-time<dayLong*2){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日hh:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        }else if(nowTime-time>dayLong&&nowTime-time<dayLong*2){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日hh:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        }
        return timeStr;
    }

    public static String folderName(String fileName){
        String substring = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        return substring;
    }

}
