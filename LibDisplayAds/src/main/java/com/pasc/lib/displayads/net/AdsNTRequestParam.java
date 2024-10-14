package com.pasc.lib.displayads.net;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 弹屏广告入参RequestParam
 * Created by qinguohuai143 on 2018/12/28.
 */

public class AdsNTRequestParam implements Serializable {

    public AdsNTRequestParam(String token, String entry, String adsVersionCode, String lastAdId, String adsPversion) {
        this.token = token;
        this.entry = entry;
        //this.localVersion = localVersion;
        this.versionCode = adsVersionCode;
        this.lastAdId = lastAdId; // 上一条广告id
        this.pVersion = adsPversion;// 南通
    }

    @SerializedName("token")
    public String token;
    @SerializedName("entry")
    public String entry;
    @SerializedName("showType")
    public String showType = "android";
    @SerializedName("id")
    public String lastAdId;
    @SerializedName("pVersion")
    public String pVersion;
//    @SerializedName("localVersion")
//    public String localVersion;
    @SerializedName("versionCode")
    public String versionCode;

}
