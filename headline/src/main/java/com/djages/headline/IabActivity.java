package com.djages.headline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.djages.IAButils.IabHelper;
import com.djages.IAButils.IabResult;
import com.djages.IAButils.Inventory;
import com.djages.IAButils.Purchase;
import com.djages.common.DebugLog;
import com.djages.common.Utils;


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


    private final String SHARED_PREFERENCE_KEY="iab_shared_preference";
    public static final String ACTION_PURCHASE = "iab_action_purchase";
    public static final String INTEND_SKU_KEY = "iab_intend_sku_key";
    public static final int PURCHASE_REQUEST_CODE = 1201;


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
        }
        return;
    }

    private void purchaseItem(String sku, int requestCode){
        if(!mIsIabConnected) return;
        mIabHelper.launchPurchaseFlow(this, sku, requestCode,
                this, ""); //todo identify user with last argument
    }

    private void syncItems(){
        //TODO check has bought any items
    }

    private void consumeItem(String sku){
        //TODO consume item
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
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        //TODO Save to preference
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()) {
            DebugLog.d(this, "Error purchasing: " + result);
            Utils.showToast(this, getString(R.string.iab_purchase_failed));
            Intent returnIntent = new Intent(ACTION_PURCHASE);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;

        }else if(info != null && info.getSku() != null){
            SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(info.getSku(), true);
            editor.commit();
            Utils.showToast(this, getString(R.string.iab_purchase_success));
            Intent returnIntent = new Intent(ACTION_PURCHASE);
            returnIntent.putExtra(INTEND_SKU_KEY, info.getSku());
            setResult(RESULT_OK, returnIntent);
            finish();
        }
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
