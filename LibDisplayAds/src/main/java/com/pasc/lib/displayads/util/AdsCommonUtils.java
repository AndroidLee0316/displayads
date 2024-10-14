package com.pasc.lib.displayads.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdsCommonUtils {

    /**
     * 拨号弹窗
     *
     * @param context
     * @param phoneNum
     */
    public static void showAdsCallPhoneDialog(final Context context, final String phoneNum) {

        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }

        AdsCommonDialog dialog = new AdsCommonDialog(context);
        dialog.setCancelText("取消");
        dialog.setConfirmText("呼叫");
        dialog.setmTvContext(phoneNum);
        dialog.setOnSelectedListener(new AdsCommonDialog.OnSelectedListener() {
            @Override
            public void onSelected() {

                call(context, phoneNum);
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();
    }

    /**
     * 调用拨号界面
     *
     * @param context
     * @param phone
     */
    public static void call(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 通过Webview展示富文本
     * @param originalTitle
     * @param originalContent
     * @return
     */
    public static String addContentToHtml(String originalTitle, String originalContent) {
        String css = "<style type=\"text/css\"> </style>";
        String html = "<html><header><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, "
                + "user-scalable=no>" + css + "</header>" + "<body>" + formatTitle(originalTitle) + originalContent + "</body>" + "</html>";
        return html;
    }

    public static String formatTitle(String title) {
        return "<p>\n"
                + "  <span style=\"font-size:17px\">\n"
                + "   <span style=\"line-height:normal\">\n"
                + "    <span style=\"background-color:#ffffff\">\n"
                + "     <span style=\"color:#000000\">"
                + "      <strong>"
                + title
                + "      </strong>"
                + "     </span>\n"
                + "    </span>\n"
                + "   </span>\n"
                + "  </span>\n"
                + "</p>";
    }


    /**
     * 处理显示的内容：
     * 1、电话、url添加href标签
     * 2、过滤img
     * @param htmlContent
     * @return
     */
    public static String formatHybridContent(String htmlContent) {

        HashMap<String, String> map = new HashMap();

        String plainText = Html.fromHtml(htmlContent.replaceAll("<a.*?</a>", "")).toString();

        String phoneRegex = ".*(^(1\\d{2}-?\\d{4}-?\\d{4})$)|(^([1-9]\\d{4,7})$)|(^(0\\d{2,3}-?\\d{5,8})$)";
        Pattern phonePattern = Pattern.compile(phoneRegex, Pattern.MULTILINE);
        Matcher phoneMatcher = phonePattern.matcher(plainText.toString());

        while (phoneMatcher.find()) {
            String s = phoneMatcher.group();
            int start = phoneMatcher.start();
            int end = phoneMatcher.end();
            if (start < 0 || start >= end) {
                continue;
            }
            StringBuilder value = new StringBuilder();
            value.append("<a href=\"tel:").append(s).append("\">").append(s).append("</a>");
            map.put(s, value.toString());
        }

        String urlRegex = "^(((www)|(http|ftp|https)://))(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_#\\./-~-]*)?$";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.MULTILINE);
        Matcher urlMatcher = pattern.matcher(plainText.toString());

        while (urlMatcher.find()) {
            String s = urlMatcher.group();
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            if (start < 0 || start >= end) {
                continue;
            }
            StringBuilder value = new StringBuilder();
            value.append("<a href=\"").append(s).append("\">").append(s).append("</a>");
            map.put(s, value.toString());
        }

        // 不显示图片，过滤img
        String imgRegex = "<img\\s?src.*?>";
        Pattern imgPattern = Pattern.compile(imgRegex, Pattern.MULTILINE);
        Matcher imgMatcher = imgPattern.matcher(htmlContent);

        while (imgMatcher.find()) {
            htmlContent = htmlContent.replaceAll(imgRegex, "<img src=\"\"/>");
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            htmlContent = htmlContent.replaceAll(entry.getKey(), entry.getValue());
        }

        return htmlContent;
    }

}
