package com.wyh.mychat.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/18.
 */

public class Picture implements Serializable{
    private String name;
    private File file;

    public Picture( String name, File file) {
        this.name = name;
        this.file = file;
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


}
