package com.example.shiyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class selectAdapter extends ArrayAdapter {
    private final int ItemId;

    public selectAdapter(Context context, int headImage, List<myBean> obj){
        super(context,headImage,obj);
        ItemId = headImage;//这个是传入我们自己定义的界面
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final myBean myBean = (myBean) getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(ItemId,null);//这个是实例化一个我们自己写的界面Item
        LinearLayout selectLinearLayout = view.findViewById(R.id.item_main_select);
        TextView headTitle = view.findViewById(R.id.item_title);
        TextView headTime = view.findViewById(R.id.item_time);
        final CheckBox headbox = view.findViewById(R.id.check);
        assert myBean != null;
        headTitle.setText(myBean.getTitle());
        headTime.setText(myBean.getTimeDis());

        headbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"你点击了第"+position+"项"
                        + headbox.isChecked() + myBean.getID(),Toast.LENGTH_SHORT).show();
                //标记选中状态
//                    myBean.setIsSelect(headbox.isChecked());
//                setIsSelected(isSelected);
//                myCallBack.removeSelete(myBean[position]);
            }
        });
        return view;
    }
    //这是回调函数
//    public interface MyCallBack{
//        void getSelete(String s);
//        void removeSelect(String s);
//    }
//    public void setCallBack(MyCallBack myCallBack) {
//        this.myCallBack = myCallBack;
//    }
//    public static HashMap<Integer,Boolean> getIsSelected(){
//        return isSelected;
//    }
//    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
//        selectAdapter.isSelected = isSelected;
//    }
}
