package com.pasc.lib.displayads.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * Trial run,just for splash ad loading for now
 * Created by zhangxu678 on 2018/7/25.
 */
public class ImageLoaderUtils {
  public interface ImageLoadListener<T, K> {

    /**
     * 图片加载成功回调
     *
     * @param source 图片url 或资源id 或drawable
     * @param view 目标载体，不传则为空
     */
    void onLoadingComplete(T source, K view);

    void onLoadingError();
  }

  private static ImageLoaderUtils mInstance;
  private RequestOptions requestOptions;

  private ImageLoaderUtils() {
    requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
  }

  public static ImageLoaderUtils getInstance() {
    if (mInstance == null) {
      synchronized (ImageLoaderUtils.class) {
        if (mInstance == null) {
          mInstance = new ImageLoaderUtils();
          return mInstance;
        }
      }
    }
    return mInstance;
  }

  private RequestOptions resolveImageType(@NonNull ImageView imageView) {
    if (imageView.getScaleType() != null) {
      switch (imageView.getScaleType()) {
        case CENTER_CROP:
          return requestOptions.centerCrop();
        case FIT_CENTER:
        case FIT_START:
        case FIT_END:
          return requestOptions.fitCenter();
        default:
      }
    }
    return requestOptions;
  }

  public void loadImage(String url, int placeholder, ImageView imageView) {
    loadImage(url, placeholder, imageView, null);
  }

  public Target loadImage(String url, int placeholder, final ImageView imageView,
                          final ImageLoadListener imageLoadListener) {

    if (imageView == null) {
      throw new IllegalArgumentException("ImageView should not be null");
    }
    return Glide.with(imageView.getContext())
        .load(url)
        .apply(resolveImageType(imageView).placeholder(placeholder))
        .into(new SimpleTarget<Drawable>() {
          @Override
          public void onLoadStarted(@Nullable Drawable placeholder) {

          }

          @Override
          public void onLoadFailed(@Nullable Drawable errorDrawable) {
            //PascLog.e("glide error");
            if (!assetContextValid(imageView.getContext())) {
              return;
            }
            if (imageLoadListener != null) {
              imageLoadListener.onLoadingError();
            }
          }

          @Override
          public void onResourceReady(@NonNull Drawable resource,
                                      @Nullable Transition<? super Drawable> transition) {
            //PascLog.e("glide complete");
            if (!assetContextValid(imageView.getContext())) {
              return;
            }
            if (imageLoadListener != null) {
              imageLoadListener.onLoadingComplete(resource, imageView);
            } else {
              imageView.setImageDrawable(resource);
            }
          }
        });
  }

  private boolean assetContextValid(Context context) {
    boolean ifContextValid = true;
    if (context instanceof Activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        ifContextValid = !((Activity) context).isDestroyed();
      } else {
        ifContextValid = !((Activity) context).isFinishing();
      }
    }
    return ifContextValid;
  }

  public void cacheImage(String url, Application application) {
    Glide.with(application).downloadOnly().load(url);
  }

  public void cancelRequest(Context context, Target target) {
    try {
      Glide.with(context).clear(target);
    }catch (Exception e){

    }
  }
}
