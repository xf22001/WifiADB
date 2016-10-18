package com.xiaofei.wifiadb.lib;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class NetStateFilter extends IntentFilter {
	public NetStateFilter() {
		// addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		// addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		// addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		// addAction(WifiManager.RSSI_CHANGED_ACTION);
	}
}
