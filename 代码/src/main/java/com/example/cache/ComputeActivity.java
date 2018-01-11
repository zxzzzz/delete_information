package com.example.cache;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by zx on 16-9-2.
 */
public class ComputeActivity extends Activity{
    ListView listView;
    static String COM_TAG="COMPUTE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.list);
       AppInfoAdapter appInfoAdapter=new AppInfoAdapter(MainActivity.appInfos,this);
        //测试是否有数据  为啥会有重复的？？？  还有没有的，类似QQ，微信
        for (AppInfo app:MainActivity.appInfos){
            Log.i(COM_TAG,app.getLabel());
            Log.i(COM_TAG,app.getPckName());
            Log.i(COM_TAG,app.getCacheSize()+"kb");
            Log.i(COM_TAG,app.getDataSize()+"kb");
            Log.i(COM_TAG,app.getCodeSize()+"kb");
        }
        listView=(ListView)findViewById(R.id.list_item);
        Log.i(COM_TAG,"设置adapter");
        listView.setAdapter(appInfoAdapter);
        Log.i(COM_TAG,"设置adapter成功");
    }
}
