package com.wyh.mychat.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/10/22.
 */

public class TouchViewPager extends ViewPager {
    public static boolean isCheaked() {
        return isCheaked;
    }

    private static boolean isCheaked=false;

    public TouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isCheaked = true;
        }else if(ev.getAction()==MotionEvent.ACTION_UP||ev.getAction()==MotionEvent.ACTION_CANCEL){
            isCheaked = false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
