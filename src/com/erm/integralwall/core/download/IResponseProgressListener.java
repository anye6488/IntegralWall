package com.erm.integralwall.core.download;

public interface IResponseProgressListener {
	
	void onProgress(int percent);

	void onSuccess(String path);
	
	void onStart();
	
	void onFailure();
}
