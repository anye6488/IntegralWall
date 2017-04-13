package com.erm.integralwall.core.receiver;

import com.erm.integralwall.core.NetManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class AppReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED) 
				|| TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			NetManager.getInstance().notifyServerWhenInstalled(packageName);
		}
	}

}
