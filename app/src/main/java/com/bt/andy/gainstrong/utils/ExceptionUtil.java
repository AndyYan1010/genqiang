package com.bt.andy.gainstrong.utils;


import com.bt.andy.gainstrong.MyAppliaction;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常同意处理
 *
 * @author btzoon
 */
public class ExceptionUtil {
    public static void handleException(Exception e) {
        if (MyAppliaction.isRelease) {
            //把异常信息变成字符串
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String string = stringWriter.toString();


            //发邮件，发到服务器
            //			LogUtil.i("", string);
        } else {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String string = stringWriter.toString();
            //			LogUtil.i("异常信息必须看", "  ");
            //			LogUtil.i("异常信息必须看", "  ");
            //			LogUtil.i("异常信息必须看", string);
            e.printStackTrace();
        }
    }

}
