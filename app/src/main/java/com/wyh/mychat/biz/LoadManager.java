package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wyh.mychat.entity.Picture;

import java.io.File;
import java.util.ArrayList;
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
    private boolean isFirst=true;

    public static List<Picture> getPicList() {
        return picList;
    }

    public static TreeSet<String> getFolderSet() {
        return folderSet;
    }

    private static List<Picture> picList = new ArrayList<>();
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

    public void getSrcList(final File sdFile, final File selfFile) {
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Runnable() {
            @Override
            public void run() {
                searchFile(sdFile);
                searchFile(selfFile);
                fileUpdate.end();
                isFirst = false;
            }
        });
    }

    public void searchFile(File file) {
        if(isFirst) {
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
                    String name = file.getName().substring(0, endIndex - 2);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Picture picture = new Picture(name,bitmap, file);
                    picList.add(picture);
                    folderSet.add(file.getPath());
                    fileUpdate.update();
                    return;
                }
            }
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                return;
            }
            String folderName = file.getName();
            for (int i = 0; i < files.length; i++) {
                searchFile(files[i]);
            }
        }
    }

    public void isStop(boolean stop) {
        isStop = stop;
    }

    public interface FileUpdate {
        void update();
        void end();
    }
}
