package com.pasc.lib.displayads.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AdsConfigBean implements Serializable {

    private static final long serialVersionUID = 750092599144714252L;

    public AdsConfigBean(String popupAdsServerPath, String splashAdsServerPath) {
        this.popupAdsServerPath = popupAdsServerPath;
        this.splashAdsServerPath = splashAdsServerPath;
    }

    @SerializedName("popupAdsServerPath")
    public String popupAdsServerPath;
    @SerializedName("splashAdsServerPath")
    public String splashAdsServerPath;

    public String getPopupAdsServerPath() {
        return popupAdsServerPath;
    }

    public void setPopupAdsServerPath(String popupAdsServerPath) {
        this.popupAdsServerPath = popupAdsServerPath;
    }

    public String getSplashAdsServerPath() {
        return splashAdsServerPath;
    }

    public void setSplashAdsServerPath(String splashAdsServerPath) {
        this.splashAdsServerPath = splashAdsServerPath;
    }
}
