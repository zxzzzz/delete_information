package com.example.cache;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zx on 16-9-19.
 */
//将检测道德私密信息与private_item做适配
public class PrivateAdapter extends BaseAdapter {
    List<PrivateItem> privateItemList;
    Context context;

    public PrivateAdapter(Context context, List<PrivateItem> privateItemList) {
        this.context = context;
        this.privateItemList = privateItemList;
    }

    @Override
    public Object getItem(int position) {
        return privateItemList.get(position);
    }

    @Override
    public int getCount() {
        return privateItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.sql_private_item,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            PrivateItem item=privateItemList.get(position);
        if (item!=null) {
            Log.i(SearchPrivate.DETAILTAG, "name不为空" + item.getName());
            viewHolder.name.setText(item.getName());
            viewHolder.value.setText(item.getValue());
            viewHolder.attribute_name.setText(item.getAttributeName());
            viewHolder.attribute_value.setText(item.getAttributeValue());
        }
        return convertView;
    }
    class  ViewHolder{
        TextView name;
        TextView value;
        TextView attribute_name;
        TextView attribute_value;

        public ViewHolder(View view) {
            name=(TextView)view.findViewById(R.id.name);
            value=(TextView)view.findViewById(R.id.value);
            attribute_name=(TextView)view.findViewById(R.id.attribute_name);
            attribute_value=(TextView)view.findViewById(R.id.attribute_value);
        }
    }
}
