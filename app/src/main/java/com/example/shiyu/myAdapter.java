package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class myAdapter extends ArrayAdapter {
    private final int ItemId;
    public myAdapter(Context context, int headImage, List<myBean> obj){
        super(context,headImage,obj);
        ItemId = headImage;//这个是传入我们自己定义的界面

    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final myBean myBean = (myBean) getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(ItemId,null);//这个是实例化一个我们自己写的界面Item
        LinearLayout linearLayout = view.findViewById(R.id.item_main);
        TextView headTitle = view.findViewById(R.id.item_title);
        TextView headTime = view.findViewById(R.id.item_time);
        assert myBean != null;
        headTitle.setText(myBean.getTitle());
        headTime.setText(myBean.getTimeDis());

        linearLayout.setOnClickListener(new View.OnClickListener() {//检查哪一项被点击了
            @Override
            public void onClick(View view) {
                //跳转提示
                Toast.makeText(getContext(),"你点击了第"+position+"项",Toast.LENGTH_SHORT).show();
                //跳转页面
                Bundle bundle = new Bundle();
                bundle.putInt("ID",myBean.getID());
                Intent intent;
                //判断跳转页面
                if (myBean.getType().equals("dairy")) {
                    bundle.putString("content", myBean.getTitle());
                    bundle.putString("time",myBean.getTimeDis());
                    intent = new Intent(getContext(), DairyDetailActivity.class);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                } else {
                    bundle.putString("content", myBean.getTitle());
                    intent = new Intent(getContext(),EachFolderActivity.class);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }
            }
        });
        return view;
    }

}
