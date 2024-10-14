package com.pasc.lib.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.config.AdsConstant;
import com.pasc.lib.displayads.listener.PopupAdsClickListener;
import com.pasc.lib.displayads.popupads.PopUpAdsNTManager;
import com.pasc.lib.displayads.popupads.PopUpAdsManager;

public class MainActivity extends Activity {

    // 弹屏广告
    private PopUpAdsNTManager popUpAdsNTManager;
    private PopUpAdsManager popUpAdsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popUpAdsManager = new PopUpAdsManager(this, "", new PopupAdsClickListener() {
            @Override
            public void onClickAds(AdsBean adsBean) {
                ToastUtils.toastMsg("handle click by yourself," + adsBean.picSkipUrl);
            }
        });

        popUpAdsNTManager = new PopUpAdsNTManager(this, "nantong", new PopupAdsClickListener() {
            @Override
            public void onClickAds(AdsBean adsBean) {
                ToastUtils.toastMsg("handle click by yourself," + adsBean.picSkipUrl);
            }
        });


        findViewById(R.id.baseline_ads_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds(0);
            }
        });
        findViewById(R.id.baseline_ads_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds(1);
            }
        });

        findViewById(R.id.baseline_ads_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds(2);
            }
        });

        findViewById(R.id.baseline_ads_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds(AdsConstant.PageType.AFFAIR);
            }
        });

        findViewById(R.id.baseline_ads_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds(AdsConstant.PageType.INTERATION);
            }
        });

        findViewById(R.id.nantong_ads_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNTAds(0);
            }
        });
        findViewById(R.id.nantong_ads_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNTAds(1);
            }
        });

        findViewById(R.id.nantong_ads_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNTAds(2);
            }
        });



    }

    private void showAds(int type) {

        popUpAdsManager.showPopupAds(type, "");

    }

    private void showNTAds(int type) {

        popUpAdsNTManager.showPopupAds(type, "", "140");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popUpAdsManager != null) {
            popUpAdsManager.detach();
        }
        if (popUpAdsManager != null) {
            popUpAdsManager.detach();
        }
    }
}
