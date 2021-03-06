package com.example.shiyu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DairyDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Integer dairyID;
    private String oldContent;
    private String oldTime;
    private TextView num;
    private EditText dairy;
    private TextView time;
    private String updateDairy = "http://192.168.43.212:3000/updateDairy";
    private String DataUrl;
    private TextView goBack;
    private TextView share;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_detail);
        //获取路由
        DataUrl = updateDairy;
        //获取传递数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        Log.d("budle", String.valueOf(bundle));
        dairyID = bundle.getInt("ID");
        oldTime = bundle.getString("time");
        oldContent = bundle.getString("content");
        //获取元素
        dairy = findViewById(R.id.dairy);
        time = findViewById(R.id.record_time);
        num = findViewById(R.id.count_num);
        init();
        goBack = findViewById(R.id.back);
        share = findViewById(R.id.share);
        goBack.setOnClickListener(this);
        share.setOnClickListener(this);
        //实时监听输入内容
        dairy.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                //保存数据
                sendRequestWithOkHttp();
            }
        });
    }

    //初始化页面
    public void init(){
        dairy.setText(oldContent);
        time.setText(oldTime);
        String ynum =  String.valueOf(oldContent.length());
        num.setText(ynum);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            //关闭activity
            finish();
        }
        if (v.getId() == R.id.share) {
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, dairy.getText().toString());
            startActivity(Intent.createChooser(textIntent, "分享"));
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
                        + "&dairyID=" + dairyID ;
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
