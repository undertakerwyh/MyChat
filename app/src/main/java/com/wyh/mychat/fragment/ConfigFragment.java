package com.wyh.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.HomeActivity;
import com.wyh.mychat.activity.ShowSrcActivity;
import com.wyh.mychat.biz.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ConfigFragment extends Fragment{
    @Bind(R.id.config_pic)
    TextView configPic;
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.config_pic, R.id.config_log_off, R.id.config_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.config_pic:
                Intent intent = new Intent(getActivity(),ShowSrcActivity.class);
                intent.putExtra("FromClass",ConfigFragment.class.getSimpleName());
                getActivity().startActivity(intent);
                break;
            case R.id.config_log_off:
                UserManager.getUserManager(getContext()).Exit();
                UserManager.getUserManager(getContext()).saveLoginInfo(false,null);
                break;
            case R.id.config_exit:
                ((HomeActivity)getActivity()).finishAll();
                break;
        }
    }


}
