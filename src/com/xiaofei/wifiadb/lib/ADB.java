package com.xiaofei.wifiadb.lib;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class ADB {
	private final String TAG = this.getClass().getName();

	private int configPort = 5555;

	// adb wifi port, default 5555
	public int getPort() {
		return configPort;
	}

	public boolean getAdbdStatus() {
		int lineCount = 0;

		try {
			Process process = Runtime.getRuntime().exec("ps | grep adbd");
			InputStreamReader isr = new InputStreamReader(
					process.getInputStream());
			LineNumberReader input = new LineNumberReader(isr);
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

	public int execSuCommand(List<String> inputs) {
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

	public int haveRoot() {
		int exitValue = 0;

		List<String> inputs = new ArrayList<String>();

		exitValue = execSuCommand(inputs);

		return exitValue;
	}

	// set adb wifi service
	public int setWifiAdbStatus(boolean status) {
		int exitValue = 0;

		List<String> inputs = new ArrayList<String>();
		inputs.add("setprop service.adb.tcp.port " + String.valueOf(getPort())
				+ "\n");
		inputs.add("stop adbd\n");

		if (status) {
			inputs.add("start adbd\n");
		}

		exitValue = execSuCommand(inputs);

		return exitValue;
	}
}
