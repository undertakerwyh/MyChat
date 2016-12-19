package com.wyh.mychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.wyh.mychat.R;
import com.wyh.mychat.activity.HomeActivity;
import com.wyh.mychat.activity.TalkActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.biz.BitmapManager;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.view.ListViewBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MessageFragment extends Fragment implements NewMessageBroadcastReceiver.NewMessageHome, TalkActivity.MySendUpdate, ListViewBar.ListViewBarListener {
    @Bind(R.id.lv_message)
    ListView lvMessage;
    private View view;
    private UniversalAdapter<Message> messageAdapter;
    private HashMap<String, Integer> messageHash = new HashMap<>();
    private List<Message> list = new ArrayList<>();
    private ListViewBar listViewBar;
    private int eventX;
    private int eventY;

    public void setDeleName(String deleName) {
        this.deleName = deleName;
    }

    private String deleName;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                list = DBManager.getDbManager(context).loadNewMessage(UserManager.getUserManager(context).loadUserName());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        /**初始化适配器*/
        initAdapter();
        lvMessage.setAdapter(messageAdapter);
        messageAdapter.addDataAddAll(list);
        /**向适配器添加数据*/
        savePosition();
        return view;
    }

    private void savePosition() {
        messageHash.clear();
        for (int i = 0; i < messageAdapter.getDataList().size(); i++) {
            messageHash.put(messageAdapter.getDataList().get(i).getName(), i);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NewMessageBroadcastReceiver.setNewMessageHome(this);
        TalkActivity.setMySendUpdate(this);
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.pop_contacts_dele_record));
        listViewBar = new ListViewBar(getContext(), list, this);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        messageAdapter = new UniversalAdapter<Message>(getContext(), R.layout.chat_item_title) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final Message message = messageAdapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_chat_name, message.getName())
                        .setTextViewContent(R.id.tv_chat_lastContent, message.getContent())
                        .setTextViewContent(R.id.tv_chat_data, CommonUtil.getTimeSelect(message.getTime()))
                        .setImageViewContent(R.id.iv_chat_headPortrait, R.drawable.headportrait)
                        .setLayoutVisivity(R.id.iv_new_icon, message.isNew())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                message.setNew(false);
                                messageAdapter.notifyDataSetChanged();
                                ((HomeActivity) getActivity()).dismissPop();
                                Intent intent = new Intent(getActivity(), TalkActivity.class);
                                intent.putExtra("name", message.getName());
                                getActivity().startActivity(intent);
                                String content = message.getContent();
                                String substring = content.substring(content.indexOf("]") + 1, content.length());
                                cleanUnRead(message.getName());
                                message.setContent(substring);
                                DBManager.getDbManager(getContext()).changeNewMessage(message);
                            }
                        }).setLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleName = message.getName();
                        listViewBar.showAsDropDown(v, eventX, eventY - v.getMeasuredHeight());
                        return true;
                    }
                }).setTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        eventX = (int) event.getX();
                        eventY = (int) event.getY();
                        return false;
                    }
                });
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void updateHome(EMMessage emMessage) {
        String msgBody = emMessage.getBody().toString();
        String[] msgType = msgBody.split(":");
        String content = null;
        if (msgType[0].equals("txt")) {
            content = msgType[1].substring(msgType[1].indexOf("\"") + 1, msgType[1].lastIndexOf("\""));
        }else if(msgType[0].equals("image")){
            content = getString(R.string.talk_pic);
            ImageMessageBody imageMessageBody = (ImageMessageBody) emMessage.getBody();
            String bitmapUrl = imageMessageBody.getThumbnailUrl();
            String from = emMessage.getFrom();
            String name = imageMessageBody.getFileName();
            long time = emMessage.getMsgTime();
            BitmapManager.getBitmapManager(getContext()).getBitmapUrl(bitmapUrl, name, from, time,false);
        }
        String userName = emMessage.getFrom();
        if (messageHash.containsKey(userName)) {
            Message message = messageAdapter.getDataList().get(messageHash.get(userName));
            if(!userName.equals(TalkActivity.getFriendName())){
                message.setNew(true);
            }
            EMConversation conversation = EMChatManager.getInstance().getConversation(userName);
            message.setContent("["+conversation.getUnreadMsgCount()+"条]"+content);
            message.setTime(emMessage.getMsgTime());
            DBManager.getDbManager(getContext()).changeNewMessage(message);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Message message = new Message(userName, emMessage.getMsgTime(), content, CommonUtil.TYPE_LEFT);
            message.setNew(true);
            DBManager.getDbManager(getContext()).saveNewMessage(message);
            messageAdapter.addDataUpdate(message);
            messageHash.put(message.getName(), messageAdapter.getCount() - 1);
        }
    }

    public void cleanUnRead(String username){
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        conversation.markAllMessagesAsRead();
    }

    @Override
    public void SendUpdate(Message message) {
        String userName = message.getName();
        if (messageHash.containsKey(userName)) {
            Message messageMain = messageAdapter.getDataList().get(messageHash.get(userName));
            if(message.getType()==CommonUtil.TYPE_PICRIGHT||message.getType()==CommonUtil.TYPE_PICLEFT){
                messageMain.setContent(getString(R.string.talk_pic));
            }else {
                messageMain.setContent(message.getContent());
            }
            messageMain.setTime(message.getTime());
            DBManager.getDbManager(getContext()).changeNewMessage(messageMain);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Message messageMain= null;
            if(message.getType()==CommonUtil.TYPE_PICRIGHT||message.getType()==CommonUtil.TYPE_PICLEFT){
                messageMain = new Message(message.getName(), message.getTime(), getString(R.string.talk_pic), message.getType());
            }else {
                messageMain = new Message(message.getName(), message.getTime(), message.getContent(), message.getType());
            }
            messageAdapter.addDataUpdate(messageMain);
            DBManager.getDbManager(getContext()).saveNewMessage(messageMain);
            messageHash.put(messageMain.getName(), messageAdapter.getCount() - 1);
        }
    }

    @Override
    public void onComplete(String name) {
        if (isAdded()) {
            if (name.equals(getString(R.string.pop_contacts_dele_record))) {
                if (messageHash.get(deleName) != null) {
                    messageAdapter.getDataList().remove((int) messageHash.get(deleName));
                }
                DBManager.getDbManager(getContext()).deleNewMessage(deleName);
                EMChatManager.getInstance().deleteConversation(deleName);
                savePosition();
                lvMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
