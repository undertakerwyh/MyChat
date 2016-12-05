package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.NetUtils;
import com.wyh.mychat.R;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.util.SystemUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/19.
 */

public class LoginFragment extends Fragment implements UserManager.LoginListener {
    @Bind(R.id.et_password_text)
    EditText etPasswordText;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.et_user_text)
    EditText etUserText;
    @Bind(R.id.tv_login_register)
    TextView tvLoginRegister;
    private View view;
    private MoveToRegister moveToRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        moveToRegister = (MoveToRegister) this.getActivity();
//        initLogin();
        return view;
    }

    private void initLogin() {
        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        private ExecutorService executors;

        private MyConnectionListener() {
            executors = Executors.newCachedThreadPool();
        }

        @Override
        public void onConnected() {
            if (UserManager.getUserManager(getContext()).loadAuto()) {
                moveToRegister.moveToHome();
            }
        }

        @Override
        public void onDisconnected(final int error) {
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if (error == EMError.USER_REMOVED) {
                                // 显示帐号已经被移除
                                Toast.makeText(getContext(), "显示帐号已经被移除", Toast.LENGTH_SHORT).show();
                            } else if (error == EMError.CONNECTION_CONFLICT) {
                                Toast.makeText(getContext(), "显示帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                // 显示帐号在其他设备登录
                            } else if (!SystemUtils.getInstance(getContext()).isNetConn()) {
                                //当前网络不可用，请检查网络设置
                                Toast.makeText(getContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();
                            } else if(!NetUtils.hasNetwork(getContext())){
                                Toast.makeText(getContext(), "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                                //连接不到聊天服务器
                            }
                        }
                    }
                );
            }
        }
        );
    }
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserManager.getUserManager(getContext()).setLoginListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_login, R.id.tv_login_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String name = etUserText.getText().toString().trim();
                String password = etPasswordText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("用户名不能为空");
                } else if (TextUtils.isEmpty(password)) {
                    showToast("用户名不能为空");
                } else {
                    UserManager.getUserManager(getContext()).login(name, password);
                }
                break;
            case R.id.tv_login_register:
                moveToRegister.moveToRegister();
                break;
        }
    }

    @Override
    public void Error(String content) {
        showToast(content);
    }

    private void showToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
        view.post(new Runnable() {
            @Override
            public void run() {
                moveToRegister.moveToHome();
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
            }
        });
    }

public interface MoveToRegister {
    void moveToRegister();

    void moveToHome();
}
}
