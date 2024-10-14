package com.pasc.lib.ads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.pasc.lib.base.util.SPUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.displayads.bean.AdsBean;
import com.pasc.lib.displayads.presenter.SplashPresenter;
import com.pasc.lib.displayads.presenter.SplashPresenterImpl;
import com.pasc.lib.displayads.view.SplashAdsView;
import com.squareup.phrase.Phrase;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, SplashAdsView{

    private boolean isFirstOpen = false;
    private SplashPresenter splashPresenter;
    private Handler handler;
    private Button skipBtn;
    private ImageView bgImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        skipBtn = (Button) findViewById(R.id.splash_skip_btn);
        bgImg = (ImageView) findViewById(R.id.splash_bg_img);
        skipBtn.setOnClickListener(this);
        bgImg.setOnClickListener(this);

        initData();
    }

    private void initData(){

        isFirstOpen = (boolean) SPUtils.getInstance().getParam(SPUtils.SP_FILE_NAME_2, SPUtils.FIRST_OPEN, true);

        if (isFirstOpen){
            goWelcomeGuide();
            return;
        }

        handler = new Handler();
        splashPresenter = new SplashPresenterImpl(this, this);

        getWindow().getDecorView().post(new Runnable() {
            @Override public void run() {
                handler.post(new Runnable() {
                    @Override public void run() {
                        doSomeThing();
                    }
                });
            }
        });

        splashPresenter.loadSplashAd(bgImg,10L);

    }

    private void doSomeThing() {
        // Add something you need

    }

    /**
     * 首次启动进入引导页
     */
    private void goWelcomeGuide(){
        Intent intent = new Intent(SplashActivity.this, WelcomeGuideActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 首次启动进入引导页
     */
    private void goToMainPage(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view != null){
            if (view.getId() == R.id.splash_skip_btn){

                onClickSkip();

            }else if (view.getId() == R.id.splash_bg_img){
                // 点击广告
                splashPresenter.handleAdsClick();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // 不能退出
        goToMainPage();
    }

    @Override
    public void showCountdown(long count) {
        if (skipBtn != null) {
            skipBtn.setVisibility(View.VISIBLE);
            skipBtn.setText(Phrase.from(this, R.string.ads_splash_skip_count_down)
                    .put("count", String.valueOf(count))
                    .format());
        }
    }

    @Override
    public void countDownFinish() {
        // go where next
        goToMainPage();
    }

    @Override
    public void handleClick(AdsBean adsBean) {
        // 支持自行处理点击事件
        Log.e("splash handle click", "");
        ToastUtils.toastMsg("do click by myself");

        // go where next
        goToMainPage();
    }

    /**
     * 点击弹屏页右上角跳过
     */
    @Override
    public void onClickSkip() {
        // go where next
        goToMainPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        if (splashPresenter != null){
            splashPresenter.detach();
        }
    }

}
