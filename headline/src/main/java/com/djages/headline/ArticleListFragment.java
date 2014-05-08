package com.djages.headline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;





/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArticleListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_NAME = "param_name";
    private static final String ARG_CODE = "param_code";

    private String mName;
    private int mCode;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;

    private ArticleListAdapter mAdapter;


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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_NAME, mName);
        outState.putInt(ARG_CODE, mCode);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mName = savedInstanceState.getString(ARG_NAME);
            mCode = savedInstanceState.getInt(ARG_CODE);
        }else if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mCode = getArguments().getInt(ARG_CODE);
        }

        mAdapter = new ArticleListAdapter(mCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_press, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }





    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(String id);
    }

}
