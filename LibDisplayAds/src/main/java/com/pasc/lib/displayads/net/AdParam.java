package com.pasc.lib.displayads.net;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.displayads.BuildConfig;

import java.io.Serializable;

/**
 * Created by huanglihou519 on 2018/6/12.
 */

public class AdParam implements Serializable {
//    public static final String ENTRY_TYPE_MAIN_PAGE = "sy";
//    public static final String ENTRY_TYPE_GOVERNMENT = "zw";
//    public static final String ENTRY_TYPE_LIFE = "sh";

    public static final String ENTRY_TYPE_MAIN_PAGE = "1";
    public static final String ENTRY_TYPE_GOVERNMENT = "2";
    public static final String ENTRY_TYPE_LIFE = "3";


    public AdParam(String entry, String appVersion, String versionCode, String pVersion, String adId, String token) {
        this.entry = entry;
        this.appVersion = appVersion;
        this.versionCode = versionCode;
        this.pVersion = pVersion;
        this.adId = adId; // 上一条广告id
        this.token = token;
    }

    @SerializedName("entry")
    public String entry;
    @SerializedName("showType")
    public String showType = "android";
    @SerializedName("id")
    public String adId;
    @SerializedName("versionCode")
    public String versionCode;
    @SerializedName("appVersion")
    public String appVersion;
    @SerializedName("token")
    public String token;
    @SerializedName("pVersion")
    public String pVersion;


}
