package com.pasc.lib.displayads.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lanshaomin
 * Date: 2019/7/8 下午2:26
 * Desc:
 */
public class DisplayAdsConfigBean {
    @SerializedName("displayAds")
    public DisplayAdsBean displayAds;

    public static class DisplayAdsBean {
        /**
         * name : 广告展示
         * enable : true
         */
        @SerializedName("name")
        public String name;
        @SerializedName("splashEnable")
        public boolean splashEnable;
        @SerializedName("popupEnable")
        public boolean popupEnable;
    }

}
