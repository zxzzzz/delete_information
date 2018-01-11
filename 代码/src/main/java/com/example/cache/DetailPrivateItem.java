package com.example.cache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 16-9-20.
 */
/*
    列表跳跃显示，如何判断为空？
    当没有数据可以传递的时候如何 判断哪个对象为空？？
 */
//显示某个应用的数据库所查询到的关键字
public class DetailPrivateItem extends Activity {
    ListView showDetail;
    static int i;
    List<PrivateItem> privateItems=new ArrayList<PrivateItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_item);
        showDetail=(ListView)findViewById(R.id.showDetail);
        Intent intent=getIntent();
        boolean empty=true;//判断该数据库是否有查询到关键词
        Bundle bundle=intent.getBundleExtra("bundle");
        //得到相应的PrivateItem类
        for (String otherTable:SearchPrivate.addTable){
            PrivateItem i=(PrivateItem)bundle.getSerializable(otherTable);
            if (i!=null) {
                privateItems.add(i);
                empty=false;
            }
        }
        Log.i(SearchPrivate.DETAILTAG,"我看看循环了几次"+i++);
     if (empty) {
            Toast.makeText(this, "没有匹配的数据哦～", Toast.LENGTH_SHORT).show();

        }
            PrivateAdapter adapter = new PrivateAdapter(this, privateItems);
            showDetail.setAdapter(adapter);

    }
}
