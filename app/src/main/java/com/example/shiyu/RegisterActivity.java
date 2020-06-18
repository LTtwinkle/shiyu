package com.example.shiyu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText passwords;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        passwords = findViewById(R.id.password);
        email = findViewById(R.id.email);
        final Button registerBtn = findViewById(R.id.register);
        final TextView goLogin = findViewById(R.id.go_login);
        //监听注册按钮
        registerBtn.setOnClickListener(this);
        goLogin.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            //请求数据
            sendRequestWithOkHttp();
        }
        if (v.getId() == R.id.go_login) {
            //跳转页面
            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
            //启动
            startActivity(intent);
        }
    }

    //发送请求
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                //设置请求路由
                String url = "http://192.168.43.212:3000/addUser?username="
                        + username.getText().toString()
                        + "&password=" + passwords.getText().toString()
                        + "&email=" + email.getText().toString();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url).get()
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        responseData = Objects.requireNonNull(response.body()).string();
                    }
                    //处理返回的数据
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object=new JSONObject(jsonData);
            String code = object.getString("code");
            String msg = object.getString("msg");
            //日志
            Log.d("code", " ：" + code);
            Log.d("msg", " ：" + msg);
            //跳转页面
            if (code.equals("200")) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            //提示框
            Looper.prepare();
            Toast.makeText(RegisterActivity.this, msg + "返回登录界面！", Toast.LENGTH_SHORT).show();
            Looper.loop();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
