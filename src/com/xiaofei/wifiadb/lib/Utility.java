package com.xiaofei.wifiadb.lib;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.Process;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Utility {

	private final static String TAG = "wifiadb.utility";

	private static int configPort = 5555;

	// adb wifi port, default 5555
	public static int getPort() {
		return configPort;
	}

	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	public static String getIp(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			String sAddr = int2ip(i);
			Log.e(TAG, new Exception().getStackTrace()[0].toString() + sAddr);
			return sAddr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

		if (state == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean getAdbdStatus() {
		int lineCount = 0;

		try {
			Process process = Runtime.getRuntime().exec("ps | grep adbd");
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String str = input.readLine();

			while (str != null) {
				lineCount++;
				str = input.readLine();
			}

			if (lineCount >= 2) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static int execSuCommand(List<String> inputs) {
		int exitValue = 0;
		Process p;

		try {
			p = Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(p.getOutputStream());

			for (String input : inputs) {
				os.writeBytes(input);
			}
			os.writeBytes("exit\n");
			os.flush();

			p.waitFor();

			exitValue = p.exitValue();

			return exitValue;
		} catch (Exception e) {
			e.printStackTrace();
			exitValue = -255;

			return exitValue;
		}
	}

	public static int haveRoot() {
		int exitValue = 0;

		List<String> inputs = new ArrayList<String>();

		exitValue = execSuCommand(inputs);

		return exitValue;
	}

	// set adb wifi service
	public static int setWifiAdbStatus(boolean status) {
		int exitValue = 0;

		List<String> inputs = new ArrayList<String>();
		inputs.add("setprop service.adb.tcp.port " + String.valueOf(getPort()) + "\n");
		inputs.add("stop adbd\n");
		if (status) {
			inputs.add("start adbd\n");
		}

		exitValue = execSuCommand(inputs);

		return exitValue;
	}
}
