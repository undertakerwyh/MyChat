package com.wyh.mychat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.ShowSrcActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.util.CommonUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/18.
 */

public class FolderFragment extends Fragment {

    @Bind(R.id.lv_folder)
    ListView lvPic;
    private View view;

    private UniversalAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder, null);
        ButterKnife.bind(this, view);
        initAdapter();
        lvPic.setAdapter(adapter);
        ShowSrcActivity.setMcurrentFragment(this);
        return view;
    }
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public void refresh(final String name){
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addDataAll(name);
                    }
                });
            }
        });
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<String>(getContext(), R.layout.layout_pic_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                String folderName = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_folder_text, CommonUtil.folderName(folderName))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("AAA", "onclick");
//                                ((ShowSrcActivity)getActivity()).showPicFragment();
                            }
                        });
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
