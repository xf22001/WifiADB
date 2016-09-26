package com.xiaofei.wifiadb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaofei.wifiadb.lib.Utility;

public class MainActivity extends Activity {
	private final static String TAG = "wifiadb";

	private LinearLayout toggleButton;
	private TextView hint, toggleLeft, toggleRight;
	private WifiStateReceiver wifiStateReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initVal();
		init();
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(wifiStateReceiver);
		super.onDestroy();
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
		Log.e(TAG, new Exception().getStackTrace()[0].toString() + Utility.getIp(this));

		if (!Utility.isWifiConnected(this)) {
			Log.e(TAG, new Exception().getStackTrace()[0].toString());
			disableToggle();
		}

		updateToggleStatus();
	}

	private void updateToggleStatus() {
		boolean toggleStatus = Utility.getAdbdStatus();
		setToggleStatus(toggleStatus);
		toggleButton.setOnClickListener(null);
		toggleButton.setOnClickListener(new ToggleClickListener());
	}

	private void disableToggle() {
		Utility.setWifiAdbStatus(false);
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

						while (!Utility.isWifiConnected(MainActivity.this)) {
							Thread.sleep(10);
						}

						while (tryTimes > 0) {
							String ip = Utility.getIp(MainActivity.this);

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
					disableToggle();
				}

				updateToggleStatus();
			}
		}
	}

	private class ToggleClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Utility.isWifiConnected(MainActivity.this)) {
				// try switch
				boolean toggleStatusLocal = !Utility.getAdbdStatus();
				boolean ret = Utility.setWifiAdbStatus(toggleStatusLocal);

				if (ret) {
					// switch successfully
					if (toggleStatusLocal) {
						Toast.makeText(MainActivity.this, "wifi adbd started!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, "wifi adbd stopped!", Toast.LENGTH_SHORT).show();
					}
				} else {
					// failed
					Toast.makeText(MainActivity.this, "没有root权限！", Toast.LENGTH_SHORT).show();
				}
			} else {
				disableToggle();
			}

			updateToggleStatus();
		}
	}

	private void setToggleStatus(boolean status) {
		if (!status) {
			toggleLeft.setText("关");

			if (Utility.isWifiConnected(this)) {
				toggleLeft.setBackgroundColor(getResources().getColor(R.color.blue_holo));
				hint.setText("");
			} else {
				toggleLeft.setBackgroundColor(getResources().getColor(R.color.gray_dark));
				hint.setText("没有WIFI连接!");
			}

			toggleRight.setText("");
			toggleRight.setBackgroundColor(getResources().getColor(R.color.gray_light));
		} else {
			toggleRight.setText("开");

			if (Utility.isWifiConnected(this)) {
				toggleRight.setBackgroundColor(getResources().getColor(R.color.blue_holo));
				hint.setText("adb connect " + Utility.getIp(this) + ":" + String.valueOf(Utility.getPort()));
			} else {
				toggleRight.setBackgroundColor(getResources().getColor(R.color.gray_dark));
				hint.setText("没有root权限!");
			}

			toggleLeft.setText("");
			toggleLeft.setBackgroundColor(getResources().getColor(R.color.gray_light));
		}
	}
}
