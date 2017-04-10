package com.erm.integralwall.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileOperator {
	
	private Reference<Context> mReference = null;
	
	public FileOperator(Context context){
		mReference = new WeakReference<Context>(context);
	}
	
	private static final OkHttpClient client = new OkHttpClient.Builder()
            //设置超时，不设置可能会报异常
            .connectTimeout(1000, TimeUnit.MINUTES)
            .readTimeout(1000, TimeUnit.MINUTES)
            .writeTimeout(1000, TimeUnit.MINUTES)
            .build();
	
	
	public void download(String url, final String fileName, final IResponseProgressListener listener){
		Request request = new Request.Builder().url(url).build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				// TODO Auto-generated method stub
				if(null != listener){//--下载失败.
					if(listener instanceof ResponseProgressListenerImpl){
	            		Message message = Message.obtain();
	                	message.what = ResponseProgressListenerImpl.FAIL;
	                	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
	                	responseProgressListenerImpl.sendMessage(message);
	            	} else {
	            		listener.onFailure();
	            	}
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                	//---开始下载.
                	if(null != listener){
                		if(listener instanceof ResponseProgressListenerImpl){
	                		Message message = Message.obtain();
	                    	message.what = ResponseProgressListenerImpl.START;
	                    	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
	                    	responseProgressListenerImpl.sendMessage(message);
	                	} else {
	                    	listener.onStart();
	                    }
	                }
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(SDPath, fileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if(null != listener){//---下载中.
                        	if(listener instanceof ResponseProgressListenerImpl){
                        		Message message = Message.obtain();
                            	message.what = ResponseProgressListenerImpl.PROGRESS;
                            	message.obj = progress;
                            	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
                            	responseProgressListenerImpl.sendMessage(message);
                        	} else {
                        		listener.onProgress(progress);
                        	}
                        	
                        }
                    }
                    fos.flush();
                    if(null != listener){//---下载成功.
                    	if(listener instanceof ResponseProgressListenerImpl){
	                    	Message message = Message.obtain();
	                    	message.what = ResponseProgressListenerImpl.SUCCESS;
	                    	message.obj = file.getAbsolutePath();
	                    	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
	                    	responseProgressListenerImpl.sendMessage(message);
	                	} else {
	                    	listener.onSuccess(file.getAbsolutePath());
	                	}
                    }
                } catch (Exception e) {
                	if(null != listener){//---下载过程中失败.
                		if(listener instanceof ResponseProgressListenerImpl){
                    		Message message = Message.obtain();
                        	message.what = ResponseProgressListenerImpl.FAIL;
                        	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
                        	responseProgressListenerImpl.sendMessage(message);
                    	} else {
                    		listener.onFailure();
                    	}
                    }
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
			
			}
		});
        	
	}
}
