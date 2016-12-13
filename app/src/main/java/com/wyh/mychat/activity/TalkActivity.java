package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.wyh.mychat.R;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.biz.SendManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.util.SystemUtils;
import com.wyh.mychat.util.TimeNoteUtil;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.xlistview.XListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TalkActivity extends BaseActivity implements View.OnClickListener, DBManager.UpdateListener, NewMessageBroadcastReceiver.NewMessageTalk {

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
    private UniversalAdapter<Message> adapter;
    private String name;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (((List<Message>) msg.obj).size() > 0) {
                        int startIndex = adapter.getDataList().size();
                        adapter.addDataToAdapterHead((List<Message>) msg.obj);
                        int endIndex = adapter.getDataList().size();
                        if (endIndex == startIndex) {
                            lvTalkMessage.setSelection(0);
                        } else {
                            lvTalkMessage.setSelection(endIndex - startIndex + 1);
                        }
                    }
                    lvTalkMessage.stopRefresh();
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        /**初始化ActionBar*/
        name = getIntent().getStringExtra("name");
        initActionBar(name, R.drawable.back, -1, this);
        ButterKnife.bind(this);
        /**初始化适配器*/
        initAdapter();
        lvTalkMessage.setAdapter(adapter);
        lvTalkMessage.setPullRefreshEnable(true);

        /**设置xListView*/
        setXListView();
        DBManager.getDbManager(getApplicationContext()).setUpdateListener(this);
        /**初始化数据*/
        DBManager.getDbManager(getApplicationContext()).loadMessageDESC(name, true);
        NewMessageBroadcastReceiver.setNewMessageTalk(this);
        UserManager.getUserManager(this).setTalkSend(true);
        edInputMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("AAA", "hasFocus:" + hasFocus);
            }
        });
        if (activityTalk instanceof ViewGroup) {
            for (int i = 0; i < activityTalk.getChildCount(); i++) {
                View innerView = activityTalk.getChildAt(i);
                setupUI(innerView);
            }
        }
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
                DBManager.getDbManager(getApplicationContext()).loadMessageDESC(name, false);
                TimeNoteUtil.getTimeNoteUtil().setRefresh(false);
            }
        });
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<Message>(getApplicationContext(), R.layout.layout_message_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                Message message = adapter.getDataList().get(positon);
                viewHolder.setChatVisible(R.id.ll_chat_left, R.id.ll_chat_right, R.id.tv_chat_left,
                        R.id.tv_chat_right, R.id.tv_time_text, message.getContent(), message.getType())
                        .setSendErrorListener(message.getErrorType());
            }
        };
    }

    @OnClick({R.id.btn_send, R.id.iv_actionbar_left, R.id.iv_other_bar_icon,R.id.ed_input_message,R.id.talk_pic_icon})
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
    }

    public static void setMySendUpdate(MySendUpdate mySendUpdate) {
        TalkActivity.mySendUpdate = mySendUpdate;
    }

    private static MySendUpdate mySendUpdate;

    private void mySendMessage(String content, int type) {
        if (!TextUtils.isEmpty(content)) {
            Message message = new Message(name, CommonUtil.getTimeLong(), content, type);
            DBManager.getDbManager(getApplicationContext()).saveMessage(message);
            String time = TimeNoteUtil.getTimeNoteUtil().sendStart(message.getTime());
            if (time != null) {
                Message timeMsg = new Message(null, 0, CommonUtil.getTimeSelect(message.getTime()), CommonUtil.TYPE_TIME);
                DBManager.getDbManager(this).setTime(message.getTime());
                adapter.addDataUpdate(timeMsg);
            }
            adapter.addDataUpdate(message);
            lvTalkMessage.setSelection(lvTalkMessage.getCount() - 1);
            mySendUpdate.SendUpdate(message);
            if (type == CommonUtil.TYPE_RIGHT) {
                SendManager.getSendMessage(this).sendTextMessage(name, content, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("AAA", "onSuccess");
                        adapter.getDataList().get(adapter.getCount() - 1).setErrorType(CommonUtil.SEND_SUCCESS);
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("AAA", "onError");
                        adapter.getDataList().get(adapter.getCount() - 1).setErrorType(CommonUtil.SEND_ERROR);
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onProgress(int i, String s) {
                        Log.e("AAA", "onProgress");
                        adapter.getDataList().get(adapter.getCount() - 1).setErrorType(CommonUtil.SEND_LOAD);
                        handler.sendEmptyMessage(2);
                    }
                });
                DBManager.getDbManager(getApplicationContext()).createSentTextMsg(name
                        , UserManager.getUserManager(getApplicationContext()).loadUserName()
                        , message.getContent(), message.getTime());
            }

        }
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