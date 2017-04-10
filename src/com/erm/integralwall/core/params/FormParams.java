package com.erm.integralwall.core.params;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.erm.integralwall.core.Constant;
import com.erm.integralwall.core.encrypt.Compression;
import com.erm.integralwall.core.encrypt.RSACodeHelper;

/**
 * 作者：liemng on 2017/3/31
 * 邮箱：859686819@qq.com
 */

import android.content.Context;

/**用于合成网络请求头信息**/
public class FormParams {

    private PhoneSysConfig mPhoneInfo;

    public FormParams(Context application){
        mPhoneInfo = new PhoneSysConfig(application);
    }
    
    /**
     * 获取广告列表所需的部分参数
     * @return
     */
    public Map<String, String> getAdsListParamsMap(){
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(Constant.ADP_CODE, Constant.APP_CODE);
		map.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
		map.put(Constant.IP, mPhoneInfo.getIPAddress());
		map.put(Constant.SDK_VERSION, Constant.SDK_VERSION_CODE);
		
		//---get all install package, but not system app.
		map.put(Constant.PACKAGE, mPhoneInfo.getAllAppsPackage(false));
		
        return map;
    }
    
    /**
     * 获取广告详情所需的部分参数
     * @return
     */
    public Map<String, String> getAdsDetailParamsMap(){
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(Constant.ADP_CODE, Constant.APP_CODE);
		map.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
		
		//---get all install package, but not system app.
		map.put(Constant.PACKAGE, mPhoneInfo.getAllAppsPackage(false));
		
        return map;
    }

    /**
     * 获取手机的配置信息，用于标识一部手机.
     *
     * @return
     */
    public String getFormParams() {
        String result;
        try {
            String[] _param = { "IMEI", "IMSI", "AndroidId", "Phone", "Other",
                    "Version", "Model", "NetType", "SysVer", "Operator",
                    "AppCode", "Mac","Brand","Resolution" };
            String[] _values = { mPhoneInfo.getPhoneIMEI(), mPhoneInfo.getPhoneIMSI(),
                    mPhoneInfo.getPhoneID(), "", Constant.USER_ID,
                    Constant.VERSION, mPhoneInfo.getPhoneModels(),
                    mPhoneInfo.getNetWorkType(), mPhoneInfo.getPhoneVersion(),
                    mPhoneInfo.getOperators(), Constant.ADP_CODE,
                    mPhoneInfo.getPhoneMAC(),mPhoneInfo.getPhoneBrand(),mPhoneInfo.getResolution()};
            JSONObject param = createJsonObj(_param, _values);
            String[] _PARAM = { "FUNC", "PARAM" };
            Object[] _VALUES = { "101001", param };
            result = createJsonObj2String(_PARAM, _VALUES);
        } catch (Exception e) {
            result = "";
        }
        return RSACodeHelper.base64Enc(Compression
                .getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));

    }

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
}
