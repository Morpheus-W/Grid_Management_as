package com.cn7782.management.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	private static final String SHARE_PRE_NAME = "share_pre_data";
	// 修改和保存
	public static void modify(String spTable, String key, String value,
			Context context) {
		SharedPreferences sp = context.getSharedPreferences(spTable,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// 查询
	public static String getValue(String spTable, String key, Context context) {
		SharedPreferences sp = context.getSharedPreferences(spTable,
				Context.MODE_PRIVATE);

		return sp.getString(key, null);
	}
	/***
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBooleanDataToSharePreference(Context context,
			String key, boolean value) {

		SharedPreferences preferences = getInstance(context);

		SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}
	static SharedPreferences preferences;
	/**
	 * 单例模式
	 * 
	 * @return
	 */
	private synchronized static SharedPreferences getInstance(Context context) {
		if (null == preferences) {
			preferences =  context.getSharedPreferences(
					SHARE_PRE_NAME, Context.MODE_PRIVATE);
		}
		return preferences;
	}
	public static boolean getBooleanDataByKey(Context context, String key,
			boolean defaultValue) {

		SharedPreferences preferences = getInstance(context);

		return preferences.getBoolean(key, defaultValue);

	}
}
