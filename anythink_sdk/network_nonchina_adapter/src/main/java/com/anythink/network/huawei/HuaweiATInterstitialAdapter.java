/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.huawei;

import android.app.Activity;
import android.content.Context;

import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

import java.util.Map;

public class HuaweiATInterstitialAdapter extends CustomInterstitialAdapter {
    String mAdId;

    InterstitialAd mInterstitialAd;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        if (serverExtras.containsKey("ad_id")) {
            mAdId = (String) serverExtras.get("ad_id");

        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "AdId is empty.");
            }
            return;
        }

        HuaweiATInitManager.getInstance().initSDK(context, serverExtras, new HuaweiATInitManager.InitListener() {
            @Override
            public void onSuccess() {
                startLoadAd(context);
            }
        });
    }

    private void startLoadAd(Context context) {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdId(mAdId);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdClosed() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            public void onAdFailed(int errorCode) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(String.valueOf(errorCode), "");
                }
            }

            public void onAdLeave() {
            }

            public void onAdOpened() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            public void onAdLoaded() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            public void onAdClicked() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            public void onAdImpression() {
            }
        });

        mInterstitialAd.loadAd(new AdParam.Builder().build());
    }

    @Override
    public void destory() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }
    }


    @Override
    public boolean isAdReady() {
        if (mInterstitialAd != null) {
            return mInterstitialAd.isLoaded();
        }
        return false;
    }

    @Override
    public void show(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        }
    }


    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return HuaweiATInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public String getNetworkPlacementId() {
        return mAdId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return HuaweiATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return HuaweiATInitManager.getInstance().getNetworkName();
    }
}
