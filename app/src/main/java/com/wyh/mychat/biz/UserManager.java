package com.wyh.mychat.biz;

import android.content.Context;
import android.support.annotation.NonNull;

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

    public void setRegisterListener(RegisterListener registerListener) {
        this.registerListener = registerListener;
    }

    private RegisterListener registerListener;

    public static UserManager getUserManager(Context context) {
        contexts = context;
        if (userManager == null) {
            synchronized (context) {
                userManager = new UserManager();
            }
        }
        return userManager;
    }

    private UserManager() {
        executors = Executors.newCachedThreadPool();
    }

    public void register(@NonNull final String name, @NonNull final String password, @NonNull String repassword) {
        String nameLower = name.toLowerCase();
        if (!name.equals(nameLower)) {
            registerListener.Error("用户名不能有大写字母");
            return;
        } else if (!CommonUtil.verifyPassword(password)) {
            registerListener.Error("请输入由大小写字母或数字组合的密码");
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

    public interface RegisterListener {
        void Error(String content);

        void success();
    }
}
