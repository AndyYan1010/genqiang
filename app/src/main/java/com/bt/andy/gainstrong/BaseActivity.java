package com.bt.andy.gainstrong;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bt.andy.gainstrong.activity.LoginActivity;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 8:53
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyAppliaction.flag == -1) {//flag为-1说明程序被杀掉
            protectApp();
        }
        MyAppliaction.listActivity.add(this);
    }
    protected void protectApp() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清空栈里MainActivity之上的所有activty
        startActivity(intent);
        finish();
        MyAppliaction.flag = 0;
    }
}
