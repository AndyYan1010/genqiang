package com.bt.andy.gainstrong;

import android.app.Activity;
import android.app.Application;

import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.ExceptionUtil;
import com.bt.andy.gainstrong.utils.SPUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 8:51
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyAppliaction extends Application {
    public static boolean             isRelease    = false;
    public static ArrayList<Activity> listActivity = new ArrayList<Activity>();
    public static int                 flag         = -1;//判断是否被回收
    public static String userID;//用户id
    public static String memID;//用户工号
    public static String userName;//用户名
    public static int YUYAN = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        //获取系统语言
        setSysLanguage();
    }

    private void setSysLanguage() {
        //获取手机系统语言设置，如果是中文就设置成英文
        //判断用户是否是第一次登陆，第一次登陆使用系统的设置。以后都用应用自己设置的语言。
        //判断sp文件是否存在
        String isFisrst = (String) SPUtils.get(this, Consts.LAUGAGE, "");
        //获取系统语言
        String able = getResources().getConfiguration().locale.getCountry();
        //判断此时的手机系统语言跟保存的时候一致。一致的话走else，否则走if
        String systemlanage = (String) SPUtils.get(this, Consts.SYSTEMLAUGAGE, "");
        if (null == isFisrst || isFisrst.equals("") || !systemlanage.equals(able)) {
            if (able.equals("CN")) {
                SPUtils.put(this, Consts.LAUGAGE, "0");
                YUYAN = 0;
            } else {
                SPUtils.put(this, Consts.LAUGAGE, "1");
                YUYAN = 1;
            }
            //第一运行时，记录下系统本地语言。
            SPUtils.put(this, Consts.SYSTEMLAUGAGE, able);
        } else {
            String yueyan = (String) SPUtils.get(this, Consts.LAUGAGE, "");
            //获取设置的语言
            if (yueyan.equals("0")) {
                YUYAN = 0;
            } else if (yueyan.equals("1")) {
                YUYAN = 1;
            }
        }
    }

    public static void exit() {
        try {
            for (Activity activity : listActivity) {
                activity.finish();
            }
            // 结束进程
            System.exit(0);
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }
}
