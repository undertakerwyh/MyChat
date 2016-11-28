package com.wyh.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.HomeActivity;
import com.wyh.mychat.activity.LoginActivity;
import com.wyh.mychat.activity.ShowSrcActivity;
import com.wyh.mychat.biz.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ConfigFragment extends Fragment implements UserManager.ExitListener{
    @Bind(R.id.config_pic)
    TextView configPic;
    @Bind(R.id.config_video)
    TextView configVideo;
    @Bind(R.id.config_log_off)
    TextView configLogOff;
    @Bind(R.id.config_exit)
    TextView configExit;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserManager.getUserManager(getContext()).setExitListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.config_pic, R.id.config_video, R.id.config_log_off, R.id.config_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.config_pic:
                ((HomeActivity)getActivity()).startActivity(ShowSrcActivity.class);
                break;
            case R.id.config_video:
                break;
            case R.id.config_log_off:
                UserManager.getUserManager(getContext()).Exit();
                break;
            case R.id.config_exit:
                ((HomeActivity)getActivity()).finishAll();
                break;
        }
    }

    @Override
    public void Error(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
        startActivity(new Intent(getActivity(),LoginActivity.class));
        ((HomeActivity)getActivity()).finish();
    }
}
