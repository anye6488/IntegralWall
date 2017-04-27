package com.erm.integralwall.ui.detail;
/**
 * 广告完成任务需要的时间
 * @author zk
 *
 */
public class GetAdsTimeBean {
	//时间
	private String Time;
	//注册轨迹
	private String RegisterState;
	//任务内容
	private String TaskIntro;
	//包名
	private String PackName;
	//应用名称
	private String Titile;
	//广告id
	private String AdsId;
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
	public String getRegisterState() {
		return RegisterState;
	}
	public void setRegisterState(String registerState) {
		RegisterState = registerState;
	}
	public String getTaskIntro() {
		return TaskIntro;
	}
	public void setTaskIntro(String taskIntro) {
		TaskIntro = taskIntro;
	}
	public String getPackName() {
		return PackName;
	}
	public void setPackName(String packName) {
		PackName = packName;
	}
	public String getTitile() {
		return Titile;
	}
	public void setTitile(String titile) {
		Titile = titile;
	}
	public String getAdsId() {
		return AdsId;
	}
	public void setAdsId(String adsId) {
		AdsId = adsId;
	}

	
	

}
