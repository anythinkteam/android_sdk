/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.rewardvideo.api;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.core.api.ErrorCode;
import com.anythink.core.common.PlacementAdManager;
import com.anythink.core.common.base.Const;
import com.anythink.core.common.base.SDKContext;
import com.anythink.core.common.utils.CommonSDKUtil;
import com.anythink.core.strategy.PlaceStrategy;
import com.anythink.core.strategy.PlaceStrategyManager;
import com.anythink.rewardvideo.bussiness.AdLoadManager;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ATRewardVideoAd {
    final String TAG = getClass().getSimpleName();
    String mPlacementId;
    ATRewardVideoListener mListener;
    AdLoadManager mAdLoadManager;

    Context mContext;
    WeakReference<Activity> mActivityWef;

    private ATRewardVideoExListener mInterListener = new ATRewardVideoExListener() {
        @Override
        public void onDeeplinkCallback(final ATAdInfo adInfo, final boolean isSuccess) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null && mListener instanceof ATRewardVideoExListener) {
                        ((ATRewardVideoExListener) mListener).onDeeplinkCallback(adInfo, isSuccess);
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoAdLoaded() {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdLoaded();
                    }
                }
            });

        }

        @Override
        public void onRewardedVideoAdFailed(final AdError errorCode) {
            if (mAdLoadManager != null) {
                mAdLoadManager.setLoadFail(errorCode);
            }
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdFailed(errorCode);
                    }
                }
            });

        }

        @Override
        public void onRewardedVideoAdPlayStart(final ATAdInfo entity) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdPlayStart(entity);
                    }
                }
            });

        }

        @Override
        public void onRewardedVideoAdPlayEnd(final ATAdInfo entity) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdPlayEnd(entity);
                    }
                }
            });

        }

        @Override
        public void onRewardedVideoAdPlayFailed(final AdError errorCode, final ATAdInfo entity) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdPlayFailed(errorCode, entity);
                    }
                }
            });

        }

        @Override
        public void onRewardedVideoAdClosed(final ATAdInfo entity) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdClosed(entity);
                    }
                }
            });

            if (isNeedAutoLoadAfterClose()) {
                load(getRequestContext(), true);
            }
        }

        @Override
        public void onRewardedVideoAdPlayClicked(final ATAdInfo entity) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onRewardedVideoAdPlayClicked(entity);
                    }
                }
            });

        }

        @Override
        public void onReward(final ATAdInfo adInfo) {
            SDKContext.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onReward(adInfo);
                    }
                }
            });
        }
    };

    public ATRewardVideoAd(Context context, String placementId) {
        mPlacementId = placementId;
        mContext = context.getApplicationContext();
        if (context instanceof Activity) {
            mActivityWef = new WeakReference<>((Activity) context);
        }
        mAdLoadManager = AdLoadManager.getInstance(context, placementId);
    }

    public void load() {
        load(getRequestContext(), false);
    }

    public void load(Context context) {
        load(context != null ? context : getRequestContext(), false);
    }

    private void load(Context context, final boolean isAutoRefresh) {
        ATSDK.apiLog(mPlacementId, Const.LOGKEY.API_REWARD, Const.LOGKEY.API_LOAD, Const.LOGKEY.START, "");
        mAdLoadManager.startLoadAd(context, isAutoRefresh, mInterListener);
    }

    private Context getRequestContext() {
        Activity activity = null;
        if (mActivityWef != null) {
            activity = mActivityWef.get();
        }
        return activity != null ? activity : mContext;
    }

    private boolean isNeedAutoLoadAfterClose() {
        PlaceStrategy placeStrategy = PlaceStrategyManager.getInstance(SDKContext.getInstance().getContext()).getPlaceStrategyByAppIdAndPlaceId(mPlacementId);
        if (placeStrategy != null) {
            return placeStrategy.getAutoRefresh() == 1 && !mAdLoadManager.isLoading();
        }
        return false;
    }

    public void setLocalExtra(Map<String, Object> map) {
        PlacementAdManager.getInstance().putPlacementLocalSettingMap(mPlacementId, map);
    }

    public void setAdListener(ATRewardVideoListener listener) {
        mListener = listener;
    }

    public boolean isAdReady() {
        if (SDKContext.getInstance().getContext() == null
                || TextUtils.isEmpty(SDKContext.getInstance().getAppId())
                || TextUtils.isEmpty(SDKContext.getInstance().getAppKey())) {
            Log.e(TAG, "SDK init error!");
            return false;
        }

        boolean isAdReady = mAdLoadManager.isAdReady(mContext);
        ATSDK.apiLog(mPlacementId, Const.LOGKEY.API_REWARD, Const.LOGKEY.API_ISREADY, String.valueOf(isAdReady), "");
        return isAdReady;
    }

    public ATAdStatusInfo checkAdStatus() {
        if (SDKContext.getInstance().getContext() == null
                || TextUtils.isEmpty(SDKContext.getInstance().getAppId())
                || TextUtils.isEmpty(SDKContext.getInstance().getAppKey())) {
            Log.e(TAG, "SDK init error!");
            return new ATAdStatusInfo(false, false, null);
        }

        ATAdStatusInfo adStatusInfo = mAdLoadManager.checkAdStatus(mContext);
        ATSDK.apiLog(mPlacementId, Const.LOGKEY.API_REWARD, Const.LOGKEY.API_AD_STATUS, adStatusInfo.toString(), "");

        return adStatusInfo;
    }

    public void show(Activity activity, String scenario) {
        String realScenario = "";
        if (CommonSDKUtil.isVailScenario(scenario)) {
            realScenario = scenario;
        }
        controlShow(activity, realScenario);
    }

    public void show(Activity activity) {
        controlShow(activity, "");
    }

    private void controlShow(Activity activity, String scenario) {

        ATSDK.apiLog(mPlacementId, Const.LOGKEY.API_REWARD, Const.LOGKEY.API_SHOW, Const.LOGKEY.START, "");
        if (SDKContext.getInstance().getContext() == null
                || TextUtils.isEmpty(SDKContext.getInstance().getAppId())
                || TextUtils.isEmpty(SDKContext.getInstance().getAppKey())) {
            AdError error = ErrorCode.getErrorCode(ErrorCode.exception, "", "sdk init error");
            if (mListener != null) {
                mListener.onRewardedVideoAdPlayFailed(error, ATAdInfo.fromAdapter(null));
            }
            Log.e(TAG, "SDK init error!");
            return;
        }

        Activity showActivity = activity;
        if (showActivity == null && mContext instanceof Activity) {
            showActivity = (Activity) mContext;
        }

        if (showActivity == null) {
            Log.e(TAG, "RewardedVideo Show Activity is null.");
        }

        mAdLoadManager.show(showActivity, scenario, mInterListener);
    }

}
