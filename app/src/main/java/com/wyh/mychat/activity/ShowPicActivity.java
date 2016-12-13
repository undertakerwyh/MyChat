package com.wyh.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wyh.mychat.R;
import com.wyh.mychat.base.BaseActivity;
import com.wyh.mychat.fragment.ConfigFragment;
import com.wyh.mychat.util.BitmapUtil;
import com.wyh.mychat.util.CommonUtil;
import com.wyh.mychat.view.ActionBar;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowPicActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_show_pictrue)
    ImageView ivShowPictrue;
    @Bind(R.id.action_bar)
    ActionBar actionBar;
    @Bind(R.id.cb_show_pic)
    CheckBox cbShowPic;
    @Bind(R.id.btn_pic_send)
    Button btnPicSend;
    @Bind(R.id.ll_show_pic)
    LinearLayout llShowPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        File picFile = new File(intent.getStringExtra("PicFile"));
        String FromClass = intent.getStringExtra("FromClass");
        String name = intent.getStringExtra("PicName");
        if(ConfigFragment.class.getSimpleName().equals(FromClass)){
            llShowPic.setVisibility(View.GONE);
        }else{
            llShowPic.setVisibility(View.VISIBLE);
        }
        initActionBar(name,R.drawable.back,-1,this);
        cbShowPic.setText("原图("+ CommonUtil.getFileSize(picFile.length())+")");
        ivShowPictrue.setImageBitmap(BitmapUtil.getBigBitmap(picFile.getAbsolutePath()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_actionbar_left:
                finish();
                break;
        }
    }
}
