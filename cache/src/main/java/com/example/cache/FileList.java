package com.example.cache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zx on 16-9-13.
 */
//将share_prefs的文件名显示出来，点击显示详细信息
public class FileList extends Activity {
    static String FILE_LIST_TAG = "mmm";
    ListView hashListView;//展示名称列表
    ArrayList<String> fileName = new ArrayList<String>();
    Set set = new HashSet();//存储名字和详细内容

    static String pName;//名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);
        Log.i(FILE_LIST_TAG, "成功跳转到fileActivity");
        hashListView = (ListView) findViewById(R.id.fileListView);
        set = ReadData.hashMap.keySet();
        String n;
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            n = (String) object;
            fileName.add(n);
            Log.i(FILE_LIST_TAG, "转型成功");
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fileName);
        hashListView.setAdapter(arrayAdapter);
        hashListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pName = fileName.get(position);
                Log.i(FILE_LIST_TAG, "pName: " + pName);
                String text = ReadData.hashMap.get(pName);
                Intent textIntent = new Intent(FileList.this, TextActivity.class);
                textIntent.putExtra(pName, text);
                startActivity(textIntent);
            }
        });
        //悬浮菜单与listView绑定
        hashListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                Log.i(FILE_LIST_TAG,"contextMenu");
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.context_menu, menu);
            }
        });
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        Log.i(FILE_LIST_TAG,"contextMenu");
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.context_menu, menu);
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i(FILE_LIST_TAG,"selected");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.explain:
                Toast.makeText(this,"解释",Toast.LENGTH_SHORT).show();
                break;
            case R.id.refactor:
                Toast.makeText(this,"修改",Toast.LENGTH_SHORT).show();
                break;
            case R.id.look:
                Toast.makeText(this,"查看",Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;

    }
}
