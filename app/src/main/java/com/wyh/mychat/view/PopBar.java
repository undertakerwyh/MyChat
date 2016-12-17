package com.wyh.mychat.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.wyh.mychat.R;

/**
 * Created by Administrator on 2016/12/1.
 */

public class PopBar extends PopupWindow {
    private Context context;
    private View parentView;
    private SparseArray views;
    public PopBar(Context context,int ResId,int layoutParams){
        this.context = context;
        views = new SparseArray();
        show(ResId,layoutParams);
    }

    public void show(int resId, int layoutParams) {
        parentView = LayoutInflater.from(context).inflate(resId,null);
        setContentView(parentView);
        //设置弹出窗体的高
        this.setWidth(layoutParams);
        this.setHeight(layoutParams);
        //设置弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));
        update();
    }
    public View getView(int resId){
        View view = (View) views.get(resId);
        if(view==null){
            view = parentView.findViewById(resId);
            views.put(resId,view);
        }
        return view;
    }
    public void setonClickListener(View.OnClickListener onClickListener){
        parentView.setOnClickListener(onClickListener);
    }
}
