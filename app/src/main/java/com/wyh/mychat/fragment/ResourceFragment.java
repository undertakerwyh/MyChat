package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.ShowSrcActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.entity.Picture;
import com.wyh.mychat.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/19.
 */

public class ResourceFragment extends Fragment {
    @Bind(R.id.lv_folder)
    ListView lvResource;
    @Bind(R.id.pb_load)
    ProgressBar pbLoad;
    private View view;

    public UniversalAdapter<Picture> getAdapter() {
        return adapter;
    }

    private UniversalAdapter<Picture> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pic, null);
        ButterKnife.bind(this, view);
        initAdapter();
        lvResource.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public Handler getHandler() {
        if(handler==null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            pbLoad.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            pbLoad.setVisibility(View.GONE);
                            break;
                    }
                }
            };
        }
        return handler;
    }

    private Handler handler;

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new UniversalAdapter<Picture>(getContext(), R.layout.layout_pic_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                Picture picture = adapter.getDataList().get(positon);
                viewHolder.setImageViewContent(R.id.iv_pic_icon, picture.getBitmap())
                        .setTextViewContent(R.id.tv_folder_text, picture.getName());
            }
        };
    }

    private boolean enter = true;

    private List<Picture> list = new ArrayList<>();

    public void clearList() {
        list.clear();
    }

    /**
     * 更新回调接口传来的值
     *
     * @param picture 图片文件的实体类
     */
    public void refresh(Picture picture) {
        list.add(picture);
        if (enter) {
            enter = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enter = true;
                    adapter.addDataAddAll(list);
                    ((ShowSrcActivity)getActivity()).setActionText(CommonUtil.folderName(ShowSrcActivity.getFolderName())+"("+adapter.getDataList().size()+")");
                    list.clear();
                }
            }, 500);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
