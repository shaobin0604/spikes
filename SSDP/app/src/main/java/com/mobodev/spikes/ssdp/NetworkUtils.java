package com.mobodev.spikes.ssdp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    static public NetworkInfo getConnectedNetworkInfo(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        Log.i(TAG, "Could not find any connected network...");

        return null;
    }

    static public boolean isEthernet(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_ETHERNET);
    }

    static public boolean isWifi(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_WIFI) || ModelUtil.ANDROID_EMULATOR;
    }

    static public boolean isMobile(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_MOBILE) || isNetworkType(networkInfo, ConnectivityManager.TYPE_WIMAX);
    }

    static public boolean isNetworkType(NetworkInfo networkInfo, int type) {
        return networkInfo != null && networkInfo.getType() == type;
    }

    static public boolean isSSDPAwareNetwork(NetworkInfo networkInfo) {
        return isWifi(networkInfo) || isEthernet(networkInfo);
    }
}
