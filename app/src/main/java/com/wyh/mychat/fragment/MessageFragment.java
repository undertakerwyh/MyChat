package com.wyh.mychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wyh.mychat.R;
import com.wyh.mychat.activity.TalkActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.entity.User;
import com.wyh.mychat.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/20.
 */

public class MessageFragment extends Fragment {
    @Bind(R.id.lv_message)
    ListView lvMessage;
    private View view;
    private UniversalAdapter<User>adapter;
    private List<User>list = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        User user1 = new User(null,"Apple","什么东西", CommonUtil.getTime(System.currentTimeMillis()));
        User user2 = new User(null,"Sina","This is a apple", CommonUtil.getTime(System.currentTimeMillis()));
        User user3 = new User(null,"Enia","Hello", CommonUtil.getTime(System.currentTimeMillis()));
        User user4 = new User(null,"Benana","I created a new avatar instead of the old one", CommonUtil.getTime(System.currentTimeMillis()));
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        /**初始化适配器*/
        initAdapter();
        lvMessage.setAdapter(adapter);
        /**向适配器添加数据*/
        adapter.addDataAddAll(list);
        return view;
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<User>(getContext(),R.layout.chat_item_title) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final User user = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_chat_name,user.getName())
                        .setTextViewContent(R.id.tv_chat_lastContent,user.getContent())
                        .setTextViewContent(R.id.tv_chat_data,user.getTime())
                        .setImageViewContent(R.id.iv_chat_headPortrait,R.drawable.headportrait)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TalkActivity.class);
                        intent.putExtra("name",user.getName());
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
