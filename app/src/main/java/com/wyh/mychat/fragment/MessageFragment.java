package com.wyh.mychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMMessage;
import com.wyh.mychat.R;
import com.wyh.mychat.activity.HomeActivity;
import com.wyh.mychat.activity.TalkActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.receive.NewMessageBroadcastReceiver;
import com.wyh.mychat.util.CommonUtil;

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

public class MessageFragment extends Fragment implements NewMessageBroadcastReceiver.NewMessageHome,TalkActivity.MySendUpdate{
    @Bind(R.id.lv_message)
    ListView lvMessage;
    private View view;
    private UniversalAdapter<Message>adapter;
    private HashMap<String,Integer>messageHash = new HashMap<>();
    private List<Message>list = new ArrayList<>();
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
        lvMessage.setAdapter(adapter);
        Log.e("MessageFragment", "list.size():" + list.size());
        adapter.addDataAddAll(list);
        /**向适配器添加数据*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NewMessageBroadcastReceiver.setNewMessageHome(this);
        TalkActivity.setMySendUpdate(this);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new UniversalAdapter<Message>(getContext(),R.layout.chat_item_title) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final Message message = adapter.getDataList().get(positon);
                messageHash.put(message.getName(),positon);
                viewHolder.setTextViewContent(R.id.tv_chat_name,message.getName())
                        .setTextViewContent(R.id.tv_chat_lastContent,message.getContent())
                        .setTextViewContent(R.id.tv_chat_data,CommonUtil.getTimeSelect(message.getTime()))
                        .setImageViewContent(R.id.iv_chat_headPortrait,R.drawable.headportrait)
                        .setLayoutVisivity(R.id.iv_new_icon,message.isNew())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        message.setNew(false);
                        adapter.notifyDataSetChanged();
                        ((HomeActivity)getActivity()).dismissPop();
                        Intent intent = new Intent(getActivity(), TalkActivity.class);
                        intent.putExtra("name",message.getName());
                        getActivity().startActivity(intent);
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
        }
        String userName = emMessage.getFrom();
        if(messageHash.containsKey(userName)){
            Message message =adapter.getDataList().get(messageHash.get(userName));
            message.setNew(true);
            message.setContent(content);
            message.setTime(emMessage.getMsgTime());
            DBManager.getDbManager(getContext()).changeNewMessage(message);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }else{
            Message message = new Message(userName,emMessage.getMsgTime(),content, CommonUtil.TYPE_LEFT);
            message.setNew(true);
            messageHash.put(message.getName(),adapter.getCount()-1);
            DBManager.getDbManager(getContext()).saveNewMessage(message);
            adapter.addDataUpdate(message);
        }
    }

    @Override
    public void SendUpdate(Message message) {
        String userName = message.getName();
        if(messageHash.containsKey(userName)){
            Message messageMain = adapter.getDataList().get(messageHash.get(userName));
            messageMain.setContent(message.getContent());
            messageMain.setTime(message.getTime());
            DBManager.getDbManager(getContext()).changeNewMessage(message);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
           });
        }else{
            messageHash.put(message.getName(),adapter.getCount()-1);
            DBManager.getDbManager(getContext()).saveNewMessage(message);
            adapter.addDataUpdate(message);
        }
    }
}
