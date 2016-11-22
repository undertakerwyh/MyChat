package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import com.wyh.mychat.entity.Picture;
import com.wyh.mychat.util.BitmapUtil;

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

    private static LruCache<String, Bitmap> lruCache = new LruCache<>(3 * 1024 * 1024);

    private FileUpdate fileUpdate;

    private boolean isStop = false;

    public void setResourceUpdate(ResourceUpdate resourceUpdate) {
        this.resourceUpdate = resourceUpdate;
    }

    private ResourceUpdate resourceUpdate;


    public static LoadManager getPicLoadManager(Context context) {
        contexts = context;
        if (picLoadManager == null) {
            synchronized (context) {
                picLoadManager = new LoadManager();
            }
        }
        return picLoadManager;
    }

    public void getResource(final File file) {
        final ExecutorService SrcService = Executors.newCachedThreadPool();
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        SrcService.execute(new Runnable() {
            @Override
            public void run() {
                searchResource(file);
            }
        });
        SrcService.shutdown();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (SrcService.isTerminated()) {
                    fileUpdate.end();
                    scheduledExecutorService.shutdown();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        resourceUpdate.end();
    }

    public void getSrcList(final File sdFile) {
        if (isFirst) {
            final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            final ExecutorService SrcService = Executors.newCachedThreadPool();
            final File[] files = sdFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                final int finalI = i;
                SrcService.execute(new Runnable() {
                    @Override
                    public void run() {
                        searchFile(files[finalI]);
                    }
                });
            }
            SrcService.shutdown();
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (SrcService.isTerminated()) {
                        fileUpdate.end();
                        isFirst = false;
                        scheduledExecutorService.shutdown();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        } else {
            Iterator<String> iterator = folderSet.descendingIterator();
            while (iterator.hasNext()) {
                fileUpdate.update(iterator.next());
            }
            fileUpdate.end();
        }
    }

    public void searchResource(File file) {
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
                String name = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().length());
                loadLruCache(name, file);
            }
        }
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            searchResource(files[i]);
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

    public interface ResourceUpdate {
        void resourceUpdate(Picture picture);

        void end();
    }

    public void loadLruCache(final String name, final File file) {
        Bitmap bitmap = lruCache.get(file.getAbsolutePath());
        if (bitmap == null) {
            bitmap = BitmapUtil.getSmallBitmap(file.getAbsolutePath());
        }
        resourceUpdate.resourceUpdate(new Picture(name, bitmap, file));
    }
}
