package com.erm.integralwall.core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.net.IResponseListener;
import com.erm.integralwall.core.service.ActivityCacheUtils;
import com.erm.integralwall.core.service.AdInfo;
import com.erm.integralwall.core.service.SdkService;
import com.erm.integralwall.ui.MainActivity;
import com.erm.integralwall.ui.detail.GetAdsTimeBean;
import com.google.gson.Gson;

/**
 * 任务管理器
 * @author lijun
 *
 */
public class AppTaskMananger {
/**
 * 启动任务
 * @param ID 任务id
 * @param context 
 */
	public static void startID(String ID,final Context context) {

		NetManager.getInstance().fetchTaskTimeByAdsID(ID,
				new IResponseListener<JSONObject>() {

					@Override
					public void onResponse(JSONObject t) {
						// TODO Auto-generated method stub
						try {
							String code = t.getString("Code");
							if (code.equals("200")) {
								String State = t.getString("State");
								if (!State.equals("1")) {
									Gson gson = new Gson();
									GetAdsTimeBean gTimeBean = gson.fromJson(
											t.toString(), GetAdsTimeBean.class);
									startService(gTimeBean.getPackName(),
											Integer.valueOf(gTimeBean
													.getAdsId()), Integer
													.valueOf(gTimeBean
															.getTime()),
											gTimeBean.getTitile(), gTimeBean
													.getRegisterState(),
											gTimeBean.getTaskIntro(),context);
								} else {
									Toast.makeText(context,
											"任务已完成", Toast.LENGTH_SHORT).show();
								}

							} else {
								Toast.makeText(context,
										"广告id有误", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "系统故障",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}

				});

	}

	/**
	 * 
	 * @param packagename
	 *            包名
	 * @param adId
	 *            广告id
	 * @param tasktime
	 *            任务时间
	 * @param appname
	 *            app名字
	 * @param is_register
	 *            注册轨迹
	 * @param task
	 *            任务
	 */
	private static void startService(String packagename, Integer adId, int tasktime,
			String appname, String is_register, String task,Context context) {
		//
		if (Utils.isAppInstalled(context, packagename)) { // 初始化监听数据
			AdInfo adinfo = new AdInfo();
			adinfo.setAdId(adId);
			adinfo.setAppName(appname);
			adinfo.setTaskTime(tasktime);
			adinfo.setPackageName(packagename);
			adinfo.setTaskInfo(task);
			adinfo.setOpenFlag(true); // 任务详情提示
			adinfo.setAlertFlag(true); // 任务未完成提示
			if (!is_register.equals("0") && !is_register.trim().equals("")) {
				adinfo.setRegister(true);
			} else {
				adinfo.setRegister(false);
			}
			// 若为注册，则监听Activity活动路径
			if (adinfo.isRegister()) {
				if (is_register != null && !is_register.trim().equals("")) {
					String[] array = is_register.split(";");
					ArrayList<String> list = new ArrayList<String>();
					for (String str : array) {
						list.add(str);
					}
					adinfo.setActivitys(list);
				}
			}
			if(ActivityCacheUtils.getInstance().get(packagename)==null)
			{
			ActivityCacheUtils.getInstance().addAdInfo(packagename, adinfo);
			}
			ActivityCacheUtils.getInstance().setLatestPackName(packagename); // 最近打开包名
			ActivityCacheUtils.getInstance().setLatestAdId(
					Integer.valueOf(adId)); // 最近打开广告ID
			PackageManager packageManager = context.getPackageManager();
			PackageInfo pi = null;
			try {
				pi = packageManager.getPackageInfo(packagename, 0);

				Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
				resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				resolveIntent.setPackage(pi.packageName);

				List<ResolveInfo> apps = packageManager.queryIntentActivities(
						resolveIntent, 0);

				ResolveInfo ri = apps.iterator().next();
				if (ri != null) {
					String className = ri.activityInfo.name;
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ComponentName cn = new ComponentName(packagename, className);
					intent.setComponent(cn);
					context.startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			opentask(context);
		} else {
			Toast.makeText(context, "沒找到对应的app",
					Toast.LENGTH_SHORT).show();
		}

	}


	/**
	 * 打开任务
	 * @param context
	 */
	public  static void opentask(Context context)
	{
		ActivityCacheUtils.getInstance().setTask_open(true);
		Intent startservice = new Intent(context,
				SdkService.class);
		context.startService(startservice);

	}
//
	/**
	 * 結束所有任務
	 * @param context
	 */
	public  static void cancelTask(Context context)
	{
		ActivityCacheUtils.getInstance().setTask_open(false);
		Intent startservice = new Intent(context,
				SdkService.class);
		context.stopService(startservice);

	}
}
