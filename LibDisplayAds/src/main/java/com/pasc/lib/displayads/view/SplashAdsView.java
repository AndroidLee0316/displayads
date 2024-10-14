package com.pasc.lib.displayads.view;

import com.pasc.lib.displayads.bean.AdsBean;

/**
 * 启动页显示广告View
 * Created by qinguohuai on 2018/11/23.
 */
public interface SplashAdsView {

    void showCountdown(long count);

    void onClickSkip();

    void countDownFinish();

    void handleClick(AdsBean adsBean);

}
