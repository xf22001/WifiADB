package com.xiaofei.wifiadb.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		final String action = intent.getAction();

		if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			boolean connected = intent.getBooleanExtra(
					WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

			if (connected) {
				try {
					int tryTimes = 100;

					while (!Utility.isWifiConnected(context)) {
						Thread.sleep(10);
					}

					while (tryTimes > 0) {
						String ip = Utility.getIp(context);

						if (ip != null) {
							break;
						} else {
							Thread.sleep(10);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
			}
		}
	}
}
