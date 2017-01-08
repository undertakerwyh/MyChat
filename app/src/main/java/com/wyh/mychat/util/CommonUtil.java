package com.wyh.mychat.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用工具类,时间转换
 */

public class CommonUtil {
    public final static String TYPE_PIC = "DF23J34J32FD0R234";
    public final static int TYPE_LEFT = 0;
    public final static int TYPE_RIGHT = 1;
    public final static int TYPE_TIME = 2;
    public final static int TYPE_PICLEFT = 3;
    public final static int TYPE_PICRIGHT = 4;

    public final static int SEND_ERROR = 5;
    public final static int SEND_LOAD = 4;
    public final static int SEND_SUCCESS = 3;

    private final static long dayLong = 1000 * 60 * 60 * 24;

    /**
     * 时间long型转换成String
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日hh:mm");
        String timeStr = simpleDateFormat.format(new Date(time));
        return timeStr;
    }

    public static String setDBSaveName(String name) {
        return name + TYPE_PIC;
    }

    public static String getDBLoadName(String name) {
        String[] split = name.split(TYPE_PIC);
        return split[0];
    }

    public static String getFileSize(long length) {
        DecimalFormat format = new DecimalFormat("#.00");
        if (length > 1024 * 1024 * 1024) {
            return format.format((double) length / 1024 / 1024 / 1024) + "G";
        } else if (length > 1024 * 1024) {
            return format.format((double) length / 1024 / 1024) + "M";
        } else if (length > 1024) {
            return format.format((double) length / 1024) + "K";
        } else {
            return format.format((double) length) + "B";
        }
    }
    public int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public final String getMD5String(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        /**
         *
         * @return 返回String类型的当前时间毫秒数
         */

    public static String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * @return 返回long类型的当前时间毫秒数
     */
    public static long getTimeLong() {
        return System.currentTimeMillis();
    }

    /**
     * 输入long型的时间毫秒数与当前的时间对比
     *
     * @param time 输入一个时间,必须小于当前时间
     * @return 返回与当前时间的关系(如昨天, 前天)
     */
    public static String getTimeSelect(long time) {
        if (time > getTimeLong()) {
            return null;
        }
        long nowTime = getTimeLong();
        String timeStr = null;
        if (nowTime - time < dayLong) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        } else if (nowTime - time > dayLong && nowTime - time < dayLong * 2) {
            timeStr = "昨天";
        } else if (nowTime - time > dayLong * 2 && nowTime - time < dayLong * 3) {
            timeStr = "前天";
        } else if (nowTime - time > dayLong && nowTime - time < dayLong * 2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日HH:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        } else if (nowTime - time > dayLong && nowTime - time < dayLong * 2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
            timeStr = simpleDateFormat.format(new Date(time));
        }
        return timeStr;
    }

    /**
     * 文件夹名转化路径
     *
     * @param fileName 文件夹路径
     * @return
     */
    public static String folderName(String fileName) {
        String substring = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        String name = substring.substring(substring.lastIndexOf(".") + 1, substring.length());
        return name;
    }

    public static boolean verifyPassword(String password) {
        Pattern pattern = Pattern
                .compile("^[a-zA-Z0-9]{6,16}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
