/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.fyber;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.core.api.ATInitMediation;
import com.anythink.core.common.base.Const;
import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.fyber.inneractive.sdk.mraid.IAMraidKit;
import com.fyber.inneractive.sdk.video.IAVideoKit;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GoogleSignatureVerifier;

import java.util.HashMap;
import java.util.Map;

public class FyberATInitManager extends ATInitMediation {


    private String mAppId;
    private static FyberATInitManager sInstance;

    private FyberATInitManager() {

    }

    public synchronized static FyberATInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new FyberATInitManager();
        }
        return sInstance;
    }


    @Override
    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {

        String app_id = (String) serviceExtras.get("app_id");

        if (TextUtils.isEmpty(mAppId) || !TextUtils.equals(mAppId, app_id)) {

            if (!TextUtils.equals(mAppId, app_id) && InneractiveAdManager.wasInitialized()) {
                InneractiveAdManager.destroy();
            }

            try {
                boolean ccpaSwitch = (boolean) serviceExtras.get(Const.NETWORK_REQUEST_PARAMS_KEY.APP_CCPA_SWITCH_KEY);
                if (ccpaSwitch) {
                    InneractiveAdManager.setUSPrivacyString("1YNN");
                }
            } catch (Throwable e) {

            }
            InneractiveAdManager.initialize(context.getApplicationContext(), app_id);

            mAppId = app_id;
        }

    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        InneractiveAdManager.setGdprConsent(isConsent);
        return true;
    }

    @Override
    public String getNetworkSDKClass() {
        return "com.fyber.inneractive.sdk.external.InneractiveAdManager";
    }

    @Override
    public Map<String, Boolean> getPluginClassStatus() {
        HashMap<String, Boolean> pluginMap = new HashMap<>();
        pluginMap.put("ia-mraid-kit-release-*.aar", false);
        pluginMap.put("ia-video-kit-release-*.aar", false);
        pluginMap.put("play-services-ads-identifier-*.aar", false);
        pluginMap.put("play-services-basement-*.aar", false);
        Class clazz;
        try {
            clazz = IAMraidKit.class;
            pluginMap.put("ia-mraid-kit-release-*.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            clazz = IAVideoKit.class;
            pluginMap.put("ia-video-kit-release-*.aar", true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

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

        return pluginMap;
    }

    @Override
    public String getNetworkName() {
        return "Fyber";
    }

    @Override
    public String getNetworkVersion() {
        return FyberATConst.getNetworkVersion();
    }
}
