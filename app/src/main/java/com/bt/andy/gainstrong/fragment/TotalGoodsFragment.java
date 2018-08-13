package com.bt.andy.gainstrong.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.andy.gainstrong.MyAppliaction;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.activity.GoodsDetailActivity;
import com.bt.andy.gainstrong.adapter.LvGoodsAdapter;
import com.bt.andy.gainstrong.adapter.MySpinnerAdapter;
import com.bt.andy.gainstrong.messegeInfo.Order;
import com.bt.andy.gainstrong.messegeInfo.SubtableInfo;
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
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/23 16:39
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class TotalGoodsFragment extends Fragment implements View.OnClickListener {
    private View     mRootView;
    private TextView mTv_title, mTv_sum_price;
    private EditText mEdit_phone, mEdit_name, mEdit_goods_id;
    private ImageView mImg_delete, mImg_confirm;
    private TextView mTv_entry, mTv_surema, mTv_no_good;
    private ImageView mImg_scan;
    private int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1001;//申请照相机权限结果
    private int REQUEST_CODE                       = 1002;//接收扫描结果
    private int DETAIL_REQUESTCODE                 = 111;//商品详情页返回参数对应码
    private int ORDER_RESULT_CODE                  = 9527;//商品详情页返回结果码
    private MyListView                    mLv_goods;
    public  List<SubtableInfo>            mData;//存放每个子表的数据
    private LvGoodsAdapter                mGoodsAdapter;
    private Spinner                       mSpinner;//配送类型选择条目
    private String                        deliveryId;//类型代码,配送类型
    private EditText                      mEdit_address;//配送地址
    private EditText                      edit_write;//摘要
    private Button                        mBt_submit;
    private LinearLayout                  mLinear_sum;//总金额
    private LinearLayout                  mLinear_type;
    private LinearLayout                  linear_write;
    private LinearLayout                  mLinear_address;
    private List<Map<String, String>>     mPsData;
    private String                        mFpoints;//积分
    private String                        memberName;//会员名
    private List<HashMap<String, String>> mHTot;//记录模糊查询结果（商品名:商品id）
    private DropdownButton                mDownbt;//下拉框显示模糊查询结果
    private List<DropBean>                mGoodsNameList;//放置商品名称
    private double                        mTotalPrice;//记录总金额
    private String defAddress = "";//记录查询的会员默认地址

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_total, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mTv_title = mRootView.findViewById(R.id.tv_title);
        mEdit_phone = mRootView.findViewById(R.id.edit_phone);//输入手机号
        mEdit_name = mRootView.findViewById(R.id.edit_name);//输入会员名
        mImg_delete = mRootView.findViewById(R.id.img_delete);//清空手机号
        mImg_confirm = mRootView.findViewById(R.id.img_confirm);//查询手机号
        mTv_entry = mRootView.findViewById(R.id.tv_entry);//确认录入会员名
        mImg_scan = mRootView.findViewById(R.id.img_scan);//扫描
        mEdit_goods_id = mRootView.findViewById(R.id.edit_goods_id);//输入商品id
        mTv_surema = mRootView.findViewById(R.id.tv_surema);//确认输入的商品id
        mTv_no_good = mRootView.findViewById(R.id.tv_no_good);//提示未添加商品
        mLv_goods = mRootView.findViewById(R.id.lv_goods);//商品列表
        mLinear_sum = mRootView.findViewById(R.id.linear_sum);//总金额
        mTv_sum_price = mRootView.findViewById(R.id.tv_sum_price);//总金额计价
        mLinear_type = mRootView.findViewById(R.id.linear_type);//选择配送类型布局
        mSpinner = mRootView.findViewById(R.id.spinner);//配送类型选择条目
        linear_write = mRootView.findViewById(R.id.linear_write);//摘要布局
        edit_write = mRootView.findViewById(R.id.edit_write);
        mLinear_address = mRootView.findViewById(R.id.linear_address);//配送地址布局
        mEdit_address = mRootView.findViewById(R.id.edit_address);//配送地址
        mBt_submit = mRootView.findViewById(R.id.bt_submit);//总表提交服务器
        mDownbt = mRootView.findViewById(R.id.downbt);
        new TypeTask().execute();//查询出所有业务类型
    }

    private void initData() {
        mTv_title.setText(getResources().getString(R.string.sales_order));
        mImg_scan.setOnClickListener(this);
        mImg_delete.setOnClickListener(this);
        mImg_confirm.setOnClickListener(this);
        mTv_surema.setOnClickListener(this);
        mData = new ArrayList();
        mGoodsAdapter = new LvGoodsAdapter(getContext(), mData);
        mLv_goods.setAdapter(mGoodsAdapter);
        mLv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        mLv_goods.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //弹出对话框提示用户将要删除该条item
                showDeleteDailog(i);
                return false;
            }
        });
        //模糊查询结果
        mGoodsNameList = new ArrayList<DropBean>();
        mDownbt.setData(mGoodsNameList);
        mDownbt.setOnDropItemSelectListener(new DropdownButton.OnDropItemSelectListener() {
            @Override
            public void onDropItemSelect(int Postion) {
                if (Postion == 0) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.choose_goods));
                    return;
                }
                HashMap<String, String> goodsMap = mHTot.get(Postion - 1);
                String fnumber = goodsMap.get("fnumber");
                mDownbt.setChecked(false);
                mDownbt.setVisibility(View.GONE);
                //跳转商品详情界面，携带商品id
                sendGoodsInfo(fnumber);
            }
        });
        mBt_submit.setOnClickListener(this);
        mBt_submit.setVisibility(View.GONE);
        mLinear_sum.setVisibility(View.GONE);
        mLinear_type.setVisibility(View.GONE);
        linear_write.setVisibility(View.GONE);
        mLinear_address.setVisibility(View.GONE);
        mDownbt.setVisibility(View.GONE);
    }

    private void showDeleteDailog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getResources().getString(R.string.reminder));
        builder.setMessage(getResources().getString(R.string.to_del));
        builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mData.remove(position);
                mGoodsAdapter.notifyDataSetChanged();
                if (mData.size() == 0) {
                    mTv_no_good.setVisibility(View.VISIBLE);
                    if (mBt_submit.isShown()) {
                        mBt_submit.setVisibility(View.GONE);
                    }
                    mLinear_sum.setVisibility(View.GONE);
                    mLinear_type.setVisibility(View.GONE);
                    linear_write.setVisibility(View.GONE);
                    mLinear_address.setVisibility(View.GONE);
                } else {
                    mTotalPrice = 0;
                    for (SubtableInfo info : mData) {
                        mTotalPrice = mTotalPrice + info.getSum_pric();
                    }
                }
                dialog.cancel();
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.img_delete:
                mEdit_phone.setText("");
                break;
            case R.id.img_confirm:
                ToastUtils.showToast(getContext(), getResources().getString(R.string.waite_for_member_id));
                String fmobile = String.valueOf(mEdit_phone.getText()).trim();
                ProgressDialogUtil.startShow(getContext(), getResources().getString(R.string.searching));
                defAddress = "";
                MemberTask memberTask = new MemberTask(fmobile);
                memberTask.execute();
                break;
            case R.id.tv_surema:
                String goodsMid = String.valueOf(mEdit_goods_id.getText()).trim();
                if (null == goodsMid || "".equals(goodsMid) || getResources().getString(R.string.commodity_coding).equals(goodsMid)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.input_commodity_coding));
                    return;
                }
                Task task = new Task(goodsMid);
                task.execute();
                //跳转商品详情界面，携带商品id
                //sendGoodsInfo(goodsid);
                break;
            case R.id.bt_submit:
                String phone = String.valueOf(mEdit_phone.getText()).trim();
                String name = String.valueOf(mEdit_name.getText()).trim();
                String remark = String.valueOf(edit_write.getText()).trim();
                String address = String.valueOf(mEdit_address.getText()).trim();
                if ("".equals(phone) || getResources().getString(R.string.phone_number).equals(phone)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.fill_phone));
                    return;
                }
                if ("".equals(name) || getResources().getString(R.string.member_name).equals(name)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.fill_member_name));
                    return;
                }
                if ("".equals(deliveryId)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.choose_distribution));
                    return;
                }
                if ("".equals(remark) || "...".equals(remark)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.fill_remark));
                    return;
                }
                if ("".equals(address) || "...".equals(address)) {
                    ToastUtils.showToast(getContext(), getResources().getString(R.string.fill_address));
                    return;
                }
                //TODO:提交总表到服务器
                Order order = new Order();
                order.setUserId(MyAppliaction.userID);
                order.setMembermobile(phone);
                order.setMembername(name);
                order.setBusinesstype(deliveryId);
                order.setRemark(remark);
                order.setAddress(address);
                order.setSubList(mData);
                order.setPoint(mFpoints);//填写积分
                SubmitTask submitTask = new SubmitTask(order);
                submitTask.execute();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //获取商品id信息，跳转activity展示，在新的页面确定后添加到listview中
                    sendGoodsInfo(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getContext(), getResources().getString(R.string.analysis_error), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == DETAIL_REQUESTCODE) {
            if (resultCode == ORDER_RESULT_CODE) {
                String orderInfo = data.getStringExtra("orderDetail");
                if (null != orderInfo) {
                    String goodsName = data.getStringExtra("goodsName");
                    double unitPrice = data.getDoubleExtra("unitPrice", 0.00);
                    //                    double unit_price = Double.parseDouble(unitPrice);
                    int number = Integer.parseInt(data.getStringExtra("number"));
                    double sumPrice = Double.parseDouble(data.getStringExtra("sumPrice"));
                    String goodsLocalId = data.getStringExtra("goodsLocalId");
                    String remark = data.getStringExtra("subremark");
                    String funitId = data.getStringExtra("funitId");//单位id
                    //填入总表list中
                    SubtableInfo goodsInfo = new SubtableInfo();
                    goodsInfo.setGoodsName(goodsName);
                    goodsInfo.setZh_unit_price(unitPrice);
                    goodsInfo.setNumber(number);
                    goodsInfo.setSum_pric(sumPrice);
                    goodsInfo.setGoodsid(goodsLocalId);
                    goodsInfo.setRemark(remark);
                    goodsInfo.setUnitid(funitId);
                    mData.add(goodsInfo);
                    mGoodsAdapter.notifyDataSetChanged();
                    mTv_no_good.setVisibility(View.GONE);
                    mLinear_sum.setVisibility(View.VISIBLE);
                    mTotalPrice = 0;
                    for (SubtableInfo info : mData) {
                        mTotalPrice = mTotalPrice + info.getSum_pric();
                    }
                    mTv_sum_price.setText("¥" + mTotalPrice);
                    mLinear_type.setVisibility(View.VISIBLE);
                    linear_write.setVisibility(View.VISIBLE);
                    mLinear_address.setVisibility(View.VISIBLE);
                    if (!"".equals(defAddress)) {
                        mEdit_address.setText(defAddress);
                    }
                    if (!mBt_submit.isShown()) {
                        mBt_submit.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void sendGoodsInfo(String goodsID) {
        ToastUtils.showToast(getContext(), getResources().getString(R.string.commodity_coding) + goodsID);
        //mEdit_goods_id.setText(goodsID);
        //跳转activity，选择添加
        showGoodsDetail(goodsID);
    }

    private void showGoodsDetail(String goodsID) {
        //跳转商品详情
        Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
        intent.putExtra("goodsId", goodsID);
        startActivityForResult(intent, DETAIL_REQUESTCODE);
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
            String sql = "select fnumber,fname from t_icitem where FHelpCode like'%" + text + "%' or fname like '%" + text + "%'";
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
                mDownbt.setVisibility(View.VISIBLE);
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
                mDownbt.setData(mGoodsNameList);
                mDownbt.setChecked(true);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(getContext(), getResources().getString(R.string.no_querying));
            }
        }
    }

    class SubmitTask extends AsyncTask<Void, String, String> {
        Order order;

        SubmitTask(Order order) {
            this.order = order;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //表头
                Document document = DocumentHelper.createDocument();
                Element rootElement = document.addElement("NewDataSet");
                Element cust = rootElement.addElement("Cust");
                //制单人id
                cust.addElement("FBillerID").setText(order.getUserId());
                //会员名
                cust.addElement("FHeadSelfS0165").setText(order.getMembername());
                //会员手机号
                cust.addElement("FHeadSelfS0166").setText(order.getMembermobile());
                //积分
                cust.addElement("FHeadSelfS01100").setText(order.getPoint());
                //业务类型
                cust.addElement("FHeadSelfS0167").setText(order.getBusinesstype());
                //摘要
                cust.addElement("FExplanation").setText(order.getRemark());
                //送货地址
                cust.addElement("FDeliveryAddress").setText(order.getAddress());

                //表体
                Document document2 = DocumentHelper.createDocument();
                Element rootElement2 = document2.addElement("NewDataSet");
                for (SubtableInfo info : order.getSubList()) {
                    Element cust2 = rootElement2.addElement("Cust");
                    //商品代码
                    cust2.addElement("FItemID").setText(info.getGoodsid());
                    //单位
                    cust2.addElement("FUnitID").setText(info.getUnitid());
                    //数量
                    cust2.addElement("FQty").setText(String.valueOf(info.getNumber()));
                    //折后单价
                    cust2.addElement("FPrice").setText(String.valueOf(info.getZh_unit_price()));
                    //金额
                    cust2.addElement("FAmount").setText(String.valueOf(info.getSum_pric()));
                    //备注
                    cust2.addElement("fnote").setText(info.getRemark());
                    //折后价格
                    cust2.addElement("FAmountDiscount").setText(String.valueOf(info.getSum_pric()));
                    //折后单价
                    cust2.addElement("FPriceDiscount").setText(String.valueOf(info.getZh_unit_price()));
                }
                OutputFormat outputFormat = OutputFormat.createPrettyPrint();
                outputFormat.setSuppressDeclaration(false);
                outputFormat.setNewlines(false);
                StringWriter stringWriter = new StringWriter();
                StringWriter stringWriter2 = new StringWriter();
                // xmlWriter是用来把XML文档写入字符串的(工具)
                XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
                XMLWriter xmlWriter2 = new XMLWriter(stringWriter2, outputFormat);
                // 把创建好的XML文档写入字符串
                xmlWriter.write(document);
                xmlWriter2.write(document2);
                String fbtouxml = stringWriter.toString().substring(38);
                String fbtixml = stringWriter2.toString().substring(38);
                Map<String, String> map = new HashMap<>();
                map.put("InterID", "0");
                map.put("BillNO", "a");
                map.put("FBtouXMl", fbtouxml);
                map.put("FBtiXML", fbtixml);
                return SoapUtil.requestWebService(Consts.ORDER, map);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("成功".equals(s)) {
                ToastUtils.showToast(getContext(), getResources().getString(R.string.sub_success));
                mData.clear();
                mGoodsAdapter.notifyDataSetChanged();
                mLinear_sum.setVisibility(View.GONE);
                mLinear_type.setVisibility(View.GONE);
                linear_write.setVisibility(View.GONE);
                mLinear_address.setVisibility(View.GONE);
                mBt_submit.setVisibility(View.GONE);
            } else {
                ToastUtils.showToast(getContext(), getResources().getString(R.string.sub_error));
            }
        }
    }

    class TypeTask extends AsyncTask<Void, String, String> {//查询订单类型

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPsData = new ArrayList();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String sql = "select fname,finterid from t_SubMessage where ftypeid=10001";
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("0")) {
                try {
                    Document doc = DocumentHelper.parseText(s);
                    Element ele = doc.getRootElement();
                    Iterator iter = ele.elementIterator("Cust");
                    while (iter.hasNext()) {
                        Element rec = (Element) iter.next();
                        String fname = rec.elementTextTrim("fname");
                        String fitemid = rec.elementTextTrim("finterid");
                        Map<String, String> map = new HashMap<>();
                        map.put("fname", fname);
                        map.put("fitemid", fitemid);
                        mPsData.add(map);
                    }
                    List<String> strList = new ArrayList<>();
                    for (Map<String, String> map : mPsData) {
                        strList.add(map.get("fname"));
                    }
                    MySpinnerAdapter adapter = new MySpinnerAdapter(getContext(), strList);
                    mSpinner.setAdapter(adapter);
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            deliveryId = mPsData.get(i).get("fitemid");
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MemberTask extends AsyncTask<Void, String, String> {
        String fmobile;

        MemberTask(String fmobile) {
            this.fmobile = fmobile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String sql = "select fname,fmobile,favailablepoints,FAddr from icvip where FMobile like '%" + fmobile + "%'";
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogUtil.hideDialog();
            if (!s.equals("0")) {
                try {
                    Document doc = DocumentHelper.parseText(s);
                    Element ele = doc.getRootElement();
                    Iterator iter = ele.elementIterator("Cust");
                    while (iter.hasNext()) {
                        Element rec = (Element) iter.next();
                        String fmobile = rec.elementTextTrim("fmobile");//手机号15162867733
                        memberName = rec.elementTextTrim("fname");//名
                        //默认地址
                        defAddress = rec.elementTextTrim("FAddr");
                        mEdit_name.setText(memberName);
                        //积分
                        mFpoints = rec.elementTextTrim("favailablepoints");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToast(getContext(), "查询失败，未获取到会员信息");
                }
            } else {
                ToastUtils.showToast(getContext(), "查询失败，未获取到相关会员信息");
            }
        }
    }
}
