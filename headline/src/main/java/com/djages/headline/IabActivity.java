package com.djages.headline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.djages.IAButils.IabHelper;
import com.djages.IAButils.IabResult;
import com.djages.IAButils.Inventory;
import com.djages.IAButils.Purchase;
import com.djages.common.DebugLog;
import com.djages.common.GaHelper;
import com.djages.common.Utils;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;


public class IabActivity extends ActionBarActivity implements
        IabHelper.OnIabPurchaseFinishedListener,
        IabHelper.QueryInventoryFinishedListener,
        IabHelper.OnConsumeFinishedListener,
        IabHelper.OnIabSetupFinishedListener{

    private IabHelper mIabHelper;
    private boolean mIsIabConnected = false;
    private String mType;
    private String mTitle;
    private String mSku;
    private int mSkuRequestCode;
    private List<String> mSkuToConsume;


    public static final String SHARED_PREFERENCE_KEY="iab_shared_preference";
    public static final String ACTION_PURCHASE = "iab_action_purchase";
    public static final String ACTION_CONSUME = "iab_action_consume";
    public static final String INTEND_SKU_KEY = "iab_intend_sku_key";
    public static final int PURCHASE_REQUEST_CODE = 1201;
    public static final int CONSUME_REQUEST_CODE = 1202;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iab);
        if(getIntent().getExtras() != null){
            mType = getIntent().getExtras().getString("type");
            mTitle = getIntent().getExtras().getString("title");
            if(getIntent().getExtras().containsKey("sku")){
                mSku =  getIntent().getExtras().getString("sku");
                mSkuRequestCode = getIntent().getExtras().getInt("sku_request_code");
            }
        }
        GaHelper.getTracker(GaHelper.TrackerName.APP_TRACKER);
        setUpIab();
    }

    @Override
    public void onDestroy(){
        if (mIabHelper != null){
            mIabHelper.dispose();
        }
        mIabHelper = null;
        super.onDestroy();
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

    private void setUpIab(){
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgGjnuFjec2Lwj7ZG8xTOlgwAlDYO4NGnEyRtZRa25C3/7Aa444Y4AHDr87xWkoafaUC6BpTOxAZQJd8z7zDmDHwbAJQEbJ4EpgQddKmgs/eO9CHUhMFjWVQKBvkie1jxVRvkvSTBKgmkLrHUh7mFtK2lJXXT9yfhBSVdIXjCSm/Qg3SkGyR11FE49jaMSXXfSyFY6oZgooTksNaLpXlNsbVr34fVXRROc41jWSEYMQ1bUUeGIC0by2inPN0kR9UBTUEKBS8DiSqpkmxraQErdhuGSEZE0dOyx8b0DiZYyaZze+iO2Kd25MpRalBK6XXZ5XHOXpdYZUv+BSKX1Y0kSQIDAQAB";
        mIabHelper = new IabHelper(this, key);
        mIabHelper.startSetup(this);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
            // Oh noes, there was a problem.
            DebugLog.e(this, "Problem setting up In-app Billing: " + result);
            mIsIabConnected = false;
            return;
        }
        // Hooray, IAB is fully set up!
        mIsIabConnected = true;
        //check is vip
        onIabSetup();
    }

    protected void onIabSetup(){
        if(mType.equals("purchase")) {
            purchaseItem(mSku, mSkuRequestCode);
        }else if(mType.equals("consume")){
            consumeItem(mSku);
        }
        return;
    }

    private void purchaseItem(String sku, int requestCode){
        if(!mIsIabConnected) return;
        mIabHelper.launchPurchaseFlow(this, sku, requestCode,
                this, ""); //todo identify user with last argument

//        mIabHelper.queryInventoryAsync(this);

    }

    private void consumeItem(String sku){
        if(!mIsIabConnected) return;
        if(mSkuToConsume == null){
            mSkuToConsume = new ArrayList<String>();
        }
        mSkuToConsume.add(sku);
        mIabHelper.queryInventoryAsync(true, mSkuToConsume, this);
    }

    private void syncItems(){
        //TODO check has bought any items
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.d(this, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (mIabHelper!=null&&!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }else {
            DebugLog.d(this, "onActivityResult handled by IABUtil.");
        }
    }


    @Override
    public void onConsumeFinished(Purchase info, IabResult result) {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(info.getSku(), false);
        editor.commit();
        Intent returnIntent = new Intent(ACTION_CONSUME);
        returnIntent.putExtra(INTEND_SKU_KEY, info.getSku());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()) {
            DebugLog.e(this, "Error purchasing: " + result);
            if(result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
                Utils.showToast(this, getString(R.string.iab_purchase_already_owned));
                DebugLog.v(this, "already bought:" + mSku);
                onPurchaseSuccessful(mSku);
                return;
            }
            Utils.showToast(this, getString(R.string.iab_purchase_failed));
            Intent returnIntent = new Intent(ACTION_PURCHASE);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;

        }else if(info != null && info.getSku() != null){
            onPurchaseSuccessful(info.getSku());
        }
    }

    private void onPurchaseSuccessful(String sku){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(sku, true);
        editor.commit();
        //Utils.showToast(this, getString(R.string.iab_purchase_success));
        Intent returnIntent = new Intent(ACTION_PURCHASE);
        returnIntent.putExtra(INTEND_SKU_KEY, sku);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void onCancelPurchase(){
        Intent returnIntent = new Intent(ACTION_PURCHASE);
        setResult(RESULT_CANCELED, returnIntent);
    }

    @Override
    public void onBackPressed(){
        onCancelPurchase();
        super.onBackPressed();
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            // handle error
//            Toast.makeText(this, "ERROR QUERY "+SKU_VIP, Toast.LENGTH_SHORT).show();
            return;
        }
        if(mSkuToConsume != null){
            for(int i=mSkuToConsume.size()-1;i>=0;i--){
                DebugLog.v(this, "to consume: "+mSkuToConsume.get(i));
                DebugLog.v(this, Boolean.toString(inv.getPurchase(mSkuToConsume.get(i))==null));

                mIabHelper.consumeAsync(inv.getPurchase(mSkuToConsume.get(i)), this);
                mSkuToConsume.remove(i);
            }
            mSkuToConsume = null;
        }else{
            DebugLog.v(this, "query inv finished");

            DebugLog.v(this, Boolean.toString(inv.hasPurchase("com.djages.headline.removeads")));

        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onCancelPurchase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
