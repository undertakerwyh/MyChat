package com.wyh.mychat.biz;

import com.wyh.mychat.entity.Picture;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */

public class PicLoadManager {
    private List<Picture>list = new ArrayList<>();
    SoftReference<List>softReference = new SoftReference<List>(list);

}
