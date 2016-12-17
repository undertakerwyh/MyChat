package com.wyh.mychat.biz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.util.CommonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/8.
 */

public class DBManager {

    private final static String DBNAME = "Message.db";
    private final static int VERSION = 1;
    private static DBManager dbManager = null;
    private static Context contexts;
    private boolean istrue = true;

    private static SaveNewMessage saveNewMessage = null;
    private static SQLiteDatabase saveNewMessagedb = null;

    /**
     * 记录加载数据的编号
     */

    public long getTime() {
        return saveTime;
    }

    public void setTime(long time) {
        DBManager.saveTime = time;
    }

    /**
     * 记录第一条数据的时间
     */
    private static long saveTime = 0;

    /**
     * 过去消息加载条数
     */
    public static final int loadNum = 20;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private UpdateListener updateListener;


    public static DBManager getDbManager(Context context) {
        if (dbManager == null) {
            synchronized (context) {
                dbManager = new DBManager();
            }
            contexts = context;
            saveNewMessage = new SaveNewMessage(contexts, DBNAME, null, VERSION);
            saveNewMessagedb = saveNewMessage.getWritableDatabase();
        }
        return dbManager;
    }

    private DBManager() {

    }

    public void saveNewMessage(Message message) {
        int type;
        if(message.isNew()==false){
            type = 0;
        }else{
            type = 1;
        }
        saveNewMessagedb.execSQL("insert into message(username,name,content,time,new) values (?,?,?,?,?)"
                , new Object[]{UserManager.getUserManager(contexts).loadUserName(),message.getName()
                        , message.getContent(),String.valueOf(message.getTime()),type });
    }
    public void changeNewMessage(Message message){
        int type;
        if(message.isNew()==false){
            type = 0;
        }else{
            type = 1;
        }
        saveNewMessagedb.execSQL("update message set content = ? , time = ? ,new = ? where name = ? and username = ?"
                ,new Object[]{message.getContent(),message.getTime(),type
                ,String.valueOf(message.getName()),UserManager.getUserManager(contexts).loadUserName()});
    }

    public void deleNewMessage(String nameDB){
        saveNewMessagedb.execSQL("delete from message where username = ? and name = ?",new Object[]{UserManager.getUserManager(contexts).loadUserName(),nameDB});
    }

    public List<Message> loadNewMessage(String nameDB) {
        List<Message> list = new ArrayList<>();
        Cursor cursor = saveNewMessagedb.rawQuery("select * from message", null);
        String name = null;
        long time = 0;
        String content = null;
        boolean isNew = false;
        int isNewNum=0;
        int type = 0;
        String userName = null;
        if (cursor.moveToFirst()) {
            do {
                userName = cursor.getString(cursor.getColumnIndex("username"));
                if(nameDB.equals(userName)) {
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    time = Long.parseLong(cursor.getString(cursor.getColumnIndex("time")));
                    content = cursor.getString(cursor.getColumnIndex("content"));
                    isNewNum= cursor.getInt(cursor.getColumnIndex("new"));
                    if(isNewNum==0){
                        isNew = false;
                    }else{
                        isNew = true;
                    }
                    if (UserManager.getUserManager(contexts).loadUserName().equals(name)) {
                        type = CommonUtil.TYPE_RIGHT;
                    } else {
                        type = CommonUtil.TYPE_LEFT;
                    }
                    Message message = new Message(name, time, content, type);
                    message.setNew(isNew);
                    list.add(message);
                }
            } while (cursor.moveToNext());
        }
        return list;
    }


    private boolean isFirst = true;

    /**
     * 保存发送信息
     *
     * @param message 实体类,保存string类型的发送名,内容和发送时间
     */
    public void saveMessage(Message message) {
        if (isFirst) {
            isFirst = false;
            saveTime = message.getTime();
        }
        String name = message.getName();
        String content = message.getContent();
        long time = message.getTime();
        int type = message.getType();
        createSentTextMsg(name, UserManager.getUserManager(contexts).loadUserName(), content, time);
    }

    private String msgId;

    public void setFirstLoad(boolean firstLoad) {
        isFirstLoad = firstLoad;
    }

    private boolean isFirstLoad = true;

    private String saveName;
    private boolean isSaveNull = true;

    /**
     * 加载数据内容
     *
     * @param name 加载好友名
     */
    public void loadMessageDESC(final String name, boolean isDBInit) {
        EMConversation conversation = EMChatManager.getInstance().getConversation(name);
        final List<Message> list = new ArrayList<>();
        List<EMMessage> messages = null;
        if (isDBInit) {
            messages = conversation.getAllMessages();
            if (messages.size() < 20) {
                isFirstLoad = true;
            } else {
                msgId = messages.get(0).getMsgId();
                isFirstLoad = false;
            }
        } else {
            if (isFirstLoad) {
                updateListener.complete(list);
                return;
            }
            messages = conversation.loadMoreMsgFromDB(msgId, loadNum);
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        final List<EMMessage> finalMessages = messages;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = true;
                for (EMMessage emMessage : finalMessages) {
                    String name = emMessage.getFrom();
                    long time = emMessage.getMsgTime();
                    String msgBody = emMessage.getBody().toString();
                    String[] msgType = msgBody.split(":");
                    String content = null;
                    String bitmapPath = null;
                    int type =-1;
                    if (istrue) {
                        isFirst = false;
                        saveTime = time;
                    }
                    istrue = false;
                    if (msgType[0].equals("txt")) {
                        type = CommonUtil.TYPE_LEFT;
                        content = msgType[1].substring(msgType[1].indexOf("\"") + 1, msgType[1].lastIndexOf("\""));
                        if (name.equals(UserManager.getUserManager(contexts).loadUserName())) {
                            type = CommonUtil.TYPE_RIGHT;
                        }
                        Message message = new Message(name, time, content, type);
                        list.add(message);
                    }else if(msgType[0].equals("image")){
                        ImageMessageBody imageMessageBody= (ImageMessageBody) emMessage.getBody();
                        if (name.equals(UserManager.getUserManager(contexts).loadUserName())) {
                            type = CommonUtil.TYPE_PICRIGHT;
                            bitmapPath = imageMessageBody.getLocalUrl();
                        }else{
                            type = CommonUtil.TYPT_PICLEFT;
                            bitmapPath = contexts.getCacheDir()+"/"+imageMessageBody.getFileName();
                        }
                        Message message = new Message(name,time,type,bitmapPath);
                        list.add(message);
                    }
                    if (isFirst) {
                        msgId = emMessage.getMsgId();
                        isFirst = false;
                    }
                }
                updateListener.complete(list);
            }
        });
    }

    public void DBClose() {
        istrue = true;
    }

    /**
     * 更新加载数据的回调接口
     */
    public interface UpdateListener {
        void complete(List<Message> list);
    }

    public EMMessage createSentTextMsg(String to, String from, String content, long time) {
        EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody body = new TextMessageBody(content);
        msg.addBody(body);
        msg.setTo(to);
        msg.setFrom(from);
        msg.setMsgTime(time);
        return msg;
    }

    //创建一条接收TextMsg
    public EMMessage createReceivedTextMsg(String to, String from, String content, long time) {
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        TextMessageBody body = new TextMessageBody(content);
        msg.addBody(body);
        msg.setFrom(from);
        msg.setTo(to);
        msg.setMsgTime(time);
        return msg;
    }
    //创建一条接收TextMsg
    public EMMessage createReceivedPicMsg(String to, String from, File file, long time) {
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.IMAGE);
        ImageMessageBody body = new ImageMessageBody(file);
        msg.addBody(body);
        msg.setFrom(from);
        msg.setTo(to);
        msg.setMsgTime(time);
        return msg;
    }

    static class SaveNewMessage extends SQLiteOpenHelper {

        private final static String NEW_MESSAGE = "create table message(id integer primary key autoincrement,username text,name text,content text,time text,new integer)";

        public SaveNewMessage(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(NEW_MESSAGE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
