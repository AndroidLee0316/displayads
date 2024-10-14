package com.pasc.lib.displayads.view.webview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebView;


public class BrowserJsInterface {
    private static final String TAG = "BrowserJsInterface";

    private Context mContext;

    private WebView mWebView;

    public BrowserJsInterface(@NonNull Context context, @NonNull WebView webView) {
        mContext = context;
        mWebView = webView;
    }

}
