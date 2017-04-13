package com.erm.integralwall.core;

import java.util.Map;

public interface IApkInstalledListener {
	
	/*
	 * 映射:apk的包名和广告ID的映射.
	 */
	Map<String, String> getMapOfPakageAndAdsID();
}
