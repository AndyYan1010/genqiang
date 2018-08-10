package com.bt.andy.gainstrong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.andy.gainstrong.R;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/24 14:42
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MySpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private List    mList;

    public MySpinnerAdapter(Context context, List list) {
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
        MyViewHolder myViewHolder;
        if (null == view) {
            myViewHolder = new MyViewHolder();
            view = View.inflate(mContext, R.layout.spinner_item_kind, null);
            myViewHolder.kind = view.findViewById(R.id.tv_kind);
            view.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) view.getTag();
        }
        if (i == 0) {
            myViewHolder.kind.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            myViewHolder.kind.setTextColor(mContext.getResources().getColor(R.color.blue_100));
        }
        myViewHolder.kind.setText(String.valueOf(mList.get(i)));
        return view;
    }

    private class MyViewHolder {
        TextView kind;
    }
}
