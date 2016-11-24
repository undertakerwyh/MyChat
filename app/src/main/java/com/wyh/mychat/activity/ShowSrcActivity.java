package com.wyh.mychat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

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

public class ShowSrcActivity extends BaseActivity implements LoadManager.ResourceUpdate, View.OnClickListener {

    @Bind(R.id.vp_resource)
    NoTouchViewPager vpResource;
    private FragmentStatePagerAdapter fragmentStatePagerAdapter;

    private static FolderFragment folderFragment;

    private static ResourceFragment resourceFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        /**初始化标题栏*/
        initActionBar(getString(R.string.my_picture), -1, -1, this);
        ButterKnife.bind(this);
        /**初始化Viewpager*/
        initViewpager();
    }

    /**
     * 设置标题栏的文字
     */
    public void setActionText(String content) {
        setActionBar(content);
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
        if(folderFragment==null){
            folderFragment = new FolderFragment();
        }
        if(resourceFragment ==null){
            resourceFragment = new ResourceFragment();
        }
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
    }

    /**
     * 显示加载动画
     */
    public void showProgress() {
        handler.sendEmptyMessage(1);
    }

    /**
     * 显示指定文件夹下的图片的fragment
     */
    public void showResource(String name) {
        getResourceFragment().getHandler().sendEmptyMessage(0);
        LoadManager.getPicLoadManager(this).isStop(false);
        LoadManager.getPicLoadManager(this).setResourceUpdate(this);
        vpResource.setCurrentItem(1);
        LoadManager.getPicLoadManager(this).getResource(new File(name));
    }



    private FolderFragment getFolderFragment() {
        if (folderFragment == null) {
            folderFragment = (FolderFragment) fragmentStatePagerAdapter.getItem(0);
        }
        return folderFragment;
    }

    private ResourceFragment getResourceFragment() {
        if (resourceFragment == null) {
            resourceFragment = (ResourceFragment) fragmentStatePagerAdapter.getItem(1);
        }
        return resourceFragment;
    }

    /**
     * 搜索结束加载动画结束
     */
    @Override
    public void ResourceEnd() {
        getResourceFragment().getHandler().sendEmptyMessage(1);
    }

    /**
     * 更新搜索图片的结果
     */
    @Override
    public void resourceUpdate(Picture picture) {
        getResourceFragment().refresh(picture);
    }


    /**
     * 重写返回键的监听
     */
    @Override
    public void onBackPressed() {
        if (vpResource.getCurrentItem() == 1) {
            vpResource.setCurrentItem(0);
            resourceFragment = (ResourceFragment) fragmentStatePagerAdapter.getItem(1);
            resourceFragment.clearList();
            resourceFragment.getAdapter().getDataList().clear();
            setActionText(getString(R.string.my_picture));
            return;
        }
        LoadManager.getFolderSet().clear();
        finish();
    }

    @Override
    public void onClick(View view) {

    }
}
