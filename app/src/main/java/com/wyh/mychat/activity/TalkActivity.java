package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.wyh.mychat.R;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.BitmapManager;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.biz.SendManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.BitmapUtil;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.util.SystemUtils;
import com.wyh.mychat.util.TimeNoteUtil;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.PopBar;
import com.wyh.mychat.view.xlistview.XListView;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TalkActivity extends BaseActivity implements View.OnClickListener, DBManager.UpdateListener,
        NewMessageBroadcastReceiver.NewMessageTalk, ShowPicActivity.PicSendListener, BitmapManager.NewMessageTalk, XListView.HideEvent {

    @Bind(R.id.action_bar)
    ActionBar actionBar;
    @Bind(R.id.lv_talk_message)
    XListView lvTalkMessage;
    @Bind(R.id.iv_other_bar_icon)
    ImageView ivOtherBarIcon;
    @Bind(R.id.ed_input_message)
    EditText edInputMessage;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.talk_pic_icon)
    ImageView talkPicIcon;
    @Bind(R.id.talk_other)
    ImageView talkOther;
    @Bind(R.id.ll_other_bar)
    LinearLayout llOtherBar;
    @Bind(R.id.activity_talk)
    LinearLayout activityTalk;
    private PopBar popBar;
    private UniversalAdapter<Message> talkAdapter;
    private String bitmapName;
    private String bitmapPath;

    public static String getFriendName() {
        return friendName;
    }

    private static String friendName;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (((List<Message>) msg.obj).size() > 0) {
                        int startIndex = talkAdapter.getDataList().size();
                        talkAdapter.addDataToAdapterHead((List<Message>) msg.obj);
                        int endIndex = talkAdapter.getDataList().size();
                        if (endIndex == startIndex) {
                            lvTalkMessage.setSelection(0);
                        } else {
                            lvTalkMessage.setSelection(endIndex - startIndex + 1);
                        }
                    }
                    lvTalkMessage.stopRefresh();
                    break;
                case 2:
                    talkAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        /**初始化ActionBar*/
        friendName = getIntent().getStringExtra("name");
        initActionBar(friendName, R.drawable.back, -1, this);
        ButterKnife.bind(this);
        /**初始化适配器*/
        initAdapter();
        lvTalkMessage.setAdapter(talkAdapter);
        lvTalkMessage.setPullRefreshEnable(true);

        /**设置xListView*/
        setXListView();
        DBManager.getDbManager(getApplicationContext()).setUpdateListener(this);
        /**初始化数据*/
        DBManager.getDbManager(getApplicationContext()).loadMessageDESC(friendName, true);
        NewMessageBroadcastReceiver.setNewMessageTalk(this);
        UserManager.getUserManager(this).setTalkSend(true);
        ShowPicActivity.setPicSendListener(this);
        BitmapManager.getBitmapManager(this).setNewMessageTalk(this);
        initPopBar();
        if (activityTalk instanceof ViewGroup) {
            for (int i = 0; i < activityTalk.getChildCount(); i++) {
                View innerView = activityTalk.getChildAt(i);
                setupUI(innerView);
            }
        }
        lvTalkMessage.setHideEvent(this);
    }

    private void initPopBar() {
        popBar = new PopBar(this, R.layout.pop_show_pic, ViewPager.LayoutParams.MATCH_PARENT);
        popBar.setonClickListener(R.id.iv_pic_show, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popBar.isShowing()) {
                    popBar.dismiss();
                }
            }
        });
        popBar.setonClickListener(R.id.tv_pic_save, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapManager.getBitmapManager(getApplicationContext()).saveBitmapFromSD(bitmapPath, bitmapName);
                Toast.makeText(TalkActivity.this, "已保存", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    SystemUtils.getInstance(getApplicationContext()).hideSoftKeyboard(TalkActivity.this);
                    return false;
                }
            });
        }
    }

    private void setXListView() {
        lvTalkMessage.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                /**更新数据*/
                DBManager.getDbManager(getApplicationContext()).loadMessageDESC(friendName, false);
                TimeNoteUtil.getTimeNoteUtil().setRefresh(false);
            }
        });
    }

    private void initAdapter() {
        talkAdapter = new UniversalAdapter<Message>(getApplicationContext(), R.layout.layout_message_item) {
            @Override
            public void assignment(ViewHolder viewHolder, final int positon) {
                final Message message = talkAdapter.getDataList().get(positon);
                viewHolder.setChatVisible(R.id.ll_chat_left, R.id.ll_chat_right, R.id.tv_chat_left,
                        R.id.tv_chat_right, R.id.iv_pic_left, R.id.iv_pic_right
                        , BitmapManager.getBitmapManager(getApplicationContext()).loadBitmapFromCache(message.getBitmapPath(), message.getType())
                        , R.id.tv_time_text, message.getContent(), message.getType())
                        .setSendErrorListener(message.getErrorType())
                        .setViewOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bitmapName = BitmapManager.getBitmapManager(getApplicationContext()).getBitmapName(message.getBitmapPath());
                                bitmapPath = message.getBitmapPath();
                                ImageView imageView = (ImageView) popBar.getView(R.id.iv_pic_show);
                                imageView.setImageBitmap(BitmapUtil.getBigBitmap(message.getBitmapPath()));
                                popBar.showAtLocation(v, Gravity.CENTER, 0, 0);
                            }
                        }, R.id.iv_pic_right, R.id.iv_pic_left);
            }
        };
    }

    @OnClick({R.id.btn_send, R.id.iv_actionbar_left, R.id.iv_other_bar_icon, R.id.ed_input_message, R.id.talk_pic_icon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_actionbar_left:
                startActivity(HomeActivity.class);
                finish();
                break;
            case R.id.btn_send:
                final String content = edInputMessage.getText().toString();
                lvTalkMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        mySendMessage(content, CommonUtil.TYPE_RIGHT);
                    }
                });
                edInputMessage.setText("");
                break;
            case R.id.iv_other_bar_icon:
                if (llOtherBar.isShown()) {
                    llOtherBar.setVisibility(View.GONE);
                } else {
                    llOtherBar.setVisibility(View.VISIBLE);
                }
                SystemUtils.getInstance(this).hideSoftKeyboard(this);
                break;
            case R.id.ed_input_message:
                llOtherBar.setVisibility(View.GONE);
                break;
            case R.id.talk_pic_icon:
                startActivity(ShowSrcActivity.class);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getDbManager(getApplicationContext()).DBClose();
        DBManager.getDbManager(getApplicationContext()).setFirstLoad(false);
        UserManager.getUserManager(this).setTalkSend(false);
        TimeNoteUtil.getTimeNoteUtil().cleanTime();
        TimeNoteUtil.getTimeNoteUtil().setRefresh(true);
        friendName = null;
    }

    public static void setMySendUpdate(MySendUpdate mySendUpdate) {
        TalkActivity.mySendUpdate = mySendUpdate;
    }

    private static MySendUpdate mySendUpdate;

    private void mySendMessage(String content, int type) {
        if (!TextUtils.isEmpty(content)) {
            Message message = new Message(friendName, CommonUtil.getTimeLong(), content, type);
            DBManager.getDbManager(getApplicationContext()).saveMessage(message);
            String time = TimeNoteUtil.getTimeNoteUtil().sendStart(message.getTime());
            if (time != null) {
                Message timeMsg = new Message(friendName, 0, CommonUtil.getTimeSelect(message.getTime()), CommonUtil.TYPE_TIME);
                DBManager.getDbManager(this).setTime(message.getTime());
                talkAdapter.addDataUpdate(timeMsg);
            }
            talkAdapter.addDataUpdate(message);
            lvTalkMessage.setSelection(lvTalkMessage.getCount() - 1);
            mySendUpdate.SendUpdate(message);
            if (type == CommonUtil.TYPE_RIGHT) {
                SendManager.getSendMessage(this).sendTextMessage(friendName, content, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        talkAdapter.getDataList().get(talkAdapter.getCount() - 1).setErrorType(CommonUtil.SEND_SUCCESS);
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onError(int i, String s) {
                        talkAdapter.getDataList().get(talkAdapter.getCount() - 1).setErrorType(CommonUtil.SEND_ERROR);
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onProgress(int i, String s) {
                        talkAdapter.getDataList().get(talkAdapter.getCount() - 1).setErrorType(CommonUtil.SEND_LOAD);
                        handler.sendEmptyMessage(2);
                    }
                });
                DBManager.getDbManager(getApplicationContext()).createSentTextMsg(friendName
                        , UserManager.getUserManager(getApplicationContext()).loadUserName()
                        , message.getContent(), message.getTime());
            }

        }
    }

    private void mySendPic(Message message, int type) {
        String time = TimeNoteUtil.getTimeNoteUtil().sendStart(message.getTime());
        if (time != null) {
            Message timeMsg = new Message(friendName, 0, CommonUtil.getTimeSelect(message.getTime()), CommonUtil.TYPE_TIME);
            DBManager.getDbManager(this).setTime(message.getTime());
            talkAdapter.addDataUpdate(timeMsg);
        }
        talkAdapter.addDataUpdate(message);
        lvTalkMessage.setSelection(lvTalkMessage.getCount() - 1);
        mySendUpdate.SendUpdate(message);
    }

    @Override
    public void sendPic(File picFile, boolean isOriginal) {
        SendManager.getSendMessage(this).sendPicMessage(friendName, picFile, isOriginal, new EMCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
        Message message = new Message(friendName, CommonUtil.getTimeLong(), CommonUtil.TYPE_PICRIGHT, picFile.getAbsolutePath());
        DBManager.getDbManager(this).createReceivedPicMsg(friendName, UserManager.getUserManager(this).loadUserName(), picFile, message.getTime());
        mySendPic(message, CommonUtil.TYPE_PICRIGHT);
    }

    @Override
    public void hide() {
        lvTalkMessage.post(new Runnable() {
            @Override
            public void run() {
                llOtherBar.setVisibility(View.GONE);
            }
        });
        SystemUtils.getInstance(this).hideSoftKeyboard(this);
    }

    public interface MySendUpdate {
        void SendUpdate(Message message);
    }

    @Override
    public void complete(final List<Message> list) {
        android.os.Message message = new android.os.Message();
        message.what = 1;
        message.obj = list;
        handler.sendMessage(message);
    }

    @Override
    public void updateTalk(EMMessage mMMessage) {
        String from = mMMessage.getFrom();
        if (from.equals(friendName)) {
            String msgBody = mMMessage.getBody().toString();
            String[] msgType = msgBody.split(":");
            String content = null;
            if (msgType[0].equals("txt")) {
                content = msgType[1].substring(msgType[1].indexOf("\"") + 1, msgType[1].lastIndexOf("\""));
            }
            final String finalContent = content;
            lvTalkMessage.post(new Runnable() {
                @Override
                public void run() {
                    mySendMessage(finalContent, CommonUtil.TYPE_LEFT);
                }
            });
        }
    }

    @Override
    public void updateTalkPic(EMMessage message) {
        ImageMessageBody imageMessageBody = (ImageMessageBody) message.getBody();
        String bitmapUrl = imageMessageBody.getThumbnailUrl();
        String from = message.getFrom();
        String name = imageMessageBody.getFileName();
        long time = message.getMsgTime();
        BitmapManager.getBitmapManager(this).getBitmapUrl(bitmapUrl, name, from, time,true);
    }

    @Override
    public void returnTalkPic(String name, String bitmapPath) {
        final Message message = new Message(name, CommonUtil.getTimeLong(), CommonUtil.TYPE_PICLEFT, bitmapPath);
        lvTalkMessage.post(new Runnable() {
            @Override
            public void run() {
                mySendPic(message, CommonUtil.TYPE_PICLEFT);
            }
        });
    }

    /**
     * 重写返回键的监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popBar.isShowing()) {
            popBar.dismiss();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}