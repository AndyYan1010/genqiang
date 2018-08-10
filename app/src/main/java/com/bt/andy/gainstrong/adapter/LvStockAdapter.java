package com.bt.andy.gainstrong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.messegeInfo.StockInfo;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/27 13:44
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvStockAdapter extends BaseAdapter {
    private Context         mContext;
    private List<StockInfo> mList;

    public LvStockAdapter(Context context, List<StockInfo> list) {
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
            view = View.inflate(mContext, R.layout.lv_item_stock, null);
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_address = view.findViewById(R.id.tv_address);
            viewHolder.tv_num = view.findViewById(R.id.tv_num);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        StockInfo stockInfo = mList.get(i);
        viewHolder.tv_name.setText(stockInfo.getFname());
        viewHolder.tv_address.setText("仓库："+stockInfo.getAddress());
        viewHolder.tv_num.setText("库存数量：" + stockInfo.getFqty());
        return view;
    }

    class MyViewHolder {
        TextView tv_name, tv_address, tv_num;
    }
}
