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

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.wyh.mychat.R;
import com.wyh.mychat.activity.LoginActivity;
import com.wyh.mychat.biz.UserManager;

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
        return view;
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
    public void Error(final String content) {
        view.post(new Runnable() {
            @Override
            public void run() {
                showToast(content);
            }
        });
    }

    @Override
    public void showWaitBar() {
        ((LoginActivity) getActivity()).showWaitBar(getContext());
    }

    @Override
    public void stopWaitBar() {
        ((LoginActivity) getActivity()).dismissWaitBar();
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
