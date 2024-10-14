package com.pasc.lib.displayads.net;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 弹屏广告入参RequestParam
 * Created by qinguohuai143 on 2018/12/28.
 */

public class AdsRequestParam implements Serializable {

    public AdsRequestParam(String entry, String localVersion, String lastAdId) {
        this.entry = entry;
        this.localVersion = localVersion;
        this.lastAdId = lastAdId; // 上一条广告id
    }

    @SerializedName("entry")
    public String entry;
    @SerializedName("showType")
    public String showType = "android";
    @SerializedName("id")
    public String lastAdId;
    @SerializedName("localVersion")
    public String localVersion;

}
