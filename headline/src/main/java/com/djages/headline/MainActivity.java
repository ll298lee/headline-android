package com.djages.headline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.djages.common.GaHelper;
import com.djages.common.SlidingTabLayout;
import com.google.android.gms.analytics.GoogleAnalytics;


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
        mTabsIndicator.setDividerColors(getResources().getColor(R.color.color3));
        mTabsIndicator.setSelectedIndicatorColors(getResources().getColor(R.color.color1));
        mTabsIndicator.setOnPageChangeListener(this);

        GaHelper.getTracker(GaHelper.TrackerName.APP_TRACKER);
        refreshAll();
    }

    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(CustomApplication.getInstance()).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        GoogleAnalytics.getInstance(CustomApplication.getInstance()).reportActivityStart(this);
        super.onStop();
    }



    private void refreshAll(){
        buildDrawer();
        int pressTabIndex = SpHelper.getInt(SpHelper.KEY_PRESS_TAB_INDEX, 0);
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


        //first time ux
        boolean drawerOpenedFirstTime = SpHelper.getBoolean(SpHelper.KEY_FIRST_DRAWER_OPEN, false);
        if(!drawerOpenedFirstTime){
            mDrawerLayout.openDrawer(mDrawerListWrap);
            SpHelper.putBoolean(SpHelper.KEY_FIRST_DRAWER_OPEN, true);
        }
    }

    private String selectTab(int index){

        DebugLog.v(this, "select tab: "+mDrawerTabAdapter.getItem(index).getName());
        SpHelper.putInt(SpHelper.KEY_PRESS_TAB_INDEX, index);
        int[] pressCodeList = ContentHelper.getPressCodeList();
        String[] pressNameList = ContentHelper.getPressNameList();
        getSupportActionBar().setTitle(getString(R.string.app_name)+" - "+pressNameList[index]);

        mTabsAdapter.setPress(pressCodeList[index]);
        mTabsIndicator.setViewPager(mTabViewPager);
        mTabViewPager.setCurrentItem(0);

        mDrawerTabAdapter.selectTab(index);
        return pressNameList[index];
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
        SharedPreferences sp = getSharedPreferences(IabActivity.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        boolean hasRemovedAds = sp.getBoolean(getString(R.string.remove_ads_sku), false);
        if(hasRemovedAds){
            menu.findItem(R.id.action_remove_ads).setVisible(false);
        }else{
            menu.findItem(R.id.action_remove_ads).setVisible(true);
        }

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
                GaHelper.sendEvent(GaHelper.UI_ACTION, "Click remove ads", "actionbar menu");
                Intent iabIntent = new Intent(this, IabActivity.class);
                iabIntent.putExtra("type", "purchase");
                iabIntent.putExtra("title", getString(R.string.action_remove_ads));
                iabIntent.putExtra("sku", getString(R.string.remove_ads_sku));
                iabIntent.putExtra("sku_request_code", 10001);
                startActivityForResult(iabIntent, IabActivity.PURCHASE_REQUEST_CODE);
                break;

//            case R.id.action_remove_ads_consume:
//                Intent iabConsumeIntent = new Intent(this, IabActivity.class);
//                iabConsumeIntent.putExtra("type", "consume");
//                iabConsumeIntent.putExtra("title", "Consume removed ads");
//                iabConsumeIntent.putExtra("sku", getString(R.string.remove_ads_sku));
//                iabConsumeIntent.putExtra("sku_request_code", 10001);
//                startActivityForResult(iabConsumeIntent, IabActivity.CONSUME_REQUEST_CODE);
//                break;

            case R.id.action_contact_us:
                GaHelper.sendEvent(GaHelper.UI_ACTION, "Click contact us", "actionbar menu");
                String to = getString(R.string.feedback_email_to);
                String subject = "["+getString(R.string.app_name)+"]"+getString(R.string.feedback_email_subject);
                String message = "";

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                // need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(email);

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
            String tabName = selectTab(position);
            String  label = ContentHelper.getCountry() + "-" +tabName;
            GaHelper.sendEvent(GaHelper.UI_ACTION, "Click left drawer item", label);
        }

        mDrawerLayout.closeDrawer(mDrawerListWrap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IabActivity.PURCHASE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                if(data.getExtras()!=null&&data.getExtras().getString(IabActivity.INTEND_SKU_KEY).equals(getString(R.string.remove_ads_sku))){
                    Intent intent = new Intent(this, MainActivity.class);
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }

            }
        }else if(requestCode == IabActivity.CONSUME_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                if(data.getExtras()!=null&&data.getExtras().getString(IabActivity.INTEND_SKU_KEY).equals(getString(R.string.remove_ads_sku))){
                    Intent intent = new Intent(this, MainActivity.class);
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            }
        }
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
