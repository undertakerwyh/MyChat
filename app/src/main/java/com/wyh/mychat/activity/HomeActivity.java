package com.wyh.mychat.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabWidget;
import android.widget.Toast;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.wyh.mychat.R;
import com.wyh.mychat.adapter.FragmentAdapter;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.fragment.ContactsFragment;
import com.wyh.mychat.fragment.MessageFragment;
import com.wyh.mychat.util.PageChangeAnimUtil;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.PopBar;
import com.wyh.mychat.view.TouchViewPager;
import com.wyh.mychat.view.ViewPagerScroller;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面HomeActivity
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.action_bar)
    ActionBar actionBar;
    @Bind(R.id.vp_Home)
    TouchViewPager vpHome;
    @Bind(R.id.ll_bottom_bar_bg)
    LinearLayout llBottomBarBg;
    @Bind(R.id.tabWidget)
    TabWidget tabWidget;
    private FragmentAdapter fragmentAdapter;
    /**
     * 保存屏幕按下移动的位置信息
     */
    private float maxScreenWidth;
    private PopupWindow pop;
    private PopupWindow friendPop;
    private PopBar newPop;

    public void setContactListener(ContactListener contactListener) {
        this.contactListener = contactListener;
    }

    private ContactListener contactListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        /**初始化actionbar*/
        String title = UserManager.getUserManager(this).loadUserName();
        initActionBar(title, -1, R.drawable.function, this);
        /**初始化viewpager*/
        initViewPager();
        /**初始化底部菜单*/
        initTabHost();
        /**viewpager与滚动条的交互*/
        initHomePageChange();
        initViewPagerScroll();
        initReceive();
        newPop = new PopBar(this,R.layout.view_new);
    }

    private void initReceive() {
        EMContactManager.getInstance().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                contactListener.refresh();
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
            }

            @Override
            public void onContactDeleted(List<String> usernameList) {
                //被删除时回调此方法
                contactListener.refresh();
            }

            @Override
            public void onContactAdded(List<String> usernameList) {
                //增加了联系人时回调此方法

            }
        });
        EMChat.getInstance().setAppInited();
    }

    public interface ContactListener{
        void refresh();
    }


    /**
     * 获取手机屏幕宽
     */
    private void initHomePageChange() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        maxScreenWidth = metrics.widthPixels;
        PageChangeAnimUtil.getPageChangeAnimUtil(getApplicationContext()).pageChangeAnim(vpHome, llBottomBarBg, maxScreenWidth);
    }

    /**
     * 底部菜单栏的初始化
     */
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

    /**
     * 初始化viewpager的消息,联系人,功能布局
     */
    private void initViewPager() {
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        MessageFragment messageFragment = new MessageFragment();
        fragmentAdapter.addToFragmentData(messageFragment);
        fragmentAdapter.addToFragmentData(new ContactsFragment());
        fragmentAdapter.addToFragmentData(new ConfigFragment());
        vpHome.setAdapter(fragmentAdapter);
        vpHome.setLongClickable(true);
    }

    /**
     * 设置viewpager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(vpHome.getContext());
            mScroller.set(vpHome, scroller);
        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PageChangeAnimUtil.getPageChangeAnimUtil(this).initPosition(vpHome.getCurrentItem());
    }

    /**
     * 显示菜单popwindows
     */
    private void ShowPopwindow() {
        View view = getLayoutInflater().inflate(R.layout.layout_config, null);
        popOnClickEvent(view);
        pop = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.transparent));
        pop.setOutsideTouchable(true);
        pop.showAsDropDown(actionBar,(int) (maxScreenWidth-190), 2);
    }

    /**
     * popWindow菜单点击事件
     * @param view
     */
    private void popOnClickEvent(View view){
        LinearLayout friend = (LinearLayout) view.findViewById(R.id.ll_add_friend);
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                ShowAddFriend();
            }
        });
    }

    /**
     * 添加好友的pop显示
     */
    private void ShowAddFriend() {
        View view = getLayoutInflater().inflate(R.layout.pop_search_friend, null);
        friendPop = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        FriendOnClickEvent(view);
        friendPop.setFocusable(true);
        friendPop.setOutsideTouchable(true);
        friendPop.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.color.transparent));
        friendPop.showAtLocation(actionBar, Gravity.CENTER,0,0);
    }

    /**
     * 添加好友界面的点击事件
     * @param view
     */
    private void FriendOnClickEvent(View view){
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_send_request);
        final EditText userName = (EditText) view.findViewById(R.id.et_friend_name);
        final EditText content = (EditText) view.findViewById(R.id.et_friend_content);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String request = content.getText().toString();
                try {
                    EMContactManager.getInstance().addContact(name,request);//需异步处理
                    Toast.makeText(HomeActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        });
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
            default:
                break;
        }
    }

}
