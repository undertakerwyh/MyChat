package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wyh.mychat.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/19.
 */

public class LoginFragment extends Fragment{
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
                moveToRegister.moveToHome();
                break;
            case R.id.tv_login_register:
                moveToRegister.moveToRegister();
                break;
        }
    }

    public interface MoveToRegister{
        void moveToRegister();
        void moveToHome();
    }
}
