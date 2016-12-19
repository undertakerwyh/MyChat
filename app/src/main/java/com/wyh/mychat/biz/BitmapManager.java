package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.LruCache;

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
    private final static String myTalkPath = Environment.getExternalStorageDirectory().getPath()+"/MyTalk";

    public static BitmapManager getBitmapManager(Context context) {
        if (bitmapManager == null) {
            synchronized (context) {
                bitmapManager = new BitmapManager();
            }
            contexts = context;
        }
        return bitmapManager;
    }

    public String getBitmapPath() {
        return contexts.getExternalFilesDir("image").getPath();
    }
    public String getBitmapName(String path){
        String[] newPath = path.split(getBitmapPath()+"/");
        return newPath[1];
    }

    public void getBitmapUrl(String bitmapUrl, String name, String from, long time) {
        bitmapAsyncTask = new BitmapAsyncTask();
        bitmapAsyncTask.execute(bitmapUrl, name, from, String.valueOf(time));
    }

    class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                String bitmapPath = getBitmapPath() + "/" + params[1];
                saveCacheUrl(params[1], in);
                loadBitmapFromCache(bitmapPath, CommonUtil.TYPT_PICLEFT);
                newMessageTalk.returnTalkPic(params[2], bitmapPath);
                DBManager.getDbManager(contexts).createReceivedPicMsg(UserManager.getUserManager(contexts).loadUserName(), params[2], new File(bitmapPath), Long.parseLong(params[3]));
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Bitmap loadBitmapFromCache(@NonNull String path, int type) {
        Bitmap bitmap = null;
        if (type == CommonUtil.TYPE_PICRIGHT || type == CommonUtil.TYPT_PICLEFT) {
            bitmap = lruCache.get(path);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    lruCache.put(path, bitmap);
                }
            }
        }
        return bitmap;
    }
    public void saveBitmapFromSD(String bitmapPath,String name){
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File picFile = new File(myTalkPath);
        if(!picFile.exists()){
            picFile.mkdirs();
        }
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(bitmapPath)));
            bos = new BufferedOutputStream(new FileOutputStream(picFile+"/"+name));
            int len = 0;
            byte[]buff = new byte[6*1024*1024];
            while ((len = bis.read(buff))!=-1){
                bos.write(buff,0,len);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bis !=null) {
                    bis.close();
                }
                if(bos!=null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveCacheUrl(String name, InputStream in) {
        File cacheFile = contexts.getExternalFilesDir("image");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
            LoadManager.getPicLoadManager(contexts).reSearch();
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
    }
}
