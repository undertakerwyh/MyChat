package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.entity.Picture;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowPicture extends BaseActivity implements LoadManager.FileUpdate {
    @Bind(R.id.lv_pic)
    ListView lvPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        LoadManager.getPicLoadManager(this).setFileUpdate(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        File sdFile = Environment.getExternalStorageDirectory();
        File selfFile = Environment.getRootDirectory();
        LoadManager.getPicLoadManager(this).getPicList(sdFile,selfFile);
    }

    @Override
    public void update(Picture picture) {
        Log.e("AAA",picture.getName());
    }

    @Override
    public void end() {

    }
}
