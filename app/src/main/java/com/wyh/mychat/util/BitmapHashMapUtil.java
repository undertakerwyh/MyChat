package com.wyh.mychat.util;

import android.content.Context;
import android.util.Log;

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
        Log.e("AAA","firstMap="+firstLetter);
        putPathName(name,path,letterName(firstLetter, folderName(folder)));
    }
    public boolean isBitMapPath(String folder,String name,String path){
        String firstLetter = name.substring(0, 1);
        Log.e("AAA","firstMap="+firstLetter);
        if(BitmapMap.containsKey(folder)){
            Map folderMap = (Map) BitmapMap.get(folder);
            if(folderMap.containsKey(firstLetter)){
                Hashtable firstMap = (Hashtable) folderMap.get(firstLetter);
                if(firstMap.containsKey(name)){
                    List nameList = (List) firstMap.get(name);
                    if(nameList.contains(path)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void getBitMapList(String folder){
        Hashtable nameTable;
        if(BitmapMap.containsKey(folder)){
            Map folderMap = (Map) BitmapMap.get(folder);
            Iterator iterator = folderMap.entrySet().iterator();
            while (iterator.hasNext()){
                nameTable = (Hashtable) iterator.next();
                Iterator iterator1 = nameTable.entrySet().iterator();
                while (iterator1.hasNext()){
                    List list = (List) iterator1.next();
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
    private Hashtable letterName(String letter,HashMap map){
        if(map.containsKey(letter)){
            return (Hashtable) map.get(letter);
        }else{
            Hashtable pathMap = new Hashtable<String,List>();
            map.put(letter,pathMap);
            return pathMap;
        }
    }
    private void putPathName(String name,String path,Hashtable map){
        if(map.containsKey(name)){
            ((List)map.get(name)).add(path);
        }else{
            List<String>list = new ArrayList<>();
            list.add(path);
            map.put(name,list);
        }
    }
}
