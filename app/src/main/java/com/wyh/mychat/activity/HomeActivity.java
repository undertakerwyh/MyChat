package com.wyh.mychat.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabWidget;

import com.wyh.mychat.R;
import com.wyh.mychat.adapter.FragmentAdapter;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.fragment.ContactsFragment;
import com.wyh.mychat.fragment.MessageFragment;
import com.wyh.mychat.util.PageChangeAnimUtil;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.TouchViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面HomeActivity
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.vp_Home)
    TouchViewPager vpHome;
    @Bind(R.id.tabWidget)
    TabWidget tabWidget;
    @Bind(R.id.ll_bottom_bar_bg)
    LinearLayout llBottomBarBg;
    @Bind(R.id.action_bar)
    ActionBar actionBar;
    private FragmentAdapter fragmentAdapter;
    /**
     * 保存屏幕按下移动的位置信息
     */
    private float maxScreen;
    private PopupWindow pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        /**初始化actionbar*/
        String title = getResources().getString(R.string.app_name);
        initActionBar(title, -1, R.drawable.function, this);
        /**初始化viewpager*/
        initViewPager();
        /**初始化底部菜单*/
        initTabHost();
        /**viewpager与滚动条的交互*/
        initHomePageChange();
    }

    private void initHomePageChange() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        maxScreen = metrics.widthPixels;
        PageChangeAnimUtil.getPageChangeAnimUtil(getApplicationContext()).pageChangeAnim(vpHome, llBottomBarBg, maxScreen);
    }

    private void initTabHost() {
        View view1 = getLayoutInflater().inflate(R.layout.layout_bottom_menu1, null);
        tabWidget.addView(view1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(0);
            }
        });
        View view2 = getLayoutInflater().inflate(R.layout.layout_bottom_menu2, null);

        tabWidget.addView(view2);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(1);
            }
        });
        View view3 = getLayoutInflater().inflate(R.layout.layout_bottom_menu3, null);

        tabWidget.addView(view3);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(2);
            }
        });
        tabWidget.setCurrentTab(0);
    }

    private void initViewPager() {
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        MessageFragment messageFragment = new MessageFragment();
        fragmentAdapter.addToFragmentData(messageFragment);
        fragmentAdapter.addToFragmentData(new ContactsFragment());
        fragmentAdapter.addToFragmentData(new ConfigFragment());
        vpHome.setAdapter(fragmentAdapter);
        vpHome.setLongClickable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_actionbar_right:
                ShowPopwindow();
                break;
            case R.id.iv_message:
                vpHome.setCurrentItem(0);
                break;
            case R.id.iv_contacts:
                vpHome.setCurrentItem(1);
                break;
            case R.id.iv_config:
                vpHome.setCurrentItem(2);
                break;
        }
    }

    private void ShowPopwindow() {
        View view = getLayoutInflater().inflate(R.layout.layout_config, null);
        pop = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.color.transparent));
        pop.setOutsideTouchable(true);
        pop.showAsDropDown(actionBar, (int) (maxScreen-50),0);
    }
}
