package com.erm.integralwall.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONObject;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

	public static void installApp(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 设置目标应用安装包路径
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	public static boolean isValidApk(String path) {
		if (null == path)
			return false;
		/** .apk占4位长度 */
		if (path.length() < 4)
			return false;
		return path.endsWith(".apk");
	}

	public static boolean isApkExist(String path) {

		if (TextUtils.isEmpty(path))
			return false;

		File file = new File(path);
		return file.exists();
	}

	public static String transitionObj2JsonString(Map<String, String> map) {
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toString();
	}


	public static String getTopRunningPkgNameAboveAndroidL2(Context context,
			long time_ms) {

		// 通过Android 5.0 之后提供的新api来获取最近一段时间内的应用的相关信息
		String topPackageName = null;

		if (Build.VERSION.SDK_INT >= 21) {

			try {
				// 根据最近time_ms毫秒内的应用统计信息进行排序获取当前顶端的包名
				long time = System.currentTimeMillis();
				UsageStatsManager usage = (UsageStatsManager) context
						.getSystemService("usagestats");
				List<UsageStats> usageStatsList = usage.queryUsageStats(
						UsageStatsManager.INTERVAL_BEST, time - time_ms, time);
				if (usageStatsList != null && usageStatsList.size() > 0) {
					SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
					for (UsageStats usageStats : usageStatsList) {
						runningTask.put(usageStats.getLastTimeUsed(),
								usageStats);
					}
					if (runningTask.isEmpty()) {
						return null;
					}
					topPackageName = runningTask.get(runningTask.lastKey())
							.getPackageName();
					Log.i("test", "##当前顶端应用包名:" + topPackageName);
				}
			}

			catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return topPackageName;
	}
	
	 public static  boolean hasEnable(Context context){
	     if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){   // 如果大于等于5.0 再做判断
	         long ts = System.currentTimeMillis();
	         UsageStatsManager usageStatsManager=(UsageStatsManager)context.getSystemService(Service.USAGE_STATS_SERVICE);
	         List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
	         if (queryUsageStats == null || queryUsageStats.isEmpty()) {
	             return false;
	         }
	     }
	     return true;
	 }
}
