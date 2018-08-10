package com.bt.andy.gainstrong.activity.meActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.andy.gainstrong.BaseActivity;
import com.bt.andy.gainstrong.R;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/23 9:27
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImg_back;
    private TextView mTv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initView() {
        mImg_back = findViewById(R.id.img_back);
        mTv_title = findViewById(R.id.tv_title);
    }

    private void initData() {
        mImg_back.setVisibility(View.VISIBLE);
        mImg_back.setOnClickListener(this);
        mTv_title.setText("设置");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
}
