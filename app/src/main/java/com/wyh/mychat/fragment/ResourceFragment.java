package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wyh.mychat.R;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.entity.Picture;

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
    private View view;

    private Handler handler = new Handler(Looper.getMainLooper());

    public UniversalAdapter<Picture> getAdapter() {
        return adapter;
    }

    private UniversalAdapter<Picture>adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pic, null);
        ButterKnife.bind(this, view);
        initAdapter();
        lvResource.setAdapter(adapter);
        return view;
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<Picture>(getContext(),R.layout.layout_pic_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                Picture picture = adapter.getDataList().get(positon);
                viewHolder.setImageViewContent(R.id.iv_pic_icon,picture.getBitmap())
                        .setTextViewContent(R.id.tv_folder_text,picture.getName());
            }
        };
    }
    private boolean enter = true;

    private List<Picture>list = new ArrayList<>();

    public void clearList(){
        list.clear();
    }

    public void refresh(Picture picture) {
        list.add(picture);
        if(enter){
            enter = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enter = true;
                    adapter.addDataAddAll(list);
                    list.clear();
                }
            },500);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
