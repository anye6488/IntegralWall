package com.erm.integralwall;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.IApkInstalledListener;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.Utils;
import com.erm.integralwall.core.download.ResponseProgressListenerImpl;
import com.erm.integralwall.core.net.IResponseListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetManager.getInstance().inject(this, new IApkInstalledListener() {
			
			@Override
			public Map<String, String> getMapOfPakageAndAdsID() {
				// TODO Auto-generated method stub com.tencent.mobileqq
				Map<String, String> map= new HashMap<String, String>();
				map.put("com.tencent.mobileqq", "1995");
				return map;
			}
		});
		
		//--获取广告列表.
		findViewById(R.id.adsList).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchAdvertsJsonByRequestParams(new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("fetchAdvertsJsonByRequestParams JSONObject: " + jsonObject);
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("fetchAdvertsJsonByRequestParams VolleyError: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		//--获取广告详情.
		findViewById(R.id.adsDetail).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchAdvertsDetailJsonByRequestParams("1995", new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("fetchAdvertsDetailJsonByRequestParams JSONObject: " + jsonObject);
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("fetchAdvertsDetailJsonByRequestParams VolleyError: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		//--完成任务
		findViewById(R.id.taskFinshed).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().notifyServerWhenTaskFinished("1995", new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("notifyServerWhenTaskFinished JSONObject: " + jsonObject);
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("notifyServerWhenTaskFinished VolleyError: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		//--完成安装
		findViewById(R.id.hasInstalled).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().notifyServerWhenInstalled("1995", new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("notifyServerWhenInstalled JSONObject: " + jsonObject);
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("notifyServerWhenInstalled VolleyError: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		//--获取apk的下载路径.
		findViewById(R.id.downloadUrl).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchApkUrlByAdsID("1995", new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("fetchApkUrlByAdsID JSONObject: " + jsonObject);
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("fetchApkUrlByAdsID VolleyError: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		findViewById(R.id.download).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().download("http://gdown.baidu.com/data/wisegame/02ba8a69a5a792b1/QQ_500.apk", "QQ_500.apk", new ResponseProgressListenerImpl(MainActivity.this) {
					
					@Override
					public void onSuccess(String path) {
						// TODO Auto-generated method stub
						Log.d("onSuccess", "path=" + path);
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgress(int percent) {
						// TODO Auto-generated method stub
						Log.d("onResponse", "progress=" + percent);
					}
					
					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						
					}
				}, true);
			}
		});
		
		findViewById(R.id.install).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.installApp(MainActivity.this, "/storage/emulated/0/QQ_500.apk");
			}
		});
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void releaseHandlers(){
		   try {
		      Class<?> clazz = getClass();
		      Field[] fields = clazz.getDeclaredFields();
		      if (fields == null || fields.length <= 0 ){
		               return;
		       }
		      for (Field field: fields){
		          field.setAccessible(true);
		          if(!Handler.class.isAssignableFrom(field.getType())) continue;
	              Handler handler = (Handler)field.get(this);
	              if (handler != null && handler.getLooper() == Looper.getMainLooper()){
	                 handler.removeCallbacksAndMessages(null);
	              }
	              field.setAccessible(false);
		      }
		   } catch (IllegalAccessException e) {
		      e.printStackTrace();
		   }
		}
}
