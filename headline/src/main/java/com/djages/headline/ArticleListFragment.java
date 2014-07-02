package com.djages.headline;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.djages.common.CustomHeaderTransformer;
import com.djages.common.DebugLog;
import com.djages.common.RESTfulAdapter;
import com.djages.common.VolleyHelper;

import java.util.Calendar;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class ArticleListFragment extends ScrollEventFragment implements
        AbsListView.OnItemClickListener,
        OnRefreshListener,
        View.OnClickListener,
        RESTfulAdapter.RESTfulAdapterEventListener{

    private static final String ARG_NAME = "param_name";
    private static final String ARG_CODE = "param_code";
    private static final String ARG_MODELS = "param_models";
    private static final String ARG_MODELS_BUNDLE = "param_models_bundle";
    private static final String ARG_CHECKED_TIME = "param_checked_time";

    private String mName;
    private int mCode;
    private List<ArticleModel> mCachedNewArticles = null;
    private Calendar mCheckNewTime = null;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;

    private ArticleListAdapter mAdapter;

    //layout view variables
    private ProgressBar mLoadingProgressBar;
    private ProgressBar mLoadMoreProgressBar;
    private PullToRefreshLayout mPullToRefreshLayout;
    private Button mNewArticleButton;



    public static ArticleListFragment newInstance(String name, int code) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putInt(ARG_CODE, code);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleListFragment() {
    }


    public void setCategory(String name, int code){
        mName = name;
        mCode = code;
        if(mAdapter != null){
            mAdapter.setCode(code);
        }
    }

    public String getCategoryName(){
        return mName;
    }

    public int getCategoryCode(){
        return mCode;
    }

    public void clearContent(){
        if(mAdapter != null && !mAdapter.isEmpty()) {
            mAdapter.clear();
        }
//        this.state

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_NAME, mName);
        outState.putInt(ARG_CODE, mCode);
        if(mAdapter != null && !mAdapter.isEmpty() && (mCachedNewArticles == null || mCachedNewArticles.size()==0)){
            ArticleModel[] modelArray = new ArticleModel[mAdapter.getCount()];
            modelArray = mAdapter.getModelList().toArray(modelArray);


            Bundle modelsBundle = new Bundle();
            modelsBundle.putParcelableArray(ARG_MODELS, modelArray);

//            outState.putParcelableArray(ARG_MODELS, modelArray);
            outState.putBundle(ARG_MODELS_BUNDLE, modelsBundle);
        }

        if(mCheckNewTime != null){
            outState.putSerializable(ARG_CHECKED_TIME, mCheckNewTime);
        }





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mCode = getArguments().getInt(ARG_CODE);
            mAdapter = new ArticleListAdapter(mCode);

            if(savedInstanceState !=null
                    && savedInstanceState.getInt(ARG_CODE)==mCode
                    && savedInstanceState.containsKey(ARG_CHECKED_TIME)){
                mCheckNewTime = (Calendar)savedInstanceState.getSerializable(ARG_CHECKED_TIME);
//                DebugLog.v(this, "retrive checked time in onCreate: "+mCheckNewTime.toString());
            }

            if(savedInstanceState !=null
                    && savedInstanceState.getInt(ARG_CODE)==mCode
                    && savedInstanceState.containsKey(ARG_MODELS_BUNDLE)){
                Bundle modelsBundle = savedInstanceState.getBundle(ARG_MODELS_BUNDLE);
                modelsBundle.setClassLoader(getClass().getClassLoader());
//                savedInstanceState.setClassLoader(this.getClass().getClassLoader());
                Parcelable[] modelArray = modelsBundle.getParcelableArray(ARG_MODELS);
                for(int i=0;i<modelArray.length;i++){
                    mAdapter.add((ArticleModel)modelArray[i]);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_press, container, false);

        mNewArticleButton = (Button) view.findViewById(R.id.new_article_button);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progressbar);
        mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.load_more_progressbar);
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            CustomHeaderTransformer transformer = new CustomHeaderTransformer();

            ActionBarPullToRefresh.from(getActivity())
                    .allChildrenArePullable()
                    .listener(this)
                    .options(Options.create()
                            .headerTransformer(transformer)
                            .build())
                    .setup(mPullToRefreshLayout);
            transformer.setProgressBarColor(getResources().getColor(R.color.color1));
        }else{
            ActionBarPullToRefresh.from(getActivity())
                    .allChildrenArePullable()
                    .listener(this)
                    .setup(mPullToRefreshLayout);
        }







        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mAdapter.setListener(this);
        mNewArticleButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mAdapter !=null && mAdapter.isEmpty()){
            mAdapter.fetch();
        }else{
            checkNewArticles();
        }
    }

    public void checkNewArticles(){

        Calendar now = Calendar.getInstance();

        boolean shouldCheck;
        if(mCheckNewTime != null){
            shouldCheck = now.getTimeInMillis() - mCheckNewTime.getTimeInMillis() > 1000 * 60 * 5;
            if(shouldCheck){
                //DebugLog.v(this, "last check time is more than thruttle time, so do check");
            }else{
                //DebugLog.v(this, "last check time is less than thruttle time, no check");
            }
        }else{
            //DebugLog.v(this, "last check time is null, so do check");
            shouldCheck = true;
        }

        if(!shouldCheck) return;
        String queryStr="?c="+mAdapter.getCode();
        StringRequest sr = new StringRequest(Request.Method.GET, mAdapter.getUrl()+queryStr, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(mAdapter == null || getActivity() == null ) return;
                mCheckNewTime = Calendar.getInstance();
                List<ArticleModel> newList =  mAdapter.parseResponse(response);
                if(newList == null) return;
                for(int i = newList.size()-1;i>=0;i--){
                    if(mAdapter.containsId(newList.get(i).getId())){
                        newList.remove(i);
                    }
                }
                showNewArticles(newList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.e("ArticleListFragment", "error on GET new articles in onStart");
            }
        });
        VolleyHelper.getRequestQueue().add(sr);

    }

    private void showNewArticles(List<ArticleModel> list){
        if(list.size() > 0){
            mCachedNewArticles = list;
            if(list.size() > 10) {
                mNewArticleButton.setText(getString(R.string.new_article_lots));
            }else{
                mNewArticleButton.setText(String.format(getString(R.string.new_article_count), Integer.toString(list.size())));
            }
            mNewArticleButton.setVisibility(View.VISIBLE);
        }else{
            mCachedNewArticles = null;
            mNewArticleButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.new_article_button){
            mAdapter.clear();
            mAdapter.fetch();
//            if(mCachedNewArticles.size()>19){
//                mAdapter.clear();
//            }
//            mAdapter.add(mCachedNewArticles);
            mCachedNewArticles = null;
            mNewArticleButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArticleModel article =  mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ArticleWebviewActivity.class);
        intent.putExtra("article_object", article);
        startActivity(intent);
    }

    @Override
    protected void onScrollTop() {

    }

    @Override
    protected void onScrollBottom() {
        DebugLog.v(this,"scroll btn");
        if(mAdapter !=null && !mAdapter.isEmpty()) {
            mAdapter.loadMore();
        }

    }

    @Override
    public void onRESTAdapterEvent(RESTfulAdapter.RESTAfulAdapterEvent event, Object obj) {
        switch(event){
            case FETCH_PREPARE:
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                break;
            case FETCH_FINISHED:
                mLoadingProgressBar.setVisibility(View.GONE);
                mPullToRefreshLayout.setRefreshComplete();
                break;
            case FETCH_SUCCESS:
                mCheckNewTime = Calendar.getInstance();
                break;
            case LOADMORE_PREPARE:
                mLoadMoreProgressBar.setVisibility(View.VISIBLE);
                break;
            case LOADMORE_FINISHED:
                mLoadMoreProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        mCachedNewArticles = null;
        mNewArticleButton.setVisibility(View.GONE);
        mAdapter.clear();
        mAdapter.fetch();
    }




    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(String id);
    }

}
