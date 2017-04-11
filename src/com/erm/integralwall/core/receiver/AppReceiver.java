package com.erm.integralwall.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AppReceiver extends BroadcastReceiver{
	private static final String TAG = AppReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {  
            String packageName = intent.getData().getSchemeSpecificPart();  
            Log.d(TAG, "--------安装成功" + packageName);  
            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();  
              
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {  
            String packageName = intent.getData().getSchemeSpecificPart();  
            Log.d(TAG, "--------替换成功" + packageName);  
            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();  
              
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {  
            String packageName = intent.getData().getSchemeSpecificPart();  
            Log.d(TAG, "--------卸载成功" + packageName);  
            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();  
        }  
	}

}
