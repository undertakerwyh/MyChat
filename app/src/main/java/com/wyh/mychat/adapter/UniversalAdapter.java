package com.wyh.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wyh.mychat.entity.Message;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.util.TimeNoteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 万能适配器
 */
public abstract class UniversalAdapter<DataType> extends BaseAdapter {

    private Context context;
    private int layoutRes;

    public List<DataType> getDataList() {
        return dataList;
    }

    private List<DataType> dataList = new ArrayList<>();

    private TimeNoteUtil timeNoteUtil;


    public UniversalAdapter(Context context, int layoutRes) {
        this.context = context;
        this.layoutRes = layoutRes;
        LayoutInflater.from(context);
        timeNoteUtil = new TimeNoteUtil();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(convertView, context, layoutRes, parent);
        assignment(viewHolder, position);
        return viewHolder.getmView();
    }

    public abstract void assignment(ViewHolder viewHolder, int positon);

    /**
     * ------------------------------------------------------------------
     * 以下是自定义方法
     */
    public void addDataAll(DataType dataType) {
        dataList.add(dataType);
        this.notifyDataSetChanged();
    }

    public void addDataAddAll(List<DataType> list) {
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addDataToAdapterHead(List<DataType> list) {
        String time = null;
        List<DataType>dataTypeList = new ArrayList<>();
        timeNoteUtil.setFirst(true);
        for(DataType dataType:list){
            time = timeNoteUtil.start(Long.parseLong(((Message) dataType).getTime()));
            if(time!=null){
                Message message = new Message(null,null,time, CommonUtil.TYPE_TIME);
                dataTypeList.add(dataTypeList.size(),(DataType) message);
            }
            dataTypeList.add(dataTypeList.size(),dataType);
        }
        dataList.addAll(0,dataTypeList);
        this.notifyDataSetChanged();
    }
}
