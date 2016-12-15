package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.attr.name;

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
                newMessageTalk.returnTalkPic(bitmap);
                saveCacheUrl(params[1], bitmap);
                File file = new File(contexts.getCacheDir().getPath() + "/" + name);
                DBManager.getDbManager(contexts).createReceivedPicMsg(UserManager.getUserManager(contexts).loadUserName(), params[2], file, Long.parseLong(params[3]));
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Bitmap loadBitmapFromCache(String path) {
        Bitmap bitmap = lruCache.get(path);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = BitmapFactory.decodeFile(path);
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
        void returnTalkPic(Bitmap bitmap);
    }
}
