package com.erm.integralwall.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Utils {
	
	public static void installApp(Context context, String path){
		Intent intent = new Intent(Intent.ACTION_VIEW);
        // 设置目标应用安装包路径    
        intent.setDataAndType(Uri.fromFile(new File(path)),    
                "application/vnd.android.package-archive");  
        context.startActivity(intent);    
	}
	
	public static boolean isAppInstalled(Context context, String packageName) {  
        PackageManager packageManager = context.getPackageManager();  
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);  
        List<String> pName = new ArrayList<String>();  
        if (pinfo != null) {  
            for (int i = 0; i < pinfo.size(); i++) {  
                String pn = pinfo.get(i).packageName;  
                pName.add(pn);  
            }  
        }  
        return pName.contains(packageName);  
    }  
}
