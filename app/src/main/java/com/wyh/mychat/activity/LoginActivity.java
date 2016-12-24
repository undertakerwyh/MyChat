package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.adapter.FragmentAdapter;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.fragment.LoginFragment;
import com.wyh.mychat.fragment.RegisterFragment;
import com.wyh.mychat.view.NoTouchViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 登陆界面Activity
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginFragment.MoveToRegister {
    /**
     * viewpager
     */
    @Bind(R.id.vp_login)
    NoTouchViewPager vpLogin;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    /**
     * fragment适配器
     */
    private FragmentAdapter adapter;
    private static final int ACTION_LOGIN = 1;
    private static final int ACTION_REGISTER = 2;
    /**
     * 判断当前页面是否是登陆页面,如果是就退出activity,否就回到登陆页面
     */
    private boolean isLogin = true;

    @Override
    protected void myHandlerMessage(Message message) {
        super.myHandlerMessage(message);
        switch (message.what) {
            /**登陆界面actionbar的变化*/
            case ACTION_LOGIN:
                vpLogin.setCurrentItem(0);
                String title = getResources().getString(R.string.title_login);
                toolbarTitle.setText(title);
                isLogin = true;
                break;
            /**注册界面actionbar的变化*/
            case ACTION_REGISTER:
                vpLogin.setCurrentItem(1);
                String title1 = getResources().getString(R.string.title_register);
                toolbarTitle.setText(title1);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                isLogin = false;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                handler.sendEmptyMessage(ACTION_LOGIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLogin() {
        handler.sendEmptyMessage(ACTION_LOGIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!UserManager.getUserManager(this).loadAuto()) {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            /**初始化ActionBar*/
            showLogin();
            /**初始化适配器*/
            initFragmentAdapter();
            /**适配viewpager*/
            vpLogin.setAdapter(adapter);
        } else {
            startActivity(HomeActivity.class);
            finish();
        }

    }

    /**
     * 初始化Fragment适配器
     */
    private void initFragmentAdapter() {
        FragmentManager manager = getSupportFragmentManager();
        adapter = new FragmentAdapter(manager);
        LoginFragment loginFragment = new LoginFragment();
        RegisterFragment registerFragment = new RegisterFragment();
        adapter.addToFragmentData(loginFragment);
        adapter.addToFragmentData(registerFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_actionbar_left:
                handler.sendEmptyMessage(ACTION_LOGIN);
                break;
            default:
                break;
        }
    }

    @Override
    public void moveToRegister() {
        handler.sendEmptyMessage(ACTION_REGISTER);
    }

    @Override
    public void moveToHome() {
        startActivity(HomeActivity.class, R.anim.right_in, R.anim.left_out);
        finish();
    }

    /**
     * 重写返回键的监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !isLogin) {
            handler.sendEmptyMessage(ACTION_LOGIN);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
