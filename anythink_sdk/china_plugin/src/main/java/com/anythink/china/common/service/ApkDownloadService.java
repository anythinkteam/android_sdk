/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.china.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.anythink.china.common.ApkDownloadManager;
import com.anythink.china.common.download.ApkBaseLoader;
import com.anythink.china.common.download.ApkLoader;
import com.anythink.china.common.download.ApkRequest;
import com.anythink.china.common.notification.ApkNotificationManager;
import com.anythink.core.common.utils.CommonLogUtil;

import java.util.HashMap;
import java.util.Map;

public class ApkDownloadService extends Service {

    private static final String TAG = ApkDownloadService.class.getSimpleName();
    public static final String EXTRA_UNIQUE_ID = "extra_unique_id";

    private Map<String, ApkLoader> mApkLoaderMap = new HashMap<>();//apkRequest.uniqueID -> ApkLoader

    public class ApkDownloadBinder extends Binder implements IApkDownloadBinder {

        @Override
        public void pause(String uniqueID) {
            ApkLoader apkLoader = mApkLoaderMap.get(uniqueID);
            if (apkLoader != null) {
                apkLoader.pause();
                mApkLoaderMap.remove(uniqueID);
            }
        }

        @Override
        public void stop(String uniqueID) {
            ApkLoader apkLoader = mApkLoaderMap.get(uniqueID);
            if (apkLoader != null) {
                apkLoader.stop();
                mApkLoaderMap.remove(uniqueID);
            }
        }

        @Override
        public boolean canStopSelf() {
            return mApkLoaderMap.size() == 0;
        }

    }


    private void download(String uniqueID) {
        try {
            Map<String, ApkRequest> downloadingRequestMap = ApkDownloadManager.getInstance(getApplicationContext()).getDownloadingRequestMap();
            ApkRequest apkRequest = downloadingRequestMap.get(uniqueID);
            if (apkRequest == null) {
                return;
            }

            final ApkLoader apkLoader = new ApkLoader(apkRequest);
            apkLoader.start(new ApkBaseLoader.DownloadListener() {
                @Override
                public void onStartBefore(final ApkRequest apkRequest, final long progress, final long all) {
                    ApkBaseLoader.DownloadListener apkLoaderListener = ApkDownloadManager.getInstance(getApplicationContext()).getApkLoaderListener(apkRequest.uniqueID);
                    if (apkLoaderListener != null) {
                        apkLoaderListener.onStartBefore(apkRequest, progress, all);
                    }
                }

                @Override
                public void onSuccess(final ApkRequest apkRequest, final long downloadTime) {
                    if (mApkLoaderMap != null) {
                        mApkLoaderMap.remove(apkRequest.uniqueID);
                    }
                    ApkBaseLoader.DownloadListener apkLoaderListener = ApkDownloadManager.getInstance(getApplicationContext()).getApkLoaderListener(apkRequest.uniqueID);
                    if (apkLoaderListener != null) {
                        apkLoaderListener.onSuccess(apkRequest, downloadTime);
                    }
                }

                @Override
                public void onProgress(final ApkRequest apkRequest, final long progress, final long all) {
                    ApkBaseLoader.DownloadListener apkLoaderListener = ApkDownloadManager.getInstance(getApplicationContext()).getApkLoaderListener(apkRequest.uniqueID);
                    if (apkLoaderListener != null) {
                        apkLoaderListener.onProgress(apkRequest, progress, all);
                    }
                }

                @Override
                public void onFailed(final ApkRequest apkRequest, final String msg) {
                    if (mApkLoaderMap != null) {
                        mApkLoaderMap.remove(apkRequest.uniqueID);
                    }
                    ApkBaseLoader.DownloadListener apkLoaderListener = ApkDownloadManager.getInstance(getApplicationContext()).getApkLoaderListener(apkRequest.uniqueID);
                    if (apkLoaderListener != null) {
                        apkLoaderListener.onFailed(apkRequest, msg);
                    }
                }

                @Override
                public void onCancel(final ApkRequest apkRequest, final long progress, final long all, final int status) {
                    if (mApkLoaderMap != null) {
                        mApkLoaderMap.remove(apkRequest.uniqueID);
                    }
                    ApkBaseLoader.DownloadListener apkLoaderListener = ApkDownloadManager.getInstance(getApplicationContext()).getApkLoaderListener(apkRequest.uniqueID);
                    if (apkLoaderListener != null) {
                        apkLoaderListener.onCancel(apkRequest, progress, all, status);
                    }
                }
            });

            if (mApkLoaderMap != null) {
                mApkLoaderMap.put(uniqueID, apkLoader);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ApkDownloadBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CommonLogUtil.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        CommonLogUtil.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String uniqueID = intent.getStringExtra(ApkDownloadService.EXTRA_UNIQUE_ID);
            download(uniqueID);
        }

        return Service.START_NOT_STICKY;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ApkNotificationManager.getInstance(this.getApplicationContext()).cancelAllNotification();

        super.onTaskRemoved(rootIntent);
    }
}
