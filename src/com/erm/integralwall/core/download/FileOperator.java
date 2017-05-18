package com.erm.integralwall.core.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.erm.integralwall.core.Utils;
import com.erm.integralwall.core.download.ResponseProgressListenerImpl.DownloadBzip;
import com.erm.integralwall.core.net.AbstractOperator;
import com.erm.integralwall.core.params.NetBzip;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileOperator extends AbstractOperator{
	
	private static final String SUFFIX = ".tpk";
	
	public FileOperator(Context context){
		super(context);
	}
	
	private static final OkHttpClient client = new OkHttpClient.Builder()
            //设置超时，不设置可能会报异常
            .connectTimeout(1000, TimeUnit.MINUTES)
            .readTimeout(1000, TimeUnit.MINUTES)
            .writeTimeout(1000, TimeUnit.MINUTES)
            .build();
	
	
	public void openOrDownload(final String url, final String path,final String fileName, final IResponseProgressListener listener, final boolean install){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		/**check to download file is exist?*/
		if(TextUtils.isEmpty(path)){
			throw new IllegalArgumentException("path argus is null....");
		}
		String absolutePath = null;
		if(!path.endsWith("/")){
			absolutePath = path + "/" + fileName;
		} else{
			absolutePath = path + fileName;
		}
		
		if(Utils.isApkExist(absolutePath)){
			if(null != mReference && null != mReference.get()){
				Utils.installApp(mReference.get(), absolutePath);
			}
			return;
		}
		
		/***/
		if(null != mapCache && mapCache.containsKey(url)){
			return;
		}
		
		Request request = new Request.Builder().url(url).build();
		Call newCall = client.newCall(request);
		//---缓存
		NetBzip netBzip = new NetBzip();
		netBzip.call = newCall;
		netBzip.path = (path + fileName + SUFFIX);
		mapCache.put(url, netBzip);
		newCall.enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				// TODO Auto-generated method stub
				mapCache.remove(url);
				if(null != listener){//--下载失败.
					if(listener instanceof ResponseProgressListenerImpl){
	            		Message message = Message.obtain();
	            		message.obj = new IllegalAccessException("Have start downloading...");
	                	message.what = ResponseProgressListenerImpl.FAIL;
	                	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
	                	responseProgressListenerImpl.sendMessage(message);
	            	} else {
	            		listener.onFailure("in downloading...");
	            	}
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if(response.code() == 404){
					if(listener instanceof ResponseProgressListenerImpl){
                		Message message = Message.obtain();
                		message.obj = "file not found";
                    	message.what = ResponseProgressListenerImpl.FAIL;
                    	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
                    	responseProgressListenerImpl.sendMessage(message);
                	} else {
                		listener.onFailure("file not found");
                	}
					return;
				}
				InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                
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
                    /**mark file*/
                    File file = new File(path, fileName + SUFFIX);
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
                    	File waitingRenameFile = new File(path, fileName);
                        
                    	DownloadBzip bzip = new ResponseProgressListenerImpl.DownloadBzip(waitingRenameFile.getAbsolutePath(), install);
                        
                        /**remove mark,indicate download have finish*/
                    	file.renameTo(waitingRenameFile);
                    	mapCache.remove(url);
                    	if(listener instanceof ResponseProgressListenerImpl){
	                    	Message message = Message.obtain();
	                    	message.what = ResponseProgressListenerImpl.SUCCESS;
	                    	message.obj = bzip;
	                    	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
	                    	responseProgressListenerImpl.sendMessage(message);
	                	} else {
	                    	listener.onSuccess(file.getAbsolutePath());
	                	}
                    }
                } catch (Exception e) {
                	mapCache.remove(url);
                	if(null != listener){//---下载过程中失败.
                		if(listener instanceof ResponseProgressListenerImpl){
                    		Message message = Message.obtain();
                    		message.obj = e.getMessage();
                        	message.what = ResponseProgressListenerImpl.FAIL;
                        	ResponseProgressListenerImpl responseProgressListenerImpl = (ResponseProgressListenerImpl) listener;
                        	responseProgressListenerImpl.sendMessage(message);
                    	} else {
                    		listener.onFailure(e.getMessage());
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
	public boolean cancel(String url){
		if(null != mapCache && mapCache.containsKey(url)){
			NetBzip netBzip = mapCache.remove(url);
			netBzip.call.cancel();
			if(!TextUtils.isEmpty(netBzip.path)){
				File file = new File(netBzip.path);
				if(file.exists())
					file.delete();
			}
			return true;
		}
		
		return false;
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
				if(!TextUtils.isEmpty(netBzip.path)){
					File file = new File(netBzip.path);
					if(file.exists())
						file.delete();
				}
				iterator.remove();
			}
		}
	}
	
}
