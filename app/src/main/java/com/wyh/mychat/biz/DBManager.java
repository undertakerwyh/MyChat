package com.wyh.mychat.biz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.util.CommonUtil;

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
    private static MySQLite mySQLite;
    private static SQLiteDatabase sqLiteDatabase;
    private static Context contexts;
    private boolean istrue = true;

    /**
     * 记录加载数据的编号
     */

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        DBManager.time = time;
    }

    /**记录第一条数据的时间*/
    private static long time = 0;

    /**
     * 过去消息加载条数
     */
    public static final int loadNum = 20;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private UpdateListener updateListener;


    public static DBManager getDbManager(Context context){
        if(dbManager==null){
            synchronized (context){
                dbManager = new DBManager();
            }
            contexts = context;
            mySQLite = new MySQLite(context,DBNAME,null,VERSION);
            sqLiteDatabase = mySQLite.getWritableDatabase();
        }
        return dbManager;
    }
    private boolean isFirst = true;

    /**
     * 保存发送信息
     * @param message 实体类,保存string类型的发送名,内容和发送时间
     */
    public void saveMessage(Message message){
        if(isFirst){
            isFirst = false;
            time = message.getTime();
        }
        String name = message.getName();
        String content = message.getContent();
        long time = message.getTime();
        int type = message.getType();
//        sqLiteDatabase.execSQL("insert into Message (name,content,time,type) values (?,?,?,?)",new Object[]{name,content,time,type});
        createSentTextMsg(name,UserManager.getUserManager(contexts).loadUserName(),content,time);
    }
    private String msgId;

    /**
     * 加载数据内容
     * @param name 加载好友名
     */
    public void loadMessageDESC(final String name,boolean isDBInit){
        EMConversation conversation = EMChatManager.getInstance().getConversation(name);
        final List<Message> list = new ArrayList<>();
        List<EMMessage> messages=null;
        if(isDBInit) {
            messages = conversation.getAllMessages();
            Log.e("AAA","true");
        }else{
            messages =  conversation.loadMoreMsgFromDB(msgId,loadNum);
            Log.e("AAA","false");
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        final List<EMMessage> finalMessages = messages;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = true;
                for(EMMessage emMessage: finalMessages){
                    String name = emMessage.getFrom();
                    long time = emMessage.getMsgTime();
                    String msgBody = emMessage.getBody().toString();
                    String []msgType =msgBody.split(":");
                    String content = null;
                    if(msgType[0].equals("txt")){
                        content = msgType[1].substring(msgType[1].indexOf("\"")+1,msgType[1].lastIndexOf("\""));
                    }
                    int type = CommonUtil.TYPE_LEFT;
                    if(name.equals(UserManager.getUserManager(contexts).loadUserName())){
                        type = CommonUtil.TYPE_RIGHT;
                    }
                    Message message = new Message(name,time,content,type);
                    list.add(message);
                    if(isFirst){
                        msgId = emMessage.getMsgId();
                        isFirst = false;
                    }
                }
                updateListener.complete(list);
            }
        });
    }
    public void DBClose(){
        istrue = true;
    }

    /**
     * 更新加载数据的回调接口
     */
    public interface UpdateListener{
        void complete(List<Message>list);
    }
    public static void DeleteData(){
        sqLiteDatabase.execSQL("delete from Message where id>?",new String []{"0"});
    }

    /**
     * 数据库初始化
     */
    static class MySQLite extends SQLiteOpenHelper {
        private static final String Message = "create table Message(id integer primary key autoincrement,name text,content text,time text,type integer)";

        public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(Message);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
    public EMMessage createSentTextMsg(String to,String from,String content,long time) {
        EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody body = new TextMessageBody(content);
        msg.addBody(body);
        msg.setTo(to);
        msg.setFrom(from);
        msg.setMsgTime(time);
        return msg;
    }

    //创建一条接收TextMsg
    public EMMessage createReceivedTextMsg(String to,String from,String content,long time) {
        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        TextMessageBody body = new TextMessageBody(content);
        msg.addBody(body);
        msg.setFrom(from);
        msg.setTo(to);
        msg.setMsgTime(time);
        return msg;
    }
}
