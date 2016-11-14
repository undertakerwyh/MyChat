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

    private TimeUpdate timeUpdate;

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
    public void addDataRefreshTime(DataType dataType) {
        dataList.add(dataType);
        this.notifyDataSetChanged();
    }

    public void addDataRefreshList(List<DataType>list){
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }
    public void addDataToAdapterHead(List<DataType>list){
        list.add(list.size(),null);
        for(DataType dataType:list){
            String time = timeNoteUtil.getTime(Long.parseLong(((Message) dataType).getTime()));
            if(time!=null){
                Message message = new Message(null,null,time, CommonUtil.TYPE_TIME);
                dataList.add(0,(DataType) message);
            }
            dataList.add(0,dataType);
        }
        this.notifyDataSetChanged();
    }
    public interface TimeUpdate{
        void start(long time);
        void end();
    }
}
