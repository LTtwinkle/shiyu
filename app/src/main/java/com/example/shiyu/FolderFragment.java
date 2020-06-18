package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import org.jetbrains.annotations.NotNull;
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

public class FolderFragment extends Fragment {
    private View root;
    private ListView listView;
    private Integer userID;
    private String folderUrl = "http://192.168.43.212:3000/getFolderList";
    private TextView listEnd;

    private List<myBean> myBeanList = new ArrayList<>();//用来存放数据的数组
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_folder, container, false);
        Log.d("begin init","ready go");
        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FolderFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
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
        listView = (ListView) root.findViewById(R.id.list_view);
        //请求数据
        sendRequestWithOkHttp();
    }
    // 接收来自activity的数据
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        userID =  ((MainActivity)context).toValue();
    }

    //发送请求
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url = folderUrl + "?userID=" + userID;
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
                            Integer folderID = obj.getInt("folderID");
                            String title=obj.getString("title");
                            String time = obj.getString("lastTime");
                            String type = "folder";
                            Log.d("title",title);
                            //存入map
                            map.put("dairyID", folderID);
                            map.put("title", title);
                            map.put("time",time);
                            //ArrayList集合
                            list.add(map);
                            myBeanList.add(new myBean(type,title,time,folderID));

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
            toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
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
                myAdapter list_item = new myAdapter(getActivity(), R.layout.item_list, myBeanList);
                listView.setAdapter(list_item);
            }
        }
    };
}
