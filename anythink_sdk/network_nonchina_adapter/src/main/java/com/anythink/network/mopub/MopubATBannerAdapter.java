/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.mopub;

import android.content.Context;
import android.view.View;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.Map;

/**
 * Created by Z on 2018/6/27.
 */

public class MopubATBannerAdapter extends CustomBannerAdapter {
    private final String TAG = MopubATBannerAdapter.class.getSimpleName();

    String adUnitId;
    MoPubView mBannerView;
    int mRefreshTime;

    private void startLoad(Context context) {
        MoPubView moPubView = new MoPubView(context);
        moPubView.setAdUnitId(adUnitId);

        if (mRefreshTime > 0) {
            moPubView.setAutorefreshEnabled(true);
        } else {
            moPubView.setAutorefreshEnabled(false);
        }

        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                mBannerView = banner;
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode.getIntCode() + "", errorCode.toString());
                }
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClose();
                }
            }
        });
        moPubView.loadAd();
    }

    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public void destory() {
        if (mBannerView != null) {
            mBannerView.setBannerAdListener(null);
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    @Override
    public void loadCustomNetworkAd(final Context activity, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        if (serverExtras.containsKey("unitid")) {
            adUnitId = (String) serverExtras.get("unitid");

        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "unitid is empty!");
            }
            return;
        }

        mRefreshTime = 0;
        try {
            if (serverExtras.containsKey("nw_rft")) {
                mRefreshTime = Integer.valueOf((String) serverExtras.get("nw_rft"));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        MopubATInitManager.getInstance().initSDK(activity.getApplicationContext(), serverExtras, new MopubATInitManager.InitListener() {
            @Override
            public void initSuccess() {
                try {
                    startLoad(activity);
                } catch (Throwable e) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public String getNetworkSDKVersion() {
        return MopubATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return MopubATInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return MopubATInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public String getNetworkPlacementId() {
        return adUnitId;
    }

    @Override
    public boolean supportImpressionCallback() {
        return false;
    }
}