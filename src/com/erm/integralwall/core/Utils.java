package com.erm.integralwall.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

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
	
	public static boolean isValidApk(String path){
		if(null == path)
			return false;
		/**.apk占4位长度*/
		if(path.length() < 4)
			return false;
		return path.endsWith(".apk");
	}
	
	public static boolean isApkExist(String path){
		
		if(TextUtils.isEmpty(path))
			return false;
		
		File file = new File(path);
		return file.exists();
	}
	
	public static String transitionObj2JsonString(Map<String, String> map){
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toString();
	}
	
	public static String getForegroundApp() {
		  final int AID_APP = 10000;
		  final int AID_USER = 100000;
		  File[] files = new File("/proc").listFiles();
		    int lowestOomScore = Integer.MAX_VALUE;
		    String foregroundProcess = null;
		    for (File file : files) {
		        if (!file.isDirectory()) {
		            continue;
		        }
		        int pid;

		        try {
		            pid = Integer.parseInt(file.getName());
		        } catch (NumberFormatException e) {
		            continue;
		        }

		        try {
		            String cgroup = read(String.format("/proc/%d/cgroup", pid));
		            String[] lines = cgroup.split("\n");
		            String cpuSubsystem;
		            String cpuaccctSubsystem;

		            if (lines.length == 2) {// 有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据
		                cpuSubsystem = lines[0];
		                cpuaccctSubsystem = lines[1];
		            } else if (lines.length == 3) {
		                cpuSubsystem = lines[0];
		                cpuaccctSubsystem = lines[2];
		            } else {
		                continue;
		            }

		            if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
		                // not an application process
		                continue;
		            }
		            if (cpuSubsystem.endsWith("bg_non_interactive")) {
		                // background policy
		                continue;
		            }

		            String cmdline = read(String.format("/proc/%d/cmdline", pid));
		            if (cmdline.contains("com.android.systemui")) {
		                continue;
		            }
		            int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2]
		                    .split("/")[1].replace("uid_", ""));
		            if (uid >= 1000 && uid <= 1038) {
		                // system process
		                continue;
		            }
		            int appId = uid - AID_APP;
		            int userId = 0;
		            // loop until we get the correct user id.
		            // 100000 is the offset for each user.

		            while (appId > AID_USER) {
		                appId -= AID_USER;
		                userId++;
		            }

		            if (appId < 0) {
		                continue;
		            }
		            // u{user_id}_a{app_id} is used on API 17+ for multiple user
		            // account support.
		            // String uidName = String.format("u%d_a%d", userId, appId);
		            File oomScoreAdj = new File(String.format(
		                    "/proc/%d/oom_score_adj", pid));
		            if (oomScoreAdj.canRead()) {
		                int oomAdj = Integer.parseInt(read(oomScoreAdj
		                        .getAbsolutePath()));
		                if (oomAdj != 0) {
		                    continue;
		                }
		            }
		            int oomscore = Integer.parseInt(read(String.format(
		                    "/proc/%d/oom_score", pid)));
		            if (oomscore < lowestOomScore) {
		                lowestOomScore = oomscore;
		                foregroundProcess = cmdline;
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return foregroundProcess;
	}
	private static String read(String path) throws IOException {
	    StringBuilder output = new StringBuilder();
	    BufferedReader reader = new BufferedReader(new FileReader(path));
	    output.append(reader.readLine());

	    for (String line = reader.readLine(); line != null; line = reader
	            .readLine()) {
	        output.append('\n').append(line);
	    }
	    reader.close();
	    return output.toString().trim();// 不调用trim()，包名后会带有乱码
	}
}
