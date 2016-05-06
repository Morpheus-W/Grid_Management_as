package com.cn7782.management.config;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.cn7782.management.R;
public class ConfigUtil {
	private static final String TAG = "Config";
	private static final String Package = "com.cn7782.management";

    
    public static int getVerCode(Context context) {
            int verCode = -1;
            try {
                    verCode = context.getPackageManager().getPackageInfo(
                            Package, 0).versionCode;
            } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
            }
            return verCode;
    }
    
    public static String getVerName(Context context) {
            String verName = "";
            try {
                    verName = context.getPackageManager().getPackageInfo(
                            Package, 0).versionName;
            } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
            }
            return verName; 

    }
    
    public static String getAppName(Context context) {
            String verName = context.getResources()
            .getText(R.string.app_name).toString();
            return verName;
    }
}
