package com.bt.andy.gainstrong.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.andy.gainstrong.BaseActivity;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.ProgressDialogUtil;
import com.bt.andy.gainstrong.utils.SoapUtil;
import com.bt.andy.gainstrong.utils.ToastUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/24 8:45
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class GoodsDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mImg_back;
    private TextView  mTv_title;
    private ImageView img_noInt;
    private int resultCode = 9527;//设置商品详情返回参数对应码
    private TextView mTv_name1;//商品名
    private TextView mTv_name2;//商品名
    private TextView mTv_unit_price;//单价
    private TextView mTv_stock;//库存
    private TextView tv_useful;//可用库存
    private TextView mTv_reduce;//减法
    private EditText mEdit_num;//购买数量
    private TextView mTv_add;//加法
    private TextView mTv_unit;//商品单位
    private EditText mEdit_discount;//折后价
    private TextView mTv_sumprice;//子单总金额
    private Button   mBt_sure;//确认
    private EditText mEdit_remarks;//备注
    private Button   mBt_submit;//确定下单
    private double goods_price  = 1000;//折后单价
    private String goodsLocalId = "";
    private String mGoodsId;
    private String mFunitid;//单位的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = getIntent();
        mGoodsId = intent.getStringExtra("goodsId");
        setView();
        setData();
    }

    private void setView() {
        mImg_back = findViewById(R.id.img_back);
        mTv_title = findViewById(R.id.tv_title);
        img_noInt = (ImageView) findViewById(R.id.img_noInt);
        mTv_name1 = findViewById(R.id.tv_name1);
        mTv_unit_price = findViewById(R.id.tv_unit_price);
        mTv_stock = findViewById(R.id.tv_stock);
        tv_useful = (TextView) findViewById(R.id.tv_useful);
        mTv_name2 = findViewById(R.id.tv_name2);
        mTv_reduce = findViewById(R.id.tv_reduce);
        mEdit_num = findViewById(R.id.edit_num);
        mTv_add = findViewById(R.id.tv_add);
        mTv_unit = findViewById(R.id.tv_unit);
        mEdit_discount = findViewById(R.id.edit_discount);
        mTv_sumprice = findViewById(R.id.tv_sumprice);
        mBt_sure = findViewById(R.id.bt_sure);
        mEdit_remarks = findViewById(R.id.edit_remarks);
        mBt_submit = findViewById(R.id.bt_submit);
    }

    private void setData() {
        mImg_back.setVisibility(View.VISIBLE);
        mImg_back.setImageResource(R.drawable.back);
        mImg_back.setOnClickListener(this);
        mTv_title.setText("商品详情");
        ProgressDialogUtil.startShow(GoodsDetailActivity.this, "正在加载，请稍等...");
        //访问网络，获取详情
        //根据扫描的代码查询
        String sql = "select top 1 isnull(d.fprice,0) fprice,a.fitemid,a.funitid,a.fname,a.FSalePrice,isnull(sum(b.FQty),0) FQty,c.fname funit from t_icitem a left join ICInventory b on a.fitemid=b.fitemid left join t_MeasureUnit c on c.fitemid=a.FUnitID left join (select FPrice,FItemID,FBegDate from ICPrcPlyEntry ) d on a.fitemid=d.fitemid where a.fnumber='" + mGoodsId + "' group by a.fname,a.FSalePrice,c.fname,a.fitemid,a.funitid,d.fprice,d.FBegDate order by d.FBegDate desc";
        //根据助记码或者名称模糊查询
        //String sql = "select a.fitemid,a.funitid,a.fname,a.FSalePrice,isnull(sum(b.FQty),0) FQty,c.fname funit from t_icitem a left join ICInventory b on a.fitemid=b.fitemid left join t_MeasureUnit c on c.fitemid=a.FUnitID where a.FHelpCode like'%" + mGoodsId + "%' or a.fname like '%" + mGoodsId + "%' group by a.fname,a.FSalePrice,c.fname,a.fitemid,a.funitid";
        new ItemTask(sql).execute();

        mTv_reduce.setOnClickListener(this);
        mTv_add.setOnClickListener(this);
        mBt_sure.setOnClickListener(this);
        mEdit_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String snum = String.valueOf(mEdit_num.getText()).trim();
                int number;
                if (null == snum || "".equals(snum)) {
                    number = 1;
                } else {
                    number = Integer.valueOf(snum);
                }
                mTv_sumprice.setText("" + number * goods_price);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEdit_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String discount = String.valueOf(mEdit_discount.getText()).trim();
                double price;
                if ("".equals(discount)) {
                    price = 0;
                } else {
                    price = Double.parseDouble(discount);
                }
                goods_price = price;
                String snum = String.valueOf(mEdit_num.getText()).trim();
                int number;
                if (null == snum || "".equals(snum)) {
                    number = 1;
                } else {
                    number = Integer.valueOf(snum);
                }
                mTv_sumprice.setText("" + goods_price * number);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                giveUpThisOrder();
                break;
            case R.id.tv_reduce:
                String snum = String.valueOf(mEdit_num.getText()).trim();
                int number;
                if (null == snum || "".equals(snum)) {
                    number = 1;
                } else {
                    number = Integer.valueOf(snum);
                }
                if (number > 1) {
                    number = number - 1;
                }
                String s1 = "" + number;
                mEdit_num.setText(s1);
                mEdit_num.setSelection(s1.length());
                break;
            case R.id.tv_add:
                String snumAdd = String.valueOf(mEdit_num.getText()).trim();
                int numberAdd;
                if (null == snumAdd || "".equals(snumAdd)) {
                    numberAdd = 0;
                } else {
                    numberAdd = Integer.valueOf(snumAdd);
                }
                numberAdd = numberAdd + 1;
                if (numberAdd > 99999999) {
                    ToastUtils.showToast(GoodsDetailActivity.this, "您想输入的超过了录入最大值");
                    return;
                }
                String s = "" + numberAdd;
                mEdit_num.setText(s);
                mEdit_num.setSelection(s.length());
                break;
            case R.id.bt_sure:
                mTv_reduce.setVisibility(View.GONE);
                mTv_add.setVisibility(View.GONE);
                String num = String.valueOf(mEdit_num.getText()).trim();
                if ("".equals(num)) {
                    mEdit_num.setText("0");
                    ToastUtils.showToast(GoodsDetailActivity.this, "数量不能为0");
                    return;
                }
                mEdit_num.setBackground(getResources().getDrawable(R.drawable.bg_round_frame));
                mEdit_num.setPadding(10, 5, 10, 5);
                mEdit_num.setEnabled(false);
                mEdit_discount.setEnabled(false);
                break;
            case R.id.bt_submit:
                String remark = String.valueOf(mEdit_remarks.getText()).trim();
                if ("...".equals(remark)) {
                    remark = "";
                }
                Intent intent = new Intent();
                intent.putExtra("orderDetail", "1");
                intent.putExtra("goodsName", String.valueOf(mTv_name1.getText()).trim());
                intent.putExtra("unitPrice", goods_price);
                intent.putExtra("number", String.valueOf(mEdit_num.getText()).trim());
                intent.putExtra("sumPrice", String.valueOf(mTv_sumprice.getText()).trim());
                intent.putExtra("goodsLocalId", goodsLocalId);
                intent.putExtra("subremark", remark);
                intent.putExtra("funitId", mFunitid);
                setResult(resultCode, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            giveUpThisOrder();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void giveUpThisOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GoodsDetailActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定要取消这笔订单吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    class ItemTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                HashMap<String, String> map = new HashMap<>();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("itemid", recordEle.elementTextTrim("fitemid"));//物料内码(提交订单用)
                    map.put("fname", recordEle.elementTextTrim("fname"));//物料名称
                    map.put("fsaleprice", recordEle.elementTextTrim("FSalePrice"));//销售单价
                    map.put("fqty", recordEle.elementTextTrim("FQty"));//库存数量
                    map.put("funit", recordEle.elementTextTrim("funit"));//单位
                    map.put("funitid", recordEle.elementTextTrim("funitid"));//单位id
                    map.put("fprice", recordEle.elementTextTrim("fprice"));//折后单价
                }
                //填充数据到页面
                mTv_name1.setText(map.get("fname"));
                mTv_name2.setText(map.get("fname"));
                double fsaleprice = Double.parseDouble(map.get("fsaleprice"));
                mTv_unit_price.setText("销售单价:" + fsaleprice + "元");
                double fqty = Double.parseDouble(map.get("fqty"));
                mTv_stock.setText("库存数量:" + fqty + map.get("funit"));
                mTv_unit.setText(map.get("funit"));
                //                goods_price = Double.parseDouble(map.get("fsaleprice"));
                goods_price = Double.parseDouble(map.get("fprice"));
                mEdit_discount.setText("" + goods_price);
                mTv_sumprice.setText("" + goods_price);
                goodsLocalId = map.get("itemid");
                mFunitid = map.get("funitid");
                String unUrl = "select sum(FAuxQty) from SEOrderEntry where FMrpClosed=0 and FItemID='" + map.get("itemid") + "'";
                UncloseItemTask uncloseItemTask = new UncloseItemTask(unUrl, fqty, map.get("funit"));
                uncloseItemTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(GoodsDetailActivity.this, "未查到此商品");
                finish();
            }
            //            ProgressDialogUtil.hideDialog();
        }
    }

    //查询订单未关闭数量
    class UncloseItemTask extends AsyncTask<Void, String, String> {
        private String sql;
        private double sum;
        private String unit;

        UncloseItemTask(String sql, double sum, String unit) {
            this.sql = sql;
            this.sum = sum;
            this.unit = unit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                HashMap<String, String> map = new HashMap<>();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("Column1", recordEle.elementTextTrim("Column1"));//订单未关闭数量
                }
                String column1 = map.get("Column1");
                double unClose = Double.parseDouble(column1);
                tv_useful.setText("可用库存:" + (sum - unClose) + unit);
                if ((sum - unClose) <= 0) {
                    ToastUtils.showToast(GoodsDetailActivity.this, "可用库存为0,该商品不可下单");
                    mBt_submit.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(GoodsDetailActivity.this, "商品可用库存查询失败");
                finish();
            }
            ProgressDialogUtil.hideDialog();
            img_noInt.setVisibility(View.GONE);
        }
    }
}
