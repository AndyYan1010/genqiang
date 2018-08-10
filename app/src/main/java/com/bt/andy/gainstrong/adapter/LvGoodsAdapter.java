package com.bt.andy.gainstrong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.messegeInfo.SubtableInfo;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/25 15:17
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvGoodsAdapter extends BaseAdapter {
    private Context            mContext;
    private List<SubtableInfo> mList;

    public LvGoodsAdapter(Context context, List<SubtableInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder viewHolder;
        if (null == view) {
            viewHolder = new MyViewHolder();
            view = View.inflate(mContext, R.layout.lv_item_goods_info, null);
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_unit_price = view.findViewById(R.id.tv_unit_price);
            viewHolder.tv_num = view.findViewById(R.id.tv_num);
            viewHolder.tv_total = view.findViewById(R.id.tv_total);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        SubtableInfo subtableInfo = mList.get(i);
        String goodsName = subtableInfo.getGoodsName();
        double zh_unit_price = subtableInfo.getZh_unit_price();
        int number = subtableInfo.getNumber();
        double sum_pric = subtableInfo.getSum_pric();
        viewHolder.tv_name.setText(goodsName);
        viewHolder.tv_unit_price.setText("¥" + zh_unit_price);
        viewHolder.tv_num.setText("x" + number);
        viewHolder.tv_total.setText("¥" + sum_pric);
        return view;
    }

    private class MyViewHolder {
        TextView tv_name, tv_unit_price, tv_num, tv_total;
    }
}
