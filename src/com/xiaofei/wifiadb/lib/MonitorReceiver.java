package com.xiaofei.wifiadb.lib;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MonitorReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();
	private final static boolean isServiceRunning = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());

		if (intent.getAction().equals(MonitorService.start)) {
			if (!getServiceStatus(context, MonitorService.class.getName())) {
				context.startService(new Intent(context, MonitorService.class));
			}
		} else if (intent.getAction().equals(MonitorService.stop)) {
			if (getServiceStatus(context, MonitorService.class.getName())) {
				context.stopService(new Intent(context, MonitorService.class));
			}
		}

	}

	boolean getServiceStatus(Context context, String className) {
		boolean isServiceRunning = false;

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (RunningServiceInfo info : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (className.equals(info.service.getClassName())) {
				isServiceRunning = true;
				break;
			}
		}

		return isServiceRunning;
	}

}
