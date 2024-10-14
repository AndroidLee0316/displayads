package com.pasc.lib.displayads.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * 服务端返回数据解析Bean
 * Created by qinguohuai143 on 2018/12/28.
 */

public class PopupAdsRspBean implements Serializable {

    @SerializedName("msg") public String msg;
    @SerializedName("code") public String code;
    @SerializedName("data") public PopupAdsBean popupAdsBean;

}