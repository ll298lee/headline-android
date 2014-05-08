package com.djages.headline;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ll298lee on 5/2/14.
 */
public class DrawerTabAdapter extends BaseAdapter {


    public static class ItemModel{
        private String name;
        private int type;
        public ItemModel(String name, int type){
            this.name = name;
            this.type = type;
        }
        public String getName(){return name;}
        public int getType(){return type;}

    }

    private List<ItemModel> mList;
    private Context mContext;

    public DrawerTabAdapter(Context context){
        mContext = context;
        mList = new ArrayList<ItemModel>();
        Resources res = mContext.getResources();
        String[] pressList=ContentHelper.getPressNameList();
        for(int i=0;i<pressList.length;i++){
            mList.add(new ItemModel(pressList[i],i));
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_tab, parent, false);

        TextView itemName = (TextView) convertView.findViewById(R.id.item_title);
        int type = mList.get(position).getType();
        String name = mList.get(position).getName();
        itemName.setText(name);
        return convertView;
    }


    @Override
    public int getCount() {
        return (mList == null) ? 0 : mList.size();
    }

    @Override
    public ItemModel getItem(int position) {
        if (position < 0  || mList == null || position >= mList.size())
            return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }


}
