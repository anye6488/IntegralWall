package com.erm.integralwall.core.params;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.erm.integralwall.core.Constant;
import com.google.gson.JsonArray;

/**
 * 作者：liemng on 2017/3/31
 * 邮箱：859686819@qq.com
 */

import android.content.Context;
import android.text.TextUtils;

/**用于合成网络请求头信息**/
public class FormParams {

    private PhoneSysConfig mPhoneInfo;

    private FormConfig mFormConfig = null;
    
    public FormParams(Context application, FormConfig formConfig){
        mPhoneInfo = new PhoneSysConfig(application);
        mFormConfig = formConfig;
    }
    
    /**
     * 获取广告列表所需的部分参数
     * @return
     */
    public String getAdsListParamsMap(){
    	
    	String retVal = "{}";
    	
		//--- 'true' get all install package, but not system app.
    	JSONArray jsonArray =  mPhoneInfo.getAllAppsPackage(true);
    	
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(Constant.PACKAGE, jsonArray);
			jsonObj.put(Constant.ADP_CODE, mFormConfig.getAppCode());
			jsonObj.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
			jsonObj.put(Constant.IP, mPhoneInfo.getIPAddress());
			jsonObj.put(Constant.SDK_VERSION, Constant.SDK_VERSION_CODE);
			jsonObj.put(Constant.IMSI, mPhoneInfo.getPhoneIMSI());
			jsonObj.put(Constant.ANDROID_ID, mPhoneInfo.getPhoneID());
			jsonObj.put(Constant.SYSTEM_VERSION, mPhoneInfo.getPhoneVersion());
			jsonObj.put(Constant.MODEL, mPhoneInfo.getPhoneModels());
			jsonObj.put(Constant.MAC, mPhoneInfo.getPhoneMAC());
			jsonObj.put(Constant.OPERATOR, mPhoneInfo.getOperators());
			jsonObj.put(Constant.NETTYPE, mPhoneInfo.getNetWorkType());
			jsonObj.put(Constant.BRAND, mPhoneInfo.getPhoneBrand());
			jsonObj.put(Constant.RESOLUTION, mPhoneInfo.getResolution());
			jsonObj.put(Constant.OTHER, mFormConfig.getUserId());
			retVal = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return retVal;
    }
    
    /**
     * 获取基础手机参数.
     * @return
     */
    public Map<String, String> getBaseParamsMap(){
    	HashMap<String,String> map = new HashMap<String, String>();
		map.put(Constant.ADP_CODE, Constant.APP_CODE);
		map.put(Constant.IMEI,mPhoneInfo.getPhoneIMEI());
		return map;
    }
    
//    /**
//     * 获取广告详情所需的部分参数
//     * @return
//     */
//    public Map<String, String> getAdsDetailParamsMap(){
//		HashMap<String,String> map = new HashMap<String, String>();
//		map.put(Constant.ADP_CODE, Constant.APP_CODE);
//		map.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
//		
//		//---get all install package, but not system app.
//		map.put(Constant.PACKAGE, mPhoneInfo.getAllAppsPackage(false));
//		
//        return map;
//    }

    public JSONObject createJsonObj(String[] _param, String[] _values) {
        JSONObject jsonObj = new JSONObject();
        try {
            for (int i = 0; i < _param.length; i++) {
                jsonObj.put(_param[i], _values[i]);
            }
        } catch (Exception e) {
            return null;
        }
        return jsonObj;
    }

    /**
     * 生成json
     * @param _param
     * @param _values
     * @return 返回String对象
     */
    public String createJsonObj2String(String[] _param, Object[] _values) {
        JSONObject jsonObj = new JSONObject();
        try {
            for (int i = 0; i < _param.length; i++) {
                jsonObj.put(_param[i], _values[i]);
            }
        } catch (Exception e) {
            return null;
        }
        return jsonObj.toString();
    }
    
    public static class FormConfig{
    	private String mAppcode = "";
    	private String mUserId = "";
    	
    	/**
    	 * 设置appcode，需要申请
    	 * @param appCode
    	 * @return
    	 */
    	public FormConfig setAppCode(String appCode){
    		this.mAppcode = appCode;
    		return this;
    	}
    	
    	/**
    	 * 用户自定义参数
    	 * @param otherParam
    	 * @return
    	 */
    	public FormConfig setUserId(String userId){
    		this.mUserId = userId;
    		return this;
    	}
    	
    	public String getAppCode(){
    		return mAppcode;
    	}
    	
    	public String getUserId(){
    		return mUserId;
    	}
    }
}
