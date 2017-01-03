package com.wyh.mychat.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/30.
 */

public class BitmapHashMapUtil {
    private static BitmapHashMapUtil bitmapHashMapUtil;
    private Map BitmapMap;
    private String name;
    public static BitmapHashMapUtil getBitmapHashMapUtil(Context context){
        if(bitmapHashMapUtil==null){
            synchronized (context){
                bitmapHashMapUtil = new BitmapHashMapUtil();
            }
        }
        return bitmapHashMapUtil;
    }
    private BitmapHashMapUtil(){
        if(BitmapMap==null) {
            BitmapMap = new HashMap<String,HashMap>();
        }
    }
    /**
     *第一层保存文件夹名HashMap<String,HashMap></>
     *第二层保存首字母HashMap<String,Hashtable></>
     * 第三层保存文件名Hashtable<String,list></>
     */
    public void putBitMapPath(String folder,String name,String path){
        String firstLetter = name.substring(0, 1);
        putPathName(name,path,letterName(firstLetter, folderName(folder)));
    }
    public boolean isBitMapPath(String folder,String name,String path){
        Map folderMap=null;
        HashMap firstMap =null;
        List nameList=null;
        String firstLetter = name.substring(0, 1);
        if(BitmapMap.containsKey(folder)){
            folderMap = (Map) BitmapMap.get(folder);
            if(folderMap.containsKey(firstLetter)){
                firstMap = (HashMap) folderMap.get(firstLetter);
                if(firstMap.containsKey(name)){
                    nameList = (List) firstMap.get(name);
                    if(nameList.contains(path)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void getBitMapList(String folder){
        HashMap nameMap = null;
        List list=null;
        if(BitmapMap.containsKey(folder)){
            Map folderMap = (Map) BitmapMap.get(folder);
            Iterator iterator = folderMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                Object value = entry.getValue();
                nameMap = (HashMap) value;
                Iterator iterator1 = nameMap.entrySet().iterator();
                while (iterator1.hasNext()){
                    Map.Entry entry1 = (Map.Entry) iterator1.next();
                    Object value1 = entry1.getValue();
                    list = (List)value1;
                    Iterator iterator2 = list.iterator();
                    while (iterator2.hasNext()){
                        returnBitMapPath.returnPath((String) iterator2.next());
                    }
                }
            }
        }
    }

    public void setReturnBitMapPath(ReturnBitMapPath returnBitMapPath) {
        this.returnBitMapPath = returnBitMapPath;
    }

    private ReturnBitMapPath returnBitMapPath;
    public interface ReturnBitMapPath{
        void returnPath(String name);
    }

    private HashMap folderName(String folder){
        if(BitmapMap.containsKey(folder)){
            return (HashMap) BitmapMap.get(folder);
        }else{
            HashMap firstMap = new HashMap<String,Hashtable>();
            BitmapMap.put(folder,firstMap);
            return firstMap;
        }
    }
    private HashMap letterName(String letter,HashMap map){
        if(map.containsKey(letter)){
            return (HashMap) map.get(letter);
        }else{
            HashMap pathMap = new HashMap<String,List>();
            map.put(letter,pathMap);
            return pathMap;
        }
    }
    private void putPathName(String name, String path, HashMap map){
        if(map.containsKey(name)){
            ((List)map.get(name)).add(path);
        }else{
            List<String>list = new ArrayList<>();
            list.add(path);
            map.put(name,list);
        }
    }
}
