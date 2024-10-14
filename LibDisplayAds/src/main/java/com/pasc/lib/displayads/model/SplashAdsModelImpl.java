package com.pasc.lib.displayads.model;

import android.text.TextUtils;

import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.config.DisplayAdsManager;
import com.pasc.lib.displayads.net.AdsApi;
import com.pasc.lib.displayads.net.AdsNetManager;
import com.pasc.lib.displayads.net.SplashAdsRequestParam;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.transform.RespTransformer;
import java.util.List;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


public class SplashAdsModelImpl implements ISplashAdsModel {

    /**
     * 获取启动页广告
     *
     * @param picSize
     * @param localVersion
     * @return
     */
    public Single<List<AdsBean>> getSplshAdsInfo(String picSize, String localVersion) {

        RespTransformer<List<AdsBean>> respTransformer = RespTransformer.newInstance();
        SplashAdsRequestParam splashAdsRequestParam = new SplashAdsRequestParam(localVersion, picSize);

        // 支持项目设置自己项目的服务端路径
        String serverPath = DisplayAdsManager.getInstance().getSplashAdsServerPath();
        if (TextUtils.isEmpty(serverPath)) {
            serverPath = AdsNetManager.GET_SPLASH_ADS_BASELINE;
        }

        return ApiGenerator.createApi(AdsApi.class)
                .getSplashAdInfos(serverPath, splashAdsRequestParam)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

}
