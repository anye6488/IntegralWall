package com.erm.integralwall.core.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.AppTaskMananger;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.Utils;
import com.erm.integralwall.core.net.IResponseListener;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class SdkService extends Service {
	private Timer mTimer;
	public static final int FOREGROUND_ID = 0;
	public static final int TipTime = 10;
	private static final String action = "com.erm.task";

	/**
	 * 计时器
	 */
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			SdkTask sdkTask = new SdkTask(this);
			mTimer.scheduleAtFixedRate(sdkTask, 0L, 1000L);

		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		startTimer();
		startForeground(FOREGROUND_ID, new Notification());
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	/**
	 * 销毁是停止计算
	 */
	@Override
	public void onDestroy() {
		mTimer.cancel();
		mTimer.purge();
		mTimer = null;
		// releaseWakeLock();
		super.onDestroy();
	}

	class SdkTask extends TimerTask {
		private Context mContext;
		private Notification mNotification;
		private NotificationManager mManager;
		private Handler handler = new Handler();

		public SdkTask(Context context) {
			this.mContext = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 5.0系统的拿包不一样
			// 版本1.0.8加上
			String packageName = "";
			packageName = Utils.Istoppackagenull(getApplicationContext());
			if (packageName == null || packageName.trim().equals("")) {
				packageName = ActivityCacheUtils.getInstance()
						.getLatestPackName();
			}

			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(
					packageName);
			if (null == adInfo) {
				String latestPackName = ActivityCacheUtils.getInstance()
						.getLatestPackName();
				if (packageName != null && !packageName.equals(latestPackName)) {
					AdInfo ad = ActivityCacheUtils.getInstance().getAdInfo(
							latestPackName);
					// 若未完成任务，即退出任务，则提示未完成
					if (ad != null && ad.isAlertFlag()) {
						ad.setAlertFlag(false);// 提示之后，不再提示
						if (ad.isRegister()) {
							onHint("该应用《" + ad.getAppName() + "》需注册，完成后立即获得奖励");
						} else {
							onHint("真可惜，《" + ad.getAppName()
									+ "》的奖励还没得到，请再多用会吧, 剩余 '"
									+ (ad.getTaskTime() - ad.getExeTime())
									+ "'秒！");
						}
					}
				}
				return;// 没有相应监控包信息，则返回，说明不是积分墙上的app
			} else {
				// 有监控包信息，收集app的活动Activity轨迹
				if (Build.VERSION.SDK_INT <21) {
					String className = getCurrentActivityName19(mContext);
					ActivityCacheUtils.getInstance()
							.set(packageName, className);
				}
				ActivityCacheUtils.getInstance().setLatestPackName(
						adInfo.getPackageName());
				ActivityCacheUtils.getInstance()
						.setLatestAdId(adInfo.getAdId());
			}
			adInfo.setAlertFlag(true);// 提示之后，不再提示
			adInfo.setOpenFlag(true);
			boolean isfinish = false;
			int taskTime = adInfo.getTaskTime();
			int trytime = adInfo.getTryTimes();
			// 广告目前已记录的时长
			int Time = adInfo.getExeTime();
			trytime = trytime + 1;
			adInfo.setTryTimes(trytime);

			if (trytime > 0 && trytime % TipTime == 0 || trytime == 1) {
				// 打开提示，若未提示，则提示之
				if (adInfo.isOpenFlag()) {
					adInfo.setOpenFlag(false);// 提示之后，不再提示
					onHint(adInfo.getTaskInfo() + " 即可获得奖励");
				}
			}
			if (Build.VERSION.SDK_INT <21) {
				if (packageName != null
						&& packageName.equals(adInfo.getPackageName())) {
					if (adInfo.isRegister()) {
						isfinish = ActivityCacheUtils.getInstance()
								.checkFinish(packageName);
						if (isfinish) {
							NetManager
									.getInstance()
									.notifyServerWhenTaskFinished(
											String.valueOf(adInfo.getAdId()),
											new IResponseListener<JSONObject>() {
												@Override
												public void onResponse(
														JSONObject t) {
													// TODO Auto-generated
													// method stub
													finishtTip(t);
												}

												@Override
												public void onErrorResponse(
														VolleyError error) {
													// TODO Auto-generated
													// method stub

												}

												@Override
												public void cancel() {
													// TODO Auto-generated
													// method stub

												}

											});

						}
						return;
					}

				}
			}
			if (taskTime > 0) {// 任务时间不能为0
				// 完成时间通知服务器
				if (Time >= Integer.valueOf(taskTime)) {
					NetManager.getInstance().notifyServerWhenTaskFinished(
							String.valueOf(adInfo.getAdId()),
							new IResponseListener<JSONObject>() {
								@Override
								public void onResponse(JSONObject t) {
									// TODO Auto-generated method stub
									finishtTip(t);
								}

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub
								}

								@Override
								public void cancel() {
									// TODO Auto-generated method stub

								}

							});

				} else if (Time < Integer.valueOf(taskTime)) {// 计算时间
					Time = Time + 1;
					adInfo.setExeTime(Time); // 设置任务已体验时间
				}
			}

		}

		private void finishtTip(JSONObject t) {
			try {
				String Title = t.getString("Titile");
				String PackName = t.getString("PackName");
				if (ActivityCacheUtils.getInstance().getAdInfo(PackName) != null) {
					int code = t.getInt("Code");
					String Info = t.getString("Info");
					if (code == 200) {
						setfinishUI(PackName, Title);
						onHint("恭喜您,《" + Title + "》已获得奖励！继续完成下一个任务吧！");
					} else {
						onHint(Info);
					}
					ActivityCacheUtils.getInstance().remove(PackName);
				}
				return;
			} catch (JSONException e) {
				// TODO Auto-generated
				// catch block
				e.printStackTrace();
				onHint("数据问题");
			}
		}

		/**
		 * 完成任务发送广播由开发者设置ui
		 * 
		 * @param packname
		 *            完成任务的包名
		 * @param title
		 *            完成任务的app
		 */
		private void setfinishUI(String packname, String title) {
			Intent intent = new Intent(action);
			intent.putExtra("packname", packname);
			intent.putExtra("title", title);
			sendBroadcast(intent);
		}

		/**
		 * 弹出黑框提示
		 */
		public void onHint(final String hint) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					try {
						Toast.makeText(getApplicationContext(), hint,
								Toast.LENGTH_LONG).show();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	/**
	 * 5.0以下系统
	 * 
	 * @param context
	 * @return
	 */
	public String getCurrentActivityName19(Context context) {
		String classname = "";
		try {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			classname = topActivity.getClassName();
		} catch (Exception e) {
			// TODO: handle exception
			classname = "";
		}
		return classname;
	}

}
