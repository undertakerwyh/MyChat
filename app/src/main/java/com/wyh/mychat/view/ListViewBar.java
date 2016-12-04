package com.wyh.mychat.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wyh.mychat.R;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class ListViewBar extends PopupWindow {
    private Context context;
    private View parentView;
    private ListView lv;
    private UniversalAdapter<String>adapter;
    private ListViewBarListener listViewBarListener;
    private List<String>list;
    public ListViewBar(Context context, List<String>list,ListViewBarListener listViewBarListener){
        this.context = context;
        this.listViewBarListener = listViewBarListener;
        this.list = list;
        initView();
    }

    private void initView() {
        parentView = LayoutInflater.from(context).inflate(R.layout.layout_scroll,null);
        setContentView(parentView);
        lv = (ListView) parentView.findViewById(R.id.lv_list);
        //设置弹出窗体的高
        this.setWidth((int) context.getResources().getDimension(R.dimen.config_item_width));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));

        update();
        adapter = new UniversalAdapter<String>(context,R.layout.layout_scroll_list) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final String content = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_listviewbar_text,content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listViewBarListener.onComplete(content);
                        ListViewBar.this.dismiss();
                    }
                });
            }
        };
        adapter.addDataAddAll(list);
        lv.setAdapter(adapter);
    }



    public interface ListViewBarListener{
        void onComplete(String name);
    }
}
