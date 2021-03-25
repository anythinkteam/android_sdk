/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.ksyun;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.anythink.core.api.ATInitMediation;
import com.anythink.network.ks.KSATConst;
import com.ksc.ad.sdk.IKsyunAdInitResultListener;
import com.ksc.ad.sdk.KsyunAdSdk;
import com.ksc.ad.sdk.KsyunAdSdkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KsyunATInitManager extends ATInitMediation {

    private static final String TAG = KsyunATInitManager.class.getSimpleName();
    private String mMediaId;
    private static KsyunATInitManager sIntance;

    private KsyunATInitManager() {

    }

    public synchronized static KsyunATInitManager getInstance() {
        if (sIntance == null) {
            sIntance = new KsyunATInitManager();
        }
        return sIntance;
    }

    @Override
    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {
        if (!(context instanceof Activity)) {
            return;
        }

        initSDK(((Activity) context), serviceExtras, null);
    }

    public synchronized void initSDK(Activity activity, Map<String, Object> serviceExtras, final IKsyunAdInitResultListener listener) {
        final String media_id = serviceExtras.get("media_id").toString();

        if (!TextUtils.isEmpty(media_id)) {
            if (TextUtils.isEmpty(mMediaId) || !TextUtils.equals(mMediaId, media_id)) {

                KsyunAdSdkConfig config = new KsyunAdSdkConfig();
                config.setSdkEnvironment(KsyunAdSdkConfig.RELEASE_ENV);
                config.setEnabeSdkRequestPermission(true);
                KsyunAdSdk.getInstance().resetSdk(activity);
                KsyunAdSdk.getInstance().init(activity, media_id, config, new IKsyunAdInitResultListener() {
                    @Override
                    public void onSuccess(Map<String, String> map) {
                        mMediaId = media_id;
                        if (listener != null) {
                            listener.onSuccess(map);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        if (listener != null) {
                            listener.onFailure(i, s);
                        }
                    }
                });
            } else {
                if (listener != null) {
                    listener.onSuccess(null);
                }
            }
        }
    }

    @Override
    public String getNetworkName() {
        return "Ksyun";
    }

    @Override
    public String getNetworkVersion() {
        return KsyunATConst.getNetworkVersion();
    }

    @Override
    public String getNetworkSDKClass() {
        return "com.ksc.ad.sdk.KsyunAdSdk";
    }

    @Override
    public List getProviderStatus() {
        ArrayList<String> list = new ArrayList<>();
        list.add("com.ksc.ad.sdk.util.KsyunFileProvider");
        return list;
    }
}
