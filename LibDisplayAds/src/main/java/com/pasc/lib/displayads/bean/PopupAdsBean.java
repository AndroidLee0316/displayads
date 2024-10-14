package com.pasc.lib.displayads.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by qinguohuai143 on 2018/12/28.
 */

public class PopupAdsBean implements Serializable {

    @SerializedName("projectileInfo") public AdsBean adBean;
    @SerializedName("version") public String version;
    @SerializedName("showTime") public long showTime = -1;
}
