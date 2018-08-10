package com.bt.andy.gainstrong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.andy.gainstrong.BaseActivity;
import com.bt.andy.gainstrong.MainActivity;
import com.bt.andy.gainstrong.MyAppliaction;
import com.bt.andy.gainstrong.R;
import com.bt.andy.gainstrong.utils.Consts;
import com.bt.andy.gainstrong.utils.SoapUtil;
import com.bt.andy.gainstrong.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/22 9:05
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdit_num;
    private EditText mEdit_psd;
    private Button   mBt_submit;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_actiivty);
        getView();
        setData();
    }

    private void getView() {
        mEdit_num = (EditText) findViewById(R.id.edit_num);
        mEdit_psd = (EditText) findViewById(R.id.edit_psd);
        mBt_submit = (Button) findViewById(R.id.bt_login);
        dialog = new Dialog(this);
    }

    private void setData() {
        mEdit_psd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_DONE:
                        //登录
                        funLogin();
                        break;
                }
                return false;
            }
        });
        mBt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                funLogin();
                break;
        }
    }

    //登录
    private void funLogin() {
        String number = mEdit_num.getText().toString().trim();
        String pass = mEdit_psd.getText().toString().trim();
        if ("".equals(number) || "请输入用户名".equals(number)) {
            ToastUtils.showToast(LoginActivity.this, "请输入用户名");
            return;
        }
        if ("请输入密码".equals(pass)) {
            //ToastUtils.showToast(LoginActivity.this,"请输入密码");
            pass = "";
        }
        new LoginTask(number, pass).execute();
        //                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //                startActivity(intent);
    }

    class LoginTask extends AsyncTask<Void, String, String> {
        String username;
        String password;

        LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("UserName", username);
            map.put("PassWord", password);
            return SoapUtil.requestWebService(Consts.Login, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s.contains("成功")) {
                String[] split = s.split("/");
                String sId = split[0];
                String userid = sId.substring(2, sId.length());
                MyAppliaction.userID = userid;//用户id
                MyAppliaction.memID = username;//工号
                if (split.length >= 2) {
                    MyAppliaction.userName = split[1];//用户姓名
                } else {
                    MyAppliaction.userName = "";//用户姓名
                    ToastUtils.showToast(LoginActivity.this, "系统中未填写姓名");
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                ToastUtils.showToast(LoginActivity.this, "登陆成功");
                finish();
            } else {
                ToastUtils.showToast(LoginActivity.this, "登陆失败");
            }
        }
    }
}
