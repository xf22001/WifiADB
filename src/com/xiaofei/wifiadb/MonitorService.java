package com.xiaofei.wifiadb;

import com.xiaofei.wifiadb.lib.WifiStateReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MonitorService extends Service {
	private final String TAG = this.getClass().getName();
	static final String start = "com.xiaofei.wifiadb.MonitorService.start";
	static final String stop = "com.xiaofei.wifiadb.MonitorService.stop";
	private NotificationManager mNM;
	private WifiStateReceiver wifiStateReceiver;
	private Intent wifiADB;

	@Override
	public void onCreate() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		/*
		 * 这里返回状态有三个值，分别是:
		 * 1、START_STICKY：当服务进程在运行时被杀死，系统将会把它置为started状态，但是不保存其传递的Intent对象
		 * ，之后，系统会尝试重新创建服务; 2、START_NOT_STICKY：当服务进程在运行时被杀死，并且没有新的Intent对象传递过来的话
		 * ，系统将会把它置为started状态， 但是系统不会重新创建服务，直到startService(Intent
		 * intent)方法再次被调用; 3、START_REDELIVER_INTENT：当服务进程在运行时被杀死，它将会在隔一段时间后自动创建
		 * ，并且最后一个传递的Intent对象将会再次传递过来。
		 */

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Bundle bundle = new Bundle();
		bundle.putString("from", "service");
		wifiADB = new Intent(this, WifiADB.class)
				.addFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtras(
						bundle);

		wifiStateReceiver = new WifiStateReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e(TAG, new Exception().getStackTrace()[0].toString());

				super.onReceive(context, intent);

				if (wifiADB != null) {
					context.startActivity(wifiADB);
				}
			}
		};

		handleCommand(intent);

		flags |= START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		return null;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());

		if (wifiStateReceiver != null) {
			unregisterReceiver(wifiStateReceiver);
			wifiStateReceiver = null;
		}

		if (mNM != null) {
			stopForeground(true);
			// mNM.cancel(R.string.app_name);
		}

		super.onDestroy();
	}

	void handleCommand(Intent intent) {

		// In this sample, we'll use the same text for the ticker and the
		// expanded notification

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent;

		if (wifiADB != null) {
			contentIntent = PendingIntent.getActivity(this, 0, wifiADB, 0);
		} else {
			contentIntent = null;
		}

		CharSequence text = "running!";
		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.app_logo, text,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.app_name), text,
				contentIntent);

		// Send the notification.

		if (mNM != null) {
			// mNM.notify("wifiadb notification tag", R.string.app_name,
			// notification);
			startForeground(R.string.app_name, notification);
		}

		if (wifiStateReceiver != null) {
			registerReceiver(wifiStateReceiver, new IntentFilter(
					WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
		}
	}
}
