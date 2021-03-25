/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.toutiao;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTDrawFeedAd;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;

import java.util.ArrayList;
import java.util.List;

public class TTATNativeAd extends CustomNativeAd {
    TTNativeAd mTTFeedAd;
    Context mContext;
    String mUnitId;

    public TTATNativeAd(Context context, String unitId, TTNativeAd ttFeedAd, boolean canInterrupt, Bitmap videoPlayBitmap, int videoPlaySize) {
        mContext = context.getApplicationContext();
        mUnitId = unitId;
        mTTFeedAd = ttFeedAd;

        setAdData(canInterrupt, videoPlayBitmap, videoPlaySize);
    }


    public void setAdData(boolean canInterrupt, Bitmap videoPlayBitmap, int videoPlaySize) {
        setTitle(mTTFeedAd.getTitle());
        setDescriptionText(mTTFeedAd.getDescription());
        setIconImageUrl(mTTFeedAd.getIcon().getImageUrl());
        List<TTImage> imageList = mTTFeedAd.getImageList();
        ArrayList<String> imageStringList = new ArrayList<>();

        if (imageList != null && imageList.size() > 0) {
            for (TTImage ttImage : imageList) {
                imageStringList.add(ttImage.getImageUrl());
            }
            setMainImageUrl(imageStringList.get(0));
        }
        setImageUrlList(imageStringList);
        setCallToActionText(mTTFeedAd.getButtonText());
        if (mTTFeedAd instanceof TTDrawFeedAd) {
            ((TTDrawFeedAd) mTTFeedAd).setCanInterruptVideoPlay(canInterrupt);
            if (videoPlayBitmap != null && videoPlaySize > 0) {
                ((TTDrawFeedAd) mTTFeedAd).setPauseIcon(videoPlayBitmap, videoPlaySize);
            }
        }

        if (mTTFeedAd instanceof TTFeedAd) {
            ((TTFeedAd) mTTFeedAd).setVideoAdListener(new TTFeedAd.VideoAdListener() {
                @Override
                public void onVideoLoad(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoError(int i, int i1) {
                }

                @Override
                public void onVideoAdStartPlay(TTFeedAd ttFeedAd) {
                    notifyAdVideoStart();
                }

                @Override
                public void onVideoAdPaused(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoAdContinuePlay(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoAdComplete(TTFeedAd ttFeedAd) {
                    notifyAdVideoEnd();
                }

                @Override
                public void onProgressUpdate(long s, long s1) {
                }
            });
        }


    }

    private void getChildView(List<View> childViews, View view) {
        if (view instanceof ViewGroup && view != mTTFeedAd.getAdView()) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                getChildView(childViews, child);
            }
        } else {
            if (view != mTTFeedAd.getAdView()) {
                childViews.add(view);
            }
        }
    }

    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {

        List<View> childViews = new ArrayList<>();
        getChildView(childViews, view);
        mTTFeedAd.registerViewForInteraction((ViewGroup) view, childViews, childViews, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdShow(TTNativeAd ttNativeAd) {
                notifyAdImpression();
            }
        });

        if(view.getContext() instanceof Activity) {
            bindDislike(((Activity) view.getContext()));
        }

    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {
        mTTFeedAd.registerViewForInteraction((ViewGroup) view, clickViewList, clickViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdShow(TTNativeAd ttNativeAd) {
                notifyAdImpression();
            }
        });

        if(view.getContext() instanceof Activity) {
            bindDislike(((Activity) view.getContext()));
        }
    }

    private void bindDislike(final Activity activity) {
        ExtraInfo extraInfo = getExtraInfo();
        if (extraInfo != null) {
            View closeView = extraInfo.getCloseView();
            if (closeView != null) {

                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTTFeedAd == null) {
                            return;
                        }

                        TTAdDislike dislikeDialog = mTTFeedAd.getDislikeDialog(activity);
                        dislikeDialog.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {

                            @Override
                            public void onSelected(int i, String s) {
                                notifyAdDislikeClick();
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onRefuse() {

                            }
                        });
                        dislikeDialog.showDislikeDialog();
                    }
                });
            }
        }
    }

    @Override
    public Bitmap getAdLogo() {
        if (mTTFeedAd != null) {
            return mTTFeedAd.getAdLogo();
        }
        return null;
    }

    @Override
    public void clear(final View view) {

    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            return mTTFeedAd.getAdView();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void destroy() {
        mContext = null;
        mTTFeedAd = null;
    }
}
