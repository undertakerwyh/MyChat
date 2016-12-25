package com.wyh.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.util.BitmapUtil;
import com.wyh.mychat.util.CommonUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowPicActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_show_pictrue)
    ImageView ivShowPictrue;
    @Bind(R.id.cb_show_pic)
    CheckBox cbShowPic;
    @Bind(R.id.btn_pic_send)
    Button btnPicSend;
    @Bind(R.id.ll_show_pic)
    LinearLayout llShowPic;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private File picFile;

    public static void setPicSendListener(PicSendListener picSendListener) {
        ShowPicActivity.picSendListener = picSendListener;
    }

    private static PicSendListener picSendListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        picFile = new File(intent.getStringExtra("PicFile"));
        String FromClass = intent.getStringExtra("FromClass");
        String name = intent.getStringExtra("PicName");
        if (ConfigFragment.class.getSimpleName().equals(FromClass)) {
            llShowPic.setVisibility(View.GONE);
        } else {
            llShowPic.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);
        toolbarTitle.setText(name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cbShowPic.setText("原图(" + CommonUtil.getFileSize(picFile.length()) + ")");
        ivShowPictrue.setImageBitmap(BitmapUtil.getBigBitmap(picFile.getAbsolutePath()));
    }

    @OnClick({R.id.btn_pic_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pic_send:
                boolean isOriginal = cbShowPic.isChecked();
                picSendListener.sendPic(picFile, isOriginal);
                startActivity(TalkActivity.class);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface PicSendListener {
        void sendPic(File picFile, boolean isOriginal);
    }
}
