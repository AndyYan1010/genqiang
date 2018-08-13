package com.bt.andy.gainstrong.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.adapter.LvStockAdapter;
import com.bt.andy.gainstrong.messegeInfo.StockInfo;
import com.bt.andy.gainstrong.myTools.DropBean;
import com.bt.andy.gainstrong.myTools.DropdownButton;
import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.ProgressDialogUtil;
import com.bt.andy.gainstrong.utils.SoapUtil;
import com.bt.andy.gainstrong.utils.ToastUtils;
import com.bt.andy.gainstrong.viewmodle.MyListView;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/26 8:37
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class Stock_F extends Fragment implements View.OnClickListener {
    private View                          mRootView;
    private TextView                      tv_title;
    private EditText                      edit_goods_id;
    private ImageView                     img_scan;
    private TextView                      tv_surema;
    private LinearLayout                  linear_drop;
    private DropdownButton                downbt;
    private List<HashMap<String, String>> mHTot;//记录模糊查询结果（商品名:商品id）
    private int REQUEST_CODE = 1002;//接收扫描结果
    private List<DropBean> mGoodsNameList;//放置商品名称
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private List<StockInfo> mStockInfos;
    private MyListView      lv_stock;//分库存列表
    private LvStockAdapter  stockAdapter;
    private TextView        tv_heji;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.stock_f, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        tv_title = mRootView.findViewById(R.id.tv_title);
        edit_goods_id = mRootView.findViewById(R.id.edit_goods_id);
        img_scan = mRootView.findViewById(R.id.img_scan);
        tv_surema = mRootView.findViewById(R.id.tv_surema);
        tv_heji = mRootView.findViewById(R.id.tv_heji);
        linear_drop = mRootView.findViewById(R.id.linear_drop);
        downbt = mRootView.findViewById(R.id.downbt);
        lv_stock = mRootView.findViewById(R.id.lv_stock);
    }

    private void initData() {
        tv_title.setText(getResources().getString(R.string.stock));
        tv_surema.setOnClickListener(this);
        img_scan.setOnClickListener(this);
        //模糊查询结果
        mGoodsNameList = new ArrayList<DropBean>();
        downbt.setData(mGoodsNameList);
        downbt.setOnDropItemSelectListener(new DropdownButton.OnDropItemSelectListener() {
            @Override
            public void onDropItemSelect(int Postion) {
                if (Postion == 0) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.choose_goods));
                    return;
                }
                HashMap<String, String> goodsMap = mHTot.get(Postion - 1);
                String fnumber = goodsMap.get("fnumber");
                downbt.setChecked(false);
                linear_drop.setVisibility(View.INVISIBLE);
                //查询商品id，写入商品库存
                writeGoodsStock(fnumber);
            }
        });
        mStockInfos = new ArrayList<>();
        stockAdapter = new LvStockAdapter(getContext(), mStockInfos);
        lv_stock.setAdapter(stockAdapter);
        tv_heji.setVisibility(View.GONE);
        linear_drop.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_surema:
                String goodsMid = String.valueOf(edit_goods_id.getText()).trim();
                linear_drop.setVisibility(View.INVISIBLE);
                tv_heji.setVisibility(View.GONE);
                if (null == goodsMid || "".equals(goodsMid) || getResources().getString(R.string.commodity_coding).equals(goodsMid)) {
                    mStockInfos.clear();
                    stockAdapter.notifyDataSetChanged();
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.input_commodity_coding));
                    return;
                }
                //查询之前关闭输入法，防止bug
                hintKeyBoard(edit_goods_id);
                new Task(goodsMid).execute();
                break;
            case R.id.img_scan:
                //动态申请照相机权限
                //第二个参数是需要申请的权限
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);
                } else {
                    Intent intent = new Intent(getContext(), CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
        }
    }

    //关闭输入法
    private void hintKeyBoard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 处理二维码扫描结果*/
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //获取商品信息
                    writeGoodsStock(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), getResources().getString(R.string.analysis_error), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void writeGoodsStock(String fnumber) {
        if (null != linear_drop) {
            linear_drop.setVisibility(View.INVISIBLE);
        }
        tv_heji.setVisibility(View.GONE);
        //访问网络，获取详情
        //根据扫描的代码查询
        //String sql = "select top 1 isnull(d.fprice,0) fprice,a.fitemid,a.funitid,a.fname,a.FSalePrice,isnull(sum(b.FQty),0) FQty,c.fname funit from t_icitem a left join ICInventory b on a.fitemid=b.fitemid left join t_MeasureUnit c on c.fitemid=a.FUnitID left join (select FPrice,FItemID,FBegDate from ICPrcPlyEntry ) d on a.fitemid=d.fitemid where a.fnumber='" + fnumber + "' group by a.fname,a.FSalePrice,c.fname,a.fitemid,a.funitid,d.fprice,d.FBegDate order by d.FBegDate desc";

        String sql2 = "select b.fnumber,b.fname,c.fnumber,c.fname,a.FQty,b.FGoodsBarCode from ICInventory a inner join t_ICItem b on a.FItemID=b.FItemID inner join t_Stock c on c.fitemid=a.FStockID  where b.FGoodsBarCode='" + fnumber + "' or b.fnumber='" + fnumber + "'";
        //根据助记码或者名称模糊查询
        new ItemTask(sql2).execute();
    }

    class Task extends AsyncTask<Void, String, String> {
        //输入框里获得
        String text;

        public Task(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String sql = "select fnumber,fname from t_icitem where FHelpCode like'%" + text + "%' or fname like '%" + text + "%' or FGoodsBarCode like '%" + text + "%'";
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (null == mHTot) {
                    mHTot = new ArrayList();
                } else {
                    mHTot.clear();
                }
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                while (iter.hasNext()) {
                    HashMap<String, String> map = new HashMap<>();
                    Element recordEle = (Element) iter.next();
                    map.put("fnumber", recordEle.elementTextTrim("fnumber"));//物料条码
                    map.put("fname", recordEle.elementTextTrim("fname"));//物料名称
                    mHTot.add(map);
                }
                //填充数据到页面
                linear_drop.setVisibility(View.VISIBLE);
                if (null == mGoodsNameList) {
                    mGoodsNameList = new ArrayList<>();
                } else {
                    mGoodsNameList.clear();
                }
                mGoodsNameList.add(new DropBean(getResources().getString(R.string.select_result)));
                for (HashMap<String, String> map : mHTot) {
                    String fname = map.get("fname");
                    mGoodsNameList.add(new DropBean(fname));
                }
                downbt.setData(mGoodsNameList);
                downbt.setChecked(true);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(getContext(), getResources().getString(R.string.no_querying));
            }
        }
    }

    class ItemTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.startShow(getContext(), getResources().getString(R.string.searching));
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
                if (null == mStockInfos) {
                    mStockInfos = new ArrayList<>();
                } else {
                    mStockInfos.clear();
                }
                double sum = 0;
                DecimalFormat df = new DecimalFormat(".00");
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                HashMap<String, String> map = new HashMap<>();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("fname", recordEle.elementTextTrim("fname"));//物料名称
                    map.put("fqty", recordEle.elementTextTrim("FQty"));//库存数量
                    map.put("fname1", recordEle.elementTextTrim("fname1"));//仓库
                    StockInfo stockInfo = new StockInfo();
                    stockInfo.setFname(map.get("fname"));//名称
                    stockInfo.setAddress(map.get("fname1"));//哪个仓库
                    String fqty = map.get("fqty");//数量（库存量）
                    double num = Double.parseDouble(fqty);
                    sum = sum + num;
                    stockInfo.setFqty(df.format(num));
                    mStockInfos.add(stockInfo);
                }
                if (mStockInfos.size() > 0) {
                    linear_drop.setVisibility(View.GONE);
                    tv_heji.setVisibility(View.VISIBLE);
                    tv_heji.setText(getResources().getString(R.string.stock)+getResources().getString(R.string.total) + df.format(sum));
                } else {
                    linear_drop.setVisibility(View.INVISIBLE);
                    tv_heji.setVisibility(View.GONE);
                }
                //填充数据到页面
                stockAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(getContext(), getResources().getString(R.string.search_error));
            }
            ProgressDialogUtil.hideDialog();
        }
    }

    /**
     * 判断字符串中是否含有中文
     */
    public static boolean isCNChar(String s) {
        boolean booleanValue = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 128) {
                booleanValue = true;
                break;
            }
        }
        return booleanValue;
    }
}
