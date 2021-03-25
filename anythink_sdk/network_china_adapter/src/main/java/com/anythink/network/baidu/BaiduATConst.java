/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.network.baidu;

import com.baidu.mobads.AdSettings;

public class BaiduATConst {
    public static final int NETWORK_FIRM_ID = 22;

    public static String getNetworkVersion() {
        try {
            return AdSettings.getSDKVersion() + "";
        } catch (Throwable e) {

        }
        return "";
    }
}
