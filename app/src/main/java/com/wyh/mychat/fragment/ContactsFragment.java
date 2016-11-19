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
import com.wyh.mychat.entity.Friends;

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
    private List<Friends> list = new ArrayList<>();
    private UniversalAdapter<Friends> adapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        for (int i = 0; i < 30; i++) {
            list.add(new Friends(null,"联系人"+ i));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, null);
        ButterKnife.bind(this, view);
        /**初始化适配器*/
        initAdapter();
        lvContacts.setAdapter(adapter);
        adapter.addDataAddAll(list);
        return view;
    }

    private void initAdapter() {
        adapter = new UniversalAdapter<Friends>(getContext(),R.layout.layout_friends_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final Friends friends = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_friends_name,friends.getName())
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), TalkActivity.class);
                                intent.putExtra("name",friends.getName());
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
