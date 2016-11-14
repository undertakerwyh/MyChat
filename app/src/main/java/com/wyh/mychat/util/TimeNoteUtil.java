package com.wyh.mychat.util;

import android.util.Log;

import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.biz.DBManager;

/**
 * Created by Administrator on 2016/11/12.
 */

public class TimeNoteUtil implements UniversalAdapter.TimeUpdate{
    private final long timeDuration = 4*60*1000;
    private long timeRef =CommonUtil.getTimeLong();
    private long saveTime=-1;
    private int i=0;

    public String getTime(long time){
        i++;
        String timeStr = null;
        if(timeRef-time>timeDuration&&saveTime-time>timeDuration&&saveTime!=-1){
            timeRef = saveTime;
            Log.e("AAA","timeRef:"+CommonUtil.getTime(timeRef)+"---time:"+CommonUtil.getTime(time)+"----saveTime:"+CommonUtil.getTime(saveTime));
            timeStr= CommonUtil.getTimeSelect(saveTime);
        }
        if(i%DBManager.loadNum== 0){
            timeStr = CommonUtil.getTimeSelect(saveTime);
            return timeStr;
        }
        saveTime = time;
        Log.e("AAA","out -------timeRef:"+CommonUtil.getTime(timeRef)+"---time:"+CommonUtil.getTime(time)+"----saveTime:"+CommonUtil.getTime(saveTime));
        return timeStr;
    }


    @Override
    public void start(long time) {

    }

    @Override
    public void end() {

    }
}
