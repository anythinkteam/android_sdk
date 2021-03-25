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
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.common.base.Const;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.mbridge.msdk.MBridgeConstans;
import com.mbridge.msdk.mbbid.out.BidManager;
import com.mbridge.msdk.out.AutoPlayMode;
import com.mbridge.msdk.out.Campaign;
import com.mbridge.msdk.out.CustomInfoManager;
import com.mbridge.msdk.out.Frame;
import com.mbridge.msdk.out.MBBidNativeHandler;
import com.mbridge.msdk.out.MBConfiguration;
import com.mbridge.msdk.out.MBMultiStateEnum;
import com.mbridge.msdk.out.MBNativeAdvancedHandler;
import com.mbridge.msdk.out.MBNativeHandler;
import com.mbridge.msdk.out.NativeAdvancedAdListener;
import com.mbridge.msdk.out.NativeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MintegralATAdapter extends CustomNativeAdapter {

    private static final String TAG = MintegralATAdapter.class.getSimpleName();
    String mPayload;
    String mCustomData = "{}";
    String mUnitType;
    String videoMuted;
    String videoAutoPlay;
    String closeButton;

    int expressWidth;
    int expressHeight;
    private String unitId = "";

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        String appid = "";
        unitId = "";
        String sdkKey = "";
        String placementId = "";
        String suportVideo_str = "1";
        //支持视频
        boolean suportVideo = false;

        try {
            if (serverExtras.containsKey("appid")) {
                appid = serverExtras.get("appid").toString();
            }
            if (serverExtras.containsKey("unitid")) {
                unitId = serverExtras.get("unitid").toString();
            }

            if (serverExtras.containsKey("placement_id")) {
                placementId = serverExtras.get("placement_id").toString();
            }
            if (serverExtras.containsKey("appkey")) {
                sdkKey = serverExtras.get("appkey").toString();
            }

            if (serverExtras.containsKey("payload")) {
                mPayload = serverExtras.get("payload").toString();
            }

            if (serverExtras.containsKey("tp_info")) {
                mCustomData = serverExtras.get("tp_info").toString();
            }

            if (serverExtras.containsKey("unit_type")) {
                mUnitType = serverExtras.get("unit_type").toString();
            }

            if (serverExtras.containsKey("video_muted")) {
                videoMuted = serverExtras.get("video_muted").toString();
            }

            if (serverExtras.containsKey("video_autoplay")) {
                videoAutoPlay = serverExtras.get("video_autoplay").toString();
            }

            if (serverExtras.containsKey("close_button")) {
                closeButton = serverExtras.get("close_button").toString();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(unitId) || TextUtils.isEmpty(sdkKey)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "mintegral appid ,unitid or sdkkey is empty.");
            }
            return;
        }

        int requestNum = 1;
        try {
            if (serverExtras != null) {
                requestNum = Integer.parseInt(serverExtras.get(Const.NETWORK_REQUEST_PARAMS_KEY.REQUEST_AD_NUM).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverExtras.containsKey("suport_video")) {
            suportVideo_str = serverExtras.get("suport_video").toString();
            if ("1".equals(suportVideo_str)) {
                suportVideo = true;
            }
        } else {
            suportVideo = false;
        }

        try {
            expressWidth = Integer.parseInt(localExtras.get(ATAdConst.KEY.AD_WIDTH).toString());
            expressHeight = Integer.parseInt(localExtras.get(ATAdConst.KEY.AD_HEIGHT).toString());
        } catch (Throwable e) {

        }

        if (expressWidth <= 0 && expressHeight <= 0) {
            try {
                expressWidth = Integer.parseInt(localExtras.get(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH).toString());
                expressHeight = Integer.parseInt(localExtras.get(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT).toString());
            } catch (Exception e) {
                Log.e(TAG, "Mintegral AdvancedNative size is empty.");
            }

        }

        final String finalUnitId = unitId;
        final boolean finalSuportVideo = suportVideo;
        final int finalRequestNum = requestNum;
        final String finalPlacementId = placementId;

        MintegralATInitManager.getInstance().initSDK(context, serverExtras, new MintegralATInitManager.InitCallback() {
            @Override
            public void onSuccess() {
                startLoad(context, serverExtras, finalPlacementId, finalUnitId, finalSuportVideo, finalRequestNum);
            }

            @Override
            public void onError(Throwable e) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", e.getMessage());
                }
            }
        });
    }

    private void startLoad(Context context, Map<String, Object> serverExtras, String placementId, String unitId, boolean suportVideo, int requestNum) {

        boolean isAutoPlay = false;
        try {
            if (serverExtras != null) {
                isAutoPlay = Boolean.parseBoolean(serverExtras.get(CustomNativeAd.IS_AUTO_PLAY_KEY).toString());
            }
        } catch (Exception e) {

        }

        if (TextUtils.equals(mUnitType, "1")) {
            loadExpressAd(context, placementId, unitId);
        } else {
            loadAd(context, requestNum, placementId, unitId, suportVideo, isAutoPlay);
        }

    }

    private void loadExpressAd(final Context context, final String placementId, final String unitId) {
        final MBNativeAdvancedHandler mtgNativeAdvancedHandler = new MBNativeAdvancedHandler((Activity) context, placementId, unitId);
        if (!TextUtils.isEmpty(videoMuted)) {
            switch (videoMuted) {
                case "0":
                    mtgNativeAdvancedHandler.setPlayMuteState(MBridgeConstans.REWARD_VIDEO_PLAY_MUTE);
                    break;
                case "1":
                    mtgNativeAdvancedHandler.setPlayMuteState(MBridgeConstans.REWARD_VIDEO_PLAY_NOT_MUTE);
                    break;
            }
        }

        if (!TextUtils.isEmpty(videoAutoPlay)) {
            switch (videoAutoPlay) {
                case "1":
                    mtgNativeAdvancedHandler.autoLoopPlay(AutoPlayMode.PLAY_WHEN_NETWORK_IS_WIFI);
                    break;
                case "2":
                    mtgNativeAdvancedHandler.autoLoopPlay(AutoPlayMode.PLAY_WHEN_USER_CLICK);
                    break;
                case "3":
                    mtgNativeAdvancedHandler.autoLoopPlay(AutoPlayMode.PLAY_WHEN_NETWORK_IS_AVAILABLE);
                    break;
            }
        }

        if (!TextUtils.isEmpty(closeButton)) {
            switch (closeButton) {
                case "0":
                    mtgNativeAdvancedHandler.setCloseButtonState(MBMultiStateEnum.positive);
                    break;
                case "1":
                    mtgNativeAdvancedHandler.setCloseButtonState(MBMultiStateEnum.negative);
                    break;
            }
        }

        mtgNativeAdvancedHandler.setNativeViewSize(expressWidth, expressHeight);

        mtgNativeAdvancedHandler.setAdListener(new NativeAdvancedAdListener() {
            @Override
            public void onLoadFailed(String s) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", s);
                }
                mtgNativeAdvancedHandler.setAdListener(null);
            }

            @Override
            public void onLoadSuccessed() {
                MintegralATExpressNativeAd mintegralATExpressNativeAd = new MintegralATExpressNativeAd(context, mtgNativeAdvancedHandler, false);
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded(mintegralATExpressNativeAd);
                }
            }

            @Override
            public void onLogImpression() {

            }

            @Override
            public void onClick() {

            }

            @Override
            public void onLeaveApp() {

            }

            @Override
            public void showFullScreen() {

            }

            @Override
            public void closeFullScreen() {

            }

            @Override
            public void onClose() {

            }
        });

        if (TextUtils.isEmpty(mPayload)) {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_LOAD, mCustomData);
            } catch (Throwable e) {
            }

            mtgNativeAdvancedHandler.load();
        } else {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_BIDLOAD, mCustomData);
            } catch (Throwable e) {
            }

            mtgNativeAdvancedHandler.loadByToken(mPayload);
        }

    }


    private void loadAd(final Context context, final int adnum, final String placementId, final String unitId, boolean supportVideo, final boolean isAutoPlay) {
        Map<String, Object> properties = MBNativeHandler
                .getNativeProperties(placementId, unitId);

        properties.put(MBridgeConstans.PROPERTIES_AD_NUM, adnum);
        properties.put(MBridgeConstans.PROPERTIES_LAYOUT_TYPE,
                MBridgeConstans.LAYOUT_NATIVE);
//        properties.put(MIntegralConstans.PLACEMENT_ID, placementId);
//        //MV 广告位 ID 必传
//        properties.put(MIntegralConstans.PROPERTIES_UNIT_ID, unitId);

        //设置是否支持视频
        properties.put(MBridgeConstans.NATIVE_VIDEO_SUPPORT, supportVideo);


        MBNativeHandler mvNativeHandler = null;
        MBBidNativeHandler mtgBidNativeHandler = null;

        if (TextUtils.isEmpty(mPayload)) {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_LOAD, mCustomData);
            } catch (Throwable e) {
            }

            mvNativeHandler = new MBNativeHandler(properties, context.getApplicationContext());
        } else {
            try {
                CustomInfoManager.getInstance().setCustomInfo(unitId, CustomInfoManager.TYPE_BIDLOAD, mCustomData);
            } catch (Throwable e) {
            }

            mtgBidNativeHandler = new MBBidNativeHandler(properties, context.getApplicationContext());
        }

        final MBNativeHandler finalMvNativeHandler = mvNativeHandler;
        final MBBidNativeHandler finalMtgBidNativeHandler = mtgBidNativeHandler;
        NativeListener.NativeAdListener listener = new NativeListener.NativeAdListener() {

            @Override
            public void onAdLoaded(List<Campaign> list, int i) {
                if (list == null || list.size() <= 0) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", "Request success but no Ad return!");
                    }

                    if (finalMvNativeHandler != null) {
                        finalMvNativeHandler.setAdListener(null);
                        finalMvNativeHandler.release();
                    } else if (finalMtgBidNativeHandler != null) {
                        finalMtgBidNativeHandler.setAdListener(null);
                        finalMtgBidNativeHandler.bidRelease();
                    }
                    return;
                }

                boolean hasReturn = false;
                List<CustomNativeAd> customNativeAds = new ArrayList<>();
                for (Campaign campaign : list) {
                    if (campaign != null) {
                        hasReturn = true;
                        boolean isHB = !TextUtils.isEmpty(mPayload);
                        MintegralATNativeAd mintegralNativeAd = new MintegralATNativeAd(context, placementId, unitId, campaign, isHB);
                        mintegralNativeAd.setIsAutoPlay(isAutoPlay);
                        customNativeAds.add(mintegralNativeAd);
                    }
                }

                if (!hasReturn) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", "Request success but no Ad return!");
                    }
                } else {
                    if (mLoadListener != null) {
                        CustomNativeAd[] customNativeAdsArray = new CustomNativeAd[customNativeAds.size()];
                        customNativeAdsArray = customNativeAds.toArray(customNativeAdsArray);
                        mLoadListener.onAdCacheLoaded(customNativeAdsArray);
                    }
                }

                if (finalMvNativeHandler != null) {
                    finalMvNativeHandler.setAdListener(null);
                    finalMvNativeHandler.release();
                } else if (finalMtgBidNativeHandler != null) {
                    finalMtgBidNativeHandler.setAdListener(null);
                    finalMtgBidNativeHandler.bidRelease();
                }
            }

            @Override
            public void onAdLoadError(String s) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", s);
                }
            }

            @Override
            public void onAdClick(Campaign campaign) {
            }

            @Override
            public void onAdFramesLoaded(List<Frame> list) {
            }

            @Override
            public void onLoggingImpression(int i) {
            }
        };

        if (finalMvNativeHandler != null) {
            finalMvNativeHandler.setAdListener(listener);
            finalMvNativeHandler.load();
        } else if (finalMtgBidNativeHandler != null) {
            finalMtgBidNativeHandler.setAdListener(listener);
            finalMtgBidNativeHandler.bidLoad(mPayload);
        }
    }


    @Override
    public String getNetworkSDKVersion() {
        return MintegralATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public void destory() {

    }

    @Override
    public String getNetworkName() {
        return MintegralATInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return MintegralATInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
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
