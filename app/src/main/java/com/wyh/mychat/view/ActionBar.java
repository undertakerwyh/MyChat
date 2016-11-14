package com.wyh.mychat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyh.mychat.R;

/**
 * Created by Administrator on 2016/10/19.
 */

public class ActionBar extends LinearLayout {
    private ImageView leftView;
    private ImageView rightView;
    private TextView textView;
    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_actionbar,this);
        leftView = (ImageView) findViewById(R.id.iv_actionbar_left);
        rightView = (ImageView) findViewById(R.id.iv_actionbar_right);
        textView = (TextView) findViewById(R.id.tv_actionbar_text);
    }
    public void setText(String content){
        textView.setText(content);
    }
    /**
     *
     * @param title 标题栏
     * @param leftRes 左边视图 -1为无图
     * @param rightRes 右边视图 -1为无图
     * @param onClickListener 监听事件
     */
    public void initActionBar(String title,int leftRes,int rightRes,OnClickListener onClickListener){
        textView.setText(title);
        if(leftRes==-1){
            leftView.setVisibility(INVISIBLE);
        }else{
            leftView.setVisibility(VISIBLE);
            leftView.setImageResource(leftRes);
            leftView.setOnClickListener(onClickListener);
        }
        if(rightRes==-1){
            rightView.setVisibility(INVISIBLE);
        }else{
            rightView.setVisibility(VISIBLE);
            rightView.setImageResource(rightRes);
            rightView.setOnClickListener(onClickListener);
        }
    }
}
