/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.core.common.entity;

import com.anythink.core.common.base.SDKContext;

import org.json.JSONObject;

public abstract class TrackerInfo {
    public static final String OFM_TID_KEY = "ofm_tid_key";

    public static final int CAPPING_REASON = 1; //For showing
    public static final int PACCING_REASON = 2; //For showing
    public static final int LOADING_REASON = 3;
    public static final int HAS_OFFER_REASON = 4;
    public static final int PLACEMENT_STRATEGY_REASON = 5;
    public static final int NO_VAIL_ADSOURCE_REASON = 6;
    public static final int LOAD_FAIL_PACING_REASON = 7;
    public static final int LOAD_CAPPING_REASON = 8; //For loading


    //unitgroup fail reason
    public static final int AD_SOURCE_RETURN_FALSE_REASON = 0;
    public static final int AD_SOURCE_TRUE_BUT_OVERTIME_REASON = 1;
    public static final int AD_SOURCE_CAPPING_REASON = 2;
    public static final int AD_SOURCE_PACCING_REASON = 3;
    public static final int AD_SOURCE_NO_RESULT_REASON = 4;
    public static final int AD_SOURCE_ADX_BID_EXPIRE_REASON = 5;


    public static final String AD_NATIVE_TYPE = "0";
    public static final String AD_REWARDVIDEO_TYPE = "1";
    public static final String AD_BANNER_TYPE = "2";
    public static final String AD_INTERSTITIAL_TYPE = "3";
    public static final String AD_SPLASH_TYPE = "4";


    protected String mPlacementId; //placementid
    protected String mRequestId;
    protected String mAdType; //Ad Type：0：native  1 video  2 banner  3 interstitial
    protected String mAsid = ""; //Asid

    public String mClickTkUrl;//Click tracking
    public int mClickTkDelayMinTime;
    public int mClickTkDelayMaxTime;

    protected String mNetworkName; //Mediation Name;

    /**
     * 5.7.8 - ofm
     */
    protected int systemId;
    protected int tid;
    protected int isOfm;

    public void setIsOfm(int isOfm) {
        this.isOfm = isOfm;
    }

    public int isOfm() {
        return this.isOfm;
    }


    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getNetworkName() {
        return mNetworkName;
    }

    public void setNetworkName(String networkName) {
        this.mNetworkName = networkName;
    }

    public String getmClickTkUrl() {
        return mClickTkUrl;
    }

    public void setmClickTkUrl(String mClickTkUrl) {
        this.mClickTkUrl = mClickTkUrl;
    }

    public int getmClickTkDelayMinTime() {
        return mClickTkDelayMinTime;
    }

    public void setmClickTkDelayMinTime(int mClickTkDelayMinTime) {
        this.mClickTkDelayMinTime = mClickTkDelayMinTime;
    }

    public int getmClickTkDelayMaxTime() {
        return mClickTkDelayMaxTime;
    }

    public void setmClickTkDelayMaxTime(int mClickTkDelayMaxTime) {
        this.mClickTkDelayMaxTime = mClickTkDelayMaxTime;
    }

    protected int mTrafficGroupId; //Placement下的ABTest的Id

    public int getmTrafficGroupId() {
        return mTrafficGroupId;
    }

    public void setmTrafficGroupId(int mTrafficGroupId) {
        this.mTrafficGroupId = mTrafficGroupId;
    }

//    public String getmPsid() {
//        return mPsid;
//    }

//    public void setmPsid(String mPsid) {
//        this.mPsid = mPsid;
//    }

//    public String getmSessionId() {
//        return mSessionId;
//    }

//    public void setmSessionId(String mSessionId) {
//        this.mSessionId = mSessionId;
//    }

    public void setAsid(String asid) {
        mAsid = asid;
    }

    public String getmPlacementId() {
        return mPlacementId;
    }

    public void setmPlacementId(String mPlacementId) {
        this.mPlacementId = mPlacementId;
    }

    public String getmRequestId() {
        return mRequestId;
    }

    public void setmRequestId(String mRequestId) {
        this.mRequestId = mRequestId;
    }

    public String getmAdType() {
        return mAdType;
    }

    public String getAdTypeString() {
        switch (mAdType) {
            case AD_NATIVE_TYPE:
                return "native";
            case AD_REWARDVIDEO_TYPE:
                return "reward";
            case AD_BANNER_TYPE:
                return "banner";
            case AD_INTERSTITIAL_TYPE:
                return "inter";
            case AD_SPLASH_TYPE:
                return "splash";
        }
        return "none";
    }

    public void setmAdType(String mAdType) {
        this.mAdType = mAdType;
    }

    public JSONObject toJSONObject(int trackingType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", trackingType);
            jsonObject.put("pl_id", mPlacementId);
            jsonObject.put("req_id", mRequestId);
            jsonObject.put("format", Integer.parseInt(mAdType));
            jsonObject.put("ps_id", SDKContext.getInstance().getPsid());
            jsonObject.put("sessionid", SDKContext.getInstance().getSessionId(mPlacementId));
            jsonObject.put("traffic_group_id", mTrafficGroupId);
            if (isOfm == 1) {
                jsonObject.put("ofm_tid", tid);
                jsonObject.put("ofm_system", systemId);
            }
            jsonObject.put("is_ofm", isOfm);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
