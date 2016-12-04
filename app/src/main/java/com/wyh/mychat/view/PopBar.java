package com.wyh.mychat.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.wyh.mychat.R;

/**
 * Created by Administrator on 2016/12/1.
 */

public class PopBar extends PopupWindow {
    private Context context;
    private View parentView;
    public PopBar(Context context,int ResId){
        this.context = context;
        initView(ResId);
    }

    private void initView(int resId) {
        parentView = LayoutInflater.from(context).inflate(resId,null);
        setContentView(parentView);
        //设置弹出窗体的高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));
        update();
    }
    public void disShow(){
        this.dismiss();
    }

}
