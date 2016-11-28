package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wyh.mychat.R;
import com.wyh.mychat.biz.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/19.
 */

public class RegisterFragment extends Fragment {
    @Bind(R.id.et_user_text)
    EditText etUserText;
    @Bind(R.id.et_password_text)
    EditText etPasswordText;
    @Bind(R.id.et_repassword_text)
    EditText etRepasswordText;
    @Bind(R.id.btn_register)
    Button btnRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
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
        UserManager.getUserManager(getContext()).register(name,password,rePassword);
    }
}
