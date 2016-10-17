package com.xiaofei.wifiadb;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaofei.wifiadb.lib.Utility;
import com.xiaofei.wifiadb.lib.WifiStateReceiver;

public class WifiADB extends Activity {
	private final String TAG = this.getClass().getName();

	private LinearLayout toggleButton;

	private TextView hint, toggleLeft, toggleRight;
	private WifiStateReceiver wifiStateReceiver;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null && bundle.getString("from") != null) {
			Log.e(TAG, new Exception().getStackTrace()[0].toString());
		}

		initVal();
		init();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onStart();
		if (wifiStateReceiver != null) {
			registerReceiver(wifiStateReceiver, new IntentFilter(
					WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
		}
		sendBroadcast(new Intent(com.xiaofei.wifiadb.MonitorService.stop));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onResume();
		updateToggleStatus(false);
	}

	@Override
	protected void onPostResume() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onPostResume();
	}

	@Override
	public void onBackPressed() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		// TODO Auto-generated method stub
		super.onStop();
		sendBroadcast(new Intent(com.xiaofei.wifiadb.MonitorService.start));
		if (wifiStateReceiver != null) {
			unregisterReceiver(wifiStateReceiver);
		}
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		super.onDestroy();
	}

	private void initVal() {
		toggleButton = (LinearLayout) findViewById(R.id.toggleLayout);
		hint = (TextView) findViewById(R.id.hint);
		toggleLeft = (TextView) findViewById(R.id.toggleLeft);
		toggleRight = (TextView) findViewById(R.id.toggleRight);

		wifiStateReceiver = new WifiStateReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e(TAG, new Exception().getStackTrace()[0].toString());
				super.onReceive(context, intent);
				Toast.makeText(WifiADB.this, "wifi state changed!",
						Toast.LENGTH_SHORT).show();

				updateToggleStatus(false);
			}
		};
	}

	private void init() {
		updateToggleStatus(false);
	}

	private class ToggleClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			boolean success = updateToggleStatus(true);

			if (!success) {
				Toast.makeText(WifiADB.this, "Error!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private boolean updateToggleStatus(boolean doToggle) {
		int exitValue = Utility.haveRoot();
		boolean wifiConnected = Utility.isWifiConnected(this);
		boolean toggleStatus = Utility.getAdbdStatus();

		boolean toggleStatusLocal = toggleStatus;
		boolean enableButton = false;
		String hintText = "";

		boolean success = false;

		toggleButton.setOnClickListener(null);

		if (exitValue != 0) {
			Toast.makeText(this, "exitValue: " + exitValue, Toast.LENGTH_SHORT)
					.show();
			hintText = "没有root权限";
		} else {
			if (wifiConnected) {
				enableButton = true;

				if (doToggle) {
					exitValue = Utility.setWifiAdbStatus(!toggleStatus);

					if (exitValue == 0) {
						toggleStatusLocal = !toggleStatus;
						success = true;
					} else {
						Toast.makeText(this, "exitValue: " + exitValue,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					success = true;
				}

				if (toggleStatusLocal) {
					hintText = "adb connect " + Utility.getIp(this) + ":"
							+ String.valueOf(Utility.getPort());
				}

				toggleButton.setOnClickListener(new ToggleClickListener());
			} else {
				if (toggleStatusLocal) {
					exitValue = Utility.setWifiAdbStatus(false);

					if (exitValue == 0) {
						toggleStatusLocal = false;
						success = true;
					} else {
						Toast.makeText(this, "exitValue: " + exitValue,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					success = true;
				}

				hintText = "没有WIFI连接";
			}
		}

		hint.setText(hintText);

		if (toggleStatusLocal) {
			toggleLeft.setText("");
			toggleLeft.setBackgroundColor(getResources().getColor(
					R.color.gray_light));

			toggleRight.setText("开");

			if (enableButton) {
				toggleRight.setBackgroundColor(getResources().getColor(
						R.color.blue_holo));
			} else {
				toggleRight.setBackgroundColor(getResources().getColor(
						R.color.gray_dark));
			}

		} else {
			toggleLeft.setText("关");

			if (enableButton) {
				toggleLeft.setBackgroundColor(getResources().getColor(
						R.color.blue_holo));
			} else {
				toggleLeft.setBackgroundColor(getResources().getColor(
						R.color.gray_dark));
			}

			toggleRight.setText("");
			toggleRight.setBackgroundColor(getResources().getColor(
					R.color.gray_light));
		}

		return success;
	}
}
