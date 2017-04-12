package com.erm.integralwall.core.download;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import com.erm.integralwall.core.Utils;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
				String path = null;
				boolean install = false;
				if(null != msg.obj && msg.obj instanceof DownloadBzip){
					DownloadBzip downloadBzip = (DownloadBzip) msg.obj;
					path = downloadBzip.path;
					install = downloadBzip.install;
				} else {
					path =  (String) msg.obj;
				}
				/***/
				onSuccess(path);
				
				/***/
				if(null != mReference && null != mReference.get() && install){
					Utils.installApp(mReference.get(), path);
				}
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
	
	public static class DownloadBzip{
		public String path;
		public boolean install;
		
		public DownloadBzip(String path, boolean install){
			this.path = path;
			this.install = install;
		}
	}
}
