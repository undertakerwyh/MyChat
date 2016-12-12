package com.wyh.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wyh.mychat.R;
import com.wyh.mychat.entity.Picture;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowPicActivity extends AppCompatActivity {

    @Bind(R.id.iv_show_pictrue)
    ImageView ivShowPictrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Picture picture = (Picture) intent.getSerializableExtra("picture");
        ivShowPictrue.setImageBitmap(picture.getBitmap());
    }
}
