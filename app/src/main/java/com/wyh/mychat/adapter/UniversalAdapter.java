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
import java.util.TreeSet;

/**
 * 万能适配器
 */
public abstract class UniversalAdapter<DataType> extends BaseAdapter {

    private Context context;
    private int layoutRes;
    private ViewHolder viewHolder;

    public List<DataType> getDataList() {
        return dataList;
    }

    private List<DataType> dataList = new ArrayList<>();

    public UniversalAdapter(Context context, int layoutRes) {
        this.context = context;
        this.layoutRes = layoutRes;
        LayoutInflater.from(context);
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
        viewHolder = ViewHolder.getViewHolder(convertView, context, layoutRes, parent);
        assignment(viewHolder, position);
        return viewHolder.getmView();
    }

    public abstract void assignment(ViewHolder viewHolder, int positon);

    /**
     * ------------------------------------------------------------------
     * 以下是自定义方法
     */
    public void addDataUpdate(DataType dataType) {
        dataList.add(dataType);
        this.notifyDataSetChanged();
    }
    public void addData(DataType dataType) {
        dataList.add(dataType);
    }

    public void addDataAddAll(List<DataType> list) {
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }
    public void addDataAllClean(List<DataType> list){
        dataList.clear();
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }
    public void addDataAllNotifyTree(TreeSet<DataType> list){
        dataList.clear();
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addDataToAdapterHead(List<DataType> list) {
        String time = null;
        List<DataType>dataTypeList = new ArrayList<>();
        TimeNoteUtil.getTimeNoteUtil().setFirst(true);
        for(DataType dataType:list){
            time = TimeNoteUtil.getTimeNoteUtil().start(((Message) dataType).getTime());
            if(time!=null){
                Message message = new Message(null,0,time, CommonUtil.TYPE_TIME);
                dataTypeList.add(dataTypeList.size(),(DataType) message);
            }
            dataTypeList.add(dataTypeList.size(),dataType);
        }
        dataList.addAll(0,dataTypeList);
        this.notifyDataSetChanged();
    }
}
