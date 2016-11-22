package com.wyh.mychat.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.wyh.mychat.entity.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

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
        final Bitmap bitmap = lruCache.get(file.getAbsolutePath());
        if (bitmap == null) {
            Luban.get(contexts)
                    .load(file)
                    .putGear(1)
                    .asObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    })
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
                        @Override
                        public Observable<? extends File> call(Throwable throwable) {
                            return Observable.empty();
                        }
                    })
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File files) {
                            try {
                                Bitmap bitmap1 = BitmapFactory.decodeStream(new FileInputStream(file));
                                lruCache.put(file.getAbsolutePath(),bitmap1);
                                resourceUpdate.resourceUpdate(new Picture(name, bitmap1, file));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            resourceUpdate.resourceUpdate(new Picture(name, bitmap, file));
        }
    }
}
