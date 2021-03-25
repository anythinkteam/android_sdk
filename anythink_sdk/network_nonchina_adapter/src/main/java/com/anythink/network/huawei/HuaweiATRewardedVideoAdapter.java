/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.huawei;

import android.app.Activity;
import android.content.Context;

import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;
import com.huawei.hms.ads.reward.RewardVerifyConfig;

import java.util.Map;

public class HuaweiATRewardedVideoAdapter extends CustomRewardVideoAdapter {
    String mAdId;
    RewardAd mRewardAd;

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
        mRewardAd = new RewardAd(context, mAdId);

        RewardAdLoadListener listener = new RewardAdLoadListener() {
            @Override
            public void onRewardedLoaded() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(String.valueOf(errorCode), "");
                }
            }
        };
        RewardVerifyConfig config = new RewardVerifyConfig.Builder().setData(mUserData)
                .setUserId(mUserId)
                .build();
        mRewardAd.setRewardVerifyConfig(config);
        mRewardAd.loadAd(new AdParam.Builder().build(), listener);
    }

    @Override
    public void destory() {
        try {
            mRewardAd = null;
            if (mRewardAd != null) {
                mRewardAd.setRewardAdListener(null);
                mRewardAd.destroy(null);
                mRewardAd = null;
            }
        } catch (Exception e) {
        }
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

    @Override
    public boolean isAdReady() {
        if (mRewardAd != null) {
            return mRewardAd.isLoaded();
        }
        return false;
    }

    boolean isPlayComplete = false;

    @Override
    public void show(Activity activity) {
        //Full Listener
//        mRewardAd.setRewardAdListener(new RewardAdListener() {
//            @Override
//            public void onRewarded(Reward reward) {
//                if (mImpressionListener != null) {
//                    mImpressionListener.onReward();
//                }
//            }
//
//            @Override
//            public void onRewardAdClosed() {
//
//            }
//
//            @Override
//            public void onRewardAdFailedToLoad(int i) {
//
//            }
//
//            @Override
//            public void onRewardAdLeftApp() {
//                if (mImpressionListener != null) {
//                    mImpressionListener.onRewardedVideoAdPlayClicked();
//                }
//            }
//
//            @Override
//            public void onRewardAdLoaded() {
//
//            }
//
//            @Override
//            public void onRewardAdOpened() {
//
//            }
//
//            @Override
//            public void onRewardAdCompleted() {
//                if (mImpressionListener != null) {
//                    mImpressionListener.onRewardedVideoAdPlayEnd();
//                }
//            }
//
//            @Override
//            public void onRewardAdStarted() {
//
//            }
//        });

        //Show status
        mRewardAd.show(activity, new RewardAdStatusListener() {
            @Override
            public void onRewardAdOpened() {
                isPlayComplete = false;
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayStart();
                }
            }

            @Override
            public void onRewardAdFailedToShow(int errorCode) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayFailed(String.valueOf(errorCode), "");
                }
            }

            @Override
            public void onRewardAdClosed() {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdClosed();
                }
            }

            @Override
            public void onRewarded(Reward reward) {
                if (!isPlayComplete) {
                    isPlayComplete = true;
                    if (mImpressionListener != null) {
                        mImpressionListener.onRewardedVideoAdPlayEnd();
                    }
                }

                if (mImpressionListener != null) {
                    mImpressionListener.onReward();
                }
            }
        });
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return HuaweiATInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }
}
