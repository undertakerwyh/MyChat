package com.wyh.mychat.receive;

/**
 * Created by Administrator on 2016/12/7.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.wyh.mychat.biz.DBManager;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.util.CommonUtil;

/**
 * 消息送达BroadcastReceiver
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {
    public static void setNewMessageTalk(NewMessageTalk newMessageTalk) {
        NewMessageBroadcastReceiver.newMessageTalk = newMessageTalk;
    }

    private static NewMessageTalk newMessageTalk;
    @Override
    public void onReceive(Context context, Intent intent) {
        //消息id
        String msgId = intent.getStringExtra("msgid");
        //发消息的人的username(userid)
        String msgFrom = intent.getStringExtra("from");
        //消息类型，文本、图片、语音消息等，这里返回的值为msg.type.ordinal()。
        //所以消息type实际为是enum类型
        int msgType = intent.getIntExtra("type", 0);
        Log.d("main", "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
        //更方便的方法是通过msgId直接获取整个message
        EMMessage message = EMChatManager.getInstance().getMessage(msgId);
        EMChatManager.getInstance().getNewMessageBroadcastAction();
        newMessageTalk.update(message);
        DBManager.getDbManager(context).createReceivedTextMsg(UserManager.getUserManager(context).loadUserName()
                ,msgFrom
                ,message.getBody().toString()
                , CommonUtil.getTimeLong());
    }
    public interface NewMessageTalk{
        void update(EMMessage message);
    }
    public interface NewMessageHome{
        void update();
    }
}