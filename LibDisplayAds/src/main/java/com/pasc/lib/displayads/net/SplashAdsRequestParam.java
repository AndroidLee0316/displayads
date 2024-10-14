package com.pasc.lib.displayads.net;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by qinguohuai143 on 2018/12/28.
 */

public class SplashAdsRequestParam implements Serializable {

    public SplashAdsRequestParam(String localVersion, String picName) {
        this.localVersion = localVersion;
        this.picName = picName;
    }

    @SerializedName("localVersion")
    public String localVersion;
    @SerializedName("showOSChannel")
    public String showOSChannel = "android";
    @SerializedName("picName")
    public String picName;


}
