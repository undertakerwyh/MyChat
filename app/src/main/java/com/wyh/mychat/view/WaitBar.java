package com.wyh.mychat.view;

import android.app.ProgressDialog;
import android.content.Context;

import com.wyh.mychat.R;

/**
 * Created by Administrator on 2016/12/6.
 */

public class WaitBar extends ProgressDialog {
    private Context context;

    public WaitBar(Context context) {
        super(context);
    }

    public void showBar() {
        this.setContentView(R.layout.dialog_wait);
        this.setCancelable(false);
        this.show();
    }
}
