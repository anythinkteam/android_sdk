/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.mintegral;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.mbridge.msdk.foundation.entity.CampaignEx;
import com.mbridge.msdk.nativex.view.MBMediaView;
import com.mbridge.msdk.out.Campaign;
import com.mbridge.msdk.out.Frame;
import com.mbridge.msdk.out.MBBidNativeHandler;
import com.mbridge.msdk.out.MBNativeHandler;
import com.mbridge.msdk.out.NativeListener;
import com.mbridge.msdk.out.OnMBMediaViewListener;
import com.mbridge.msdk.out.OnMBMediaViewListenerPlus;

import java.util.List;
import java.util.Map;


public class MintegralATNativeAd extends CustomNativeAd {
    private final String TAG = MintegralATNativeAd.class.getSimpleName();
    Context mContext;
    MBNativeHandler mvNativeHandler;
    MBBidNativeHandler mvBidNativeHandler;

    Campaign mCampaign;

    public MintegralATNativeAd(Context context, String placementId
            , String unitId
            , Campaign campaign, boolean isHB) {
        mContext = context.getApplicationContext();
        Map<String, Object> properties = MBNativeHandler.getNativeProperties(placementId, unitId);

        mCampaign = campaign;

        if (isHB) {
            mvBidNativeHandler = new MBBidNativeHandler(properties, context);
            mvBidNativeHandler.setAdListener(new NativeListener.NativeAdListener() {
                @Override
                public void onAdLoaded(List<Campaign> list, int i) {

                }

                @Override
                public void onAdLoadError(String s) {
                }

                @Override
                public void onAdClick(Campaign campaign) {
                    notifyAdClicked();
                }

                @Override
                public void onAdFramesLoaded(List<Frame> list) {

                }

                @Override
                public void onLoggingImpression(int i) {
                    notifyAdImpression();
                }
            });
        } else {
            mvNativeHandler = new MBNativeHandler(properties, context);
            mvNativeHandler.setAdListener(new NativeListener.NativeAdListener() {
                @Override
                public void onAdLoaded(List<Campaign> list, int i) {

                }

                @Override
                public void onAdLoadError(String s) {
                }

                @Override
                public void onAdClick(Campaign campaign) {
                    notifyAdClicked();
                }

                @Override
                public void onAdFramesLoaded(List<Frame> list) {

                }

                @Override
                public void onLoggingImpression(int i) {
                    notifyAdImpression();
                }
            });
        }

        setAdData();
    }

    public void setAdData() {
        setTitle(mCampaign.getAppName());
        setDescriptionText(mCampaign.getAppDesc());
        setIconImageUrl(mCampaign.getIconUrl());
        setCallToActionText(mCampaign.getAdCall());
        setMainImageUrl(mCampaign.getImageUrl());
        setStarRating(mCampaign.getRating());

        CampaignEx campaignEx = (CampaignEx) mCampaign;

        if (campaignEx.getVideoUrlEncode() != null && campaignEx.getVideoUrlEncode().length() > 0) {
            MintegralATNativeAd.this.mAdSourceType = VIDEO_TYPE;
        } else {
            MintegralATNativeAd.this.mAdSourceType = IMAGE_TYPE;
        }
    }


    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
        if (mvNativeHandler != null) {
            mvNativeHandler.registerView(view, mCampaign);
        }

        if (mvBidNativeHandler != null) {
            mvBidNativeHandler.registerView(view, mCampaign);
        }
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {
        if (mvNativeHandler != null) {
            mvNativeHandler.registerView(view, clickViewList, mCampaign);
        }

        if (mvBidNativeHandler != null) {
            mvBidNativeHandler.registerView(view, mCampaign);
        }
    }

    @Override
    public void clear(final View view) {
        if (mMVMediaView != null) {
            mMVMediaView.destory();
            mMVMediaView = null;
        }
        if (mvNativeHandler != null) {
            mvNativeHandler.unregisterView(view, mCampaign);
        }
        if (mvBidNativeHandler != null) {
            mvBidNativeHandler.unregisterView(view, mCampaign);
        }
    }


    MBMediaView mMVMediaView;

    @Override
    public View getAdMediaView(Object... object) {
        try {
            mMVMediaView = new MBMediaView(mContext);
            mMVMediaView.setIsAllowFullScreen(true);
            mMVMediaView.setNativeAd(mCampaign);
            mMVMediaView.setOnMediaViewListener(new OnMBMediaViewListenerPlus() {
                @Override
                public void onEnterFullscreen() {

                }

                @Override
                public void onExitFullscreen() {

                }

                @Override
                public void onStartRedirection(Campaign campaign, String s) {

                }

                @Override
                public void onFinishRedirection(Campaign campaign, String s) {

                }

                @Override
                public void onRedirectionFailed(Campaign campaign, String s) {

                }

                @Override
                public void onVideoAdClicked(Campaign campaign) {
                    notifyAdClicked();
                }

                @Override
                public void onVideoStart() {
                    notifyAdVideoStart();
                }

                @Override
                public void onVideoComplete() {
                    notifyAdVideoEnd();
                }
            });

            return mMVMediaView;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy() {
        if (mMVMediaView != null) {
            mMVMediaView.destory();
            mMVMediaView = null;
        }
        if (mvNativeHandler != null) {
            mvNativeHandler.setAdListener(null);
            mvNativeHandler.clearVideoCache();
            mvNativeHandler.release();
            mvNativeHandler = null;
        }

        if (mvBidNativeHandler != null) {
            mvBidNativeHandler.setAdListener(null);
            mvBidNativeHandler.clearVideoCache();
            mvBidNativeHandler.bidRelease();
            mvBidNativeHandler = null;
        }
        mContext = null;
        mCampaign = null;
    }

    boolean mIsAutoPlay;

    public void setIsAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
    }


}
