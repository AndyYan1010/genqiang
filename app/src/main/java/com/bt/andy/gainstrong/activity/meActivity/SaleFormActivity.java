package com.bt.andy.gainstrong.activity.meActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bt.andy.gainstrong.BaseActivity;
import com.bt.andy.gainstrong.MyAppliaction;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.adapter.LvFormAdapter;
import com.bt.andy.gainstrong.messegeInfo.SaleFormInfo;
import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.ProgressDialogUtil;
import com.bt.andy.gainstrong.utils.SoapUtil;
import com.bt.andy.gainstrong.utils.ToastUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/13 9:01
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SaleFormActivity extends BaseActivity implements View.OnClickListener {
    private ImageView          img_back;
    private TextView           tv_title;
    private ListView           lv_form;
    private List<SaleFormInfo> mData;
    private LvFormAdapter      formAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_form);
        setView();
        setData();
    }

    private void setView() {
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_form = (ListView) findViewById(R.id.lv_form);
    }

    private void setData() {
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(this);
        tv_title.setText("我的销售报表");
        mData = new ArrayList();
        formAdapter = new LvFormAdapter(SaleFormActivity.this, mData);
        lv_form.setAdapter(formAdapter);
        ProgressDialogUtil.startShow(SaleFormActivity.this, "正在加载，请稍等...");
        new ZHUTask("").execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    class ReportTask extends AsyncTask<Void, String, String> {
        //输入框里获得
        String text;

        public ReportTask(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String sql = text;
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
                while (iter.hasNext()) {
                    HashMap<String, String> map = new HashMap<>();
                    Element recordEle = (Element) iter.next();
                    map.put("fnumber", recordEle.elementTextTrim("fnumber"));//物料条码
                    map.put("fname", recordEle.elementTextTrim("fname"));//物料名称

                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(SaleFormActivity.this, "未查询到类似商品助记码");
            }
        }
    }

    class ZHUTask extends AsyncTask<Void, String, String> {
        String mUserID;

        ZHUTask(String userID) {
            this.mUserID = userID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "JA_select";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/JA_select";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);
            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            //            String sql = "select c.fname,b.fauxqty,b.FAuxPrice,b.famount,b.FNote,a.FBillNo,FHeadSelfS0165,FHeadSelfS0166" +
            //                    " from SEOrder a inner join SEOrderEntry b on a.finterid=b.finterid inner join t_icitem c on c.fitemid=b.fitemid";

            String sql = "select c.fname,b.fauxqty,b.FAuxPrice,b.famount,b.FNote,a.FBillNo,FHeadSelfS0165,FHeadSelfS0166," +
                    "d.fname  from SEOrder a inner join SEOrderEntry b on a.finterid=b.finterid inner join t_icitem c on " +
                    "c.fitemid=b.fitemid inner join t_user d on d.fuserid=a.fbillerid" + " where d.fname='" + MyAppliaction.memID + "'";
            Log.i("主表查询语句", sql);
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_Currency");

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString() + "=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            //子订单存放列表maplist
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String fbillNo = recordEle.elementTextTrim("FBillNo");//单据号
                    String fname = recordEle.elementTextTrim("fname");//名称
                    String unit_price = recordEle.elementTextTrim("FAuxPrice");//单价
                    String num = recordEle.elementTextTrim("fauxqty");//数量
                    String sum = recordEle.elementTextTrim("famount");//总价

                    SaleFormInfo formInfo = new SaleFormInfo();
                    double pri = Double.parseDouble(unit_price);
                    double number = Double.parseDouble(num);
                    DecimalFormat df = new DecimalFormat(".00");
                    String uniPri = df.format(pri);
                    String numT = df.format(number);

                    formInfo.setFbillno(fbillNo);
                    formInfo.setName(fname);
                    formInfo.setUnit_price(uniPri);
                    formInfo.setNum(numT);
                    formInfo.setSum(sum);

                    mData.add(formInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(SaleFormActivity.this, "查找详情出错");
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s);
            ProgressDialogUtil.hideDialog();
            formAdapter.notifyDataSetChanged();
        }
    }
}
