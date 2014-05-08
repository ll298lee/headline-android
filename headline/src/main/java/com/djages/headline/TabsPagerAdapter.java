package com.djages.headline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.djages.common.DebugLog;

import java.util.ArrayList;

/**
 * Created by ll298lee on 5/8/14.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<ArticleListFragment> mFragmentList;
    private String[] mCategoryList;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<ArticleListFragment>();
    }


    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);

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
        return PagerAdapter.POSITION_NONE;
    }


    public void setPress(int pressCode){
        DebugLog.v(this, "select pressCode: " + pressCode);
        mCategoryList = ContentHelper.getCategoryList(pressCode);
        if(mFragmentList.size()>mCategoryList.length){
            for(int i=mFragmentList.size()-1;i>=mCategoryList.length;i--) {
                mFragmentList.remove(i);

            }
        }

        //ensure size
        mFragmentList.ensureCapacity(mCategoryList.length);
        while (mFragmentList.size() < mCategoryList.length) {
            mFragmentList.add(null);
        }

        for(int j=0; j<mCategoryList.length; j++){
            String name = mCategoryList[j];
            int code = Integer.parseInt((""+pressCode)+j);
            ArticleListFragment fragment = mFragmentList.get(j);
            if(fragment==null){
                mFragmentList.set(j, ArticleListFragment.newInstance(name, code));
            }else{
                fragment.setCategory(name, code);
                fragment.clearContent();
            }
        }





        notifyDataSetChanged();
    }
}
