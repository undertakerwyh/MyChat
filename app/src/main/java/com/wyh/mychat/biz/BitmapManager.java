package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.wyh.mychat.util.CommonUtil;

import java.io.File;
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

    public static BitmapManager getBitmapManager(Context context) {
        if (bitmapManager == null) {
            synchronized (context) {
                bitmapManager = new BitmapManager();
            }
            contexts = context;
        }
        return bitmapManager;
    }

    private BitmapManager() {
        bitmapAsyncTask = new BitmapAsyncTask();
    }

    public void getBitmapUrl(String bitmapUrl, String name, String from, long time) {
        bitmapAsyncTask.execute(bitmapUrl, name, from, String.valueOf(time));
    }

    class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                String bitmapPath = contexts.getCacheDir().getPath() + "/" + params[1];
                newMessageTalk.returnTalkPic(bitmapPath);
                saveCacheUrl(params[1], bitmap);
                loadBitmapFromCache(bitmapPath,CommonUtil.TYPT_PICLEFT);
                DBManager.getDbManager(contexts).createReceivedPicMsg(UserManager.getUserManager(contexts).loadUserName(), params[2],new File(bitmapPath), Long.parseLong(params[3]));
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Bitmap loadBitmapFromCache(@NonNull String path,int type) {
        Bitmap bitmap=null;
        if(type== CommonUtil.TYPE_PICRIGHT||type==CommonUtil.TYPT_PICLEFT) {
            bitmap = lruCache.get(path);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(path);
                lruCache.put(path, bitmap);
            }
        }
        return bitmap;
    }

    public void saveCacheUrl(String name, Bitmap bitmap) {
        File cacheFile = contexts.getCacheDir();
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        OutputStream out;
        try {
            out = new FileOutputStream(new File(cacheFile, name));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setNewMessageTalk(NewMessageTalk newMessageTalk) {
        this.newMessageTalk = newMessageTalk;
    }

    private NewMessageTalk newMessageTalk;

    public interface NewMessageTalk {
        void returnTalkPic(String bitmapPath);
    }
}
