package com.wyh.mychat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.util.CommonUtil;

/**
 * listview万能适配器
 */
public class ViewHolder {
    private View mView;
    private SparseArray<View> views;

    public ViewHolder(Context context, int layoutRes, ViewGroup parent) {
        views = new SparseArray<>();
        mView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        mView.setTag(this);
    }

    public <DataType extends View> DataType getView(int resId) {
        View view = views.get(resId);
        if (view == null) {
            view = mView.findViewById(resId);
            views.put(resId, view);
        }
        return (DataType) view;
    }

    public static ViewHolder getViewHolder(View convertView, Context context, int layoutRes, ViewGroup parent) {
        if (convertView == null) {
            return new ViewHolder(context, layoutRes, parent);
        }
        return (ViewHolder) convertView.getTag();
    }

    public View getmView() {
        return mView;
    }

    public ViewHolder setImageViewContent(int ViewId, int resId) {
        ImageView imageView = this.getView(ViewId);
        imageView.setImageResource(resId);
        return this;
    }

    public ViewHolder setTextViewContent(int ViewId, String content) {
        TextView textView = this.getView(ViewId);
        textView.setText(content);
        return this;
    }

    public ViewHolder setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setViewOnClickListener(int ViewId,View.OnClickListener listener){
        View view = this.getView(ViewId);
        view.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setImageViewContent(int ViewId, Drawable icon) {
        ImageView imageView = this.getView(ViewId);
        imageView.setBackground(icon);
        return this;
    }

    public ViewHolder setLayoutVisivity(int ViewId, boolean isShow) {
        View view = this.getView(ViewId);
        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public ViewHolder setCheckBoxContent(int ViewId, boolean isSelected) {
        CheckBox checkBox = this.getView(ViewId);
        checkBox.setChecked(isSelected);
        return this;
    }

    public ViewHolder setOnCheckedChangeListener(int ViewId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        CheckBox checkBox = this.getView(ViewId);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        return this;
    }

    public ViewHolder setImageViewContent(int ViewId, Bitmap bitmap) {
        ImageView imageView = this.getView(ViewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setListviewOnitemListener(ListView listView, AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
        return this;
    }

    public ViewHolder setTextColorContent(int ViewId, int color) {
        TextView textView = this.getView(ViewId);
        textView.setTextColor(color);
        return this;
    }

    public ViewHolder setLongClickListener(View.OnLongClickListener listener) {
        mView.setOnLongClickListener(listener);
        return this;
    }

    public ViewHolder setTouchListener(View.OnTouchListener onTouchListener) {
        mView.setOnTouchListener(onTouchListener);
        return this;
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------------------
     * 以下是自定义方法
     */



    public ViewHolder setChatVisible(int leftLayoutId, int rightLayoutId, int leftId, int rightId, int timeId, String content, int type) {
        LinearLayout left = this.getView(leftLayoutId);
        LinearLayout right = this.getView(rightLayoutId);
        TextView time = this.getView(timeId);
        if (type == CommonUtil.TYPE_LEFT) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            TextView leftText = this.getView(leftId);
            leftText.setText(content);
        } else if (type == CommonUtil.TYPE_RIGHT) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);
            time.setVisibility(View.GONE);
            TextView rightText = this.getView(rightId);
            rightText.setText(content);
        } else if (type == CommonUtil.TYPE_TIME) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            time.setText(content);
        }
        return this;
    }



    public ViewHolder setSendErrorListener(int errorType){
        ImageView imageView = this.getView(R.id.iv_chat_error);
        ProgressBar progressBar = this.getView(R.id.pb_chat_loading);
        if(errorType==CommonUtil.SEND_ERROR){
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else if(errorType==CommonUtil.SEND_LOAD){
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }else if(errorType==CommonUtil.SEND_SUCCESS){
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        return this;
    }
}
