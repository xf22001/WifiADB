package com.xiaofei.wifiadb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaofei.wifiadb.lib.ADB;
import com.xiaofei.wifiadb.lib.MonitorService;
import com.xiaofei.wifiadb.lib.NetStateFilter;
import com.xiaofei.wifiadb.lib.NetStateReceiver;
import com.xiaofei.wifiadb.lib.NetUtils;

public class WifiADB extends Activity {
	private final String TAG = this.getClass().getName();

	private LinearLayout toggleButton;

	private TextView hint, netInfo, toggleLeft, toggleRight;
	private NetStateReceiver wifiStateReceiver;
	private ADB mADB;
	private NetUtils mNetUtils;
	private boolean mADBRunning = false;

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

		initVal();
		updateToggleStatus(false);
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
			registerReceiver(wifiStateReceiver, new NetStateFilter());
		}
		sendBroadcast(new Intent(MonitorService.stop));
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

		if (mADBRunning) {
			sendBroadcast(new Intent(MonitorService.start));
		}

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
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getString("from") != null) {
			Log.e(TAG, new Exception().getStackTrace()[0].toString());
		}

		toggleButton = (LinearLayout) findViewById(R.id.toggleLayout);
		hint = (TextView) findViewById(R.id.hint);
		netInfo = (TextView) findViewById(R.id.netInfo);
		toggleLeft = (TextView) findViewById(R.id.toggleLeft);
		toggleRight = (TextView) findViewById(R.id.toggleRight);

		wifiStateReceiver = new NetStateReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e(TAG, new Exception().getStackTrace()[0].toString());
				super.onReceive(context, intent);

				updateToggleStatus(false);
			}
		};

		mADB = new ADB();
		mNetUtils = new NetUtils(this);
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
		int exitValue = mADB.haveRoot();
		boolean wifiConnected = mNetUtils.isWifiConnected();
		boolean toggleStatus = mADB.getAdbdStatus();

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
					exitValue = mADB.setWifiAdbStatus(!toggleStatus);

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
					hintText = "adb connect " + mNetUtils.getIp() + ":"
							+ String.valueOf(mADB.getPort());
				}

				toggleButton.setOnClickListener(new ToggleClickListener());
			} else {
				if (toggleStatusLocal) {
					exitValue = mADB.setWifiAdbStatus(false);

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
		// netInfo.setText(wifiStateReceiver.getContent());

		mADBRunning = toggleStatusLocal;

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
