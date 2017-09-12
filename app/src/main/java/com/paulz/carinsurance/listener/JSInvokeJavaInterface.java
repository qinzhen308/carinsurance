package com.paulz.carinsurance.listener;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.paulz.carinsurance.ui.CommonWebActivity;

/**
 * Created by paulz on 2016/7/1.
 */
public class JSInvokeJavaInterface {

    private Activity context;
    private WebView mWebView;

    public JSInvokeJavaInterface(Activity context,WebView view){
        this.context=context;
        mWebView=view;
    }


    @JavascriptInterface
    public void openDetail(String url){
        CommonWebActivity.invoke(context,url,null);
    }

    @JavascriptInterface
    public void backWebPage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.goBack();

            }
        });

    }

    @JavascriptInterface
    public void nextWebPage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.goForward();
            }
        });

    }

    @JavascriptInterface
    public void finishNativePage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.stopLoading();
                context.finish();
            }
        });

    }


}
