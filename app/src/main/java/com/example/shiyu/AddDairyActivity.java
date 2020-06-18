package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddDairyActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView num;
    private EditText dairy;
    private TextView time;
    private String addDairy = "http://192.168.43.212:3000/addDairy";
    private String DataUrl;
    private Integer folderID;
    private Integer userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_detail);
        dairy = findViewById(R.id.dairy);
        time = findViewById(R.id.record_time);
        num = findViewById(R.id.count_num);
        final TextView goBack = findViewById(R.id.back);
        DataUrl = addDairy;
        //初始化界面
        init();
        //监听注册按钮
        goBack.setOnClickListener(this);
    }

    //初始化页面
    public void init(){
//        dairy.setText(oldContent);
        String nowtime = String.valueOf(new Date());
        time.setText(nowtime);
        String ynum =  String.valueOf(0);
        num.setText(ynum);
        //主Activity 获取用户登录信息
        SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取存储的ID
        userID =  sp.getInt("userID", 1);
        folderID = sp.getInt("folderID", 1);
        Log.d("now log",userID + "" + folderID);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            if (dairy.getText().toString().length() > 0) {
                //保存数据
                sendRequestWithOkHttp();
            }
            //关闭activity
            finish();
        }
    }
    //发送请求
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置请求路由
                String url = DataUrl
                        + "?title=" + dairy.getText().toString()
                        + "&folderID=" + folderID
                        + "&userID=" + userID ;
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url).get()
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //处理结果
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object=new JSONObject(jsonData);
            String code = object.getString("code");
            String msg = object.getString("msg");
            //日志
            Log.d("code and msg", " ：" + code +"," + msg);
            //提示框
            Looper.prepare();
            //fragment 操作提示框
            Toast toast;
            toast = Toast.makeText(AddDairyActivity.this, msg, Toast.LENGTH_SHORT);
//            更改toast位置
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Looper.loop();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
