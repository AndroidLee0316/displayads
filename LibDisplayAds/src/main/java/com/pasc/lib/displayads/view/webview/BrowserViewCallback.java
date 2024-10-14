package com.pasc.lib.displayads.view.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


public class BrowserViewCallback implements ILoadingView {

    public void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    public void onPageFinished(WebView view, String url) {

    }

    public void doUpdateVisitedHistory() {

    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    public void onReceivedTitle(WebView view, String title) {

    }

//    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
//
//    }

    public void onHideCustomView() {

    }

    public View getVideoLoadingProgressView() {
        return null;
    }

    public void onProgressChanged(WebView view, int newProgress) {

    }

    /**
     * 异常回调（筛选选中异常回调出去）
     */
    public void onCustomError(String failingUrl, int errorCode) {

    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {

    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

    }

    //For Android 5.0
    public boolean onShowFileChooser(WebView webView,
            ValueCallback<Uri[]> filePathsCallback,
            WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }

    public boolean onReceiveError(String failingUrl, int errorCode) {
        return false;
    }

    public boolean stopLoadingWhileSslErrorOccurred() {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }
    public void onGeolocationPermissionsHidePrompt(){}

    public void onWebViewDestroy() {
    }
}
