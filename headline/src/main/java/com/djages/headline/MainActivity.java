package com.djages.headline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.djages.common.DebugLog;
import com.djages.common.SlidingTabLayout;


public class MainActivity extends AdsActivity implements
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

    private boolean  mDoubleBackToExitPressedOnce = false;

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
        int pressTabIndex = SpHelper.getInt(SpHelper.KEY_PRESS_TAB_INDEX,0);
        selectTab(pressTabIndex);
    }

    private void buildDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListWrap = (LinearLayout) findViewById(R.id.drawer_wrap);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerTabAdapter = new DrawerTabAdapter(getApplicationContext());
        mDrawerList.setAdapter(mDrawerTabAdapter);
        mDrawerList.setOnItemClickListener(this);
        mDrawerLayout.setFocusableInTouchMode(false);

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
        SpHelper.putInt(SpHelper.KEY_PRESS_TAB_INDEX, index);
        int[] pressCodeList = ContentHelper.getPressCodeList();
        String[] pressNameList = ContentHelper.getPressNameList();
        getSupportActionBar().setTitle(getString(R.string.app_name)+" - "+pressNameList[index]);

        mTabsAdapter.setPress(pressCodeList[index]);
        mTabsIndicator.setViewPager(mTabViewPager);
        mTabViewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        mDoubleBackToExitPressedOnce = true;
        mDrawerLayout.openDrawer(mDrawerListWrap);
        Toast.makeText(this, getString(R.string.toast_back_button_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
            case R.id.action_change_country:
                String[] countries = getResources().getStringArray(R.array.country_selection_list);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.country_select_title));
                builder.setItems(countries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        SpHelper.putInt(SpHelper.KEY_PRESS_TAB_INDEX, 0);
                        ContentHelper.setCountry(index);
                        refreshAll();
                    }
                });
                builder.show();
                break;
            case R.id.action_remove_ads:
                Intent iabIntent = new Intent(this, IabActivity.class);
                iabIntent.putExtra("type", "purchase");
                iabIntent.putExtra("title", getString(R.string.action_remove_ads));
                iabIntent.putExtra("sku", "com.djages.headline.removeads");
                iabIntent.putExtra("sku_request_code", 10001);
                startActivityForResult(iabIntent, IabActivity.PURCHASE_REQUEST_CODE);
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
