package com.wyh.mychat.biz;

import android.content.Context;
import android.os.Environment;

import com.wyh.mychat.entity.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private LoadManager() {
    }

    public void getSrcList(final File sdFile) {
        if (isFirst) {
            final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            final ExecutorService fixService = Executors.newFixedThreadPool(3);
            final File[] files = sdFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                final int finalI = i;
                fixService.execute(new Runnable() {
                    @Override
                    public void run() {
                        searchFile(files[finalI]);
                    }
                });
            }
            fixService.shutdown();
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (fixService.isTerminated()) {
                        fileUpdate.end();
                        isFirst = false;
                        scheduledExecutorService.shutdown();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        } else {
            Iterator<String>iterator = folderSet.descendingIterator();
            while (iterator.hasNext()) {
                fileUpdate.update(iterator.next());
            }
            fileUpdate.end();
        }
    }

    public void searchFile(File file) {
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
                String[] headSplit = file.getPath().split(Environment.getExternalStorageDirectory().getPath() + "/");
                String[] folderSplit = headSplit[1].split("/");
                String folder = Environment.getExternalStorageDirectory().getPath() + "/" + folderSplit[0];
                if (!folderSet.contains(folder)) {
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
    }

    public void isStop(boolean stop) {
        isStop = stop;
    }

    public interface FileUpdate {
        void update(String folder);

        void end();
    }
}
