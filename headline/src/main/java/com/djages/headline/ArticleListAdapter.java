package com.djages.headline;

import android.view.View;
import android.view.ViewGroup;

import com.djages.common.RESTfulAdapter;
import com.djages.headline.api.ApiUrls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ll298lee on 5/8/14.
 */
public class ArticleListAdapter extends RESTfulAdapter {
    private int mCode;

    public ArticleListAdapter(int code){
        mCode = code;
    }

    public void setCode(int code){
        mCode = code;
    }

    @Override
    protected String getUrl() {
        return ApiUrls.ARTICLE;
    }

    @Override
    protected Map<String, String> getParams() {
        mParams.put("c", Integer.toString(mCode));
        return mParams;
    }

    @Override
    protected List parseResponse(String response) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
