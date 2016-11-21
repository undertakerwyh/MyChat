package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.ProgressBar;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.entity.Picture;
import com.wyh.mychat.fragment.FolderFragment;
import com.wyh.mychat.fragment.ResourceFragment;
import com.wyh.mychat.view.NoTouchViewPager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity implements LoadManager.FileUpdate ,LoadManager.ResourceUpdate{

    @Bind(R.id.pb_load)
    ProgressBar pbLoad;
    @Bind(R.id.vp_resource)
    NoTouchViewPager vpResource;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    private FolderFragment folderFragment;

    private ResourceFragment resourceFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        initViewpager();
    }

    private void initViewpager() {
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            Fragment[] fragment = {new FolderFragment(),new ResourceFragment()};

            @Override
            public Fragment getItem(int position) {
                return fragment[position];
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        vpResource.setAdapter(fragmentStatePagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pbLoad.setVisibility(View.VISIBLE);
        LoadManager.getPicLoadManager(this).isStop(false);
        File sdFile = Environment.getExternalStorageDirectory();
        LoadManager.getPicLoadManager(this).setFileUpdate(this);
        LoadManager.getPicLoadManager(this).getSrcList(sdFile);
    }

    public void showResource(String name){
        LoadManager.getPicLoadManager(this).isStop(false);
        LoadManager.getPicLoadManager(this).setResourceUpdate(this);
        vpResource.setCurrentItem(1);
        LoadManager.getPicLoadManager(this).getResource(new File(name));
    }

    @Override
    public void update(final String folder) {
        if (folderFragment == null) {
            folderFragment = (FolderFragment) fragmentStatePagerAdapter.getItem(0);
        }
        folderFragment.refresh(folder);
    }

    @Override
    public void update(Picture picture) {
        if(resourceFragment ==null){
            resourceFragment = (ResourceFragment) fragmentStatePagerAdapter.getItem(1);
        }
        resourceFragment.refresh(picture);
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
