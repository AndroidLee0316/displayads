package com.pasc.lib.displayads.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.ImageView;
import com.bumptech.glide.request.target.Target;
import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.config.AdsConstant;
import com.pasc.lib.displayads.config.DisplayAdsManager;
import com.pasc.lib.displayads.model.SplashAdsModelImpl;
import com.pasc.lib.displayads.util.AdsAppUtil;
import com.pasc.lib.displayads.util.Cache.ACache;
import com.pasc.lib.displayads.util.ImageLoaderUtils;
import com.pasc.lib.displayads.view.SplashAdsView;
import com.pasc.lib.imageloader.PascCallback;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.statistics.StatisticsManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by qinguohuai on 2018/12/28.
 */
public class SplashPresenterImpl implements SplashPresenter {
    public final static int COUNT_DOWN = 3;
    private static final int MAX_CACHE_SIZE = 1000 * 1000 * 50;
    private ACache aCache;
    private static final String SPLASH_ADS_INFO = "splash_ads_info";
    private Activity activity;
    private List<AdsBean> adsListBean;
    private String lastAdId;

    private AdsBean fullAdsBean;

    private boolean loadSuccess;
    private AdsBean cacheAdsBean;
    private Target target;

    private Disposable disposable;
    private CompositeDisposable compositeDisposable;
    private SplashAdsView splashAdsView;
    private SplashAdsModelImpl splashAdsModel;
    private long mStartTimeMillis;
    private long mDelayMaxMillis = 1500; //单位毫秒

    public SplashPresenterImpl(SplashAdsView splashAdsView, Activity activity) {
        this.splashAdsView = splashAdsView;
        splashAdsModel = new SplashAdsModelImpl();

        this.activity = activity;

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void countDown() {
        compositeDisposable.clear();
        compositeDisposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .take(COUNT_DOWN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (splashAdsView != null) {
                            splashAdsView.showCountdown(COUNT_DOWN - aLong);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (splashAdsView != null) {
                            splashAdsView.countDownFinish();
                        }

                    }
                }));
    }

    @Override
    public void loadSplashAd(final ImageView adsImageView, long delayMaxMillis) {
        mDelayMaxMillis = delayMaxMillis;
        mStartTimeMillis = System.currentTimeMillis();
        if (splashAdsView == null || activity == null) {
            return;
        }

        // 开始倒计时
        countDown();

        if(!DisplayAdsManager.getInstance().isSplashEnable()){
            return;
        }

        // 读取广告换乘
        aCache = ACache.get(new File(activity.getFilesDir(), "smt/ad"), MAX_CACHE_SIZE,
                Integer.MAX_VALUE);

        if (aCache != null) {
            cacheAdsBean = (AdsBean) aCache.getAsObject(SPLASH_ADS_INFO);
        }

        if (cacheAdsBean != null) {
            lastAdId = cacheAdsBean.id;
        }

        // 无网络处理
        if (!AdsAppUtil.isNetworkAvailable(activity)) {
            // 无网络直接显示缓存数据
            showFullAds(adsImageView, cacheAdsBean, true);
            return;
        }

        // 网络可用从服务端获取最新数据
        String appVersionName = AdsAppUtil.getAppVersionName(activity);

        disposable = splashAdsModel.getSplshAdsInfo(
                obtainPicScale(), appVersionName
        ).onErrorReturn(new Function<Throwable, List<AdsBean>>() {
            @Override
            public List<AdsBean> apply(Throwable throwable) throws Exception {
                return Collections.emptyList();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<List<AdsBean>>() {
                    @Override
                    public boolean test(List<AdsBean> adsBean) throws Exception {
                        return adsBean != null
                                && adsBean.size() != 0;
                    }
                })
                .map(new Function<List<AdsBean>, Observable<AdsBean>>() {
                    @Override
                    public Observable<AdsBean> apply(List<AdsBean> adBeans) throws Exception {
                        adsListBean = adBeans;
                        return Observable.fromIterable(adBeans);
                    }
                })
                .subscribe(new Consumer<Observable<AdsBean>>() {
                    @Override
                    public void accept(Observable<AdsBean> adBeanObservable) throws Exception {
                        adBeanObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<AdsBean>() {
                                               @Override
                                               public void accept(final AdsBean adBean) throws Exception {
                                                   if (adBean == null) {
                                                       return;
                                                   }

                                                   showFullAds(adsImageView, adBean, false);

                                                   disposable.dispose();
                                               }
                                           }
                                );
                    }
                });

        compositeDisposable.add(disposable);
    }

    /**
     * 显示Ads
     */
    private void showFullAds(final ImageView targetImgView, final AdsBean bean, boolean isNetUnAvailable) {

        if (bean == null) {
            return;
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime < bean.showStartTime || currentTime > bean.showEndTime) {
            return;
        }

        if (ifShouldLoad(bean)) {
            //延时加载广告图片
            long delayMillis = 0;
            if (currentTime - mStartTimeMillis < mDelayMaxMillis) {
                delayMillis = mDelayMaxMillis - (currentTime - mStartTimeMillis);
            }
            targetImgView.postDelayed(new Runnable() {
                @Override public void run() {
                    PascImageLoader.getInstance().loadImageUrl(bean.picUrl, targetImgView,
                        PascImageLoader.SCALE_DEFAULT, new PascCallback() {
                            @Override
                            public void onSuccess() {
                                // if (splashAdsView != null) {
                                // 目前在Presenter内实现，后续扩展使用Bitmap自行显示
                                // splashAdsView.showAds(null);
                                // }

                                // 缓存广告在本机的显示时间
                                bean.showTime = currentTime;

                                setLoadSuccess(true, bean);
                                // 图片加载后重新开始倒计时
                                countDown();

                            }

                            @Override
                            public void onError() {
                                // 图片加载失败时
                                //StatisticsManager.getInstance().onEvent("ad_full_show", "图片加载失败");
                            }
                        }, 500);
                }
            }, delayMillis);

            SplashPresenterImpl.this.fullAdsBean = bean;
            // 埋点
            StatisticsManager.getInstance().onEvent("ad_full_show", bean.title);

        }else {
            // 保存广告数据
            if (cacheAdsBean != null && cacheAdsBean.id != null && cacheAdsBean.id.equals(bean.id)) {
                bean.showTime = cacheAdsBean.showTime;
            }
            saveSplashAds(bean);
        }
    }

    /**
     * 是否显示
     *
     * @param adsBean
     * @return
     */
    private boolean ifShouldLoad(AdsBean adsBean) {

        if (!adsBean.isEnable()) {
            return false;
        }
        if (adsBean.isSplashEnd()) {
            return false;
        }
        if (AdsConstant.getFrequency(adsBean.frequency) == AdsConstant.Frequency.ONE_TIME && (lastAdId != null
                && lastAdId.equals(adsBean.id))) {
            return false;
        }

        if (AdsConstant.getFrequency(adsBean.frequency) == AdsConstant.Frequency.EVERY_TIME) {
            return true;
        }
        if (cacheAdsBean != null && AdsConstant.getFrequency(adsBean.frequency) == AdsConstant.Frequency.ONE_DAY_ONE_TIME) {
            return !DateUtils.isToday(cacheAdsBean.showTime);
        }

        return true;
    }

    /**
     * 在SplshPresenter内部不处理点击事件
     */
    @Override
    public void handleAdsClick() {
        if (!canJumpToDetails()) {
            return;
        }

        // 自行处理点击事件的跳转
        if (splashAdsView != null) {
            splashAdsView.handleClick(fullAdsBean);
        }

        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }

    }

    private void setLoadSuccess(boolean loadSuccess, AdsBean saveAdsBean) {
        this.loadSuccess = loadSuccess;

        saveSplashAds(saveAdsBean);
        cacheAdsPic(saveAdsBean);
    }

    private void saveSplashAds(AdsBean saveAdsBean){
        if (saveAdsBean != null){
            if (aCache != null){
                aCache.put(SPLASH_ADS_INFO, saveAdsBean);
            }
        }
    }

    public boolean canJumpToDetails() {
        return loadSuccess && fullAdsBean != null && !TextUtils.isEmpty(fullAdsBean.picSkipUrl);
    }

    private void cacheAdsPic(AdsBean cacheBean) {
        if (!loadSuccess || cacheBean == null) {
            return;
        }

        PascImageLoader.getInstance().cacheImage(cacheBean.picUrl);

    }

//    public boolean ifPicLoadError() {
//        return adBean != null && !TextUtils.isEmpty(adBean.picUrl) && !loadSuccess;
//    }


    private String obtainPicScale() {
        boolean ifShowLongPic = false;
        float density = 0.0f;
        if (activity != null) {
            ifShowLongPic =
                    activity.getResources().getDisplayMetrics().heightPixels / activity.getResources()
                            .getDisplayMetrics().widthPixels >= 18 / 9;
            density = activity.getResources().getDisplayMetrics().density;
        }

        if (ifShowLongPic) {
            return "3X-2";
        }
        if (density < 2.5f) {
            return "2X";
        }
        return "3X-1";
    }

    @Override
    public void detach() {
        if (target != null) {
            ImageLoaderUtils.getInstance().cancelRequest(activity, target);
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }


}
