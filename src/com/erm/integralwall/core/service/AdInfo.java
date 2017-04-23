package com.erm.integralwall.core.service;

import java.util.List;

public class AdInfo {
	
	private Integer adId;
	
	private String packageName;
	
	private int taskTime = 0;
	
	private String taskInfo;
	
	private String appName;
	
	private int  exeTime;
	
	private boolean isRegister;

	private List<String> activitys;//注册流程Activity
	
	private boolean openFlag;
	
	private boolean alertFlag;


	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public int getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(int taskTime) {
		this.taskTime = taskTime;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}

	public int getExeTime() {
		return exeTime;
	}

	public void setExeTime(int exeTime) {
		this.exeTime = exeTime;
	}

	public boolean isRegister() {
		return isRegister;
	}

	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}

	public List<String> getActivitys() {
		return activitys;
	}

	public void setActivitys(List<String> activitys) {
		this.activitys = activitys;
	}

	public boolean isOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(boolean openFlag) {
		this.openFlag = openFlag;
	}

	public boolean isAlertFlag() {
		return alertFlag;
	}

	public void setAlertFlag(boolean alertFlag) {
		this.alertFlag = alertFlag;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
		
}
