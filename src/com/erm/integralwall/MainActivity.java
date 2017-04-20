package com.erm.integralwall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.AdvertsAdapter;
import com.erm.integralwall.core.IApkInstalledListener;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.net.IResponseListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView mAdverts = null;
	private ListView mAdvertListView;
	private AdvertsAdapter mAdvertsAdapter;
	
	
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

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetManager.getInstance().cancelAll();
	}
	
}
