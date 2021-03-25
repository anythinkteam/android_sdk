/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.appnext;

import android.content.Context;

import com.anythink.core.api.ATInitMediation;
import com.appnext.ads.interstitial.InterstitialActivity;
import com.appnext.banners.BaseBannerView;
import com.appnext.base.Appnext;
import com.appnext.nativeads.NativeAdView;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GoogleSignatureVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppnextATInitManager extends ATInitMediation {

    private static final String TAG = AppnextATInitManager.class.getSimpleName();
    private boolean mIsInit;
    private static AppnextATInitManager sInstance;

    private AppnextATInitManager() {

    }

    public synchronized static AppnextATInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppnextATInitManager();
        }
        return sInstance;
    }

    @Override
    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {
        if (!mIsInit) {
            Appnext.init(context);
            mIsInit = true;
        }
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        Appnext.setParam("consent", String.valueOf(isConsent));
        return true;
    }

    @Override
    public String getNetworkName() {
        return "Appnext";
    }

    @Override
    public String getNetworkSDKClass() {
        return "com.appnext.base.Appnext";
    }

    @Override
    public Map<String, Boolean> getPluginClassStatus() {
        HashMap<String, Boolean> pluginMap = new HashMap<>();
        pluginMap.put("play-services-ads-identifier-*.aar", false);
        pluginMap.put("play-services-basement-*.aar", false);

        pluginMap.put("AppnextAndroidSDKAds.aar", false);
        pluginMap.put("AppnextAndroidSDKBanners.aar", false);
        pluginMap.put("AppnextAndroidSDKNativeads.aar", false);

        Class clazz;
        try {
            clazz = AdvertisingIdClient.class;
            pluginMap.put("play-services-ads-identifier-*.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            clazz = GoogleSignatureVerifier.class;
            pluginMap.put("play-services-basement-*.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            clazz = InterstitialActivity.class;
            pluginMap.put("AppnextAndroidSDKAds.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            clazz = BaseBannerView.class;
            pluginMap.put("AppnextAndroidSDKBanners.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            clazz = NativeAdView.class;
            pluginMap.put("AppnextAndroidSDKNativeads.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return pluginMap;
    }
}
