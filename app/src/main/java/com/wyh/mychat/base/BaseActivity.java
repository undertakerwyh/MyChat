package com.wyh.mychat.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.wyh.mychat.R;
import com.wyh.mychat.biz.UserManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class BaseActivity extends AppCompatActivity {
    private static List<AppCompatActivity> activities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }


    /**
     * 清除所有的活跃activity
     */
    public void finishAll() {
        Iterator<AppCompatActivity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            iterator.next().finish();
        }
    }

    private ProgressDialog progressDialog = null;

    public void showWaitBar(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(getString(R.string.dialog_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void showAddFriend(final Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(getResources().getString(R.string.pop_friend_title));
        View view = LayoutInflater.from(context).inflate(R.layout.pop_search_friend, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_friend_name);
        dialog.setView(view);
        dialog.setNegativeButton(getString(R.string.setting_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (!UserManager.getUserManager(getApplicationContext()).isUserName(name)) {
                        if (!UserManager.getUserManager(getApplicationContext()).isFriendExist(name)) {
                            try {
                                EMContactManager.getInstance().addContact(name, null);//需异步处理
                                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "已是好友", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "不能对自己帐号发送请求", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setNeutralButton(getString(R.string.setting_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dismissWaitBar() {
        progressDialog.dismiss();
    }


    /**
     * Handler
     */
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myHandlerMessage(msg);
        }
    };

    protected void myHandlerMessage(Message message) {

    }

    /**
     * 启动指定activity
     */
    public void startActivity(Class<?> TargetActivity) {
        Intent intent = new Intent(this, TargetActivity);
        startActivity(intent);
    }

    /**
     * 启动指定activity和切换动画
     */
    public void startActivity(Class<?> TargetActivity, int animStart, int animEnd) {
        Intent intent = new Intent(this, TargetActivity);
        startActivity(intent);
        overridePendingTransition(animStart, animEnd);
    }
}
