package com.erm.integralwall.core.receiver;

import com.erm.integralwall.core.AppTaskMananger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class TaskBroadcastReceiver extends BroadcastReceiver{
	
	public static final String TASK_ID = "task_id";
	
	public static final String ENABLE_SERVICE_TO_CHECKED_TASK = "android.intent.action.check.task";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (null != intent) {
			String task = intent.getStringExtra(TASK_ID);
			if (!TextUtils.isEmpty(task))
				AppTaskMananger.startID(task, context);
		}
	}
}
