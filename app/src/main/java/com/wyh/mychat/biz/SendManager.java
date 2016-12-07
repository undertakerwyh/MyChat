package com.wyh.mychat.biz;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

/**
 * Created by Administrator on 2016/12/7.
 */

public class SendManager {
    private static SendManager sendMessage;
    private EMConversation conversation;

    public static SendManager getSendMessage(Context context) {
        if (sendMessage == null) {
            synchronized (context) {
                sendMessage = new SendManager();
            }
        }
        return sendMessage;
    }

    private String userNameSave;
    /**
     * 发送文本消息
     *
     * @param userName 接送人id
     */
    public void sendTextMessage(@NonNull String userName, String content, EMCallBack emCallBack) {
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        if(!userNameSave.equals(userName)) {
            conversation = EMChatManager.getInstance().getConversation(userName);
        }
        userNameSave = userName;
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(content);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(userName);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message,emCallBack);
    }
}
