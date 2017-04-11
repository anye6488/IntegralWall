package com.erm.integralwall;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.IResponseListener;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.Utils;
import com.erm.integralwall.core.download.ResponseProgressListenerImpl;
import com.erm.integralwall.core.receiver.AppReceiver;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NetManager.getInstance().inject(this);
		NetManager.getInstance().getchAdvertsDetailJsonByRequestParams("1223",
				new IResponseListener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						System.out.println("JSONObject: " + jsonObject);
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out.println("VolleyError: " + error);
					}

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}
				});

		NetManager
				.getInstance()
				.download(
						"http://img17.3lian.com/d/file/201701/17/e02c067fdeb22fcc9142c94436fcdae2.jpg",
						"ha.jpg", new ResponseProgressListenerImpl(this) {

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
						});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
