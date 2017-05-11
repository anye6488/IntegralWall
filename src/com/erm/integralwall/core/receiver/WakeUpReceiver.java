package com.erm.integralwall.core.receiver;

import com.erm.integralwall.core.service.ActivityCacheUtils;
import com.erm.integralwall.core.service.SdkService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WakeUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) { // 锁屏
			if (ActivityCacheUtils.getInstance().isTask_open()) {
				Intent startservice = new Intent(context, SdkService.class);
				context.stopService(startservice);
			}
		}
		if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) { // 解锁
			if (ActivityCacheUtils.getInstance().isTask_open()) {
			Intent startservice = new Intent(context, SdkService.class);
			context.startService(startservice);
			}
		}

	}

}
