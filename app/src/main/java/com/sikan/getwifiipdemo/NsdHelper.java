/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sikan.getwifiipdemo;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NsdHelper {

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    //自己可更改设备信息
    private String mServiceName = "THETA";
    //一般是这种类型 _http._tcp.
    private static final String SERVICE_TYPE = "_osc._tcp.";

    private static final String TAG = "NsdHelper";

    private NsdFoundListener mNsdFoundListener;

    private boolean isDiscovery;

    public interface NsdFoundListener{
        void foundWifi(NsdServiceInfo mService);
        void foundWifiError(NsdServiceInfo mService);
    }


    NsdHelper(Context context,NsdFoundListener listener) {
        this.mNsdFoundListener=listener;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    void discoverServices() {
        mDiscoveryListener = new MyDiscoveryListener();
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        isDiscovery=false;
    }
    
    void stopDiscovery() {
        if(!isDiscovery){
            isDiscovery=true;
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    }

    /*** New classes to avoid : java.lang.IllegalArgumentException: listener already in use ***/

    private class MyResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "*Jorgesys Resolve failed" + errorCode);
            mNsdFoundListener.foundWifiError(serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "*Jorgesys Resolve Succeeded. " + serviceInfo);
            mNsdFoundListener.foundWifi(serviceInfo);
        }
    }

    private class MyDiscoveryListener implements NsdManager.DiscoveryListener {
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(TAG, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            if(service.getServiceName().equals(mServiceName)){
                Log.e(TAG,"Unknown service:"+service);
            }else if (service.getServiceName().contains(mServiceName)){
                mNsdManager.resolveService(service, new MyResolveListener());
            }else{
                Log.e(TAG,"Unknown service:"+service);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            Log.e(TAG, "service lost" + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

    }
}
