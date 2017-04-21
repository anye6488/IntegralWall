package com.erm.integralwall.ui.detail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.R;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.download.ResponseProgressListenerImpl;
import com.erm.integralwall.core.net.IResponseListener;
import com.erm.integralwall.ui.detail.DetailBzip.Task;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity{
	
	private TaskAdapter mTaskAdapter;
	private ListView mTaskListView;
	private TextView mDetailTextVew;
	private Button mDownload;
	private int mAdvertID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);
		
		mTaskListView = (ListView) findViewById(R.id.task_listview);
		mTaskAdapter = new TaskAdapter(this);
		mTaskListView.setAdapter(mTaskAdapter);
		
		mDetailTextVew = (TextView) findViewById(R.id.detail);
		Intent intent = getIntent();
		if(null != intent){
			mAdvertID = intent.getIntExtra("ID", 1995);
			NetManager.getInstance().fetchAdvertsDetailJsonByRequestParams(String.valueOf(mAdvertID), new IResponseListener<JSONObject>() {
				
				@Override
				public void onResponse(JSONObject jsonObject) {
					// TODO Auto-generated method stub
					Gson gson = new Gson();
//					Map<String, DetailBzip> detailMap = gson.fromJson(jsonObject.toString(), new TypeToken<Map<String, DetailBzip>>() {}.getType());  
					DetailBzip detailBzip = gson.fromJson(jsonObject.toString(), DetailBzip.class);
					mTaskAdapter.setUpData(detailBzip.getTask());
					mDetailTextVew.setText(detailBzip.toString());
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
		} else {
			Toast.makeText(this, "无效的参数...", 1).show();;
		}
		
		mDownload = (Button) findViewById(R.id.download);
		mDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchApkUrlByAdsID(String.valueOf(mAdvertID), new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						String url;
						try {
							url = jsonObject.getString("Url");
							Log.d("ArMn", "download info:" + url);
							download(mAdvertID + ".apk");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
	}
	
	private void download(String name){
		String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		NetManager.getInstance().openOrDownload("http://gdown.baidu.com/data/wisegame/02ba8a69a5a792b1/QQ_500.apk",
				SDPath, name, new ResponseProgressListenerImpl(DetailActivity.this) {
			
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
				mDownload.setText("当前进度=" + percent +"%");
			}
			
			@Override
			public void onFailure() {
				// TODO Auto-generated method stub
				
			}
		}, true);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetManager.getInstance().cancel("http://gdown.baidu.com/data/wisegame/02ba8a69a5a792b1/QQ_500.apk");
	}

	static class TaskAdapter extends BaseAdapter{

		private Context mContext;
		
		public List<DetailBzip.Task> mTaskList = new ArrayList<Task>();
		public TaskAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}
		
		public void setUpData(Map<String, DetailBzip.Task> taskMap){
			mTaskList.clear();
			
			Set<String> keySet = taskMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				Task task = taskMap.get(key);
				mTaskList.add(task);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTaskList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTaskList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			ViewHolder mViewHolder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adverts_item, null, false);
				mViewHolder = new ViewHolder(convertView);
				convertView.setTag(mViewHolder);;
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			
			mViewHolder.mContent.setText(mTaskList.get(position).toString());
			return convertView;
		}
		
		static class ViewHolder {
			public TextView mContent;
			
			public  ViewHolder(View view){
				mContent = (TextView) view.findViewById(R.id.content);
			}
		}
	}
}
