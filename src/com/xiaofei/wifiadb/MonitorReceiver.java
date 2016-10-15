package com.xiaofei.wifiadb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MonitorReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());

		if (intent.getAction().equals(com.xiaofei.wifiadb.MonitorService.start)) {
			Log.e(TAG, new Exception().getStackTrace()[0].toString());
			context.startService(new Intent(context, MonitorService.class));
		} else if (intent.getAction().equals(
				com.xiaofei.wifiadb.MonitorService.stop)) {
			Log.e(TAG, new Exception().getStackTrace()[0].toString());
			context.stopService(new Intent(context, MonitorService.class));
		}

	}

}
