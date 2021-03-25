/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.core.api;

import android.content.Context;
import android.util.Log;

import com.anythink.core.common.OffLineTkManager;
import com.anythink.core.common.base.Const;
import com.anythink.core.common.base.SDKContext;
import com.anythink.core.common.base.UploadDataLevelManager;
import com.anythink.core.common.utils.CommonSDKUtil;
import com.anythink.core.common.utils.task.TaskManager;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;


/**
 * SDK open api
 */

public class ATSDK {

    /**
     * GDPR LEVEL
     */
    public static final int PERSONALIZED = 0;
    public static final int NONPERSONALIZED = 1;
    public static final int UNKNOWN = 2;


    /**
     * Mark of SDK init
     */
    private static boolean HAS_INIT = false;

    private ATSDK() {

    }

    /**
     * sdk初始化
     *
     * @param context
     * @param appId
     * @param appKey
     */
    public static void init(Context context, String appId, String appKey) {
        init(context, appId, appKey, null);

    }

    /**
     * SDK init
     *
     * @param context
     * @param appId
     * @param appKey
     */
    @Deprecated
    public synchronized static void init(Context context, String appId, String appKey, ATSDKInitListener listener) {

        try {
            if (context == null) {
                if (listener != null) {
                    listener.onFail("init: Context is null!");
                }
                Log.e(Const.RESOURCE_HEAD, "init: Context is null!");
                return;
            }


            SDKContext.getInstance().init(context, appId, appKey);

            if (listener != null) {
                listener.onSuccess();
            }

            TaskManager.getInstance().run_proxy(new Runnable() {
                @Override
                public void run() {
                    OffLineTkManager.getInstance().tryToReSendRequest();
                }
            });


        } catch (Exception ex) {
            if (Const.DEBUG) {
                ex.printStackTrace();
            }

        } catch (Error e) {

        }
    }

    /**
     * Check SDK Area
     *
     * @return
     */
    public static boolean isCnSDK() {
        return SDKContext.getInstance().getExHandler() != null;
    }

    /**
     * Before initSDK
     **/
    public static void setChannel(String channel) {
        if (CommonSDKUtil.isChannelValid(channel)) {
            SDKContext.getInstance().setChannel(channel);
        }
    }

    public static void setSubChannel(String subChannel) {
        if (CommonSDKUtil.isSubChannelValid(subChannel)) {
            SDKContext.getInstance().setSubChannel(subChannel);
        }
    }

    /**
     * init custom key-value
     **/
    public static void initCustomMap(Map<String, Object> customMap) {
        SDKContext.getInstance().setAppCustomMap(customMap);
    }

    /**
     * init placement custom key-value
     */
    public static void initPlacementCustomMap(String placmentId, Map<String, Object> customMap) {
        SDKContext.getInstance().setPlacementCustomMap(placmentId, customMap);
    }

    /**
     * Set Exclude MyOffer Package List
     */
    public static void setExcludeMyOfferPkgList(List<String> packageList) {
        SDKContext.getInstance().setExcludeMyOfferPkgList(packageList);
    }

    /**
     * GDPR LEVEL Setting
     */
    public static void setGDPRUploadDataLevel(Context context, int level) {
        if (context == null) {
            Log.e(Const.RESOURCE_HEAD, "setGDPRUploadDataLevel: context should not be null");
            return;
        }

        /**Can't not set without PERSONALIZED and NONPERSONALIZED **/
        if (level == PERSONALIZED || level == NONPERSONALIZED) {
            UploadDataLevelManager.getInstance(context).setUploadDataLevel(level);
        } else {
            Log.e(Const.RESOURCE_HEAD, "GDPR level setting error!!! Level must be PERSONALIZED or NONPERSONALIZED.");
        }

    }

    public static void deniedUploadDeviceInfo(String... deviceInfos) {
        SDKContext.getInstance().deniedUploadDeviceInfo(deviceInfos);
    }

    /**
     * Get GDPR LEVEL
     */
    public static int getGDPRDataLevel(Context context) {
        return UploadDataLevelManager.getInstance(context).getUploadDataLevel();
    }

    /**
     * Check current area is EU-Traffic
     */
    public static boolean isEUTraffic(Context context) {
        return UploadDataLevelManager.getInstance(context).isEUTraffic();
    }

    public static void checkIsEuTraffic(Context context, NetTrafficeCallback netTrafficeCallback) {
        UploadDataLevelManager.getInstance(context).checkIsEuTraffic(netTrafficeCallback);
    }

    /**
     * Show GDPR Activity
     *
     * @param context
     */
    public static void showGdprAuth(Context context) {
        UploadDataLevelManager.getInstance(context).showUploadDataNotifyDialog(context, null);
    }

    /**
     * Show GDPR Activity with callback
     *
     * @param context
     */
    public static void showGdprAuth(Context context, ATGDPRAuthCallback callback) {
        UploadDataLevelManager.getInstance(context).showUploadDataNotifyDialog(context, callback);
    }


    /**
     * SDK Version
     *
     * @return
     */
    public static String getSDKVersionName() {
        return Const.SDK_VERSION_NAME;
    }


    /**
     * Open Debug log switch
     */
    public static void setNetworkLogDebug(boolean debug) {
        SDKContext.getInstance().setNetworkLogDebug(debug);
    }

    public static boolean isNetworkLogDebug() {
        return SDKContext.getInstance().isNetworkLogDebug();
    }

    /**
     * Check the correctness of SDK-init
     */
    public static void integrationChecking(Context context) {
        SDKContext.getInstance().integrationChecking(context);
    }


    public static void apiLog(String placementId, String adType, String apiStr, String result, String extra) {
        if (ATSDK.isNetworkLogDebug()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("placementId", placementId);
                jsonObject.put("adtype", adType);
                jsonObject.put("api", apiStr);
                jsonObject.put("result", result);
                jsonObject.put("reason", extra);
                Log.i(Const.RESOURCE_HEAD + "_network", jsonObject.toString());
            } catch (Throwable e) {

            }
        }
    }

    public static void setAdLogoVisible(boolean visible) {
        SDKContext.getInstance().setAdLogoVisible(visible);
    }

}
