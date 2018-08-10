package com.bt.andy.gainstrong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.messegeInfo.SaleFormInfo;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/13 9:12
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvFormAdapter extends BaseAdapter {
    private List<SaleFormInfo> mList;
    private Context            mContext;

    public LvFormAdapter(Context context, List<SaleFormInfo> list) {
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
            view = View.inflate(mContext, R.layout.lv_item_form, null);
            viewHolder = new MyViewHolder();
            viewHolder.tv_billno = view.findViewById(R.id.tv_billno);
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_unit_price = view.findViewById(R.id.tv_unit_price);
            viewHolder.tv_num = view.findViewById(R.id.tv_num);
            viewHolder.tv_total = view.findViewById(R.id.tv_total);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        SaleFormInfo saleFormInfo = mList.get(i);
        String fbillno = saleFormInfo.getFbillno();
        String name = saleFormInfo.getName();
        String unit_price = saleFormInfo.getUnit_price();
        String num = saleFormInfo.getNum();
        String sum = saleFormInfo.getSum();
        viewHolder.tv_billno.setText(fbillno);
        viewHolder.tv_name.setText(name);
        viewHolder.tv_unit_price.setText(unit_price);
        viewHolder.tv_num.setText("x" + num);
        viewHolder.tv_total.setText(sum);
        return view;
    }

    private class MyViewHolder {
        TextView tv_billno, tv_name, tv_unit_price, tv_num, tv_total;
    }
}
