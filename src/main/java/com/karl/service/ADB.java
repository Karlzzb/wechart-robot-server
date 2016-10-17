package com.karl.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class ADB {
	private static final Logger LOGGER = LoggerFactory.getLogger(ADB.class);

	private AndroidDebugBridge mAndroidDebugBridge;

	public boolean initialize() {
		boolean success = true;
		// String adbLocation = System
		// .getProperty("com.android.screenshot.bindir");

		if (success) {
			String adbLocation = new File(System.getProperty("java.class.path"))
					.getAbsoluteFile().getParentFile().toString();
			if (adbLocation != null && !adbLocation.isEmpty()) {
				adbLocation += File.separator + "/adb/adb.exe";
			}else { adbLocation = "adb"; }
			 
			LOGGER.debug("adbLocation=" + adbLocation);
			AndroidDebugBridge.initIfNeeded(false);
			mAndroidDebugBridge = AndroidDebugBridge.createBridge(adbLocation,
					true);
			if (mAndroidDebugBridge == null) {
				success = false;
			}
		}

		if (success) {
			int count = 0;
			while (mAndroidDebugBridge.hasInitialDeviceList() == false) {
				try {
					Thread.sleep(100);
					count++;
				} catch (InterruptedException e) {
				}
				if (count > 100) {
					success = false;
					break;
				}
			}
		}

		if (!success) {
			terminate();
		}

		return success;
	}

	public void terminate() {
		AndroidDebugBridge.terminate();
	}

	public IDevice[] getDevices() {
		IDevice[] devices = null;
		if (mAndroidDebugBridge != null) {
			devices = mAndroidDebugBridge.getDevices();
		}
		return devices;
	}
}
