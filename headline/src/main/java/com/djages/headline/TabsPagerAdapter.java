package com.djages.headline;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;

import com.djages.common.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ll298lee on 5/8/14.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {
//    private ArrayList<ArticleListFragment> mFragmentList;
    private String[] mCategoryList;
    private int mPressCode;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
//        mFragmentList = new ArrayList<ArticleListFragment>();
    }


    @Override
    public Fragment getItem(int position) {
        String name = mCategoryList[position];
        int code = Integer.parseInt((""+mPressCode)+position);
        return ArticleListFragment.newInstance(name, code);
    }

    @Override
    public int getCount() {

        return mCategoryList == null ? 0:mCategoryList.length;

    }

    @Override
    public String getPageTitle(int position){

        return mCategoryList == null?"":mCategoryList[position];
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }


    public void setPress(int pressCode){
        DebugLog.v(this, "select pressCode: " + pressCode);
        ContentHelper.setPress(pressCode);
        mCategoryList = ContentHelper.getCategoryList(pressCode);
        mPressCode = pressCode;
        notifyDataSetChanged();
    }
}
