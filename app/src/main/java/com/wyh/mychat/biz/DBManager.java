package com.wyh.mychat.biz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wyh.mychat.entity.Message;

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
    private boolean istrue = true;
    /**
     * 记录加载数据的编号
     */
    private int maxId = -1;

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
    public static final int loadNum = 18;

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private UpdateListener updateListener;


    public static DBManager getDbManager(Context context){
        if(dbManager==null){
            synchronized (context){
                dbManager = new DBManager();
            }
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
            time = Long.parseLong(message.getTime());
        }
        String name = message.getName();
        String content = message.getContent();
        String time = message.getTime();
        int type = message.getType();
        sqLiteDatabase.execSQL("insert into Message (name,content,time,type) values (?,?,?,?)",new Object[]{name,content,time,type});
    }
    private int maxLoad;

    /**
     * 加载数据内容
     * @param name 加载好友名
     * @param size 当前加载的条数,判断是否与数据库同步
     */
    public void loadMessageDESC(final String name, final int size){
        final List<Message> list = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int loop =0;
                String nameLoad;
                int idLoad;
                int typeLoad;
                String timeLoad;
                String contentLoad;
                Cursor cursor = sqLiteDatabase.rawQuery("select * from Message order by id DESC",null);
                if(cursor.moveToFirst()){
                    do {
                        nameLoad = cursor.getString(cursor.getColumnIndex("name"));
                        idLoad = cursor.getInt(cursor.getColumnIndex("id"));
                        if(maxLoad<idLoad){
                            maxLoad = idLoad;
                        }
                        if(maxLoad>size) {
                            if (name.equals(nameLoad) && (idLoad < maxId || istrue) && loop < loadNum) {
                                typeLoad = cursor.getType(cursor.getColumnIndex("type"));
                                timeLoad = cursor.getString(cursor.getColumnIndex("time"));
                                if (istrue) {
                                    isFirst = false;
                                    time = Long.parseLong(timeLoad);
                                }
                                contentLoad = cursor.getString(cursor.getColumnIndex("content"));
                                Message message = new Message(nameLoad, timeLoad, contentLoad, typeLoad);
                                list.add(0, message);
                                istrue = false;
                                maxId = idLoad;
                                loop++;
                            }
                        }
                    }while (cursor.moveToNext());
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
}
