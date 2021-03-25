/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.china.oaid.platform;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.anythink.china.oaid.OaidCallback;
import com.anythink.core.common.utils.CommonLogUtil;


public class HWOaidAidlUtil {
    private static final String TAG = "OaidAidlUtil";
    private static final String SERVICE_PACKAGE_NAME = "com.huawei.hwid";
    private static final String SERVICE_ACTION = "com.uodis.opendevice.OPENIDS_SERVICE";
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private HWIdentifierService mService;
    private OaidCallback mCallback;

    public HWOaidAidlUtil(Context context) {
        mContext = context;
    }

    private boolean bindService() {
        CommonLogUtil.d(TAG, "bindService");
        if (null == mContext) {
            CommonLogUtil.e(TAG, "context is null");
            return false;
        }
        mServiceConnection = new IdentifierServiceConnection();
        Intent intent = new Intent(SERVICE_ACTION);
        intent.setPackage(SERVICE_PACKAGE_NAME);
        boolean result = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        CommonLogUtil.i(TAG, "bindService result: " + result);
        return result;
    }

    private void unbindService() {
        CommonLogUtil.i(TAG, "unbindService");
        if (null == mContext) {
            CommonLogUtil.e(TAG, "context is null");
            return;
        }
        if (null != mServiceConnection) {
            mContext.unbindService(mServiceConnection);
            mService = null;
            mContext = null;
            mCallback = null;
        }
    }

    public void getOaid(OaidCallback callback) {
        if (null == callback) {
            CommonLogUtil.e(TAG, "callback is null");
            return;
        }
        mCallback = callback;
        bindService();
    }

    private final class IdentifierServiceConnection implements ServiceConnection {

        private IdentifierServiceConnection() {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CommonLogUtil.i(TAG, "onServiceConnected");
            mService = HWIdentifierService.Stub.asInterface(iBinder);
            if (null != mService) {
                try {
                    if (null != mCallback) {
                        mCallback.onSuccuss(mService.getOaid(), mService.isOaidTrackLimited());
                    }
                } catch (RemoteException e) {
                    CommonLogUtil.e(TAG, "getChannelInfo RemoteException");
                    if (null != mCallback) {
                        mCallback.onFail(e.getMessage());
                    }
                } catch (Exception e) {
                    CommonLogUtil.e(TAG, "getChannelInfo Excepition");
                    if (null != mCallback) {
                        mCallback.onFail(e.getMessage());
                    }
                } finally {
                    unbindService();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            CommonLogUtil.i(TAG, "onServiceDisconnected");
            mService = null;
        }
    }
}
