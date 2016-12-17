package com.wyh.mychat.biz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/17.
 */

public class ConfigManager {
    private static ConfigManager configManager;
    private static Context contexts;
    public static ConfigManager getConfigManager(Context context){
        contexts = context;
        if(configManager==null){
            synchronized (context){
                configManager = new ConfigManager();
            }
        }
        return configManager;
    }
    private ConfigManager(){
        sharedPreferences = contexts.getSharedPreferences("config",Context.MODE_PRIVATE);
    }

    private SharedPreferences sharedPreferences;
    public void saveNewPopConfig(boolean isShowing){
        sharedPreferences.edit().putBoolean("NewPop_isShowing",isShowing).commit();
    }
    public boolean loadNewPopConfig(){
        return sharedPreferences.getBoolean("NewPop_isShowing",false);
    }
    public void saveBottomBarNum(int num){
        Log.e("AAA","num="+num);
        sharedPreferences.edit().putInt("BottomBarNum",num).commit();
    }
    public int loadBottomBarNum(){
        return sharedPreferences.getInt("BottomBarNum",0);
    }
}
