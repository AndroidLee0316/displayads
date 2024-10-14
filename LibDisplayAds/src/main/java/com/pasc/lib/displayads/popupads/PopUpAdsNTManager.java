package com.pasc.lib.displayads.popupads;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import com.pasc.lib.displayads.R;
import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.bean.PopupAdsBean;
import com.pasc.lib.displayads.config.AdsConstant;
import com.pasc.lib.displayads.listener.PopupAdsClickListener;
import com.pasc.lib.displayads.net.AdsNetManager;
import com.pasc.lib.displayads.util.AdsDateUtil;
import com.pasc.lib.displayads.util.Cache.ACache;
import com.pasc.lib.displayads.util.TaskMachine;
import com.pasc.lib.statistics.StatisticsManager;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableMaybeObserver;


/**
 * 首页弹屏广告
 * 南通专用
 */
public class PopUpAdsNTManager {

    private static final String TAG = PopUpAdsNTManager.class.getSimpleName();

    private static final String AD_NT_INFO_MAIN_CACHE_KEY = "ad_nt_info_main_cache_key";
    private static final String AD_NT_INFO_GOVERNMENT_CACHE_KEY = "ad_nt_info_government_cache_key";
    private static final String AD_NT_INFO_LIFE_CACHE_KEY = "ad_nt_info_life_cache_key";


    private ACache aCache;

    private Activity activity;
    private CompositeDisposable disposables = new CompositeDisposable();
    private SparseBooleanArray doneSign;
    private SparseArray<TaskMachine> pendingTasks;
    private @AdsConstant.PageType int currentPage;
    private Handler uiHandler;
    private String projectName; // 项目名称

    public PopupAdsClickListener onClickAdsListener;

    private AdsDialog adsDialog;


    public PopUpAdsNTManager(Activity activity, String arg0, PopupAdsClickListener onClickAdsListener) {
        this.activity = activity;
        this.projectName = arg0;
        this.onClickAdsListener = onClickAdsListener;
        if (activity != null) {
            uiHandler = new Handler(activity.getMainLooper());
            aCache = ACache.get(new File(activity.getFilesDir(), "smt/ad"), AdsConstant.MAX_CACHE_SIZE,
                    Integer.MAX_VALUE);
        }

        doneSign = new SparseBooleanArray(4);
        pendingTasks = new SparseArray<>(4);
        pendingTasks.put(AdsConstant.PageType.MAIN, new TaskMachine(new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                uiHandler.post(command);
            }
        }));
        pendingTasks.put(AdsConstant.PageType.GOVERNMENT, new TaskMachine(new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                uiHandler.post(command);
            }
        }));
        pendingTasks.put(AdsConstant.PageType.LIFE, new TaskMachine(new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                uiHandler.post(command);
            }
        }));
        pendingTasks.put(AdsConstant.PageType.NONE, new TaskMachine(new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                uiHandler.post(command);
            }
        }));

    }


    /**
     * 显示广告
     * @param type
     * @param userToken
     * @param adsVersionCode
     */
    public void showPopupAds(int type, String userToken, String adsVersionCode) {
        if (!AdsConstant.checkPopupAdsTypeValid(type)){
            Log.e("PASC POPUPADS", "type is invalid");
            return;
        }

        currentPage = type;
        if (!doneSign.get(type, false)) {
            doneSign.put(type, true);
            getPopUpAdsData(type, userToken, adsVersionCode);
        }
        pendingTasks.get(type).perfectTask();
    }

    private void getPopUpAdsData(final int type, String userToken, String adsVersionCode) {
        if (type == AdsConstant.PageType.NONE) return;

        disposables.clear();//点击到其他页面就取消之前的
        PopupAdsBean cacheAdsBean = (PopupAdsBean) aCache.getAsObject(getAdsCacheKey(type));

        String adId = null;
        String adsVersion = "0.0";

        if (cacheAdsBean != null && cacheAdsBean.adBean != null) {
            adId = cacheAdsBean.adBean.id;
            adsVersion = cacheAdsBean.adBean.version;
        } else {
            cacheAdsBean = new PopupAdsBean();
        }

        final String finalLastAdsVersion = adsVersion;
        final String finalAdId = adId;
        final PopupAdsBean finalCacheResp = cacheAdsBean;
        final String adsType = AdsConstant.getPopupTypeNT(projectName, type);

        disposables.add(AdsNetManager.getPouUpAdsInfoForNT(adsType, adsVersionCode, adId, finalLastAdsVersion, userToken)
                .onErrorReturn(new Function<Throwable, PopupAdsBean>() {
                    @Override
                    public PopupAdsBean apply(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                        return finalCacheResp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<PopupAdsBean, PopupAdsBean>() {
                    @Override
                    public PopupAdsBean apply(PopupAdsBean adResp) throws Exception {
                        return adResp == null ? finalCacheResp : adResp;
                    }
                })
                .filter(new Predicate<PopupAdsBean>() {
                    @Override
                    public boolean test(PopupAdsBean adsReqBean) throws Exception {
                        return adsReqBean != null && adsReqBean.adBean != null && adsReqBean.adBean.isEnable();
                    }
                })
                .map(new Function<PopupAdsBean, PopupAdsBean>() {
                    @Override
                    public PopupAdsBean apply(PopupAdsBean popupAdsBean) throws Exception {
                        if (popupAdsBean != null && popupAdsBean.adBean != null){
                            if (!TextUtils.isEmpty(popupAdsBean.adBean.id)
                                    && !TextUtils.isEmpty(popupAdsBean.version)
                                    && popupAdsBean.adBean.id.equals(finalAdId)
                                    //为了保证每天弹出类型记录今天是否已经弹
                                    && popupAdsBean.version.equals(finalLastAdsVersion)) {
                                return finalCacheResp;
                            }
                        }

                        return popupAdsBean;
                    }
                })
                .subscribeWith(new DisposableMaybeObserver<PopupAdsBean>() {
                    @Override
                    public void onSuccess(final PopupAdsBean adResp) {
                        if (type != currentPage) {
                            pendingTasks.get(type).addTask(new Runnable() {
                                @Override
                                public void run() {
                                    handleAds(adResp, type);
                                }
                            });
                            return;
                        }

                        handleAds(adResp, type);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    /**
     * 广告显示逻辑：
     * 1、只显示一次的广告：只有当后端返回不为空的时候才显示；
     * 2、每天一次的广告：后台每次都返回数据，判断是否为同一条广告的规则：id+Pversion；
     * 3、每次都显示的广告：同2
     * @param popupAdsBean
     * @param type
     */
    private void handleAds(PopupAdsBean popupAdsBean, int type) {
        if (popupAdsBean == null || popupAdsBean.adBean == null){
            return;
        }

        final AdsBean adBean = popupAdsBean.adBean;
        boolean needPopup = false;

        do {
            if (!adBean.isEnable()) {
                break;
            }
            if (adBean.isEnd()) {
                break;
            }
            // 有版本升级提示框显示时，不弹出广告；
            // 解决方案：
            // 1、调用方先检查版本更新后，根据结果再调用显示广告；
            // 2、调用方当需要显示版本更新弹窗时，先调用广告的dismissPopupAds方法；

            if (popupAdsBean.showTime == -1) {
                needPopup = true;
                break;
            }
            if (AdsConstant.getFrequency(adBean.frequency) == AdsConstant.Frequency.ONE_TIME) {
                break;
            }
            if (AdsConstant.getFrequency(adBean.frequency) == AdsConstant.Frequency.EVERY_TIME) {
                needPopup = true;
                break;
            }
            if (AdsConstant.getFrequency(adBean.frequency) == AdsConstant.Frequency.ONE_DAY_ONE_TIME) {
                needPopup = !AdsDateUtil.isToday(popupAdsBean.showTime);
                break;
            }
        } while (false);

        if (needPopup) {
            adsDialog = new AdsDialog(activity, adBean.getPopLayout())
                    .setAdClick(R.id.ad_view)
                    .setAdClose(R.id.close_view)
                    .setBindViewListener(new AdsDialog.OnAdBindViewListener() {
                        @Override
                        public void onBindView(AdsDialog.ViewHolder holder) {
                            holder.setImage(R.id.ad_view, adBean.picUrl)
                                    .setText(R.id.tv_title, adBean.title)
                                    .setText(R.id.tv_content, adBean.textContent);
                        }
                    })
                    .setOnAdClickListener(new AdsDialog.OnAdClickListener() {
                        @Override
                        public void onClick(View view) {

                            onClickAdsListener.onClickAds(adBean);
                            dismissPopupAds();

                        }
                    }).setOnAdCloseListener(new AdsDialog.OnAdCloseListener() {
                        @Override
                        public void onClose(View view) {
                            dismissPopupAds();
                        }
                    }).setOnAdNoticeUrlClickListener(new AdsDialog.OnNoticeUrlClickListener() {
                        @Override
                        public void onClick(View view, String url) {
                            AdsBean urlAdsBean = new AdsBean();
                            urlAdsBean.picSkipUrl = url;
                            onClickAdsListener.onClickAds(urlAdsBean);
                            dismissPopupAds();
                        }
                    })
            ;

            adsDialog.show();
            // 埋点
            StatisticsManager.getInstance().onEvent("ad_pop_show", adBean.title);
        }

        popupAdsBean.showTime = System.currentTimeMillis();
        aCache.put(getAdsCacheKey(type), popupAdsBean);

    }

    private String getAdsCacheKey(@AdsConstant.PageType int type) {
        switch (type) {
            case AdsConstant.PageType.MAIN:
                return AD_NT_INFO_MAIN_CACHE_KEY;
            case AdsConstant.PageType.GOVERNMENT:
                return AD_NT_INFO_GOVERNMENT_CACHE_KEY;
            case AdsConstant.PageType.LIFE:
                return AD_NT_INFO_LIFE_CACHE_KEY;
            default:
                throw new IllegalArgumentException("not support type");
        }
    }

    public void detach() {
        if (disposables != null){
            disposables.clear();
        }
        if (uiHandler != null){
            uiHandler.removeCallbacksAndMessages(null);
        }

        dismissPopupAds();

    }

    /**
     * 关闭广告弹窗
     */
    public void dismissPopupAds(){
        if (adsDialog != null && adsDialog.isShowing()){
            adsDialog.dismiss();
        }
        adsDialog = null;
    }


    public void clearCache() {
        aCache.put(AD_NT_INFO_MAIN_CACHE_KEY, (Serializable) null);
        aCache.put(AD_NT_INFO_GOVERNMENT_CACHE_KEY, (Serializable) null);
        aCache.put(AD_NT_INFO_LIFE_CACHE_KEY, (Serializable) null);
    }

}
