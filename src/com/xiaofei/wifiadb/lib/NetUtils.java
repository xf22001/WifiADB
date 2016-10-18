package com.xiaofei.wifiadb.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetUtils {
	private final String TAG = this.getClass().getName();
	Context mContext;

	private NetUtils() {
	}

	public NetUtils(Context context) {
		mContext = context;
	}

	public String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	public String getIp() {
		try {
			WifiManager wifiManager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			String sAddr = int2ip(i);
			Log.e(TAG, new Exception().getStackTrace()[0].toString() + sAddr);
			return sAddr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		if (state == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}
}
