package com.djages.headline;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

/**
 * Created by ll298lee on 5/9/14.
 */
abstract public class ScrollEventFragment extends Fragment implements AbsListView.OnScrollListener{
    protected boolean mIsScrollAtBottom;
    protected boolean mIsScrollAtTop;
    protected boolean mIsScrolling;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            mIsScrolling = false;
            if(mIsScrollAtTop){
                onScrollTop();
            }else if(mIsScrollAtBottom){
                onScrollBottom();
            }
        }else{
            mIsScrolling = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem == totalItemCount) {
            mIsScrollAtBottom = true;

        }else{
            mIsScrollAtBottom = false;
        }

        if(firstVisibleItem == 0){
            mIsScrollAtTop = true;
        }else{
            mIsScrollAtTop = false;
        }
    }

    abstract protected void onScrollTop();
    abstract protected void onScrollBottom();
}
