package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.fragment.FolderFragment;
import com.wyh.mychat.fragment.PicFragment;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity{

    @Bind(R.id.pb_load)
    ProgressBar pbLoad;

    public static Fragment getMcurrentFragment() {
        return mcurrentFragment;
    }

    public static void setMcurrentFragment(Fragment mcurrentFragment) {
        ShowSrcActivity.mcurrentFragment = mcurrentFragment;
    }

    private static Fragment mcurrentFragment;

    private boolean enter=true;
    private ScheduledExecutorService scheduledExecutorService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        pbLoad.setVisibility(View.VISIBLE);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

    }

    @Override
    protected void onStart() {
        super.onStart();
        File sdfile = Environment.getExternalStorageDirectory();
        File selfFile = Environment.getRootDirectory();
        LoadManager.getPicLoadManager(this).getSrcList(sdfile,selfFile);
    }

    public void showFolderFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show,new FolderFragment()).commitAllowingStateLoss();
    }
    public void showPicFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show,new PicFragment()).commitAllowingStateLoss();
    }

}
