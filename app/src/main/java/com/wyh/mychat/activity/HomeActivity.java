package com.wyh.mychat.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabWidget;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.wyh.mychat.R;
import com.wyh.mychat.adapter.FragmentAdapter;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.fragment.ContactsFragment;
import com.wyh.mychat.fragment.MessageFragment;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.PageChangeAnimUtil;
import com.wyh.mychat.util.SystemUtils;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.ListViewBar;
import com.wyh.mychat.view.PopBar;
import com.wyh.mychat.view.TouchViewPager;
import com.wyh.mychat.view.ViewPagerScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面HomeActivity
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, UserManager.ExitListener, NewMessageBroadcastReceiver.NewMessagePop {


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
    private ListViewBar listViewBar;
    private PopupWindow friendPop;
    private PopBar newPop;
    private View view3;
    private View view1;
    private MessageFragment messageFragment;
    private NewMessageBroadcastReceiver msgReceiver;

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
        initBroadcastReceiver();
        UserManager.getUserManager(getApplicationContext()).setExitListener(this);
        NewMessageBroadcastReceiver.setNewMessagePop(this);
        newPop = new PopBar(this, R.layout.view_new, ViewPager.LayoutParams.WRAP_CONTENT);
        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.setting_friend));
        listViewBar = new ListViewBar(this, list, new ListViewBar.ListViewBarListener() {
            @Override
            public void onComplete(String name) {
                listViewBar.dismiss();
                ShowAddFriend();
            }
        });
        initPagerListener();
    }

    private void initPagerListener() {
        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    dismissPop();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initBroadcastReceiver() {
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
    }

    public Handler getHandler() {
        return handler;
    }

    private void initReceive() {
        EMChat.getInstance().setAppInited();
        EMContactManager.getInstance().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                contactListener.delete(username);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                try {
                    EMChatManager.getInstance().acceptInvitation(username);
                    contactListener.refresh();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onContactDeleted(List<String> usernameList) {
                //被删除时回调此方法
                for(String name:usernameList){
                    contactListener.delete(name);
                }
            }

            @Override
            public void onContactAdded(List<String> usernameList) {
                //增加了联系人时回调此方法
                if (usernameList.size() > 0) {
                    contactListener.added(usernameList);
                }
            }
        });
    }

    @Override
    public void updatePop() {
        if (vpHome.getCurrentItem() != 0) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    newPop.showAsDropDown(vpHome, (int) maxScreenWidth/6 , newPop.getHeight());
                }
            });
        }
    }

    public interface ContactListener {
        void refresh();
        void delete(String usernameList);
        void added(List<String> usernameList);
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
        view1 = getLayoutInflater().inflate(R.layout.layout_bottom_menu1, null);
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
        view3 = getLayoutInflater().inflate(R.layout.layout_bottom_menu3, null);

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
        messageFragment = new MessageFragment();
        fragmentAdapter.addToFragmentData(messageFragment);
        fragmentAdapter.addToFragmentData(new ContactsFragment());
        fragmentAdapter.addToFragmentData(new ConfigFragment());
        vpHome.setAdapter(fragmentAdapter);
        vpHome.setLongClickable(true);
    }

    public MessageFragment getMessageFragment() {
        return messageFragment;
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

    public void dismissPop() {
        if (newPop.isShowing()) {
            newPop.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PageChangeAnimUtil.getPageChangeAnimUtil(this).initPosition(vpHome.getCurrentItem());
    }

    private int height = 0;

    /**
     * 显示菜单popwindows
     */
    private void ShowPopwindow() {
        listViewBar.showAsDropDown(actionBar, (int) (maxScreenWidth - height - 4), 0);
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
        friendPop.showAtLocation(actionBar, Gravity.CENTER, 0, 0);
    }

    /**
     * 添加好友界面的点击事件
     *
     * @param view
     */
    private void FriendOnClickEvent(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_send_request);
        final EditText userName = (EditText) view.findViewById(R.id.et_friend_name);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (!UserManager.getUserManager(getApplicationContext()).isUserName(name)) {
                        if (!UserManager.getUserManager(getApplicationContext()).isFriendExist(name)) {
                            try {
                                EMContactManager.getInstance().addContact(name, null);//需异步处理
                                Toast.makeText(HomeActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "已是好友", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "不能对自己帐号发送请求", Toast.LENGTH_SHORT).show();
                    }
                    friendPop.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
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

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        private ExecutorService executors;
        private boolean isNetError = false;

        private MyConnectionListener() {
            executors = Executors.newCachedThreadPool();
        }

        @Override
        public void onConnected() {
            if (isNetError) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        isNetError = false;
                    }
                });
            }
        }

        @Override
        public void onDisconnected(final int error) {
            executors.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      getHandler().post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (error == EMError.USER_REMOVED) {
                                                                    // 显示帐号已经被移除
                                                                    Toast.makeText(getApplicationContext(), "显示帐号已经被移除", Toast.LENGTH_SHORT).show();
                                                                    UserManager.getUserManager(getApplicationContext()).Exit();
                                                                    UserManager.getUserManager(getApplicationContext()).saveLoginInfo(false, null);
                                                                } else if (error == EMError.CONNECTION_CONFLICT) {
                                                                    Toast.makeText(getApplicationContext(), "显示帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                                                    UserManager.getUserManager(getApplicationContext()).Exit();
                                                                    UserManager.getUserManager(getApplicationContext()).saveLoginInfo(false, null);
                                                                    // 显示帐号在其他设备登录
                                                                } else if (!SystemUtils.getInstance(getApplicationContext()).isNetConn()) {
                                                                    //当前网络不可用，请检查网络设置
                                                                    Toast.makeText(getApplicationContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();
                                                                    isNetError = true;
                                                                } else if (!NetUtils.hasNetwork(getApplicationContext())) {
                                                                    Toast.makeText(getApplicationContext(), "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                                                                    isNetError = true;
                                                                    //连接不到聊天服务器
                                                                }
                                                            }
                                                        }
                                      );
                                  }
                              }
            );
        }
    }

    @Override
    public void Error(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
        startActivity(LoginActivity.class);
        finish();
    }
}
