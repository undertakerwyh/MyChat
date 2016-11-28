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
import android.widget.Toast;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.LoginActivity;
import com.wyh.mychat.biz.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/19.
 */

public class RegisterFragment extends Fragment implements UserManager.RegisterListener {
    @Bind(R.id.et_user_text)
    EditText etUserText;
    @Bind(R.id.et_password_text)
    EditText etPasswordText;
    @Bind(R.id.et_repassword_text)
    EditText etRepasswordText;
    @Bind(R.id.btn_register)
    Button btnRegister;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserManager.getUserManager(getContext()).setRegisterListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        String name = etUserText.getText().toString().trim();
        String password = etPasswordText.getText().toString().trim();
        String rePassword = etRepasswordText.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            showToast("用户名不能为空");
        }else if(TextUtils.isEmpty(password)){
            showToast("密码不能为空");
        }else if(TextUtils.isEmpty(rePassword)){
            showToast("确认密码不能为空");
        }else {
            UserManager.getUserManager(getContext()).register(name,password,rePassword);
        }
    }

    private void showToast(final String content){
        view.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void Error(String content) {
        showToast(content);
    }

    @Override
    public void success() {
        showToast("注册成功");
        ((LoginActivity)getActivity()).showLogin();
    }
}
