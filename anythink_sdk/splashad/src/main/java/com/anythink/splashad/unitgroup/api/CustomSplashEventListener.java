/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.splashad.unitgroup.api;


import android.content.Context;

import com.anythink.core.api.ATNetworkConfirmInfo;

public interface CustomSplashEventListener {
    void onSplashAdShow();//Ad show

    void onSplashAdClicked(); //Ad Click

    void onSplashAdDismiss(); //Ad Dismiss

    void onDeeplinkCallback(boolean isSuccess);

    void onDownloadConfirm(Context context, ATNetworkConfirmInfo networkConfirmInfo);

}
