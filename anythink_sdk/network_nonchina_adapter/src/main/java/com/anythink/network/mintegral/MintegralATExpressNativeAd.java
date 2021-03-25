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
import com.mbridge.msdk.out.MBNativeAdvancedHandler;
import com.mbridge.msdk.out.NativeAdvancedAdListener;

import java.util.List;


public class MintegralATExpressNativeAd extends CustomNativeAd {
    private final String TAG = MintegralATExpressNativeAd.class.getSimpleName();
    MBNativeAdvancedHandler mtgNativeAdvancedHandler;

    public MintegralATExpressNativeAd(Context context, MBNativeAdvancedHandler mbNativeAdvancedHandler, boolean isHB) {
        this.mtgNativeAdvancedHandler = mbNativeAdvancedHandler;
        mbNativeAdvancedHandler.setAdListener(new NativeAdvancedAdListener() {
            @Override
            public void onLoadFailed(String s) {

            }

            @Override
            public void onLoadSuccessed() {

            }

            @Override
            public void onLogImpression() {
                notifyAdImpression();
            }

            @Override
            public void onClick() {
                notifyAdClicked();
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
                notifyAdDislikeClick();
            }
        });
    }


    @Override
    public void clear(final View view) {

    }

    @Override
    public void prepare(View view, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, layoutParams);
        if (mtgNativeAdvancedHandler != null) {
            mtgNativeAdvancedHandler.onResume();
        }


    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, clickViewList, layoutParams);
        if (mtgNativeAdvancedHandler != null) {
            mtgNativeAdvancedHandler.onResume();
        }
    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            return mtgNativeAdvancedHandler.getAdViewGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy() {
        if (mtgNativeAdvancedHandler != null) {
            mtgNativeAdvancedHandler.setAdListener(null);
            mtgNativeAdvancedHandler = null;
        }
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    boolean mIsAutoPlay;

    public void setIsAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mtgNativeAdvancedHandler != null) {
            mtgNativeAdvancedHandler.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mtgNativeAdvancedHandler != null) {
            mtgNativeAdvancedHandler.onPause();
        }
    }
}
