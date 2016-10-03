package com.xiaofei.wifiadb;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaofei.wifiadb.lib.Utility;

public class WifiADB extends Activity {
	private final static String TAG = "wifiadb";

	private LinearLayout toggleButton;

	private TextView hint, toggleLeft, toggleRight;
	private WifiStateReceiver wifiStateReceiver;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initVal();
		init();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
		updateToggleStatus(false);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(wifiStateReceiver);
		super.onDestroy();
		Log.e(TAG, new Exception().getStackTrace()[0].toString());
	}

	private void initVal() {
		this.toggleButton = (LinearLayout) findViewById(R.id.toggleLayout);
		this.hint = (TextView) findViewById(R.id.hint);
		this.toggleLeft = (TextView) findViewById(R.id.toggleLeft);
		this.toggleRight = (TextView) findViewById(R.id.toggleRight);

		this.wifiStateReceiver = new WifiStateReceiver();
		registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
	}

	private void init() {
		updateToggleStatus(false);
	}

	private class WifiStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

				if (connected) {
					try {
						int tryTimes = 100;

						while (!Utility.isWifiConnected(WifiADB.this)) {
							Thread.sleep(10);
						}

						while (tryTimes > 0) {
							String ip = Utility.getIp(WifiADB.this);

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

				updateToggleStatus(false);
			}
		}
	}

	private class ToggleClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			boolean success = updateToggleStatus(true);

			if (!success) {
				Toast.makeText(WifiADB.this, "Error!", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(WifiADB.this, "exitValue: " + exitValue, Toast.LENGTH_SHORT).show();
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
						Toast.makeText(WifiADB.this, "exitValue: " + exitValue, Toast.LENGTH_SHORT).show();
					}
				} else {
					success = true;
				}

				if (toggleStatusLocal) {
					hintText = "adb connect " + Utility.getIp(this) + ":" + String.valueOf(Utility.getPort());
				}

				toggleButton.setOnClickListener(new ToggleClickListener());
			} else {
				if (toggleStatusLocal) {
					exitValue = Utility.setWifiAdbStatus(false);
					if (exitValue == 0) {
						toggleStatusLocal = false;
						success = true;
					} else {
						Toast.makeText(WifiADB.this, "exitValue: " + exitValue, Toast.LENGTH_SHORT).show();
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
			toggleLeft.setBackgroundColor(getResources().getColor(R.color.gray_light));

			toggleRight.setText("开");

			if (enableButton) {
				toggleRight.setBackgroundColor(getResources().getColor(R.color.blue_holo));
			} else {
				toggleRight.setBackgroundColor(getResources().getColor(R.color.gray_dark));
			}

		} else {
			toggleLeft.setText("关");

			if (enableButton) {
				toggleLeft.setBackgroundColor(getResources().getColor(R.color.blue_holo));
			} else {
				toggleLeft.setBackgroundColor(getResources().getColor(R.color.gray_dark));
			}

			toggleRight.setText("");
			toggleRight.setBackgroundColor(getResources().getColor(R.color.gray_light));
		}

		return success;
	}
}
