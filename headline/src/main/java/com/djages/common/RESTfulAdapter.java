package com.djages.common;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class RESTfulAdapter<T extends AbstractModel> extends
        BaseAdapter {
    public interface RESTfulAdapterEventListener {
        public void onRESTAdapterEvent(RESTAfulAdapterEvent event, Object obj);
    }
    public enum RESTAfulAdapterEvent {
        FETCH_PREPARE,
        FETCH_FINISHED,
        FETCH_SUCCESS,
        FETCH_FAILED,
        NETWORK_ERROR,
        ITEM_ADDED,
        ITEM_REMOVED,
        LOADMORE_PREPARE,
        LOADMORE_FINISHED,
        LOADMORE_SUCCESS,
        LOADMORE_FAILED
    }


    protected List<T> mList;
    protected RESTfulAdapterEventListener mListener;
    protected Map<String, String> mParams;
    private HashMap<String, Integer> mIdIndexMap;
    private FetchTask mFetchTask;


    public RESTfulAdapter() {
        super();
        mList = new ArrayList<T>();
        mIdIndexMap = new HashMap<String, Integer>();
        mParams = new HashMap<String, String>();
        mListener = new RESTfulAdapterEventListener(){
            @Override
            public void onRESTAdapterEvent(RESTAfulAdapterEvent event, Object obj) {
            }
        };

    }

    protected abstract String getUrl();
    protected abstract Map<String, String> getParams();
    protected abstract List<T> parseResponse(String response);
    protected boolean isOrdered(){
        return false;
    };

    protected Map<String, String> getLoadMoreParams(){
        return getParams();
    }

    protected int getHttpMethod(){
        return Request.Method.GET;
    }

    public void setListener(RESTfulAdapterEventListener listener){
        mListener = listener;
    }

    public void fetch() {
        if(mFetchTask == null){
            mFetchTask = new FetchTask();
            mFetchTask.execute();
        }
    }


    public void fetch(int method, Map<String, String> params, boolean isUpdate){
        if(mFetchTask == null) {
            mFetchTask = new FetchTask(method, params, isUpdate);
            mFetchTask.execute();
        }
    }


    public void loadMore(){
        if(mFetchTask == null){
            mFetchTask = new FetchTask(true);
            mFetchTask.execute();
        }
    }




    protected class FetchTask extends AsyncTask<Void, Void, List<T>> {
        private boolean mIsLoadMore = false;
        private boolean mIsUpdate = false;
        private int mMethod = Request.Method.GET;
        private Map<String, String> mParams = null;
        private Map<String, String> mLoadMoreParams = null;

        public FetchTask(){
            mIsLoadMore = false;
            mIsUpdate = false;
            mMethod = getHttpMethod();
            mParams = getParams();
            mLoadMoreParams = getLoadMoreParams();
        }

        public FetchTask(boolean isLoadMore){
            mIsLoadMore = isLoadMore;
            mIsUpdate = false;
            mMethod = getHttpMethod();
            mParams = getParams();
            mLoadMoreParams = getLoadMoreParams();
        }

        public FetchTask(int method, Map<String, String> params, boolean isUpdate){
            mIsLoadMore = false;
            mIsUpdate = isUpdate;
            mMethod = method;
            mParams = params;
            mLoadMoreParams = getLoadMoreParams();
        }

        @Override
        protected void onPreExecute() {
            if(!mIsLoadMore) {
                mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.FETCH_PREPARE, null);
            }else {
                mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.LOADMORE_PREPARE, null);
            }
        }

        @Override
        protected List<T> doInBackground(Void... params) {
            String res = null;
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request;
            final Map<String, String> paramMap = mIsLoadMore ? mLoadMoreParams : mParams;
            if(mMethod == Request.Method.GET){
                String queryStr="?";
                if(paramMap != null) {
                    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                        queryStr += entry.getKey() + "=" + entry.getValue() + "&";
                    }
                }else{
                    queryStr = "";
                }
                Log.v("debug", "GET: "+getUrl()+queryStr);
                request = new StringRequest(Request.Method.GET, getUrl()+queryStr, future, future);
            }else{
                request = new StringRequest(Request.Method.POST, getUrl(), future, future){
                    protected Map<String, String> getParams() {
                        return paramMap;
                    };
                };
            }

            VolleyHelper.getRequestQueue().add(request);

            try {
                res = future.get();
            } catch (InterruptedException e) {
                //TODO handle exception
            } catch (ExecutionException e) {
                //TODO handle exception
            }

            if(res == null){
                return null;
            }

            try{
                List<T> models = parseResponse(res);
                return models;
            }catch(Exception e){
                //TODO handle exception
                return null;
            }
        }

        protected void onPostExecute(List<T> modelsToAdd) {
            if(!mIsLoadMore) {
                mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.FETCH_FINISHED, null);
            }else {
                mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.LOADMORE_FINISHED, null);
            }


            if(modelsToAdd != null){
                if(!mIsLoadMore && !mIsUpdate){
                    clearHelper();
                }
                addHelper(modelsToAdd);
                notifyDataSetChanged();

                if(!mIsLoadMore){
                    mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.FETCH_SUCCESS, modelsToAdd);
                }else{
                    mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.LOADMORE_SUCCESS, null);
                }
            }else{
                if(!mIsLoadMore){
                    mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.FETCH_FAILED, null);
                }else{
                    mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.LOADMORE_FAILED, null);
                }
            }
            mFetchTask = null;
        }

        protected void onCancelled(){
            notifyDataSetChanged();
            mFetchTask = null;
        }
    }


    public void cancelAllTasks(){
        if(mFetchTask != null){
            mFetchTask.cancel(true);
        }
    }



    public void clear() {
        clearHelper();
        notifyDataSetChanged();
    }


    public void add(List<T> models){
        addHelper(models);
        notifyDataSetChanged();
        mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.ITEM_ADDED, models);
    }

    public void add(T model){
        addHelper(model, true);
        notifyDataSetChanged();
        mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.ITEM_ADDED, model);
    }


    public void remove(String id){
        removeHelper(id);
        notifyDataSetChanged();
        mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.ITEM_REMOVED, id);
    }

    public void remove(T model){
        removeHelper(model.getId());
        notifyDataSetChanged();
        mListener.onRESTAdapterEvent(RESTAfulAdapterEvent.ITEM_REMOVED, model);
    }

    public void sort(){
        sortHelper();
        notifyDataSetChanged();
    }



    private void clearHelper(){
        mList.clear();
        mIdIndexMap.clear();
    }

    private void addHelper(List<T> models){
        for(T model: models){
            addHelper(model, false);
        }
        if(isOrdered()){
            sortHelper();
        }
    }

    private void addHelper(T model, boolean sort) {
        String id = model.getId();
        boolean doSort = isOrdered() && sort;
        if(mIdIndexMap.containsKey(id)){
            mList.set(mIdIndexMap.get(id), model);
        }else{
            mList.add(model);
            if(!doSort){
                mIdIndexMap.put(model.getId(), mList.size()-1);
            }
        }
        if(doSort){
            sortHelper();
        }
    }


    private void removeHelper(String id){
        Integer index = mIdIndexMap.get(id);
        if(index == null) return;
        mList.remove(index);
        if(isOrdered()){
            resetIndexMap();
        }else{
            mIdIndexMap.remove(id);
        }
    }



    private void sortHelper(){
        Collections.sort(mList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
        resetIndexMap();
    }

    private void resetIndexMap(){
        mIdIndexMap.clear();
        for(int i=0;i<mList.size();i++){
            mIdIndexMap.put(mList.get(i).getId(), i);
        }
    }


    @Override
    public int getCount() {
        return (mList == null) ? 0 : mList.size();
    }

    public T getItemById(String id){
        if(!mIdIndexMap.containsKey(id)){
            return null;
        }
        return mList.get(mIdIndexMap.get(id));
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position >= mList.size())
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
