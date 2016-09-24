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
    private final static String TAG = "adbwifi";

    private LinearLayout toggleButton;
    private TextView hint, toggleLeft, toggleRight;
    private boolean toggleStatus = false;
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
        registerReceiver(wifiStateReceiver, new IntentFilter(
                             WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
    }

    private void init() {
        Log.e(TAG,
              new Exception().getStackTrace()[0].toString()
              + Utility.getIp(this));

        if (Utility.isWifiConnected(this)) {
            Log.e(TAG, new Exception().getStackTrace()[0].toString());
            updateToggleStatus();
        } else {
            Log.e(TAG, new Exception().getStackTrace()[0].toString());
            disableToggle();
        }
    }

    private void updateToggleStatus() {
        toggleStatus = Utility.getAdbdStatus();
        setToggleStatus(toggleStatus);
        toggleButton.setOnClickListener(null);
        toggleButton.setOnClickListener(new ToggleClickListener());
    }

    private void disableToggle() {
        Utility.setWifiAdbStatus(false);
        toggleStatus = false;
        setToggleStatus(toggleStatus);
        hint.setText("wifi is not connected");
        toggleButton.setOnClickListener(null);
    }

    private class WifiStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                boolean connected = intent.getBooleanExtra(
                                        WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);

                if (connected) {
                    try {
                        int tryTimes = 100;

                        // while (!Utility.isWifiConnected(MainActivity.this)) {
                        // Thread.sleep(10);
                        // }

                        while (tryTimes > 0) {
                            String ip = Utility.getIp(MainActivity.this);

                            if (ip != null) {
                                break;
                            } else {
                                Thread.sleep(10);
                            }
                        }

                        updateToggleStatus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    disableToggle();
                }

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
                        Toast.makeText(MainActivity.this,
                                       "adb wifi service started", Toast.LENGTH_SHORT)
                        .show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                       "adb wifi service stopped", Toast.LENGTH_SHORT)
                        .show();
                    }

                    updateToggleStatus();
                } else {
                    // failed
                    Toast.makeText(MainActivity.this, "adb wifi service error",
                                   Toast.LENGTH_SHORT).show();
                }
            } else {
                disableToggle();
            }
        }
    }

    private void setToggleStatus(boolean status) {
        if (!status) {
            toggleLeft.setText("OFF");
            toggleLeft.setBackgroundColor(getResources().getColor(
                                              R.color.gray_dark));
            toggleRight.setText("");
            toggleRight.setBackgroundColor(getResources().getColor(
                                               R.color.gray_light));
            hint.setText("adbd is stopped!");
        } else {
            toggleLeft.setText("");
            toggleLeft.setBackgroundColor(getResources().getColor(
                                              R.color.gray_light));
            toggleRight.setText("ON");
            toggleRight.setBackgroundColor(getResources().getColor(
                                               R.color.blue_holo));

            hint.setText("adb connect " + Utility.getIp(this) + ":"
                         + String.valueOf(Utility.getPort()));
        }
    }
}
