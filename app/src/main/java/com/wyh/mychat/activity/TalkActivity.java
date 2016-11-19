package com.wyh.mychat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wyh.mychat.R;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.util.TimeNoteUtil;
import com.wyh.mychat.view.ActionBar;
import com.wyh.mychat.view.xlistview.XListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TalkActivity extends BaseActivity implements View.OnClickListener, DBManager.UpdateListener {

    @Bind(R.id.action_bar)
    ActionBar actionBar;
    @Bind(R.id.ed_input_message)
    EditText edInputMessage;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.lv_talk_message)
    XListView lvTalkMessage;
    private UniversalAdapter<Message> adapter;
    private String name;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int startIndex = adapter.getDataList().size();
                    adapter.addDataToAdapterHead((List<Message>) msg.obj);
                    int endIndex = adapter.getDataList().size();
                    if(endIndex==startIndex){
                        lvTalkMessage.setSelection(0);
                    }else{
                        lvTalkMessage.setSelection(endIndex-startIndex+1);
                    }
                    lvTalkMessage.stopRefresh();
                    break;
                default:
                    break;
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
        DBManager.getDbManager(getApplicationContext()).loadMessageDESC(name);
    }

    private void setXListView() {
        lvTalkMessage.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                /**更新数据*/
                DBManager.getDbManager(getApplicationContext()).loadMessageDESC(name);
            }
        });
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<Message>(getApplicationContext(), R.layout.layout_message_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                Message message = adapter.getDataList().get(positon);
                viewHolder.setChatVisible(R.id.ll_chat_left, R.id.ll_chat_right, R.id.tv_chat_left,
                        R.id.tv_chat_right,R.id.tv_time_text, message.getContent(), message.getType());
            }
        };
    }

    @OnClick({R.id.btn_send, R.id.iv_actionbar_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_actionbar_left:
                startActivity(HomeActivity.class);
                finish();
                break;
            case R.id.btn_send:
                String content = edInputMessage.getText().toString();
                mySendMessage(content);
                edInputMessage.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getDbManager(getApplicationContext()).DBClose();
    }

    private void mySendMessage(String content) {
        if(!TextUtils.isEmpty(content)) {
            Message message = new Message(name, CommonUtil.getTime(), content, CommonUtil.TYPE_RIGHT);
            if (CommonUtil.getTimeLong() - DBManager.getTime() > TimeNoteUtil.timeDuration) {
                Message timeMsg = new Message(null, null, CommonUtil.getTimeSelect(CommonUtil.getTimeLong()), CommonUtil.TYPE_TIME);
                adapter.addDataAll(timeMsg);
            }
            adapter.addDataAll(message);
            lvTalkMessage.setSelection(adapter.getDataList().size());
            DBManager.getDbManager(getApplicationContext()).saveMessage(message);
        }
    }

    @Override
    public void complete(final List<Message> list) {
        android.os.Message message = new android.os.Message();
        message.what = 1;
        message.obj = list;
        handler.sendMessage(message);
    }
}

