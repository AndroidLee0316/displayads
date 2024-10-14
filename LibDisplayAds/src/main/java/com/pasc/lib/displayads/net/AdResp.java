package com.pasc.lib.displayads.net;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.displayads.bean.AdsBean;

import java.io.Serializable;

/**
 * Created by huanglihou519 on 2018/6/12.
 */

public class AdResp implements Serializable {
    @SerializedName("projectileInfo") public AdsBean adBean;
    @SerializedName("version") public String version;
    @SerializedName("showTime") public long showTime = -1;
}
