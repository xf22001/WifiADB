package com.xiaofei.wifiadb;

import com.xiaofei.wifiadb.lib.WifiStateReceiver;

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
	private WifiStateReceiver wifiStateReceiver;

	@Override
	public void onCreate() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onStart(intent, startId);

		if (this.wifiStateReceiver == null) {
			this.wifiStateReceiver = new WifiStateReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					Log.e(TAG, new Exception().getStackTrace()[0].toString());

					super.onReceive(context, intent);

					Intent wifiADB = new Intent(context, WifiADB.class);
					wifiADB.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// 用Bundle携带数据
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					bundle.putString("from", "service");
					intent.putExtras(bundle);
					context.startActivity(wifiADB);
				}
			};
			registerReceiver(wifiStateReceiver, new IntentFilter(
					WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
		}

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

		flags = START_STICKY;
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
		stopForeground(true);

		if (wifiStateReceiver != null) {
			unregisterReceiver(wifiStateReceiver);
			wifiStateReceiver = null;
		}

		super.onDestroy();
	}
}
