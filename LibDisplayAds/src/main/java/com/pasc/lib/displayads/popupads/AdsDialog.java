package com.pasc.lib.displayads.popupads;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.lib.displayads.R;
import com.pasc.lib.displayads.util.AdsCommonUtils;
import com.pasc.lib.displayads.util.CustomTagHandler;
import com.pasc.lib.displayads.view.webview.BrowserView;
import com.pasc.lib.displayads.view.webview.BrowserViewCallback;
import com.pasc.lib.imageloader.PascImageLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdsDialog extends Dialog {
    private final View root;
    private static Context context;

    private OnAdCloseListener onAdCloseListener;
    private OnAdClickListener onAdClickListener;
    // 处理通知里富文本带url的跳转
    private static OnNoticeUrlClickListener onAdNoticeUrlClickListener;

    public AdsDialog(@NonNull Context context, int layout) {
        super(context, R.style.UpdateDialog);
        root = LayoutInflater.from(context).inflate(layout, null);
        setContentView(root);
        this.context = context;

        setCanceledOnTouchOutside(false);
    }

    public AdsDialog setAdClick(int... viewIds) {
        if (root == null) {
            return this;
        }
        for (int i : viewIds) {
            View adView = root.findViewById(i);
            if (adView != null) {
                adView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dismiss();
                        if (onAdClickListener != null) onAdClickListener.onClick(v);
                    }
                });
            }

        }
        return this;
    }

    public AdsDialog setAdClose(int viewId) {
        if (root == null) {
            return this;
        }
        View closeView = root.findViewById(viewId);
        if (closeView == null) {
            return this;
        }
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();
                if (onAdCloseListener != null) onAdCloseListener.onClose(v);
            }
        });
        return this;
    }

    public AdsDialog setBindViewListener(OnAdBindViewListener onAdBindViewListener) {
        if (onAdBindViewListener != null) {
            onAdBindViewListener.onBindView(new ViewHolder(root));
        }
        return this;
    }

    public AdsDialog setOnAdCloseListener(OnAdCloseListener onCloseListener) {
        this.onAdCloseListener = onCloseListener;
        return this;
    }

    public AdsDialog setOnAdClickListener(OnAdClickListener onAdClickListener) {
        this.onAdClickListener = onAdClickListener;
        return this;
    }

    public AdsDialog setOnAdNoticeUrlClickListener(OnNoticeUrlClickListener onAdNoticeUrlClickListener) {
        this.onAdNoticeUrlClickListener = onAdNoticeUrlClickListener;
        return this;
    }

    public AdsDialog setOnAdCancelListener(OnCancelListener onCancelListener) {
        this.setOnCancelListener(onCancelListener);
        return this;
    }

    public interface OnAdCloseListener {
        void onClose(View view);
    }

    public interface OnAdClickListener {
        void onClick(View view);
    }

    public interface OnAdBindViewListener {
        void onBindView(ViewHolder holder);
    }

    /**
     * 处理通知内容（富文本）带url的跳转
     */
    public interface OnNoticeUrlClickListener {
        void onClick(View view, String url);
    }

    public static class ViewHolder {
        private View root;

        public ViewHolder(View view) {
            this.root = view;
        }

        public ViewHolder setText(int viewId, String text) {
            View view = root.findViewById(viewId);
            if (view == null || text == null || TextUtils.isEmpty(text)) {
                return this;
            }

            String imgRegex = "<img\\s?src.*?>";
            Pattern imgPattern = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE);
            Matcher imgMatcher = imgPattern.matcher(text);

            while (imgMatcher.find()) {
                text = text.replaceAll(imgRegex, "<img src=\"\"/>");
            }


            String telRegex = "tel://";
            Pattern telPattern = Pattern.compile(telRegex, Pattern.CASE_INSENSITIVE);
            Matcher telMatcher = telPattern.matcher(text);

            while (telMatcher.find()) {
                text = text.replaceAll(telRegex, "");
            }


            ((TextView) view).setText(getSpannableString(Html.fromHtml(text, null, new CustomTagHandler())));
            ((TextView) view).setMovementMethod(LinkMovementMethod.getInstance());

            return this;
        }


        public ViewHolder loadText(int viewId, String htmlContent) {
            if (root == null) {
                return this;
            }
            View view = root.findViewById(viewId);
            if (view == null || !(view instanceof BrowserView)) {
                return this;
            }
            final BrowserView browserView = (BrowserView) view;
            browserView.getSettings().setDefaultFontSize(16);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                browserView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            } else {
                browserView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            browserView.setCallback(new BrowserViewCallback() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http") | url.startsWith("www")) {

                        onAdNoticeUrlClickListener.onClick(browserView, url);

//                        BrowserBranchUtils.browserBranch(view.getContext(), url, null);

                    } else if (url.startsWith("tel")) {
                        AdsCommonUtils.showAdsCallPhoneDialog(context, url.replace("tel:", ""));
                        //Util.showCallDialog(view.getContext(), url.replace("tell:",""));
                    }
                    return true;
                }
            });
            String newContent = AdsCommonUtils.formatHybridContent(htmlContent);
            browserView.loadData(newContent.replaceAll("\\\\", ""),
                    "text/html; charset=UTF-8", null);

            return this;
        }

        public ViewHolder setImage(int viewId, String imageUrl) {
            View iv = root.findViewById(viewId);
            if (iv != null && iv instanceof ImageView) {
                PascImageLoader.getInstance().loadImageUrl(imageUrl, (ImageView) iv);
            }
            return this;
        }

        public View getView(int viewId) {
            View view = root.findViewById(viewId);
            return view;
        }

        private SpannableString getSpannableString(Spanned spanned) {
            SpannableString sp = new SpannableString(spanned);

            String phoneRegex = "(1\\d{2}-?\\d{4}-?\\d{4})|([1-9]\\d{4,7})|(0\\d{2,3}-?\\d{5,8})";
            Pattern phonePattern = Pattern.compile(phoneRegex);
            Matcher phoneMatcher = phonePattern.matcher(sp.toString());

            Log.d("sp", sp.toString());

            while (phoneMatcher.find()) {
                String s = phoneMatcher.group();
                int start = phoneMatcher.start();
                int end = phoneMatcher.end();
                if (start < 0 || start >= end) {
                    continue;
                }
                PhoneClickableSpan span = new PhoneClickableSpan(s);
                sp.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            String urlRegex = "(((www)|(http|ftp|https)://))(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_#\\./-~-]*)?";
            Pattern pattern = Pattern.compile(urlRegex);
            Matcher urlMatcher = pattern.matcher(sp.toString());

            while (urlMatcher.find()) {
                String s = urlMatcher.group();
                int start = urlMatcher.start();
                int end = urlMatcher.end();
                if (start < 0 || start >= end) {
                    continue;
                }
                URLClickableSpan span = new URLClickableSpan(s);
                sp.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            return sp;
        }


    }

    public static class PhoneClickableSpan extends ClickableSpan {
        private String phoneNum;

        public PhoneClickableSpan(String phoneNum) {
            if (TextUtils.isEmpty(phoneNum)) {
                this.phoneNum = "";
            } else {
                this.phoneNum = phoneNum.replaceAll("-", "");
            }
        }

        @Override
        public void onClick(View widget) {
            if (!TextUtils.isEmpty(phoneNum)) {
                AdsCommonUtils.showAdsCallPhoneDialog(context, phoneNum);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
        }
    }

    public static class URLClickableSpan extends ClickableSpan {
        private String url;

        public URLClickableSpan(String url) {
            this.url = url.startsWith("www.") ? "http://" + url : url;
        }

        @Override
        public void onClick(View widget) {

            onAdNoticeUrlClickListener.onClick(widget, url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
        }
    }

}
