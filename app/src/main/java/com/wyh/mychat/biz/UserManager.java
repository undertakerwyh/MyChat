package com.wyh.mychat.biz;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.wyh.mychat.util.CommonUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/28.
 */

public class UserManager {
    private static UserManager userManager = null;
    private static Context contexts = null;
    private ExecutorService executors;
    private RegisterListener registerListener;
    private LoginListener loginListener;
    private  SharedPreferences loginSP =null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public void setExitListener(ExitListener exitListener) {
        this.exitListener = exitListener;
    }

    private ExitListener exitListener;

    public void setRegisterListener(RegisterListener registerListener) {
        this.registerListener = registerListener;
    }


    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }


    public static UserManager getUserManager(Context context) {
        contexts = context;
        if (userManager == null) {
            synchronized (context) {
                userManager = new UserManager();
            }
        }
        return userManager;
    }

    public void saveLoginInfo(boolean auto){
        if(loginSP==null) {
            loginSP = contexts.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        }
        loginSP.edit().putBoolean("autoLogin",auto).commit();
    }
    public boolean LoadLoginInfo(){
        if(loginSP==null) {
            loginSP = contexts.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        }
        return loginSP.getBoolean("autoLogin",false);
    }

    private UserManager() {
        executors = Executors.newCachedThreadPool();
    }

    /**
     * 注册用户
     *
     * @param name       用户名(小写字母)
     * @param password   密码(大小写或数字6-16位)
     * @param repassword 确认密码
     */
    public void register(@NonNull final String name, @NonNull final String password, @NonNull String repassword) {
        String nameLower = name.toLowerCase();
        if (!name.equals(nameLower)) {
            registerListener.Error("用户名不能有大写字母");
            return;
        } else if (!CommonUtil.verifyPassword(password)) {
            registerListener.Error("请输入6到16位由大小写字母或数字组合的密码");
            return;
        } else if (!password.equals(repassword)) {
            registerListener.Error("确认密码不正确");
            return;
        } else {
            executors.execute(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMChatManager.getInstance().createAccountOnServer(name, password);
                        registerListener.success();
                    } catch (final EaseMobException e) {
                        //注册失败
                        int errorCode = e.getErrorCode();
                        if (errorCode == EMError.NONETWORK_ERROR) {
                            registerListener.Error("网络异常，请检查网络！");
                        } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                            registerListener.Error("用户已存在！");
                        } else if (errorCode == EMError.UNAUTHORIZED) {
                            registerListener.Error("注册失败，无权限！");
                        } else {
                            registerListener.Error("注册失败: " + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    public void login(final String userName, String password) {
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                loginListener.success();
                UserManager.this.userName = userName;
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                loginListener.Error("登录聊天服务器失败！");
            }
        });
    }

    public void Exit(){
        //此方法为异步方法
        EMChatManager.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                exitListener.success();
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                exitListener.Error("网络异常,退出失败");
            }
        });
    }

    public interface ExitListener{
        void Error(String content);
        void success();
    }

    public interface LoginListener{
        void Error(String content);

        void success();
    }

    public interface RegisterListener {
        void Error(String content);

        void success();
    }

}
