/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.core.common.base;

import android.app.Activity;
import android.os.Looper;

import com.anythink.core.common.entity.AdTrackingInfo;
import com.anythink.core.common.utils.task.TaskManager;
import com.anythink.core.strategy.PlaceStrategy;

import java.lang.ref.WeakReference;

/**
 * All Mediation's BaseAdapter
 */
public abstract class AnyThinkBaseAdapter {
    private AdTrackingInfo mTrackingInfo;
    private PlaceStrategy.UnitGroupInfo mUnitgroupInfo;
    boolean isRefresh;
    protected WeakReference<Activity> mActivityRef;

    final public void setTrackingInfo(AdTrackingInfo adTrackingInfo) {
        mTrackingInfo = adTrackingInfo;
    }

    final public AdTrackingInfo getTrackingInfo() {
        return mTrackingInfo;
    }

    final public PlaceStrategy.UnitGroupInfo getmUnitgroupInfo() {
        return mUnitgroupInfo;
    }

    final public void setmUnitgroupInfo(PlaceStrategy.UnitGroupInfo mUnitgroupInfo) {
        this.mUnitgroupInfo = mUnitgroupInfo;
    }

    final public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    final public boolean isRefresh() {
        return this.isRefresh;
    }

    final public void refreshActivityContext(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }


    final public void postOnMainThread(Runnable runnable) {
        SDKContext.getInstance().runOnMainThread(runnable);
    }

    final public void runOnNetworkRequestThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            TaskManager.getInstance().runNetworkRequest(runnable);
        } else {
            runnable.run();
        }
    }

//    final public void log(String action, String status, String extraMsg) {
//        if (ATSDK.isNetworkLogDebug()) {
//            if (mTrackingInfo != null) {
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    if (mTrackingInfo.ismIsDefaultNetwork()) {
//                        jsonObject.put("isDefault", true);
//                    }
//                    jsonObject.put("placementId", mTrackingInfo.getmPlacementId());
//                    jsonObject.put("adType", mTrackingInfo.getAdTypeString());
//                    jsonObject.put("action", action);
//                    jsonObject.put("refresh", mTrackingInfo.getmRefresh());
//                    jsonObject.put("result", status);
//                    jsonObject.put("position", mTrackingInfo.getRequestLevel());
//                    jsonObject.put("networkType", mTrackingInfo.getmNetworkType());
//                    jsonObject.put("networkName", mTrackingInfo.getNetworkName());
//                    jsonObject.put("networkVersion", mTrackingInfo.getmNetworkVersion());
//                    jsonObject.put("networkUnit", mTrackingInfo.getmNetworkContent());
//                    jsonObject.put("isHB", mTrackingInfo.getmBidType());
//                    jsonObject.put("msg", extraMsg);
//                    jsonObject.put("hourly_frequency", mTrackingInfo.getmHourlyFrequency());
//                    jsonObject.put("daily_frequency", mTrackingInfo.getmDailyFrequency());
//                    jsonObject.put("network_list", mTrackingInfo.getmNetworkList());
//                    jsonObject.put("request_network_num", mTrackingInfo.getmRequestNetworkNum());
//                    jsonObject.put("handle_class", getClass().getName());
//                    SDKContext.getInstance().printJson(Const.RESOURCE_HEAD + "_network", jsonObject.toString());
//
//                } catch (Exception e) {
//
//                }
//
//            }
//        }
//    }

}
