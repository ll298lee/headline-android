package com.djages.headline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djages.common.DebugLog;
import com.djages.common.RESTfulAdapter;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class ArticleListFragment extends ScrollEventFragment implements
        AbsListView.OnItemClickListener,
        OnRefreshListener,
        RESTfulAdapter.RESTfulAdapterEventListener{

    private static final String ARG_NAME = "param_name";
    private static final String ARG_CODE = "param_code";
    private static final String ARG_MODELS = "param_models";
    private static final String ARG_MODELS_BUNDLE = "param_models_bundle";

    private String mName;
    private int mCode;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;

    private ArticleListAdapter mAdapter;

    //layout view variables
    private ProgressBar mLoadingProgressBar;
    private ProgressBar mLoadMoreProgressBar;
    private PullToRefreshLayout mPullToRefreshLayout;


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
        if(mAdapter != null && !mAdapter.isEmpty()){
            ArticleModel[] modelArray = new ArticleModel[mAdapter.getCount()];
            modelArray = mAdapter.getModelList().toArray(modelArray);


            Bundle modelsBundle = new Bundle();
            modelsBundle.putParcelableArray(ARG_MODELS, modelArray);

//            outState.putParcelableArray(ARG_MODELS, modelArray);
            outState.putBundle(ARG_MODELS_BUNDLE, modelsBundle);
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


        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progressbar);
        mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.load_more_progressbar);
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);

        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mAdapter.setListener(this);

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
        mAdapter.clear();
        mAdapter.fetch();
    }


    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(String id);
    }

}
