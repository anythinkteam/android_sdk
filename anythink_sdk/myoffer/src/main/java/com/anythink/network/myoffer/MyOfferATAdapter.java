/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.myoffer;

import android.content.Context;

import com.anythink.basead.entity.OfferError;
import com.anythink.basead.listeners.AdLoadListener;
import com.anythink.core.api.BaseAd;
import com.anythink.core.common.MyOfferAPIProxy;
import com.anythink.core.common.base.Const;
import com.anythink.core.common.entity.BaseAdRequestInfo;
import com.anythink.basead.myoffer.MyOfferNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;

import java.util.Map;

public class MyOfferATAdapter extends CustomNativeAdapter {
    private String offer_id = "";

    private boolean isDefaultOffer = false; //用于判断兜底offer的

    MyOfferNativeAd mMyOfferNativeAd;

    BaseAdRequestInfo mMyOfferRequestInfo;

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtra) {

        if (serverExtras.containsKey("my_oid")) {
            offer_id = serverExtras.get("my_oid").toString();
        }
        if (serverExtras.containsKey(Const.NETWORK_REQUEST_PARAMS_KEY.BASE_AD_PARAMS_KEY)) {
            mMyOfferRequestInfo = (BaseAdRequestInfo) serverExtras.get(Const.NETWORK_REQUEST_PARAMS_KEY.BASE_AD_PARAMS_KEY);
        }

        initNativeObject(context);
        final Context applicationContext = context.getApplicationContext();

        mMyOfferNativeAd.load(new AdLoadListener() {
            @Override
            public void onAdDataLoaded() {

            }

            @Override
            public void onAdCacheLoaded() {
                if (mLoadListener != null) {
                    MyOfferATNativeAd myOfferATNativeAd = new MyOfferATNativeAd(applicationContext, mMyOfferNativeAd);
                    mLoadListener.onAdCacheLoaded(myOfferATNativeAd);
                }
            }

            @Override
            public void onAdLoadFailed(OfferError error) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(error.getCode(), error.getDesc());
                }
            }
        });

    }

    private void initNativeObject(final Context context) {
        mMyOfferNativeAd = new MyOfferNativeAd(context, mMyOfferRequestInfo, offer_id, isDefaultOffer);
    }


    @Override
    public boolean initNetworkObjectByPlacementId(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtra) {
        if (serverExtras.containsKey("my_oid")) {
            offer_id = serverExtras.get("my_oid").toString();
        }
        if (serverExtras.containsKey(Const.NETWORK_REQUEST_PARAMS_KEY.BASE_AD_PARAMS_KEY)) {
            mMyOfferRequestInfo = (BaseAdRequestInfo) serverExtras.get(Const.NETWORK_REQUEST_PARAMS_KEY.BASE_AD_PARAMS_KEY);
        }

        if (serverExtras.containsKey(MyOfferAPIProxy.MYOFFER_DEFAULT_TAG)) {
            isDefaultOffer = (Boolean) serverExtras.get(MyOfferAPIProxy.MYOFFER_DEFAULT_TAG);
        }

        mMyOfferNativeAd = new MyOfferNativeAd(context, mMyOfferRequestInfo, offer_id, isDefaultOffer);
        return true;
    }


    @Override
    public BaseAd getBaseAdObject(Context context) {
        if (mMyOfferNativeAd != null && mMyOfferNativeAd.isReady()) {
            MyOfferATNativeAd myOfferATNativeAd = new MyOfferATNativeAd(context, mMyOfferNativeAd);
            return myOfferATNativeAd;
        }
        return null;
    }

    @Override
    public void destory() {
        if (mMyOfferNativeAd != null) {
            mMyOfferNativeAd.setListener(null);
            mMyOfferNativeAd = null;
        }
    }

    @Override
    public String getNetworkName() {
        return "MyOffer";
    }

    @Override
    public String getNetworkPlacementId() {
        return offer_id;
    }


    @Override
    public String getNetworkSDKVersion() {
        return Const.SDK_VERSION_NAME;
    }

}
