package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.fragment.FolderFragment;
import com.wyh.mychat.fragment.PicFragment;

import java.io.File;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity implements LoadManager.FileUpdate{

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        File sdFile = Environment.getExternalStorageDirectory();
        File selfFile = Environment.getRootDirectory();
        LoadManager.getPicLoadManager(this).setFileUpdate(this);
        LoadManager.getPicLoadManager(this).getSrcList(sdFile,selfFile);
    }

    public void showFolderFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show,new FolderFragment()).commitAllowingStateLoss();
    }
    public void showPicFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fm_show,new PicFragment()).commitAllowingStateLoss();
    }

    @Override
    public void update() {

    }

    @Override
    public void end() {

    }
}
