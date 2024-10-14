package com.pasc.lib.displayads.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String name = "pasc_ads_shared_preference";
    private int mode = Context.MODE_PRIVATE;

    public SharedPreferencesUtil(Context context) {
        this.sp = context.getSharedPreferences(name, mode);
        this.editor = sp.edit();
    }

    /**
     * 创建一个工具类，默认打开名字为name的SharedPreferences实例
     * @param context
     * @param name   唯一标识
     * @param mode   权限标识
     */
    public SharedPreferencesUtil(Context context, String name, int mode) {
        this.sp = context.getSharedPreferences(name, mode);
        this.editor = sp.edit();
    }


    public void saveLong(String key, long value) {
        editor.putLong(key, value).commit();
    }


    public long getLong(String key, long defaultValue) {
        if (sp != null){
            return sp.getLong(key, defaultValue);
        }

        return 0;
    }

    public void saveString(String key, String value){
        editor.putString(key, value).commit();
    }

    public String getString(String key){
        if (sp != null) {
            return sp.getString(key, "");
        }
        return "";
    }

    /**
     * 添加信息到SharedPreferences
     *
     * @param map
     * @throws Exception
     */
    public void add(Map<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            editor.putString(key, map.get(key));
        }
        editor.commit();
    }

    /**
     * 删除信息
     *
     * @throws Exception
     */
    public void deleteAll() throws Exception {
        editor.clear();
        editor.commit();
    }

    /**
     * 删除一条信息
     */
    public void delete(String key){
        editor.remove(key);
        editor.commit();
    }


    /**
     * 获取此SharedPreferences的Editor实例
     * @return
     */
    public SharedPreferences.Editor getEditor() {
        return editor;
    }


}
