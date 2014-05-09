package com.djages.headline;

import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.djages.common.DebugLog;
import com.djages.common.SlidingTabLayout;


public class MainActivity extends ActionBarActivity implements
        AdapterView.OnItemClickListener,
        ViewPager.OnPageChangeListener,
        ArticleListFragment.OnFragmentInteractionListener{
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerListWrap;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerTabAdapter mDrawerTabAdapter;
    private ListView mDrawerList;

    private ViewPager mTabViewPager;
    private TabsPagerAdapter mTabsAdapter;
    private SlidingTabLayout mTabsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        mTabViewPager = (ViewPager)findViewById(R.id.tab_pager);
        mTabsIndicator = (SlidingTabLayout)findViewById(R.id.tabs);
        mTabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mTabViewPager.setAdapter(mTabsAdapter);
        mTabsIndicator.setCustomTabView(R.layout.sliding_tab_layout_tab_view, R.id.tab_text);
        mTabsIndicator.setViewPager(mTabViewPager);
        mTabsIndicator.setDividerColors(getResources().getColor(R.color.color19));
        mTabsIndicator.setSelectedIndicatorColors(getResources().getColor(R.color.color12));
        mTabsIndicator.setOnPageChangeListener(this);


        refreshAll();
    }

    private void refreshAll(){
        buildDrawer();
        selectTab(0);
    }

    private void buildDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListWrap = (LinearLayout) findViewById(R.id.drawer_wrap);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerTabAdapter = new DrawerTabAdapter(getApplicationContext());
        mDrawerList.setAdapter(mDrawerTabAdapter);
        mDrawerList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectTab(int index){
        DebugLog.v(this, "select tab: "+mDrawerTabAdapter.getItem(index).getName());
        int[] pressCodeList = ContentHelper.getPressCodeList();
        String[] pressNameList = ContentHelper.getPressNameList();
        getSupportActionBar().setTitle(getString(R.string.app_name)+" - "+pressNameList[index]);

        mTabsAdapter.setPress(pressCodeList[index]);
        mTabsIndicator.setViewPager(mTabViewPager);
        mTabViewPager.setCurrentItem(0);


    }




    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        if(isDrawerOpen()){
//            getSupportActionBar().setTitle("");
//            getSupportActionBar().setLogo(R.drawable.logo);
//
//            return true;
//        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()){
            case R.id.action_settings:
                String country = ContentHelper.getCountry();
                if(country.equals("tw_presses")){
                    ContentHelper.setCountry(1);
                    refreshAll();
                }else{
                    ContentHelper.setCountry(0);
                    refreshAll();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.drawer_list){
            selectTab(position);
        }

        mDrawerLayout.closeDrawer(mDrawerListWrap);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}
