package com.erm.integralwall.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.erm.integralwall.core.AbstractOperator;
import com.erm.integralwall.core.params.NetBzip;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileOperator extends AbstractOperator{
	
	public FileOperator(Context context){
		super(context);
	}
	
	private static final OkHttpClient client = new OkHttpClient.Builder()
            //设置超时，不设置可能会报异常
            .connectTimeout(1000, TimeUnit.MINUTES)
            .readTimeout(1000, TimeUnit.MINUTES)
            .writeTimeout(1000, TimeUnit.MINUTES)
            .build();
	
	
	public void download(String url, final String fileName, final IResponseProgressListener listener){
		Request request = new Request.Builder().url(url).build();
		Call newCall = client.newCall(request);
		//---缓存
		NetBzip netBzip = new NetBzip();
		netBzip.call = newCall;
		mapCache.put(url, netBzip);
		newCall.enqueue(new Callback() {

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
	
	@Override
	public void cancel(String url){
		if(null != mapCache && mapCache.containsKey(url)){
			NetBzip netBzip = mapCache.remove(url);
			netBzip.call.cancel();
		}
	}
	
	@Override
	public void cancelAll(){
		if(null != mapCache && mapCache.size() > 0){
			Set<String> keySet = mapCache.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String next = iterator.next();
				NetBzip netBzip =  mapCache.get(next);
				netBzip.call.cancel();
				iterator.remove();
			}
		}
	}
	
}
