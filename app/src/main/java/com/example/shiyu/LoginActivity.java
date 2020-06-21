package com.example.shiyu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText passwords;
    private SharedPreferences sp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        passwords = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.login);
        final TextView goRegister = findViewById(R.id.go_register);
        //监听注册按钮
        loginBtn.setOnClickListener(this);
        goRegister.setOnClickListener(this);
        sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            //请求数据
            sendRequestWithOkHttp();
        }
        if (v.getId() == R.id.go_register) {
            Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
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
                String url = "http://192.168.43.212:3000/login?username="
                        + username.getText().toString()
                        + "&password=" + passwords.getText().toString();
                System.out.println(url);
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
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //打印输出
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object=new JSONObject(jsonData);
            String code = object.getString("code");
            String msg = object.getString("msg");
            // 操作成功
            if (code.equals("200")) {
                // 打印用户ID
                JSONObject data = object.getJSONObject("data");
                int userID = data.getInt("id");
                Log.d("id"," : " + userID);
                //保存用户重要信息
                SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("userID", userID);
                editor.apply();
                //跳转页面
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //创建默认文件夹
                createDefaultFolder(userID);
            }
            //提示框
            Looper.prepare();
            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            Looper.loop();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //创建默认文件夹
    private void createDefaultFolder(final Integer id) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url = "http://192.168.43.212:3000/defaultFolder?userID="
                        + id ;
                System.out.println(url);
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
                    saveFolderID(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //处理返回数据
    private void saveFolderID(String jsonData) {}
}
