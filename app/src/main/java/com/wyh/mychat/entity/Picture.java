package com.wyh.mychat.entity;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Administrator on 2016/11/18.
 */

public class Picture {
    private File file;
    private Bitmap bitmap;

    public Picture(File file, Bitmap bitmap) {
        this.file = file;
        this.bitmap = bitmap;
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
