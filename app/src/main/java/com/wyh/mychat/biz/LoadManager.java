package com.wyh.mychat.biz;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wyh.mychat.R;
import com.wyh.mychat.entity.Picture;
import com.wyh.mychat.fragment.ResourceFragment;

import java.io.File;
import java.util.Iterator;
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

    private FileUpdate fileUpdate;

    private ResourceUpdate resourceUpdate;

    private boolean isStop = false;

    private boolean isStopFile = false;

    private static TreeSet<String> folderSet = new TreeSet<>();

    private ScheduledExecutorService scheduledSrcService;
    private ExecutorService srcService;
    private ExecutorService ServiceResource;
    private ScheduledExecutorService scheduledResourceService;

    public void setFileUpdate(FileUpdate fileUpdate) {
        this.fileUpdate = fileUpdate;
    }

    public void setResourceUpdate(ResourceUpdate resourceUpdate) {
        this.resourceUpdate = resourceUpdate;
    }

    public static LoadManager getPicLoadManager(Context context) {
        contexts = context;
        if (picLoadManager == null) {
            synchronized (context) {
                picLoadManager = new LoadManager();
            }
        }
        return picLoadManager;
    }


    /**
     * 获取file中的图片资源
     */
    public void getResource(final File file) {
        isStop = false;
        ResourceFragment.initList();
        ServiceResource = Executors.newCachedThreadPool();
        scheduledResourceService = Executors.newScheduledThreadPool(1);
        ServiceResource.execute(new Runnable() {
            @Override
            public void run() {
                searchResource(file);
            }
        });
        ServiceResource.shutdown();
        scheduledResourceService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ServiceResource.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
                        resourceUpdate.ResourceEnd();
                        scheduledResourceService.shutdown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void reSearch() {
        isFirst = true;
    }

    /**
     * 获取sd卡中有图片的文件夹
     */
    public void getSrcList(final File sdFile) {
        if (isFirst) {
            folderSet.clear();
            scheduledSrcService = Executors.newScheduledThreadPool(1);
            final File[] files = sdFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                final int finalI = i;
                srcService = Executors.newCachedThreadPool();
                srcService.execute(new Runnable() {
                    @Override
                    public void run() {
                        searchFile(files[finalI]);
                    }
                });
            }
            srcService.shutdown();
            scheduledSrcService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (srcService.isTerminated()) {
                        fileUpdate.fileEnd();
                        isFirst = false;
                        scheduledSrcService.shutdown();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        } else {
            Iterator<String> iterator = folderSet.descendingIterator();
            while (iterator.hasNext()) {
                fileUpdate.update(iterator.next());
            }
            fileUpdate.fileEnd();
        }
    }

    /**
     * 搜索图片资源的递归方法
     */
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
            if (type.equals("png") || type.equals("jpg") || type.equals("gif") && !isStop) {
                String name = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().length());
                resourceUpdate.resourceUpdate(new Picture(name, file));
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

    /**
     * 搜索有图片资源的文件夹的递归方法
     */
    public void searchFile(File file) {
        if (isStopFile) {
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
        isStopFile = stop;
    }

    /**
     * 搜索到的文件夹回调接口
     */
    public interface FileUpdate {
        void update(String folder);

        void fileEnd();
    }

    /**
     * 搜索到的文件夹下资源回调接口
     */
    public interface ResourceUpdate {
        void resourceUpdate(Picture picture);

        void ResourceEnd();
    }

    public void stopSearch() {
        isStop = true;
        if (scheduledResourceService != null) {
            scheduledResourceService.shutdown();
        }
        if (ServiceResource != null) {
            ServiceResource.shutdown();
        }
        resourceUpdate.ResourceEnd();
    }

    /**
     * Lrucache保存图片资源和读取图片资源
     *
     * @param file 图片文件
     */
    public void loadLruCache(final File file, ImageView imageView) {
        Glide.with(contexts)
                .load(file)
                .override(128,128)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.drawable.load_pic)
                .into(imageView);
    }
}
