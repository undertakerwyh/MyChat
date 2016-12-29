package com.wyh.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.ShowPicActivity;
import com.wyh.mychat.activity.ShowSrcActivity;
import com.wyh.mychat.adapter.RecyclerViewAdapter;
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.entity.Picture;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/19.
 */

public class ResourceFragment extends Fragment implements LoadManager.ResourceUpdate {
    @Bind(R.id.lv_folder)
    RecyclerView lvResource;
    private View view;

    public RecyclerViewAdapter<Picture> getAdapter() {
        return adapter;
    }

    private static RecyclerViewAdapter<Picture> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pic, null);
        ButterKnife.bind(this, view);
        initAdapter();
        lvResource.setLayoutManager(new GridLayoutManager(getContext(),3, LinearLayoutManager.VERTICAL,false));
        lvResource.setAdapter(adapter);
        return view;
    }

    private Handler handler = new Handler();

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new RecyclerViewAdapter<Picture>(getContext(), R.layout.layout_recycler_pic) {
            @Override
            public void assignment(MyViewHolder viewHolder, int position) {
                final Picture picture = adapter.getDataList().get(position);
                viewHolder.setImageViewContent(R.id.iv_pic_icon, picture.getFile())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShowPicActivity.class);
                                intent.putExtra("PicFile",picture.getFile().getAbsolutePath());
                                intent.putExtra("FromClass",((ShowSrcActivity)getActivity()).getFromClass());
                                intent.putExtra("PicName",picture.getName());
                                getActivity().startActivity(intent);
                            }
                        });
            }
        };
    }

    public static void initList() {
        adapter.getDataList().clear();
    }

    private boolean isEnter=true;
    /**
     * 更新回调接口传来的值
     *
     * @param picture 图片文件的实体类
     */
    public void refresh(final Picture picture) {
        adapter.addDataNotUpdate(picture);
        if(isEnter){
            isEnter = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isEnter = true;
                }
            },500);
        }
    }

    /**
     * 显示指定文件夹下的图片的fragment
     */
    public void showResource(String name) {
        adapter.getDataList().clear();
        LoadManager.getPicLoadManager(getContext()).isStop(false);
        LoadManager.getPicLoadManager(getContext()).setResourceUpdate(this);
        LoadManager.getPicLoadManager(getContext()).getResource(new File(name));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 更新搜索图片的结果
     */
    @Override
    public void resourceUpdate(Picture picture) {
        refresh(picture);
    }

}
