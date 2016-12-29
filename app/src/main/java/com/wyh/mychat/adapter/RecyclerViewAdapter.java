package com.wyh.mychat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.entity.Message;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.util.TimeNoteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Administrator on 2016/12/28.
 */

public abstract class RecyclerViewAdapter<DataType> extends RecyclerView.Adapter<RecyclerViewAdapter<DataType>.MyViewHolder> {

    private Context context;
    private MyViewHolder viewHolder;
    private int resId;
    public List<DataType> getDataList() {
        return dataList;
    }

    public RecyclerViewAdapter(Context context, int resId) {
        this.context = context;
        this.resId = resId;
    }

    private List<DataType> dataList = new ArrayList<>();


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resId,parent,false);
        viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        assignment(holder,position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private SparseArray<View> views = new SparseArray<>();

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public <DataType extends View> DataType getView(int resId) {
            View view = views.get(resId);
            if (view == null) {
                view = mView.findViewById(resId);
                views.put(resId, view);
            }
            return (DataType) view;
        }
        public MyViewHolder setImageViewContent(int ViewId, int resId) {
            ImageView imageView = this.getView(ViewId);
            imageView.setImageResource(resId);
            return this;
        }

        public MyViewHolder setTextViewContent(int ViewId, String content) {
            TextView textView = this.getView(ViewId);
            textView.setText(content);
            return this;
        }

        public MyViewHolder setOnClickListener(View.OnClickListener listener) {
            mView.setOnClickListener(listener);
            return this;
        }

        public MyViewHolder setOnClickListener(View.OnClickListener listener, int... ResId) {
            for (int id : ResId) {
                View view = this.getView(id);
                view.setOnClickListener(listener);
            }
            return this;
        }

        public MyViewHolder setViewOnClickListener(View.OnClickListener listener, int... ViewId) {
            for (int id : ViewId) {
                View view = this.getView(id);
                view.setOnClickListener(listener);
            }
            return this;
        }

        public MyViewHolder setImageViewContent(int ViewId, Drawable icon) {
            ImageView imageView = this.getView(ViewId);
            imageView.setBackground(icon);
            return this;
        }

        public MyViewHolder setLayoutVisivity(int ViewId, boolean isShow) {
            View view = this.getView(ViewId);
            if (isShow) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public MyViewHolder setCheckBoxContent(int ViewId, boolean isSelected) {
            CheckBox checkBox = this.getView(ViewId);
            checkBox.setChecked(isSelected);
            return this;
        }

        public MyViewHolder setOnCheckedChangeListener(int ViewId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            CheckBox checkBox = this.getView(ViewId);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
            return this;
        }

        public MyViewHolder setImageViewContent(int ViewId,File file) {
            ImageView imageView = this.getView(ViewId);
            LoadManager.getPicLoadManager(context).loadLruCache(file,imageView);
            return this;
        }

        public MyViewHolder setListviewOnitemListener(ListView listView, AdapterView.OnItemClickListener listener) {
            listView.setOnItemClickListener(listener);
            return this;
        }

        public MyViewHolder setTextColorContent(int ViewId, int color) {
            TextView textView = this.getView(ViewId);
            textView.setTextColor(color);
            return this;
        }

        public MyViewHolder setLongClickListener(View.OnLongClickListener listener) {
            mView.setOnLongClickListener(listener);
            return this;
        }

        public MyViewHolder setTouchListener(View.OnTouchListener onTouchListener) {
            mView.setOnTouchListener(onTouchListener);
            return this;
        }

        /**
         * ----------------------------------------------------------------------------------------------------------------------------
         * 以下是自定义方法
         */


        public MyViewHolder setChatVisible(int leftLayoutId, int rightLayoutId, int leftId, int rightId, int picLeftId, int picRightId, int pbLeftId, int pbRightId, Bitmap bitmap, int timeId, String content, int type) {
            LinearLayout left = this.getView(leftLayoutId);
            LinearLayout right = this.getView(rightLayoutId);
            TextView leftText = this.getView(leftId);
            ImageView leftPic = this.getView(picLeftId);
            TextView rightText = this.getView(rightId);
            ImageView rightPic = this.getView(picRightId);
            TextView time = this.getView(timeId);
            RelativeLayout rlLeft = this.getView(pbLeftId);
            RelativeLayout rlRight = this.getView(pbRightId);
            if (type == CommonUtil.TYPE_LEFT) {
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                leftPic.setVisibility(View.GONE);
                rlLeft.setVisibility(View.GONE);
                leftText.setVisibility(View.VISIBLE);
                leftText.setText(content);
            } else if (type == CommonUtil.TYPE_PICLEFT) {
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                leftText.setVisibility(View.GONE);
                if (bitmap == null) {
                    rlLeft.setVisibility(View.VISIBLE);
                    leftPic.setVisibility(View.GONE);
                } else {
                    leftPic.setVisibility(View.VISIBLE);
                    rlLeft.setVisibility(View.GONE);
                    leftPic.setImageBitmap(bitmap);
                }
            } else if (type == CommonUtil.TYPE_RIGHT) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);
                time.setVisibility(View.GONE);
                rightText.setVisibility(View.VISIBLE);
                rlRight.setVisibility(View.GONE);
                rightPic.setVisibility(View.GONE);
                rightText.setText(content);
            } else if (type == CommonUtil.TYPE_PICRIGHT) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.VISIBLE);
                time.setVisibility(View.GONE);
                rightText.setVisibility(View.GONE);
                if (bitmap == null) {
                    rlRight.setVisibility(View.VISIBLE);
                    rightPic.setVisibility(View.GONE);
                } else {
                    rlRight.setVisibility(View.GONE);
                    rightPic.setVisibility(View.VISIBLE);
                    rightPic.setImageBitmap(bitmap);
                }
            } else if (type == CommonUtil.TYPE_TIME) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
                time.setText(content);
            }
            return this;
        }


        public MyViewHolder setSendErrorListener(int errorType) {
            ImageView imageView = this.getView(R.id.iv_chat_error);
            ProgressBar progressBar = this.getView(R.id.pb_chat_loading);
            if (errorType == CommonUtil.SEND_ERROR) {
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else if (errorType == CommonUtil.SEND_LOAD) {
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            } else if (errorType == CommonUtil.SEND_SUCCESS) {
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            return this;
        }
    }

    public abstract void assignment(MyViewHolder viewHolder, int position);

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
