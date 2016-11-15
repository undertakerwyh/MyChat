package com.wyh.mychat.util;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.wyh.mychat.view.TouchViewPager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/5.
 */

public class PageChangeAnimUtil {
    private static int positionPos;
    /**
     * 记录上一个点的位置
     */
    private float oldPixel = 0;
    /**
     * 滚动条的位置信息
     */
    private float left;
    private float right;
    private int bottom;
    private int top;

    private float bottomSpeed =1.5f;
    /**
     * isLeft为true时往左滑,false时往右滑
     */
    private boolean isLeft;
    /**
     * Viewpager是否静止,true为静止
     */
    private boolean isIdle = true;
    /**
     * 记录Viewpager的滚动页
     */
    private int oldPosition = 0;

    public static int getPositionPos() {
        return positionPos;
    }

    public static PageChangeAnimUtil pageChangeAnimUtil = null;
    private ExecutorService executorService;

    private Handler handler = new Handler();

    public static PageChangeAnimUtil getPageChangeAnimUtil(Context context) {
        if (pageChangeAnimUtil == null) {
            synchronized (context) {
                pageChangeAnimUtil = new PageChangeAnimUtil();
            }
        }
        return pageChangeAnimUtil;
    }

    private float maxScreen;
    private LinearLayout linearLayout;

    /**
     * viewpager的滚动与滚动条的交互,需要重写ViewPager的dispatchTouchEvent方法添加是否触摸屏幕的判断
     *
     * @param viewPager    Viewpager 与滚动条的绑定
     * @param linearLayout 滚动条布局
     * @param maxScreen    屏幕大小
     */
    public void pageChangeAnim(ViewPager viewPager, final LinearLayout linearLayout, final float maxScreen) {
        this.maxScreen = maxScreen;
        this.linearLayout = linearLayout;
        executorService = Executors.newCachedThreadPool();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, int positionOffsetPixels) {
                positionPos = positionOffsetPixels;
                if (isIdle) {
                    oldPixel = positionOffsetPixels;
                    isIdle = false;
                }
                if (oldPosition != position) {
                    oldPixel = positionOffsetPixels;
                }
                if (positionOffsetPixels - oldPixel > 0) {
                    isLeft = true;
                } else if (positionOffsetPixels - oldPixel < 0) {
                    isLeft = false;
                }
                final float move = Math.abs(positionOffsetPixels- oldPixel);
                top = linearLayout.getTop();
                bottom = linearLayout.getBottom();
                left = linearLayout.getLeft();
                right = linearLayout.getRight();
                final int finalPositionOffsetPixels = positionOffsetPixels;
                /**
                 * TouchViewPager:重写Listview的dispatchTouchEvent方法添加是否触摸屏幕的判断
                 */
                if (TouchViewPager.isCheaked() || finalPositionOffsetPixels != 0) {
                    if (isLeft) {
                        switch (position){
                            case 0:
                                if((int) (left + move/3+bottomSpeed)>(int) (maxScreen / 3)){
//                                    linearLayout.layout((int) (maxScreen / 3), top, (int) maxScreen + (int) (maxScreen / 3), bottom);
                                }else{
                                    linearLayout.layout((int) (left + move/3+bottomSpeed), top, (int) (right + move/3+bottomSpeed), bottom);
                                }
                                break;
                            case 1:
                                if((int) (left + move/3+bottomSpeed)>(int) (2*maxScreen / 3)){
//                                    linearLayout.layout((int) (2 * maxScreen / 3), top, (int) maxScreen + (int) (2 * maxScreen / 3), bottom);
                                }else{
                                    linearLayout.layout((int) (left + move/3+bottomSpeed), top, (int) (right + move/3+bottomSpeed), bottom);
                                }
                                break;
                        }
                    } else {
                        switch (position){
                            case 0:
                                if((int)(left - move/3-bottomSpeed)<0){
                                    linearLayout.layout(0, top, (int) maxScreen, bottom);
                                }else{
                                    linearLayout.layout((int)(left - move/3-bottomSpeed), top, (int)(right - move/3-bottomSpeed), bottom);
                                }
                                break;
                            case 1:
                                if((int)(left - move/3-bottomSpeed)<(int) (maxScreen / 3)){
                                    linearLayout.layout((int) (maxScreen / 3), top, (int) maxScreen + (int) (2*maxScreen / 3), bottom);
                                }else{
                                    linearLayout.layout((int)(left - move/3-bottomSpeed), top, (int)(right - move/3-bottomSpeed), bottom);
                                }
                                break;
                        }

                    }
                } else {
                    if (position == 0) {
                        linearLayout.layout(0, top, (int) maxScreen, bottom);
                        isIdle = true;
                    } else if (position == 1) {
                        linearLayout.layout((int) (maxScreen / 3), top, (int) maxScreen + (int) (maxScreen / 3), bottom);
                        isIdle = true;
                    } else if (position == 2) {
                        linearLayout.layout((int) (2 * maxScreen / 3), top, (int) maxScreen + (int) (2 * maxScreen / 3), bottom);
                        isIdle = true;
                    }
                }
                oldPosition = position;
                oldPixel = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
