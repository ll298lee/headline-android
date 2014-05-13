package com.djages.headline;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.djages.common.Utils;


public class ArticleWebviewActivity extends AdsActivity {
    private ArticleModel mArticle;
    private ArticleModel getArticle(){
        if(mArticle == null){
            mArticle = getIntent().getExtras().getParcelable("article_object");
        }
        return mArticle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_article_webview);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_webview, menu);

        getSupportActionBar().setTitle(getArticle().getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
    @SuppressWarnings("deprecation")
    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                boolean isRoot = isTaskRoot();
                if(isRoot) {
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }
                finish();
                return true;

            case R.id.copy_link:
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(getArticle().getLink());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("text label",getArticle().getLink());
                    clipboard.setPrimaryClip(clip);
                }
                Utils.showToast(this, getString(R.string.share_copy_link_success));
                return true;

            case R.id.open_in_browser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getArticle().getLink()));
                startActivity(browserIntent);
                return true;

            //TODO Open with native browser
            case R.id.share_to_apps:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getArticle().getTitle()+"\n"+getArticle().getLink()+"\n\n- "+getString(R.string.share_from)+" "+getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_article_to)));

                return true;
        }





        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {
        private WebView mWebView;
        private boolean misLoading = false;
        private ArticleModel mArticle;

        public PlaceholderFragment() {
        }

        private ArticleModel getArticle(){
            if(mArticle == null){
                mArticle = getActivity().getIntent().getExtras().getParcelable("article_object");
            }
            return mArticle;
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article_webview, container, false);
            mWebView = (WebView) rootView.findViewById(R.id.webview);
            return rootView;
        }


        @SuppressLint("NewApi")
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);



            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if(currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
                mWebView.getSettings().setSupportZoom(true);
                mWebView.getSettings().setDisplayZoomControls(false);
                mWebView.getSettings().setBuiltInZoomControls(true);
                mWebView.getSettings().setUseWideViewPort(true);
                //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            mWebView.getSettings().setJavaScriptEnabled(true);



            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url){


                }
            });

            final ActionBarActivity activity = (ActionBarActivity)getActivity();

            mWebView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress){
                    //Make the bar disappear after URL is loaded, and changes string to Loading...
//                    activity.setTitle("Loading...");
//                    activity.setProgress(progress * 100); //Make the bar disappear after URL is loaded



                    // Return the app name after finish loading
                    if(progress == 100){
                        misLoading = false;
                        if(activity!=null)
                            activity.setSupportProgressBarIndeterminateVisibility(false);
                    }else{
                        if(!misLoading && activity!=null){
                            activity.setSupportProgressBarIndeterminateVisibility(true);
                        }
                        misLoading = true;

                    }
                }
            });

            mWebView.loadUrl(getArticle().getLink());

        }
    }
}
