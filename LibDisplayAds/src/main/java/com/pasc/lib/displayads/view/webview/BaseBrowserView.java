package com.pasc.lib.displayads.view.webview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pasc.lib.displayads.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseBrowserView extends WebView {
  public static final String TAG = "BrowserView";
  public static final List<String> WHITE_HTTPS_HOST_FILTER = new ArrayList<String>() {{
      add("(.*)?qbox.me");
      add("(.*)?pingan.com.cn");
      add("(.*)?qq.com");
      add("202.69.28.21");
  }};
  /**
   * web与native交互的时候，跟进该过滤列表隐藏title显示虚拟back按键
   */
  public static final List<String> WEB_TITLE_HOST_FILTER = new ArrayList<String>() {{
      add("test-iicp-dmzstg.pingan.com.cn");
      add("iicp.pingan.com.cn");
      add("202.69.28.21");
  }};
  protected Context context;
  /**
   * 需要拦截的错误码
   */
  private List<Integer> errorCodeList = new ArrayList<Integer>() {{
      add(WebViewClient.ERROR_CONNECT);
      //add(WebViewClient.ERROR_HOST_LOOKUP);
      add(WebViewClient.ERROR_TIMEOUT);
  }};
  private String mUrl;
  private  BrowserViewCallback callback;
  private boolean enableHostCheck = false;
  private WebScrollListener listener;
  private WebViewClient webViewClient = new WebViewClient() {
      @Override
      public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
          super.doUpdateVisitedHistory(view, url, isReload);
          if (callback != null) {
              callback.doUpdateVisitedHistory();
          }
      }

      @Override
      public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {

          return super.shouldInterceptRequest(webView, s);
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
          //PascLog.d(TAG, url);
          super.onPageStarted(view, url, favicon);
          if (callback != null) {
              callback.onPageStarted(view, url, favicon);
          }
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
          //PascLog.d(TAG, url);
          if (callback != null) {
              return callback.shouldOverrideUrlLoading(view, url);
          }
          return super.shouldOverrideUrlLoading(view, url);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
          //PascLog.d(TAG, url);
          super.onPageFinished(view, url);
          if (callback != null) {
              callback.onPageFinished(view, url);
          }
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request,
                                  WebResourceError error) {
//          int errorCode = error != null ? error.getErrorCode() : 0;
//          String failingUrl = request != null ? request.getUrl().toString() : "";
//          if (callback != null) {
//              callback.onReceiveError(failingUrl, errorCode);
//              if (errorCodeList.contains(errorCode) ) {
//                  //callback.onCustomError(failingUrl, errorCode);
//              }
//          }
      }

      @Override
      public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                      WebResourceResponse errorResponse) {
          super.onReceivedHttpError(view, request, errorResponse);
//          PascLog.d(new String[]{request.getUrl().toString(),
//                  errorResponse.getStatusCode()+""},"onReceivedHttpError request: %s, statusCode: %s");
      }

      @Override
      public void onReceivedError(WebView view, int errorCode, String description,
                                  String failingUrl) {
          //super.onReceivedError(view, errorCode, description, failingUrl);
          if (callback != null) {
              callback.onReceiveError(failingUrl, errorCode);
              if (errorCodeList.contains(errorCode)) {
                  //callback.onCustomError(failingUrl, errorCode);
              }
          }
//          PascLog.d(TAG, failingUrl, errorCode);
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
          if (enableHostCheck) {
              if (view != null && !TextUtils.isEmpty(view.getUrl())) {
                  String url = view.getUrl();
                  Uri uri = Uri.parse(url);
                  if (uri != null) {
                      String host = uri.getHost();
                      for (String filterHost : BaseBrowserView.WHITE_HTTPS_HOST_FILTER) {
                          if (host.matches(filterHost)) {
                              handler.proceed();
                              return;
                          }
                      }
                  }
              }
              showSslErrorDialog(handler);
          } else {
              handler.proceed();
          }
      }
  };
  private WebChromeClient webChromeClient = new WebChromeClient() {

      @Override
      public void onProgressChanged(WebView view, int newProgress) {
          if (callback != null) {
              callback.onProgressChanged(view, newProgress);
          }
      }

      @Override
      public void onReceivedTitle(WebView view, String title) {
          if (callback != null) {
              callback.onReceivedTitle(view, title);
          }
      }

//      @Override
//      public void onShowCustomView(View view,
//                                   IX5WebChromeClient.CustomViewCallback customViewCallback) {
//          PascLog.d("BrowserView#onShowCustomView....");
//          if (callback != null) {
//              callback.onShowCustomView(view, customViewCallback);
//          }
//      }

      @Override
      public void onHideCustomView() {
          //PascLog.d("BrowserView#onHideCustomView....");
          if (callback != null) {
              callback.onHideCustomView();
          }
      }

      @Override
      public View getVideoLoadingProgressView() {
          //PascLog.d("BrowserView#getVideoLoadingProgressView....");
          if (callback != null) {
              return callback.getVideoLoadingProgressView();
          }
          return super.getVideoLoadingProgressView();
      }

      @Override
      public void onGeolocationPermissionsHidePrompt() {
          // PascLog.d("onGeolocationPermissionsHidePrompt....");
          super.onGeolocationPermissionsHidePrompt();
      }

//      @Override
//      public void onGeolocationPermissionsShowPrompt(String origin,
//                                                     GeolocationPermissionsCallback callback) {
//          callback.invoke(origin, true, false);
//          super.onGeolocationPermissionsShowPrompt(origin, callback);
//      }

      // For Android 3.0+
      public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
          // PascLog.d("onFileChooser for Android 3.0 +");
          if (callback != null) {
              callback.openFileChooser(uploadMsg, acceptType);
          }
      }

      //For Android 4.1
//      @Override
//      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
//                                  String capture) {
//          PascLog.d("openFileChooser for Android 4.1");
//          if (callback != null) {
//              callback.openFileChooser(uploadMsg, acceptType, capture);
//          }
//      }

      // android 5.0
      @Override
      public boolean onShowFileChooser(WebView webView,
                                       ValueCallback<Uri[]> filePathsCallback, FileChooserParams fileChooserParams) {
          if (callback != null) {
              return callback.onShowFileChooser(webView, filePathsCallback, fileChooserParams);
          }
          return false;
      }

      @Override
      public boolean onJsAlert(WebView webView, String s, String message, JsResult jsResult) {
          if (message.contains("页面只能在微信浏览器中打开")) {//在第三方页面 个税页面存在弹框提示只能在微信中打开 产品要求去掉
              jsResult.confirm();
              return true;
          } else {
              return super.onJsAlert(webView, s, message, jsResult);
          }
      }
  };

  public BaseBrowserView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  public BaseBrowserView(Context context, AttributeSet attributeSet,
                         int i) {
    super(context, attributeSet, i);
  }

  public static void addWhiteHttpsHostFiter(String... hosts) {
      if (hosts != null && hosts.length > 0) {
          WHITE_HTTPS_HOST_FILTER.addAll(Arrays.asList(hosts));
      }
  }

  public void setWebScrollListener(WebScrollListener listener) {
      this.listener = listener;
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
      super.onScrollChanged(l, t, oldl, oldt);

      if (listener != null) {
          if (t - oldt <= 2) {
              listener.onScrollDown();
          }
          if (oldt - t >= 2) {
              listener.onScrollUp();
          }
          listener.scrollHeight(t);
      }
  }

  protected void init() {
      setWebViewClient(webViewClient);
      setWebChromeClient(webChromeClient);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
          removeJavascriptInterface("searchBoxJavaBridge_");
      }
      setHorizontalScrollBarEnabled(true);
      //        setVerticalFadingEdgeEnabled(true);
      WebSettings settings = getSettings();
      if (Build.VERSION.SDK_INT >= 21) {//WebView支持https和http混合内容页面
          settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
      }
      settings.setUseWideViewPort(true);
      settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
      settings.setLoadWithOverviewMode(true);
      settings.setAppCacheEnabled(true);
      settings.setCacheMode(WebSettings.LOAD_DEFAULT);//缓存策略
      // TODO 待优化
      //String appCachePath = FileUiUtils.getExternalCacheDir(context).getAbsolutePath();
      //settings.setAppCachePath(appCachePath);
      settings.setDomStorageEnabled(true);
      settings.setDatabaseEnabled(true);
      settings.setGeolocationEnabled(true);
      settings.setAppCacheMaxSize(Long.MAX_VALUE);
      settings.setGeolocationDatabasePath("/data/data/" + getContext().getPackageName() + "/geo");
//      settings.setUserAgentString(settings.getUserAgentString()
//              + "/SZSMT_Android,VERSION:"
//              + CommonUtils.getVersionName(getContext()));
      settings.setAllowFileAccess(true);
      settings.setJavaScriptEnabled(true);
      settings.setAllowContentAccess(true);

      setBackgroundColor(Color.WHITE);
  }

  protected abstract Object getJsInterface();

  public void loadUrl(String url) {
      if (url != null && !url.equals(this.mUrl)) {
          this.mUrl = url;
          super.loadUrl(url);
      }
  }

  /**
   * WebView调用H5的方法
   */
  public void loadJavaScript(String methodName, String... parameters) {
      StringBuilder sb = new StringBuilder();
      sb.append("javascript:");
      sb.append(methodName + "(");
      for (String s : parameters) {
          sb.append("'" + s + "',");
      }
      if (",".equals(sb.charAt(sb.length() - 1))) {
          sb.deleteCharAt(sb.length() - 1);
      }
      sb.append(")");
      loadUrl(sb.toString());
  }

  /**
   * 允许重复加载相同地址
   */
  public void loadCanRepeatUrl(String url) {
      if (url != null) {
          this.mUrl = url;
          super.loadUrl(url);
      }
  }

  public void loadParamsUrl(String url) {
      if (url != null && !url.equals(this.mUrl)) {
          url = getInsecureUrl(url);
          super.loadUrl(url);
      }
  }

  private String getInsecureUrl(String url) {
      if (TextUtils.isEmpty(url)) {
          return url;
      }
      return url;
  }

  public void setCallback( BrowserViewCallback callback) {
      this.callback = callback;
  }

  public void setEnableHostCheck(boolean enableHostCheck) {
      this.enableHostCheck = enableHostCheck;
  }

  @Override
  public void destroy() {
      super.destroy();
      if (callback != null) {
          callback.onWebViewDestroy();
      }
  }

  private void showSslErrorDialog(final SslErrorHandler handler) {
      // TODO 暂时注释

//      final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//      builder.setTitle(R.string.temp_prompt)
//              .setMessage(String.format(getResources().getString(R.string.temp_unsafe_link), mUrl))
//              .setPositiveButton(R.string.temp_ignore, new DialogInterface.OnClickListener() {
//                  @Override
//                  public void onClick(DialogInterface dialogInterface, int i) {
//                      handler.proceed();
//                      dialogInterface.dismiss();
//                  }
//              })
//              .setNegativeButton(R.string.temp_cancel, new DialogInterface.OnClickListener() {
//                  @Override
//                  public void onClick(DialogInterface dialogInterface, int i) {
//                      dialogInterface.dismiss();
//                      callback.stopLoadingWhileSslErrorOccurred();
//                  }
//              })
//              .create()
//              .show();
  }
}
