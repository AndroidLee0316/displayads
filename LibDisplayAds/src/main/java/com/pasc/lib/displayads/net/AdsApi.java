package com.pasc.lib.displayads.net;


import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.bean.PopupAdsBean;
import com.pasc.lib.displayads.bean.PopupAdsRspBean;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseResp;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 网络接口定义
 */
public interface AdsApi {

    /**
     * 查询弹框广告信息
     */
    @POST
    Single<BaseResp<PopupAdsRspBean>> getPopupAdInfo(
            @Url String url,
            @Header("token") String token,
            @Body AdsRequestParam param);

    @POST
    Single<BaseResp<PopupAdsRspBean>> getPopupAdInfoNoToken(
            @Url String url,
            @Body AdsRequestParam param);


    @FormUrlEncoded
    @POST
    Single<BaseResp<PopupAdsBean>> getPopupAdInfoForNT(
            @Url String url,
            @Field("jsonData") BaseParam<AdsNTRequestParam> param);

    /**
     * 获取启动页广告
     */
    @POST
    Single<BaseResp<List<AdsBean>>> getSplashAdInfos(
            @Url String url,
            @Body SplashAdsRequestParam param
    );


}
