package com.example.cache;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 16-9-19.
 */

//根据用户的需求查询数据库
public class SearchPrivate extends Activity {
    final static String DETAILTAG="hhhhhhhh";
    EditText input;//用户输入框

    ListView showList;//展示列表  应用程序名称
    Button searchButton;
    String inputContent;//输入内容
    List<String> pathDataNames;//所有数据库路径
    List<String> allDataBase=new ArrayList<String>();//应用程序/数据库名称
    static  ArrayList<String> addTable;
    //static  HashMap<String ,ArrayList<PrivateItem>>  hashMap=new HashMap<String, ArrayList<PrivateItem>>();//存储数据库名称及其对应的数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_private_layout);
        input=(EditText)findViewById(R.id.input);
        searchButton=(Button)findViewById(R.id.search);
        showList=(ListView)findViewById(R.id.listShowPrivate);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pathDataNames=getDatabasePath();
                getDataName();
                ArrayAdapter adapter=new ArrayAdapter(SearchPrivate.this,android.R.layout.simple_list_item_1,allDataBase);;
                showList.setAdapter(adapter);
            }
        });


        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String onlyName = allDataBase.get(position);

                    inputContent = input.getText().toString();
                    //          ArrayList<PrivateItem> listItem=hashMap.get(onlyName);
                    //        PrivateItem n=new PrivateItem();
                    Intent intent = new Intent(SearchPrivate.this, DetailPrivateItem.class);
                    Bundle bundle = new Bundle();//将一个数据库中匹配的信息传递过去？？？？
                    //     bundle.putSerializable(onlyName,n);
                    addTable = new ArrayList<String>();
                    DatabaseHelper helper = new DatabaseHelper(SearchPrivate.this, onlyName, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    //查询表名
                    Cursor cursorOne = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
                    while (cursorOne.moveToNext()) {
                        String name = cursorOne.getString(0);
                        Log.i(DETAILTAG, "表名为：" + name);
                        addTable.add(name);
                        if (!name.equals("android_metadata")) {
                            Log.i(DETAILTAG, "要查询的表名为：" + name);
                            PrivateItem addItem = searchData(inputContent, db, name);
                            if (addItem != null)
                                bundle.putSerializable(name, addItem);
                        }
                    }
                    // ArrayList<String> tables=getTablePath("/data/data/com.example.cache/databases/"+onlyName);
//                for (String name:tables){
//                    Log.i(DETAILTAG,"表的路径为："+name);
//                    String[] tablesPath=name.split("/");
//                    String onlyTable=tablesPath[tablesPath.length-1];
//                    Log.i(DETAILTAG,"表名为："+onlyTable);
//                    addTable.add(onlyTable);
//                    PrivateItem addItem=searchData(inputContent,onlyName,onlyTable);
//                    bundle.putSerializable(name,addItem);
//                }
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }

        });

    }
    //在指定的数据库下,指定表下查询所给的关键字
    public PrivateItem searchData(String input, SQLiteDatabase sqliteData,String table){
            Log.i(DETAILTAG,"输入的内容是："+input);
//       DatabaseHelper dataHelper=new DatabaseHelper(this,db,1);
//       SQLiteDatabase  sqliteData=dataHelper.getWritableDatabase();
    //    tablePaths=getTablePath(path);
     //   ArrayList<PrivateItem> privateItemsList=new ArrayList<PrivateItem>();//一个数据库下的检索出来的数据
      //  for (String table:tablePaths){
            PrivateItem privateItems=new PrivateItem();
            boolean empty=true;
            Cursor cursor=sqliteData.rawQuery("select * from "+table+" where name like '%"+input+"%' or value like '%"+input+"%' or attribute_name like '%"+input+"%' or attribute_value like'%"+input+"%'",null);
            while (cursor.moveToNext()){
                empty=false;
                Log.i(DETAILTAG,"cursor不为空");
                privateItems.setName(cursor.getString(0));
                privateItems.setValue(cursor.getString(1));
                privateItems.setAttributeName(cursor.getString(2));
                privateItems.setAttributeValue(cursor.getString(3));
         //       privateItemsList.add(privateItems);
            }
        if (empty==false)
          return privateItems;
        else {
            return null;
        }
        }
        //
    // hashMap.put(onlyDatabase,privateItemsList);






//取得所有数据库名称
    private void getDataName() {
         // List<String> tablePaths;
        for (String path:pathDataNames) {
            String[] dataBase = path.split("/");
            String onlyDatabase = dataBase[dataBase.length - 1];
            Log.i(DETAILTAG,"数据库名称为："+onlyDatabase);
            if (onlyDatabase.matches(".+db$")) {
                Log.i(DETAILTAG, "匹配了" + onlyDatabase);
                allDataBase.add(onlyDatabase);
            }
        }
    }
//得到所有数据库的路径
    public ArrayList<String> getDatabasePath(){
        ArrayList<String> pathDataName=new ArrayList<String>();
        File  fileOne=new File("/data/data/com.example.cache/databases");
        File[] files;
        if (fileOne.isDirectory()) {
            files = fileOne.listFiles();
            String path;
            for (File file : files) {
                path = file.toString();
                Log.i(DETAILTAG, "数据库名称为：" + path);
                pathDataName.add(path);

            }
        }else {
            Log.i(DETAILTAG,"数据表不为目录");

        }
        return pathDataName;
    }

    //得到数据库下的所有表
//    public ArrayList<String> getTablePath(String dataName){
//        Log.i(DETAILTAG,"所在的数据库为："+dataName);
//        ArrayList<String> tableNames=new ArrayList<String>();
//        File fileOne=new File(dataName);
//        File[] files;
//        //if (fileOne.isDirectory()) {
//            files = fileOne.listFiles();
//            String path;
//            for (File file : files) {
//                path = file.toString();
//                Log.i(DETAILTAG, "数据表的名称为：" + path);
//                tableNames.add(path);
//            }
//        //}else {
//          //  Log.i(DETAILTAG, "数据库不为目录");
//       // }
//        return tableNames;
//
//    }

}
