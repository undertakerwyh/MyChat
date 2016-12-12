package com.wyh.mychat.biz;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;

import java.io.File;

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
        if (!userName.equals(userNameSave)) {
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
        EMChatManager.getInstance().sendMessage(message, emCallBack);
    }

    public void sendPicMessage(String username, File picFile, EMCallBack emCallBack) {
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        //如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);

        ImageMessageBody body = new ImageMessageBody(picFile);
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        message.setReceipt(username);
        conversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message, emCallBack);
    }
}
