package com.xiaofei.wifiadb.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetStateReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();
	private String mText;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		final String action = intent.getAction();

		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			mText = "\nNETWORK_STATE_CHANGED_ACTION:";

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mText += "\nnetworkInfo:";
			mText += networkInfo;

			mText += "\nnetworkInfo.getDetailedState():";
			mText += networkInfo.getDetailedState();

			Log.e(TAG, new Exception().getStackTrace()[0].toString() + mText);
		} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			mText = "\nWIFI_STATE_CHANGED_ACTION:";
			int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN);
			mText += "\nstate:";

			switch (state) {
			case WifiManager.WIFI_STATE_DISABLING:
				mText += "WifiManager.WIFI_STATE_DISABLING";
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				mText += "WifiManager.WIFI_STATE_DISABLED";
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				mText += "WifiManager.WIFI_STATE_ENABLING";
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				mText += "WifiManager.WIFI_STATE_ENABLED";
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				mText += "WifiManager.WIFI_STATE_UNKNOWN";
				break;
			default:
				break;
			}

			Log.e(TAG, new Exception().getStackTrace()[0].toString() + mText);
		} else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
			mText = "\nSUPPLICANT_STATE_CHANGED_ACTION:";
			SupplicantState supplicantState = (SupplicantState) intent
					.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

			mText += "\nsupplicantState:";
			mText += supplicantState;

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();

			mText += "\nwifiInfo:";
			mText += wifiInfo;

			Log.e(TAG, new Exception().getStackTrace()[0].toString() + mText);
		} else if (action
				.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			mText = "\nSUPPLICANT_CONNECTION_CHANGE_ACTION:";
			boolean connected = intent.getBooleanExtra(
					WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

			mText += "\nconnected:";
			mText += connected;
			Log.e(TAG, new Exception().getStackTrace()[0].toString() + mText);
		} else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
			mText = "\nRSSI_CHANGED_ACTION:";
			int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);

			mText += "\nrssi:";
			mText += rssi;
			Log.e(TAG, new Exception().getStackTrace()[0].toString() + mText);
		}
	}

	public String getContent() {
		return mText;
	}
}
