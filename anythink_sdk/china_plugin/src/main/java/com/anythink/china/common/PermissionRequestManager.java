/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.china.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.anythink.china.activity.TransparentActivity;

import java.util.Random;

public class PermissionRequestManager {

    public final static String READ_PHONE_STATE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    public final static String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * Use checkPermissionGrant to check the permission whether it's grant before using requestPermission
     *
     * @param context
     * @param callback
     * @param permissionList
     */
    public static void requestPermission(Context context, PermissionAuthorizeCallback callback, String... permissionList) {
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int requestCode = new Random().nextInt(1000000000);
            if (callback != null) {
                TransparentActivity.permissionMap.put(requestCode, callback);
            }
            Intent intent = new Intent(context, TransparentActivity.class);
            intent.putExtra(TransparentActivity.TYPE, TransparentActivity.PERMISSION_HANDLE_TYPE);
            intent.putExtra(TransparentActivity.REQUEST_CODE_KEY, requestCode);
            intent.putExtra(TransparentActivity.PERMISSION_LIST, permissionList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("PermissionManager", "Build.VERSION.SDK_INT below 23 does not require permission");
        }
    }

    public static boolean checkPermissionGrant(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public interface PermissionAuthorizeCallback {
        void onResultCallback(String[] permissionList, int[] resultCode);
    }

}
