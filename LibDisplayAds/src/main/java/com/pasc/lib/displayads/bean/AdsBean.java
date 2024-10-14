package com.pasc.lib.displayads.bean;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.displayads.R;

import java.io.Serializable;

/**
 * Created by huanglihou519 on 2018/6/12.
 * 弹窗广告信息
 */

public class AdsBean implements Serializable {

  private static final long serialVersionUID = 7356499696250136948L;

  @SerializedName("picUrl")
  public String picUrl;
  @SerializedName("picSkipUrl")
  public String picSkipUrl;
  @SerializedName("picName")
  public String picName;
  @SerializedName("id")
  public String id;
  @SerializedName("frequency")
  public String frequency;
  @SerializedName("version")
  public String version;
  @SerializedName("isEnable")
  public String isEnable;
  @SerializedName("showEndDay")
  public long showEndDay;
  @SerializedName("title")
  public String title = "";
  @SerializedName("urlType")
  public String urlType;
  @SerializedName("showEndTime")
  public long showEndTime;
  //for splash pic
  @SerializedName("url")
  public String url;
  @SerializedName("showStartTime")
  public long showStartTime;
  public long showTime;
  @SerializedName("contentType")
  public int contentType; //弹屏样式（1：图片；2：文本）
  @SerializedName("textContent")
  public String textContent;
  @SerializedName("serviceId")
  public String serviceId;
  @SerializedName("pVersion")
  public String pVersion;

  public boolean isEnable() {
    return isEnable.equals("1");
  }


  public int getPopLayout() {
//    return contentType == 1 ? R.layout.ads_dialog_popup_adslayout : R.layout.ads_dialog_popup_noticelayout;
    return contentType == 1 ? R.layout.ads_dialog_popup_adslayout : R.layout.ads_dialog_popup_notice_layout;
  }

  public boolean isEnd() {
    return System.currentTimeMillis() > showEndDay;
  }

  public boolean isSplashEnd() {
    return System.currentTimeMillis() > showEndTime;
  }
}
