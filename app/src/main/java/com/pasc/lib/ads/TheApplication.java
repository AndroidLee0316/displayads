package com.pasc.lib.ads;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.util.AppUtils;
import com.pasc.lib.displayads.config.DisplayAdsManager;
import com.pasc.lib.hybrid.HybridInitConfig;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.callback.HybridInitCallback;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;
import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.router.RouterManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;


public class TheApplication extends Application {

    private static Context applicationContext;

    private static final int ENV_TYPE = 2;  // 2-beta


    // 基线测试环境
    private static final String HOST_URL = "http://nxsc-stg.yun.city.pingan.com/";


    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();

        if (AppUtils.getPIDName(this).equals(getPackageName())) {//主进程
            AppProxy.getInstance().init(this, false)
                    .setIsDebug(BuildConfig.DEBUG)
                    .setProductType(BuildConfig.PRODUCT_FLAVORS_TYPE)
                    .setHost(HOST_URL) // 自定义HostUrl
                    .setVersionName(BuildConfig.VERSION_NAME);


            /**********网络*************/
            initNet();

            initHybrid();

            initImageLoader();

            // ARouter
            initARouter();

            initDiaplayAds();

        }
    }

    private void initDiaplayAds() {
//        String popupServerurl = "nantongsmt/appProjectile/queryAppProjectileInfoNew.do";
//        String splashServerurl = "";
//        DisplayAdsManager.getInstance().init(new AdsConfigBean(popupServerurl, splashServerurl));
        DisplayAdsManager.getInstance().initConfig(this, "pasc.pingan.service.ads.json");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Context getApplication() {
        return applicationContext;
    }


    /****初始化网络****/
    private void initNet() {

        NetConfig config = new NetConfig.Builder(this)
//                .baseUrl(UrlManager.getApiUrlRoot())
                .baseUrl(HOST_URL)
                .headers(HeaderUtil.getHeaders(BuildConfig.DEBUG, null))
                .gson(ConvertUtil.getConvertGson())
                .isDebug(BuildConfig.DEBUG)
                .build();

        NetManager.init(config);

        DownLoadManager.getDownInstance().init(this, 3, 5, 0);
    }

    /**
     * 初始化图片加载框架
     */
    private void initImageLoader() {
        PascImageLoader.getInstance().init(this, PascImageLoader.GLIDE_CORE, R.color.C_EAF7FF);
    }

    private void initARouter() {
        RouterManager.initARouter(this, BuildConfig.DEBUG);
    }

    private void initHybrid() {

        PascHybrid.getInstance().init(new HybridInitConfig()
                .setHybridInitCallback(new HybridInitCallback() {
                    @Override
                    public void loadImage(ImageView imageView, String url) {
                        PascImageLoader.getInstance().loadImageUrl(url, imageView);
                    }

                    @Override
                    public void setWebSettings(WebSettings settings) {
                        settings.setUserAgent(settings.getUserAgentString()
                                + "/openweb=paschybrid/MaanshanSMT_Android,VERSION:"
                                + BuildConfig.VERSION_NAME);
                    }

                    @Override
                    public String themeColorString() {
                        return "#333333";
                    }

                    @Override
                    public int titleCloseButton() {
                        return WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE;
                    }

                    @Override
                    public void onWebViewCreate(WebView webView) {

                    }

                    @Override
                    public void onWebViewProgressChanged(WebView webView, int progress) {

                    }

                    @Override
                    public void onWebViewPageFinished(WebView webView, String url) {

                    }
                }));
    }

}

