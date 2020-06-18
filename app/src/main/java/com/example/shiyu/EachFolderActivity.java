package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EachFolderActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private Integer userID;
    private TextView title;
    private String DataUrl = "http://192.168.43.212:3000/getDairyList";
    private Integer folderID;
    private String oldContent;

    private List<myBean> myBeanList = new ArrayList<>();//用来存放数据的数组
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();

//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.fragment_folder);
        title = findViewById(R.id.title);

        //获取传递数据
        Intent intent = getIntent();
        //从intent对象中把封装好的数据取出来
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        Log.d("budle", String.valueOf(bundle));
        folderID = bundle.getInt("ID");
        oldContent = bundle.getString("content");
        Log.d("the bundle",folderID + "" );

        TextView back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }
    //每次进入刷新
    public void onStart() {
        super.onStart();
        init();
    }
    //初始化listview界面
    private void init(){
        //清除缓存数据
        list.clear();
        myBeanList.clear();
        listView = (ListView) findViewById(R.id.list_view);

        //主Activity 获取用户登录信息
        SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取存储的ID
        userID =  sp.getInt("userID", 1);
        title.setText(oldContent);
        //请求数据
        sendRequestWithOkHttp();
    }

    //发送请求
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url = DataUrl
                        + "?userID=" + userID
                        +"&folderID=" + folderID;
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
    //处理结果
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object=new JSONObject(jsonData);
            String code = object.getString("code");
            String msg = object.getString("msg");
            //日志
            Log.d("code and msg", " ：" + code +"," + msg);
            //跳转页面
            if (code.equals("200")) {
                //获取对象数组
                JSONArray data = object.getJSONArray("data");
                if ( data.length() > 0) {
                    Log.d("the lenght ", String.valueOf(data.length()));
                    //显示最新数据
                    for (int i = data.length() -1; i >= 0 ; i--){
                        JSONObject obj = data.getJSONObject(i);
                        Map<String, Object> map=new HashMap<String, Object>();
                        try {
                            Integer dairyID = obj.getInt("dairyID");
                            String title=obj.getString("title");
                            String time = obj.getString("time");
                            String type = "dairy";
                            Log.d("title",title);
                            //存入map
                            map.put("dairyID", dairyID);
                            map.put("title", title);
                            map.put("time",time);
                            //ArrayList集合
                            list.add(map);
                            myBeanList.add(new myBean(type,title,time,dairyID));

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    //传递消息
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }

            //提示框
            Looper.prepare();
            //fragment 操作提示框
            Toast toast;
            toast = Toast.makeText(EachFolderActivity.this, msg, Toast.LENGTH_SHORT);
//            更改toast位置
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Looper.loop();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Handler运行在主线程中(UI线程中)，  它与子线程可以通过Message对象来传递数据
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                myAdapter list_item = new myAdapter(EachFolderActivity.this, R.layout.item_list, myBeanList);
                listView.setAdapter(list_item);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            //关闭activity
            finish();
        }
    }
}
