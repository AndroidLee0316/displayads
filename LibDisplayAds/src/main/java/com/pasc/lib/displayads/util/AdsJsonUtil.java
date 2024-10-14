package com.pasc.lib.displayads.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;



public class AdsJsonUtil {

    public static Gson getGson() {
        return AdsJsonUtil.InstanceHolder.gson;
    }

    public static String toJson(Object obj) {
        return toJson(obj, (Type) null);
    }

    public static String toJson(Object obj, Type typeOfT) {
        if (obj == null) {
            return "{}";
        } else if (obj.getClass() == String.class) {
            return obj.toString();
        } else {
            return typeOfT != null ? AdsJsonUtil.InstanceHolder.gson.toJson(obj, typeOfT) : AdsJsonUtil.InstanceHolder.gson.toJson(obj);
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return AdsJsonUtil.InstanceHolder.gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return AdsJsonUtil.InstanceHolder.gson.fromJson(json, classOfT);
    }

    public static Map<String, String> mapFromJson(String json) {
        Type type = (new TypeToken<HashMap<String, String>>() {
        }).getType();
        return (Map) fromJson(json, type);
    }

    private static class InstanceHolder {
        public static Gson gson = new Gson();

        private InstanceHolder() {
        }
    }

}
