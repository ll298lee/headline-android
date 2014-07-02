package com.djages.headline;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.djages.common.DebugLog;
import com.djages.common.GsonHelper;
import com.djages.common.RESTfulAdapter;
import com.djages.common.VolleyHelper;
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


    private class ViewHolder {
        public TextView titleView;
        public TextView summaryView;
        public NetworkImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.article_title);
            viewHolder.titleView.setEllipsize(TextUtils.TruncateAt.END);
            viewHolder.titleView.setMaxLines(2);

            viewHolder.summaryView = (TextView) convertView.findViewById(R.id.article_summary);
            viewHolder.summaryView.setEllipsize(TextUtils.TruncateAt.END);
            viewHolder.summaryView.setMaxLines(5);

            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.article_image);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleModel article = mList.get(position);


        viewHolder.titleView.setText(article.getTitle());

        if(article.getImage() != null && !article.getImage().isEmpty()){
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageUrl(article.getImage(), VolleyHelper.getImageLoader());

        }else{
            viewHolder.imageView.setVisibility(View.GONE);
        }




//        String summaryStr = "<font color=#eb623d>"+article.getDateString()+"</font> <font color=#666666>|  "+article.getDescription()+"</font>";

        String date = article.getDateString();
        String summary = article.getDescription();
        final SpannableString text = new SpannableString(date + " |  "+ summary);

        text.setSpan(new RelativeSizeSpan(0.8f),
                0, date.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color6)),
                date.length(), date.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color1)),
                0, date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



        viewHolder.summaryView.setText(text);



        return convertView;
    }
}
