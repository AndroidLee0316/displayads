package com.pasc.lib.displayads.view.webview;

import android.content.Context;
import android.util.AttributeSet;


public class BrowserView extends BaseBrowserView {

    private BrowserJsInterface mJsInterface;

    public BrowserView(Context context) {
        this(context, null);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @Override
    protected Object getJsInterface() {
        mJsInterface = new BrowserJsInterface(context, BrowserView.this);
        return mJsInterface;
    }

    @Override
    public void destroy() {
        super.destroy();
//        if (mJsInterface != null) {
//            EventUtils.onH5PageEnd(mJsInterface.getPageInfo());
//        }
    }

}
