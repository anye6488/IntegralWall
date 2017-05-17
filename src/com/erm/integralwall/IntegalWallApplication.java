package com.erm.integralwall;

import com.erm.integralwall.core.NetManager;

import android.app.Application;

public class IntegalWallApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
//		NetManager.getInstance().inject(this, null);
	}
	
}
