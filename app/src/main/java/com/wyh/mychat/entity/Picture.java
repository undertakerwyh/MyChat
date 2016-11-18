package com.wyh.mychat.entity;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Administrator on 2016/11/18.
 */

public class Picture {
    private String folder;
    private String name;
    private Bitmap bitmap;
    private File file;

    public Picture(String folder, String name, Bitmap bitmap, File file) {
        this.folder = folder;
        this.name = name;
        this.bitmap = bitmap;
        this.file = file;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
