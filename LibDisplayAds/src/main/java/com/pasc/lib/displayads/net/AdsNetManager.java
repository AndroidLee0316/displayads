package com.pasc.lib.displayads.net;

import android.text.TextUtils;

import com.pasc.lib.displayads.bean.PopupAdsBean;
import com.pasc.lib.displayads.bean.PopupAdsRspBean;
import com.pasc.lib.displayads.config.AdsConstant;
import com.pasc.lib.displayads.config.DisplayAdsManager;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.transform.RespTransformer;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


public class AdsNetManager {

    // 弹屏广告
    // 基线后台因为网关验证Token，故因Token分了俩接口
    public static final String GET_POPUP_ADS_BASELINE = "api/platform/screenAdvertisement/queryAppProjectileInfo";
    public static final String GET_POPUP_ADS_NOTOKEN_BASELINE = "api/platform/screenAdvertisement/queryAppProjectileInfoNoAuth";

    // 弹屏广告 南通地址
    public static final String GET_POPUP_ADS_NT = "nantongsmt/appProjectile/queryAppProjectileInfoNew.do";

    // 获取闪屏广告列表
    public static final String GET_SPLASH_ADS_BASELINE = "api/platform/screenAdvertisement/queryAllScreenAdvertisement";
    // 南通地址
    public static final String GET_SPLASH_ADS_NT = "";


    /**
     * 获取首页弹屏广告
     *
     * @param entry
     * @param localVersion
     * @param lastAdId
     * @param token
     * @return
     */
    public static Single<PopupAdsRspBean> getPouUpAdsInfo(String projectName, String entry, String localVersion, String lastAdId, String token) {

        AdsRequestParam adsRequestParam = new AdsRequestParam(entry, localVersion, lastAdId);

        RespTransformer<PopupAdsRspBean> respTransformer = RespTransformer.newInstance();

        String serverPath = DisplayAdsManager.getInstance().getPopupAdsServerPath();
        // 后台因为网关验证Token，有无Token分了俩接口
        if (TextUtils.isEmpty(token)) {
            if (TextUtils.isEmpty(serverPath)) {
                serverPath = GET_POPUP_ADS_NOTOKEN_BASELINE;
            }
            return ApiGenerator.createApi(AdsApi.class)
                    .getPopupAdInfoNoToken(serverPath, adsRequestParam)
                    .compose(respTransformer)
                    .subscribeOn(Schedulers.io());
        } else {
            if (TextUtils.isEmpty(serverPath)) {
                serverPath = GET_POPUP_ADS_BASELINE;
            }
            return ApiGenerator.createApi(AdsApi.class)
                    .getPopupAdInfo(serverPath, token, adsRequestParam)
                    .compose(respTransformer)
                    .subscribeOn(Schedulers.io());
        }
    }

    /**
     * 获取首页弹屏广告
     *
     * @param entry
     * @param lastAdId
     * @param token
     * @return
     */
    public static Single<PopupAdsBean> getPouUpAdsInfoForNT(String entry, String adsVersionCode, String lastAdId, String adsPversion, String token) {

        AdsNTRequestParam adsRequestParam = new AdsNTRequestParam(token, entry, adsVersionCode, lastAdId, adsPversion);
        RespTransformer<PopupAdsBean> respTransformer = RespTransformer.newInstance();

        String serverPath = DisplayAdsManager.getInstance().getPopupAdsServerPath();

        if (TextUtils.isEmpty(serverPath)) {
            serverPath = GET_POPUP_ADS_NT;
        }
        return ApiGenerator.createApi(AdsApi.class)
                .getPopupAdInfoForNT(serverPath, new BaseParam<>(adsRequestParam))
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

}
