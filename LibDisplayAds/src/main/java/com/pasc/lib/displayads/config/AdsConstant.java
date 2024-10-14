package com.pasc.lib.displayads.config;


import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdsConstant {

    public static final String ENTRY_TYPE_MAIN_PAGE = "sy";
    public static final String ENTRY_TYPE_GOVERNMENT = "zw";
    public static final String ENTRY_TYPE_LIFE = "sh";
    public static final String ENTRY_TYPE_NOT_SUPPORT = "none";
    //这里暂时使用如果项目新加就添加字段吧，为了兼容就版本，新加字段不合理但是是最简单的
    //办事
    public static final String ENTRY_TYPE_AFFAIR = "bs";
    //互动
    public static final String ENTRY_TYPE_INTERATION = "hd";
    // 南通百通项目，type为1，2，3
    public static final String PASC_ADS_FOR_NANTONG_PROJECT = "nantong";
    public static final String ENTRY_TYPE_MAIN_PAGE_NT = "1";
    public static final String ENTRY_TYPE_GOVERNMENT_NT = "2";
    public static final String ENTRY_TYPE_LIFE_NT = "3";
    // ACache Size
    public static final int MAX_CACHE_SIZE = 1000 * 1000 * 50;


    public @interface PageType {
        int MAIN = 0;
        int GOVERNMENT = 1;
        int LIFE = 2;
        int NONE = 3;
        //这里写在NONE后面不合理，但是怕影响其他项目，先这样吧
        int AFFAIR = 4;
        int INTERATION = 5;
    }

    public @interface Frequency {
        int ONE_TIME = 1;//只弹一次
        int ONE_DAY_ONE_TIME = 2;//一天一次
        int EVERY_TIME = 3;//每次启动
        int NONE = 4;
    }

    /**
     * 获取请求弹屏广告接口需要的Type
     *
     * @param type
     * @return
     */
    public static String getPopupType(int type) {
        switch (type) {
            case PageType.MAIN:
                return ENTRY_TYPE_MAIN_PAGE;
            case PageType.GOVERNMENT:
                return ENTRY_TYPE_GOVERNMENT;
            case PageType.LIFE:
                return ENTRY_TYPE_LIFE;
            case PageType.AFFAIR:
                return ENTRY_TYPE_AFFAIR;
            case PageType.INTERATION:
                return ENTRY_TYPE_INTERATION;
            default:
                Log.e("PASC POPUPADS", "not support type");
                return ENTRY_TYPE_NOT_SUPPORT;
        }
    }

    public static String getPopupTypeNT(String projectName, int type) {
        if (PASC_ADS_FOR_NANTONG_PROJECT.contains(projectName)) {
            switch (type) {
                case PageType.MAIN:
                    return ENTRY_TYPE_MAIN_PAGE_NT;
                case PageType.GOVERNMENT:
                    return ENTRY_TYPE_GOVERNMENT_NT;
                case PageType.LIFE:
                    return ENTRY_TYPE_LIFE_NT;
                default:
                    Log.e("PASC POPUPADS", "not support type");
                    return ENTRY_TYPE_NOT_SUPPORT;
            }
        } else {
            switch (type) {
                case PageType.MAIN:
                    return ENTRY_TYPE_MAIN_PAGE;
                case PageType.GOVERNMENT:
                    return ENTRY_TYPE_GOVERNMENT;
                case PageType.LIFE:
                    return ENTRY_TYPE_LIFE;
                default:
                    Log.e("PASC POPUPADS", "not support type");
                    return ENTRY_TYPE_NOT_SUPPORT;
            }
        }

    }

    /**
     * 获取广告显示频率
     *
     * @return
     */
    public static int getFrequency(String frequency) {
        switch (frequency) {
            case "1":
                return Frequency.ONE_TIME;
            case "2":
                return Frequency.ONE_DAY_ONE_TIME;
            case "3":
                return Frequency.EVERY_TIME;
            default:
                return Frequency.NONE;
        }
    }

    /**
     * 检查type有效性
     *
     * @param type
     * @return
     */
    public static boolean checkPopupAdsTypeValid(int type) {
        String reg = "[0-3]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(type + "");
        return matcher.matches();
    }

}
