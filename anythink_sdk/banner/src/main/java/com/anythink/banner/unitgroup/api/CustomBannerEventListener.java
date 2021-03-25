/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.banner.unitgroup.api;


/**
 * Used by Mediation
 */
public interface CustomBannerEventListener {

    void onBannerAdClicked();//Callback of Ad click

    void onBannerAdShow();////Callback of Ad impression

    void onBannerAdClose();////Callback of Ad close

    void onDeeplinkCallback(boolean isSuccess);

}
