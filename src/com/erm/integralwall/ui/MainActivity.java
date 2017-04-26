package com.erm.integralwall.ui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.R;
import com.erm.integralwall.R.id;
import com.erm.integralwall.R.layout;
import com.erm.integralwall.core.IApkInstalledListener;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.net.IResponseListener;
import com.erm.integralwall.core.service.ActivityCacheUtils;
import com.erm.integralwall.core.service.AdInfo;
import com.erm.integralwall.core.service.SdkService;
import com.erm.integralwall.ui.detail.DetailActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private TextView mAdverts = null;
	private ListView mAdvertListView;
	private AdvertsAdapter mAdvertsAdapter;
	// 是否退出
	private boolean isBind = false;
	private bineConnection bine;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetManager.getInstance().inject(this, null);
		// 启动监听
		registerScreenActionReceiver();
		
		mAdvertListView = (ListView) findViewById(R.id.ads_listview);
		mAdvertsAdapter = new AdvertsAdapter(this);
		mAdvertListView.setAdapter(mAdvertsAdapter);
		
		NetManager.getInstance().fetchAdvertsJsonByRequestParams(new IResponseListener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				try {
					org.json.JSONArray jsonArray = jsonObject.getJSONArray("AdsList");
					String arrayString = jsonArray.toString();
					java.lang.reflect.Type listType = new TypeToken<ArrayList<Advert>>(){}.getType();
					Gson gson = new Gson();
					List<Advert> list = gson.fromJson(arrayString, listType);
					mAdvertsAdapter.setUpdata(list);
					mAdvertsAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		
		
		mAdvertListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Advert item = mAdvertsAdapter.getItem(position);
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				intent.putExtra("ID", item.getAdsId());
				startActivity(intent);
			}
		});
		
		/*//--获取广告列表.
		mAdverts = (TextView) findViewById(R.id.ads_textview);
		
		findViewById(R.id.adsList).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchAdvertsJsonByRequestParams(new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						try {
							org.json.JSONArray jsonArray = jsonObject.getJSONArray("AdsList");
							String arrayString = jsonArray.toString();
							java.lang.reflect.Type listType = new TypeToken<ArrayList<Advert>>(){}.getType();
							Gson gson = new Gson();
							List<Advert> list = gson.fromJson(arrayString, listType);
							Message obtainMessage = mHandler.obtainMessage();
							obtainMessage.obj = list;
							mHandler.sendMessage(obtainMessage);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						try {
//							JSONObject jsonObj = jsonObject.getJSONObject("1982");
//							Advers advers = new Advers();
//							advers.AdsId = jsonObj.getString("AdsId");
//							advers.Title = jsonObj.getString("Title");
//							advers.Logo = jsonObj.getString("Logo");
//							advers.Size = jsonObj.getString("Size");
//							advers.Detail = jsonObj.getString("Detail");
//							advers.PackName = jsonObj.getString("PackName");
//							advers.Price = jsonObj.getString("Price");
//							advers.is_register = jsonObj.getString("is_register");
							
							mAdverts.setText(jsonObject.toString());
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
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
						mAdverts.setText(jsonObject.toString());
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
						mAdverts.setText(jsonObject.toString());
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
						mAdverts.setText(jsonObject.toString());
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
				NetManager.getInstance().fetchApkUrlByAdsID("1698", new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("fetchApkUrlByAdsID JSONObject: " + jsonObject);
						mAdverts.setText(jsonObject.toString());
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
				String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
				NetManager.getInstance().openOrDownload("http://gdown.baidu.com/data/wisegame/02ba8a69a5a792b1/QQ_500.apk",
						SDPath, "QQ_500.apk", new ResponseProgressListenerImpl(MainActivity.this) {
					
					@Override
					public void onSuccess(String path) {
						// TODO Auto-generated method stub
						Log.d("onSuccess", "path=" + path);
					}
					
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						Log.d("onStart", "======onStart=========");
					}
					
					@Override
					public void onProgress(int percent) {
						// TODO Auto-generated method stub
						Log.d("onResponse", "progress=" + percent);
						mAdverts.setText( "当前进度=" + percent +"%");
					}
					
					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						
					}
				}, true);
			}
		});
		
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().cancel("http://gdown.baidu.com/data/wisegame/02ba8a69a5a792b1/QQ_500.apk");
			}
		});*/
	}

	public void button(View view)
	{
		 startService("com.tencent.mobileqq", 1995, 10, "qq", "0", "任務內容");
	}
	
	
	/**
	 * 
	 * @param packagename 包名
	 * @param adId 广告id
	 * @param tasktime 任务时间
	 * @param appname app名字
	 * @param is_register 注册轨迹
	 * @param task 任务
	 */
	public void startService(String packagename,Integer adId,int tasktime,String appname,String is_register,String task)
	{
		//初始化监听数据
		AdInfo adinfo=new AdInfo();
		adinfo.setAdId(adId);
		adinfo.setAppName(appname);
		adinfo.setTaskTime(tasktime);
		adinfo.setPackageName(packagename);
		adinfo.setTaskInfo(task);
		adinfo.setOpenFlag(true);  //任务详情提示
		adinfo.setAlertFlag(true); //任务未完成提示
		if(!is_register.equals("0") && !is_register.trim().equals("")){
			adinfo.setRegister(true);
		}else{
			adinfo.setRegister(false);
		}
		//若为注册，则监听Activity活动路径
		if(adinfo.isRegister()){
			if(is_register != null && !is_register.trim().equals("")){
				String[] array = is_register.split(";");
				ArrayList<String> list = new ArrayList<String>();
				for(String str : array){				
					list.add(str);
				}
				adinfo.setActivitys(list);
			}
		}
		
		ActivityCacheUtils.getInstance().addAdInfo(packagename, adinfo);
		ActivityCacheUtils.getInstance().setLatestPackName(packagename);       //最近打开包名
		ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //最近打开广告ID
		Intent startservice = new Intent(this, SdkService.class);
		isBind = bindService(startservice, bine = new bineConnection(),
				BIND_AUTO_CREATE);
		PackageManager packageManager = getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packagename, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = packageManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packagename, className);
				intent.setComponent(cn);
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 监听开锁瓶，短信。
	 **/
	private void registerScreenActionReceiver() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) { // 锁屏
				if (isBind) {
					unbindService(bine);
					isBind = false;
				}
			}
			if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) { // 解锁
				Intent startservice = new Intent(MainActivity.this,
						SdkService.class);
				isBind = bindService(startservice, bine = new bineConnection(),
						BIND_AUTO_CREATE);
			}
			
		}
	};
	
	
	/**
	 * 
	 * @author lijun
	 *
	 */
	private class bineConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	}
	
	/**
	 * 关闭服务，停止监听
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isBind) {
			unbindService(bine);
			isBind = false;
		}
		NetManager.getInstance().cancelAll();
	}
	
}
