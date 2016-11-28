package com.wyh.mychat.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wyh.mychat.R;
import com.wyh.mychat.view.ActionBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class BaseActivity extends AppCompatActivity{
    private static List<AppCompatActivity> activities = new ArrayList<>();
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }


    /**
     * 清除所有的活跃activity
     */
    public void finishAll(){
        Iterator<AppCompatActivity> iterator = activities.iterator();
        while (iterator.hasNext()){
            iterator.next().finish();
        }
    }

    /**
     * 初始化ActionBar
     * @param title 标题栏
     * @param leftRes 左边视图
     * @param rightRes 右边视图
     * @param onClickListener 监听事件
     */
    protected void initActionBar(String title, int leftRes, int rightRes, View.OnClickListener onClickListener){
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.initActionBar(title,leftRes,rightRes,onClickListener);
    }

    protected void setActionBar(String title){
        actionBar.setText(title);
    }

    /**
     * Handler
     */
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myHandlerMessage(msg);
        }
    };
    protected void myHandlerMessage(Message message){

    }
    /**启动指定activity*/
    public void startActivity(Class<?>TargetActivity){
        Intent intent = new Intent(this,TargetActivity);
        startActivity(intent);
    }
    /**启动指定activity和切换动画*/
    public void startActivity(Class<?>TargetActivity,int animStart,int animEnd){
        Intent intent = new Intent(this,TargetActivity);
        startActivity(intent);
        overridePendingTransition(animStart,animEnd);
    }
}
