package com.example.cache;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zx on 16-9-10.
 */
//申请root权限  /读取shared_prefs文件
public class ReadData extends Activity{
    static String FILETAG="FILETAG";
    static HashMap<String ,String> hashMap=new HashMap<String, String>();//存储文件名和内容
    Button grand;
    Button readData;
    Button readXML;
    Button getFileList;//获取xml的文件名
    Button showName;
    Button writeToSql;//写入数据库
    Button searchPrivate;
    DatabaseHelper databaseHelper;//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_data);
        readData=(Button)findViewById(R.id.readFile);
        readXML=(Button)findViewById(R.id.readXML);
        grand=(Button)findViewById(R.id.grand);
        getFileList=(Button)findViewById(R.id.getFileList);
        showName=(Button)findViewById(R.id.showName);
        writeToSql=(Button)findViewById(R.id.writeToSql);
        searchPrivate=(Button)findViewById(R.id.search);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.grand:
                        grandSU();
                        break;
                    case R.id.readFile:
                        readFile("/data/data/com.tencent.mobileqq/shared_prefs/1144844635.xml");
                        break;
                    case R.id.readXML:
                        break;
                    case R.id.getFileList:
                        String name;
                        String labelName;
                        //android.setting/没有shared_prefs???
                        for (AppInfo appInfo:MainActivity.appInfos) {
                            labelName=appInfo.getLabel();
                            name = "/data/data/" + appInfo.getPckName()+ "/shared_prefs";
                            File file=new File(name);
                            //如果存在的话继续执行  不存在的话就跳过
                            if (file.exists())
                                getFileList(name,labelName);
                        }
                        break;
                    case R.id.showName:
                        Log.i(FILETAG,"显示列表");
                        Intent hashIntent=new Intent(ReadData.this,FileList.class);
                        Log.i(FILETAG,"开始显示");
                        startActivity(hashIntent);
                    break;
                    case R.id.search:
                        Log.i(FILETAG,"开始查询");
                        Intent searchIntent=new Intent(ReadData.this,SearchPrivate.class);
                        startActivity(searchIntent);
                    /*
                    case R.id.writeToSql:
                        writeToSql("my","ssss");
                    */
                }
            }
        };
        readData.setOnClickListener(listener);
        readXML.setOnClickListener(listener);
        grand.setOnClickListener(listener);
        getFileList.setOnClickListener(listener);
        showName.setOnClickListener(listener);
        searchPrivate.setOnClickListener(listener);
       // writeToSql.setOnClickListener(listener);
    }

    //获取su授权
    public void grandSU(){
        try {
            //调用su
            Process process = Runtime.getRuntime().exec("su");
            //获取子进程的输出流。输出流被传送给由该 Process 对象表示的进程的标准输入流。
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String command;
            //以读写的方式重新挂载/dev/.../system
            os.writeBytes("mount -o remount,rw /dev/block/mtdblock3 /system\n");
            //使用busybox将 /data/data/com.../su 复制到/system/bin/su下面
            os.writeBytes("busybox cp /data/data/com.koushikdutta.superuser/su /system/bin/su\n");
            //将su的拥有者改为指定的用户和组  即0：0
            os.writeBytes("busybox chown 0:0 /system/bin/su\n");
            //以4755(rwx+rx+rx)(所有者+用户组+其他组)的权限执行 su
            os.writeBytes("chmod 4755 /system/bin/su\n");
            Log.i(FILETAG,"提取权限");
            //授予所有的share_prefs文件下的xml 777权限，解决了只能用RE管理器或者ADB shell手动授予权限
            for (int i=0;i<MainActivity.PACKAGENAME.size();i++) {
                Log.i(FILETAG, "for");
                command = "chmod -R 777 /data/data/" + MainActivity.PACKAGENAME.get(i) + "/shared_prefs\n";
                Log.i(FILETAG, "os开始");
                os.writeBytes(command);
                Log.i(FILETAG, "777成功");
            }
            //退出
            os.writeBytes("exit\n");
            os.flush();


        }catch (IOException e){}



    }
    //遍历节点
    public void listNodes(Element e,SQLiteDatabase db,String labelName) {
        Log.i(FILETAG,"当前节点的名称：" + e.getName());
        Log.i(FILETAG,"当前节点的值： "+e.getText());
        String string1=e.getName();
        String string2=e.getText();
        String string3;
        String string4;
        String ts1=null;
        String ts2=null;
        String ts3=null;
        String ts4=null;
        List<Attribute>  attributes=e.attributes();
        for (Attribute attribute:attributes){
                Log.i(FILETAG,"当前节点属性名称： "+attribute.getName());
                Log.i(FILETAG,"当前节点属性值: "+attribute.getText());
                string3=attribute.getName();
                string4=attribute.getText();
                ts1=string1.replace(":","_");
                ts2=string2.replace(":","_");
                ts3=string3.replace(":","_");
                ts4=string4.replace(":","_");
                if (!e.getText().isEmpty()) {
                    Log.i(FILETAG,"text不为空");
                    insert(db, labelName, ts1,ts2,ts3,ts4);
                }
                else if (e.getText().isEmpty())
                    insert(db,labelName,ts1,"空",ts3,ts4);
        }
        Iterator<Element> elementIterator=e.elementIterator();
        while (elementIterator.hasNext()){
            Element element=elementIterator.next();
            listNodes(element,db,labelName);

        }

    }

    private void insert(SQLiteDatabase db,String labelName,String name, String text, String name1, String text1) {
        Log.i(FILETAG,"插入的："+name+"  ");
        db.execSQL("insert into "+labelName+" values(? ,? ,? ,?)",new String[]{"_"+name,"_"+text,"_"+name1,"_"+text1});
    }

    /*获得指定文件下的所有文件的名字列表
        name:w文件路径
        label：应用名称
     */
    public void getFileList(String name,String label) {
        File[] files = new File(name).listFiles();
        String fileName;//要写入的文件全ming
        Log.i(FILETAG, name);
        for (File file : files) {
            Log.i(FILETAG, file.toString());
            fileName=file.toString();
            readToFile(fileName,label);
         //   writeToFile(lastName);
        }
    }

    //读取所有的xml文件

    public void readToFile(String name,String label) {
        String writeName;//要写入文件的文件名：包名+xml名
        String lastName;//XMl名
        String []nameList;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            Log.i(FILETAG,"readName:"+name);
            BufferedReader reader = new BufferedReader(new FileReader(name));
            String s;
            Log.i(FILETAG,"readToFile");
            while ((s = reader.readLine()) != null) {
                Log.i(FILETAG,"开始读取");
                stringBuilder.append(s + "\n");
                Log.i(FILETAG,"读取成功");
            }
            reader.close();
            nameList=name.split("/");
            lastName=nameList[nameList.length-1];
            String finalName=lastName.replace(".","_");
            String finalName2=finalName.replace("-","_").replace(":","_");
            writeName=label+"."+lastName;
            Log.i(FILETAG,writeName);
            hashMap.put(writeName,stringBuilder.toString());//存储
            writeToFile(writeName,stringBuilder.toString());
            writeToSql(label,"_"+finalName2,name);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(FILETAG,"读取失败");
        }

    }

    /*
        将文件写入指定数据库
        pckName:数据库名
        fileName:表名
        name:要读取的xml路径

     */
    //int i=0;
    public void writeToSql(String pckName,String fileName,String name){
        Log.i(FILETAG,"写入数据库");
     //   int t=i++;
        String createTable="create table if not exists "+fileName+" (name varchar(255),value varchar(255),attribute_name varchar(255),attribute_value varchar(255))";
        databaseHelper= new DatabaseHelper(this,pckName+".db",1);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        Log.i(FILETAG,".....");
        sqLiteDatabase.execSQL(createTable);
        readXML(name,sqLiteDatabase,fileName);
        Log.i(FILETAG,"成功");


    }


    //将文件写入指定文件夹
    public void writeToFile(String name,String text){
        try {
            Log.i(FILETAG,"text"+text);
            Log.i(FILETAG,"writeToFile");
            //String dirName=this.getFilesDir().getPath();
          //  Log.i(FILETAG,"dirName"+dirName);
//            BufferedReader bufferedReader = new BufferedReader(new StringReader(text));
//            Log.i(FILETAG,"buffered");
//            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(name)));
//            Log.i(FILETAG,"print");
//            int line = 1;
//            String s;
//            while ((s = bufferedReader.readLine())!=null) {
//                Log.i(FILETAG,"正在写入");
//                printWriter.println(line++ + ":" + s);
//                Log.i(FILETAG,"写入成功");
//                printWriter.close();
//            }
            File file=new File(name);
            if (!file.exists()) {
                FileOutputStream fileOutputStream = openFileOutput(name, MODE_APPEND);
                PrintStream printStream = new PrintStream(fileOutputStream);
                printStream.println(text);
                Log.i(FILETAG, "写入成功");
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.i(FILETAG,"写入失败");
        }
    }
    //测试是否能阅读XML文件并输出
    public void readFile(String name){
        try {
            File file = new File(name);
            if (file!=null){
                Log.i(FILETAG,"文件不为空");

            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while (bufferedReader.readLine()!=null){
                line=bufferedReader.readLine();
                Log.i(FILETAG,line);
                Toast.makeText(this,line,Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            Log.i(FILETAG,"读取xml有问题");
        }
    }


    /*解析XML 文件
        name:XML路径
        db：要写入的数据库
        labelName：要写入的表名
     */
    public void readXML(String name,SQLiteDatabase db,String labelName){
        Log.i(FILETAG,"当前阅读的XML文件名：  "+name);
        try {
            SAXReader saxReader=new SAXReader();
            Document document=saxReader.read( new File(name));
            if (document!=null) {
                Log.i(FILETAG,"document不为空");
                Element rootElement = document.getRootElement();
                listNodes(rootElement,db,labelName);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            Log.i(FILETAG,"获取document时有问题");
        }

    }
}


