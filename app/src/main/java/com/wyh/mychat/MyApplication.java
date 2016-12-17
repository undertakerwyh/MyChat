package com.wyh.mychat;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

/**
 * Created by Administrator on 2016/11/26.
 */

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        EMChat.getInstance().init(this);

        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，避免消耗不必要的资源

        EMChatManager.getInstance().getChatOptions().setUseRoster(true);
        EMChatManager.getInstance().getChatOptions().setShowNotificationInBackgroud(false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private static MyApplication myApplication;
    public static MyApplication getMyApplication(){
        return myApplication;
    }
}
