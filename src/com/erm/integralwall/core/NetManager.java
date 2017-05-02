package com.erm.integralwall.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.erm.integralwall.core.download.FileOperator;
import com.erm.integralwall.core.download.IResponseProgressListener;
import com.erm.integralwall.core.encrypt.RSACodeHelper;
import com.erm.integralwall.core.net.IResponseListener;
import com.erm.integralwall.core.net.NetOperator;
import com.erm.integralwall.core.params.FormParams;
import com.erm.integralwall.ui.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class NetManager {
	
	private static final String TAG = NetManager.class.getSimpleName();
	
	private NetManager(){};
	
	private static NetManager mNetManager = null;
	
	private Reference<Context> mReference = null;

	private NetOperator mNetOperator;

	private FormParams mFormParams;

	private FileOperator mFileOperator;
	
	private Map<String , String> mPakage2AdsIdMap = new HashMap<String, String>();
	
	private IApkInstalledListener mApkInstalledListener;
	
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
	public void inject(Context context, IApkInstalledListener listener){
		/***/
		mApkInstalledListener = listener;
		mReference = new WeakReference<Context>(context);
		mFormParams = new FormParams(context.getApplicationContext());
		mNetOperator = new NetOperator(context);
		
		mFileOperator = new FileOperator(context);
	}
	
	/**
	 * 获取广告列表
	 * @param listener 请求网络回调
	 */
	public void fetchAdvertsJsonByRequestParams(IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getAdsListParamsMap("ArMn");
			
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_LIST_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 获取广告详情
	 * @param listener 请求网络回调
	 */
	public void fetchAdvertsDetailJsonByRequestParams(String adsID, IResponseListener<JSONObject> listener){
		Log.d(TAG, "download have finished, next to notify server.");
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_DETAIL_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 用户完成安装之后的调用的接口.
	 * @param listener 请求网络回调
	 */
	public void notifyServerWhenInstalled(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			
			String obj2JsonString = Utils.transitionObj2JsonString(map);
			
			/**enable encrypt*/
			try {
				obj2JsonString = RSACodeHelper.encode(RSACodeHelper.encryptByPublicKey(obj2JsonString.getBytes()));
				
//				String decrypt = new String(RSACodeHelper.decryptByPrivateKey(RSACodeHelper.decode(obj2JsonString)));
//				Log.d(TAG, "notifyServerWhenInstalled decrypt: " + decrypt);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mNetOperator.fetchJsonByRequestParams(Constant.WHEN_HAS_INSTALLED_URL, obj2JsonString, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 用户完成安装的时候回调的接口.
	 * @param pagekage 已经安装的APK包名
	 */
	public void notifyServerWhenInstalled(String pagekage){
		/***/
		if(null == mReference || null == mReference.get()){
			Log.d(TAG, "App can have exited or have lower momery...");
			return;
		}
		
		String advertID = mPakage2AdsIdMap.remove(pagekage);
		if(!TextUtils.isEmpty(advertID)){
			/**if use sdk inner cache*/
			notifyServerWhenInstalled(advertID, null);
			if(Constant.DEBUG){
				Intent intent = new Intent(MainActivity.ENABLE_SERVICE_TO_CHECKED_TASK);
				intent.putExtra(MainActivity.TASK_ID, advertID);
				mReference.get().sendBroadcast(intent);
			}
		} else {
			/**Skip if listener or callback is not null,else do nothing*/
			if(null == mApkInstalledListener || null == mApkInstalledListener.getMapOfPakageAndAdsID()){
				Toast.makeText(mReference.get(), "没有匹配的包名被找到..." + pagekage, Toast.LENGTH_LONG).show();
				return;
			}
			
			Map<String, String> map = mApkInstalledListener.getMapOfPakageAndAdsID();
			if(map.containsKey(pagekage)){
				String AdsId = map.get(pagekage);
				notifyServerWhenInstalled(AdsId, null);
				
				if(Constant.DEBUG){
					Intent intent = new Intent(MainActivity.ENABLE_SERVICE_TO_CHECKED_TASK);
					intent.putExtra(MainActivity.TASK_ID, AdsId);
					mReference.get().sendBroadcast(intent);
				}
			}
		}
	}
	
	/**
	 * 用户完成任务的时候回调的接口.
	 * @param listener 请求网络回调
	 */
	public void notifyServerWhenTaskFinished(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			map.put(Constant.IMEI, "99000828419910");
			String obj2JsonString = Utils.transitionObj2JsonString(map);
			
			/**enable encrypt*/
			try {
				obj2JsonString = RSACodeHelper.encode(RSACodeHelper.encryptByPublicKey(obj2JsonString.getBytes()));
				
//				String decrypt = new String(RSACodeHelper.decryptByPrivateKey(RSACodeHelper.decode(obj2JsonString)));
//				Log.d(TAG, "notifyServerWhenInstalled decrypt: " + decrypt);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mNetOperator.fetchJsonByRequestParams(Constant.WHEN_TASK_FINISHED_URL, obj2JsonString, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 根据广告ID获取下载的URL.
	 * @param listener 请求网络回调
	 */
	public void fetchApkUrlByAdsID(String adsID, String pakage, IResponseListener<JSONObject> listener){
		if(TextUtils.isEmpty(adsID) || TextUtils.isEmpty(pakage)){
			Log.d(TAG, "fetch apk url params is null...");
			return;
		}
		
		/**cache pakage and advert id.*/
		mPakage2AdsIdMap.put(pakage, adsID);
		
		if(null != mNetOperator){
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.ADVERTS_ID, adsID);
			
			mNetOperator.fetchJsonByRequestParams(Constant.FETCH_APK_DOWNLOAD_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 根据广告ID获取任务时长
	 * @param listener 请求网络回调
	 */
	public void fetchTaskTimeByAdsID(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			map.put(Constant.IMEI, "99000828419910");
			mNetOperator.fetchJsonByRequestParams(Constant.FETCH_TASK_TIME_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 文件下载，如果存在就直接安装,如果安装过则直接打开.
	 * @param url
	 */
	public void openOrDownload(String url, String path, String fileName, String packageName, IResponseProgressListener listener, boolean install){
		if(!TextUtils.isEmpty(packageName) && null !=  mReference && null != mReference.get()){
			PackageManager packageManager = mReference.get().getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(packageName);
			if(null != intent){
				mReference.get().startActivity(intent);
				return;
			}
		}
		
		if(TextUtils.isEmpty(path) || TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName)){
			throw new IllegalArgumentException("url or path, filename is not allow null...");
		}
		if(null != mFileOperator)
			mFileOperator.openOrDownload(url, path, fileName, listener, install);
	}
	
	public void cancel(String url){
		if(TextUtils.isEmpty(url))
			return;
		
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
