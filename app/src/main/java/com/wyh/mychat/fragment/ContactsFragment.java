package com.wyh.mychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.wyh.mychat.R;
import com.wyh.mychat.activity.HomeActivity;
import com.wyh.mychat.activity.TalkActivity;
import com.wyh.mychat.adapter.UniversalAdapter;
import com.wyh.mychat.adapter.ViewHolder;
import com.wyh.mychat.biz.UserManager;
import com.wyh.mychat.view.ListViewBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ContactsFragment extends Fragment implements ListViewBar.ListViewBarListener, HomeActivity.ContactListener {
    @Bind(R.id.lv_contacts)
    ListView lvContacts;
    private View view;
    private List<String> list = new ArrayList<>();
    private UniversalAdapter<String> adapter;
    private ListViewBar listViewBar;
    private String deleName;

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
            if(UserManager.getUserManager(getContext()).isFirstAdd()) {
                Log.e("AAA","enter");
                UserManager.getUserManager(getContext()).saveFriendList(EMContactManager.getInstance().getContactUserNames());
            }
            adapter.addDataAddAll(UserManager.getUserManager(getContext()).loadFriendList());
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.pop_contacts_delete));
        listViewBar = new ListViewBar(getContext(), list, this);
        ((HomeActivity) getActivity()).setContactListener(this);
    }

    private int eventX;
    private int eventY;

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        adapter = new UniversalAdapter<String>(getContext(), R.layout.layout_friends_item) {
            @Override
            public void assignment(ViewHolder viewHolder, int positon) {
                final String friends = adapter.getDataList().get(positon);
                viewHolder.setTextViewContent(R.id.tv_friends_name, friends)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), TalkActivity.class);
                                intent.putExtra("name", friends);
                                getActivity().startActivity(intent);
                            }
                        }).setLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        initPopWindow(v);
                        deleName = friends;
                        return false;
                    }
                }).setTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        eventX = (int) event.getX();
                        eventY = (int) event.getY();
                        return false;
                    }
                });
            }
        };
    }

    private void initPopWindow(View view) {
        listViewBar.showAsDropDown(view, eventX, eventY - view.getMeasuredHeight());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onComplete(String name) {
        if (name.equals(getString(R.string.pop_contacts_delete))) {
            try {
                EMContactManager.getInstance().deleteContact(deleName);
                UserManager.getUserManager(getContext()).deleteFriendName(deleName);
                updateList();
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList() {
        ((HomeActivity) getActivity()).getHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> contactUserNames = EMContactManager.getInstance().getContactUserNames();
                    adapter.addDataAllNotify(contactUserNames);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void refresh() {
        updateList();
    }

    @Override
    public void added(final List<String> usernameList) {
        ((HomeActivity) getActivity()).getHandler().post(new Runnable() {
            @Override
            public void run() {
                adapter.addDataAllNotify(usernameList);
            }
        });
    }
}