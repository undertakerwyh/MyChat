package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.wyh.mychat.entity.Picture;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.R.id.list;

/**
 * Created by Administrator on 2016/11/18.
 */

public class LoadManager {
    private static LruCache<String,Bitmap>lruCache = new LruCache<>(3*1024*1024);
    private SoftReference softReference = new SoftReference(list);
    private static LoadManager picLoadManager;
    private static Context contexts;

    public void setFileUpdate(FileUpdate fileUpdate) {
        this.fileUpdate = fileUpdate;
    }

    private FileUpdate fileUpdate;


    private boolean isStop = false;
    public static LoadManager getPicLoadManager(Context context){
        contexts = context;
        if(picLoadManager==null){
            synchronized (context){
                picLoadManager = new LoadManager();
            }
        }
        return picLoadManager;
    }

    public void getPicList(final File sdFile, final File selfFile){
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Runnable() {
            @Override
            public void run() {
                searchFile(sdFile,sdFile.getName());
                searchFile(selfFile,selfFile.getName());
                fileUpdate.end();
            }
        });
    }
    public void searchFile(File file,String folder){
        if(isStop){
            return;
        }
        if(!file.exists()||file==null||!file.canRead()){
            return;
        }
        if(!file.isDirectory()){
            if(file.length()<=0){
                return;
            }
            int endIndex = file.getName().lastIndexOf(".");
            if(endIndex==-1){
                return;
            }
            String type = file.getName().substring(endIndex+1);
            if(type.equals("png")||type.equals("jpg")||type.equals("gif")){
                String name = file.getName().substring(0,endIndex-2);
                Picture picture = new Picture(folder,name,saveLruCache(file),file);
                fileUpdate.update(picture);
                return;
            }
        }
        File[]files = file.listFiles();
        if(files==null||files.length<=0){
            return;
        }
        String folderName = file.getName();
        for(int i=0;i<files.length;i++){
            searchFile(files[i],folderName);
        }
    }

    public void isStop(boolean stop){
        isStop = stop;
    }

    private Bitmap saveLruCache(File file){
        Bitmap bitmap=lruCache.get(file.getAbsolutePath());
        if(bitmap==null){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            lruCache.put(file.getAbsolutePath(),bitmap);
        }
        return bitmap;
    }

    public interface FileUpdate{
        void update(Picture picture);
        void end();
    }
}
