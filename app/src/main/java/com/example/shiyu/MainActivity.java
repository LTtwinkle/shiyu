package com.example.shiyu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements HomeFragment.CallBackValue {
    private String nowfrag;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        //隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_main);
        TextView fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nowfrag.equals("dairy")) {
                    //在日记列表页，创建新日记，跳转页面
                    Log.d("add new ","true");
                    Intent intent=new Intent(MainActivity.this, AddDairyActivity.class);
                    startActivity(intent);
                } else {
                    //在文件夹列表页，创建新文件夹
                    Log.d("create folder",nowfrag);
                }
            }
        });
    }
    @Override
    public void SendMessageValue(String type) {
        nowfrag = type;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //利用Fragment的生命周期onAttach()方法，获取宿主activity
    public int toValue(){
        //主Activity 获取用户登录信息
        SharedPreferences sp = super.getSharedPreferences("data", Context.MODE_PRIVATE);
        return  sp.getInt("userID", 1);
    }
}
