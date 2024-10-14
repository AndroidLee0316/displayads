package com.pasc.lib.ads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pasc.lib.base.util.SPUtils;

public class WelcomeGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将activity设置为全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_guide);

        initUI();
    }

    private void initUI() {
        findViewById(R.id.welcom_bg_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMainPage();
            }
        });
    }

    private void goMainPage() {
        SPUtils.getInstance().setParam(SPUtils.SP_FILE_NAME_2, SPUtils.FIRST_OPEN, false);
        enterMainActivity();
    }

    private void enterMainActivity() {
        Intent intent = new Intent(WelcomeGuideActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SPUtils.getInstance().setParam(SPUtils.SP_FILE_NAME_2, SPUtils.FIRST_OPEN, false);
//        StatisticsManager.getInstance().onPause(this);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
