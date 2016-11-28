package com.wyh.mychat.biz;

import android.content.Context;

/**
 * Created by Administrator on 2016/11/28.
 */

public class UserManager {
    private static UserManager userManager = null;
    private static Context contexts = null;
    public static UserManager getUserManager(Context context){
        contexts = context;
        if(userManager==null){
            synchronized (context){
                userManager = new UserManager();
            }
        }
        return userManager;
    }

    public void register(String name,String password,String repassword){

    }
}
