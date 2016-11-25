package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.os.Environment;
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
import com.wyh.mychat.biz.LoadManager;
import com.wyh.mychat.util.CommonUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class FolderFragment extends Fragment implements LoadManager.FileUpdate {

    @Bind(R.id.lv_folder)
    ListView lvFolders;
    @Bind(R.id.pb_load)
    ProgressBar pbLoad;
    private View view;

    private UniversalAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder, null);
        ButterKnife.bind(this, view);
        initAdapter();
        lvFolders.setAdapter(adapter);
        return view;
    }


    public Handler getHandler() {
        if (handler == null) {
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * 更新回调接口传来的值
     *
     * @param name 传入的值
     */
    public void refresh(final String name) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.addDataUpdate(name);
                ((ShowSrcActivity) getActivity()).setActionText(getResources().getString(R.string.my_picture) + "(" + adapter.getDataList().size() + ")");
            }
        });
    }

    private void showFolder() {
        adapter.getDataList().clear();
        getHandler().sendEmptyMessage(0);
        LoadManager.getPicLoadManager(getContext()).isStop(false);
        File sdFile = Environment.getExternalStorageDirectory();
        LoadManager.getPicLoadManager(getContext()).setFileUpdate(this);
        LoadManager.getPicLoadManager(getContext()).getSrcList(sdFile);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new UniversalAdapter<String>(getContext(), R.layout.layout_pic_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final String folderName = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_folder_text, CommonUtil.folderName(folderName))
                        .setImageViewContent(R.id.iv_pic_icon, R.drawable.folder)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((ShowSrcActivity) getActivity()).setActionText(CommonUtil.folderName(folderName));
                                ((ShowSrcActivity) getActivity()).showResource(folderName);
                                ShowSrcActivity.setFolderName(folderName);
                            }
                        });
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showFolder();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 更新搜索文件夹结果
     */
    @Override
    public void update(final String folder) {
        refresh(folder);
    }

    /**
     * 搜索结束加载动画结束
     */
    @Override
    public void fileEnd() {
        handler.sendEmptyMessage(1);
    }
}
