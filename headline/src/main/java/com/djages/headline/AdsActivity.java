package com.djages.headline;


import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

abstract public class AdsActivity extends ActionBarActivity {
    private RelativeLayout adBannerLayout;
    private AdView adMobAdView;
    private InterstitialAd interstitial;
    private AdRequest mAdreq;

    private boolean hasBoughtRemoveAds(){
        SharedPreferences sp = getSharedPreferences(IabActivity.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        return sp.getBoolean(getString(R.string.remove_ads_sku), false);
    }



    protected void setupAds(){

        adBannerLayout = (RelativeLayout) findViewById(R.id.adLayout);
        adBannerLayout.setGravity(Gravity.CENTER);
        adBannerLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mAdreq = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addKeyword("")
                .setGender(AdRequest.GENDER_UNKNOWN)
                .addKeyword("NETWORK")
                .addKeyword("food")
                .addKeyword("restaurant")
                .addKeyword("promotion")
                .addKeyword("餐廳")
                .addKeyword("優惠")
                .addKeyword("特價")
                .addKeyword("美食")
                .addKeyword("食品")
                .build();



        //TODO: 請填入admob的mediation ID
        adMobAdView = new AdView(this);
        adMobAdView.setAdUnitId("b0bb7ff1d03446ba");
        adMobAdView.setAdSize(AdSize.BANNER);

        adMobAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {}
            public void onAdFailedToLoad(int errorCode) {}
            public void onAdOpened() {}
            public void onAdClosed() {}
            public void onAdLeftApplication() {}
        });

        adBannerLayout.removeAllViews();
        adBannerLayout.addView(adMobAdView);

    }

    private void refreshAd(){
        if(adMobAdView != null && mAdreq != null)
            adMobAdView.loadAd(mAdreq);
    }

    @Override
    public void onStart(){
        super.onStart();
        ｀
        if(hasBoughtRemoveAds()) return;

        if(adMobAdView == null || mAdreq == null){
            setupAds();
            adMobAdView.loadAd(mAdreq);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resume the AdView.
        if(adMobAdView != null)
            adMobAdView.resume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        if(adMobAdView != null)
            adMobAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if(adMobAdView != null)
            adMobAdView.destroy();

        super.onDestroy();
    }


}
