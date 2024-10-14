package com.pasc.lib.displayads.view.webview;

/**
 * Created by ascetic on 2018/5/31.
 */

public interface WebScrollListener {
        void onScrollUp();//上滑
        void onScrollDown();//下滑
        void scrollHeight(int h);
}
