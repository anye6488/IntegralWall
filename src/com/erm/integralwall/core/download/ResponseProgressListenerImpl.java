package com.erm.integralwall.core.download;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class ResponseProgressListenerImpl extends Handler implements IResponseProgressListener{
	
	private static final String TAG = ResponseProgressListenerImpl.class.getSimpleName();
	
	protected static final int START = 10001;
	protected static final int PROGRESS = 10002;
	protected static final int SUCCESS = 10003;
	protected static final int FAIL = 10004;
	
	private Reference<Context> mReference = null;
	
	public ResponseProgressListenerImpl(Context context){
		mReference = new WeakReference<Context>(context);
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if(null != mReference && null != mReference.get()){
			switch (msg.what) {
			case START:
				onStart();
				break;
			case PROGRESS:
				int percent = (Integer) msg.obj;
				onProgress(percent);
				break;
			case SUCCESS:
				String path =  (String) msg.obj;
				onSuccess(path);
				break;
			case FAIL:
				onFailure();
				break;
	
			default:
				break;
			}
		}
	}
	
	@Override
	public void onStart() {}
	
	@Override
	public void onFailure() {}
	
	@Override
	public void onSuccess(String path) {}
}
