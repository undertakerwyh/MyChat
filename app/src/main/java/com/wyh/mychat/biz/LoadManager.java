package com.wyh.mychat.biz;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.wyh.mychat.entity.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/18.
 */

public class LoadManager {
    private static LoadManager picLoadManager;
    private static Context contexts;
    private boolean isFirst = true;

    public static List<Picture> getPicList() {
        return picList;
    }

    private static List<Picture> picList = new ArrayList<>();

    public static TreeSet<String> getFolderSet() {
        return folderSet;
    }

    private static TreeSet<String> folderSet = new TreeSet<>();

    public void setFileUpdate(FileUpdate fileUpdate) {
        this.fileUpdate = fileUpdate;
    }

    private FileUpdate fileUpdate;

    private boolean isStop = false;

    public static LoadManager getPicLoadManager(Context context) {
        contexts = context;
        if (picLoadManager == null) {
            synchronized (context) {
                picLoadManager = new LoadManager();
            }
        }
        return picLoadManager;
    }

    public void getSrcList(final File sdFile) {
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Runnable() {
            @Override
            public void run() {
                searchFile(sdFile);
                fileUpdate.end();
                isFirst = false;
            }
        });
    }

    public void searchFile(File file) {
        if (isFirst) {
            if (isStop) {
                return;
            }
            if (!file.exists() || file == null || !file.canRead()) {
                return;
            }
            if (!file.isDirectory()) {
                if (file.length() <= 0) {
                    return;
                }
                int endIndex = file.getName().lastIndexOf(".");
                if (endIndex == -1) {
                    return;
                }
                String type = file.getName().substring(endIndex + 1);
                if (type.equals("png") || type.equals("jpg") || type.equals("gif")) {
                    String[] headSplit = file.getPath().split(Environment.getExternalStorageDirectory().getPath()+"/");
                    String[] folderSplit = headSplit[1].split("/");
                    String folder = Environment.getExternalStorageDirectory().getPath()+"/"+folderSplit[0];
                    if(!folderSet.contains(folder)){
                        Log.e("AAA",folder);
                        folderSet.add(folder);
                        fileUpdate.update(folder);
                    }
                    return;
                }
            }
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                searchFile(files[i]);
            }
        }else{
            Iterator<String>iterator = folderSet.iterator();
            while (iterator.hasNext()){
                fileUpdate.update(iterator.next());
            }
        }
    }

    public void isStop(boolean stop) {
        isStop = stop;
    }

    public interface FileUpdate {
        void update(String folder);

        void end();
    }
}
