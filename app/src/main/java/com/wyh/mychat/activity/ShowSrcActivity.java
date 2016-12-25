package com.wyh.mychat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.BitmapManager;
import com.wyh.mychat.biz.ConfigManager;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.fragment.FolderFragment;
import com.wyh.mychat.fragment.ResourceFragment;
import com.wyh.mychat.view.NoTouchViewPager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ShowSrcActivity extends BaseActivity {

    @Bind(R.id.vp_resource)
    NoTouchViewPager vpResource;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    private static FolderFragment folderFragment;

    private static ResourceFragment resourceFragment;

    public String getFromClass() {
        return FromClass;
    }

    private String FromClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        /**初始化标题栏*/
        ButterKnife.bind(this);
        FromClass = getIntent().getStringExtra("FromClass");
        toolbarTitle.setText(FromClass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /**初始化Viewpager*/
        initViewpager();
        isCreatePicFile();
    }

    private void isCreatePicFile() {
        File file = new File(BitmapManager.myTalkPath);
        if (file.exists() && file.length() <= 0) {
            ConfigManager.getConfigManager(getApplicationContext()).savePicFile(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    /**
     * 设置标题栏的文字
     */
    public void setActionText(String content) {
        toolbarTitle.setText(content);
    }

    public static String getFolderName() {
        return folderName;
    }

    public static void setFolderName(String folderName) {
        ShowSrcActivity.folderName = folderName;
    }

    private static String folderName;

    /**
     * 初始化Viewpager
     */
    private void initViewpager() {
        if (folderFragment == null) folderFragment = new FolderFragment();
        if (resourceFragment == null) resourceFragment = new ResourceFragment();
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            Fragment[] fragment = {folderFragment, resourceFragment};

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
        vpResource.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showProgress();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpResource.setCurrentItem(0);
    }

    public void showResource(String folderName) {
        vpResource.setCurrentItem(1);
        getResourceFragment().showResource(folderName);
    }


    /**
     * 显示加载动画
     */
    public void showProgress() {
        handler.sendEmptyMessage(1);
    }

    private ResourceFragment getResourceFragment() {
        if (resourceFragment == null) {
            resourceFragment = (ResourceFragment) fragmentStatePagerAdapter.getItem(1);
        }
        return resourceFragment;
    }

    private FolderFragment getFolderFragment() {
        if (folderFragment == null) {
            folderFragment = (FolderFragment) fragmentStatePagerAdapter.getItem(0);
        }
        return folderFragment;
    }

    /**
     * 重写返回键的监听
     */
    @Override
    public void onBackPressed() {
        if (vpResource.getCurrentItem() == 1) {
            LoadManager.getPicLoadManager(this).stopSearch();
            vpResource.setCurrentItem(0);
            resourceFragment = (ResourceFragment) fragmentStatePagerAdapter.getItem(1);
            resourceFragment.setStopText(true);
            setActionText(getString(R.string.my_picture) + "(" + getFolderFragment().getAdapter().getDataList().size() + ")");
            return;
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
