package com.erm.integralwall.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONObject;

import com.erm.integralwall.core.download.FileOperator;
import com.erm.integralwall.core.download.IResponseProgressListener;
import com.erm.integralwall.core.params.FormParams;

import android.content.Context;

public class NetManager {
	
	private static final String TAG = NetManager.class.getSimpleName();
	
	private NetManager(){};
	
	private static NetManager mNetManager = null;
	
	private Reference<Context> mReference = null;

	private NetOperator mNetOperator;

	private FormParams mFormParams;

	private FileOperator mFileOperator;
	
	public static NetManager getInstance(){
		if(null == mNetManager){
			synchronized (NetManager.class) {
				if(null == mNetManager)
					mNetManager = new NetManager();
			}
		}
		return mNetManager;
	}
	
	/**
	 * 创建该对象之后，紧接着必须调用该方法.
	 * @param context
	 */
	public void inject(Context context){
		/***/
		mReference = new WeakReference<Context>(context);
		mFormParams = new FormParams(context.getApplicationContext());
		mNetOperator = new NetOperator(context);
		
		mFileOperator = new FileOperator(context);
	}
	
	
	/**
	 * 1.获取广告列表
	 * @param listener 请求网络回调
	 */
	public void fetchAdvertsJsonByRequestParams(IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getAdsListParamsMap();
			
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_LIST_URL, map, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * 2.获取广告详情
	 * @param listener 请求网络回调
	 */
	public void getchAdvertsDetailJsonByRequestParams(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getAdsListParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_LIST_URL, map, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 文件下载
	 * @param url
	 */
	public void download(String url, String fileName, IResponseProgressListener listener){
		
		if(null != mFileOperator)
			mFileOperator.download(url, fileName, listener);
	}
	
	public void cancel(String url){
		boolean flag = false;
		if(null != mNetOperator)
			flag = mNetOperator.cancel(url);
		
		if(null != mFileOperator && !flag)
			mFileOperator.cancel(url);
			
	}
	
	public void cancelAll(){
		if(null != mNetOperator)
			mNetOperator.cancelAll();
		
		if(null != mFileOperator)
			mFileOperator.cancelAll();
	}
}
