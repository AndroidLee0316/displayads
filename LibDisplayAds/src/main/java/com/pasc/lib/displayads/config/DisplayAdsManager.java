package com.pasc.lib.displayads.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.displayads.bean.AdsConfigBean;
import com.pasc.lib.displayads.bean.DisplayAdsConfigBean;
import com.pasc.lib.displayads.util.AssetsUtil;

/**
 * 广告相关配置项
 */
public class DisplayAdsManager {

    private static volatile DisplayAdsManager instance;
    private AdsConfigBean adsConfigBean;
    private DisplayAdsConfigBean.DisplayAdsBean displayAdsBean;

    private DisplayAdsManager() {

    }

    public static DisplayAdsManager getInstance() {
        if (instance == null) {
            synchronized (DisplayAdsManager.class) {
                if (instance == null) {
                    instance = new DisplayAdsManager();
                }
            }
        }

        return instance;
    }

    /**
     * 初始化后台地址
     *
     * @param configBean
     */
    public void init(AdsConfigBean configBean) {
        this.adsConfigBean = configBean;
    }

    /**
     * 弹屏广告请求接口路径
     *
     * @return
     */
    public String getPopupAdsServerPath() {

        if (adsConfigBean != null && !TextUtils.isEmpty(adsConfigBean.popupAdsServerPath)) {
            return adsConfigBean.popupAdsServerPath;
        } else {

            return "";
        }
    }

    /**
     * 弹屏广告请求接口路径
     *
     * @return
     */
    public String getSplashAdsServerPath() {

        if (adsConfigBean != null && !TextUtils.isEmpty(adsConfigBean.splashAdsServerPath)) {
            return adsConfigBean.splashAdsServerPath;
        } else {
            return "";
        }
    }

    /**
     * 初始化配置文件
     */
    public void initConfig(Context context, String jsonPath) {
        if (TextUtils.isEmpty(jsonPath)) {
            throw new NullPointerException("请传入正确的serviceConfigPath");
        }
        try {
            DisplayAdsConfigBean displayAdsConfigBean = new Gson().fromJson(AssetsUtil.parseFromAssets(context, jsonPath), DisplayAdsConfigBean.class);
            if (displayAdsConfigBean != null) {
                displayAdsBean = displayAdsConfigBean.displayAds;
            }

        } catch (Exception e) {
            Log.v("DisplayAdsUrlDispatcher", e.getMessage());
        }
    }

    /**
     * 弹屏广告是否可用
     *
     * @return
     */
    public boolean isPopupEnable() {
        return displayAdsBean == null || displayAdsBean.popupEnable;
    }
    /**
     * 闪屏广告是否可用
     *
     * @return
     */
    public boolean isSplashEnable() {
        return displayAdsBean == null || displayAdsBean.splashEnable;
    }
}
