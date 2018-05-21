package com.example.nabeo.movementhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nabeo on 2017/11/08.
 */

public class MyAdapter extends BaseAdapter {
    ArrayList<Record> list;
    Context context;
    LayoutInflater layoutInflater = null;

    public MyAdapter(Context context){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<Record> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.record_row, parent, false);
        ((TextView)convertView.findViewById(R.id.departure)).setText(list.get(position).getDeparturePoin());
        //((TextView)convertView.findViewById(R.id.arrow)).setText("->");
        ((TextView)convertView.findViewById(R.id.arrive)).setText(list.get(position).getArrivalPoint());
        ((TextView)convertView.findViewById(R.id.firstTime)).setText("入室：" + list.get(position).getFirstEntryTime() + "\n" +
                "退室：" + list.get(position).getFirstLeaveTime());
        ((TextView)convertView.findViewById(R.id.secondTime)).setText("入室：" + list.get(position).getSecondEntryTime() + "\n" +
                "退室：" + list.get(position).getSecondLeaveTime());
        return convertView;
    }
}
