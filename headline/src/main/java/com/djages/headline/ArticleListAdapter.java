package com.djages.headline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djages.common.DebugLog;
import com.djages.common.GsonHelper;
import com.djages.common.RESTfulAdapter;
import com.djages.headline.api.ApiUrls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ll298lee on 5/8/14.
 */
public class ArticleListAdapter extends RESTfulAdapter<ArticleModel> {
    private int mCode;
    private Context mContext;

    public ArticleListAdapter(int code){
        mCode = code;
        mContext = CustomApplication.getAppContext();
    }

    public void setCode(int code){
        mCode = code;
    }

    @Override
    protected String getUrl() {
        return ApiUrls.ARTICLE;
    }

    @Override
    public Map<String, String> getParams() {
        mParams.put("c", Integer.toString(mCode));

        //TODO this is for test
//        mParams.put("o", Integer.toString(3));
        return mParams;
    }

    public int getCode(){
        return mCode;
    }

    @Override
    protected Map<String, String> getLoadMoreParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", Integer.toString(mCode));
        params.put("o", Integer.toString(mList.size()));
        return params;
    }

    @Override
    public List<ArticleModel> parseResponse(String response) {
        try {
            Gson gson = GsonHelper.getGson();
            Type listType = new TypeToken<List<ArticleModel>>() {
            }.getType();
            List<ArticleModel> list = (List<ArticleModel>) gson.fromJson(GsonHelper.getJsonParser().parse(response), listType);
            return list;
        }catch(Exception e){
            DebugLog.e(this, "parse Article JSON error");
            return null;
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false);
        ((TextView)convertView.findViewById(R.id.title)).setText(mList.get(position).getTitle());
        return convertView;
    }
}
