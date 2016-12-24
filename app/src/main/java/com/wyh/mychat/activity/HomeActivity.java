package com.wyh.mychat.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TabWidget;
import android.widget.TextView;
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
import com.wyh.mychat.biz.BitmapManager;
import com.wyh.mychat.biz.ConfigManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.fragment.ContactsFragment;
import com.wyh.mychat.fragment.MessageFragment;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.SystemUtils;
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
public class HomeActivity extends BaseActivity implements UserManager.ExitListener,
        NewMessageBroadcastReceiver.NewMessagePop, TalkActivity.HomeNewListener {


    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.vp_Home)
    TouchViewPager vpHome;
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
    private View view2;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

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
        initToolbar(title);

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
        initNewPop();
        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.setting_friend));
        initPagerListener();
        BitmapManager.getBitmapManager(this).loadBitmapDownload();
        TalkActivity.setHomeNewListener(this);
    }

    private void initToolbar(String title) {
        toolbar.inflateMenu(R.menu.menu_home);
        toolbarTitle.setText(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        showAddFriend(HomeActivity.this);
                        break;
                }
                return false;
            }
        });
    }

    private void initNewPop() {
        newPop = new PopBar(this, R.layout.view_new, ViewPager.LayoutParams.WRAP_CONTENT);
        newPop.setonClickListener(R.id.ll_new_pop, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpHome.setCurrentItem(0);
            }
        });
    }

    private void initPagerListener() {
        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        dismissPop();
                        ConfigManager.getConfigManager(getApplicationContext()).saveNewPopConfig(false);
                        setVpHomeRed(1);
                        break;
                    case 1:
                        setVpHomeRed(2);
                        break;
                    case 2:
                        setVpHomeRed(3);
                        break;
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
                for (String name : usernameList) {
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
    public void updatePop(String msgFrom) {
        if (vpHome.getCurrentItem() != 0 && !msgFrom.equals(TalkActivity.getFriendName())) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    newPop.showAsDropDown(vpHome, (int) maxScreenWidth / 6, newPop.getHeight());
                }
            });
        }
    }

    @Override
    public void unRead(String name) {
        unReadClean(name);
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
    }

    /**
     * 底部菜单栏的初始化
     */
    private void initTabHost() {
        view1 = getLayoutInflater().inflate(R.layout.layout_bottom_menu1, null);
        tabWidget.addView(view1);
        imageView1 = (ImageView) findViewById(R.id.iv_message);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(0);
                setVpHomeRed(1);
            }
        });
        setVpHomeRed(1);
        view2 = getLayoutInflater().inflate(R.layout.layout_bottom_menu2, null);
        tabWidget.addView(view2);
        imageView2 = (ImageView) findViewById(R.id.iv_contacts);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(1);
                setVpHomeRed(2);
            }
        });
        view3 = getLayoutInflater().inflate(R.layout.layout_bottom_menu3, null);
        tabWidget.addView(view3);
        imageView3 = (ImageView) findViewById(R.id.iv_config);
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpHome.setCurrentItem(2);
                setVpHomeRed(3);
            }
        });
        tabWidget.setCurrentTab(0);
    }

    public void setVpHomeRed(int id){
        for(int i=1;i<4;i++){
            setVpHomeBlack(i);
        }
        switch (id){
            case 1:
                imageView1.setImageResource(R.drawable.message_red);
                break;
            case 2:
                imageView2.setImageResource(R.drawable.contacts_red);
                break;
            case 3:
                imageView3.setImageResource(R.drawable.config_red);
                break;
        }
    }

    public void setVpHomeBlack(int id){
        switch (id){
            case 1:
                if(imageView1!=null)
                    imageView1.setImageResource(R.drawable.message);
                break;
            case 2:
                if(imageView2!=null)
                imageView2.setImageResource(R.drawable.contacts);
                break;
            case 3:
                if(imageView3!=null)
                imageView3.setImageResource(R.drawable.config);
                break;
        }
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

    @Override
    protected void myHandlerMessage(Message message) {
        super.myHandlerMessage(message);
        switch (message.what) {
            case 1:
                newPop.showAsDropDown(vpHome, (int) maxScreenWidth / 6, newPop.getHeight());
                break;
        }
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

    public void unReadClean(String name) {
        dismissPop();
        getMessageFragment().cleanUnRead(name);
        getMessageFragment().cancelNew(name);
    }

    @Override
    public void success() {
        startActivity(LoginActivity.class);
        finish();
    }
}
