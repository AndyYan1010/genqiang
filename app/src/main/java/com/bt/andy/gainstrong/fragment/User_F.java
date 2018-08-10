package com.bt.andy.gainstrong.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bt.andy.gainstrong.MyAppliaction;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.activity.LoginActivity;
import com.bt.andy.gainstrong.activity.meActivity.SaleFormActivity;
import com.bt.andy.gainstrong.utils.ToastUtils;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 16:42
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class User_F extends Fragment implements View.OnClickListener {
    private View           mRootView;
    private ImageView      mImg_head;//头像
    private TextView       mTv_name;//员工姓名
    private TextView       mTv_number;//员工号
    private RelativeLayout mRelative_set;//设置条目
    private RelativeLayout mRelative_msg;//消息
    private RelativeLayout mRelative_form;//销售报表
    private RelativeLayout mRelative_exit;//退出

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.user_f, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mImg_head = mRootView.findViewById(R.id.img_head);
        mTv_number = mRootView.findViewById(R.id.tv_number);
        mTv_name = mRootView.findViewById(R.id.tv_name);
        mRelative_set = mRootView.findViewById(R.id.relative_set);
        mRelative_msg = mRootView.findViewById(R.id.relative_msg);
        mRelative_form = mRootView.findViewById(R.id.relative_form);
        mRelative_exit = mRootView.findViewById(R.id.relative_exit);
    }

    private void initData() {
        mRelative_set.setOnClickListener(this);
        mRelative_msg.setOnClickListener(this);
        mRelative_form.setOnClickListener(this);
        mRelative_exit.setOnClickListener(this);
        mTv_name.setText("姓名：" + MyAppliaction.userName);
        mTv_number.setText("工号：" + MyAppliaction.memID);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_set:
                ToastUtils.showToast(getContext(), "暂未开放");
                break;
            case R.id.relative_msg:
                ToastUtils.showToast(getContext(), "暂未开放");
                break;
            case R.id.relative_form:
                Intent intent = new Intent(getContext(), SaleFormActivity.class);
                startActivity(intent);
                break;
            case R.id.relative_exit:
                exitLogin();
                break;
        }
    }

    private void exitLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定要退出当前登录帐号吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(getContext(), LoginActivity.class);
                startActivity(intent);
                ((Activity) getContext()).finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }
}
