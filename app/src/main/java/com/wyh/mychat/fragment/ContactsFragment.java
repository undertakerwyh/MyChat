package com.wyh.mychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.wyh.mychat.R;
import com.wyh.mychat.activity.TalkActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ContactsFragment extends Fragment {
    @Bind(R.id.lv_contacts)
    ListView lvContacts;
    private View view;
    private List<String> list = new ArrayList<>();
    private UniversalAdapter<String> adapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, null);
        ButterKnife.bind(this, view);
        /**初始化适配器*/
        initAdapter();
        lvContacts.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            list = EMContactManager.getInstance().getContactUserNames();
            adapter.addDataAddAll(list);
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new UniversalAdapter<String>(getContext(),R.layout.layout_friends_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final String friends = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_friends_name,friends)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), TalkActivity.class);
                                intent.putExtra("name",friends);
                                getActivity().startActivity(intent);
                            }
                        });
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
