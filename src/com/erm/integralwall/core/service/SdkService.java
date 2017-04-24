package com.erm.integralwall.core.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
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
import android.widget.Toast;

public class SdkService extends Service{
	private Timer mTimer;
	public static final int FOREGROUND_ID = 0;
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
		startForeground(FOREGROUND_ID, new Notification());
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		startTimer();
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
		//releaseWakeLock();
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
			//5.0系统的拿包不一样
			//版本1.0.8加上
			String packageName = "";
			if (Build.VERSION.SDK_INT > 19) {
				packageName = getCurrentPkgName20(mContext);
			} else {
				packageName = getCurrentPkgName19(mContext);
			}							
			//Android5.1.1的版本，拿不到包名
			if(packageName==null  ||  packageName.trim().equals("")){
				
				return;
				
			}else{
				AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
				if(null == adInfo){
					String latestPackName = ActivityCacheUtils.getInstance().getLatestPackName();
					if(packageName!=null && !packageName.equals(latestPackName)){
						AdInfo ad = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);
						//若未完成任务，即退出任务，则提示未完成
						if(ad !=null && ad.isAlertFlag()){
							ad.setAlertFlag(false);//提示之后，不再提示
							if(ad.isRegister()){
								onHint("该应用《"+ad.getAppName()+"》需注册，完成后立即获得奖励");
							}else{
								onHint("真可惜，《"+ad.getAppName()+"》的奖励还没得到，请再多用会吧, 剩余 '"+(ad.getTaskTime()-ad.getExeTime())+"'秒！");
							}
						}
					}
					return ;//没有相应监控包信息，则返回，说明不是积分墙上的app
				}else{
					//有监控包信息，收集app的活动Activity轨迹
					if (Build.VERSION.SDK_INT <= 19) {
						String className = getCurrentActivityName19(mContext);				
						ActivityCacheUtils.getInstance().set(packageName,className);					
					}
					ActivityCacheUtils.getInstance().setLatestPackName(adInfo.getPackageName());
					ActivityCacheUtils.getInstance().setLatestAdId(adInfo.getAdId());
				}
				adInfo.setAlertFlag(true);//提示之后，不再提示
				boolean isfinish = false;
				int taskTime = adInfo.getTaskTime();
				//广告目前已记录的时长
				int Time = adInfo.getExeTime();
				if (Build.VERSION.SDK_INT <= 19) {
					if(packageName!=null && packageName.equals(adInfo.getPackageName())){				
						if(adInfo.isRegister()){
							isfinish = ActivityCacheUtils.getInstance().checkFinish(packageName);
							if(isfinish){
										ActivityCacheUtils.getInstance().remove(packageName);
										onHint("恭喜您,《" + adInfo.getAppName() + "》已获得奖励！继续完成下一个任务吧！");
										return ;
									}
								}
							
							}
						}
				//打开提示，若未提示，则提示之
				if(adInfo.isOpenFlag()){
					adInfo.setOpenFlag(false);//提示之后，不再提示
					onHint(adInfo.getTaskInfo()+" 即可获得奖励");
				}
				if (taskTime> 0) {//任务时间不能为0
					//完成时间通知服务器
					if (Time >= Integer.valueOf(taskTime)) {
						onHint("恭喜您,《" + adInfo.getAppName() + "》已获得奖励！继续完成下一个任务吧！");
						ActivityCacheUtils.getInstance().remove(packageName); //任务完成，清空缓存记录
								
							}
					else if (Time < Integer.valueOf(taskTime)) {//计算时间
						Time = Time + 1;
						adInfo.setExeTime(Time); //设置任务已体验时间
					}
				}
				}	
			
		
		}
		
		
		/**
		 * 弹出黑框提示
		 */
		public void onHint(final String hint) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					try{
						Toast.makeText(getApplicationContext(), hint, 3000).show();
					   
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}

		}
	

		/**
		 * 5.0以下系统
		 * @param context
		 * @return
		 */
		public String getCurrentPkgName19(Context context) {
			ActivityManager mActivityManager = (ActivityManager) context.getSystemService("activity");
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			return topActivity.getPackageName();
		}
		
		/**
		 * 5.0以下系统
		 * @param context
		 * @return
		 */
		public String getCurrentActivityName19(Context context) {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService("activity");
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			return topActivity.getClassName();
		}
		/**
		 * 5.0以上系统
		 * @param context
		 * @return
		 */
		public String getCurrentPkgName20(Context context) {
			ActivityManager.RunningAppProcessInfo currentInfo = null;
			Field field = null;
			int START_TASK_TO_FRONT = 2;
			String pkgName = "";
			try {
				field = ActivityManager.RunningAppProcessInfo.class
						.getDeclaredField("processState");			
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List appList = am.getRunningAppProcesses();
				// ActivityManager.RunningAppProcessInfo app : appList
				if(appList != null){
					for (int i = 0; i < appList.size(); i++) {
						ActivityManager.RunningAppProcessInfo app = (RunningAppProcessInfo) appList
								.get(i);
						if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
							Integer state = null;
							try {
								state = field.getInt(app);
								if (state != null && state == START_TASK_TO_FRONT) {
									currentInfo = app;
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}						
						}
					}
				}
				if (currentInfo != null) {
					pkgName = currentInfo.processName;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			//pkgName =  printForegroundTask(context) ;
			return pkgName;
		}	
		
		
	}