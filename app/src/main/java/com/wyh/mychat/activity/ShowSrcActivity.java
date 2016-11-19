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
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity implements LoadManager.FileUpdate {

    @Bind(R.id.pb_load)
    ProgressBar pbLoad;

    public static Fragment getMcurrentFragment() {
        return mcurrentFragment;
    }

    public static void setMcurrentFragment(Fragment mcurrentFragment) {
        ShowSrcActivity.mcurrentFragment = mcurrentFragment;
    }

    private static Fragment mcurrentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pbLoad.setVisibility(View.VISIBLE);
        File sdFile = Environment.getExternalStorageDirectory();
        File selfFile = Environment.getRootDirectory();
        LoadManager.getPicLoadManager(this).setFileUpdate(this);
        LoadManager.getPicLoadManager(this).getSrcList(sdFile, selfFile);
    }

    public void showFolderFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show, new FolderFragment()).commitAllowingStateLoss();
    }

    public void showPicFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show, new PicFragment()).commitAllowingStateLoss();
    }

    private boolean enter = true;

    @Override
    public void update() {
        if (enter) {
            enter = false;
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    ((FolderFragment) getSupportFragmentManager().findFragmentById(R.id.fm_show)).refresh();
                }
            }, 500, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public void end() {
        pbLoad.post(new Runnable() {
            @Override
            public void run() {
                pbLoad.setVisibility(View.GONE);
            }
        });
    }
}
