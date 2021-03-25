/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.mintegral;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.mbridge.msdk.mbbid.out.BidManager;
import com.mbridge.msdk.out.CustomInfoManager;
import com.mbridge.msdk.out.MBBidRewardVideoHandler;
import com.mbridge.msdk.out.MBRewardVideoHandler;
import com.mbridge.msdk.out.RewardVideoListener;

import java.util.Map;

public class MintegralATRewardedVideoAdapter extends CustomRewardVideoAdapter {
    private final String TAG = MintegralATRewardedVideoAdapter.class.getSimpleName();

    MBRewardVideoHandler mMvRewardVideoHandler;
    MBBidRewardVideoHandler mMvBidRewardVideoHandler;
    String placementId = "";
    String unitId = "";
    String mPayload;
    String mCustomData = "{}";

    /***
     * init
     */
    private void init(Context context) {

        RewardVideoListener videoListener = new RewardVideoListener() {

            @Override
            public void onVideoLoadSuccess(String placementId, String unitId) {
                try {
                    if (mMvRewardVideoHandler != null) {
                        MintegralATInitManager.getInstance().put(getTrackingInfo().getmUnitGroupUnitId(), mMvRewardVideoHandler);
                    }

                    if (mMvBidRewardVideoHandler != null) {
                        MintegralATInitManager.getInstance().put(getTrackingInfo().getmUnitGroupUnitId(), mMvBidRewardVideoHandler);
                    }
                } catch (Exception e) {

                }

                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onLoadSuccess(String placementId, String unitId) {
                if (mLoadListener != null) {
                    mLoadListener.onAdDataLoaded();
                }
            }

            @Override
            public void onVideoLoadFail(String pErrorMSG) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", pErrorMSG);
                }
            }

            @Override
            public void onAdShow() {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayStart();
                }
            }

            @Override
            public void onAdClose(boolean pIsCompleteView, String pRewardName,
                                  float pRewardAmout) {
                if (mImpressionListener != null) {
                    if (pIsCompleteView) {
                        mImpressionListener.onReward();
                    }
                    mImpressionListener.onRewardedVideoAdClosed();
                }

                try {
                    MintegralATInitManager.getInstance().remove(getTrackingInfo().getmUnitGroupUnitId());
                } catch (Exception e) {

                }
            }

            @Override
            public void onShowFail(String pErrorMSG) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayFailed("", pErrorMSG);
                }

            }

            @Override
            public void onVideoAdClicked(String placementId, String unitId) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayClicked();
                }
            }

            @Override
            public void onVideoComplete(String placementId, String unitId) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayEnd();
                }
            }

            @Override
            public void onEndcardShow(String placementId, String unitId) {

            }
        };

        if (TextUtils.isEmpty(mPayload)) {
            mMvRewardVideoHandler = new MBRewardVideoHandler(context.getApplicationContext(), placementId, unitId);
            mMvRewardVideoHandler.setRewardVideoListener(videoListener);
        } else {
            mMvBidRewardVideoHandler = new MBBidRewardVideoHandler(context.getApplicationContext(), placementId, unitId);
            mMvBidRewardVideoHandler.setRewardVideoListener(videoListener);
        }
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {

        String appid = (String) serverExtras.get("appid");
        String appkey = (String) serverExtras.get("appkey");
        unitId = (String) serverExtras.get("unitid");

        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey) || TextUtils.isEmpty(unitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "mintegral appid, appkey or unitid is empty!");
            }
            return;
        }

        if (serverExtras.containsKey("payload")) {
            mPayload = serverExtras.get("payload").toString();
        }

        if (serverExtras.containsKey("tp_info")) {
            mCustomData = serverExtras.get("tp_info").toString();
        }

        if (serverExtras.containsKey("placement_id")) {
            placementId = serverExtras.get("placement_id").toString();
        }

        MintegralATInitManager.getInstance().initSDK(context.getApplicationContext(), serverExtras, new MintegralATInitManager.InitCallback() {
            @Override
            public void onSuccess() {
                //init
                init(context);
                //load ad
                startLoad();
            }

            @Override
            public void onError(Throwable e) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean initNetworkObjectByPlacementId(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        if (serverExtras != null) {
            if (serverExtras.containsKey("appid") && serverExtras.containsKey("appkey") && serverExtras.containsKey("unitid")) {
                unitId = serverExtras.get("unitid").toString();
                if (serverExtras.containsKey("placement_id")) {
                    placementId = serverExtras.get("placement_id").toString();
                }
                init(context);
                return true;
            }
        }
        return false;
    }

    /***
     * load ad
     */
    public void startLoad() {
        if (mMvRewardVideoHandler != null) {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_LOAD, mCustomData);
            } catch (Throwable e) {
            }
            mMvRewardVideoHandler.load();
        }

        if (mMvBidRewardVideoHandler != null) {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_BIDLOAD, mCustomData);
            } catch (Throwable e) {
            }
            mMvBidRewardVideoHandler.loadFromBid(mPayload);
        }
    }

    @Override
    public void destory() {
        if (mMvBidRewardVideoHandler != null) {
            mMvBidRewardVideoHandler.setRewardVideoListener(null);
            mMvBidRewardVideoHandler = null;
        }

        if (mMvRewardVideoHandler != null) {
            mMvRewardVideoHandler.setRewardVideoListener(null);
            mMvRewardVideoHandler = null;
        }

    }


    @Override
    public boolean isAdReady() {
        if (mMvRewardVideoHandler != null) {
            return mMvRewardVideoHandler.isReady();
        }

        if (mMvBidRewardVideoHandler != null) {
            return mMvBidRewardVideoHandler.isBidReady();
        }
        return false;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return MintegralATInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public void show(Activity activity) {
        if (mMvRewardVideoHandler != null) {
            mMvRewardVideoHandler.show("1", mUserId);
        }

        if (mMvBidRewardVideoHandler != null) {
            mMvBidRewardVideoHandler.showFromBid("1", mUserId);
        }
    }


    @Override
    public String getNetworkSDKVersion() {
        return MintegralATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return MintegralATInitManager.getInstance().getNetworkName();
    }

    @Override
    public String getNetworkPlacementId() {
        return unitId;
    }

    @Override
    public String getBiddingToken(Context context) {
        return BidManager.getBuyerUid(context);
    }
}