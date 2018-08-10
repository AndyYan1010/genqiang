package com.bt.andy.gainstrong.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

public class SoapUtil {
    /**
     * 请求webservice接口
     * @param methodName
     * @param mMap
     * @return
     */
    public static String requestWebService(String methodName,Map<String,String> mMap){
        try{
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/"+methodName;

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的参数
            for(String s : mMap.keySet()){
                rpc.addProperty(String.valueOf(s),String.valueOf(mMap.get(s)));
            }

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            // 调用WebService
            transport.call(soapAction, envelope);

            // 获取返回的数据
            String result = "";
            if (envelope.bodyIn instanceof SoapFault) {
                Log.i("服务器返回", (SoapObject) envelope.getResponse() + "");
            } else {
                SoapObject object = (SoapObject) envelope.bodyIn;
                // 获取返回的结果
                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
                result = object.getProperty(0).toString();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
