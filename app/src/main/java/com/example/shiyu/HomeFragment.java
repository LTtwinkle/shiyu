package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class HomeFragment extends Fragment {
    private View root;
    private ListView listView;
    private Integer userID;
    private String DataUrl = "http://192.168.43.212:3000/getAllDairy";
    private TextView listEnd;

    private List<myBean> myBeanList = new ArrayList<>();//用来存放数据的数组
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化list列表
        Log.d("begin init","ready go");

        //切换下一个fragment
        TextView folder_frag = root.findViewById(R.id.folder);
        folder_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBackValue.SendMessageValue("folder");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        return root;
    }
    //每次进入刷新
    public void onStart() {
        super.onStart();
        init();
        callBackValue.SendMessageValue("dairy");
    }
    //初始化listview界面
    private void init(){
        //清除缓存数据
        list.clear();
        myBeanList.clear();
        listView = (ListView) root.findViewById(R.id.list_view);
        //请求数据
        sendRequestWithOkHttp();
        createDefaultFolder(userID);
    }

    // 接收来自activity的数据
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        userID =  ((MainActivity)context).toValue();
//        回调函数
        callBackValue = (CallBackValue) getActivity();
    }
    //回调函数
    private CallBackValue callBackValue;
//    //定义一个接口，向父activity传递信息。让“下一步”按钮可以被点击
    public interface CallBackValue{
        public void SendMessageValue(String type);
    }

    //发送请求
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                //设置url
                String url = DataUrl + "?userID=" + userID;
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
    //获取默认文件夹ID
    private void createDefaultFolder(final Integer id) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url = "http://192.168.43.212:3000/getDefaultFolderID?userID="
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
    //保存folderID
    private void saveFolderID(String jsonData) {
        try {
            JSONObject object=new JSONObject(jsonData);
            String code = object.getString("code");
            String msg = object.getString("msg");
            //日志
            Log.d("code and msg", code +"," + msg);
            //跳转页面
            if (code.equals("200")) {
                JSONObject obj=new JSONObject(jsonData);
                // 操作成功
                if (obj.getString("code").equals("200")) {
                    // 打印用户ID
//                    JSONObject data = object.getJSONObject("data");
                    int folderID = obj.getInt("data");
                    Log.d("folderID",String.valueOf(folderID));
                    //保存用户重要信息
                    SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("folderID", folderID);
                    editor.apply();
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
}
