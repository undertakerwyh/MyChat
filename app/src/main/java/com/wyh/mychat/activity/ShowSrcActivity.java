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
import com.wyh.mychat.fragment.FolderFragment;
import com.wyh.mychat.view.NoTouchViewPager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity implements LoadManager.FileUpdate {

    @Bind(R.id.pb_load)
    ProgressBar pbLoad;
    @Bind(R.id.vp_resource)
    NoTouchViewPager vpResource;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    public static Fragment getMcurrentFragment() {
        return mcurrentFragment;
    }

    public static void setMcurrentFragment(Fragment mcurrentFragment) {
        ShowSrcActivity.mcurrentFragment = mcurrentFragment;
    }

    private static Fragment mcurrentFragment;

    private FolderFragment folderFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        initViewpager();
    }

    private void initViewpager() {
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            Fragment[] fragment = {new FolderFragment()};
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return fragment[position];
                }
                return null;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
        vpResource.setAdapter(fragmentStatePagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pbLoad.setVisibility(View.VISIBLE);
        File sdFile = Environment.getExternalStorageDirectory();
        LoadManager.getPicLoadManager(this).setFileUpdate(this);
        LoadManager.getPicLoadManager(this).getSrcList(sdFile);
    }

    @Override
    public void update(final String folder) {
        if(folderFragment==null){
            folderFragment = (FolderFragment) fragmentStatePagerAdapter.getItem(0);
        }
        folderFragment.refresh(folder);
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
