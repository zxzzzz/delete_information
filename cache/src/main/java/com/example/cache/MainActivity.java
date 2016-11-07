package com.example.cache;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//鉴于启动速度有点慢，决定加个splash,网上喷的很多

//点完清理缓存在点计算缓存的话，缓存没变为0,必须要退出重新计算？？？
public class MainActivity extends AppCompatActivity {
   // MyObserver myObserver=new MyObserver();
    static String TAG="MY_LOG";
    static String TAG_T="FILE_LOG";
    static String TAG_TEST="TEST";
    static String TAG_P="PACKAGENAME";//hander要发送的字段
   // boolean canSplash;//判断是否在Splash阶段
    static final int SPLASH_ONE=3;
    static  ArrayList<String > PACKAGENAME=new ArrayList<String>();//所有应用的包名
    long cacheSize;//缓存大小
    long codeSize;//应用程序大小
    long dataSize;//数据大小
    long freeSize;///data。。
    long allCacheSize;//总缓存大
    List<String >  packagesName=new ArrayList<String>();//包名,系统识别应用的标识
    String name;//label名，类似于"微信"
    Drawable icon;//应用程序图标
    Button writeButton;//向sharedPreferences写入数据
    Button computeSize;
    Button clearCache;
    Button readButton;//读取sharedPreferences
    Button readXML;
    Button readData;//读取/data/data
    SharedPreferences sharedPreferences;//
    SharedPreferences.Editor editor;//
    String content;//sharedPreferences.xml的文本内容
    int count=0;//AppInfo的索引
    //String permission;
    PackageManager packageManager;
    List<ResolveInfo> applicationInfoList=new ArrayList<ResolveInfo>();
    public static List<AppInfo> appInfos=new ArrayList<AppInfo>();
   // ListView listView=null;
  //  PackageInfo packageInfos=new PackageInfo();

    //开启一个新线程
    android.os.Handler myHandler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SPLASH_ONE:
                    ViewSwitcher switcher=(ViewSwitcher)findViewById(R.id.viewSwitch);
                    //    ImageView imageView=(ImageView)switcher.getChildAt(0);
                    //     imageView.setImageResource(0);
                    switcher.removeViewAt(0);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏模式
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window= MainActivity.this.getWindow();
        window.setFlags(flag,flag);
        setContentView(R.layout.activity_main);
        //Message message=new Message();
        //message.what=SPLASH_ONE;
        myHandler.sendEmptyMessageDelayed(SPLASH_ONE,2000);
        readData=(Button)this.findViewById(R.id.data_data);
        writeButton=(Button)this.findViewById(R.id.write);
        readButton=(Button)this.findViewById(R.id.read);
        computeSize=(Button)this.findViewById(R.id.computeSize);
        clearCache=(Button)this.findViewById(R.id.clearCache);
        readXML=(Button)this.findViewById(R.id.read_prefs);
        Log.i(TAG_T,"获取prefs");
        sharedPreferences=this.getSharedPreferences("myPre",MODE_PRIVATE);
        Log.i(TAG_T,"获取prefs成功");
        editor=sharedPreferences.edit();
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.write:
                        writePres();
                        break;
                    case R.id.read:
                        readPres();
                        break;
                    case R.id.computeSize:
                        Log.i(TAG,"开始执行querySize");
                        queryAllSize();
                        Log.i(TAG,"开启Intent");
                        Intent intent=new Intent(MainActivity.this,ComputeActivity.class);
                        startActivity(intent);
                        Log.i(TAG,"开启Activity ");
                        break;
                    case R.id.clearCache:
                        Log.i(TAG,"开始清理缓存");
                        clearCache();
                        Log.i(TAG,"执行完毕clearCache");
                        break;
                    case R.id.read_prefs:
                        Log.i(TAG,"执行readFile()");
                        readFile("/data/data/com.example.cache/shared_prefs/myPre.xml");
                        break;
                    case R.id.data_data:
                        Intent intent1=new Intent(MainActivity.this,ReadData.class);
                        startActivity(intent1);
                        break;
                    default:
                        break;

                }
            }
        };
        readData.setOnClickListener(listener);
        writeButton.setOnClickListener(listener);
        readButton.setOnClickListener(listener);
        computeSize.setOnClickListener(listener);
        clearCache.setOnClickListener(listener);
        readXML.setOnClickListener(listener);
        //测试一下数据是否符合
        //querySize("com.UCMobile");
        //Log.i(TAG_T,"开始执行获取文件");

        //AppInfoAdapter appInfoAdapter=new AppInfoAdapter(appInfos,this);


    }
    //当AIDL文件改变时 cleanProject ---make module app
    //AIDL文件放在/main/aidl/包名/下，编译生成的java文件在Packages里可以看到
    class MyObserver extends IPackageStatsObserver.Stub{

//计算大小，通过传回的stat参数
        //最后调用的这个方法，执行完毕querySize和clearCache ？？线程阻塞？？？？？如果是进程内部使用oneway关键字不起作用，无法传输完数据立刻返回
        @Override
        public void onGetStatsCompleted(PackageStats stat, boolean success) throws RemoteException {
            Log.i(TAG, "开始计算大小,执行onGetStstsCompleted");
            cacheSize = stat.cacheSize;
            allCacheSize+=cacheSize;
            codeSize = stat.codeSize;
            dataSize = stat.dataSize;
            AppInfo appInfo = appInfos.get(count++);
            //appInfo.setPckName(stat.packageName);
            //invoke是如何工作的？oneway关键字起作用了吗？getPackageSizeInfo()里不是调用onGetStatsCompleted吗？
            if (stat.packageName.equals(appInfo.getPckName())) {
                //这样判断的话，所有的size都为0，说明size与应用不匹配
                PACKAGENAME.add(stat.packageName);//获取应用的包名
                Log.i(ReadData.FILETAG,stat.packageName);
                appInfo.setCacheSize(cacheSize);
                appInfo.setCodeSize(codeSize);
                appInfo.setDataSize(dataSize);
            }
            //String.equal(String)  与 ==有什么区别？？？
            if (stat.packageName.equals("com.UCMobile") ){
                Log.i(TAG_TEST, stat.packageName + "缓存大小" + cacheSize);
                Log.i(TAG_TEST, stat.packageName + "代码大小" + codeSize);
                Log.i(TAG_TEST, stat.packageName + "数据大小" + dataSize);
            }
        }

    }
    //计算全部缓存大小
    void queryAllSize(){
        if (packageManager==null)
                packageManager=this.getPackageManager();
        //Intent.ACTION_MAIN不是启动activity吗   为啥没有qq?
        Intent intent=new Intent(Intent.ACTION_MAIN,null);
   //     intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        applicationInfoList=packageManager.queryIntentActivities(intent,0);
        //根据名字排序，如果不排序的话，只能显示系统应用和部分第三方应用，为什么？？？？？？？？？？
        Collections.sort(applicationInfoList,new ResolveInfo.DisplayNameComparator(packageManager));
        try {
            String packageName;
            for (ResolveInfo app : applicationInfoList) {
                Log.i(TAG, "得到包名和权限");
                packageName = app.activityInfo.packageName;
                Log.i(TAG_P,packageName);
                name=(String) app.loadLabel(packageManager);
                icon=app.loadIcon(packageManager);
                //集合初始化和赋值的问题？？
                //判断是否出现重复包名，为啥会出现重复包名呢？一个应用程序里的Intent.ACTION_MAIN,category:default不是有且只有一个吗？？
                if (!packagesName.contains(packageName)) {
                    packagesName.add(packageName);
                    AppInfo appInfo = new AppInfo();
                    appInfo.setIcon(icon);
                    appInfo.setPckName(packageName);
                    appInfo.setLabel(name);
                    appInfos.add(appInfo);
                    querySize(packageName);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"计算application信息时出错");

        }
    }
    //计算单个应用缓存大小大小
    void querySize(String pck){
        //如何获取PackageManager实例？
        if(packageManager==null)
            packageManager=this.getPackageManager();
        try {
            Log.i(TAG,"开始执行querySize");
            //反射得到PackageManager.getPackageSizeInfo(String,IPackageDataObserver)方法
            Method getSizeInfo=packageManager.getClass().getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
            //调用getPackageSizeInfo，将计算得到的值传递给PackageStats
            Log.i(TAG,""+getSizeInfo.toString());
            Log.i(TAG,"执行完毕反射");
            getSizeInfo.invoke(packageManager,pck,new MyObserver());
            Log.i(TAG,"执行完毕invoke");
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,".....getsizeInfo出错..");

        }


    }

    //清理缓存
    void clearCache(){
        if (packageManager==null)
            packageManager=this.getPackageManager();
        try {
            /*
             *如何找到隐藏的API？？？？？
			 * freeStorageAndNotify  :freeStorageSize ： The number of bytes of storage to be freed by
			 * the system. Say if freeStorageSize is XX, and the current free
			 * storage is YY, if XX is less than YY, just return. if not free
			 * XX-YY number of bytes if possible.
			 * 当所要清除的空间不够时，系统为满足要求，就释放缺少的空间大小，即清理所有的缓存大小
			 */
            //方法名写错了。。。
            Method freeStorage=packageManager.getClass().getMethod("freeStorageAndNotify",Long.TYPE, IPackageDataObserver.class);
            Log.i(TAG,"方法名为："+freeStorage.toString());
            Long cacheSizeInfo=Long.valueOf(getDataSize()-1l);
            Log.i(TAG,"调用freeStorage");
            Log.i(TAG_P,"/data/data大小"+cacheSizeInfo);
            Log.i(TAG_P,"allCacheSize"+allCacheSize);
            freeStorage.invoke(packageManager, cacheSizeInfo, new IPackageDataObserver() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        Log.i(TAG,"调用onRemoveCompleted（）");
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"清理过程失败");

        }


            Toast.makeText(MainActivity.this,"已清理缓存大小为"+allCacheSize+"kb",Toast.LENGTH_SHORT).show();
    }

    //计算/data 目录大小
    long getDataSize(){
        Log.i(TAG,"计算/data目录大小");
        File file= Environment.getDataDirectory();
        if (file==null)
                freeSize=0L;
        String path=file.getPath();
        Log.i(TAG,path);
        //计算文件系统的大小 与数量
        StatFs statFs=new StatFs(path);
        long number=statFs.getBlockCountLong();
        long size=statFs.getBlockSizeLong();
        freeSize=number*size;
        Log.i(TAG,"/data目录大小为"+freeSize+"kb");
        return freeSize;


    }


    //测试添加SharedPreferences文件
    void writePres(){
        editor.putInt("int-1",33);
        editor.putString("String-1","good");
        editor.commit();
    }
    //测试得到SharedPreferences返回值
    void readPres(){
                int getInt=sharedPreferences.getInt("int-1",0);
                String getString=sharedPreferences.getString("String-1",null);
                int getInt2=sharedPreferences.getInt("int-2",333333);
                Toast.makeText(MainActivity.this,getInt+"/n"+getInt2+"/n"+getString,Toast.LENGTH_SHORT).show();
    }



//测试是否能获取本应用SharedPreferences文件，可以获取到==
    void readFile(String name){
        Log.i(TAG_T,"文件名："+name);
        File file=new File(name);
        try {
            //InputStream fileInputStream=new FileInputStream(file);
            if (file.isDirectory())
                Log.i(TAG_T, "文件是目录");
            else {
                Log.i(TAG_T,"文件存在");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    Log.i(TAG_T, line);
                    content += line;
                }
                if (content != null) {
                    Log.i(TAG_T, "文件成功获取");
                    Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG_T, "文件获取失败");
                }
                //   Log.i(TAG,content);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
