package com.bt.andy.gainstrong.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bt.andy.gainstrong.R;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 16:41
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Home_F extends Fragment {
    private View mRootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.home_f, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {

    }

    private void initData() {
        TotalGoodsFragment totalFgm = new TotalGoodsFragment();
        FragmentTransaction ftt = getFragmentManager().beginTransaction();
        ftt.add(R.id.frame_home, totalFgm, "totalFgm");
        ftt.commit();
    }
}
