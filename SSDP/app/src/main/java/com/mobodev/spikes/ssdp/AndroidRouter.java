package com.mobodev.spikes.ssdp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AndroidRouter extends Router {
    private static final String TAG = "AndroidRouter";

    final private Context context;

    final private WifiManager wifiManager;
    protected WifiManager.MulticastLock multicastLock;
    protected WifiManager.WifiLock wifiLock;
    protected NetworkInfo networkInfo;
    protected BroadcastReceiver broadcastReceiver;

    public AndroidRouter(Context context, boolean isDevice) {
        super(isDevice);

        this.context = context.getApplicationContext();
        this.wifiManager = ((WifiManager) this.context.getSystemService(Context.WIFI_SERVICE));
        this.networkInfo = NetworkUtils.getConnectedNetworkInfo(this.context);

        // Only register for network connectivity changes if we are not running on emulator
        if (!ModelUtil.ANDROID_EMULATOR) {
            this.broadcastReceiver = createConnectivityBroadcastReceiver();
            context.registerReceiver(broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    protected BroadcastReceiver createConnectivityBroadcastReceiver() {
        return new ConnectivityBroadcastReceiver();
    }

    @Override
    protected int getLockTimeoutMillis() {
        return 15000;
    }

    @Override
    public void shutdown() throws RouterException {
        super.shutdown();
        unregisterBroadcastReceiver();
    }

    @Override
    public boolean enable() throws RouterException {
        lock(writeLock);
        try {
            boolean enabled;
            if ((enabled = super.enable())) {
                // Enable multicast on the WiFi network interface,
                // requires android.permission.CHANGE_WIFI_MULTICAST_STATE
                if (isWifi()) {
                    setWiFiMulticastLock(true);
                    setWifiLock(true);
                }
            }
            return enabled;
        } finally {
            unlock(writeLock);
        }
    }

    @Override
    public boolean disable() throws RouterException {
        lock(writeLock);
        try {
            // Disable multicast on WiFi network interface,
            // requires android.permission.CHANGE_WIFI_MULTICAST_STATE
            if (isWifi()) {
                setWiFiMulticastLock(false);
                setWifiLock(false);
            }
            return super.disable();
        } finally {
            unlock(writeLock);
        }
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public boolean isMobile() {
        return NetworkUtils.isMobile(networkInfo);
    }

    public boolean isWifi() {
        return NetworkUtils.isWifi(networkInfo);
    }

    public boolean isEthernet() {
        return NetworkUtils.isEthernet(networkInfo);
    }

    public boolean enableWiFi() {
        Log.i(TAG, "Enabling WiFi...");
        try {
            return wifiManager.setWifiEnabled(true);
        } catch (Throwable t) {
            // workaround (HTC One X, 4.0.3)
            //java.lang.SecurityException: Permission Denial: writing com.android.providers.settings.SettingsProvider
            // uri content://settings/system from pid=4691, uid=10226 requires android.permission.WRITE_SETTINGS
            //	at android.os.Parcel.readException(Parcel.java:1332)
            //	at android.os.Parcel.readException(Parcel.java:1286)
            //	at android.net.wifi.IWifiManager$Stub$Proxy.setWifiEnabled(IWifiManager.java:1115)
            //	at android.net.wifi.WifiManager.setWifiEnabled(WifiManager.java:946)
            Log.w(TAG, "SetWifiEnabled failed", t);
            return false;
        }
    }

    public void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    protected void setWiFiMulticastLock(boolean enable) {
        if (multicastLock == null) {
            multicastLock = wifiManager.createMulticastLock(getClass().getSimpleName());
        }

        if (enable) {
            if (multicastLock.isHeld()) {
                Log.w(TAG, "WiFi multicast lock already acquired");
            } else {
                Log.i(TAG, "WiFi multicast lock acquired");
                multicastLock.acquire();
            }
        } else {
            if (multicastLock.isHeld()) {
                Log.i(TAG, "WiFi multicast lock released");
                multicastLock.release();
            } else {
                Log.w(TAG, "WiFi multicast lock already released");
            }
        }
    }

    protected void setWifiLock(boolean enable) {
        if (wifiLock == null) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getClass().getSimpleName());
        }

        if (enable) {
            if (wifiLock.isHeld()) {
                Log.w(TAG, "WiFi lock already acquired");
            } else {
                Log.i(TAG, "WiFi lock acquired");
                wifiLock.acquire();
            }
        } else {
            if (wifiLock.isHeld()) {
                Log.i(TAG, "WiFi lock released");
                wifiLock.release();
            } else {
                Log.w(TAG, "WiFi lock already released");
            }
        }
    }

    /**
     * Can be overriden by subclasses to do additional work.
     *
     * @param oldNetwork <code>null</code> when first called by constructor.
     */
    protected void onNetworkTypeChange(NetworkInfo oldNetwork, NetworkInfo newNetwork) throws RouterException {
        Log.i(TAG, String.format("Network type changed %s => %s",
                oldNetwork == null ? "" : oldNetwork.getTypeName(),
                newNetwork == null ? "NONE" : newNetwork.getTypeName()));

        if (disable()) {
            Log.i(TAG, String.format(
                    "Disabled router on network type change (old network: %s)",
                    oldNetwork == null ? "NONE" : oldNetwork.getTypeName()
            ));
        }

        networkInfo = newNetwork;
        if (enable()) {
            // Can return false (via earlier InitializationException thrown by NetworkAddressFactory) if
            // no bindable network address found!
            Log.i(TAG, String.format(
                    "Enabled router on network type change (new network: %s)",
                    newNetwork == null ? "NONE" : newNetwork.getTypeName()
            ));
        }
    }

    /**
     * Handles errors when network has been switched, during reception of
     * network switch broadcast. Logs a warning by default, override to
     * change this behavior.
     */
    protected void handleRouterExceptionOnNetworkTypeChange(RouterException ex) {
        Throwable cause = Exceptions.unwrap(ex);
        if (cause instanceof InterruptedException) {
            Log.i(TAG, "Router was interrupted: " + ex, cause);
        } else {
            Log.w(TAG, "Router error on network change: " + ex, ex);
        }
    }

    class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
                return;

            displayIntentInfo(intent);

            NetworkInfo newNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);

            // When Android switches WiFI => MOBILE, sometimes we may have a short transition
            // with no network: WIFI => NONE, NONE => MOBILE
            // The code below attempts to make it look like a single WIFI => MOBILE
            // transition, retrying up to 3 times getting the current network.
            //
            // Note: this can block the UI thread for up to 3s
            if (networkInfo != null && newNetworkInfo == null) {
                for (int i = 1; i <= 3; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    Log.w(TAG, String.format(
                            "%s => NONE network transition, waiting for new network... retry #%d",
                            networkInfo.getTypeName(), i
                    ));
                    newNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);
                    if (newNetworkInfo != null)
                        break;
                }
            }

            if (isSameNetworkType(networkInfo, newNetworkInfo)) {
                Log.i(TAG, "No actual network change... ignoring event!");
            } else {
                try {
                    onNetworkTypeChange(networkInfo, newNetworkInfo);
                } catch (RouterException ex) {
                    handleRouterExceptionOnNetworkTypeChange(ex);
                }
            }
        }

        protected boolean isSameNetworkType(NetworkInfo network1, NetworkInfo network2) {
            if (network1 == null && network2 == null)
                return true;
            if (network1 == null || network2 == null)
                return false;
            return network1.getType() == network2.getType();
        }

        protected void displayIntentInfo(Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            Log.i(TAG, "Connectivity change detected...");
            Log.i(TAG, "EXTRA_NO_CONNECTIVITY: " + noConnectivity);
            Log.i(TAG, "EXTRA_REASON: " + reason);
            Log.i(TAG, "EXTRA_IS_FAILOVER: " + isFailover);
            Log.i(TAG, "EXTRA_NETWORK_INFO: " + (currentNetworkInfo == null ? "none" : currentNetworkInfo));
            Log.i(TAG, "EXTRA_OTHER_NETWORK_INFO: " + (otherNetworkInfo == null ? "none" : otherNetworkInfo));
            Log.i(TAG, "EXTRA_EXTRA_INFO: " + intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
        }

    }
}
