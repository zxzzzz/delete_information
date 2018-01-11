package com.example.cache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by zx on 16-9-13.
 */
//显示文件的详细信息
public class TextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_layout);
        Log.i(FileList.FILE_LIST_TAG,"成功跳转到TextActivity ");
        TextView content=(TextView)findViewById(R.id.content);
        Intent intent=getIntent();
        String text=intent.getStringExtra(FileList.pName);
        Log.i(FileList.FILE_LIST_TAG,"内容是："+text);
        content.setText(text);
    }
}
