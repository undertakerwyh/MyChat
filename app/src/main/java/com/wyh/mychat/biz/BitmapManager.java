package com.wyh.mychat.biz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.wyh.mychat.util.BitmapUtil;
import com.wyh.mychat.util.CommonUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/12/15.
 */

public class BitmapManager {
    private static BitmapManager bitmapManager = null;
    private BitmapAsyncTask bitmapAsyncTask;
    private static Context contexts;
    private static LruCache<String, Bitmap> lruCache = new LruCache<>(3 * 1024 * 1024);
    public final static String myTalkPath = Environment.getExternalStorageDirectory().getPath() + "/MyTalk";
    private static BitmapSQLite bitmapSQLite;
    private static SQLiteDatabase bitmapDatebase;

    private static final String BITMAPDOWNLOAD = "bitmapDownload.db";
    private static final int VERSION = 1;

    public static BitmapManager getBitmapManager(Context context) {
        if (bitmapManager == null) {
            synchronized (context) {
                bitmapManager = new BitmapManager();
            }
            contexts = context;
            bitmapSQLite = new BitmapSQLite(contexts, BITMAPDOWNLOAD, null, VERSION);
            bitmapDatebase = bitmapSQLite.getWritableDatabase();
        }

        return bitmapManager;
    }

    private BitmapManager() {

    }

    public String getBitmapPath() {
        return contexts.getExternalFilesDir("image").getPath();
    }

    public String getBitmapName(String path) {
        String[] newPath = path.split(getBitmapPath() + "/");
        return newPath[1];
    }

    public void getBitmapUrl(String bitmapUrl, String name, String from, long time, boolean isTalk) {
        bitmapAsyncTask = new BitmapAsyncTask();
        bitmapAsyncTask.execute(bitmapUrl, name, from, String.valueOf(time), String.valueOf(isTalk));
    }
    private void getBitmapDownload(String bitmapUrl, String name, String from, long time, boolean isTalk) {
        bitmapAsyncTask = new BitmapAsyncTask();
        bitmapAsyncTask.execute(bitmapUrl, name, from, String.valueOf(time), String.valueOf(isTalk),"down");
    }

    public void saveBitmapDownload(String bitmapUrl, String name, String from, long time) {
        bitmapDatebase.execSQL("insert into bitmap(url,name,from_name,time) values (?,?,?,?)", new Object[]{bitmapUrl, name, from, time});
    }

    public void deleBitmapDownload(String bitmapUrl) {
        bitmapDatebase.execSQL("delete from bitmap where url = ?", new Object[]{bitmapUrl});
    }

    public void loadBitmapDownload() {
        Cursor cursor = bitmapDatebase.rawQuery("select * from bitmap", null);
        String bitmapUrl = null;
        String name = null;
        String from = null;
        long time = 0;
        if (cursor.moveToFirst()) {
            do {
                bitmapUrl = cursor.getString(cursor.getColumnIndex("url"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                from = cursor.getString(cursor.getColumnIndex("from_name"));
                time = Long.parseLong(cursor.getString(cursor.getColumnIndex("time")));
                File file = new File(contexts.getExternalFilesDir("image"),name);
                if(file.exists()&&file.length()>0&&file!=null){
                    file.delete();
                }
                getBitmapDownload(bitmapUrl,name,from,time,false);
            } while (cursor.moveToNext());
        }
    }

    class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
        public boolean isLoading() {
            return isLoading;
        }

        private boolean isLoading = false;

        private String bitmapUrl = null;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                isLoading = true;
                URL url = new URL(params[0]);
                bitmapUrl = params[0];
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                String bitmapPath = getBitmapPath() + "/" + params[1];
                if (newMessageTalk != null && Boolean.valueOf(params[4])) {
                    newMessageTalk.returnTalkPic(params[2], bitmapPath);
                }
                saveCacheUrl(params[1], in);
                if(params.length>=5) {
                    loadBitmapFromCache(bitmapPath, CommonUtil.TYPE_PICLEFT);
                    DBManager.getDbManager(contexts).createReceivedPicMsg(UserManager.getUserManager(contexts).loadUserName(), params[2], new File(bitmapPath), Long.parseLong(params[3]));
                }
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (newMessageTalk != null) {
                newMessageTalk.update();
            }
            deleBitmapDownload(bitmapUrl);
            isLoading = false;
        }
    }

    public Bitmap loadBitmapFromCache(@NonNull String path, int type) {
        Bitmap bitmap = null;
        if (type == CommonUtil.TYPE_PICRIGHT || type == CommonUtil.TYPE_PICLEFT) {
            bitmap = lruCache.get(path);
            if (bitmapAsyncTask == null && bitmap == null) {
                bitmap = BitmapUtil.getSmallBitmap(path);
                if (bitmap != null) {
                    lruCache.put(path, bitmap);
                }
            } else if (bitmap == null && !bitmapAsyncTask.isLoading()) {
                bitmap = BitmapUtil.getSmallBitmap(path);
                if (bitmap != null) {
                    lruCache.put(path, bitmap);
                }
            }
        }
        return bitmap;
    }

    private boolean isCreate = false;

    public void saveBitmapFromSD(String bitmapPath, String name) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File picFile = new File(myTalkPath);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }


        try {
            bis = new BufferedInputStream(new FileInputStream(new File(bitmapPath)));
            bos = new BufferedOutputStream(new FileOutputStream(picFile + "/" + name));
            int len = 0;
            byte[] buff = new byte[6 * 1024 * 1024];
            while ((len = bis.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (picFile.length() > 0 && ConfigManager.getConfigManager(contexts).loadPicFile()) {
            LoadManager.getPicLoadManager(contexts).reSearch();
            ConfigManager.getConfigManager(contexts).savePicFile(false);
        }

    }

    public void saveCacheUrl(String name, InputStream in) {
        File cacheFile = contexts.getExternalFilesDir("image");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        OutputStream out = null;
        byte[] buff = new byte[6 * 1024 * 1024];
        int len;
        try {
            out = new FileOutputStream(new File(cacheFile, name));
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setNewMessageTalk(NewMessageTalk newMessageTalk) {
        this.newMessageTalk = newMessageTalk;
    }

    private NewMessageTalk newMessageTalk;

    public interface NewMessageTalk {
        void returnTalkPic(String name, String bitmapPath);

        void update();
    }

    private static class BitmapSQLite extends SQLiteOpenHelper {
        private final String BITMAPUTRL = "create table bitmap(id integer primary key autoincrement,url text,name text,from_name text,time text)";

        public BitmapSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(BITMAPUTRL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
