package com.pasc.lib.displayads.presenter;

import android.widget.ImageView;

public interface SplashPresenter {

    void loadSplashAd(ImageView imageView, long delayMillis);

    void countDown();

    void handleAdsClick();

    void detach();

}
