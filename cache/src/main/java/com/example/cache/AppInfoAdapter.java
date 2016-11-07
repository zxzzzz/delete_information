package com.example.cache;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zx on 16-9-2.
 */
public class AppInfoAdapter extends BaseAdapter {
    List<AppInfo> appInfos;
    Context context;
//    LayoutInflater layoutInflater;

    public AppInfoAdapter(List<AppInfo> appInfos, Context context) {
        this.appInfos = appInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return appInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null) {
            //一开始写的if(convertView==null||convertView.getTag()==null),当convertview为null时，如果访问getTag()会产生异常吗？？？？
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.info_item, null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
        }else {
            //convertView=viewHolder.get
            viewHolder=(ViewHolder)convertView.getTag();


        }
        AppInfo appInfo=(AppInfo)getItem(position);
        viewHolder.icon.setImageDrawable(appInfo.getIcon());
        viewHolder.label.setText(appInfo.getLabel());
        viewHolder.cacheSize.setText("缓存:"+appInfo.getCacheSize()+"kb");
        viewHolder.dataSize.setText("数据:"+appInfo.getDataSize()+"kb");
        viewHolder.codeSize.setText("代码:"+appInfo.getCodeSize()+"kb");
       // viewHolder.pckName.setText(""+appInfo.getPckName());
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView label;
        TextView cacheSize;
        TextView dataSize;
        TextView codeSize;
       // TextView pckName;
        public ViewHolder(View view){
        //    pckName=(TextView)view.findViewById(R.id.pckName);
                icon=(ImageView)view.findViewById(R.id.icon);
                label=(TextView)view.findViewById(R.id.label);
                cacheSize=(TextView)view.findViewById(R.id.cacheSize);
                dataSize=(TextView)view.findViewById(R.id.dataSize);
                codeSize=(TextView)view.findViewById(R.id.codeSize);

        }

    }


}