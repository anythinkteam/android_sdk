/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.core.common.utils.CommonUtil;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventExListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static final String TAG = NativeAdActivity.class.getSimpleName();

    String placementIds[] = new String[]{
            DemoApplicaion.mPlacementId_native_all
            , DemoApplicaion.mPlacementId_native_facebook
            , DemoApplicaion.mPlacementId_native_banner_facebook
            , DemoApplicaion.mPlacementId_native_admob
            , DemoApplicaion.mPlacementId_native_inmobi
            , DemoApplicaion.mPlacementId_native_applovin
            , DemoApplicaion.mPlacementId_native_mintegral
            , DemoApplicaion.mPLacementId_native_automatic_rending_mintegral
            , DemoApplicaion.mPlacementId_native_mopub
            , DemoApplicaion.mPlacementId_native_appnext
            , DemoApplicaion.mPlacementId_native_nend
            , DemoApplicaion.mPlacementId_native_googleAdManager
            , DemoApplicaion.mPlacementId_native_myoffer
            , DemoApplicaion.mPlacementId_native_huawei
            , DemoApplicaion.mPlacementId_native_adx
            , DemoApplicaion.mPlacementId_native_online
            , DemoApplicaion.mPlacementId_native_toutiao
            , DemoApplicaion.mPlacementId_native_toutiao_drawer

    };

    String unitGroupName[] = new String[]{
            "All network",
            "Facebook",
            "Faceboon native banner",
            "Admob",
            "Inmobi",
            "Applovin",
            "Mintegral",
            "Mintegral auto-rending",
            "Mopub",
            "Appnext",
            "Nend",
            "Google Ad Manager",
            "MyOffer",
            "Huawei",
            "Adx",
            "OnlineApi",
            "Pangle Feed",
            "Pangle Draw"
    };

    ATNative atNatives[] = new ATNative[placementIds.length];
    ATNativeAdView anyThinkNativeAdView;
    NativeAd mNativeAd;

    ImageView mCloseView;

    int mCurrentSelectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_native);

        Spinner spinner = (Spinner) findViewById(R.id.native_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                NativeAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(NativeAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initCloseView();

        int padding = dip2px(10);
        final int containerHeight = dip2px(340);
        final int adViewWidth = getResources().getDisplayMetrics().widthPixels - 2 * padding;
        final int adViewHeight = containerHeight - 2 * padding;

        final NativeDemoRender anyThinkRender = new NativeDemoRender(this);
        anyThinkRender.setCloseView(mCloseView);

        for (int i = 0; i < placementIds.length; i++) {
            atNatives[i] = new ATNative(this, placementIds[i], new ATNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded() {
                    Log.i(TAG, "onNativeAdLoaded");
                    Toast.makeText(NativeAdActivity.this, "load success..."
                            , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Log.i(TAG, "onNativeAdLoadFail, " + adError.getFullErrorInfo());
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.getFullErrorInfo(), Toast.LENGTH_LONG).show();

                }
            });

            if (anyThinkNativeAdView == null) {
                anyThinkNativeAdView = new ATNativeAdView(this);
            }
        }


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (anyThinkNativeAdView != null) {
                    anyThinkNativeAdView.removeAllViews();

                    if (anyThinkNativeAdView.getParent() == null) {
                        ((FrameLayout) findViewById(R.id.ad_container)).addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, containerHeight));
                    }
                }

                Map<String, Object> localMap = new HashMap<>();

                // since v5.6.4
                localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
                localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);

                atNatives[mCurrentSelectIndex].setLocalExtra(localMap);

                atNatives[mCurrentSelectIndex].makeAdRequest();
            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = atNatives[mCurrentSelectIndex].getNativeAd();
                if (nativeAd != null) {

                    if (anyThinkNativeAdView != null) {
                        anyThinkNativeAdView.removeAllViews();

                        if (anyThinkNativeAdView.getParent() == null) {
                            ((FrameLayout) findViewById(R.id.ad_container)).addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, containerHeight));
                        }
                    }

                    if (mNativeAd != null) {
                        mNativeAd.destory();
                    }
                    mNativeAd = nativeAd;
                    mNativeAd.setNativeEventListener(new ATNativeEventExListener() {
                        @Override
                        public void onDeeplinkCallback(ATNativeAdView view, ATAdInfo adInfo, boolean isSuccess) {
                            Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                        }

                        @Override
                        public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdImpressed:\n" + entity.toString());
                        }

                        @Override
                        public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdClicked:\n" + entity.toString());
                        }

                        @Override
                        public void onAdVideoStart(ATNativeAdView view) {
                            Log.i(TAG, "native ad onAdVideoStart");
                        }

                        @Override
                        public void onAdVideoEnd(ATNativeAdView view) {
                            Log.i(TAG, "native ad onAdVideoEnd");
                        }

                        @Override
                        public void onAdVideoProgress(ATNativeAdView view, int progress) {
                            Log.i(TAG, "native ad onAdVideoProgress:" + progress);
                        }
                    });
                    mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                        @Override
                        public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdCloseButtonClick");
                            if (view.getParent() != null) {
                                ((ViewGroup) view.getParent()).removeView(view);
                                view.removeAllViews();
                            }
                        }
                    });
                    try {
                        mNativeAd.renderAdView(anyThinkNativeAdView, anyThinkRender);
                    } catch (Exception e) {

                    }

                    anyThinkNativeAdView.addView(mCloseView);

                    anyThinkNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(anyThinkNativeAdView, anyThinkRender.getClickView(), null);
                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!", Toast.LENGTH_LONG).show();

                }

            }
        });
        anyThinkNativeAdView.setPadding(padding, padding, padding, padding);

    }

    private void initCloseView() {
        if (mCloseView == null) {
            mCloseView = new ImageView(this);
            mCloseView.setImageResource(R.drawable.ad_close);

            int padding = CommonUtil.dip2px(this, 5);
            mCloseView.setPadding(padding, padding, padding, padding);

            int size = CommonUtil.dip2px(this, 30);
            int margin = CommonUtil.dip2px(this, 2);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
            layoutParams.topMargin = margin;
            layoutParams.rightMargin = margin;
            layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

            mCloseView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
    }

    @Override
    protected void onPause() {
        if (mNativeAd != null) {
            mNativeAd.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mNativeAd != null) {
            mNativeAd.onResume();
        }
        super.onResume();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
