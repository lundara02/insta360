package com.gothru.insta360.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.util.Log;

public class CameraBindNetworkManager extends ReactContextBaseJavaModule {
    public enum ErrorCode {
        OK, BIND_NETWORK_FAIL
    }
    private static String ACTION_BIND_NETWORK_NOTIFY = "com.gothru.insta360.ACTION_BIND_NETWORK_NOTIFY";
    private static String EXTRA_KEY_IS_BIND = "extra_key_is_bind";

    private boolean mHasBindNetwork = false;
    private boolean mIsBindingNetwork = false;
    private String mProcessName = null;

    public CameraBindNetworkManager() {
//        super(reactContext);
    }
    private static CameraBindNetworkManager sInstance;

    public static CameraBindNetworkManager getInstance() {
        if (sInstance == null) {
            sInstance = new CameraBindNetworkManager();
        }
        return sInstance;
    }

    @ReactMethod
    public void initWithOtherProcess() {
        mProcessName = getProcessName();
        bindNetwork(null);
        registerChildProcessBindNetworkReceiver();
    }

    private void registerChildProcessBindNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BIND_NETWORK_NOTIFY);
        getReactApplicationContext().registerReceiver(mOtherProcessBindNetworkReceiver, intentFilter);
    }

    private BroadcastReceiver mOtherProcessBindNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ACTION_BIND_NETWORK_NOTIFY)) {
                boolean isBind = intent.getBooleanExtra(EXTRA_KEY_IS_BIND, false);
                if (isBind) {
                    bindNetwork(null);
                } else {
                    unbindNetwork();
                }
            }
        }
    };

    private String getProcessName() {
        int pid = Process.myPid();
        ActivityManager manager = (ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = manager.getRunningAppProcesses();
        if (runningAppProcessesList == null) {
            runningAppProcessesList = new ArrayList<>();
        }
        for (ActivityManager.RunningAppProcessInfo process : runningAppProcessesList) {
            if (process.pid == pid) {
                return process.processName;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "CameraBindNetworkManager";
    }

    @ReactMethod
    public void bindNetwork(IBindNetWorkCallback bindNetWorkCallback) {
        if (mIsBindingNetwork) {
            if (bindNetWorkCallback != null) {
                bindNetWorkCallback.onResult(ErrorCode.OK);
            }
        } else if (mHasBindNetwork) {
            if (bindNetWorkCallback != null) {
                bindNetWorkCallback.onResult(ErrorCode.OK);
            }
        } else {
            Log.d("Bind Network", "Sini"+bindNetWorkCallback);
            bindWifiNet(bindNetWorkCallback);
        }
    }

    @ReactMethod
    public void unbindNetwork() {
        unbindWifiNet();
    }


    @ReactMethod
    private void bindWifiNet(IBindNetWorkCallback bindNetWorkCallback) {
        if (mIsBindingNetwork) {
            return;
        }
        mIsBindingNetwork = true;
        Network network = getWifiNetwork();
        if (network != null) {
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (ConnectivityManager.setProcessDefaultNetwork(network)) {
                        mHasBindNetwork = true;
                        mIsBindingNetwork = false;
                        if (bindNetWorkCallback != null) {
                            bindNetWorkCallback.onResult(ErrorCode.OK);
                        }
                    } else {
                        mIsBindingNetwork = false;
                        if (bindNetWorkCallback != null) {
                            bindNetWorkCallback.onResult(ErrorCode.BIND_NETWORK_FAIL);
                        }
                    }
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.bindProcessToNetwork(network)) {
                        mHasBindNetwork = true;
                        mIsBindingNetwork = false;
                        if (bindNetWorkCallback != null) {
                            bindNetWorkCallback.onResult(ErrorCode.OK);
                        }
                    } else {
                        mIsBindingNetwork = false;
                        if (bindNetWorkCallback != null) {
                            bindNetWorkCallback.onResult(ErrorCode.BIND_NETWORK_FAIL);
                        }
                    }
                }
            } catch (IllegalStateException e) {
                mIsBindingNetwork = false;
                if (bindNetWorkCallback != null) {
                    bindNetWorkCallback.onResult(ErrorCode.BIND_NETWORK_FAIL);
                }
            }
        } else {
            mIsBindingNetwork = false;
            if (bindNetWorkCallback != null) {
                bindNetWorkCallback.onResult(ErrorCode.BIND_NETWORK_FAIL);
            }
        }
    }

    private Network getWifiNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : connManager.getAllNetworks()) {
            NetworkInfo netInfo = connManager.getNetworkInfo(network);
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return network;
            }
        }
        return null;
    }

    private void unbindWifiNet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (ConnectivityManager.setProcessDefaultNetwork(null)) {
                mHasBindNetwork = false;
            }
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.bindProcessToNetwork(null)) {
                mHasBindNetwork = false;
            }
        }
    }

    /************************* interface *************************/
    public interface IBindNetWorkCallback {
        void onResult(ErrorCode errorCode);
    }

}
