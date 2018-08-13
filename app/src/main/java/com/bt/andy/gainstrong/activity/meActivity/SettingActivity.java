package com.bt.andy.gainstrong.activity.meActivity;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.andy.gainstrong.BaseActivity;
import com.bt.andy.gainstrong.MyAppliaction;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.SPUtils;
import com.bt.andy.gainstrong.utils.ToastUtils;

import java.util.Locale;

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
    private TextView  mTv_title;
    private TextView  tv_cglang;

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
        tv_cglang = (TextView) findViewById(R.id.tv_cglang);
    }

    private void initData() {
        mImg_back.setVisibility(View.VISIBLE);
        mImg_back.setOnClickListener(this);
        mTv_title.setText(getResources().getString(R.string.setting));
        tv_cglang.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_cglang:
                //单选对话框
                singleLanguage();
                break;
        }
    }

    private int checkedItemId = -1;

    //单选对话框/选择语言
    private void singleLanguage() {
        final String[] item = {"中文", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.language); //设置图标
        builder.setTitle("请选择语言");
        builder.setSingleChoiceItems(item, checkedItemId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//-1不选择,
                checkedItemId = i;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Configuration config = getResources().getConfiguration();
                Resources resources = getResources();
                if (checkedItemId == 0) {
                    config.locale = Locale.CHINESE;//切换中文
                    SPUtils.put(SettingActivity.this, Consts.LAUGAGE, "0");
                    MyAppliaction.YUYAN=0;
                    ToastUtils.showToast(SettingActivity.this,"选择了中文");
                } else {
                    config.locale = Locale.ENGLISH;//切换英文
                    SPUtils.put(SettingActivity.this,Consts.LAUGAGE, "1");
                    MyAppliaction.YUYAN=1;
                    ToastUtils.showToast(SettingActivity.this,"选择了英文");
                }
                DisplayMetrics dm = resources.getDisplayMetrics();
                resources.updateConfiguration(config, dm);
            }
        });
        builder.show();
    }
}

