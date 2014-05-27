package com.djages.headline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.djages.common.DebugLog;

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
    public Fragment getItem(int index) {
        int position = ContentHelper.mapCategoryPosition(index, ContentHelper.getPress());

        String name = mCategoryList[position];
        int code = Integer.parseInt((""+mPressCode)+String.format("%02d", position));
        return ArticleListFragment.newInstance(name, code);
    }

    @Override
    public int getCount() {

        return mCategoryList == null ? 0:mCategoryList.length;

    }

    @Override
    public String getPageTitle(int index){
        int position = ContentHelper.mapCategoryPosition(index, ContentHelper.getPress());

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
